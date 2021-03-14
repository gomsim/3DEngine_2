package rendering;

import components.Vertex;
import util.IllegalGeometryException;
import util.Mapper;

import java.awt.*;

import static util.MathUtil.clamp;
import static util.MathUtil.inverseSquare;
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

    void rasterise(Projection projection, double zMin, double zMax){
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
                        double pixelDepth = pixelDepth(x, y, projection);
                        if (pixelDepth > zMin && pixelDepth < zMax && pixelDepth < raster.getDepth(x,y)){
                            raster.setDepth(x, y, pixelDepth);
                            //raster.setColor(x, y, (int)(pixelDepth));
                            if(((int)pixelDepth) % 10 < 3)
                                raster.setColor(x, y, Color.RED.getRGB());
                            else
                                raster.setColor(x, y, getColorByDistance(Color.CYAN, pixelDepth)); //TODO: colorByDist is only temporary!!
                        }
                    }catch (IllegalGeometryException e){
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
    private double pixelDepth(int x, int y, Projection projection){ //This implementation was just found from reasoning. There is probably a strictly mathematical way.
        Vertex[] projVert = projection.polygon.getVertices();
        double[] target = new double[] {x, y}; //Target point without Z (ie. Z is unknown at this point)

        double[] intersectATarg_BC = intersectsAtXY(projVert[A].coordinates, target, projVert[B].coordinates, projVert[C].coordinates);

        double[] interpolBC = interpolate(projVert[B].coordinates, projVert[C].coordinates, intersectATarg_BC);
        target = interpolate(projVert[A].coordinates, interpolBC, target); //Target point WITH Z

        //TODO: Recently changed from raw Z-value to distance from ORIGIN.
        // Probably more precise, but less cost effective
        return distanceBetween(ORIGIN, target);
    }

    private boolean outsideRaster(int x, int y){
        return x < 0 || x >= imgWidth || y < 0 || y >= imgHeight;
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

}
