package rendering;

public class Camera {

    private static final int VIEW_ANGLE = 90;
    private static final double LENS_DISTANCE = 100;
    private static final double LENS_WIDTH = calculateLensWidth(VIEW_ANGLE, LENS_DISTANCE);
    private static final double LENS_HEIGHT = calculateLensHeight(VIEW_ANGLE, LENS_DISTANCE);

    private static double calculateLensWidth(int viewAngle, double lensDistance){
        return lensDistance * 2 * (viewAngle / 90);
    }
    private static double calculateLensHeight(int viewAngle, double lensDistance){
        return lensDistance * 2 * (viewAngle / 90);
    }

    Projection capture(){

    }
}
