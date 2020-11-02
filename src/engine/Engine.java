package engine;

import components.Vertex;
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

    private void resolveTransformation(){ //TODO: This fucking works!!!! Only for some reason translation doeasn't.
        //TODO: Focking clean up these messy methods...
        double[][] transMatrix = getTransformationMatrix();
        for (Artifact artifact: artifacts){
            for (Vertex vertex: artifact.getVertices()){
                double[][] transformation = toMatrix(vertex.coordinates);
                transformation = multiply(transMatrix, transformation);
                tranferCoordinates(vertex.coordinates, transformation);
            }
            artifact.rotateWithoutNestling(transMatrix);
        }
    }
    private double[][] toMatrix(double[] arr){ //TODO: Clean this shit up
        double[][] matrix = new double[arr.length + 1][1];
        matrix[X][0] = arr[X];
        matrix[Y][0] = arr[Y];
        matrix[Z][0] = arr[Z];
        return matrix;
    }
    private void tranferCoordinates(double[] target, double[][] transformation){ //TODO: Clean this shit up
        target[X] = transformation[X][0];
        target[Y] = transformation[Y][0];
        target[Z] = transformation[Z][0];
    }


    void moveCamera(double[] vec){
        translationBuffer[X] += vec[X];
        translationBuffer[Y] += vec[Y];
        translationBuffer[Z] += vec[Z];
    }
    void rotateCamera(double[] degs){
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
    private double[][] getTransformationMatrix(){ //TODO: Leaves scaling out at the moment everywhere.
        double[][] matrix = new double[NUM_DIMENSIONS + 1][NUM_DIMENSIONS + 1];
        double[][] rotationMatrix = rotationMatrix(correctForRealisticMovement(rotationBuffer));
        for (int y = 0; y < rotationMatrix.length; y++){
            for (int x = 0; x < rotationMatrix[0].length; x++){
                matrix[y][x] = rotationMatrix[y][x];
            }
        }
        matrix[NUM_DIMENSIONS][X] = 0;
        matrix[NUM_DIMENSIONS][Y] = 0;
        matrix[NUM_DIMENSIONS][Z] = 0;
        matrix[NUM_DIMENSIONS][Z + 1] = 1;
        matrix[X][NUM_DIMENSIONS] = translationBuffer[X];
        matrix[Y][NUM_DIMENSIONS] = translationBuffer[Y];
        matrix[Z][NUM_DIMENSIONS] = translationBuffer[Z];
        return matrix;
    }
}

