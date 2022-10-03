package javax.swing.text;

import java.awt.geom.Rectangle2D;
import java.awt.Container;
import javax.swing.event.DocumentEvent;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Graphics;
import javax.swing.SwingConstants;

public abstract class View implements SwingConstants
{
    public static final int BadBreakWeight = 0;
    public static final int GoodBreakWeight = 1000;
    public static final int ExcellentBreakWeight = 2000;
    public static final int ForcedBreakWeight = 3000;
    public static final int X_AXIS = 0;
    public static final int Y_AXIS = 1;
    static final Position.Bias[] sharedBiasReturn;
    private View parent;
    private Element elem;
    int firstUpdateIndex;
    int lastUpdateIndex;
    
    public View(final Element elem) {
        this.elem = elem;
    }
    
    public View getParent() {
        return this.parent;
    }
    
    public boolean isVisible() {
        return true;
    }
    
    public abstract float getPreferredSpan(final int p0);
    
    public float getMinimumSpan(final int n) {
        if (this.getResizeWeight(n) == 0) {
            return this.getPreferredSpan(n);
        }
        return 0.0f;
    }
    
    public float getMaximumSpan(final int n) {
        if (this.getResizeWeight(n) == 0) {
            return this.getPreferredSpan(n);
        }
        return 2.14748365E9f;
    }
    
    public void preferenceChanged(final View view, final boolean b, final boolean b2) {
        final View parent = this.getParent();
        if (parent != null) {
            parent.preferenceChanged(this, b, b2);
        }
    }
    
    public float getAlignment(final int n) {
        return 0.5f;
    }
    
    public abstract void paint(final Graphics p0, final Shape p1);
    
    public void setParent(final View parent) {
        if (parent == null) {
            for (int i = 0; i < this.getViewCount(); ++i) {
                if (this.getView(i).getParent() == this) {
                    this.getView(i).setParent(null);
                }
            }
        }
        this.parent = parent;
    }
    
    public int getViewCount() {
        return 0;
    }
    
    public View getView(final int n) {
        return null;
    }
    
    public void removeAll() {
        this.replace(0, this.getViewCount(), null);
    }
    
    public void remove(final int n) {
        this.replace(n, 1, null);
    }
    
    public void insert(final int n, final View view) {
        this.replace(n, 0, new View[] { view });
    }
    
    public void append(final View view) {
        this.replace(this.getViewCount(), 0, new View[] { view });
    }
    
    public void replace(final int n, final int n2, final View[] array) {
    }
    
    public int getViewIndex(final int n, final Position.Bias bias) {
        return -1;
    }
    
    public Shape getChildAllocation(final int n, final Shape shape) {
        return null;
    }
    
    public int getNextVisualPositionFrom(int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
        if (n < -1) {
            throw new BadLocationException("Invalid position", n);
        }
        array[0] = Position.Bias.Forward;
        switch (n2) {
            case 1:
            case 5: {
                if (n == -1) {
                    n = ((n2 == 1) ? Math.max(0, this.getEndOffset() - 1) : this.getStartOffset());
                    break;
                }
                final JTextComponent textComponent = (JTextComponent)this.getContainer();
                final Caret caret = (textComponent != null) ? textComponent.getCaret() : null;
                Point magicCaretPosition;
                if (caret != null) {
                    magicCaretPosition = caret.getMagicCaretPosition();
                }
                else {
                    magicCaretPosition = null;
                }
                int x;
                if (magicCaretPosition == null) {
                    final Rectangle modelToView = textComponent.modelToView(n);
                    x = ((modelToView == null) ? 0 : modelToView.x);
                }
                else {
                    x = magicCaretPosition.x;
                }
                if (n2 == 1) {
                    n = Utilities.getPositionAbove(textComponent, n, x);
                }
                else {
                    n = Utilities.getPositionBelow(textComponent, n, x);
                }
                break;
            }
            case 7: {
                if (n == -1) {
                    n = Math.max(0, this.getEndOffset() - 1);
                    break;
                }
                n = Math.max(0, n - 1);
                break;
            }
            case 3: {
                if (n == -1) {
                    n = this.getStartOffset();
                    break;
                }
                n = Math.min(n + 1, this.getDocument().getLength());
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad direction: " + n2);
            }
        }
        return n;
    }
    
