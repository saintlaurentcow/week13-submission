package supplementary;

import growables.Growable;
import java.awt.*;
import javax.swing.*;
import placed_objects.Ground;
import placed_objects.Patch;
import placed_objects.sky.Sky;

public class Window extends JFrame{
    //Window of the application
    //Defines screenspace

    public static int WIN_WIDTH = 1200;
    public static int WIN_HEIGHT = 900;
    private final Sky sky;
    private final Ground ground;

    Timer timer;

    public Window() {
        sky = new Sky();
        ground = new Ground();

        //Basic window props
        this.setTitle("GAASK Plant Simulation COMP2000");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //Exit application when x pressed
        this.setResizable(false);
        this.setLayout(new GridBagLayout());    //Main window only contains sky and ground

        //Keep the main sky and ground in a single column
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth=GridBagConstraints.REMAINDER;
        c.fill=GridBagConstraints.BOTH;

        c.weightx = 1.0;
        c.weighty = 1.0;

        this.add(sky,c);
        this.add(ground,c);

        this.pack();
        this.setLocationRelativeTo(null);   //Centre after pack so the size is known
        this.setVisible(true);

        //Swing timer runs on the event dispatch thread, so it is safe to touch components
        timer = new Timer(20, e -> {
            ground.tick();
            sky.setCensus(ground.getCensus());
            sky.tick();
        });
        timer.start();
    }

    //Sky will deal with it's own components, so anything added to the window
    //must be added to the ground.

    public Sky getSky() {
        return sky;
    }

    public Ground getGround() {
        return ground;
    }

    public void addToGround(Growable g) {
        ground.addGrowable(g);
    }

    public void addToGround(Patch<?> p) {
        ground.addPatch(p);
    }

    public void removeFromGround(Component comp) {
        ground.remove(comp);
    }

    public void refresh() {
        ground.revalidate();
        ground.repaint();
    }
}