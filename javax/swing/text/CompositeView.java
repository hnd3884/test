package javax.swing.text;

import java.awt.Shape;
import java.awt.Rectangle;

public abstract class CompositeView extends View
{
    private static View[] ZERO;
    private View[] children;
    private int nchildren;
    private short left;
    private short right;
    private short top;
    private short bottom;
    private Rectangle childAlloc;
    
    public CompositeView(final Element element) {
        super(element);
        this.children = new View[1];
        this.nchildren = 0;
        this.childAlloc = new Rectangle();
    }
    
    protected void loadChildren(final ViewFactory viewFactory) {
        if (viewFactory == null) {
            return;
        }
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
    
    @Override
    public void setParent(final View parent) {
        super.setParent(parent);
        if (parent != null && this.nchildren == 0) {
            this.loadChildren(this.getViewFactory());
        }
    }
    
    @Override
    public int getViewCount() {
        return this.nchildren;
    }
    
    @Override
    public View getView(final int n) {
        return this.children[n];
    }
    
    @Override
    public void replace(final int n, final int n2, View[] zero) {
        if (zero == null) {
            zero = CompositeView.ZERO;
        }
        for (int i = n; i < n + n2; ++i) {
            if (this.children[i].getParent() == this) {
                this.children[i].setParent(null);
            }
            this.children[i] = null;
        }
        final int n3 = zero.length - n2;
        final int n4 = n + n2;
        final int n5 = this.nchildren - n4;
        final int n6 = n4 + n3;
        if (this.nchildren + n3 >= this.children.length) {
            final View[] children = new View[Math.max(2 * this.children.length, this.nchildren + n3)];
            System.arraycopy(this.children, 0, children, 0, n);
            System.arraycopy(zero, 0, children, n, zero.length);
            System.arraycopy(this.children, n4, children, n6, n5);
            this.children = children;
        }
        else {
            System.arraycopy(this.children, n4, this.children, n6, n5);
            System.arraycopy(zero, 0, this.children, n, zero.length);
        }
        this.nchildren += n3;
        for (int j = 0; j < zero.length; ++j) {
            zero[j].setParent(this);
        }
    }
    
    @Override
    public Shape getChildAllocation(final int n, final Shape shape) {
        final Rectangle insideAllocation = this.getInsideAllocation(shape);
        this.childAllocation(n, insideAllocation);
        return insideAllocation;
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        final boolean b = bias == Position.Bias.Backward;
        final int n2 = b ? Math.max(0, n - 1) : n;
        if (b && n2 < this.getStartOffset()) {
            return null;
        }
        int viewIndexAtPosition = this.getViewIndexAtPosition(n2);
        if (viewIndexAtPosition != -1 && viewIndexAtPosition < this.getViewCount()) {
            final View view = this.getView(viewIndexAtPosition);
            if (view != null && n2 >= view.getStartOffset() && n2 < view.getEndOffset()) {
                final Shape childAllocation = this.getChildAllocation(viewIndexAtPosition, shape);
                if (childAllocation == null) {
                    return null;
                }
                Shape shape2 = view.modelToView(n, childAllocation, bias);
                if (shape2 == null && view.getEndOffset() == n && ++viewIndexAtPosition < this.getViewCount()) {
                    shape2 = this.getView(viewIndexAtPosition).modelToView(n, this.getChildAllocation(viewIndexAtPosition, shape), bias);
                }
                return shape2;
            }
        }
        throw new BadLocationException("Position not represented by view", n);
    }
    
    @Override
    public Shape modelToView(final int n, final Position.Bias bias, final int n2, final Position.Bias bias2, final Shape shape) throws BadLocationException {
        if (n == this.getStartOffset() && n2 == this.getEndOffset()) {
            return shape;
        }
        final Rectangle insideAllocation = this.getInsideAllocation(shape);
        final Rectangle rectangle = new Rectangle(insideAllocation);
        final View viewAtPosition = this.getViewAtPosition((bias == Position.Bias.Backward) ? Math.max(0, n - 1) : n, rectangle);
        final Rectangle rectangle2 = new Rectangle(insideAllocation);
        final View viewAtPosition2 = this.getViewAtPosition((bias2 == Position.Bias.Backward) ? Math.max(0, n2 - 1) : n2, rectangle2);
        if (viewAtPosition != viewAtPosition2) {
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                final View view;
                if ((view = this.getView(i)) == viewAtPosition || view == viewAtPosition2) {
                    final Rectangle rectangle3 = new Rectangle();
                    Rectangle rectangle4;
                    View view2;
                    if (view == viewAtPosition) {
                        rectangle4 = viewAtPosition.modelToView(n, bias, viewAtPosition.getEndOffset(), Position.Bias.Backward, rectangle).getBounds();
                        view2 = viewAtPosition2;
                    }
                    else {
                        rectangle4 = viewAtPosition2.modelToView(viewAtPosition2.getStartOffset(), Position.Bias.Forward, n2, bias2, rectangle2).getBounds();
                        view2 = viewAtPosition;
                    }
                    View view3;
                    while (++i < viewCount && (view3 = this.getView(i)) != view2) {
                        rectangle3.setBounds(insideAllocation);
                        this.childAllocation(i, rectangle3);
                        rectangle4.add(rectangle3);
                    }
                    if (view2 != null) {
                        Shape shape2;
                        if (view2 == viewAtPosition2) {
                            shape2 = viewAtPosition2.modelToView(viewAtPosition2.getStartOffset(), Position.Bias.Forward, n2, bias2, rectangle2);
                        }
                        else {
                            shape2 = viewAtPosition.modelToView(n, bias, viewAtPosition.getEndOffset(), Position.Bias.Backward, rectangle);
                        }
                        if (shape2 instanceof Rectangle) {
                            rectangle4.add((Rectangle)shape2);
                        }
                        else {
                            rectangle4.add(shape2.getBounds());
                        }
                    }
                    return rectangle4;
                }
            }
            throw new BadLocationException("Position not represented by view", n);
        }
        if (viewAtPosition == null) {
            return shape;
        }
        return viewAtPosition.modelToView(n, bias, n2, bias2, rectangle);
    }
    
