package engine;

import rendering.Renderer;
import components.Artifact;
import java.util.ArrayList;
import java.util.Arrays;

import static util.VectorUtil.*;

public class Engine {

    private static final int FRAME_RATE = 60;

    private static Engine instance;

    private Renderer renderer = new Renderer();
    private ArrayList<Artifact> artifacts = new ArrayList<>();
    private double viewTiltAngle = 0;
    private double[] translationBuffer = new double[NUM_DIMENSIONS];
    private double[] rotationBuffer = new double[NUM_DIMENSIONS];
    private double[] scalingBuffer = new double[NUM_DIMENSIONS];

    private Engine(){
        new GUI(renderer);
    }
    public static Engine instance(){
        if (instance == null)
            instance = new Engine();
        return instance;
    }

    public void run(){
        while(true){
            resolveTransformation();
            cleanTransformationBuffers();
            renderer.render(); //TODO: Threading problems here??
            try{
                Thread.sleep(1000/FRAME_RATE);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
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

    /*public void move(double[] vec){
        for (Artifact artifact: artifacts){
            artifact.translate(vec);
        }
    }*/

    /*public void rotate(double[] degs){
        degs = correctForRealisticMovement(degs);

        double[][] rotationMatrix = rotationMatrix(degs);

        for (Artifact artifact: artifacts){
            artifact.rotate(rotationMatrix);
        }
    }*/

    private void resolveTransformation(){
        
    }


    void moveCamera(double[] vec){
        translationBuffer[X] += vec[X];
        translationBuffer[Y] += vec[Y];
        translationBuffer[Z] += vec[Z];
    }
    void rotate(double[] degs){
        rotationBuffer[X] += degs[X];
        rotationBuffer[Y] += degs[Y];
        rotationBuffer[Z] += degs[Z];
    }
    private void cleanTransformationBuffers(){
        translationBuffer = new double[NUM_DIMENSIONS];
        rotationBuffer = new double[NUM_DIMENSIONS];
        scalingBuffer = new double[NUM_DIMENSIONS];
    }

    private double[] correctForRealisticMovement(double[] degs){
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
    private double[][] rotationMatrix(double[] degs){
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

