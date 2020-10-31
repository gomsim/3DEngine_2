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
    private static final int LENS_HEIGHT = calculateLensHeight(VIEW_ANGLE, LENS_DISTANCE);

    private static final double[] CAMERA_POS = new double[] {(double)SCREEN_WIDTH/2, (double)SCREEN_HEIGHT/2, 0};

    private Rasteriser rasteriser = new Rasteriser(LENS_WIDTH, LENS_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);

    private static int calculateLensWidth(int viewAngle, double lensDistance){
        return (int)lensDistance * 2 * (viewAngle / 90);
    }
    private static int calculateLensHeight(int viewAngle, double lensDistance){
        return (int)lensDistance * 2 * (viewAngle / 90);
    }

    Raster capture(){
        rasteriser.cleanRaster();
        for (Artifact artifact: Engine.instance().getArtifacts()){
            //TODO: Använd trådpoolen i ThreadPool för vardera artefakt. För detta måste dock Rasteriser var trådsäkert.
            //System.out.println("Artefact behind camera? \n" + behindCamera(artifact));
            if (!behindCamera(artifact)){
                for (Polygon polygon: artifact.getPolygons()){
                    //System.out.println("Polygon to project: \n" + polygon);
                    System.out.println("Polygon behind camera? \n" + behindCamera(polygon));
                    System.out.println("FACING CANMEERAAA? \n" + facingCamera(polygon));
                    if (!behindCamera(polygon) && facingCamera(polygon)){//TODO: Kommentera av när det är enklare att se saker i rendereingen

                        Projection projection = projectPolygon(artifact, polygon);
                        System.out.println("Projection to rasterize: \n" + projection.polygon);

                        rasteriser.rasterise(projection, LENS_DISTANCE);
                    }
                }
            }
        }
        return rasteriser.getRaster();
    }
    private Projection projectPolygon(Artifact artifact, Polygon polygon){
        return new Projection(
                projectVertex(artifact, polygon.getVertices()[A]),
                projectVertex(artifact, polygon.getVertices()[B]),
                projectVertex(artifact, polygon.getVertices()[C]),
                artifact.getColor()
        );
    }
    private Vertex projectVertex(Artifact artifact, Vertex vertex){
        double[] coordinates = add(artifact.localPointToGlobal(vertex.coordinates), CAMERA_POS);//TODO: Måste lägga till CAMERA_POS för att polygonerna skall projicera till camerans position istället för ORIGIN
        double[] interpolation = interpolate(coordinates, CAMERA_POS, LENS_DISTANCE, Z); //TODO: Egentligen vill jag flytta projicerinen och inte kameran, menmen...
        return new Vertex(
                interpolation[X],
                interpolation[Y],
                coordinates[Z]
        );
    }
    private boolean behindCamera(Polygon polygon){ //TODO: Why in the fudge of hell does this function work in reverse Z-wise??
        Vertex[] vertices = polygon.getVertices();
        return//TODO: This SHOULD wourk when the Z-values are closer than LENS_DISTANCE but works the other way around somehow.
                vertices[A].coordinates[Z] > LENS_DISTANCE &&
                vertices[B].coordinates[Z] > LENS_DISTANCE &&
                vertices[C].coordinates[Z] > LENS_DISTANCE;
    }
    public boolean behindCamera(Artifact artifact){
        return artifact.getZ() + artifact.getDepth() < LENS_DISTANCE;
    }

    private boolean facingCamera(Polygon polygon){ //TODO: Why in the fudge of hell does this function work in reverse Z-wise??
        return polygon.getNormal()[Z] > 0;//TODO: This SHOULD wourk when the Z-value is closer negative (ie. towards the camera) but works the other way around somehow.
    }
}
