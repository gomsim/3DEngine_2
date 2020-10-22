package components;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

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
                    polygonVertices[i] = vertices.get(polygonVertices.hashCode());
            }
        }
    }
    public double[] getPos(){
        return new double[] {x,y,z};
    }
    public boolean isBehindOrigin(){
        return z < 0 && z + depth < 0;
    }

    public HashSet<Polygon> getPolygons(){
        return polygons;
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

    private double[] globalPointToLocal(double[] global){
        return subtract(global, getPos());
    }
    private double[] localPointToGlobal(double[] local){
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
    private void setBounds(){
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
        x += polygonsXMin[0];
        y += polygonsYMin[0];
        z += polygonsZMin[0];
        width = (polygonsXMax[polygonsXMax.length-1] - polygonsXMin[0]);
        height = (polygonsYMax[polygonsYMax.length-1] - polygonsYMin[0]);
        depth = (polygonsZMax[polygonsZMax.length-1] - polygonsZMin[0]);
        for (Polygon polygon: polygons){
            polygon.translate(new double[] {-polygonsXMin[0],-polygonsYMin[0],-polygonsZMin[0]});
        }
    }
    public String toString(){
        StringBuilder builder = new StringBuilder();
        //builder.append(color+" ["+x+" "+y+" "+z+"] ("+width+" "+height+" "+depth+")");
        builder.append("\n");
        for (Vertex vertex: vertices.values()){
            builder.append(vertices);
            builder.append("\n");
        }
        return builder.toString();
    }
}

