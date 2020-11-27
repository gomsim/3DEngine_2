package rendering;

import components.Polygon;
import components.Vertex;
import util.IllegalGeometryException;

import java.util.ArrayList;

import static util.VectorUtil.*;
import static util.VectorUtil.interpolate;

class ClippingData {

    private ArrayList<Vertex> inFront = new ArrayList<>();
    private ArrayList<Vertex> behind = new ArrayList<>();
    private double lensDistance;

    ClippingData(Polygon polygon, double lensDistance){
        this.lensDistance = lensDistance;

        for (Vertex vertex: polygon.getVertices()){
            if (vertex.coordinates[Z] < lensDistance) {
                behind.add(vertex);
            }
            else {
                inFront.add(vertex);
            }
        }
    }

    Polygon[] clipPolygons(){
        if (inFront.size() == 1){
            return new Polygon[] {clipTwoVertices()};
        }else if (inFront.size() == 2){
            return clipOneVertex();
        }else{
            throw new IllegalGeometryException("Doesn't need clipping, smh... -___-' -> " + inFront.size());
        }
    }

    boolean needsClipping(){
        return inFront.size() != 3;
    }

    private Polygon clipTwoVertices(){
        Vertex a = inFront.get(0);
        double[] b = interpolate(a.coordinates, behind.get(0).coordinates, lensDistance, Z);
        double[] c = interpolate(a.coordinates, behind.get(1).coordinates, lensDistance, Z);
        return new Polygon(
                a,
                new Vertex(b[X], b[Y], b[Z]),
                new Vertex(c[X], c[Y], c[Z])
        );
    }

    private Polygon[] clipOneVertex(){
        Vertex a = inFront.get(0);
        Vertex b = inFront.get(1);
        double[] c = interpolate(a.coordinates, behind.get(0).coordinates, lensDistance, Z);
        double[] d = interpolate(b.coordinates, behind.get(0).coordinates, lensDistance, Z);
        return new Polygon[] {
                new Polygon(
                        a,
                        b,
                        new Vertex(c)
                ),
                new Polygon(
                        b,
                        new Vertex(c),
                        new Vertex(d)
                )
        };
    }
}
