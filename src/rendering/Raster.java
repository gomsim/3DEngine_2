package rendering;

class Raster {

    int[] colorBuffer;
    double[] depthBuffer;

    private int imgWidth, imgHeight;

    Raster(int imgWidth, int imgHeight){
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        colorBuffer = new int[imgWidth * imgHeight];
        depthBuffer = new double[imgWidth * imgHeight];
        for (int i = 0; i < depthBuffer.length; i++){
            depthBuffer[i] = Double.MAX_VALUE;
        }
    }

    double getDepth(int x, int y){
        return depthBuffer[y*imgWidth + x];
    }
    void setDepth(int x, int y, double z){
        depthBuffer[y*imgWidth + x] = z;
    }
    double getColor(int x, int y){
        return colorBuffer[y*imgWidth + x];
    }
    void setColor(int x, int y, int color){
        colorBuffer[y*imgWidth + x] = color;
    }
}
