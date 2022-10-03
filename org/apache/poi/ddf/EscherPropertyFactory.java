package org.apache.poi.ddf;

import java.util.Iterator;
import java.util.function.BiFunction;
import org.apache.poi.util.LittleEndian;
import java.util.ArrayList;
import java.util.List;

public final class EscherPropertyFactory
{
    public List<EscherProperty> createProperties(final byte[] data, final int offset, final short numProperties) {
        final List<EscherProperty> results = new ArrayList<EscherProperty>();
        int pos = offset;
        for (int i = 0; i < numProperties; ++i) {
            final short propId = LittleEndian.getShort(data, pos);
            final int propData = LittleEndian.getInt(data, pos + 2);
            final boolean isComplex = (propId & 0x8000) != 0x0;
            final EscherPropertyTypes propertyType = EscherPropertyTypes.forPropertyID(propId);
            BiFunction<Short, Integer, EscherProperty> con = null;
            switch (propertyType.holder) {
                case BOOLEAN: {
                    con = (BiFunction<Short, Integer, EscherProperty>)EscherBoolProperty::new;
                    break;
                }
                case RGB: {
                    con = (BiFunction<Short, Integer, EscherProperty>)EscherRGBProperty::new;
                    break;
                }
                case SHAPE_PATH: {
                    con = (BiFunction<Short, Integer, EscherProperty>)EscherShapePathProperty::new;
                    break;
                }
                default: {
                    if (isComplex) {
                        con = (BiFunction<Short, Integer, EscherProperty>)((propertyType.holder == EscherPropertyTypesHolder.ARRAY) ? EscherArrayProperty::new : EscherComplexProperty::new);
                        break;
                    }
                    con = (BiFunction<Short, Integer, EscherProperty>)EscherSimpleProperty::new;
                    break;
                }
            }
            results.add(con.apply(propId, propData));
            pos += 6;
        }
        for (final EscherProperty p : results) {
            if (p instanceof EscherArrayProperty) {
                final EscherArrayProperty eap = (EscherArrayProperty)p;
                pos += eap.setArrayData(data, pos);
            }
            else {
                if (!(p instanceof EscherComplexProperty)) {
                    continue;
                }
                final EscherComplexProperty ecp = (EscherComplexProperty)p;
                final int cdLen = ecp.getComplexData().length;
                final int leftover = data.length - pos;
                if (leftover < cdLen) {
                    throw new IllegalStateException("Could not read complex escher property, length was " + cdLen + ", but had only " + leftover + " bytes left");
                }
                pos += ecp.setComplexData(data, pos);
            }
        }
        return results;
    }
}
