package components;

import java.awt.*;
import java.util.*;

import static util.VectorUtil.*;

public class Artifact {

    //Dimensions of containing box
    private double x, y, z;
    private double width, height, depth;

    //shape
    private HashMap<Vertex, Vertex> vertices = new HashMap<>();
    private HashSet<Polygon> polygons = new HashSet<>();
    private Color color;

    public Artifact(int x, int y, int z, Color color, Polygon ... polygons){
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        addPolygons(polygons);
        setBounds();
    }

    private void addPolygons(Polygon ... polygons){
        for (Polygon polygon: polygons){
            Vertex[] polygonVertices = polygon.getVertices();
            for (int i = 0; i < polygonVertices.length; i++){
                if (!vertices.containsKey(polygonVertices[i]))
                    vertices.put(polygonVertices[i], polygonVertices[i]);
                else
                    polygonVertices[i] = vertices.get(polygonVertices[i]);
            }
            polygon.setArt(this);
            this.polygons.add(polygon);
        }
    }
    public double[] getPos(){
        return new double[] {x,y,z};
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDepth() {
        return depth;
    }

    public HashSet<Polygon> getPolygons(){
        return polygons;
    }
    public Collection<Vertex> getVertices(){
        return vertices.values();
    }
    public Color getColor(){
        return color;
    }
    public boolean insideBoundingBox(double[] point){
        int margin = 1;
        return point[X] >= x-margin &&
                point[X] <= x + width+margin &&
                point[Y] >= y-margin &&
                point[Y] <= y + height+margin &&
                point[Z] >= z-margin &&
                point[Z] <= z + depth+margin;
    }

    public double[] globalPointToLocal(double[] global){
        return subtract(global, getPos());
    }
    public double[] localPointToGlobal(double[] local){
        return add(local, getPos());
    }

    public void translate(double[] dir){
        x += dir[X];
        y += dir[Y];
        z += dir[Z];
    }
    public void scale(/*Some parameters*/){

    }

    public void rotate(double[][] rot){
        double[] res = {
                rot[X][0]*x + rot[X][1]*y + rot[X][2]*z,
                rot[Y][0]*x + rot[Y][1]*y + rot[Y][2]*z,
                rot[Z][0]*x + rot[Z][1]*y + rot[Z][2]*z
        };

        x = res[X];
        y = res[Y];
        z = res[Z];
        for (Vertex vertex: vertices.values()){
            vertex.rotate(rot);
        }
        setBounds();
    }

    public void setBounds(){
        if (vertices.isEmpty())
            return;
        double xMax = Double.MIN_VALUE, yMax = Double.MIN_VALUE, zMax = Double.MIN_VALUE;
        double xMin = Double.MAX_VALUE, yMin = Double.MAX_VALUE, zMin = Double.MAX_VALUE;
        for (Vertex vertex: vertices.values()){
            if (vertex.coordinates[X] < xMin)
                xMin = vertex.coordinates[X];
            if (vertex.coordinates[X] > xMax)
                xMax = vertex.coordinates[X];
            if (vertex.coordinates[Y] < yMin)
                yMin = vertex.coordinates[Y];
            if (vertex.coordinates[Y] > yMax)
                yMax = vertex.coordinates[Y];
            if (vertex.coordinates[Z] < zMin)
                zMin = vertex.coordinates[Z];
            if (vertex.coordinates[Z] > zMax)
                zMax = vertex.coordinates[Z];
        }
        x += xMin;
        y += yMin;
        z += zMin;
        width = (xMax - xMin);
        height = (yMax - yMin);
        depth = (zMax - zMin);
        for (Vertex vertex: vertices.values()){
            vertex.translate(new double[] {-xMin,-yMin,-zMin});
        }
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(color+" ["+x+" "+y+" "+z+"] ("+width+" "+height+" "+depth+")");
        builder.append("\n");
        for (Vertex vertex: vertices.values()){
            builder.append(vertex);
            builder.append("\n");
        }
        return builder.toString();
    }
}

