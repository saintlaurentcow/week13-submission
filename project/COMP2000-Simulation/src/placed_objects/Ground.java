package placed_objects;
import exceptions.ObjectLimitExceededException;
import growables.Growable;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.*;
import supplementary.Window;
import java.util.stream.Stream;
import supplementary.Census;



public class Ground extends JPanel{
    static final int MAX_OBJECTS = 512;
    final ArrayList<Growable> growables;
    final ArrayList<Patch<? extends Growable>> patches;

    public static boolean parallel = true;
    private long totalNanos;
    private int tickCount;

    private volatile Census census = new Census();

    public Ground() {
        this.setPreferredSize(new Dimension(Window.WIN_WIDTH, Window.WIN_HEIGHT/4*3));
        this.setBackground(Color.green);
        this.setLayout(null);   //Freeform layout

        growables = new ArrayList<>();
        patches = new ArrayList<>();

        //Trigger event whenever a new component is added to ground
        this.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                //Notify observers
            }
        });
    }

    public void tick() {
        List<Growable> snapshot = getGrowables();
        List<Consumer<Ground>> actions = snapshot.parallelStream()
        .flatMap(g -> g.think(snapshot).stream())
        .collect(Collectors.toList());

        actions.forEach(a -> a.accept(this));

        ArrayList<Growable> dead = getDead();
        dead.forEach(i-> i.prepareForRemoval());
        dead.forEach(i -> delete(i));
        getEmptyPatches().forEach(i -> delete(i));
        repaint();
        revalidate();
    }

    public void addGrowable(Growable g) throws ObjectLimitExceededException {
        if(growables.size() >= MAX_OBJECTS) {
            throw new ObjectLimitExceededException();
        }
        growables.add(g);
        super.add((Component) g);
    }

    public void addPatch(Patch<? extends Growable> p) {
        patches.add(p);
        super.add((Component) p);
    }

    public void delete(Growable g) {
        growables.remove(g);
        remove((Component) g);
    }

    public void delete(Patch<? extends Growable> p) {
        patches.remove(p);
        remove(p);
    }

    public ArrayList<Growable> getGrowables() {
        //Create a new copy of the ArrayList to prevent concurrent modification
        return new ArrayList<>(growables);
    }

    public ArrayList<Patch<? extends Growable>> getEmptyPatches() {
        ArrayList<Patch<? extends Growable>> empty = new ArrayList<>();
        for (Patch<? extends Growable> p : patches) {
            if(p.isEmpty()) {
                empty.add(p);
            }
        }
        return empty;
    }

    public ArrayList<Growable> getDead() {
        //Filter ArrayList for dead growables
        ArrayList<Growable> list = getGrowables();
        list.removeIf(i -> !i.getState().equals("DEAD"));
        return list;
    }
}