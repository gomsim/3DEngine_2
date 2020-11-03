package components;

import util.IllegalGeometryException;

import static util.VectorUtil.*;

public class Vertex {

    public double[] coordinates = new double[NUM_DIMENSIONS];

    public Vertex(double x, double y, double z){
        /*if (x < 0 || y < 0 || z < 0){ temporarily disabled. //TODO: can be enabled again once Projection has been decoupleded from Polygon and vertex.
            throw new IllegalGeometryException("Coordinates cannot be negative. Got: [ x:" + x + " y:" + y + " z:" + z + " ]");
        }*/
        //TODO: If keeping the +1 it should probably be in a constand MARGIN
        coordinates[X] = x+1; //TODO: Why +1 again?? On second thought I think it is because otherwise the polygon could be exactly AT the bounding box edge of the artifact and not be rendered, if the system is set up in such a way.
        coordinates[Y] = y+1; //TODO: Why +1 again?? On second thought I think it is because otherwise the polygon could be exactly AT the bounding box edge of the artifact and not be rendered, if the system is set up in such a way.
        coordinates[Z] = z+1; //TODO: Why +1 again?? On second thought I think it is because otherwise the polygon could be exactly AT the bounding box edge of the artifact and not be rendered, if the system is set up in such a way.
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