package org.apache.poi.hpsf;

import org.apache.poi.util.POILogFactory;
import java.util.Date;
import java.math.BigInteger;
import org.apache.poi.util.LittleEndian;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.IOUtils;
import java.io.UnsupportedEncodingException;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.util.POILogger;

public class VariantSupport extends Variant
{
    public static final int[] SUPPORTED_TYPES;
    private static final POILogger logger;
    private static final int MAX_RECORD_LENGTH = 100000;
    private static boolean logUnsupportedTypes;
    private static List<Long> unsupportedMessage;
    private static final byte[] paddingBytes;
    
    public static void setLogUnsupportedTypes(final boolean logUnsupportedTypes) {
        VariantSupport.logUnsupportedTypes = logUnsupportedTypes;
    }
    
    public static boolean isLogUnsupportedTypes() {
        return VariantSupport.logUnsupportedTypes;
    }
    
    protected static void writeUnsupportedTypeMessage(final UnsupportedVariantTypeException ex) {
        if (isLogUnsupportedTypes()) {
            if (VariantSupport.unsupportedMessage == null) {
                VariantSupport.unsupportedMessage = new LinkedList<Long>();
            }
            final Long vt = ex.getVariantType();
            if (!VariantSupport.unsupportedMessage.contains(vt)) {
                VariantSupport.logger.log(7, ex.getMessage());
                VariantSupport.unsupportedMessage.add(vt);
            }
        }
    }
    
    public boolean isSupportedType(final int variantType) {
        for (final int st : VariantSupport.SUPPORTED_TYPES) {
            if (variantType == st) {
                return true;
            }
        }
        return false;
    }
    
    public static Object read(final byte[] src, final int offset, final int length, final long type, final int codepage) throws ReadingNotSupportedException, UnsupportedEncodingException {
        final LittleEndianByteArrayInputStream lei = new LittleEndianByteArrayInputStream(src, offset);
        return read(lei, length, type, codepage);
    }
    
