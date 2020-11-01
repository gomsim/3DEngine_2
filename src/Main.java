import components.Artifact;
import components.Vertex;
import components.Polygon;
import engine.Engine;

import static util.VectorUtil.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {

    public static void main(String[] args){
        /*Projection projection = new Projection(
                new Vertex(2,4,0),
                new Vertex(2,0,1),
                new Vertex(0,4,1)
        );


        double[] result = Rasteriser.pixelDepth(projection,1,3);
        System.out.println("[" + result[X] + "," + result[Y] + "," + result[Z] + "]");*/


        Engine engine = Engine.instance();

        int size = 500;
        int wallLength = 2000;
        int bigSize = size * 5;
        int yellowPos = 200;

        //Triangles behind each other
        /*engine.add(
                new Artifact(
                -size/2,
                -size/2,
                size*1,
                Color.RED,
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size/2,0,0),
                        new Vertex(size,size,0))
        ),      new Artifact(
                -size/2,
                -size/2,
                size*2,
                Color.YELLOW,
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size/2,0,0),
                        new Vertex(size,size,0))
        ),      new Artifact(
                size,
                -size/2,
                size*1,
                Color.GREEN,
                new Polygon(
                        new Vertex(0,size,wallLength),
                        new Vertex(0,0,0),
                        new Vertex(0,size,0)),
                new Polygon(
                        new Vertex(0,0,wallLength),
                        new Vertex(0,0,0),
                        new Vertex(0,size,wallLength))
        ));*/

        //Intersecting triangles
        engine.add(
                new Artifact(
                        -size/2,
                        -size/2,
                        500,
                        Color.RED,
                        /*new Polygon(
                                new Vertex(0,size,0),
                                new Vertex(size/2,0,size),
                                new Vertex(size,size,0)),*/
                        new Polygon(
                                new Vertex(0,size,yellowPos ),
                                new Vertex(size/2,0,yellowPos ),
                                new Vertex(size,size,yellowPos ))
                ));

        //Triangles side by side
        /*engine.add(new Artifact(
                -size/2,
                -size/2,ssssssssssssssssssssssssssssssssssssssssssss
                500,
                Color.RED,
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size/2,0,0),
                        new Vertex(size,size,0))
        ),new Artifact(
                size/2,
                -size/2,
                400,
                Color.YELLOW,
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size/2,0,0),
                        new Vertex(size,size,0))
        ));*/

        //Intersecting triangles
        /*engine.add(new Artifact(
                -bigSize/2,
                -bigSize/2,
                500,
                Color.RED,
                new Polygon(
                        new Vertex(0,bigSize,0),
                        new Vertex(bigSize/2,0,bigSize),
                        new Vertex(bigSize,bigSize,0))
        ),new Artifact(
                -bigSize/2,
                -bigSize/2,
                600,
                Color.YELLOW,
                new Polygon(
                        new Vertex(0,bigSize,bigSize),
                        new Vertex(bigSize/2,0,0),
                        new Vertex(bigSize,bigSize,bigSize))
        ));*/

        //Tetrahedron
        /*engine.add(new Artifact(
                -size/2,
                -size/2,
                1000,
                Color.RED,
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size/2,0,size/2),
                        new Vertex(size,size,0)),
                new Polygon(
                        new Vertex(size,size,0),
                        new Vertex(size/2,0,size/2),
                        new Vertex(size/2,size,size)),
                new Polygon(
                        new Vertex(size/2,size,size),
                        new Vertex(size/2,0,size/2),
                        new Vertex(0,size,0)),
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size,size,0),
                        new Vertex(size/2,size,size))
        ));*/

        //Large Tetrahedron
        /*Artifact largeTetrahedron = new Artifact(
                -bigSize/2,
                -bigSize/2,
                -bigSize/2,
                Color.RED,
                new Polygon(
                        new Vertex(0,bigSize,0),
                        new Vertex(bigSize/2,0,bigSize/2),
                        new Vertex(bigSize,bigSize,0)),
                new Polygon(
                        new Vertex(bigSize,bigSize,0),
                        new Vertex(bigSize/2,0,bigSize/2),
                        new Vertex(bigSize/2,bigSize,bigSize)),
                new Polygon(
                        new Vertex(bigSize/2,bigSize,bigSize),
                        new Vertex(bigSize/2,0,bigSize/2),
                        new Vertex(0,bigSize,0)),
                new Polygon(
                        new Vertex(0,bigSize,0),
                        new Vertex(bigSize,bigSize,0),
                        new Vertex(bigSize/2,bigSize,bigSize))
        );
        engine.add(largeTetrahedron);*/

        int cielingHeight = size*2;
        //Large Room
        /*engine.add(new Artifact(
                -bigSize/2,
                -cielingHeight + size/2,
                -size/2,
                Color.ORANGE,
                //FRONT
                new Polygon(
                        new Vertex(0,0,0),
                        new Vertex(bigSize,0,0),
                        new Vertex(0,cielingHeight,0)),
                new Polygon(
                        new Vertex(bigSize,0,0),
                        new Vertex(0,cielingHeight,0),
                        new Vertex(bigSize,cielingHeight,0)),
                //BACK
                new Polygon(
                        new Vertex(0,0,bigSize),
                        new Vertex(bigSize,0,bigSize),
                        new Vertex(0,cielingHeight,bigSize)),
                new Polygon(
                        new Vertex(bigSize,0,bigSize),
                        new Vertex(0,cielingHeight,bigSize),
                        new Vertex(bigSize,cielingHeight,bigSize)),
                //LEFT
                new Polygon(
                        new Vertex(0,0,0),
                        new Vertex(0,cielingHeight,0),
                        new Vertex(0,cielingHeight,bigSize)),
                new Polygon(
                        new Vertex(0,0,0),
                        new Vertex(0,cielingHeight,bigSize),
                        new Vertex(0,0,bigSize)),
                //RIGHT
                new Polygon(
                        new Vertex(bigSize,0,0),
                        new Vertex(bigSize,cielingHeight,0),
                        new Vertex(bigSize,cielingHeight,bigSize)),
                new Polygon(
                        new Vertex(bigSize,0,0),
                        new Vertex(bigSize,cielingHeight,bigSize),
                        new Vertex(bigSize,0,bigSize)),
                //UP
                new Polygon(
                        new Vertex(0,0,0),
                        new Vertex(bigSize,0,0),
                        new Vertex(0,0,bigSize)),
                new Polygon(
                        new Vertex(bigSize,0,0),
                        new Vertex(bigSize,0,bigSize),
                        new Vertex(0,0,bigSize)),
                //DOWN
                new Polygon(
                        new Vertex(0,cielingHeight,0),
                        new Vertex(bigSize,cielingHeight,0),
                        new Vertex(0,cielingHeight,bigSize)),
                new Polygon(
                        new Vertex(bigSize,cielingHeight,0),
                        new Vertex(bigSize,cielingHeight,bigSize),
                        new Vertex(0,cielingHeight,bigSize))
        ));*/

        //LARGE CUBE ROOM
        /*Artifact largeCubeRoom = new Artifact(
                -bigSize/2,
                -bigSize + size/2,
                -size/2,
                Color.ORANGE,
                //FRONT
                new Polygon(
                        new Vertex(0,0,0),
                        new Vertex(bigSize,0,0),
                        new Vertex(0,bigSize,0)),
                new Polygon(
                        new Vertex(bigSize,0,0),
                        new Vertex(0,bigSize,0),
                        new Vertex(bigSize,bigSize,0)),
                //BACK
                new Polygon(
                        new Vertex(0,0,bigSize),
                        new Vertex(bigSize,0,bigSize),
                        new Vertex(0,bigSize,bigSize)),
                new Polygon(
                        new Vertex(bigSize,0,bigSize),
                        new Vertex(0,bigSize,bigSize),
                        new Vertex(bigSize,bigSize,bigSize)),
                //LEFT
                new Polygon(
                        new Vertex(0,0,0),
                        new Vertex(0,bigSize,0),
                        new Vertex(0,bigSize,bigSize)),
                new Polygon(
                        new Vertex(0,0,0),
                        new Vertex(0,bigSize,bigSize),
                        new Vertex(0,0,bigSize)),
                //RIGHT
                new Polygon(
                        new Vertex(bigSize,0,0),
                        new Vertex(bigSize,bigSize,0),
                        new Vertex(bigSize,bigSize,bigSize)),
                new Polygon(
                        new Vertex(bigSize,0,0),
                        new Vertex(bigSize,bigSize,bigSize),
                        new Vertex(bigSize,0,bigSize)),
                //UP
                new Polygon(
                        new Vertex(0,0,0),
                        new Vertex(bigSize,0,0),
                        new Vertex(0,0,bigSize)),
                new Polygon(
                        new Vertex(bigSize,0,0),
                        new Vertex(bigSize,0,bigSize),
                        new Vertex(0,0,bigSize)),
                //DOWN
                new Polygon(
                        new Vertex(0,bigSize,0),
                        new Vertex(bigSize,bigSize,0),
                        new Vertex(0,bigSize,bigSize)),
                new Polygon(
                        new Vertex(bigSize,bigSize,0),
                        new Vertex(bigSize,bigSize,bigSize),
                        new Vertex(0,bigSize,bigSize))
        );
        engine.add(largeCubeRoom);*/

        //LIGHT BULB
        /*size = 10;
        Artifact lightBulb = new Artifact(
                500,
                -500,
                500,
                Color.WHITE,
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size/2,0,size/2),
                        new Vertex(size,size,0)),
                new Polygon(
                        new Vertex(size,size,0),
                        new Vertex(size/2,0,size/2),
                        new Vertex(size/2,size,size)),
                new Polygon(
                        new Vertex(size/2,size,size),
                        new Vertex(size/2,0,size/2),
                        new Vertex(0,size,0)),
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size,size,0),
                        new Vertex(size/2,size,size))
        );
        lightBulb.setLuminescence(600);
        engine.add(lightBulb);

        Artifact greenlightBulb = new Artifact(
                -500,
                -500,
                500,
                Color.GREEN,
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size/2,0,size/2),
                        new Vertex(size,size,0)),
                new Polygon(
                        new Vertex(size,size,0),
                        new Vertex(size/2,0,size/2),
                        new Vertex(size/2,size,size)),
                new Polygon(
                        new Vertex(size/2,size,size),
                        new Vertex(size/2,0,size/2),
                        new Vertex(0,size,0)),
                new Polygon(
                        new Vertex(0,size,0),
                        new Vertex(size,size,0),
                        new Vertex(size/2,size,size))
        );
        greenlightBulb.setLuminescence(600);
        engine.add(greenlightBulb);*/

        //FLOOR
        /*int floorSize = 5000;
        ArrayList<Polygon> lines = new ArrayList<>();
        for (int x = 0; x < 4000; x+=100){
                    lines.add(new Polygon(
                            new Vertex(x+0,0,0),
                            new Vertex(x+0,0,floorSize),
                            new Vertex(x+3,0,0)));
            lines.add(new Polygon(
                            new Vertex(x+0,0,0),
                            new Vertex(x+0,0,floorSize),
                            new Vertex(x+3,0,floorSize)));
            lines.add(new Polygon(
                            new Vertex(0,0,3+x),
                            new Vertex(floorSize,0,0+x),
                            new Vertex(0,0,0+x)));
            lines.add(new Polygon(
                            new Vertex(0,0,3+x),
                            new Vertex(floorSize,0,3+x),
                            new Vertex(floorSize,0,0+x)));
        }
        Artifact floor = new Artifact(
                -2000,
                size/2,
                -size*3,
                Color.PINK,
                lines.toArray(new Polygon[lines.size()]));
        engine.add(floor);*/

        engine.run();
    }
}
