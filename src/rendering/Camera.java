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
    private static final double LENS_DISTANCE = 100;

    private static final int LENS_WIDTH = calculateLensWidth(VIEW_ANGLE, LENS_DISTANCE);
    private static final int LENS_HEIGHT = calculateLensHeight(VIEW_ANGLE, LENS_DISTANCE);

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
            if (!artifact.isBehindOrigin()){
                for (Polygon polygon: artifact.getPolygons()){
                    if (!behindCamera(polygon) && facingCamera(polygon)){//TODO: Idf you see nothing, try removing these statements temporarily
                        Projection projection = projectPolygon(artifact, polygon);
                        rasteriser.rasterise(projection);
                    }
                }
            }
        }
        return rasteriser.getRaster();
    }
    private Projection projectPolygon(Artifact artifact, Polygon polygon){
        return new Projection(
                projectVertex(artifact, polygon.getVertices()[Polygon.A]),
                projectVertex(artifact, polygon.getVertices()[Polygon.B]),
                projectVertex(artifact, polygon.getVertices()[Polygon.C])
        );
    }
    private Vertex projectVertex(Artifact artifact, Vertex vertex){
        double[] coordinates = artifact.localPointToGlobal(vertex.coordinates);
        double zRatio = LENS_DISTANCE / coordinates[Z];
        return new Vertex(
                coordinates[X] * zRatio,
                coordinates[Y] * zRatio,
                coordinates[Z]
                );
    }
    private boolean behindCamera(Polygon polygon){
        Vertex[] vertices = polygon.getVertices();
        return
                vertices[A].coordinates[Z] < 0 &&
                vertices[B].coordinates[Z] < 0 &&
                vertices[C].coordinates[Z] < 0;
    }

    private boolean facingCamera(Polygon polygon){
        return polygon.getNormal()[Z] < 0;
    }
}
