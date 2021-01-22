package engine;

import rendering.Renderer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static util.VectorUtil.*;

public class GUI extends JFrame {

    //TODO: Move out to config file
    private static final double MOVEMENT_SPEED = 65;
    private static final double TURNING_SPEED = 1.7;

    private final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    private final MovementProcessor movementProcessor = new MovementProcessor();

    public GUI(Renderer renderer){
        setUpWindow();

        addKeyListener(new MoveListener());
        addMouseMotionListener(new MouseTurningListener());

        renderer.setBounds(getX(),getY(),getWidth(),getHeight());
        add(renderer);

        Toolkit toolkit = getToolkit();
        setCursor(toolkit.createCustomCursor(toolkit.getImage(""), new Point(), "transparent"));

        setVisible(true);

        new Thread(movementProcessor).start();
    }

    private void setUpWindow(){
        setFocusable(true);
        setTitle("3D Engine 2.0");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setLayout(null);
        requestFocus();
    }

    private class MouseTurningListener extends MouseMotionAdapter{
        private Robot robot;
        private boolean blockMouseEvents;


        MouseTurningListener(){
            try{
                robot = new Robot();
            }catch(AWTException e){
                e.printStackTrace();
            }
            robot.mouseMove(SCREEN_WIDTH/2,SCREEN_HEIGHT/2);
        }

        public void mouseMoved(MouseEvent event){
            if (blockMouseEvents && event.getX() == 0 && event.getY() == 0) {
                blockMouseEvents = false;
            }else{
                double deltaX = ((double)(event.getX() - SCREEN_WIDTH/2)) / 10;
                double deltaY = ((double)(event.getY() - SCREEN_HEIGHT/2)) / 10;
                Engine.instance().rotateCamera(new double[] {deltaY * TURNING_SPEED,-deltaX * TURNING_SPEED,0});
                robot.mouseMove(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
                blockMouseEvents = true;
            }
        }
    }

    private class MoveListener extends KeyAdapter {
        public void keyPressed(KeyEvent event){
            movementProcessor.toAdd.add(event.getKeyCode());
        }
        public void keyReleased(KeyEvent event){
            movementProcessor.toRemove.add(event.getKeyCode());
        }
    }

    private static class MovementProcessor implements Runnable{
        private final BlockingQueue<Integer> toAdd = new ArrayBlockingQueue<>(15);
        private final BlockingQueue<Integer> toRemove = new ArrayBlockingQueue<>(15);
        private final HashSet<Integer> pressedKeys = new HashSet<>();

        public void run(){
            while(true){
                double[] offset = new double[NUM_DIMENSIONS];
                toAdd.drainTo(pressedKeys);

                java.util.List<Integer> temp = new ArrayList<>();
                toRemove.drainTo(temp);
                pressedKeys.removeAll(temp);

                for (Integer keyPress: pressedKeys) {
                    switch (keyPress) {
                        case KeyEvent.VK_A:
                            offset[X] += MOVEMENT_SPEED;
                            break;
                        case KeyEvent.VK_D:
                            offset[X] += -MOVEMENT_SPEED;
                            break;
                        case KeyEvent.VK_W:
                            offset[Z] += -MOVEMENT_SPEED;
                            break;
                        case KeyEvent.VK_S:
                            offset[Z] += MOVEMENT_SPEED;
                            break;
                        case KeyEvent.VK_SPACE:
                            offset[Y] += MOVEMENT_SPEED;
                            break;
                        case KeyEvent.VK_CONTROL:
                            offset[Y] += -MOVEMENT_SPEED;
                            break;
                        case KeyEvent.VK_ESCAPE:
                            System.exit(0);
                    }
                }
                Engine.instance().moveCamera(offset);
                try{
                    Thread.sleep(10);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}

