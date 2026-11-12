package growables;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import placed_objects.Ground;


public interface Growable {
    public void spread();     //Different things spread in different ways, e.g. wind, bees, spores
    public Point getPosition();
    public String getState();
    public void increaseSpreadNum(double factor);
    public void increaseLifespan(double factor);
    public List<Consumer<Ground>> think(List<Growable> snapshot);
    public Rectangle getHitbox();
    public void fightAgainst(Growable g);
    public int getFightingPower();
    public void loseFight();
    public Growable isColliding();
    public void setFighting(boolean state);
    public boolean getFighting();
    public void prepareForRemoval();
}