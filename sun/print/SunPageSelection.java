package sun.print;

import javax.print.attribute.PrintRequestAttribute;

public final class SunPageSelection implements PrintRequestAttribute
{
    public static final SunPageSelection ALL;
    public static final SunPageSelection RANGE;
    public static final SunPageSelection SELECTION;
    private int pages;
    
    public SunPageSelection(final int pages) {
        this.pages = pages;
    }
    
    @Override
    public final Class getCategory() {
        return SunPageSelection.class;
    }
    
    @Override
    public final String getName() {
        return "sun-page-selection";
    }
    
    @Override
    public String toString() {
        return "page-selection: " + this.pages;
    }
    
    static {
        ALL = new SunPageSelection(0);
        RANGE = new SunPageSelection(1);
        SELECTION = new SunPageSelection(2);
    }
}
