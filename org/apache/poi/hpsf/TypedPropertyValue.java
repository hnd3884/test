package org.apache.poi.hpsf;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.LittleEndianInput;
import java.math.BigInteger;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public class TypedPropertyValue
{
    private static final POILogger LOG;
    private int _type;
    private Object _value;
    
    public TypedPropertyValue(final int type, final Object value) {
        this._type = type;
        this._value = value;
    }
    
    public Object getValue() {
        return this._value;
    }
    
    public void read(final LittleEndianByteArrayInputStream lei) {
        this._type = lei.readShort();
        final short padding = lei.readShort();
        if (padding != 0) {
            TypedPropertyValue.LOG.log(5, "TypedPropertyValue padding at offset " + lei.getReadIndex() + " MUST be 0, but it's value is " + padding);
        }
        this.readValue(lei);
    }
    
    public void readValue(final LittleEndianByteArrayInputStream lei) {
        switch (this._type) {
            case 0:
            case 1: {
                this._value = null;
                break;
            }
            case 16: {
                this._value = lei.readByte();
                break;
            }
            case 17: {
                this._value = lei.readUByte();
                break;
            }
            case 2: {
                this._value = lei.readShort();
                break;
            }
            case 18: {
                this._value = lei.readUShort();
                break;
            }
            case 3:
            case 22: {
                this._value = lei.readInt();
                break;
            }
            case 10:
            case 19:
            case 23: {
                this._value = lei.readUInt();
                break;
            }
            case 20: {
                this._value = lei.readLong();
                break;
            }
            case 21: {
                final byte[] biBytesLE = new byte[8];
                lei.readFully(biBytesLE);
                final byte[] biBytesBE = new byte[9];
                int i = biBytesLE.length;
                for (final byte b : biBytesLE) {
                    if (i <= 8) {
                        biBytesBE[i] = b;
                    }
                    --i;
                }
                this._value = new BigInteger(biBytesBE);
                break;
            }
            case 4: {
                this._value = Float.intBitsToFloat(lei.readInt());
                break;
            }
            case 5: {
                this._value = lei.readDouble();
                break;
            }
            case 6: {
                final Currency cur = new Currency();
                cur.read(lei);
                this._value = cur;
                break;
            }
            case 7: {
                final Date date = new Date();
                date.read(lei);
                this._value = date;
                break;
            }
            case 8:
            case 30: {
                final CodePageString cps = new CodePageString();
                cps.read(lei);
                this._value = cps;
                break;
            }
            case 11: {
                final VariantBool vb = new VariantBool();
                vb.read(lei);
                this._value = vb;
                break;
            }
            case 14: {
                final Decimal dec = new Decimal();
                dec.read(lei);
                this._value = dec;
                break;
            }
            case 31: {
                final UnicodeString us = new UnicodeString();
                us.read(lei);
                this._value = us;
                break;
            }
            case 64: {
                final Filetime ft = new Filetime();
                ft.read(lei);
                this._value = ft;
                break;
            }
            case 65:
            case 70: {
                final Blob blob = new Blob();
                blob.read(lei);
                this._value = blob;
                break;
            }
            case 66:
            case 67:
            case 68:
            case 69: {
                final IndirectPropertyName ipn = new IndirectPropertyName();
                ipn.read(lei);
                this._value = ipn;
                break;
            }
            case 71: {
                final ClipboardData cd = new ClipboardData();
                cd.read(lei);
                this._value = cd;
                break;
            }
            case 72: {
                final GUID guid = new GUID();
                guid.read(lei);
                this._value = lei;
                break;
            }
            case 73: {
                final VersionedStream vs = new VersionedStream();
                vs.read(lei);
                this._value = vs;
                break;
            }
            case 4098:
            case 4099:
            case 4100:
            case 4101:
            case 4102:
            case 4103:
            case 4104:
            case 4106:
            case 4107:
            case 4108:
            case 4112:
            case 4113:
            case 4114:
            case 4115:
            case 4116:
            case 4117:
            case 4126:
            case 4127:
            case 4160:
            case 4167:
            case 4168: {
                final Vector vec = new Vector((short)(this._type & 0xFFF));
                vec.read(lei);
                this._value = vec;
                break;
            }
            case 8194:
            case 8195:
            case 8196:
            case 8197:
            case 8198:
            case 8199:
            case 8200:
            case 8202:
            case 8203:
            case 8204:
            case 8206:
            case 8208:
            case 8209:
            case 8210:
            case 8211:
            case 8214:
            case 8215: {
                final Array arr = new Array();
                arr.read(lei);
                this._value = arr;
                break;
            }
            default: {
                final String msg = "Unknown (possibly, incorrect) TypedPropertyValue type: " + this._type;
                throw new UnsupportedOperationException(msg);
            }
        }
    }
    
    static void skipPadding(final LittleEndianByteArrayInputStream lei) {
        final int offset = lei.getReadIndex();
        for (int skipBytes = 4 - (offset & 0x3) & 0x3, i = 0; i < skipBytes; ++i) {
            lei.mark(1);
            final int b = lei.read();
            if (b == -1 || b != 0) {
                lei.reset();
                break;
            }
        }
    }
    
    static {
        LOG = POILogFactory.getLogger(TypedPropertyValue.class);
    }
}
