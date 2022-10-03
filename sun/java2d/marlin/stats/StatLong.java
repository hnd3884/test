package sun.java2d.marlin.stats;

public class StatLong
{
    public final String name;
    public long count;
    public long sum;
    public long min;
    public long max;
    
    public StatLong(final String name) {
        this.count = 0L;
        this.sum = 0L;
        this.min = 2147483647L;
        this.max = -2147483648L;
        this.name = name;
    }
    
    public void reset() {
        this.count = 0L;
        this.sum = 0L;
        this.min = 2147483647L;
        this.max = -2147483648L;
    }
    
    public void add(final int n) {
        ++this.count;
        this.sum += n;
        if (n < this.min) {
            this.min = n;
        }
        if (n > this.max) {
            this.max = n;
        }
    }
    
    public void add(final long n) {
        ++this.count;
        this.sum += n;
        if (n < this.min) {
            this.min = n;
        }
        if (n > this.max) {
            this.max = n;
        }
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(128)).toString();
    }
    
    public final StringBuilder toString(final StringBuilder sb) {
        sb.append(this.name).append('[').append(this.count);
        sb.append("] sum: ").append(this.sum).append(" avg: ");
        sb.append(trimTo3Digits(this.sum / (double)this.count));
        sb.append(" [").append(this.min).append(" | ").append(this.max).append("]");
        return sb;
    }
    
    public static double trimTo3Digits(final double n) {
        return (long)(1000.0 * n) / 1000.0;
    }
}
