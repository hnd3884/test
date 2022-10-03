package org.apache.poi.hpsf;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public class VariantBool
{
    private static final POILogger LOG;
    static final int SIZE = 2;
    private boolean _value;
    
    public void read(final LittleEndianByteArrayInputStream lei) {
        final short value = lei.readShort();
        switch (value) {
            case 0: {
                this._value = false;
                break;
            }
            case -1: {
                this._value = true;
                break;
            }
            default: {
                VariantBool.LOG.log(5, "VARIANT_BOOL value '" + value + "' is incorrect");
                this._value = true;
                break;
            }
        }
    }
    
    public boolean getValue() {
        return this._value;
    }
    
    public void setValue(final boolean value) {
        this._value = value;
    }
    
    static {
        LOG = POILogFactory.getLogger(VariantBool.class);
    }
}
