package rendering;

import components.Polygon;
import components.Vertex;
import util.IllegalGeometryException;
import util.Mapper;

import java.awt.*;

import static util.MathUtil.clamp;
import static util.MathUtil.within;
import static util.VectorUtil.*;

class Rasteriser {

    private Raster raster;
    private int lensWidth, lensHeigh;
    private int imgWidth, imgHeight;
    private Mapper xMapper;
    private Mapper yMapper;

    Rasteriser(int lensWidth, int lensHeigh, int imgWidth, int imgHeight){
        raster = new Raster(imgWidth, imgHeight);
        this.lensWidth = lensWidth;
        this.lensHeigh = lensHeigh;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        //xMapper = new Mapper(-(double)lensWidth/2,(double)lensWidth/2, 0, imgWidth);
        //yMapper = new Mapper(-(double)lensHeigh/2, (double)lensHeigh/2, 0, imgHeight);
    }
    void cleanRaster(){
        raster = new Raster(imgWidth, imgHeight);
    }

    void rasterise(Polygon polygon, Projection projection, double zMin, double zMax){
        //TODO: DENNA process kommer vara trådad där antalet trådar i systemet överför info från projektionen till rastret.
        //TODO: Glöm inte att invänta traådarna när resteriseringen är klar, så att det blir en "atomär" operation.
        //TODO: Använd trådpoolen i ThreadPool

        //int xMin = xMapper.mapInt(projection.bounds.xMin);
        //int xMax = xMapper.mapInt(projection.bounds.xMax);
        //int yMin = yMapper.mapInt(projection.bounds.yMin);
        //int yMax = yMapper.mapInt(projection.bounds.yMax);

        //TODO: Borde egentligen basera sina bounds på mappern!!
        for (int x = projection.bounds.xMin; x < projection.bounds.xMax; x++){
            for (int y = projection.bounds.yMin; y < projection.bounds.yMax; y++){
                if (!outsideRaster(x, y) && insideProjection(x, y, projection)){
                    try{
                        double pixelDepth = pixelDepth(x, y, polygon);
                        if (/*pixelDepth > zMin &&*/ pixelDepth < zMax && pixelDepth < raster.getDepth(x,y)){
                            raster.setDepth(x, y, pixelDepth);
                            raster.setColorIfInside(x, y, getColorByDistance(projection.color, pixelDepth)); //TODO: colorByDist is only temporary!!
                        }
                    }catch (IllegalGeometryException e){
                        e.printStackTrace();
                        System.out.println("Threw IllegalGeometryException due to 'straight' artifacts.");
                        //TODO: Must use ther mothod to deal with 'straight' artifacts
                    }
                }
            }
        }
    }
    private int getColorByDistance(Color color, double dist){//TODO: This is only temporary
        dist /= 1000;
        int red = (int)clamp((color.getRed()/dist), 0, 255);
        int green = (int)clamp((color.getGreen()/dist), 0, 255);
        int blue = (int)clamp((color.getBlue()/dist), 0, 255);
        return new Color(red, green, blue).getRGB();
    }

    private double pixelDepth(double x, double y, Polygon polygon){
        //The old way. Let's mourn for a bit...
        /*Vertex[] projVerts = projection.polygon.getVertices();
        double[] target = new double[] {x, y}; //Target point without Z (ie. Z is unknown at this point)
        double[] intersectATarg_BC = intersectsAtXY(projVerts[A].coordinates, target, projVerts[B].coordinates, projVerts[C].coordinates);
        double[] interpolBC = interpolate(projVerts[B].coordinates, projVerts[C].coordinates, intersectATarg_BC);
        target = interpolate(projVerts[A].coordinates, interpolBC, target); //Target point WITH Z*/

        return intersectDistance(polygon, ORIGIN, unitVector(new double[] {x - Camera.CAMERA_OFFSET[X], y - Camera.CAMERA_OFFSET[Y], Camera.LENS_DISTANCE}));// distanceBetween(Camera.CAMERA_OFFSET, target);
    }

    private boolean outsideRaster(int x, int y){
        return !(within(x, 0, imgWidth) && within(y, 0, imgHeight));
    }
    private boolean insideProjection(int x, int y, Projection projection){
        Vertex[] vertices = projection.polygon.getVertices();
        double projArea = area(vertices[A].coordinates, vertices[B].coordinates, vertices[C].coordinates);

        double subArea1 = area(vertices[A].coordinates, vertices[B].coordinates, new double[] {x,y});
        double subArea2 = area(vertices[A].coordinates, new double[] {x,y}, vertices[C].coordinates);
        double subArea3 = area(new double[] {x,y}, vertices[B].coordinates, vertices[C].coordinates);

        return (int)projArea == (int)(subArea1 + subArea2 + subArea3); //TODO: kan behöva filas på. Kan uppstå problem pga. gränsvärden vid flyttal.
    }
    private double area(double[] a, double[] b, double[] c){
        return Math.abs((a[X]*(b[Y] - c[Y]) + b[X]*(c[Y] - a[Y]) + c[X]*(a[Y] - b[Y])) / 2);
    }

    Raster getRaster(){
        return raster;
    }

    private double intersectDistance(Polygon polygon, double[] lineSource, double[] dir){
        double[] n = polygon.getNormal();
        double[] u = dir;
        double[] w = subtract(polygon.getVertices()[A].asVector(),lineSource);
        boolean parallel = dotProduct(n,u) == 0;
        if (parallel)
            return -1;
        double s = (dotProduct(n,w)) / (dotProduct(n,u));
        if (intersectionInsidePolygon(polygon, add(lineSource,multiply(u,s))))
            return s;
        else
            return -1;
    }
    private boolean intersectionInsidePolygon(Polygon polygon, double[] i){
        double[] u = subtract(polygon.getVertices()[B].asVector(),polygon.getVertices()[A].asVector());
        double[] v = subtract(polygon.getVertices()[C].asVector(),polygon.getVertices()[A].asVector());
        double[] w = subtract(i, polygon.getVertices()[A].asVector());
        double uv = dotProduct(u,v);
        double wv = dotProduct(w,v);
        double wu = dotProduct(w,u);
        double vv = dotProduct(v);
        double uu = dotProduct(u);
        double commonDenominator = (Math.pow(uv,2) - uu * vv);
        double s = (uv * wv - vv * wu) / commonDenominator;
        double t = (uv * wu - uu * wv) / commonDenominator;
        return s >= 0 && t >= 0 && t+s <= 1;
    }

}
