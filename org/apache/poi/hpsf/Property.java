package org.apache.poi.hpsf;

import org.apache.poi.util.POILogFactory;
import java.nio.charset.Charset;
import org.apache.poi.util.CodePageUtil;
import java.text.DateFormat;
import org.apache.poi.util.LocaleUtil;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import org.apache.poi.util.HexDump;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import java.util.Objects;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndian;
import java.io.UnsupportedEncodingException;
import org.apache.poi.util.POILogger;

public class Property
{
    public static final int DEFAULT_CODEPAGE = 1252;
    private static final POILogger LOG;
    private long id;
    private long type;
    private Object value;
    
    public Property() {
    }
    
    public Property(final Property p) {
        this(p.id, p.type, p.value);
    }
    
    public Property(final long id, final long type, final Object value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }
    
    public Property(final long id, final byte[] src, final long offset, final int length, final int codepage) throws UnsupportedEncodingException {
        this.id = id;
        if (id == 0L) {
            throw new UnsupportedEncodingException("Dictionary not allowed here");
        }
        int o = (int)offset;
        this.type = LittleEndian.getUInt(src, o);
        o += 4;
        try {
            this.value = VariantSupport.read(src, o, length, (int)this.type, codepage);
        }
        catch (final UnsupportedVariantTypeException ex) {
            VariantSupport.writeUnsupportedTypeMessage(ex);
            this.value = ex.getValue();
        }
    }
    
    public Property(final long id, final LittleEndianByteArrayInputStream leis, final int length, final int codepage) throws UnsupportedEncodingException {
        this.id = id;
        if (id == 0L) {
            throw new UnsupportedEncodingException("Dictionary not allowed here");
        }
        this.type = leis.readUInt();
        try {
            this.value = VariantSupport.read(leis, length, (int)this.type, codepage);
        }
        catch (final UnsupportedVariantTypeException ex) {
            VariantSupport.writeUnsupportedTypeMessage(ex);
            this.value = ex.getValue();
        }
    }
    
    public long getID() {
        return this.id;
    }
    
    public void setID(final long id) {
        this.id = id;
    }
    
    public long getType() {
        return this.type;
    }
    
