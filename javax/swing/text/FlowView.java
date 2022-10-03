package javax.swing.text;

import java.awt.Graphics;
import java.util.Vector;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import java.awt.Container;
import java.awt.Rectangle;
import javax.swing.event.DocumentEvent;

public abstract class FlowView extends BoxView
{
    protected int layoutSpan;
    protected View layoutPool;
    protected FlowStrategy strategy;
    
    public FlowView(final Element element, final int n) {
        super(element, n);
        this.layoutSpan = Integer.MAX_VALUE;
        this.strategy = new FlowStrategy();
    }
    
    public int getFlowAxis() {
        if (this.getAxis() == 1) {
            return 0;
        }
        return 1;
    }
    
    public int getFlowSpan(final int n) {
        return this.layoutSpan;
    }
    
    public int getFlowStart(final int n) {
        return 0;
    }
    
    protected abstract View createRow();
    
    @Override
    protected void loadChildren(final ViewFactory viewFactory) {
        if (this.layoutPool == null) {
            this.layoutPool = new LogicalView(this.getElement());
        }
        this.layoutPool.setParent(this);
        this.strategy.insertUpdate(this, null, null);
    }
    
    @Override
    protected int getViewIndexAtPosition(final int n) {
        if (n >= this.getStartOffset() && n < this.getEndOffset()) {
            for (int i = 0; i < this.getViewCount(); ++i) {
                final View view = this.getView(i);
                if (n >= view.getStartOffset() && n < view.getEndOffset()) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    @Override
    protected void layout(final int n, final int n2) {
        final int flowAxis = this.getFlowAxis();
        int layoutSpan;
        if (flowAxis == 0) {
            layoutSpan = n;
        }
        else {
            layoutSpan = n2;
        }
        if (this.layoutSpan != layoutSpan) {
            this.layoutChanged(flowAxis);
            this.layoutChanged(this.getAxis());
            this.layoutSpan = layoutSpan;
        }
        if (!this.isLayoutValid(flowAxis)) {
            final int axis = this.getAxis();
            final int n3 = (axis == 0) ? this.getWidth() : this.getHeight();
            this.strategy.layout(this);
            if (n3 != (int)this.getPreferredSpan(axis)) {
                final View parent = this.getParent();
                if (parent != null) {
                    parent.preferenceChanged(this, axis == 0, axis == 1);
                }
                final Container container = this.getContainer();
                if (container != null) {
                    container.repaint();
                }
            }
        }
        super.layout(n, n2);
    }
    
    @Override
    protected SizeRequirements calculateMinorAxisRequirements(final int n, SizeRequirements sizeRequirements) {
        if (sizeRequirements == null) {
            sizeRequirements = new SizeRequirements();
        }
        final float preferredSpan = this.layoutPool.getPreferredSpan(n);
        sizeRequirements.minimum = (int)this.layoutPool.getMinimumSpan(n);
        sizeRequirements.preferred = Math.max(sizeRequirements.minimum, (int)preferredSpan);
        sizeRequirements.maximum = Integer.MAX_VALUE;
        sizeRequirements.alignment = 0.5f;
        return sizeRequirements;
    }
    
    @Override
    public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.layoutPool.insertUpdate(documentEvent, shape, viewFactory);
        this.strategy.insertUpdate(this, documentEvent, this.getInsideAllocation(shape));
    }
    
    @Override
    public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.layoutPool.removeUpdate(documentEvent, shape, viewFactory);
        this.strategy.removeUpdate(this, documentEvent, this.getInsideAllocation(shape));
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.layoutPool.changedUpdate(documentEvent, shape, viewFactory);
        this.strategy.changedUpdate(this, documentEvent, this.getInsideAllocation(shape));
    }
    
    @Override
    public void setParent(final View parent) {
        super.setParent(parent);
        if (parent == null && this.layoutPool != null) {
            this.layoutPool.setParent(null);
        }
    }
    
    public static class FlowStrategy
    {
        Position damageStart;
        Vector<View> viewBuffer;
        
        public FlowStrategy() {
            this.damageStart = null;
        }
        
        void addDamage(final FlowView flowView, final int n) {
            if (n >= flowView.getStartOffset() && n < flowView.getEndOffset()) {
                if (this.damageStart != null) {
                    if (n >= this.damageStart.getOffset()) {
                        return;
                    }
                }
                try {
                    this.damageStart = flowView.getDocument().createPosition(n);
                }
                catch (final BadLocationException ex) {
                    assert false;
                }
            }
        }
        
        void unsetDamage() {
            this.damageStart = null;
        }
        
        public void insertUpdate(final FlowView flowView, final DocumentEvent documentEvent, final Rectangle rectangle) {
            if (documentEvent != null) {
                this.addDamage(flowView, documentEvent.getOffset());
            }
            if (rectangle != null) {
                final Container container = flowView.getContainer();
                if (container != null) {
                    container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
            }
            else {
                flowView.preferenceChanged(null, true, true);
            }
        }
        
        public void removeUpdate(final FlowView flowView, final DocumentEvent documentEvent, final Rectangle rectangle) {
            this.addDamage(flowView, documentEvent.getOffset());
            if (rectangle != null) {
                final Container container = flowView.getContainer();
                if (container != null) {
                    container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
            }
            else {
                flowView.preferenceChanged(null, true, true);
            }
        }
        
        public void changedUpdate(final FlowView flowView, final DocumentEvent documentEvent, final Rectangle rectangle) {
            this.addDamage(flowView, documentEvent.getOffset());
            if (rectangle != null) {
                final Container container = flowView.getContainer();
                if (container != null) {
                    container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
            }
            else {
                flowView.preferenceChanged(null, true, true);
            }
        }
        
        protected View getLogicalView(final FlowView flowView) {
            return flowView.layoutPool;
        }
        
        public void layout(final FlowView flowView) {
            final View logicalView = this.getLogicalView(flowView);
            final int endOffset = flowView.getEndOffset();
            int viewIndexAtPosition;
            int i;
            if (flowView.majorAllocValid) {
                if (this.damageStart == null) {
                    return;
                }
                for (int offset = this.damageStart.getOffset(); (viewIndexAtPosition = flowView.getViewIndexAtPosition(offset)) < 0; --offset) {}
                if (viewIndexAtPosition > 0) {
                    --viewIndexAtPosition;
                }
                i = flowView.getView(viewIndexAtPosition).getStartOffset();
            }
            else {
                viewIndexAtPosition = 0;
                i = flowView.getStartOffset();
            }
            this.reparentViews(logicalView, i);
            this.viewBuffer = new Vector<View>(10, 10);
            final int viewCount = flowView.getViewCount();
            while (i < endOffset) {
                if (viewIndexAtPosition >= viewCount) {
                    flowView.append(flowView.createRow());
                }
                else {
                    flowView.getView(viewIndexAtPosition);
                }
                i = this.layoutRow(flowView, viewIndexAtPosition, i);
                ++viewIndexAtPosition;
            }
            this.viewBuffer = null;
            if (viewIndexAtPosition < viewCount) {
                flowView.replace(viewIndexAtPosition, viewCount - viewIndexAtPosition, null);
            }
            this.unsetDamage();
        }
        
        protected int layoutRow(final FlowView flowView, final int n, int endOffset) {
            final View view = flowView.getView(n);
            float n2 = (float)flowView.getFlowStart(n);
            float n3 = (float)flowView.getFlowSpan(n);
            final int endOffset2 = flowView.getEndOffset();
            final TabExpander tabExpander = (flowView instanceof TabExpander) ? flowView : null;
            final int flowAxis = flowView.getFlowAxis();
            int n4 = 0;
            float n5 = 0.0f;
            float n6 = 0.0f;
            int n7 = -1;
            int n8 = 0;
            this.viewBuffer.clear();
            while (endOffset < endOffset2 && n3 >= 0.0f) {
                View view2 = this.createView(flowView, endOffset, (int)n3, n);
                if (view2 == null) {
                    break;
                }
                final int breakWeight = view2.getBreakWeight(flowAxis, n2, n3);
                if (breakWeight >= 3000) {
                    final View breakView = view2.breakView(flowAxis, endOffset, n2, n3);
                    if (breakView != null) {
                        this.viewBuffer.add(breakView);
                        break;
                    }
                    if (n8 == 0) {
                        this.viewBuffer.add(view2);
                        break;
                    }
                    break;
                }
                else {
                    if (breakWeight >= n4 && breakWeight > 0) {
                        n4 = breakWeight;
                        n5 = n2;
                        n6 = n3;
                        n7 = n8;
                    }
                    float n9;
                    if (flowAxis == 0 && view2 instanceof TabableView) {
                        n9 = ((TabableView)view2).getTabbedSpan(n2, tabExpander);
                    }
                    else {
                        n9 = view2.getPreferredSpan(flowAxis);
                    }
                    if (n9 > n3 && n7 >= 0) {
                        if (n7 < n8) {
                            view2 = this.viewBuffer.get(n7);
                        }
                        for (int i = n8 - 1; i >= n7; --i) {
                            this.viewBuffer.remove(i);
                        }
                        view2 = view2.breakView(flowAxis, view2.getStartOffset(), n5, n6);
                    }
                    n3 -= n9;
                    n2 += n9;
                    this.viewBuffer.add(view2);
                    endOffset = view2.getEndOffset();
                    ++n8;
                }
            }
            final View[] array = new View[this.viewBuffer.size()];
            this.viewBuffer.toArray(array);
            view.replace(0, view.getViewCount(), array);
            return (array.length > 0) ? view.getEndOffset() : endOffset;
        }
        
        protected void adjustRow(final FlowView flowView, final int n, final int n2, final int n3) {
            final int flowAxis = flowView.getFlowAxis();
            final View view = flowView.getView(n);
            final int viewCount = view.getViewCount();
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            int n7 = -1;
            for (int i = 0; i < viewCount; ++i) {
                final View view2 = view.getView(i);
                final int breakWeight = view2.getBreakWeight(flowAxis, (float)(n3 + n4), (float)(n2 - n4));
                if (breakWeight >= n5 && breakWeight > 0) {
                    n5 = breakWeight;
                    n7 = i;
                    n6 = n4;
                    if (breakWeight >= 3000) {
                        break;
                    }
                }
                n4 += (int)view2.getPreferredSpan(flowAxis);
            }
            if (n7 < 0) {
                return;
            }
            final int n8 = n2 - n6;
            final View view3 = view.getView(n7);
            final View[] array = { view3.breakView(flowAxis, view3.getStartOffset(), (float)(n3 + n6), (float)n8) };
            final View logicalView = this.getLogicalView(flowView);
            final int startOffset = view.getView(n7).getStartOffset();
            final int endOffset = view.getEndOffset();
            for (int j = 0; j < logicalView.getViewCount(); ++j) {
                final View view4 = logicalView.getView(j);
                if (view4.getEndOffset() > endOffset) {
                    break;
                }
                if (view4.getStartOffset() >= startOffset) {
                    view4.setParent(logicalView);
                }
            }
            view.replace(n7, viewCount - n7, array);
        }
        
        void reparentViews(final View parent, final int n) {
            final int viewIndex = parent.getViewIndex(n, Position.Bias.Forward);
            if (viewIndex >= 0) {
                for (int i = viewIndex; i < parent.getViewCount(); ++i) {
                    parent.getView(i).setParent(parent);
                }
            }
        }
        
        protected View createView(final FlowView flowView, final int n, final int n2, final int n3) {
            final View logicalView = this.getLogicalView(flowView);
            final View view = logicalView.getView(logicalView.getViewIndex(n, Position.Bias.Forward));
            if (n == view.getStartOffset()) {
                return view;
            }
            return view.createFragment(n, view.getEndOffset());
        }
    }
    
    static class LogicalView extends CompositeView
    {
        LogicalView(final Element element) {
            super(element);
        }
        
        @Override
        protected int getViewIndexAtPosition(final int n) {
            if (this.getElement().isLeaf()) {
                return 0;
            }
            return super.getViewIndexAtPosition(n);
        }
        
        @Override
        protected void loadChildren(final ViewFactory viewFactory) {
            final Element element = this.getElement();
            if (element.isLeaf()) {
                this.append(new LabelView(element));
            }
            else {
                super.loadChildren(viewFactory);
            }
        }
        
        @Override
        public AttributeSet getAttributes() {
            final View parent = this.getParent();
            return (parent != null) ? parent.getAttributes() : null;
        }
        
        @Override
        public float getPreferredSpan(final int n) {
            float max = 0.0f;
            float n2 = 0.0f;
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                final View view = this.getView(i);
                n2 += view.getPreferredSpan(n);
                if (view.getBreakWeight(n, 0.0f, 2.14748365E9f) >= 3000) {
                    max = Math.max(max, n2);
                    n2 = 0.0f;
                }
            }
            return Math.max(max, n2);
        }
        
        @Override
        public float getMinimumSpan(final int n) {
            float n2 = 0.0f;
            float n3 = 0.0f;
            int n4 = 0;
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                final View view = this.getView(i);
                if (view.getBreakWeight(n, 0.0f, 2.14748365E9f) == 0) {
                    n3 += view.getPreferredSpan(n);
                    n4 = 1;
                }
                else if (n4 != 0) {
                    n2 = Math.max(n3, n2);
                    n4 = 0;
                    n3 = 0.0f;
                }
                if (view instanceof ComponentView) {
                    n2 = Math.max(n2, view.getMinimumSpan(n));
                }
            }
            return Math.max(n2, n3);
        }
        
        @Override
        protected void forwardUpdateToView(final View view, final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            final View parent = view.getParent();
            view.setParent(this);
            super.forwardUpdateToView(view, documentEvent, shape, viewFactory);
            view.setParent(parent);
        }
        
        @Override
        protected void forwardUpdate(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            super.forwardUpdate(elementChange, documentEvent, shape, viewFactory);
            final DocumentEvent.EventType type = documentEvent.getType();
            if (type == DocumentEvent.EventType.INSERT || type == DocumentEvent.EventType.REMOVE) {
                this.firstUpdateIndex = Math.min(this.lastUpdateIndex + 1, this.getViewCount() - 1);
                this.lastUpdateIndex = Math.max(this.getViewCount() - 1, 0);
                for (int i = this.firstUpdateIndex; i <= this.lastUpdateIndex; ++i) {
                    final View view = this.getView(i);
                    if (view != null) {
                        view.updateAfterChange();
                    }
                }
            }
        }
        
        @Override
        public void paint(final Graphics graphics, final Shape shape) {
        }
        
        @Override
        protected boolean isBefore(final int n, final int n2, final Rectangle rectangle) {
            return false;
        }
        
        @Override
        protected boolean isAfter(final int n, final int n2, final Rectangle rectangle) {
            return false;
        }
        
        @Override
        protected View getViewAtPoint(final int n, final int n2, final Rectangle rectangle) {
            return null;
        }
        
        @Override
        protected void childAllocation(final int n, final Rectangle rectangle) {
        }
    }
}
