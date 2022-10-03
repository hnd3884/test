package org.jfree.util;

import java.io.ObjectOutputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import java.awt.Paint;

public class PaintList extends AbstractObjectList
{
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof PaintList) {
            final PaintList that = (PaintList)obj;
            for (int listSize = this.size(), i = 0; i < listSize; ++i) {
                if (!PaintUtilities.equal(this.getPaint(i), that.getPaint(i))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public Paint getPaint(final int index) {
        return (Paint)this.get(index);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        for (int count = stream.readInt(), i = 0; i < count; ++i) {
            final int index = stream.readInt();
            if (index != -1) {
                this.setPaint(index, SerialUtilities.readPaint(stream));
            }
        }
    }
    
    public void setPaint(final int index, final Paint paint) {
        this.set(index, paint);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        final int count = this.size();
        stream.writeInt(count);
        for (int i = 0; i < count; ++i) {
            final Paint paint = this.getPaint(i);
            if (paint != null) {
                stream.writeInt(i);
                SerialUtilities.writePaint(paint, stream);
            }
            else {
                stream.writeInt(-1);
            }
        }
    }
}
