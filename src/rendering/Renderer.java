package rendering;

import javax.swing.*;
import java.awt.*;

public class Renderer extends JPanel {

    static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    private Camera camera = new Camera();

    public void render(){
        //Låt kameran fånga ett raster av scenen
        //Överför rastret till bildbufferten och rendera den i paintComponent
    }
}
