package rendering;

class Rasteriser {

    private Raster raster;
    private int lensWidth, lensHeigh;
    private int imgWidth, imgHeight;

    Rasteriser(int lensWidth, int lensHeigh, int imgWidth, int imgHeight){
        raster = new Raster(imgWidth, imgHeight);
        this.lensWidth = lensWidth;
        this.lensHeigh = lensHeigh;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }
    void cleanRaster(){
        raster = new Raster(imgWidth, imgHeight);
    }

    void rasterise(Projection projection){
        //TODO: DENNA process kommer vara trådad där antalet trådar i systemet överför info från projektionen till rastret.
        //TODO: Glöm inte att invänta traådarna när resteriseringen är klar, så att det blir en "atomär" operation.
        //TODO: Använd trådpoolen i ThreadPool
    }

    Raster getRaster(){
        return raster;
    }

}
