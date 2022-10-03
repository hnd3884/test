package org.apache.poi.hpsf;

import java.util.List;
import java.util.ArrayList;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.Internal;

@Internal
public class Vector
{
    private final short _type;
    private TypedPropertyValue[] _values;
    
    public Vector(final short type) {
        this._type = type;
    }
    
    public void read(final LittleEndianByteArrayInputStream lei) {
        final long longLength = lei.readUInt();
        if (longLength > 2147483647L) {
            throw new UnsupportedOperationException("Vector is too long -- " + longLength);
        }
        final int length = (int)longLength;
        final List<TypedPropertyValue> values = new ArrayList<TypedPropertyValue>();
        final int paddedType = (this._type == 12) ? 0 : this._type;
        for (int i = 0; i < length; ++i) {
            final TypedPropertyValue value = new TypedPropertyValue(paddedType, null);
            if (paddedType == 0) {
                value.read(lei);
            }
            else {
                value.readValue(lei);
            }
            values.add(value);
        }
        this._values = values.toArray(new TypedPropertyValue[0]);
    }
    
    public TypedPropertyValue[] getValues() {
        return this._values;
    }
}
