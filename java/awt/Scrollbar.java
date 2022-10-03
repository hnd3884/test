package java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.AdjustmentEvent;
import java.util.EventListener;
import java.awt.peer.ScrollbarPeer;
import javax.accessibility.AccessibleState;
import java.awt.event.AdjustmentListener;
import javax.accessibility.Accessible;

public class Scrollbar extends Component implements Adjustable, Accessible
{
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    int value;
    int maximum;
    int minimum;
    int visibleAmount;
    int orientation;
    int lineIncrement;
    int pageIncrement;
    transient boolean isAdjusting;
    transient AdjustmentListener adjustmentListener;
    private static final String base = "scrollbar";
    private static int nameCounter;
    private static final long serialVersionUID = 8451667562882310543L;
    private int scrollbarSerializedDataVersion;
    
    private static native void initIDs();
    
    public Scrollbar() throws HeadlessException {
        this(1, 0, 10, 0, 100);
    }
    
    public Scrollbar(final int n) throws HeadlessException {
        this(n, 0, 10, 0, 100);
    }
    
    public Scrollbar(final int orientation, final int n, final int n2, final int n3, final int n4) throws HeadlessException {
        this.lineIncrement = 1;
        this.pageIncrement = 10;
        this.scrollbarSerializedDataVersion = 1;
        GraphicsEnvironment.checkHeadless();
        switch (orientation) {
            case 0:
            case 1: {
                this.orientation = orientation;
                this.setValues(n, n2, n3, n4);
                return;
            }
            default: {
                throw new IllegalArgumentException("illegal scrollbar orientation");
            }
        }
    }
    
