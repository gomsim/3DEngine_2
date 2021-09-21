package rendering;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Renderer extends JPanel {

    static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width; //TODO: Måste anpassas efter skalningen i Windows
    static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height; //TODO: Måste anpassas efter skalningen i Windows

    private Camera camera = new Camera();
    private BufferedImage img = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);


    public void render(){
        Raster raster = camera.capture();
        transferBufferToImage(raster.colorBuffer);
        repaint();
    }

    private void transferBufferToImage(int[] buffer){
        int[] pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < pixels.length; i++){
            pixels[i] = buffer[i];
        }
    }

    @Override
    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        graphics.drawImage(img,0,0, SCREEN_WIDTH, SCREEN_HEIGHT,this);
    }
}