    public abstract Shape modelToView(final int p0, final Shape p1, final Position.Bias p2) throws BadLocationException;
    
    public Shape modelToView(final int n, final Position.Bias bias, final int n2, final Position.Bias bias2, final Shape shape) throws BadLocationException {
        final Shape modelToView = this.modelToView(n, shape, bias);
        Shape shape2;
        if (n2 == this.getEndOffset()) {
            try {
                shape2 = this.modelToView(n2, shape, bias2);
            }
            catch (final BadLocationException ex) {
                shape2 = null;
            }
            if (shape2 == null) {
                final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
                shape2 = new Rectangle(rectangle.x + rectangle.width - 1, rectangle.y, 1, rectangle.height);
            }
        }
        else {
            shape2 = this.modelToView(n2, shape, bias2);
        }
        final Rectangle bounds = modelToView.getBounds();
        final Rectangle rectangle2 = (Rectangle)((shape2 instanceof Rectangle) ? shape2 : shape2.getBounds());
        if (bounds.y != rectangle2.y) {
            final Rectangle rectangle3 = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
            bounds.x = rectangle3.x;
            bounds.width = rectangle3.width;
        }
        bounds.add(rectangle2);
        return bounds;
    }
    
    public abstract int viewToModel(final float p0, final float p1, final Shape p2, final Position.Bias[] p3);
    
