package org.jfree.chart.title;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.jfree.util.ObjectUtilities;
import org.jfree.chart.event.TitleChangeListener;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.TitleChangeEvent;
import javax.swing.event.EventListenerList;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import java.io.Serializable;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.AbstractBlock;

public abstract class Title extends AbstractBlock implements Block, Cloneable, Serializable
{
    private static final long serialVersionUID = -6675162505277817221L;
    public static final RectangleEdge DEFAULT_POSITION;
    public static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGNMENT;
    public static final VerticalAlignment DEFAULT_VERTICAL_ALIGNMENT;
    public static final RectangleInsets DEFAULT_PADDING;
    private RectangleEdge position;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private transient EventListenerList listenerList;
    private boolean notify;
    
    protected Title() {
        this(Title.DEFAULT_POSITION, Title.DEFAULT_HORIZONTAL_ALIGNMENT, Title.DEFAULT_VERTICAL_ALIGNMENT, Title.DEFAULT_PADDING);
    }
    
    protected Title(final RectangleEdge position, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
        this(position, horizontalAlignment, verticalAlignment, Title.DEFAULT_PADDING);
    }
    
    protected Title(final RectangleEdge position, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment, final RectangleInsets padding) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        if (horizontalAlignment == null) {
            throw new IllegalArgumentException("Null 'horizontalAlignment' argument.");
        }
        if (verticalAlignment == null) {
            throw new IllegalArgumentException("Null 'verticalAlignment' argument.");
        }
        if (padding == null) {
            throw new IllegalArgumentException("Null 'spacer' argument.");
        }
        this.position = position;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.setPadding(padding);
        this.listenerList = new EventListenerList();
        this.notify = true;
    }
    
    public RectangleEdge getPosition() {
        return this.position;
    }
    
    public void setPosition(final RectangleEdge position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        if (this.position != position) {
            this.position = position;
            this.notifyListeners(new TitleChangeEvent(this));
        }
    }
    
    public HorizontalAlignment getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
    
    public void setHorizontalAlignment(final HorizontalAlignment alignment) {
        if (alignment == null) {
            throw new IllegalArgumentException("Null 'alignment' argument.");
        }
        if (this.horizontalAlignment != alignment) {
            this.horizontalAlignment = alignment;
            this.notifyListeners(new TitleChangeEvent(this));
        }
    }
    
    public VerticalAlignment getVerticalAlignment() {
        return this.verticalAlignment;
    }
    
    public void setVerticalAlignment(final VerticalAlignment alignment) {
        if (alignment == null) {
            throw new IllegalArgumentException("Null 'alignment' argument.");
        }
        if (this.verticalAlignment != alignment) {
            this.verticalAlignment = alignment;
            this.notifyListeners(new TitleChangeEvent(this));
        }
    }
    
    public boolean getNotify() {
        return this.notify;
    }
    
    public void setNotify(final boolean flag) {
        this.notify = flag;
        if (flag) {
            this.notifyListeners(new TitleChangeEvent(this));
        }
    }
    
    public abstract void draw(final Graphics2D p0, final Rectangle2D p1);
    
    public Object clone() throws CloneNotSupportedException {
        final Title duplicate = (Title)super.clone();
        duplicate.listenerList = new EventListenerList();
        return duplicate;
    }
    
    public void addChangeListener(final TitleChangeListener listener) {
        this.listenerList.add(TitleChangeListener.class, listener);
    }
    
    public void removeChangeListener(final TitleChangeListener listener) {
        this.listenerList.remove(TitleChangeListener.class, listener);
    }
    
    protected void notifyListeners(final TitleChangeEvent event) {
        if (this.notify) {
            final Object[] listeners = this.listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TitleChangeListener.class) {
                    ((TitleChangeListener)listeners[i + 1]).titleChanged(event);
                }
            }
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Title)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final Title that = (Title)obj;
        return this.position == that.position && this.horizontalAlignment == that.horizontalAlignment && this.verticalAlignment == that.verticalAlignment && this.notify == that.notify;
    }
    
    public int hashCode() {
        int result = 193;
        result = 37 * result + ObjectUtilities.hashCode((Object)this.position);
        result = 37 * result + ObjectUtilities.hashCode((Object)this.horizontalAlignment);
        result = 37 * result + ObjectUtilities.hashCode((Object)this.verticalAlignment);
        return result;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
    }
    
    static {
        DEFAULT_POSITION = RectangleEdge.TOP;
        DEFAULT_HORIZONTAL_ALIGNMENT = HorizontalAlignment.CENTER;
        DEFAULT_VERTICAL_ALIGNMENT = VerticalAlignment.CENTER;
        DEFAULT_PADDING = new RectangleInsets(1.0, 1.0, 1.0, 1.0);
    }
}
