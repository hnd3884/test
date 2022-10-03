package sun.print;

import javax.print.attribute.PrintRequestAttribute;

public final class SunMinMaxPage implements PrintRequestAttribute
{
    private int page_max;
    private int page_min;
    
    public SunMinMaxPage(final int page_min, final int page_max) {
        this.page_min = page_min;
        this.page_max = page_max;
    }
    
    @Override
    public final Class getCategory() {
        return SunMinMaxPage.class;
    }
    
    public final int getMin() {
        return this.page_min;
    }
    
    public final int getMax() {
        return this.page_max;
    }
    
    @Override
    public final String getName() {
        return "sun-page-minmax";
    }
}
