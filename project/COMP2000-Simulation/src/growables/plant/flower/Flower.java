package growables.plant.flower;

import growables.plant.*;
import java.awt.*;
import placed_objects.Ground;
import placed_objects.sky.Sky;

public abstract class Flower extends Plant {
    public PlantState bloomState;
    public int bloomTime = 6;

    public Flower(Point p, int size, Sky sky, Ground ground) {
        super(p, size, sky, ground);
        bloomState = new LambdaState("BLOOMING",
    g -> setBackground(Color.RED),
    life -> {
        if (life > growthDelay * 4) state = deadState;
        bloom();
    });
    }

    public void bloom(){    //Display the flower blooming
        this.setBackground(Color.RED);
        if ((int) (Math.random() * 100) == 0) {
            spread();
        }
    }
}