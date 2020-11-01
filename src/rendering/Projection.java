package rendering;

import components.Vertex;
import components.Polygon;

import java.awt.*;

import static util.VectorUtil.*;

class Projection {

    Color color;
    Polygon polygon;
    Bounds bounds;

    public Projection(Vertex a, Vertex b, Vertex c, Color color) {
        this.color = color;
        polygon = new Polygon(a, b, c);
        bounds = getBounds(a, b, c);
    }
    public Projection(Polygon polygon, Color color) {
        this(
                polygon.getVertices()[A],
                polygon.getVertices()[B],
                polygon.getVertices()[C],
                color
        );
    }
    private Bounds getBounds(Vertex ... vertices){
        float xMin = Integer.MAX_VALUE, xMax = Integer.MIN_VALUE;
        float yMin = Integer.MAX_VALUE, yMax = Integer.MIN_VALUE;
        for (Vertex vertex: vertices){
            if (vertex.coordinates[X] < xMin)
                xMin = (float)vertex.coordinates[X];
            if (vertex.coordinates[X] > xMax)
                xMax = (float)vertex.coordinates[X];
            if (vertex.coordinates[Y] < yMin)
                yMin = (float)vertex.coordinates[Y];
            if (vertex.coordinates[Y] > yMax)
                yMax = (float)vertex.coordinates[Y];
        }
        return new Bounds(Math.round(xMin), Math.round(xMax), Math.round(yMin), Math.round(yMax));
    }

    static class Bounds{
        int xMin, xMax, yMin, yMax;
        Bounds(int xMin, int xMax, int yMin, int yMax){
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }
    }
}
