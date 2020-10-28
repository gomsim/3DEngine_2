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
                if (pixelDepth < raster.getDepth(x,y)){
                    raster.setDepth(x,y,pixelDepth);
                    //raster.setColor(x,y,(int)pixelDepth*100);
                }
            }
        }
    }
    private double pixelDepth(Projection projection, int x, int y){ //This implementation was just found from reasoning. There is probably a strictly mathematical way.
        //TODO: Check which neither Target-vector nor BC can be strictly vertical or horizontal
        Vertex[] projVert = projection.polygon.getVertices();
        double[] target = new double[] {x, y, 0};
        double[] intersectionBE = intersectsAtXY(projVert[A].coordinates, target, projVert[B].coordinates, projVert[C].coordinates);
        double[] interpolBC = interpolate(projVert[B].coordinates, projVert[C].coordinates, intersectionBE[X], X);
        double[] interpolTarget = interpolate(projVert[A].coordinates, interpolBC, x, X);
        return interpolTarget[Z];
    }
    private int rasterisePixel(Projection projection, int x, int y){ //TODO: Set colorBuffer here, based on depthBuffer (whose closest to camera)
        return -1;
    }

    Raster getRaster(){
        return raster;
    }

}
