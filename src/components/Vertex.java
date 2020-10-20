package components;

import util.VectorUtil;

import static util.VectorUtil.*;

public class Vertex {

    public double[] coordinates = new double[VectorUtil.NUM_DIMENSIONS];

    public Vertex(int x, int y, int z){
        //Should throw exception if any coordinate is negative, I think.
        coordinates[X] = x;//+1; //TODO: Why +1 again??
        coordinates[Y] = y;//+1; //TODO: Why +1 again??
        coordinates[Z] = z;//+1; //TODO: Why +1 again??
    }
    public double[] asVector(){
        return new double[] {coordinates[X],coordinates[Y],coordinates[Z]};
    }
    public String toString(){
        return "["+(int)coordinates[X]+" "+(int)coordinates[Y]+" "+(int)coordinates[Z]+"]";
    }
}