import growables.plant.flower.sunflower.Sunflower;
import growables.plant.weed.Weed;
import java.awt.Point;
import placed_objects.Patch;
import supplementary.Window;
import placed_objects.Ground;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        //Run with "seq" to use the sequential stream for timing comparison
        if (args.length > 0 && args[0].equals("seq")) {
            Ground.parallel = false;
        }

        SwingUtilities.invokeLater(() -> {
            Window window = new Window();

            Patch<Sunflower> p = new Patch<>(100, new Point(200, 200));
            window.addToGround(p);

            Sunflower sf1 = new Sunflower(new Point(200, 200), window.getSky(), window.getGround());
            window.addToGround(sf1);
            sf1.givePatch(p);

            Sunflower sf2 = new Sunflower(new Point(1000, 500), window.getSky(), window.getGround());
            Weed w = new Weed(new Point(600, 350), window.getSky(), window.getGround());

            sf2.givePatch(p);
            window.addToGround(sf2);
            window.addToGround(w);
        });
    }
}