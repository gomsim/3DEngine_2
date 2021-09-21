package rendering;

import components.Artifact;
import components.Polygon;
import components.Vertex;
import engine.Engine;

import java.awt.*;

import static rendering.Renderer.SCREEN_HEIGHT;
import static rendering.Renderer.SCREEN_WIDTH;
import static util.VectorUtil.*;

class Camera {

    //TODO: VIEW_ANDLE and LENS_DISTANCE should be moved to config file
    private static final int VIEW_ANGLE = 90;
    public static final double LENS_DISTANCE = 500;
    private static final double VIEW_DISTANCE = 10000;

    private static final int LENS_WIDTH = calculateLensWidth(VIEW_ANGLE, LENS_DISTANCE);
    private static final int LENS_HEIGHT = calculateLensHeight(LENS_WIDTH);

    static final double[] CAMERA_OFFSET = new double[] {(double)SCREEN_WIDTH/2, (double)SCREEN_HEIGHT/2, 0};

    private Rasteriser rasteriser = new Rasteriser(LENS_WIDTH, LENS_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);

    private static int calculateLensWidth(int viewAngle, double lensDistance){
        return (int)lensDistance * 2 * (viewAngle / 90);
    }
    private static int calculateLensHeight(int lensWidth){
        return (int)((9f/16) * lensWidth);
    }

    Raster capture(){
        int i = 0;
        rasteriser.cleanRaster();
        for (Artifact artifact: Engine.instance().getArtifacts()){
            //TODO: Använd trådpoolen i ThreadPool för vardera artefakt. För detta måste dock Rasteriser var trådsäkert.
            if (!behindCamera(artifact)){
                for (Polygon polygon: artifact.getPolygons()){
                    Polygon wPolygon = toWorldSpace(polygon, artifact);
                    if (!behindCamera(wPolygon) && facingCamera(wPolygon)){ //TODO: Check also so that polygon doesn't cut through ORIGIN. If it does so there is no point in rendering it, which also gets rid of "straight" artifacts.
                        ClippingData clippingData = new ClippingData(wPolygon, LENS_DISTANCE);
                        Projection projection;
                        if (clippingData.needsClipping()){ //TODO: Clean this mofo shit up! Put in a method or som'n'.
                            for (Polygon clippedPolygon: clippingData.clipPolygons()){
                                projection = projectPolygon(artifact, clippedPolygon);
                                rasteriser.rasterise(clippedPolygon, projection, LENS_DISTANCE, VIEW_DISTANCE);
                            }
                        }else{
                            projection = projectPolygon(artifact, wPolygon);
                            rasteriser.rasterise(wPolygon, projection, LENS_DISTANCE, VIEW_DISTANCE);
                        }
                    }
                }
            }
        }
        addCrossHair(rasteriser.getRaster());
        return rasteriser.getRaster();
    }

    private void addCrossHair(Raster raster){
        raster.setColor(SCREEN_WIDTH/2, SCREEN_HEIGHT/2, Color.WHITE.getRGB());
        raster.setColor((SCREEN_WIDTH/2)+1, (SCREEN_HEIGHT/2), Color.WHITE.getRGB());
        raster.setColor((SCREEN_WIDTH/2), (SCREEN_HEIGHT/2)+1, Color.WHITE.getRGB());
        raster.setColor((SCREEN_WIDTH/2)+1, (SCREEN_HEIGHT/2)+1, Color.WHITE.getRGB());
    }
    private Projection projectPolygon(Artifact artifact, Polygon polygon){
        polygon = new Polygon(
                projectVertex(polygon.getVertices()[A]),
                projectVertex(polygon.getVertices()[B]),
                projectVertex(polygon.getVertices()[C])
        );
        polygon.translate(CAMERA_OFFSET);
        return new Projection(polygon, artifact.getColor());
    }
    private Vertex projectVertex(Vertex vertex){
        double[] coordinates = vertex.coordinates;
        double[] interpolation = interpolate(coordinates, ORIGIN, LENS_DISTANCE, Z);
        return new Vertex(
                interpolation[X],
                interpolation[Y],
                0 //Only X and Y are used to determine projection's location on raster
        );
    }
    private Polygon toWorldSpace(Polygon polygon, Artifact artifact){ //TODO: Clean this mofo shit up!
        return new Polygon(
                new Vertex(artifact.localPointToGlobal(polygon.getVertices()[A].coordinates)),
                new Vertex(artifact.localPointToGlobal(polygon.getVertices()[B].coordinates)),
                new Vertex(artifact.localPointToGlobal(polygon.getVertices()[C].coordinates))
        );
    }
    private boolean behindCamera(Artifact artifact){
        return artifact.getZ() + artifact.getDepth() < LENS_DISTANCE;
    }
    private boolean behindCamera(Polygon polygon){
        Vertex[] vertices = polygon.getVertices();
        return
                vertices[A].coordinates[Z] < LENS_DISTANCE &&
                vertices[B].coordinates[Z] < LENS_DISTANCE &&
                vertices[C].coordinates[Z] < LENS_DISTANCE;
    }

    private boolean facingCamera(Polygon polygon){
        Vertex[] vertices = polygon.getVertices();
        double avgX = (vertices[A].coordinates[X] + vertices[B].coordinates[X] + vertices[C].coordinates[X]) / 3;
        double avgY = (vertices[A].coordinates[Y] + vertices[B].coordinates[Y] + vertices[C].coordinates[Y]) / 3;
        double avgZ = (vertices[A].coordinates[Z] + vertices[B].coordinates[Z] + vertices[C].coordinates[Z]) / 3;

        return dotProduct(subtract(ORIGIN, new double[] {avgX, avgY, avgZ}), polygon.getNormal()) > 0;
    }
}
