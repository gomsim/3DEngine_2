package rendering;

import components.Vertex;
import util.Mapper;

import java.awt.*;

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
        xMapper = new Mapper(lensWidth, imgWidth);
        xMapper = new Mapper(lensHeigh, imgHeight);
    }
    void cleanRaster(){
        raster = new Raster(imgWidth, imgHeight);
    }

    void rasterise(Projection projection, double zBoundary){
        //TODO: DENNA process kommer vara trådad där antalet trådar i systemet överför info från projektionen till rastret.
        //TODO: Glöm inte att invänta traådarna när resteriseringen är klar, så att det blir en "atomär" operation.
        //TODO: Använd trådpoolen i ThreadPool

        //TODO: Rasteriseringen måste kolla färgen på pixlar och djupet (MHA interpolering)
        for (int x = projection.bounds.xMin; x < projection.bounds.xMax; x++){
            for (int y = projection.bounds.yMin; y < projection.bounds.yMax; y++){
                if (!outsideRaster(x,y) && insideProjection(projection, x, y)){
                    double pixelDepth = pixelDepth(projection, x, y);
                    if (pixelDepth > zBoundary && pixelDepth < raster.getDepth(x,y)){
                        raster.setDepth(x,y,pixelDepth);
                        raster.setColor(x,y,(int)pixelDepth);
                    }
                }
            }
        }
    }
    private double pixelDepth(Projection projection, int x, int y){ //This implementation was just found from reasoning. There is probably a strictly mathematical way.
        //TODO: Check which neither Target-vector nor BC can be strictly vertical or horizontal
        Vertex[] projVert = projection.polygon.getVertices();
        double[] targ = new double[] {x, y, 0}; //Target point without Z (unknown)

        double[] intersectATarg_BC = intersectsAtXY(projVert[A].coordinates, targ, projVert[B].coordinates, projVert[C].coordinates);
        double[] interpolBC = interpolate(projVert[B].coordinates, projVert[C].coordinates, intersectATarg_BC[X], X);

        targ = interpolate(projVert[A].coordinates, interpolBC, x, X); //Target point WITH Z

        return targ[Z];
    }
    private int rasterisePixel(Projection projection, int x, int y){ //TODO: Set colorBuffer here, based on depthBuffer (whose closest to camera)
        return -1;
    }

    private boolean outsideRaster(int x, int y){
        return x < 0 || x >= imgWidth || y < 0 || y >= imgHeight;
    }
    private boolean insideProjection(Projection projection, int x, int y){
        Vertex[] vertices = projection.polygon.getVertices();
        double projArea = area(vertices[A].coordinates, vertices[B].coordinates, vertices[C].coordinates);

        double subArea1 = area(vertices[A].coordinates, vertices[B].coordinates, new double[] {x,y});
        double subArea2 = area(vertices[A].coordinates, new double[] {x,y}, vertices[C].coordinates);
        double subArea3 = area(new double[] {x,y}, vertices[B].coordinates, vertices[C].coordinates);

        return (int)projArea == (int)(subArea1 + subArea2 + subArea3); //TODO: kan behöva filas på. Kan uppstå problem pga. fel med flyttal.
    }
    private double area(double[] a, double[] b, double[] c){
        return Math.abs((a[X]*(b[Y] - c[Y]) + b[X]*(c[Y] - a[Y]) + c[X]*(a[Y] - b[Y])) / 2);
    }

    Raster getRaster(){
        return raster;
    }

}
