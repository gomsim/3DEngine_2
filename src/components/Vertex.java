package components;

import util.IllegalGeometryException;

import static util.VectorUtil.*;

public class Vertex {

    public double[] coordinates = new double[NUM_DIMENSIONS];

    public Vertex(int x, int y, int z){
        if (x < 0 || y < 0 || z < 0){
            throw new IllegalGeometryException("Vertices cannot be negative");
        }
        coordinates[X] = x;//+1; //TODO: Why +1 again?? On second thought I think it is because otherwise the polygon could be exactly AT the bounding box edge of the artifact and not be rendered, if the system is set up in such a way.
        coordinates[Y] = y;//+1; //TODO: Why +1 again?? On second thought I think it is because otherwise the polygon could be exactly AT the bounding box edge of the artifact and not be rendered, if the system is set up in such a way.
        coordinates[Z] = z;//+1; //TODO: Why +1 again?? On second thought I think it is because otherwise the polygon could be exactly AT the bounding box edge of the artifact and not be rendered, if the system is set up in such a way.
    }
    public double[] asVector(){
        return new double[] {coordinates[X],coordinates[Y],coordinates[Z]};
    }
    public String toString(){
        return "["+(int)coordinates[X]+" "+(int)coordinates[Y]+" "+(int)coordinates[Z]+"]";
    }
}