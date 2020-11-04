package components;

import util.IllegalGeometryException;

import static util.VectorUtil.*;

public class Vertex {

    public double[] coordinates = new double[NUM_DIMENSIONS];
    private static int MARGIN = 1;

    public Vertex(double x, double y, double z){
        /*if (x < 0 || y < 0 || z < 0){ temporarily disabled. //TODO: can be enabled again once Projection has been decoupleded from Polygon and vertex.
            throw new IllegalGeometryException("Coordinates cannot be negative. Got: [ x:" + x + " y:" + y + " z:" + z + " ]");
        }*/
        coordinates[X] = x + MARGIN;
        coordinates[Y] = y + MARGIN;
        coordinates[Z] = z + MARGIN;
    }
    public Vertex(double[] coordinates){
        this(coordinates[X], coordinates[Y], coordinates[Z]);
    }
    public double[] asVector(){
        return new double[] {coordinates[X],coordinates[Y],coordinates[Z]};
    }
    public void rotate(double[][] rot){ //TODO: Really just a matric multiplication between coordinates vector and rotation matrix... So can be simplified.
        double result[] = {
                rot[X][0] * coordinates[X] + rot[X][1] * coordinates[Y] + rot[X][2] * coordinates[Z],
                rot[Y][0] * coordinates[X] + rot[Y][1] * coordinates[Y] + rot[Y][2] * coordinates[Z],
                rot[Z][0] * coordinates[X] + rot[Z][1] * coordinates[Y] + rot[Z][2] * coordinates[Z]
        };
        coordinates[X] = result[X];
        coordinates[Y] = result[Y];
        coordinates[Z] = result[Z];
    }

    public void translate(double[] dir){
        coordinates[X] += dir[X];
        coordinates[Y] += dir[Y];
        coordinates[Z] += dir[Z];
    }

    public boolean equals(Object object){
        if (!(object instanceof Vertex))
            return false;
        Vertex other = (Vertex) object;
        return other.coordinates[X] == coordinates[X] &&
                other.coordinates[Y] == coordinates[Y] &&
                other.coordinates[Z] == coordinates[Z];
    }
    public int hashCode(){
        return (int)(13 * coordinates[X] * coordinates[Y] * coordinates[Z]);
    }
    public String toString(){
        return "["+(int)coordinates[X]+" "+(int)coordinates[Y]+" "+(int)coordinates[Z]+"]";
    }
}