package java.awt;

import sun.awt.AWTAccessor;
import java.util.EventListener;
import java.awt.event.AdjustmentEvent;
import java.awt.peer.ScrollPanePeer;
import java.awt.event.AdjustmentListener;
import java.io.Serializable;

public class ScrollPaneAdjustable implements Adjustable, Serializable
{
    private ScrollPane sp;
    private int orientation;
    private int value;
    private int minimum;
    private int maximum;
    private int visibleAmount;
    private transient boolean isAdjusting;
    private int unitIncrement;
    private int blockIncrement;
    private AdjustmentListener adjustmentListener;
    private static final String SCROLLPANE_ONLY = "Can be set by scrollpane only";
    private static final long serialVersionUID = -3359745691033257079L;
    
    private static native void initIDs();
    
    ScrollPaneAdjustable(final ScrollPane sp, final AdjustmentListener adjustmentListener, final int orientation) {
        this.unitIncrement = 1;
        this.blockIncrement = 1;
        this.sp = sp;
        this.orientation = orientation;
        this.addAdjustmentListener(adjustmentListener);
    }
    
    void setSpan(final int minimum, final int n, final int n2) {
        this.minimum = minimum;
        this.maximum = Math.max(n, this.minimum + 1);
        this.visibleAmount = Math.min(n2, this.maximum - this.minimum);
        this.visibleAmount = Math.max(this.visibleAmount, 1);
        this.blockIncrement = Math.max((int)(n2 * 0.9), 1);
        this.setValue(this.value);
    }
    
    @Override
    public int getOrientation() {
        return this.orientation;
    }
    
    @Override
    public void setMinimum(final int n) {
        throw new AWTError("Can be set by scrollpane only");
    }
    
    @Override
    public int getMinimum() {
        return 0;
    }
    
    @Override
    public void setMaximum(final int n) {
        throw new AWTError("Can be set by scrollpane only");
    }
    
    @Override
    public int getMaximum() {
        return this.maximum;
    }
    
    @Override
    public synchronized void setUnitIncrement(final int unitIncrement) {
        if (unitIncrement != this.unitIncrement) {
            this.unitIncrement = unitIncrement;
            if (this.sp.peer != null) {
                ((ScrollPanePeer)this.sp.peer).setUnitIncrement(this, unitIncrement);
            }
        }
    }
    
    @Override
    public int getUnitIncrement() {
        return this.unitIncrement;
    }
    
    @Override
    public synchronized void setBlockIncrement(final int blockIncrement) {
        this.blockIncrement = blockIncrement;
    }
    
    @Override
    public int getBlockIncrement() {
        return this.blockIncrement;
    }
    
    @Override
    public void setVisibleAmount(final int n) {
        throw new AWTError("Can be set by scrollpane only");
    }
    
    @Override
    public int getVisibleAmount() {
        return this.visibleAmount;
    }
    
    public void setValueIsAdjusting(final boolean isAdjusting) {
        if (this.isAdjusting != isAdjusting) {
            this.isAdjusting = isAdjusting;
            this.adjustmentListener.adjustmentValueChanged(new AdjustmentEvent(this, 601, 5, this.value, isAdjusting));
        }
    }
    
    public boolean getValueIsAdjusting() {
        return this.isAdjusting;
    }
    
    @Override
    public void setValue(final int n) {
        this.setTypedValue(n, 5);
    }
    
    private void setTypedValue(int value, final int n) {
        value = Math.max(value, this.minimum);
        value = Math.min(value, this.maximum - this.visibleAmount);
        if (value != this.value) {
            this.value = value;
            this.adjustmentListener.adjustmentValueChanged(new AdjustmentEvent(this, 601, n, this.value, this.isAdjusting));
        }
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    @Override
    public synchronized void addAdjustmentListener(final AdjustmentListener adjustmentListener) {
        if (adjustmentListener == null) {
            return;
        }
        this.adjustmentListener = AWTEventMulticaster.add(this.adjustmentListener, adjustmentListener);
    }
    
    @Override
    public synchronized void removeAdjustmentListener(final AdjustmentListener adjustmentListener) {
        if (adjustmentListener == null) {
            return;
        }
        this.adjustmentListener = AWTEventMulticaster.remove(this.adjustmentListener, adjustmentListener);
    }
    
    public synchronized AdjustmentListener[] getAdjustmentListeners() {
        return AWTEventMulticaster.getListeners(this.adjustmentListener, AdjustmentListener.class);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[" + this.paramString() + "]";
    }
    
    public String paramString() {
        return ((this.orientation == 1) ? "vertical," : "horizontal,") + "[0.." + this.maximum + "],val=" + this.value + ",vis=" + this.visibleAmount + ",unit=" + this.unitIncrement + ",block=" + this.blockIncrement + ",isAdjusting=" + this.isAdjusting;
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setScrollPaneAdjustableAccessor(new AWTAccessor.ScrollPaneAdjustableAccessor() {
            @Override
            public void setTypedValue(final ScrollPaneAdjustable scrollPaneAdjustable, final int n, final int n2) {
                scrollPaneAdjustable.setTypedValue(n, n2);
            }
        });
    }
}
