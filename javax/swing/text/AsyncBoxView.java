package javax.swing.text;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

public class AsyncBoxView extends View
{
    int axis;
    List<ChildState> stats;
    float majorSpan;
    boolean estimatedMajorSpan;
    float minorSpan;
    protected ChildLocator locator;
    float topInset;
    float bottomInset;
    float leftInset;
    float rightInset;
    ChildState minRequest;
    ChildState prefRequest;
    boolean majorChanged;
    boolean minorChanged;
    Runnable flushTask;
    ChildState changing;
    
    public AsyncBoxView(final Element element, final int axis) {
        super(element);
        this.stats = new ArrayList<ChildState>();
        this.axis = axis;
        this.locator = new ChildLocator();
        this.flushTask = new FlushTask();
        this.minorSpan = 32767.0f;
        this.estimatedMajorSpan = false;
    }
    
    public int getMajorAxis() {
        return this.axis;
    }
    
    public int getMinorAxis() {
        return (this.axis == 0) ? 1 : 0;
    }
    
    public float getTopInset() {
        return this.topInset;
    }
    
    public void setTopInset(final float topInset) {
        this.topInset = topInset;
    }
    
    public float getBottomInset() {
        return this.bottomInset;
    }
    
    public void setBottomInset(final float bottomInset) {
        this.bottomInset = bottomInset;
    }
    
    public float getLeftInset() {
        return this.leftInset;
    }
    
    public void setLeftInset(final float leftInset) {
        this.leftInset = leftInset;
    }
    
    public float getRightInset() {
        return this.rightInset;
    }
    
    public void setRightInset(final float rightInset) {
        this.rightInset = rightInset;
    }
    
    protected float getInsetSpan(final int n) {
        return (n == 0) ? (this.getLeftInset() + this.getRightInset()) : (this.getTopInset() + this.getBottomInset());
    }
    
    protected void setEstimatedMajorSpan(final boolean estimatedMajorSpan) {
        this.estimatedMajorSpan = estimatedMajorSpan;
    }
    
    protected boolean getEstimatedMajorSpan() {
        return this.estimatedMajorSpan;
    }
    
    protected ChildState getChildState(final int n) {
        synchronized (this.stats) {
            if (n >= 0 && n < this.stats.size()) {
                return this.stats.get(n);
            }
            return null;
        }
    }
    
    protected LayoutQueue getLayoutQueue() {
        return LayoutQueue.getDefaultQueue();
    }
    
    protected ChildState createChildState(final View view) {
        return new ChildState(view);
    }
    
    protected synchronized void majorRequirementChange(final ChildState childState, final float n) {
        if (!this.estimatedMajorSpan) {
            this.majorSpan += n;
        }
        this.majorChanged = true;
    }
    
    protected synchronized void minorRequirementChange(final ChildState childState) {
        this.minorChanged = true;
    }
    
    protected void flushRequirementChanges() {
        final AbstractDocument abstractDocument = (AbstractDocument)this.getDocument();
        try {
            abstractDocument.readLock();
            View parent = null;
            boolean b = false;
            boolean b2 = false;
            synchronized (this) {
                synchronized (this.stats) {
                    final int viewCount = this.getViewCount();
                    if (viewCount > 0 && (this.minorChanged || this.estimatedMajorSpan)) {
                        this.getLayoutQueue();
                        ChildState childState = this.getChildState(0);
                        ChildState childState2 = this.getChildState(0);
                        float majorSpan = 0.0f;
                        for (int i = 1; i < viewCount; ++i) {
                            final ChildState childState3 = this.getChildState(i);
                            if (this.minorChanged) {
                                if (childState3.min > childState.min) {
                                    childState = childState3;
                                }
                                if (childState3.pref > childState2.pref) {
                                    childState2 = childState3;
                                }
                            }
                            if (this.estimatedMajorSpan) {
                                majorSpan += childState3.getMajorSpan();
                            }
                        }
                        if (this.minorChanged) {
                            this.minRequest = childState;
                            this.prefRequest = childState2;
                        }
                        if (this.estimatedMajorSpan) {
                            this.majorSpan = majorSpan;
                            this.estimatedMajorSpan = false;
                            this.majorChanged = true;
                        }
                    }
                }
                if (this.majorChanged || this.minorChanged) {
                    parent = this.getParent();
                    if (parent != null) {
                        if (this.axis == 0) {
                            b = this.majorChanged;
                            b2 = this.minorChanged;
                        }
                        else {
                            b2 = this.majorChanged;
                            b = this.minorChanged;
                        }
                    }
                    this.majorChanged = false;
                    this.minorChanged = false;
                }
            }
            if (parent != null) {
                parent.preferenceChanged(this, b, b2);
                final Container container = this.getContainer();
                if (container != null) {
                    container.repaint();
                }
            }
        }
        finally {
            abstractDocument.readUnlock();
        }
    }
    
