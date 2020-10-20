package components;

import util.VectorUtil;

import static util.VectorUtil.*;

public class Polygon {

    private static final int A = 0, B = 1, C = 2;
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

    public void rotate(double[][] rot) {
        double resA[] = {
                rot[X][0] * vertices[A].coordinates[X] + rot[X][1] * vertices[A].coordinates[Y] + rot[X][2] * vertices[A].coordinates[Z],
                rot[Y][0] * vertices[A].coordinates[X] + rot[Y][1] * vertices[A].coordinates[Y] + rot[Y][2] * vertices[A].coordinates[Z],
                rot[Z][0] * vertices[A].coordinates[X] + rot[Z][1] * vertices[A].coordinates[Y] + rot[Z][2] * vertices[A].coordinates[Z]
        };
        vertices[A].coordinates[X] = resA[X];
        vertices[A].coordinates[Y] = resA[Y];
        vertices[A].coordinates[Z] = resA[Z];

        double resB[] = {
                rot[X][0] * vertices[B].coordinates[X] + rot[X][1] * vertices[B].coordinates[Y] + rot[X][2] * vertices[B].coordinates[Z],
                rot[Y][0] * vertices[B].coordinates[X] + rot[Y][1] * vertices[B].coordinates[Y] + rot[Y][2] * vertices[B].coordinates[Z],
                rot[Z][0] * vertices[B].coordinates[X] + rot[Z][1] * vertices[B].coordinates[Y] + rot[Z][2] * vertices[B].coordinates[Z]
        };
        vertices[B].coordinates[X] = resB[X];
        vertices[B].coordinates[Y] = resB[Y];
        vertices[B].coordinates[Z] = resB[Z];

        double resC[] = {
                rot[X][0] * vertices[C].coordinates[X] + rot[X][1] * vertices[C].coordinates[Y] + rot[X][2] * vertices[C].coordinates[Z],
                rot[Y][0] * vertices[C].coordinates[X] + rot[Y][1] * vertices[C].coordinates[Y] + rot[Y][2] * vertices[C].coordinates[Z],
                rot[Z][0] * vertices[C].coordinates[X] + rot[Z][1] * vertices[C].coordinates[Y] + rot[Z][2] * vertices[C].coordinates[Z]
        };
        vertices[C].coordinates[X] = resC[X];
        vertices[C].coordinates[Y] = resC[Y];
        vertices[C].coordinates[Z] = resC[Z];
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

