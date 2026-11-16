package placed_objects;
import exceptions.ObjectLimitExceededException;
import growables.Growable;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;
import supplementary.Census;
import supplementary.Window;

public class Ground extends JPanel{
    static final int MAX_OBJECTS = 512;
    final ArrayList<Growable> growables;
    final ArrayList<Patch<? extends Growable>> patches;

    public static boolean parallel = true;   //Set to false to run the sequential version for timing
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
        long start = System.nanoTime();

        //Phase 1: every plant decides what it wants to do (parallel unless switched off)
        List<Growable> snapshot = getGrowables();
        Stream<Growable> stream = parallel ? snapshot.parallelStream() : snapshot.stream();
        List<Consumer<Ground>> actions = stream
                .flatMap(g -> g.think(snapshot).stream())
                .collect(Collectors.toList());

        totalNanos += System.nanoTime() - start;
        tickCount++;
        if (tickCount % 200 == 0) {
            System.out.printf("%s, %d plants: %.3f ms per tick%n",
                    parallel ? "parallel" : "sequential", snapshot.size(), totalNanos / 200 / 1e6);
            totalNanos = 0;
        }

        //Phase 2 (one thread): apply the changes to the world
        actions.forEach(a -> a.accept(this));

        //Phase 3: clean up the dead
        ArrayList<Growable> dead = getDead();
        dead.forEach(i -> i.prepareForRemoval());
        dead.forEach(i -> delete(i));
        getEmptyPatches().forEach(i -> delete(i));

        //Count everything with the custom collector for the on-screen stats
        census = getGrowables().parallelStream().collect(Census.collector());

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

    public Census getCensus() {
        return census;
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