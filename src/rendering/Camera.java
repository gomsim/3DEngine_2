package rendering;

import components.Artifact;
import components.Polygon;
import components.Vertex;
import engine.Engine;

import static rendering.Renderer.*;
import static util.VectorUtil.*;

class Camera {

    //TODO: VIEW_ANDLE and LENS_DISTANCE should be moved to config file
    private static final int VIEW_ANGLE = 90;
    private static final double LENS_DISTANCE = 500;

    private static final int LENS_WIDTH = calculateLensWidth(VIEW_ANGLE, LENS_DISTANCE);
    private static final int LENS_HEIGHT = calculateLensHeight(LENS_WIDTH);

    private static final double[] CAMERA_OFFSET = new double[] {(double)SCREEN_WIDTH/2, (double)SCREEN_HEIGHT/2, 0};

    private Rasteriser rasteriser = new Rasteriser(LENS_WIDTH, LENS_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);

    private static int calculateLensWidth(int viewAngle, double lensDistance){
        return (int)lensDistance * 2 * (viewAngle / 90);
    }
    private static int calculateLensHeight(int lensWidth){
        return (int)((9f/16) * lensWidth);
    }

    Raster capture(){
        rasteriser.cleanRaster();
        for (Artifact artifact: Engine.instance().getArtifacts()){
            //TODO: Använd trådpoolen i ThreadPool för vardera artefakt. För detta måste dock Rasteriser var trådsäkert.
            if (!behindCamera(artifact)){
                for (Polygon polygon: artifact.getPolygons()){
                    //if (/*!behindCamera(polygon) && facingCamera(polygon)*/){//TODO: Kommentera av när det är enklare att se saker i rendereingen
                        Projection projection = projectPolygon(artifact, polygon);
                    /*System.out.println("-----------------RENBDERING-------------- ");
                    System.out.println("Polygon: " + polygon);
                    System.out.println("Projection: " + projection.polygon);
                    System.out.println("----------------------------------------- ");*/
                    rasteriser.rasterise(projection, LENS_DISTANCE);
                    //}
                }
            }
        }
        return rasteriser.getRaster();
    }
    private Projection projectPolygon(Artifact artifact, Polygon polygon){
        polygon = new Polygon(
                projectVertex(artifact, polygon.getVertices()[A]),
                projectVertex(artifact, polygon.getVertices()[B]),
                projectVertex(artifact, polygon.getVertices()[C])
        );
        polygon.translate(CAMERA_OFFSET);
        return new Projection(polygon, artifact.getColor());
    }
    private Vertex projectVertex(Artifact artifact, Vertex vertex){
        double[] coordinates = artifact.localPointToGlobal(vertex.coordinates);
        double[] interpolation = interpolate(coordinates, ORIGIN, LENS_DISTANCE, Z);
        return new Vertex(
                interpolation[X],
                interpolation[Y],
                coordinates[Z]
        );
    }
    private boolean behindCamera(Polygon polygon){ //TODO: This is ALMOST always returning true...
        Vertex[] vertices = polygon.getVertices();
        return
                vertices[A].coordinates[Z] < LENS_DISTANCE &&
                vertices[B].coordinates[Z] < LENS_DISTANCE &&
                vertices[C].coordinates[Z] < LENS_DISTANCE;
    }
    public boolean behindCamera(Artifact artifact){
        return artifact.getZ() + artifact.getDepth() < LENS_DISTANCE;
    }

    private boolean facingCamera(Polygon polygon){ //TODO: Why in the fudge of hell does this function work in reverse Z-wise??
        return polygon.getNormal()[Z] < 0;//TODO: This SHOULD wourk when the Z-value is closer negative (ie. towards the camera) but works the other way around somehow.

        //TODO: Svaret på frågan kan vara att det handlar ju för fan inte bara om Z-axeln. Om en polygon är riktad mot dig skiljer sig beroende på vinkeln från dig till den. Det är som en sfär runt dig, inte bara vilken sida av Z den är.
    }
}
