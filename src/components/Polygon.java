package components;

import util.VectorUtil;

import static util.VectorUtil.*;

public class Polygon {

    private Vertex[] vertices = new Vertex[NUM_DIMENSIONS];

    public Polygon(Vertex a, Vertex b, Vertex c) {
        vertices[A] = a;
        vertices[B] = b;
        vertices[C] = c;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public double[] getNormal() {
        return VectorUtil.getNormal(subtract(vertices[B].asVector(), vertices[A].asVector()),
                subtract(vertices[C].asVector(), vertices[A].asVector()));
    }

    public void translate(double[] dir) {
        for (Vertex vertex : vertices) {
            vertex.coordinates[X] += dir[X];
            vertex.coordinates[Y] += dir[Y];
            vertex.coordinates[Z] += dir[Z];
        }
    }

    public double minCoordinate(int axis){
        double min = vertices[A].coordinates[axis];
        if  (vertices[B].coordinates[axis] < min)
            min = vertices[B].coordinates[axis];
        if (vertices[C].coordinates[axis] < min)
            min = vertices[C].coordinates[axis];
        return min;
    }

    public double maxCoordinate(int axis){
        double max = vertices[A].coordinates[axis];
        if  (vertices[B].coordinates[axis] > max)
            max = vertices[B].coordinates[axis];
        if (vertices[C].coordinates[axis] > max)
            max = vertices[C].coordinates[axis];
        return max;
    }

    public boolean equals(Object object){
        if (!(object instanceof Polygon))
            return false;
        Polygon other = (Polygon) object;
        return other.vertices[X].equals(vertices[X]) &&
                other.vertices[Y].equals(vertices[Y]) &&
                other.vertices[Z].equals(vertices[Z]);
    }
    public int hashCode(){
        return 13 * vertices[X].hashCode() * vertices[Y].hashCode() * vertices[Z].hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Vertex vertex : vertices) {
            builder.append(vertex);
            builder.append("\n");
        }
        builder.append("   ---   ");
        return builder.toString();
    }
}

