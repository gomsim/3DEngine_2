package rendering;

import components.Artifact;

import static rendering.Renderer.*;

class Camera {

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
        //Rensa rasteriseraren
        //Loopa genom alla artefakter och projicera dem
        //  För varje artefakt be rasteriseraren rasterisera artefakten
        //Hämta ut och returnera rastret
    }
    private Projection project(Artifact artifact){
        return null;
    }
}
