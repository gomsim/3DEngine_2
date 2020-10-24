package rendering;

class Raster {

    int[] colorBuffer;
    double[] depthBuffer;

    Raster(int imgWidth, int imgHeight){
        colorBuffer = new int[imgWidth * imgHeight];
        depthBuffer = new double[imgWidth * imgHeight];
    }
}
