package javax.swing.plaf.synth;

public class ColorType
{
    public static final ColorType FOREGROUND;
    public static final ColorType BACKGROUND;
    public static final ColorType TEXT_FOREGROUND;
    public static final ColorType TEXT_BACKGROUND;
    public static final ColorType FOCUS;
    public static final int MAX_COUNT;
    private static int nextID;
    private String description;
    private int index;
    
    protected ColorType(final String description) {
        if (description == null) {
            throw new NullPointerException("ColorType must have a valid description");
        }
        this.description = description;
        synchronized (ColorType.class) {
            this.index = ColorType.nextID++;
        }
    }
    
    public final int getID() {
        return this.index;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
    
    static {
        FOREGROUND = new ColorType("Foreground");
        BACKGROUND = new ColorType("Background");
        TEXT_FOREGROUND = new ColorType("TextForeground");
        TEXT_BACKGROUND = new ColorType("TextBackground");
        FOCUS = new ColorType("Focus");
        MAX_COUNT = Math.max(ColorType.FOREGROUND.getID(), Math.max(ColorType.BACKGROUND.getID(), ColorType.FOCUS.getID())) + 1;
    }
}
