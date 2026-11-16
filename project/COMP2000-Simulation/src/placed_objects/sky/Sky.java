package placed_objects.sky;
import java.awt.*;
import java.time.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;
import supplementary.Window;
import supplementary.Census;

public class Sky extends JPanel implements SkySubject{
    SkyState sunnyState;
    SkyState nightState;

    SkyState state;

    public Instant startTime;
    public long dayTime;
    public int hour;    //Divide by 1000 for the hour

    private final List<SkyObserver> observers;
    private Census census = new Census();

    public Sky() {
        sunnyState = new SunnyState(this);
        nightState = new NightState(this);
        state = sunnyState;

        observers = new CopyOnWriteArrayList<>(); 

        startTime = Instant.now();
        this.setPreferredSize(new Dimension(Window.WIN_WIDTH, Window.WIN_HEIGHT/4));
    }

    public void tick() {
        //1 real-life second = 1 hour, 1 minute = ~17ms
        dayTime = Duration.between(startTime, Instant.now()).toMillis();
        hour = (int) ((dayTime % 24000));
        state.checkChange();
        revalidate();
        repaint();
    }

    public void setCensus(Census census){
        this.census = census;
    }

    public String getState() {
        return state.getName();
    }

    @Override
    public void registerObserver(SkyObserver o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(SkyObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for(SkyObserver o : observers) {
            o.update(state.getName(), hour);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        state.paintComponent(g);
        drawCensus(g);
    }

    private void drawCensus(Graphics g) {
        int x = 10;
        int y = getHeight() - 110;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(x, y, 230, 100);
        g.setColor(Color.WHITE);
        g.drawString("Plants: " + census.total(), x + 10, y + 20);
        g.drawString("Sunflowers: " + census.count("Sunflower"), x + 10, y + 38);
        g.drawString("Weeds: " + census.count("Weed"), x + 10, y + 56);
        g.drawString("Blooming: " + census.countState("BLOOMING"), x + 10, y + 74);
       
}
}