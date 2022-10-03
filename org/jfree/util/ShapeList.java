package org.jfree.util;

import java.io.ObjectOutputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import java.awt.Shape;

public class ShapeList extends AbstractObjectList
{
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(final Object o) {
        return o != null && (o == this || (o instanceof ShapeList && super.equals(o)));
    }
    
    public Shape getShape(final int index) {
        return (Shape)this.get(index);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        for (int count = stream.readInt(), i = 0; i < count; ++i) {
            final int index = stream.readInt();
            if (index != -1) {
                this.setShape(index, SerialUtilities.readShape(stream));
            }
        }
    }
    
    public void setShape(final int index, final Shape shape) {
        this.set(index, shape);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        final int count = this.size();
        stream.writeInt(count);
        for (int i = 0; i < count; ++i) {
            final Shape shape = this.getShape(i);
            if (shape != null) {
                stream.writeInt(i);
                SerialUtilities.writeShape(shape, stream);
            }
            else {
                stream.writeInt(-1);
            }
        }
    }
}