    @Override
    public void replace(final int n, final int n2, final View[] array) {
        synchronized (this.stats) {
            for (int i = 0; i < n2; ++i) {
                final ChildState childState = this.stats.remove(n);
                final float majorSpan = childState.getMajorSpan();
                childState.getChildView().setParent(null);
                if (majorSpan != 0.0f) {
                    this.majorRequirementChange(childState, -majorSpan);
                }
            }
            final LayoutQueue layoutQueue = this.getLayoutQueue();
            if (array != null) {
                for (int j = 0; j < array.length; ++j) {
                    final ChildState childState2 = this.createChildState(array[j]);
                    this.stats.add(n + j, childState2);
                    layoutQueue.addTask(childState2);
                }
            }
            layoutQueue.addTask(this.flushTask);
        }
    }
    
    protected void loadChildren(final ViewFactory viewFactory) {
        final Element element = this.getElement();
        final int elementCount = element.getElementCount();
        if (elementCount > 0) {
            final View[] array = new View[elementCount];
            for (int i = 0; i < elementCount; ++i) {
                array[i] = viewFactory.create(element.getElement(i));
            }
            this.replace(0, 0, array);
        }
    }
    
    protected synchronized int getViewIndexAtPosition(int n, final Position.Bias bias) {
        n = ((bias == Position.Bias.Backward) ? Math.max(0, n - 1) : n);
        return this.getElement().getElementIndex(n);
    }
    
    @Override
    protected void updateLayout(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final Shape shape) {
        if (elementChange != null) {
            this.locator.childChanged(this.getChildState(Math.max(elementChange.getIndex() - 1, 0)));
        }
    }
    
    @Override
    public void setParent(final View parent) {
        super.setParent(parent);
        if (parent != null && this.getViewCount() == 0) {
            this.loadChildren(this.getViewFactory());
        }
    }
    
    @Override
    public synchronized void preferenceChanged(final View view, final boolean b, final boolean b2) {
        if (view == null) {
            this.getParent().preferenceChanged(this, b, b2);
        }
        else {
            if (this.changing != null && this.changing.getChildView() == view) {
                this.changing.preferenceChanged(b, b2);
                return;
            }
            final ChildState childState = this.getChildState(this.getViewIndex(view.getStartOffset(), Position.Bias.Forward));
            childState.preferenceChanged(b, b2);
            final LayoutQueue layoutQueue = this.getLayoutQueue();
            layoutQueue.addTask(childState);
            layoutQueue.addTask(this.flushTask);
        }
    }
    
    @Override
    public void setSize(final float n, final float n2) {
        this.setSpanOnAxis(0, n);
        this.setSpanOnAxis(1, n2);
    }
    
    float getSpanOnAxis(final int n) {
        if (n == this.getMajorAxis()) {
            return this.majorSpan;
        }
        return this.minorSpan;
    }
    
