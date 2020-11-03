package components;

import java.awt.*;
import java.util.*;

import static util.VectorUtil.*;

public class Artifact {

    //Dimensions of containing box
    private double x, y, z;
    private double width, height, depth;

    //shape
    private HashMap<Integer, Vertex> vertices = new HashMap<>();
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
                if (!vertices.containsKey(polygonVertices[i].hashCode()))
                    vertices.put(polygonVertices[i].hashCode(), polygonVertices[i]);
                else
                    polygonVertices[i] = vertices.get(polygonVertices[i].hashCode());
            }
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
    public void rotateWithoutNestling(double[][] rot){ //TODO: Clean this shit up
        double[] res = {
                rot[X][0]*x + rot[X][1]*y + rot[X][2]*z,
                rot[Y][0]*x + rot[Y][1]*y + rot[Y][2]*z,
                rot[Z][0]*x + rot[Z][1]*y + rot[Z][2]*z
        };

        x = res[X];
        y = res[Y];
        z = res[Z];
        setBounds();
    }

    public void transform(double[] transVec, double[] scaleVec, double[][] rotMatrix){
        //TODO: Detta är bättre att göra när väl renderingen är på plats så att man ser om det funkar bra.
        // Tanken är att transform-metoden gör en samanslagen transformationsberäkning för alla tre transformationer.
        // Varje artefakt lär dock behöva göra sin egen beräkning, därav placeringen av metoden här istället för i Engine.
        // referens: https://www.youtube.com/watch?v=vQ60rFwh2ig
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
    /*public void setBounds(){ //Old version that still works
        if (polygons.isEmpty())
            return;
        double[] polygonsXMax = new double[polygons.size()];
        double[] polygonsXMin = new double[polygons.size()];
        double[] polygonsYMax = new double[polygons.size()];
        double[] polygonsYMin = new double[polygons.size()];
        double[] polygonsZMax = new double[polygons.size()];
        double[] polygonsZMin = new double[polygons.size()];
        int i = 0;
        for (Polygon polygon: polygons){
            //TODO: Varför använder jag inte bara if-sateser här och ersätter polygonXMax med värdet om det är högra änhögsta värdet, t.ex.??
            polygonsXMax[i] = polygon.maxCoordinate(X);
            polygonsXMin[i] = polygon.minCoordinate(X);
            polygonsYMax[i] = polygon.maxCoordinate(Y);
            polygonsYMin[i] = polygon.minCoordinate(Y);
            polygonsZMax[i] = polygon.maxCoordinate(Z);
            polygonsZMin[i] = polygon.minCoordinate(Z);
            i++;
        }
        Arrays.sort(polygonsXMax);
        Arrays.sort(polygonsXMin);
        Arrays.sort(polygonsYMax);
        Arrays.sort(polygonsYMin);
        Arrays.sort(polygonsZMax);
        Arrays.sort(polygonsZMin);
        //TODO: The +1 from Polygon could probably go in here to accomodate the bounds to the polygon instead of the other way around.
        x += polygonsXMin[0];
        y += polygonsYMin[0];
        z += polygonsZMin[0];
        width = (polygonsXMax[polygonsXMax.length-1] - polygonsXMin[0]);
        height = (polygonsYMax[polygonsYMax.length-1] - polygonsYMin[0]);
        depth = (polygonsZMax[polygonsZMax.length-1] - polygonsZMin[0]);
        for (Polygon polygon: polygons){
            polygon.translate(new double[] {-polygonsXMin[0],-polygonsYMin[0],-polygonsZMin[0]});
        }
    }*/
    public String toString(){
        StringBuilder builder = new StringBuilder();
        //builder.append(color+" ["+x+" "+y+" "+z+"] ("+width+" "+height+" "+depth+")");
        builder.append("\n");
        for (Vertex vertex: vertices.values()){
            builder.append(vertex);
            builder.append("\n");
        }
        return builder.toString();
    }
}

