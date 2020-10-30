package rendering;

import components.Vertex;
import components.Polygon;

import static util.VectorUtil.*;

class Projection {

    Polygon polygon;
    Bounds bounds;

    public Projection(Vertex a, Vertex b, Vertex c) {
        polygon = new Polygon(a, b, c);
        bounds = getBounds(a, b, c);
    }
    private Bounds getBounds(Vertex ... vertices){
        int xMin = Integer.MAX_VALUE, xMax = Integer.MIN_VALUE;
        int yMin = Integer.MAX_VALUE, yMax = Integer.MIN_VALUE;
        for (Vertex vertex: vertices){
            if (vertex.coordinates[X] < xMin)
                xMin = Math.round((float)vertex.coordinates[X]);
            else if (vertex.coordinates[X] > xMax)
                xMax = Math.round((float)vertex.coordinates[X]);
            if (vertex.coordinates[Y] < yMin)
                yMin = Math.round((float)vertex.coordinates[Y]);
            else if (vertex.coordinates[Y] > yMax)
                yMax = Math.round((float)vertex.coordinates[Y]);
        }
        return new Bounds(xMin, xMax, yMin, yMax);
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
