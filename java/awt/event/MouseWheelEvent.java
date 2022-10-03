package java.awt.event;

import java.awt.Component;

public class MouseWheelEvent extends MouseEvent
{
    public static final int WHEEL_UNIT_SCROLL = 0;
    public static final int WHEEL_BLOCK_SCROLL = 1;
    int scrollType;
    int scrollAmount;
    int wheelRotation;
    double preciseWheelRotation;
    private static final long serialVersionUID = 6459879390515399677L;
    
    public MouseWheelEvent(final Component component, final int n, final long n2, final int n3, final int n4, final int n5, final int n6, final boolean b, final int n7, final int n8, final int n9) {
        this(component, n, n2, n3, n4, n5, 0, 0, n6, b, n7, n8, n9);
    }
    
    public MouseWheelEvent(final Component component, final int n, final long n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final boolean b, final int n9, final int n10, final int n11) {
        this(component, n, n2, n3, n4, n5, n6, n7, n8, b, n9, n10, n11, n11);
    }
    
    public MouseWheelEvent(final Component component, final int n, final long n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final boolean b, final int scrollType, final int scrollAmount, final int wheelRotation, final double preciseWheelRotation) {
        super(component, n, n2, n3, n4, n5, n6, n7, n8, b, 0);
        this.scrollType = scrollType;
        this.scrollAmount = scrollAmount;
        this.wheelRotation = wheelRotation;
        this.preciseWheelRotation = preciseWheelRotation;
    }
    
    public int getScrollType() {
        return this.scrollType;
    }
    
    public int getScrollAmount() {
        return this.scrollAmount;
    }
    
    public int getWheelRotation() {
        return this.wheelRotation;
    }
    
    public double getPreciseWheelRotation() {
        return this.preciseWheelRotation;
    }
    
    public int getUnitsToScroll() {
        return this.scrollAmount * this.wheelRotation;
    }
    
    @Override
    public String paramString() {
        String s;
        if (this.getScrollType() == 0) {
            s = "WHEEL_UNIT_SCROLL";
        }
        else if (this.getScrollType() == 1) {
            s = "WHEEL_BLOCK_SCROLL";
        }
        else {
            s = "unknown scroll type";
        }
        return super.paramString() + ",scrollType=" + s + ",scrollAmount=" + this.getScrollAmount() + ",wheelRotation=" + this.getWheelRotation() + ",preciseWheelRotation=" + this.getPreciseWheelRotation();
    }
}