    public void setType(final long type) {
        this.type = type;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    protected int getSize(final int property) throws WritingNotSupportedException {
        int length = Variant.getVariantLength(this.type);
        if (length >= 0 || this.type == 0L) {
            return length;
        }
        if (length == -2) {
            throw new WritingNotSupportedException(this.type, null);
        }
        if (this.type == 30L || this.type == 31L) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                length = this.write(bos, property) - 8;
                length += (4 - (length & 0x3) & 0x3);
                return length;
            }
            catch (final IOException e) {
                throw new WritingNotSupportedException(this.type, this.value);
            }
        }
        throw new WritingNotSupportedException(this.type, this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Property)) {
            return false;
        }
        final Property p = (Property)o;
        final Object pValue = p.getValue();
        final long pId = p.getID();
        if (this.id != pId || (this.id != 0L && !this.typesAreEqual(this.type, p.getType()))) {
            return false;
        }
        if (this.value == null && pValue == null) {
            return true;
        }
        if (this.value == null || pValue == null) {
            return false;
        }
        final Class<?> valueClass = this.value.getClass();
        final Class<?> pValueClass = pValue.getClass();
        if (!valueClass.isAssignableFrom(pValueClass) && !pValueClass.isAssignableFrom(valueClass)) {
            return false;
        }
        if (!(this.value instanceof byte[])) {
            return this.value.equals(pValue);
        }
        final byte[] thisVal = (byte[])this.value;
        final byte[] otherVal = (byte[])pValue;
        final int len = unpaddedLength(thisVal);
        if (len != unpaddedLength(otherVal)) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (thisVal[i] != otherVal[i]) {
                return false;
            }
        }
        return true;
    }
    
    private static int unpaddedLength(final byte[] buf) {
        final int end = buf.length - (buf.length + 3) % 4;
        for (int i = buf.length; i > end; --i) {
            if (buf[i - 1] != 0) {
                return i;
            }
        }
        return end;
    }
    
    private boolean typesAreEqual(final long t1, final long t2) {
        return t1 == t2 || (t1 == 30L && t2 == 31L) || (t2 == 30L && t1 == 31L);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.type, this.value);
    }
    
    @Override
    public String toString() {
        return this.toString(1252, null);
    }
    
    public String toString(final int codepage, final PropertyIDMap idMap) {
        final StringBuilder b = new StringBuilder();
        b.append("Property[");
        b.append("id: ");
        b.append(this.id);
        String idName = (idMap == null) ? null : idMap.get((Object)this.id);
        if (idName == null) {
            idName = PropertyIDMap.getFallbackProperties().get((Object)this.id);
        }
        if (idName != null) {
            b.append(" (");
            b.append(idName);
            b.append(")");
        }
        b.append(", type: ");
        b.append(this.getType());
        b.append(" (");
        b.append(this.getVariantName());
        b.append(") ");
        final Object value = this.getValue();
        b.append(", value: ");
        if (value instanceof String) {
            b.append((String)value);
            b.append("\n");
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                this.write(bos, codepage);
            }
            catch (final Exception e) {
                Property.LOG.log(5, "can't serialize string", e);
            }
            if (bos.size() > 8) {
                final String hex = HexDump.dump(bos.toByteArray(), -8L, 8);
                b.append(hex);
            }
        }
        else if (value instanceof byte[]) {
            b.append("\n");
            final byte[] bytes = (byte[])value;
            if (bytes.length > 0) {
                final String hex = HexDump.dump(bytes, 0L, 0);
                b.append(hex);
            }
        }
        else if (value instanceof Date) {
            final Date d = (Date)value;
            final long filetime = Filetime.dateToFileTime(d);
            if (Filetime.isUndefined(d)) {
                b.append("<undefined>");
            }
            else if (filetime >>> 32 == 0L) {
                long l = filetime * 100L;
                final TimeUnit tu = TimeUnit.NANOSECONDS;
                final long hr = tu.toHours(l);
                l -= TimeUnit.HOURS.toNanos(hr);
                final long min = tu.toMinutes(l);
                l -= TimeUnit.MINUTES.toNanos(min);
                final long sec = tu.toSeconds(l);
                l -= TimeUnit.SECONDS.toNanos(sec);
                final long ms = tu.toMillis(l);
                final String str = String.format(Locale.ROOT, "%02d:%02d:%02d.%03d", hr, min, sec, ms);
                b.append(str);
            }
            else {
                final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT);
                df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
                b.append(df.format(d));
            }
        }
        else if (this.type == 0L || this.type == 1L || value == null) {
            b.append("null");
        }
        else {
            b.append(value);
            final String decoded = this.decodeValueFromID();
            if (decoded != null) {
                b.append(" (");
                b.append(decoded);
                b.append(")");
            }
        }
        b.append(']');
        return b.toString();
    }
    
    private String getVariantName() {
        if (this.getID() == 0L) {
            return "dictionary";
        }
        return Variant.getVariantName(this.getType());
    }
    
    private String decodeValueFromID() {
        try {
            switch ((int)this.getID()) {
                case 1: {
                    return CodePageUtil.codepageToEncoding(((Number)this.value).intValue());
                }
                case Integer.MIN_VALUE: {
                    return LocaleUtil.getLocaleFromLCID(((Number)this.value).intValue());
                }
            }
        }
        catch (final Exception e) {
            Property.LOG.log(5, "Can't decode id " + this.getID());
        }
        return null;
    }
    
    public int write(final OutputStream out, final int codepage) throws IOException, WritingNotSupportedException {
        int length = 0;
        long variantType = this.getType();
        if (variantType == 30L && codepage != 1200) {
            final String csStr = CodePageUtil.codepageToEncoding((codepage > 0) ? codepage : 1252);
            if (!Charset.forName(csStr).newEncoder().canEncode((CharSequence)this.value)) {
                variantType = 31L;
            }
        }
        LittleEndian.putUInt(variantType, out);
        length += 4;
        length += VariantSupport.write(out, variantType, this.getValue(), codepage);
        return length;
    }
    
    static {
        LOG = POILogFactory.getLogger(Property.class);
    }
}
