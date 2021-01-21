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
        return artifacts.addAll(Arrays.asList(artifact));
    }
    public ArrayList<Artifact> getArtifacts(){
        return artifacts;
    }

    private void resolveTransformation(){
        double[][] transMatrix = getTransformationMatrix();
        for (Artifact artifact: artifacts){
            for (Vertex vertex: artifact.getVertices()){
                double[][] transformation = vecToOneDMatrix(artifact.localPointToGlobal(vertex.coordinates));
                transformation = multiply(transMatrix, transformation);
                vertex.coordinates = oneDMatrixToVec(artifact, transformation);
            }
            artifact.setBounds();
        }
    }
    private double[][] vecToOneDMatrix(double[] arr){
        double[][] matrix = new double[arr.length + 1][1];
        matrix[X][0] = arr[X];
        matrix[Y][0] = arr[Y];
        matrix[Z][0] = arr[Z];
        matrix[NUM_DIMENSIONS][0] = 1;
        return matrix;
    }
    private double[] oneDMatrixToVec(Artifact artifact, double[][] transformation){
        return artifact.globalPointToLocal(new double[] {
                transformation[X][0],
                transformation[Y][0],
                transformation[Z][0]
        });
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
        else if (viewTiltAngle + degs[X] >= 90)
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

