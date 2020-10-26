package rendering;

import components.Vertex;
import util.Mapper;

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

    void rasterise(Projection projection){
        //TODO: DENNA process kommer vara trådad där antalet trådar i systemet överför info från projektionen till rastret.
        //TODO: Glöm inte att invänta traådarna när resteriseringen är klar, så att det blir en "atomär" operation.
        //TODO: Använd trådpoolen i ThreadPool

        //TODO: Rasteriseringen måste kolla färgen på pixlar och djupet (MHA interpolering)
        for (int x = projection.bounds.xMin; x < projection.bounds.xMax; x++){
            for (int y = projection.bounds.yMin; y < projection.bounds.yMax; y++){
                double pixelDepth = pixelDepth(projection, x, y);
            }
        }
    }
    private double pixelDepth(Projection projection, int x, int y){ //This implementation was just found from reasoning. There is probably a strictly mathematical way.
        double[] centerVector = {x,y,0};
        Vertex[] projectionVertices = projection.polygon.getVertices();
        double[] intersectionBE = intersectsAtXY(projectionVertices[A].coordinates, new double[] {x*999,y*999}, projectionVertices[B].coordinates, projectionVertices[C].coordinates);
        double deltaX = projectionVertices[B].coordinates[X];
        double deltaZ = projectionVertices[C].coordinates[X];
        double intersectRatio = 0;
        //TODO: Do some focking shait here tomorrow when the sun is upp...
        return -1;
    }
    private int rasterisePixel(Projection projection, int x, int y){
        return -1;
    }

    Raster getRaster(){
        return raster;
    }

}
