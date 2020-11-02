package util;

public class VectorUtil {

    public static final int X = 0, Y = 1, Z = 2;
    public static final int RED = 0, GREEN = 1, BLUE = 2;
    public static final int A = 0, B = 1, C = 2;
    public static final int NUM_DIMENSIONS = 3;
    public static final double[] ORIGIN = {0,0,0};

    public static double[] unitVector(int x, int y, int z){
        double length = Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
        return new double[] {x/length,y/length,z/length};
    }
    public static double[] unitVector(double[] vector){
        if (vector.length != NUM_DIMENSIONS)
            throw new IllegalArgumentException("Vector not of length 3");
        double length = Math.sqrt(Math.pow(vector[X],2) + Math.pow(vector[Y],2) + Math.pow(vector[Z],2));
        return new double[] {vector[X]/length,vector[Y]/length,vector[Z]/length};
    }
    public static double[] subtract(double[] vec, double[] by){
        double[] result = new double[NUM_DIMENSIONS];
        result[X] = vec[X] - by[X];
        result[Y] = vec[Y] - by[Y];
        result[Z] = vec[Z] - by[Z];
        return result;
    }
    public static double[] add(double[] vec, double[] by){
        double[] result = new double[NUM_DIMENSIONS];
        result[X] = vec[X] + by[X];
        result[Y] = vec[Y] + by[Y];
        result[Z] = vec[Z] + by[Z];
        return result;
    }
    public static double[] add(double[] vec, double by){
        double[] result = new double[NUM_DIMENSIONS];
        result[X] = vec[X] + by;
        result[Y] = vec[Y] + by;
        result[Z] = vec[Z] + by;
        return result;
    }
    public static double dotProduct(double[] a, double[] b){
        return a[X] * b[X] + a[Y] * b[Y] + a[Z] * b[Z];
    }
    public static double dotProduct(double[] vec){
        return vec[X] * vec[X] + vec[Y] * vec[Y] + vec[Z] * vec[Z];
    }
    public static double[] multiply(double[] vec, double multiplier){
        double[] result = new double[NUM_DIMENSIONS];
        result[X] = vec[X] * multiplier;
        result[Y] = vec[Y] * multiplier;
        result[Z] = vec[Z] * multiplier;
        return result;
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
    public static double[] copy(double[] vec){
        double[] copy = new double[NUM_DIMENSIONS];
        for (int i = 0; i < vec.length; i++)
            copy[i] = vec[i];
        return copy;
    }

    /**
     * A polygon having the corners A, B, and C. a is A-B and b is C-B.
     * @param a is the first corner minus the third.
     * @param b is the second corner minus the third.
     * @return the normal of the triangle formed by a, b and the origin.
     */
    public static double[] getNormal(double[] a, double[] b){
        double[] normal = new double[NUM_DIMENSIONS];
        normal[X] = a[Y] * b[Z] - a[Z] * b[Y];
        normal[Y] = a[Z] * b[X] - a[X] * b[Z];
        normal[Z] = a[X] * b[Y] - a[Y] * b[X];
        return unitVector(normal);
    }
    public static double[] getReflection(double[] incident, double[] normal){
        //TODO: Can perhaps be more efficient by hard coing the maths.
        //TODO: Also! Needs to take int concideration which direction of the normal the incident comes
        return subtract(incident,multiply(multiply(normal,dotProduct(incident,normal)),2));
    }
    public static String asString(double[] vec){
        if (vec == null)
            return null;
        return "["+vec[X]+","+vec[Y]+","+vec[Z]+"]";
    }
    public static double length(double[] vec){
        return
            Math.sqrt(
                Math.pow(vec[X],2) +
                Math.pow(vec[Y],2) +
                Math.pow(vec[Z],2)
            );
    }
    public static double angleBetween(double[] v1, double[] v2){
        return Math.acos(VectorUtil.dotProduct(v1,v2)/(VectorUtil.length(v1)*VectorUtil.length(v2)));
    }
    public static double[] intersectsAtXY(double[] v1a, double[] v1b, double[] v2a, double[] v2b){ //This one feels CONTRIIIIIIVED...
        double[] intersection = new double[NUM_DIMENSIONS - 1];
        intersection[X] =
                ((v1a[X]*v1b[Y] - v1a[Y]*v1b[X])*(v2a[X] - v2b[X]) -
                (v2a[X]*v2b[Y] - v2a[Y]*v2b[X])*(v1a[X] - v1b[X])) /
                ((v1a[X] - v1b[X])*(v2a[Y] - v2b[Y]) -
                (v2a[X] - v2b[X])*(v1a[Y] - v1b[Y]));

        intersection[Y] =
                ((v1a[X]*v1b[Y] - v1a[Y]*v1b[X])*(v2a[Y] - v2b[Y]) -
                (v2a[X]*v2b[Y] - v2a[Y]*v2b[X])*(v1a[Y] - v1b[Y])) /
                ((v1a[X] - v1b[X])*(v2a[Y] - v2b[Y]) -
                (v2a[X] - v2b[X])*(v1a[Y] - v1b[Y]));
        return intersection;
    }
    public static double[] interpolate(double[] a, double[] b, double at, int axis){
        if (axis != X && axis != Y && axis != Z){
            throw new IllegalGeometryException("Axis must be 0, 1 or 2. Got: " + axis);
        }
        if (a[axis] == b[axis]){
            throw new IllegalGeometryException("Impossible interpolation between [" + a[X] + "," + a[Y] + "," + a[Z] + "] and [" + b[X] + "," + b[Y] + "," + b[Z] + "] at axis " + (axis==0?"x":axis==1?"y":"z") + ":" + at);
        }
        double[] deltaAB = vectorOf(a, b);
        at -= a[axis];
        double ratioAt = at/deltaAB[axis];
        return add(multiply(deltaAB, ratioAt), a);
    }
    public static double[] interpolate(double[] a, double[] b, double[] at){ //TODO: Not always finding a valid axis to interpolate
        //TODO: gets "IllegalGeometryException: Impossible interpolation between [1144.3718024985128,537.1046995835812,1682.0] and [1144.3718024985128,537.1046995835812,1682.0] at axis y:NaN"
        int axis = 0;
        while (axis < at.length - 1 && a[axis] == b[axis]){
            axis++;
        }
        return interpolate(a, b, at[axis], axis);
    }
    public static double[] vectorOf(double[] from, double[] to){
        return subtract(to, from);
    }
    public static int normalAxis(double[] normal){
        if (normal[X] != 0)
            return X;
        if (normal[Y] != 0)
            return Y;
        if (normal[Z] != 0)
            return Z;
        else
            return -1;
    }
    public static double inverseSquare(double distance){
        return 1/(Math.pow(distance,2));
    }
    public static double sigmoid(double distance){
        return 1/(1 + Math.exp(-distance));
    }
}