    void setSpanOnAxis(final int n, final float n2) {
        final float insetSpan = this.getInsetSpan(n);
        if (n == this.getMinorAxis()) {
            final float minorSpan = n2 - insetSpan;
            if (minorSpan != this.minorSpan) {
                this.minorSpan = minorSpan;
                final int viewCount = this.getViewCount();
                if (viewCount != 0) {
                    final LayoutQueue layoutQueue = this.getLayoutQueue();
                    for (int i = 0; i < viewCount; ++i) {
                        final ChildState childState = this.getChildState(i);
                        childState.childSizeValid = false;
                        layoutQueue.addTask(childState);
                    }
                    layoutQueue.addTask(this.flushTask);
                }
            }
        }
        else if (this.estimatedMajorSpan) {
            this.majorSpan = n2 - insetSpan;
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape allocation) {
        synchronized (this.locator) {
            this.locator.setAllocation(allocation);
            this.locator.paintChildren(graphics);
        }
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        final float insetSpan = this.getInsetSpan(n);
        if (n == this.axis) {
            return this.majorSpan + insetSpan;
        }
        if (this.prefRequest != null) {
            return this.prefRequest.getChildView().getPreferredSpan(n) + insetSpan;
        }
        return insetSpan + 30.0f;
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        if (n == this.axis) {
            return this.getPreferredSpan(n);
        }
        if (this.minRequest != null) {
            return this.minRequest.getChildView().getMinimumSpan(n);
        }
        if (n == 0) {
            return this.getLeftInset() + this.getRightInset() + 5.0f;
        }
        return this.getTopInset() + this.getBottomInset() + 5.0f;
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        if (n == this.axis) {
            return this.getPreferredSpan(n);
        }
        return 2.14748365E9f;
    }
    
    @Override
    public int getViewCount() {
        synchronized (this.stats) {
            return this.stats.size();
        }
    }
    
    @Override
    public View getView(final int n) {
        final ChildState childState = this.getChildState(n);
        if (childState != null) {
            return childState.getChildView();
        }
        return null;
    }
    
    @Override
    public Shape getChildAllocation(final int n, final Shape shape) {
        return this.locator.getChildAllocation(n, shape);
    }
    
    @Override
    public int getViewIndex(final int n, final Position.Bias bias) {
        return this.getViewIndexAtPosition(n, bias);
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        final int viewIndex = this.getViewIndex(n, bias);
        final Shape childAllocation = this.locator.getChildAllocation(viewIndex, shape);
        final ChildState childState = this.getChildState(viewIndex);
        synchronized (childState) {
            return childState.getChildView().modelToView(n, childAllocation, bias);
        }
    }
    
    @Override
    public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        final int viewIndexAtPoint;
        final Shape childAllocation;
        synchronized (this.locator) {
            viewIndexAtPoint = this.locator.getViewIndexAtPoint(n, n2, shape);
            childAllocation = this.locator.getChildAllocation(viewIndexAtPoint, shape);
        }
        final ChildState childState = this.getChildState(viewIndexAtPoint);
        final int viewToModel;
        synchronized (childState) {
            viewToModel = childState.getChildView().viewToModel(n, n2, childAllocation, array);
        }
        return viewToModel;
    }
    
    @Override
    public int getNextVisualPositionFrom(final int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
        if (n < -1) {
            throw new BadLocationException("invalid position", n);
        }
        return Utilities.getNextVisualPositionFrom(this, n, bias, shape, n2, array);
    }
    
    public class ChildLocator
    {
        protected ChildState lastValidOffset;
        protected Rectangle lastAlloc;
        protected Rectangle childAlloc;
        
        public ChildLocator() {
            this.lastAlloc = new Rectangle();
            this.childAlloc = new Rectangle();
        }
        
        public synchronized void childChanged(final ChildState childState) {
            if (this.lastValidOffset == null) {
                this.lastValidOffset = childState;
            }
            else if (childState.getChildView().getStartOffset() < this.lastValidOffset.getChildView().getStartOffset()) {
                this.lastValidOffset = childState;
            }
        }
        
        public synchronized void paintChildren(final Graphics graphics) {
            final Rectangle clipBounds = graphics.getClipBounds();
            final int viewIndexAtVisualOffset = this.getViewIndexAtVisualOffset((AsyncBoxView.this.axis == 0) ? ((float)(clipBounds.x - this.lastAlloc.x)) : ((float)(clipBounds.y - this.lastAlloc.y)));
            final int viewCount = AsyncBoxView.this.getViewCount();
            float majorOffset = AsyncBoxView.this.getChildState(viewIndexAtVisualOffset).getMajorOffset();
            for (int i = viewIndexAtVisualOffset; i < viewCount; ++i) {
                final ChildState childState = AsyncBoxView.this.getChildState(i);
                childState.setMajorOffset(majorOffset);
                final Shape childAllocation = this.getChildAllocation(i);
                if (!this.intersectsClip(childAllocation, clipBounds)) {
                    break;
                }
                synchronized (childState) {
                    childState.getChildView().paint(graphics, childAllocation);
                }
                majorOffset += childState.getMajorSpan();
            }
        }
        
