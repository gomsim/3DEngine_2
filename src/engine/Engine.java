package engine;

import rendering.Renderer;
import components.Artifact;
import java.util.ArrayList;
import java.util.Arrays;

import static util.VectorUtil.*;

public class Engine {

    public static Engine instance;
    private Renderer renderer = new Renderer();
    private ArrayList<Artifact> artifacts = new ArrayList<>();
    private ArrayList<Artifact> luminecents = new ArrayList<>();
    public static final int FRAME_RATE = 60;
    private double viewTiltAngle = 0;

    private Engine(){
        new GUI(renderer);
    }
    public static Engine instance(){
        if (instance == null)
            instance = new Engine();
        return instance;
    }

    public void run(){
        long startTime;
        long duration;
        int i = 0;
        while(true){
            startTime = System.nanoTime();
            renderer.render(); //TODO: Threading problems here??
            try{
                Thread.sleep(1000/FRAME_RATE);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            /*duration = System.nanoTime()-startTime;
            if (++i % 60 == 0) // print current framerate to console
                System.out.println(1000000000L/duration);*/
        }
    }
    public boolean add(Artifact ... artifact){
        boolean success = artifacts.addAll(Arrays.asList(artifact));
        return success;
    }
    public ArrayList<Artifact> getArtifacts(){
        return artifacts;
    }

    //TODO: MAJOR TODO!! I need to combine the rotation matrix, each vertex's position and the translation matrix into one in order to do a combined transformation calculation!!!!!!!!!

    public void move(double[] vec){
        for (Artifact artifact: artifacts){
            artifact.translate(vec);
            //System.out.println(artifact); //FOR TESTING
        }
    }

    public void rotate(double[] degs){
        degs = correctForNaturalMovement(degs);

        double[][] rotationMatrix = genRotMatrix(degs);

        for (Artifact artifact: artifacts){
            artifact.rotate(rotationMatrix);
            //System.out.println(artifact); //FOR TESTING
        }
    }
    private double[] correctForNaturalMovement(double[] degs){
        if (viewTiltAngle + degs[X] <= -90)
            degs[X] = -90 - viewTiltAngle;
        if (viewTiltAngle + degs[X] >= 90)
            degs[X] = 90 - viewTiltAngle;
        viewTiltAngle += degs[X];

        double angle = viewTiltAngle / 90;
        degs[Z] = degs[Y]*angle;

        if (viewTiltAngle > 0) //Nedåt
            degs[Y] += -degs[Z];
        else if (viewTiltAngle < 0) //Uppåt
            degs[Y] += degs[Z];
        return degs;
    }
    private double[][] genRotMatrix(double[] degs){
        double rotX = Math.toRadians(degs[X]);
        double rotY = Math.toRadians(degs[Y]);
        double rotZ = Math.toRadians(degs[Z]);

        double sinX = Math.sin(rotX);
        double cosX = Math.cos(rotX);
        double sinY = Math.sin(rotY);
        double cosY = Math.cos(rotY);
        double sinZ = Math.sin(rotZ);
        double cosZ = Math.cos(rotZ);

        return new double[][]{
                {cosZ * cosY, cosZ * sinY * sinX - sinZ * cosX, cosZ * sinY * cosX + sinZ * sinX},
                {sinZ * cosY, sinZ * sinY * sinX + cosZ * cosX, sinZ * sinY * cosX - cosZ * sinX},
                {-sinY, cosY * sinX, cosY * cosX}
        };
    }
}

