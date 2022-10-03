package java.awt;

public class BufferCapabilities implements Cloneable
{
    private ImageCapabilities frontCaps;
    private ImageCapabilities backCaps;
    private FlipContents flipContents;
    
    public BufferCapabilities(final ImageCapabilities frontCaps, final ImageCapabilities backCaps, final FlipContents flipContents) {
        if (frontCaps == null || backCaps == null) {
            throw new IllegalArgumentException("Image capabilities specified cannot be null");
        }
        this.frontCaps = frontCaps;
        this.backCaps = backCaps;
        this.flipContents = flipContents;
    }
    
    public ImageCapabilities getFrontBufferCapabilities() {
        return this.frontCaps;
    }
    
    public ImageCapabilities getBackBufferCapabilities() {
        return this.backCaps;
    }
    
    public boolean isPageFlipping() {
        return this.getFlipContents() != null;
    }
    
    public FlipContents getFlipContents() {
        return this.flipContents;
    }
    
    public boolean isFullScreenRequired() {
        return false;
    }
    
    public boolean isMultiBufferAvailable() {
        return false;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    public static final class FlipContents extends AttributeValue
    {
        private static int I_UNDEFINED;
        private static int I_BACKGROUND;
        private static int I_PRIOR;
        private static int I_COPIED;
        private static final String[] NAMES;
        public static final FlipContents UNDEFINED;
        public static final FlipContents BACKGROUND;
        public static final FlipContents PRIOR;
        public static final FlipContents COPIED;
        
        private FlipContents(final int n) {
            super(n, FlipContents.NAMES);
        }
        
        static {
            FlipContents.I_UNDEFINED = 0;
            FlipContents.I_BACKGROUND = 1;
            FlipContents.I_PRIOR = 2;
            FlipContents.I_COPIED = 3;
            NAMES = new String[] { "undefined", "background", "prior", "copied" };
            UNDEFINED = new FlipContents(FlipContents.I_UNDEFINED);
            BACKGROUND = new FlipContents(FlipContents.I_BACKGROUND);
            PRIOR = new FlipContents(FlipContents.I_PRIOR);
            COPIED = new FlipContents(FlipContents.I_COPIED);
        }
    }
}