    public static Object read(final LittleEndianByteArrayInputStream lei, final int length, final long type, final int codepage) throws ReadingNotSupportedException, UnsupportedEncodingException {
        final int offset = lei.getReadIndex();
        final TypedPropertyValue typedPropertyValue = new TypedPropertyValue((int)type, null);
        try {
            typedPropertyValue.readValue(lei);
        }
        catch (final UnsupportedOperationException exc) {
            try {
                final byte[] v = IOUtils.toByteArray(lei, length, 100000);
                throw new ReadingNotSupportedException(type, v);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        switch ((int)type) {
            case 0:
            case 3:
            case 4:
            case 5:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21: {
                return typedPropertyValue.getValue();
            }
            case 2: {
                return typedPropertyValue.getValue();
            }
            case 64: {
                final Filetime filetime = (Filetime)typedPropertyValue.getValue();
                return filetime.getJavaValue();
            }
            case 30: {
                final CodePageString cpString = (CodePageString)typedPropertyValue.getValue();
                return cpString.getJavaValue(codepage);
            }
            case 31: {
                final UnicodeString uniString = (UnicodeString)typedPropertyValue.getValue();
                return uniString.toJavaString();
            }
            case 71: {
                final ClipboardData clipboardData = (ClipboardData)typedPropertyValue.getValue();
                return clipboardData.toByteArray();
            }
            case 11: {
                final VariantBool bool = (VariantBool)typedPropertyValue.getValue();
                return bool.getValue();
            }
            default: {
                final int unpadded = lei.getReadIndex() - offset;
                lei.setReadIndex(offset);
                final byte[] v2 = IOUtils.safelyAllocate(unpadded, 100000);
                lei.readFully(v2, 0, unpadded);
                throw new ReadingNotSupportedException(type, v2);
            }
        }
    }
    
    public static int write(final OutputStream out, final long type, final Object value, final int codepage) throws IOException, WritingNotSupportedException {
        int length = -1;
        switch ((int)type) {
            case 11: {
                if (value instanceof Boolean) {
                    final int bb = value ? 255 : 0;
                    out.write(bb);
                    out.write(bb);
                    length = 2;
                    break;
                }
                break;
            }
            case 30: {
                if (value instanceof String) {
                    final CodePageString codePageString = new CodePageString();
                    codePageString.setJavaValue((String)value, codepage);
                    length = codePageString.write(out);
                    break;
                }
                break;
            }
            case 31: {
                if (value instanceof String) {
                    final UnicodeString uniString = new UnicodeString();
                    uniString.setJavaValue((String)value);
                    length = uniString.write(out);
                    break;
                }
                break;
            }
            case 71: {
                if (value instanceof byte[]) {
                    final byte[] cf = (byte[])value;
                    out.write(cf);
                    length = cf.length;
                    break;
                }
                break;
            }
            case 0: {
                LittleEndian.putUInt(0L, out);
                length = 4;
                break;
            }
            case 2: {
                if (value instanceof Number) {
                    LittleEndian.putShort(out, ((Number)value).shortValue());
                    length = 2;
                    break;
                }
                break;
            }
            case 18: {
                if (value instanceof Number) {
                    LittleEndian.putUShort(((Number)value).intValue(), out);
                    length = 2;
                    break;
                }
                break;
            }
            case 3: {
                if (value instanceof Number) {
                    LittleEndian.putInt(((Number)value).intValue(), out);
                    length = 4;
                    break;
                }
                break;
            }
            case 19: {
                if (value instanceof Number) {
                    LittleEndian.putUInt(((Number)value).longValue(), out);
                    length = 4;
                    break;
                }
                break;
            }
            case 20: {
                if (value instanceof Number) {
                    LittleEndian.putLong(((Number)value).longValue(), out);
                    length = 8;
                    break;
                }
                break;
            }
            case 21: {
                if (!(value instanceof Number)) {
                    break;
                }
                final BigInteger bi = (BigInteger)((value instanceof BigInteger) ? value : BigInteger.valueOf(((Number)value).longValue()));
                if (bi.bitLength() > 64) {
                    throw new WritingNotSupportedException(type, value);
                }
                final byte[] biBytesBE = bi.toByteArray();
                final byte[] biBytesLE = new byte[8];
                int i = biBytesBE.length;
                for (final byte b : biBytesBE) {
                    if (i <= 8) {
                        biBytesLE[i - 1] = b;
                    }
                    --i;
                }
                out.write(biBytesLE);
                length = 8;
                break;
            }
            case 4: {
                if (value instanceof Number) {
                    final int floatBits = Float.floatToIntBits(((Number)value).floatValue());
                    LittleEndian.putInt(floatBits, out);
                    length = 4;
                    break;
                }
                break;
            }
            case 5: {
                if (value instanceof Number) {
                    LittleEndian.putDouble(((Number)value).doubleValue(), out);
                    length = 8;
                    break;
                }
                break;
            }
            case 64: {
                final Filetime filetimeValue = (value instanceof Date) ? new Filetime((Date)value) : new Filetime();
                length = filetimeValue.write(out);
                break;
            }
        }
        if (length == -1) {
            if (!(value instanceof byte[])) {
                throw new WritingNotSupportedException(type, value);
            }
            final byte[] b2 = (byte[])value;
            out.write(b2);
            length = b2.length;
            writeUnsupportedTypeMessage(new WritingNotSupportedException(type, value));
        }
        final int padding = 4 - (length & 0x3) & 0x3;
        out.write(VariantSupport.paddingBytes, 0, padding);
        return length + padding;
    }
    
    static {
        SUPPORTED_TYPES = new int[] { 0, 2, 3, 20, 5, 64, 30, 31, 71, 11 };
        logger = POILogFactory.getLogger(VariantSupport.class);
        paddingBytes = new byte[3];
    }
}
