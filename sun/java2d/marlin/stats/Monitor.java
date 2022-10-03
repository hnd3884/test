package sun.java2d.marlin.stats;

public final class Monitor extends StatLong
{
    private static final long INVALID = -1L;
    private long start;
    
    public Monitor(final String s) {
        super(s);
        this.start = -1L;
    }
    
    public void start() {
        this.start = System.nanoTime();
    }
    
    public void stop() {
        final long n = System.nanoTime() - this.start;
        if (this.start != -1L && n > 0L) {
            this.add(n);
        }
        this.start = -1L;
    }
}