        public synchronized Shape getChildAllocation(final int n, final Shape allocation) {
            if (allocation == null) {
                return null;
            }
            this.setAllocation(allocation);
            final ChildState childState = AsyncBoxView.this.getChildState(n);
            if (this.lastValidOffset == null) {
                this.lastValidOffset = AsyncBoxView.this.getChildState(0);
            }
            if (childState.getChildView().getStartOffset() > this.lastValidOffset.getChildView().getStartOffset()) {
                this.updateChildOffsetsToIndex(n);
            }
            return this.getChildAllocation(n);
        }
        
        public int getViewIndexAtPoint(final float n, final float n2, final Shape allocation) {
            this.setAllocation(allocation);
            return this.getViewIndexAtVisualOffset((AsyncBoxView.this.axis == 0) ? (n - this.lastAlloc.x) : (n2 - this.lastAlloc.y));
        }
        
        protected Shape getChildAllocation(final int n) {
            final ChildState childState = AsyncBoxView.this.getChildState(n);
            if (!childState.isLayoutValid()) {
                childState.run();
            }
            if (AsyncBoxView.this.axis == 0) {
                this.childAlloc.x = this.lastAlloc.x + (int)childState.getMajorOffset();
                this.childAlloc.y = this.lastAlloc.y + (int)childState.getMinorOffset();
                this.childAlloc.width = (int)childState.getMajorSpan();
                this.childAlloc.height = (int)childState.getMinorSpan();
            }
            else {
                this.childAlloc.y = this.lastAlloc.y + (int)childState.getMajorOffset();
                this.childAlloc.x = this.lastAlloc.x + (int)childState.getMinorOffset();
                this.childAlloc.height = (int)childState.getMajorSpan();
                this.childAlloc.width = (int)childState.getMinorSpan();
            }
            final Rectangle childAlloc = this.childAlloc;
            childAlloc.x += (int)AsyncBoxView.this.getLeftInset();
            final Rectangle childAlloc2 = this.childAlloc;
            childAlloc2.y += (int)AsyncBoxView.this.getRightInset();
            return this.childAlloc;
        }
        
        protected void setAllocation(final Shape shape) {
            if (shape instanceof Rectangle) {
                this.lastAlloc.setBounds((Rectangle)shape);
            }
            else {
                this.lastAlloc.setBounds(shape.getBounds());
            }
            AsyncBoxView.this.setSize((float)this.lastAlloc.width, (float)this.lastAlloc.height);
        }
        
        protected int getViewIndexAtVisualOffset(final float n) {
            final int viewCount = AsyncBoxView.this.getViewCount();
            if (viewCount > 0) {
                final boolean b = this.lastValidOffset != null;
                if (this.lastValidOffset == null) {
                    this.lastValidOffset = AsyncBoxView.this.getChildState(0);
                }
                if (n > AsyncBoxView.this.majorSpan) {
                    if (!b) {
                        return 0;
                    }
                    return AsyncBoxView.this.getViewIndex(this.lastValidOffset.getChildView().getStartOffset(), Position.Bias.Forward);
                }
                else {
                    if (n > this.lastValidOffset.getMajorOffset()) {
                        return this.updateChildOffsets(n);
                    }
                    float n2 = 0.0f;
                    for (int i = 0; i < viewCount; ++i) {
                        final float n3 = n2 + AsyncBoxView.this.getChildState(i).getMajorSpan();
                        if (n < n3) {
                            return i;
                        }
                        n2 = n3;
                    }
                }
            }
            return viewCount - 1;
        }
        
        int updateChildOffsets(final float n) {
            final int viewCount = AsyncBoxView.this.getViewCount();
            int n2 = viewCount - 1;
            final int viewIndex = AsyncBoxView.this.getViewIndex(this.lastValidOffset.getChildView().getStartOffset(), Position.Bias.Forward);
            float majorOffset = this.lastValidOffset.getMajorOffset();
            for (int i = viewIndex; i < viewCount; ++i) {
                final ChildState childState = AsyncBoxView.this.getChildState(i);
                childState.setMajorOffset(majorOffset);
                majorOffset += childState.getMajorSpan();
                if (n < majorOffset) {
                    n2 = i;
                    this.lastValidOffset = childState;
                    break;
                }
            }
            return n2;
        }
        
        void updateChildOffsetsToIndex(final int n) {
            final int viewIndex = AsyncBoxView.this.getViewIndex(this.lastValidOffset.getChildView().getStartOffset(), Position.Bias.Forward);
            float majorOffset = this.lastValidOffset.getMajorOffset();
            for (int i = viewIndex; i <= n; ++i) {
                final ChildState childState = AsyncBoxView.this.getChildState(i);
                childState.setMajorOffset(majorOffset);
                majorOffset += childState.getMajorSpan();
            }
        }
        
