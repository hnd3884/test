package sun.java2d.marlin.stats;

public final class Histogram extends StatLong
{
    static final int BUCKET = 2;
    static final int MAX = 20;
    static final int LAST = 19;
    static final int[] STEPS;
    private final StatLong[] stats;
    
    static int bucket(final int n) {
        for (int i = 1; i < 20; ++i) {
            if (n < Histogram.STEPS[i]) {
                return i - 1;
            }
        }
        return 19;
    }
    
    public Histogram(final String s) {
        super(s);
        this.stats = new StatLong[20];
        for (int i = 0; i < 20; ++i) {
            this.stats[i] = new StatLong(String.format("%5s .. %5s", Histogram.STEPS[i], (i + 1 < 20) ? Integer.valueOf(Histogram.STEPS[i + 1]) : "~"));
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        for (int i = 0; i < 20; ++i) {
            this.stats[i].reset();
        }
    }
    
    @Override
    public void add(final int n) {
        super.add(n);
        this.stats[bucket(n)].add(n);
    }
    
    @Override
    public void add(final long n) {
        this.add((int)n);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(2048);
        super.toString(sb).append(" { ");
        for (int i = 0; i < 20; ++i) {
            if (this.stats[i].count != 0L) {
                sb.append("\n        ").append(this.stats[i].toString());
            }
        }
        return sb.append(" }").toString();
    }
    
    static {
        (STEPS = new int[20])[0] = 0;
        Histogram.STEPS[1] = 1;
        for (int i = 2; i < 20; ++i) {
            Histogram.STEPS[i] = Histogram.STEPS[i - 1] * 2;
        }
    }
}
