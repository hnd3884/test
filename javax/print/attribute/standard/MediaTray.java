package javax.print.attribute.standard;

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.Attribute;

public class MediaTray extends Media implements Attribute
{
    private static final long serialVersionUID = -982503611095214703L;
    public static final MediaTray TOP;
    public static final MediaTray MIDDLE;
    public static final MediaTray BOTTOM;
    public static final MediaTray ENVELOPE;
    public static final MediaTray MANUAL;
    public static final MediaTray LARGE_CAPACITY;
    public static final MediaTray MAIN;
    public static final MediaTray SIDE;
    private static final String[] myStringTable;
    private static final MediaTray[] myEnumValueTable;
    
    protected MediaTray(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return MediaTray.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return MediaTray.myEnumValueTable.clone();
    }
    
    static {
        TOP = new MediaTray(0);
        MIDDLE = new MediaTray(1);
        BOTTOM = new MediaTray(2);
        ENVELOPE = new MediaTray(3);
        MANUAL = new MediaTray(4);
        LARGE_CAPACITY = new MediaTray(5);
        MAIN = new MediaTray(6);
        SIDE = new MediaTray(7);
        myStringTable = new String[] { "top", "middle", "bottom", "envelope", "manual", "large-capacity", "main", "side" };
        myEnumValueTable = new MediaTray[] { MediaTray.TOP, MediaTray.MIDDLE, MediaTray.BOTTOM, MediaTray.ENVELOPE, MediaTray.MANUAL, MediaTray.LARGE_CAPACITY, MediaTray.MAIN, MediaTray.SIDE };
    }
}