    @Override
    String constructComponentName() {
        synchronized (Scrollbar.class) {
            return "scrollbar" + Scrollbar.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createScrollbar(this);
            }
            super.addNotify();
        }
    }
    
    @Override
    public int getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final int orientation) {
        synchronized (this.getTreeLock()) {
            if (orientation == this.orientation) {
                return;
            }
            switch (orientation) {
                case 0:
                case 1: {
                    this.orientation = orientation;
                    if (this.peer != null) {
                        this.removeNotify();
                        this.addNotify();
                        this.invalidate();
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("illegal scrollbar orientation");
                }
            }
        }
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleState", (orientation == 1) ? AccessibleState.HORIZONTAL : AccessibleState.VERTICAL, (orientation == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
        }
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    @Override
    public void setValue(final int n) {
        this.setValues(n, this.visibleAmount, this.minimum, this.maximum);
    }
    
    @Override
    public int getMinimum() {
        return this.minimum;
    }
    
    @Override
    public void setMinimum(final int n) {
        this.setValues(this.value, this.visibleAmount, n, this.maximum);
    }
    
    @Override
    public int getMaximum() {
        return this.maximum;
    }
    
    @Override
    public void setMaximum(int n) {
        if (n == Integer.MIN_VALUE) {
            n = -2147483647;
        }
        if (this.minimum >= n) {
            this.minimum = n - 1;
        }
        this.setValues(this.value, this.visibleAmount, this.minimum, n);
    }
    
    @Override
    public int getVisibleAmount() {
        return this.getVisible();
    }
    
    @Deprecated
    public int getVisible() {
        return this.visibleAmount;
    }
    
    @Override
    public void setVisibleAmount(final int n) {
        this.setValues(this.value, n, this.minimum, this.maximum);
    }
    
    @Override
    public void setUnitIncrement(final int lineIncrement) {
        this.setLineIncrement(lineIncrement);
    }
    
    @Deprecated
    public synchronized void setLineIncrement(final int n) {
        final int lineIncrement = (n < 1) ? 1 : n;
        if (this.lineIncrement == lineIncrement) {
            return;
        }
        this.lineIncrement = lineIncrement;
        final ScrollbarPeer scrollbarPeer = (ScrollbarPeer)this.peer;
        if (scrollbarPeer != null) {
            scrollbarPeer.setLineIncrement(this.lineIncrement);
        }
    }
    
    @Override
    public int getUnitIncrement() {
        return this.getLineIncrement();
    }
    
    @Deprecated
    public int getLineIncrement() {
        return this.lineIncrement;
    }
    
    @Override
    public void setBlockIncrement(final int pageIncrement) {
        this.setPageIncrement(pageIncrement);
    }
    
    @Deprecated
    public synchronized void setPageIncrement(final int n) {
        final int pageIncrement = (n < 1) ? 1 : n;
        if (this.pageIncrement == pageIncrement) {
            return;
        }
        this.pageIncrement = pageIncrement;
        final ScrollbarPeer scrollbarPeer = (ScrollbarPeer)this.peer;
        if (scrollbarPeer != null) {
            scrollbarPeer.setPageIncrement(this.pageIncrement);
        }
    }
    
    @Override
    public int getBlockIncrement() {
        return this.getPageIncrement();
    }
    
    @Deprecated
    public int getPageIncrement() {
        return this.pageIncrement;
    }
    
    public void setValues(int value, int visibleAmount, int minimum, int maximum) {
        final int value2;
        synchronized (this) {
            if (minimum == Integer.MAX_VALUE) {
                minimum = 2147483646;
            }
            if (maximum <= minimum) {
                maximum = minimum + 1;
            }
            long n = maximum - (long)minimum;
            if (n > 2147483647L) {
                n = 2147483647L;
                maximum = minimum + (int)n;
            }
            if (visibleAmount > (int)n) {
                visibleAmount = (int)n;
            }
            if (visibleAmount < 1) {
                visibleAmount = 1;
            }
            if (value < minimum) {
                value = minimum;
            }
            if (value > maximum - visibleAmount) {
                value = maximum - visibleAmount;
            }
            value2 = this.value;
            this.value = value;
            this.visibleAmount = visibleAmount;
            this.minimum = minimum;
            this.maximum = maximum;
            final ScrollbarPeer scrollbarPeer = (ScrollbarPeer)this.peer;
            if (scrollbarPeer != null) {
                scrollbarPeer.setValues(value, this.visibleAmount, minimum, maximum);
            }
        }
        if (value2 != value && this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleValue", value2, value);
        }
    }
    
    public boolean getValueIsAdjusting() {
        return this.isAdjusting;
    }
    
    public void setValueIsAdjusting(final boolean isAdjusting) {
        final boolean isAdjusting2;
        synchronized (this) {
            isAdjusting2 = this.isAdjusting;
            this.isAdjusting = isAdjusting;
        }
        if (isAdjusting2 != isAdjusting && this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleState", isAdjusting2 ? AccessibleState.BUSY : null, isAdjusting ? AccessibleState.BUSY : null);
        }
    }
    
    @Override
    public synchronized void addAdjustmentListener(final AdjustmentListener adjustmentListener) {
        if (adjustmentListener == null) {
            return;
        }
        this.adjustmentListener = AWTEventMulticaster.add(this.adjustmentListener, adjustmentListener);
        this.newEventsOnly = true;
    }
    
    @Override
    public synchronized void removeAdjustmentListener(final AdjustmentListener adjustmentListener) {
        if (adjustmentListener == null) {
            return;
        }
        this.adjustmentListener = AWTEventMulticaster.remove(this.adjustmentListener, adjustmentListener);
    }
    
    public synchronized AdjustmentListener[] getAdjustmentListeners() {
        return this.getListeners(AdjustmentListener.class);
    }
    
    @Override
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        if (clazz == AdjustmentListener.class) {
            return AWTEventMulticaster.getListeners(this.adjustmentListener, clazz);
        }
        return super.getListeners(clazz);
    }
    
    @Override
    boolean eventEnabled(final AWTEvent awtEvent) {
        if (awtEvent.id == 601) {
            return (this.eventMask & 0x100L) != 0x0L || this.adjustmentListener != null;
        }
        return super.eventEnabled(awtEvent);
    }
    
    @Override
    protected void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof AdjustmentEvent) {
            this.processAdjustmentEvent((AdjustmentEvent)awtEvent);
            return;
        }
        super.processEvent(awtEvent);
    }
    
    protected void processAdjustmentEvent(final AdjustmentEvent adjustmentEvent) {
        final AdjustmentListener adjustmentListener = this.adjustmentListener;
        if (adjustmentListener != null) {
            adjustmentListener.adjustmentValueChanged(adjustmentEvent);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",val=" + this.value + ",vis=" + this.visibleAmount + ",min=" + this.minimum + ",max=" + this.maximum + ((this.orientation == 1) ? ",vert" : ",horz") + ",isAdjusting=" + this.isAdjusting;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        AWTEventMulticaster.save(objectOutputStream, "adjustmentL", this.adjustmentListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        objectInputStream.defaultReadObject();
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            if ("adjustmentL" == ((String)object).intern()) {
                this.addAdjustmentListener((AdjustmentListener)objectInputStream.readObject());
            }
            else {
                objectInputStream.readObject();
            }
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTScrollBar();
        }
        return this.accessibleContext;
    }
    
    static {
        Scrollbar.nameCounter = 0;
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }
    
    protected class AccessibleAWTScrollBar extends AccessibleAWTComponent implements AccessibleValue
    {
        private static final long serialVersionUID = -344337268523697807L;
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (Scrollbar.this.getValueIsAdjusting()) {
                accessibleStateSet.add(AccessibleState.BUSY);
            }
            if (Scrollbar.this.getOrientation() == 1) {
                accessibleStateSet.add(AccessibleState.VERTICAL);
            }
            else {
                accessibleStateSet.add(AccessibleState.HORIZONTAL);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SCROLL_BAR;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            return Scrollbar.this.getValue();
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            if (n instanceof Integer) {
                Scrollbar.this.setValue(n.intValue());
                return true;
            }
            return false;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return Scrollbar.this.getMinimum();
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            return Scrollbar.this.getMaximum();
        }
    }
}
