package org.jfree.util;

import java.io.ObjectOutputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import java.awt.Stroke;

public class StrokeList extends AbstractObjectList
{
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(final Object o) {
        return o != null && (o == this || (o instanceof StrokeList && super.equals(o)));
    }
    
    public Stroke getStroke(final int index) {
        return (Stroke)this.get(index);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        for (int count = stream.readInt(), i = 0; i < count; ++i) {
            final int index = stream.readInt();
            if (index != -1) {
                this.setStroke(index, SerialUtilities.readStroke(stream));
            }
        }
    }
    
    public void setStroke(final int index, final Stroke stroke) {
        this.set(index, stroke);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        final int count = this.size();
        stream.writeInt(count);
        for (int i = 0; i < count; ++i) {
            final Stroke stroke = this.getStroke(i);
            if (stroke != null) {
                stream.writeInt(i);
                SerialUtilities.writeStroke(stroke, stream);
            }
            else {
                stream.writeInt(-1);
            }
        }
    }
}
