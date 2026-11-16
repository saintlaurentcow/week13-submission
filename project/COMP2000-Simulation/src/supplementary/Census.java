package supplementary;

import growables.Growable;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;

public class Census {
    private final Map<String, Long> bySpecies = new TreeMap<>();
    private final Map<String, Long> byState = new TreeMap<>();
    private long total;
    private long powerSum;

    public void add(Growable g) {
        bySpecies.merge(g.getClass().getSimpleName(), 1L, Long::sum);
        byState.merge(g.getState(), 1L, Long::sum);
        total++;
        powerSum += g.getFightingPower();
    }

    public Census merge(Census other) {
        other.bySpecies.forEach((k, v) -> bySpecies.merge(k, v, Long::sum));
        other.byState.forEach((k, v) -> byState.merge(k, v, Long::sum));
        total += other.total;
        powerSum += other.powerSum;
        return this;
    }

    public static Collector<Growable, Census, Census> collector() {
        return Collector.of(Census::new, Census::add, Census::merge);
    }

    public long count(String species) {
        return bySpecies.getOrDefault(species, 0L);
    }

    public long countState(String state) {
        return byState.getOrDefault(state, 0L);
    }

    public long total() {
        return total;
    }

    public double averagePower() {
        return total == 0 ? 0 : (double) powerSum / total;
    }
}
