package util;

public class MathUtil {
    public static double inverseSquare(double distance){
        return 1/(Math.pow(distance,2));
    }
    public static double sigmoid(double distance){
        return 1/(1 + Math.exp(-distance));
    }

    /**
     * Limits val to min and max.
     * @return val if val is between min and max, min if val is less than min
     * and max if val is more than max;
     */
    public static double clamp(double val, double min, double max){
        return Math.max(min, Math.min(val, max));
    }
    public static boolean within(double val, double min, double max){
        return val < max && val > min ;
    }
    /**
     * @return how much more val is than max, or how much less val is than min.
     * if val is between min and max 0 is returned.
     */
    public static double excess(double val, double min, double max){
        if (val > max)
            return val - max;
        if (val < min)
            return val - min;
        return 0;
    }
    /**
     * What value val must be assigned to keep the accumulated value of factors
     * added with val in between min and max.
     */
    public static double cap(double val, double min, double max, double ... factors){
        double acc = val;
        for (double factor: factors)
            acc += factor;
        if (within(acc, min, max))
            return val;
        else
            return val - excess(acc, min, max);
    }
    public static double[][] multiply(double[][] a, double[][] b){
        int aRows = a.length;
        int aCols = a[0].length;
        int bRows = b.length;
        int bCols = b[0].length;

        if (aCols != bRows)
            throw new ArithmeticException("matrix a's num columns must equal b's num rows. Got: " + "a num cols: " + aCols + " b num rows " + bRows);
        double[][] result = new double[aRows][bCols];

        int resRows = result.length;
        int resCols = result[0].length;


        for (int row = 0; row < resRows; row++){
            for (int col = 0; col < resCols; col++){
                double sum = 0;
                for (int i = 0; i < aCols; i++){
                    sum += a[row][i] * b[i][col];
                }
                result[row][col] = sum;
            }
        }
        return result;
    }
}
