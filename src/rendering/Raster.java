package rendering;

import util.OutsideBoundsException;

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
        throwExceptionIfOutsideBounds(x, y);
        return depthBuffer[y*imgWidth + x];
    }
    void setDepth(int x, int y, double z){
        throwExceptionIfOutsideBounds(x, y);
        depthBuffer[y*imgWidth + x] = z;
    }
    double getColor(int x, int y){
        throwExceptionIfOutsideBounds(x, y);
        return colorBuffer[y*imgWidth + x];
    }
    void setColor(int x, int y, int color){
        throwExceptionIfOutsideBounds(x, y);
        colorBuffer[y*imgWidth + x] = color;
    }
    private boolean outsideBounds(int x, int y){
        return y*imgWidth + x >= depthBuffer.length || y*imgWidth + x < 0;
    }
    private void throwExceptionIfOutsideBounds(int x, int y){
        if (outsideBounds(x, y))
            throw new OutsideBoundsException("x:" + x + " y:" + y + " is outside raster of size (" + imgWidth + "," + imgHeight + ")");
    }
}
