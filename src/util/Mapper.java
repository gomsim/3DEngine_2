package util;

public class Mapper{

    private double ratio;
    private double fromStart, toStart;

    public Mapper(double fromStart, double fromEnd, double toStart, double toEnd){
        this.fromStart = fromStart;
        this.toStart = toStart;
        // + 1 to include last indices
        ratio = (fromEnd - fromStart + 1) / (toEnd - toStart + 1);
    }
    public Mapper(double from, double to){
        this(0,from,0,to);
    }

    public double mapDouble(double value){
        return ((value - fromStart) / ratio) + toStart;
    }

    public int mapInt(double value){
        return (int)mapDouble(value);
    }
}