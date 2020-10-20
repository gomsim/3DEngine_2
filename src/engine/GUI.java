package engine;

import rendering.Renderer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;

import static util.VectorUtil.*;

public class GUI extends JFrame {

    //TODO: Move out to config file
    private static final double MOVEMENT_SPEED = 65;
    private static final double TURNING_SPEED = 1.7;

    private final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    public GUI(Renderer renderer){
        //super(getScreenConfiguration());
        setUpWindow();

        addKeyListener(new MoveListener());
        addMouseMotionListener(new MouseTurningListener());
        //setOpacity(0.3f);//FOR TESTING

        renderer.setBounds(getX(),getY(),getWidth(),getHeight());
        add(renderer);

        setCursor(getToolkit().createCustomCursor(null, new Point(), "transparent"));

        setVisible(true);
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

    private static GraphicsConfiguration getScreenConfiguration(){
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
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
                Engine.instance().rotate(new double[] {deltaY * TURNING_SPEED,-deltaX * TURNING_SPEED,0});
                robot.mouseMove(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
                blockMouseEvents = true;
            }
        }
    }

    private class MoveListener extends KeyAdapter {

        private HashSet<Integer> pressedKeys = new HashSet<>();

        public void keyPressed(KeyEvent event){
            double[] offset = new double[NUM_DIMENSIONS];
            pressedKeys.add(event.getKeyCode());
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
                        offset[Y] += -MOVEMENT_SPEED*5;
                        break;
                }
            }
            Engine.instance().move(offset);
        }
        public void keyReleased(KeyEvent event){
            pressedKeys.remove(event.getKeyCode());
        }
    }
}

