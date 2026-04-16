package pl.lodz.p;

public enum Enum {
    LOW(0.1, "Mały (10%)"),
    MEDIUM(0.3, "Średni (30%)"),
    HIGH(0.5, "Duży (50%)");

    private final double probability;
    private final String label;

    Enum(double probability, String label) {
        this.probability = probability;
        this.label = label;
    }

    public double getProbability() {
        return probability;
    }

    @Override
    public String toString() {
        return label;
    }
}