    public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        if (this.getViewCount() > 0) {
            DocumentEvent.ElementChange change = documentEvent.getChange(this.getElement());
            if (change != null && !this.updateChildren(change, documentEvent, viewFactory)) {
                change = null;
            }
            this.forwardUpdate(change, documentEvent, shape, viewFactory);
            this.updateLayout(change, documentEvent, shape);
        }
    }
    
    public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        if (this.getViewCount() > 0) {
            DocumentEvent.ElementChange change = documentEvent.getChange(this.getElement());
            if (change != null && !this.updateChildren(change, documentEvent, viewFactory)) {
                change = null;
            }
            this.forwardUpdate(change, documentEvent, shape, viewFactory);
            this.updateLayout(change, documentEvent, shape);
        }
    }
    
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        if (this.getViewCount() > 0) {
            DocumentEvent.ElementChange change = documentEvent.getChange(this.getElement());
            if (change != null && !this.updateChildren(change, documentEvent, viewFactory)) {
                change = null;
            }
            this.forwardUpdate(change, documentEvent, shape, viewFactory);
            this.updateLayout(change, documentEvent, shape);
        }
    }
    
    public Document getDocument() {
        return this.elem.getDocument();
    }
    
    public int getStartOffset() {
        return this.elem.getStartOffset();
    }
    
    public int getEndOffset() {
        return this.elem.getEndOffset();
    }
    
    public Element getElement() {
        return this.elem;
    }
    
    public Graphics getGraphics() {
        return this.getContainer().getGraphics();
    }
    
    public AttributeSet getAttributes() {
        return this.elem.getAttributes();
    }
    
    public View breakView(final int n, final int n2, final float n3, final float n4) {
        return this;
    }
    
    public View createFragment(final int n, final int n2) {
        return this;
    }
    
    public int getBreakWeight(final int n, final float n2, final float n3) {
        if (n3 > this.getPreferredSpan(n)) {
            return 1000;
        }
        return 0;
    }
    
    public int getResizeWeight(final int n) {
        return 0;
    }
    
    public void setSize(final float n, final float n2) {
    }
    
    public Container getContainer() {
        final View parent = this.getParent();
        return (parent != null) ? parent.getContainer() : null;
    }
    
    public ViewFactory getViewFactory() {
        final View parent = this.getParent();
        return (parent != null) ? parent.getViewFactory() : null;
    }
    
    public String getToolTipText(final float n, final float n2, Shape childAllocation) {
        final int viewIndex = this.getViewIndex(n, n2, childAllocation);
        if (viewIndex >= 0) {
            childAllocation = this.getChildAllocation(viewIndex, childAllocation);
            if (((Rectangle2D)((childAllocation instanceof Rectangle) ? childAllocation : childAllocation.getBounds())).contains(n, n2)) {
                return this.getView(viewIndex).getToolTipText(n, n2, childAllocation);
            }
        }
        return null;
    }
    
    public int getViewIndex(final float n, final float n2, final Shape shape) {
        for (int i = this.getViewCount() - 1; i >= 0; --i) {
            final Shape childAllocation = this.getChildAllocation(i, shape);
            if (childAllocation != null && ((Rectangle2D)((childAllocation instanceof Rectangle) ? childAllocation : childAllocation.getBounds())).contains(n, n2)) {
                return i;
            }
        }
        return -1;
    }
    
    protected boolean updateChildren(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final ViewFactory viewFactory) {
        final Element[] childrenRemoved = elementChange.getChildrenRemoved();
        final Element[] childrenAdded = elementChange.getChildrenAdded();
        View[] array = null;
        if (childrenAdded != null) {
            array = new View[childrenAdded.length];
            for (int i = 0; i < childrenAdded.length; ++i) {
                array[i] = viewFactory.create(childrenAdded[i]);
            }
        }
        int length = 0;
        final int index = elementChange.getIndex();
        if (childrenRemoved != null) {
            length = childrenRemoved.length;
        }
        this.replace(index, length, array);
        return true;
    }
    
    protected void forwardUpdate(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.calculateUpdateIndexes(documentEvent);
        int n;
        int index = n = this.lastUpdateIndex + 1;
        final Element[] array = (Element[])((elementChange != null) ? elementChange.getChildrenAdded() : null);
        if (array != null && array.length > 0) {
            index = elementChange.getIndex();
            n = index + array.length - 1;
        }
        for (int i = this.firstUpdateIndex; i <= this.lastUpdateIndex; ++i) {
            if (i < index || i > n) {
                final View view = this.getView(i);
                if (view != null) {
                    this.forwardUpdateToView(view, documentEvent, this.getChildAllocation(i, shape), viewFactory);
                }
            }
        }
    }
    
    void calculateUpdateIndexes(final DocumentEvent documentEvent) {
        final int offset = documentEvent.getOffset();
        this.firstUpdateIndex = this.getViewIndex(offset, Position.Bias.Forward);
        if (this.firstUpdateIndex == -1 && documentEvent.getType() == DocumentEvent.EventType.REMOVE && offset >= this.getEndOffset()) {
            this.firstUpdateIndex = this.getViewCount() - 1;
        }
        this.lastUpdateIndex = this.firstUpdateIndex;
        final View view = (this.firstUpdateIndex >= 0) ? this.getView(this.firstUpdateIndex) : null;
        if (view != null && view.getStartOffset() == offset && offset > 0) {
            this.firstUpdateIndex = Math.max(this.firstUpdateIndex - 1, 0);
        }
        if (documentEvent.getType() != DocumentEvent.EventType.REMOVE) {
            this.lastUpdateIndex = this.getViewIndex(offset + documentEvent.getLength(), Position.Bias.Forward);
            if (this.lastUpdateIndex < 0) {
                this.lastUpdateIndex = this.getViewCount() - 1;
            }
        }
        this.firstUpdateIndex = Math.max(this.firstUpdateIndex, 0);
    }
    
    void updateAfterChange() {
    }
    
    protected void forwardUpdateToView(final View view, final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        final DocumentEvent.EventType type = documentEvent.getType();
        if (type == DocumentEvent.EventType.INSERT) {
            view.insertUpdate(documentEvent, shape, viewFactory);
        }
        else if (type == DocumentEvent.EventType.REMOVE) {
            view.removeUpdate(documentEvent, shape, viewFactory);
        }
        else {
            view.changedUpdate(documentEvent, shape, viewFactory);
        }
    }
    
    protected void updateLayout(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final Shape shape) {
        if (elementChange != null && shape != null) {
            this.preferenceChanged(null, true, true);
            final Container container = this.getContainer();
            if (container != null) {
                container.repaint();
            }
        }
    }
    
    @Deprecated
    public Shape modelToView(final int n, final Shape shape) throws BadLocationException {
        return this.modelToView(n, shape, Position.Bias.Forward);
    }
    
    @Deprecated
    public int viewToModel(final float n, final float n2, final Shape shape) {
        View.sharedBiasReturn[0] = Position.Bias.Forward;
        return this.viewToModel(n, n2, shape, View.sharedBiasReturn);
    }
    
    static {
        sharedBiasReturn = new Position.Bias[1];
    }
}
