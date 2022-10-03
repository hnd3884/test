package javax.print.attribute;

import java.io.Serializable;

public abstract class ResolutionSyntax implements Serializable, Cloneable
{
    private static final long serialVersionUID = 2706743076526672017L;
    private int crossFeedResolution;
    private int feedResolution;
    public static final int DPI = 100;
    public static final int DPCM = 254;
    
    public ResolutionSyntax(final int n, final int n2, final int n3) {
        if (n < 1) {
            throw new IllegalArgumentException("crossFeedResolution is < 1");
        }
        if (n2 < 1) {
            throw new IllegalArgumentException("feedResolution is < 1");
        }
        if (n3 < 1) {
            throw new IllegalArgumentException("units is < 1");
        }
        this.crossFeedResolution = n * n3;
        this.feedResolution = n2 * n3;
    }
    
    private static int convertFromDphi(final int n, final int n2) {
        if (n2 < 1) {
            throw new IllegalArgumentException(": units is < 1");
        }
        return (n + n2 / 2) / n2;
    }
    
    public int[] getResolution(final int n) {
        return new int[] { this.getCrossFeedResolution(n), this.getFeedResolution(n) };
    }
    
    public int getCrossFeedResolution(final int n) {
        return convertFromDphi(this.crossFeedResolution, n);
    }
    
    public int getFeedResolution(final int n) {
        return convertFromDphi(this.feedResolution, n);
    }
    
    public String toString(final int n, final String s) {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getCrossFeedResolution(n));
        sb.append('x');
        sb.append(this.getFeedResolution(n));
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }
        return sb.toString();
    }
    
    public boolean lessThanOrEquals(final ResolutionSyntax resolutionSyntax) {
        return this.crossFeedResolution <= resolutionSyntax.crossFeedResolution && this.feedResolution <= resolutionSyntax.feedResolution;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof ResolutionSyntax && this.crossFeedResolution == ((ResolutionSyntax)o).crossFeedResolution && this.feedResolution == ((ResolutionSyntax)o).feedResolution;
    }
    
    @Override
    public int hashCode() {
        return (this.crossFeedResolution & 0xFFFF) | (this.feedResolution & 0xFFFF) << 16;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.crossFeedResolution);
        sb.append('x');
        sb.append(this.feedResolution);
        sb.append(" dphi");
        return sb.toString();
    }
    
    protected int getCrossFeedResolutionDphi() {
        return this.crossFeedResolution;
    }
    
    protected int getFeedResolutionDphi() {
        return this.feedResolution;
    }
}
