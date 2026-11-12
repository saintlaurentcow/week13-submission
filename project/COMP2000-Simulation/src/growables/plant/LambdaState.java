package growables.plant;

import java.awt.Graphics;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class LambdaState implements PlantState {
    private final String name;
    private final Consumer<Graphics> painter;
    private final LongConsumer transition;

    public LambdaState(String name, Consumer<Graphics> painter, LongConsumer transition){
        this.name = name;
        this.painter = painter;
        this.transition = transition;
    }
    @Override 
    public void paintComponent(Graphics g){
        painter.accept(g);
    }

    @Override 
    public void checkChange(long lifespan){
        transition.accept(lifespan);
    }

    @Override 
    public String getName(){
        return name;
    }
    
}