    @Override
    public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        final Rectangle insideAllocation = this.getInsideAllocation(shape);
        if (this.isBefore((int)n, (int)n2, insideAllocation)) {
            int n3 = -1;
            try {
                n3 = this.getNextVisualPositionFrom(-1, Position.Bias.Forward, shape, 3, array);
            }
            catch (final BadLocationException ex) {}
            catch (final IllegalArgumentException ex2) {}
            if (n3 == -1) {
                n3 = this.getStartOffset();
                array[0] = Position.Bias.Forward;
            }
            return n3;
        }
        if (this.isAfter((int)n, (int)n2, insideAllocation)) {
            int nextVisualPosition = -1;
            try {
                nextVisualPosition = this.getNextVisualPositionFrom(-1, Position.Bias.Forward, shape, 7, array);
            }
            catch (final BadLocationException ex3) {}
            catch (final IllegalArgumentException ex4) {}
            if (nextVisualPosition == -1) {
                nextVisualPosition = this.getEndOffset() - 1;
                array[0] = Position.Bias.Forward;
            }
            return nextVisualPosition;
        }
        final View viewAtPoint = this.getViewAtPoint((int)n, (int)n2, insideAllocation);
        if (viewAtPoint != null) {
            return viewAtPoint.viewToModel(n, n2, insideAllocation, array);
        }
        return -1;
    }
    
    @Override
    public int getNextVisualPositionFrom(final int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
        if (n < -1) {
            throw new BadLocationException("invalid position", n);
        }
        this.getInsideAllocation(shape);
        switch (n2) {
            case 1: {
                return this.getNextNorthSouthVisualPositionFrom(n, bias, shape, n2, array);
            }
            case 5: {
                return this.getNextNorthSouthVisualPositionFrom(n, bias, shape, n2, array);
            }
            case 3: {
                return this.getNextEastWestVisualPositionFrom(n, bias, shape, n2, array);
            }
            case 7: {
                return this.getNextEastWestVisualPositionFrom(n, bias, shape, n2, array);
            }
            default: {
                throw new IllegalArgumentException("Bad direction: " + n2);
            }
        }
    }
    
    @Override
    public int getViewIndex(int n, final Position.Bias bias) {
        if (bias == Position.Bias.Backward) {
            --n;
        }
        if (n >= this.getStartOffset() && n < this.getEndOffset()) {
            return this.getViewIndexAtPosition(n);
        }
        return -1;
    }
    
    protected abstract boolean isBefore(final int p0, final int p1, final Rectangle p2);
    
    protected abstract boolean isAfter(final int p0, final int p1, final Rectangle p2);
    
    protected abstract View getViewAtPoint(final int p0, final int p1, final Rectangle p2);
    
    protected abstract void childAllocation(final int p0, final Rectangle p1);
    
    protected View getViewAtPosition(final int n, final Rectangle rectangle) {
        final int viewIndexAtPosition = this.getViewIndexAtPosition(n);
        if (viewIndexAtPosition >= 0 && viewIndexAtPosition < this.getViewCount()) {
            final View view = this.getView(viewIndexAtPosition);
            if (rectangle != null) {
                this.childAllocation(viewIndexAtPosition, rectangle);
            }
            return view;
        }
        return null;
    }
    
    protected int getViewIndexAtPosition(final int n) {
        return this.getElement().getElementIndex(n);
    }
    
    protected Rectangle getInsideAllocation(final Shape shape) {
        if (shape != null) {
            Rectangle bounds;
            if (shape instanceof Rectangle) {
                bounds = (Rectangle)shape;
            }
            else {
                bounds = shape.getBounds();
            }
            this.childAlloc.setBounds(bounds);
            final Rectangle childAlloc = this.childAlloc;
            childAlloc.x += this.getLeftInset();
            final Rectangle childAlloc2 = this.childAlloc;
            childAlloc2.y += this.getTopInset();
            final Rectangle childAlloc3 = this.childAlloc;
            childAlloc3.width -= this.getLeftInset() + this.getRightInset();
            final Rectangle childAlloc4 = this.childAlloc;
            childAlloc4.height -= this.getTopInset() + this.getBottomInset();
            return this.childAlloc;
        }
        return null;
    }
    
    protected void setParagraphInsets(final AttributeSet set) {
        this.top = (short)StyleConstants.getSpaceAbove(set);
        this.left = (short)StyleConstants.getLeftIndent(set);
        this.bottom = (short)StyleConstants.getSpaceBelow(set);
        this.right = (short)StyleConstants.getRightIndent(set);
    }
    
    protected void setInsets(final short top, final short left, final short bottom, final short right) {
        this.top = top;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
    }
    
    protected short getLeftInset() {
        return this.left;
    }
    
    protected short getRightInset() {
        return this.right;
    }
    
    protected short getTopInset() {
        return this.top;
    }
    
    protected short getBottomInset() {
        return this.bottom;
    }
    
    protected int getNextNorthSouthVisualPositionFrom(final int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
        return Utilities.getNextVisualPositionFrom(this, n, bias, shape, n2, array);
    }
    
    protected int getNextEastWestVisualPositionFrom(final int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
        return Utilities.getNextVisualPositionFrom(this, n, bias, shape, n2, array);
    }
    
    protected boolean flipEastAndWestAtEnds(final int n, final Position.Bias bias) {
        return false;
    }
    
    static {
        CompositeView.ZERO = new View[0];
    }
}
