package java.awt;

public final class DisplayMode
{
    private Dimension size;
    private int bitDepth;
    private int refreshRate;
    public static final int BIT_DEPTH_MULTI = -1;
    public static final int REFRESH_RATE_UNKNOWN = 0;
    
    public DisplayMode(final int n, final int n2, final int bitDepth, final int refreshRate) {
        this.size = new Dimension(n, n2);
        this.bitDepth = bitDepth;
        this.refreshRate = refreshRate;
    }
    
    public int getHeight() {
        return this.size.height;
    }
    
    public int getWidth() {
        return this.size.width;
    }
    
    public int getBitDepth() {
        return this.bitDepth;
    }
    
    public int getRefreshRate() {
        return this.refreshRate;
    }
    
    public boolean equals(final DisplayMode displayMode) {
        return displayMode != null && this.getHeight() == displayMode.getHeight() && this.getWidth() == displayMode.getWidth() && this.getBitDepth() == displayMode.getBitDepth() && this.getRefreshRate() == displayMode.getRefreshRate();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DisplayMode && this.equals((DisplayMode)o);
    }
    
    @Override
    public int hashCode() {
        return this.getWidth() + this.getHeight() + this.getBitDepth() * 7 + this.getRefreshRate() * 13;
    }
}