        boolean intersectsClip(final Shape shape, final Rectangle rectangle) {
            final Rectangle rectangle2 = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
            return rectangle2.intersects(rectangle) && this.lastAlloc.intersects(rectangle2);
        }
    }
    
    public class ChildState implements Runnable
    {
        private float min;
        private float pref;
        private float max;
        private boolean minorValid;
        private float span;
        private float offset;
        private boolean majorValid;
        private View child;
        private boolean childSizeValid;
        
        public ChildState(final View child) {
            this.child = child;
            this.minorValid = false;
            this.majorValid = false;
            this.childSizeValid = false;
            this.child.setParent(AsyncBoxView.this);
        }
        
        public View getChildView() {
            return this.child;
        }
        
        @Override
        public void run() {
            final AbstractDocument abstractDocument = (AbstractDocument)AsyncBoxView.this.getDocument();
            try {
                abstractDocument.readLock();
                if (this.minorValid && this.majorValid && this.childSizeValid) {
                    return;
                }
                if (this.child.getParent() == AsyncBoxView.this) {
                    synchronized (AsyncBoxView.this) {
                        AsyncBoxView.this.changing = this;
                    }
                    this.updateChild();
                    synchronized (AsyncBoxView.this) {
                        AsyncBoxView.this.changing = null;
                    }
                    this.updateChild();
                }
            }
            finally {
                abstractDocument.readUnlock();
            }
        }
        
        void updateChild() {
            boolean b = false;
            synchronized (this) {
                if (!this.minorValid) {
                    final int minorAxis = AsyncBoxView.this.getMinorAxis();
                    this.min = this.child.getMinimumSpan(minorAxis);
                    this.pref = this.child.getPreferredSpan(minorAxis);
                    this.max = this.child.getMaximumSpan(minorAxis);
                    this.minorValid = true;
                    b = true;
                }
            }
            if (b) {
                AsyncBoxView.this.minorRequirementChange(this);
            }
            boolean b2 = false;
            float n = 0.0f;
            synchronized (this) {
                if (!this.majorValid) {
                    final float span = this.span;
                    this.span = this.child.getPreferredSpan(AsyncBoxView.this.axis);
                    n = this.span - span;
                    this.majorValid = true;
                    b2 = true;
                }
            }
            if (b2) {
                AsyncBoxView.this.majorRequirementChange(this, n);
                AsyncBoxView.this.locator.childChanged(this);
            }
            synchronized (this) {
                if (!this.childSizeValid) {
                    float n2;
                    float n3;
                    if (AsyncBoxView.this.axis == 0) {
                        n2 = this.span;
                        n3 = this.getMinorSpan();
                    }
                    else {
                        n2 = this.getMinorSpan();
                        n3 = this.span;
                    }
                    this.childSizeValid = true;
                    this.child.setSize(n2, n3);
                }
            }
        }
        
        public float getMinorSpan() {
            if (this.max < AsyncBoxView.this.minorSpan) {
                return this.max;
            }
            return Math.max(this.min, AsyncBoxView.this.minorSpan);
        }
        
        public float getMinorOffset() {
            if (this.max < AsyncBoxView.this.minorSpan) {
                return (AsyncBoxView.this.minorSpan - this.max) * this.child.getAlignment(AsyncBoxView.this.getMinorAxis());
            }
            return 0.0f;
        }
        
        public float getMajorSpan() {
            return this.span;
        }
        
        public float getMajorOffset() {
            return this.offset;
        }
        
        public void setMajorOffset(final float offset) {
            this.offset = offset;
        }
        
        public void preferenceChanged(final boolean b, final boolean b2) {
            if (AsyncBoxView.this.axis == 0) {
                if (b) {
                    this.majorValid = false;
                }
                if (b2) {
                    this.minorValid = false;
                }
            }
            else {
                if (b) {
                    this.minorValid = false;
                }
                if (b2) {
                    this.majorValid = false;
                }
            }
            this.childSizeValid = false;
        }
        
        public boolean isLayoutValid() {
            return this.minorValid && this.majorValid && this.childSizeValid;
        }
    }
    
    class FlushTask implements Runnable
    {
        @Override
        public void run() {
            AsyncBoxView.this.flushRequirementChanges();
        }
    }
}
