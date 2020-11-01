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
        xMapper = new Mapper(-(double)lensWidth/2,(double)lensWidth/2, 0, imgWidth);
        yMapper = new Mapper(-(double)lensHeigh/2, (double)lensHeigh/2, 0, imgHeight);
    }
    void cleanRaster(){
        raster = new Raster(imgWidth, imgHeight);
    }

    void rasterise(Projection projection, double zBoundary){
        //TODO: DENNA process kommer vara trådad där antalet trådar i systemet överför info från projektionen till rastret.
        //TODO: Glöm inte att invänta traådarna när resteriseringen är klar, så att det blir en "atomär" operation.
        //TODO: Använd trådpoolen i ThreadPool

        //TODO: Rasteriseringen måste kolla färgen på pixlar och djupet (MHA interpolering)
        int xMin = xMapper.mapInt(projection.bounds.xMin);
        int xMax = xMapper.mapInt(projection.bounds.xMax);
        int yMin = yMapper.mapInt(projection.bounds.yMin);
        int yMax = yMapper.mapInt(projection.bounds.yMax);

        //TODO: Borde egentligen basera sina bounds på mappern!!
        for (int x = projection.bounds.xMin; x < projection.bounds.xMax; x++){
            for (int y = projection.bounds.yMin; y < projection.bounds.yMax; y++){
                if (!outsideRaster(x, y) && insideProjection(projection, x, y)){
                    double pixelDepth = pixelDepth(projection, x, y);
                    if (pixelDepth > zBoundary && pixelDepth < raster.getDepth(x,y)){
                        raster.setDepth(x, y, pixelDepth);
                        double inverse = sigmoid(pixelDepth);
                        raster.setColor(x, y, new Color((int)(projection.color.getRed()*inverse), (int)(projection.color.getGreen()*inverse), (int)(projection.color.getBlue()*inverse)).getRGB());
                    }
                }
            }
        }
    }
    private double pixelDepth(Projection projection, int x, int y){ //This implementation was just found from reasoning. There is probably a strictly mathematical way.
        //TODO: Check which neither Target-vector nor BC can be strictly vertical or horizontal
        Vertex[] projVert = projection.polygon.getVertices();
        double[] targ = new double[] {x, y, 0}; //Target point without Z (ie. Z is unknown at this point)

        double[] intersectATarg_BC = intersectsAtXY(projVert[A].coordinates, targ, projVert[B].coordinates, projVert[C].coordinates);

        double[] interpolBC = interpolate(projVert[B].coordinates, projVert[C].coordinates, intersectATarg_BC);
        targ = interpolate(projVert[A].coordinates, interpolBC, targ); //Target point WITH Z

        return targ[Z];
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

        return (int)projArea == (int)(subArea1 + subArea2 + subArea3); //TODO: kan behöva filas på. Kan uppstå problem pga. gränsvärden vid flyttal.
    }
    private double area(double[] a, double[] b, double[] c){
        return Math.abs((a[X]*(b[Y] - c[Y]) + b[X]*(c[Y] - a[Y]) + c[X]*(a[Y] - b[Y])) / 2);
    }

    Raster getRaster(){
        return raster;
    }

}
