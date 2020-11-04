package rendering;

import components.Artifact;
import components.Polygon;
import components.Vertex;
import engine.Engine;

import java.util.ArrayList;

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
        int i = 0;
        rasteriser.cleanRaster();
        for (Artifact artifact: Engine.instance().getArtifacts()){
            //TODO: Använd trådpoolen i ThreadPool för vardera artefakt. För detta måste dock Rasteriser var trådsäkert.
            if (!behindCamera(artifact)){
                for (Polygon polygon: artifact.getPolygons()){
                    Polygon wPolygon = toWorldSpace(polygon, artifact);
                    ClippingData clippingData = getClippingData(wPolygon);
                    Projection projection;
                    if (clippingData.inFront.size() == 1){ //TODO: Clean this mofo shit up! Put in a method or som'n'.
                        projection =  projectPolygon(artifact, clipTwoVertices(clippingData));
                        rasteriser.rasterise(projection, LENS_DISTANCE);
                    }else if (clippingData.inFront.size() == 2){
                        for (Polygon clipPoly: clipOneVertex(clippingData)){
                            projection =  projectPolygon(artifact, clipPoly);
                            rasteriser.rasterise(projection, LENS_DISTANCE);
                        }
                    }else{
                        projection = projectPolygon(artifact, wPolygon);
                        rasteriser.rasterise(projection, LENS_DISTANCE);
                    }
                    /*if (i++ == 0){
                        System.out.println("-----------------RENBDERING-------------- ");
                        System.out.println("Polygon: " + polygon);
                        System.out.println("Projection: " + projection.polygon);
                        System.out.println("----------------------------------------- ");
                    }*/
                }
            }
        }
        return rasteriser.getRaster();
    }
    Polygon clipTwoVertices(ClippingData clippingData){ //TODO: Clean this mofo shit up!
        Vertex a = clippingData.inFront.get(0);
        double[] b = interpolate(a.coordinates, clippingData.behind.get(0).coordinates, LENS_DISTANCE, Z);
        double[] c = interpolate(a.coordinates, clippingData.behind.get(1).coordinates, LENS_DISTANCE, Z);
        return new Polygon(
                a,
                new Vertex(b[X], b[Y], b[Z]),
                new Vertex(c[X], c[Y], c[Z])
                );
    }
    Polygon[] clipOneVertex(ClippingData clippingData){ //TODO: Clean this mofo shit up!
        Vertex a = clippingData.inFront.get(0);
        Vertex b = clippingData.inFront.get(1);
        double[] c = interpolate(a.coordinates, clippingData.behind.get(0).coordinates, LENS_DISTANCE, Z);
        double[] d = interpolate(b.coordinates, clippingData.behind.get(0).coordinates, LENS_DISTANCE, Z);
        return new Polygon[] {
                new Polygon(
                        a,
                        b,
                        new Vertex(c)
                ),
                new Polygon(
                        b,
                        new Vertex(c),
                        new Vertex(d)
                )
        };
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
                coordinates[Z]
        );
    }
    private ClippingData getClippingData(Polygon polygon){  //TODO: Clean this mofo shit up!
        ClippingData clippingData = new ClippingData();

        for (Vertex vertex: polygon.getVertices()){
            if (vertex.coordinates[Z] < LENS_DISTANCE)
                clippingData.behind.add(vertex);
            else
                clippingData.inFront.add(vertex);
        }
        return clippingData;
    }
    private Polygon toWorldSpace(Polygon polygon, Artifact artifact){ //TODO: Clean this mofo shit up!
        return new Polygon(
                new Vertex(artifact.localPointToGlobal(polygon.getVertices()[A].coordinates)),
                new Vertex(artifact.localPointToGlobal(polygon.getVertices()[B].coordinates)),
                new Vertex(artifact.localPointToGlobal(polygon.getVertices()[C].coordinates))
        );
    }
    public boolean behindCamera(Artifact artifact){
        return artifact.getZ() + artifact.getDepth() < LENS_DISTANCE;
    }

    private boolean facingCamera(Polygon polygon){ //TODO: Why in the fudge of hell does this function work in reverse Z-wise??
        return polygon.getNormal()[Z] < 0;//TODO: This SHOULD wourk when the Z-value is closer negative (ie. towards the camera) but works the other way around somehow.

        //TODO: Svaret på frågan kan vara att det handlar ju för fan inte bara om Z-axeln. Om en polygon är riktad mot dig skiljer sig beroende på vinkeln från dig till den. Det är som en sfär runt dig, inte bara vilken sida av Z den är.
    }
    private static class ClippingData{ //TODO: Clean this mofo shit up!
        ArrayList<Vertex> inFront = new ArrayList<>();
        ArrayList<Vertex> behind = new ArrayList<>();
    }
}
