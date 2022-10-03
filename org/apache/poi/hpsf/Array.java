package org.apache.poi.hpsf;

import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.Internal;

@Internal
public class Array
{
    private final ArrayHeader _header;
    private TypedPropertyValue[] _values;
    
    public Array() {
        this._header = new ArrayHeader();
    }
    
    public void read(final LittleEndianByteArrayInputStream lei) {
        this._header.read(lei);
        final long numberOfScalarsLong = this._header.getNumberOfScalarValues();
        if (numberOfScalarsLong > 2147483647L) {
            final String msg = "Sorry, but POI can't store array of properties with size of " + numberOfScalarsLong + " in memory";
            throw new UnsupportedOperationException(msg);
        }
        final int numberOfScalars = (int)numberOfScalarsLong;
        this._values = new TypedPropertyValue[numberOfScalars];
        final int paddedType = (this._header._type == 12) ? 0 : this._header._type;
        for (int i = 0; i < numberOfScalars; ++i) {
            final TypedPropertyValue typedPropertyValue = new TypedPropertyValue(paddedType, null);
            typedPropertyValue.read(lei);
            this._values[i] = typedPropertyValue;
            if (paddedType != 0) {
                TypedPropertyValue.skipPadding(lei);
            }
        }
    }
    
    public TypedPropertyValue[] getValues() {
        return this._values;
    }
    
    static class ArrayDimension
    {
        private long _size;
        private int _indexOffset;
        
        void read(final LittleEndianByteArrayInputStream lei) {
            this._size = lei.readUInt();
            this._indexOffset = lei.readInt();
        }
    }
    
    static class ArrayHeader
    {
        private ArrayDimension[] _dimensions;
        private int _type;
        
        void read(final LittleEndianByteArrayInputStream lei) {
            this._type = lei.readInt();
            final long numDimensionsUnsigned = lei.readUInt();
            if (1L > numDimensionsUnsigned || numDimensionsUnsigned > 31L) {
                final String msg = "Array dimension number " + numDimensionsUnsigned + " is not in [1; 31] range";
                throw new IllegalPropertySetDataException(msg);
            }
            final int numDimensions = (int)numDimensionsUnsigned;
            this._dimensions = new ArrayDimension[numDimensions];
            for (int i = 0; i < numDimensions; ++i) {
                final ArrayDimension ad = new ArrayDimension();
                ad.read(lei);
                this._dimensions[i] = ad;
            }
        }
        
        long getNumberOfScalarValues() {
            long result = 1L;
            for (final ArrayDimension dimension : this._dimensions) {
                result *= dimension._size;
            }
            return result;
        }
        
        int getType() {
            return this._type;
        }
    }
}
