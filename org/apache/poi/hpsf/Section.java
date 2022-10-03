package org.apache.poi.hpsf;

import org.apache.poi.util.POILogFactory;
import java.util.Arrays;
import java.util.TreeMap;
import java.io.InputStream;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.IOUtils;
import java.util.HashMap;
import org.apache.poi.util.LittleEndianOutputStream;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.io.UnsupportedEncodingException;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndian;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.apache.poi.util.POILogger;

public class Section
{
    private static final int MAX_RECORD_LENGTH = 100000;
    private static final POILogger LOG;
    private Map<Long, String> dictionary;
    private ClassID formatID;
    private final ByteArrayOutputStream sectionBytes;
    private final long _offset;
    private final Map<Long, Property> properties;
    private transient boolean wasNull;
    
    public Section() {
        this.sectionBytes = new ByteArrayOutputStream();
        this.properties = new LinkedHashMap<Long, Property>();
        this._offset = -1L;
    }
    
    public Section(final Section s) {
        this.sectionBytes = new ByteArrayOutputStream();
        this.properties = new LinkedHashMap<Long, Property>();
        this._offset = -1L;
        this.setFormatID(s.getFormatID());
        for (final Property p : s.properties.values()) {
            this.properties.put(p.getID(), new Property(p));
        }
        this.setDictionary(s.getDictionary());
    }
    
    public Section(final byte[] src, final int offset) throws UnsupportedEncodingException {
        this.sectionBytes = new ByteArrayOutputStream();
        this.properties = new LinkedHashMap<Long, Property>();
        this.formatID = new ClassID(src, offset);
        int offFix = (int)LittleEndian.getUInt(src, offset + 16);
        if (src[offFix] == 0) {
            for (int i = 0; i < 3 && src[offFix] == 0; ++i, ++offFix) {}
            for (int i = 0; i < 3 && (src[offFix + 3] != 0 || src[offFix + 7] != 0 || src[offFix + 11] != 0); ++i, --offFix) {}
        }
        this._offset = offFix;
        final LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(src, offFix);
        final int size = (int)Math.min(leis.readUInt(), src.length - this._offset);
        final int propertyCount = (int)leis.readUInt();
        final TreeBidiMap<Long, Long> offset2Id = (TreeBidiMap<Long, Long>)new TreeBidiMap();
        for (int j = 0; j < propertyCount; ++j) {
            final long id = leis.readUInt();
            final long off = leis.readUInt();
            offset2Id.put((Comparable)off, (Comparable)id);
        }
        final Long cpOffset = (Long)offset2Id.getKey((Object)1L);
        int codepage = -1;
        if (cpOffset != null) {
            leis.setReadIndex(Math.toIntExact(this._offset + cpOffset));
            final long type = leis.readUInt();
            if (type != 2L) {
                throw new HPSFRuntimeException("Value type of property ID 1 is not VT_I2 but " + type + ".");
            }
            codepage = leis.readUShort();
            this.setCodepage(codepage);
        }
        for (final Map.Entry<Long, Long> me : offset2Id.entrySet()) {
            final long off2 = me.getKey();
            long id2 = me.getValue();
            if (id2 == 1L) {
                continue;
            }
            final int pLen = propLen(offset2Id, off2, size);
            leis.setReadIndex(Math.toIntExact(this._offset + off2));
            if (id2 == 0L) {
                leis.mark(100000);
                if (this.readDictionary(leis, pLen, codepage)) {
                    continue;
                }
                leis.reset();
                try {
                    id2 = Math.max(31L, (long)offset2Id.inverseBidiMap().lastKey()) + 1L;
                    this.setProperty(new Property(id2, leis, pLen, codepage));
                }
                catch (final RuntimeException e) {
                    Section.LOG.log(3, "Dictionary fallback failed - ignoring property");
                }
            }
            else {
                this.setProperty(new Property(id2, leis, pLen, codepage));
            }
        }
        this.sectionBytes.write(src, Math.toIntExact(this._offset), size);
        this.padSectionBytes();
    }
    
    private static int propLen(final TreeBidiMap<Long, Long> offset2Id, final Long entryOffset, final long maxSize) {
        final Long nextKey = (Long)offset2Id.nextKey((Comparable)entryOffset);
        final long begin = entryOffset;
        final long end = (nextKey != null) ? nextKey : maxSize;
        return Math.toIntExact(end - begin);
    }
    
    public ClassID getFormatID() {
        return this.formatID;
    }
    
    public void setFormatID(final ClassID formatID) {
        this.formatID = formatID;
    }
    
    public void setFormatID(final byte[] formatID) {
        ClassID fid = this.getFormatID();
        if (fid == null) {
            fid = new ClassID();
            this.setFormatID(fid);
        }
        fid.setBytes(formatID);
    }
    
    public long getOffset() {
        return this._offset;
    }
    
    public int getPropertyCount() {
        return this.properties.size();
    }
    
    public Property[] getProperties() {
        return this.properties.values().toArray(new Property[0]);
    }
    
    public void setProperties(final Property[] properties) {
        this.properties.clear();
        for (final Property p : properties) {
            this.setProperty(p);
        }
    }
    
    public Object getProperty(final long id) {
        this.wasNull = !this.properties.containsKey(id);
        return this.wasNull ? null : this.properties.get(id).getValue();
    }
    
    public void setProperty(final int id, final String value) {
        this.setProperty(id, 30L, value);
    }
    
    public void setProperty(final int id, final int value) {
        this.setProperty(id, 3L, value);
    }
    
    public void setProperty(final int id, final long value) {
        this.setProperty(id, 20L, value);
    }
    
    public void setProperty(final int id, final boolean value) {
        this.setProperty(id, 11L, value);
    }
    
    public void setProperty(final int id, final long variantType, final Object value) {
        this.setProperty(new Property(id, variantType, value));
    }
    
    public void setProperty(final Property p) {
        final Property old = this.properties.get(p.getID());
        if (old == null || !old.equals(p)) {
            this.properties.put(p.getID(), p);
            this.sectionBytes.reset();
        }
    }
    
    public void setProperty(final int id, final Object value) {
        if (value instanceof String) {
            this.setProperty(id, (String)value);
        }
        else if (value instanceof Long) {
            this.setProperty(id, (long)value);
        }
        else if (value instanceof Integer) {
            this.setProperty(id, (int)value);
        }
        else if (value instanceof Short) {
            this.setProperty(id, (int)value);
        }
        else if (value instanceof Boolean) {
            this.setProperty(id, (boolean)value);
        }
        else {
            if (!(value instanceof Date)) {
                throw new HPSFRuntimeException("HPSF does not support properties of type " + value.getClass().getName() + ".");
            }
            this.setProperty(id, 64L, value);
        }
    }
    
    int getPropertyIntValue(final long id) {
        final Object o = this.getProperty(id);
        if (o == null) {
            return 0;
        }
        if (!(o instanceof Long) && !(o instanceof Integer)) {
            throw new HPSFRuntimeException("This property is not an integer type, but " + o.getClass().getName() + ".");
        }
        final Number i = (Number)o;
        return i.intValue();
    }
    
    boolean getPropertyBooleanValue(final int id) {
        final Boolean b = (Boolean)this.getProperty(id);
        return b != null && b;
    }
    
    protected void setPropertyBooleanValue(final int id, final boolean value) {
        this.setProperty(id, 11L, value);
    }
    
    public int getSize() {
        final int size = this.sectionBytes.size();
        if (size > 0) {
            return size;
        }
        try {
            return this.calcSize();
        }
        catch (final HPSFRuntimeException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new HPSFRuntimeException(ex2);
        }
    }
    
    private int calcSize() throws WritingNotSupportedException, IOException {
        this.sectionBytes.reset();
        this.write(this.sectionBytes);
        this.padSectionBytes();
        return this.sectionBytes.size();
    }
    
    private void padSectionBytes() {
        final byte[] padArray = { 0, 0, 0 };
        final int pad = 4 - (this.sectionBytes.size() & 0x3) & 0x3;
        this.sectionBytes.write(padArray, 0, pad);
    }
    
    public boolean wasNull() {
        return this.wasNull;
    }
    
    public String getPIDString(final long pid) {
        Map<Long, String> dic = this.getDictionary();
        if (dic == null || !dic.containsKey(pid)) {
            final ClassID fmt = this.getFormatID();
            if (SummaryInformation.FORMAT_ID.equals(fmt)) {
                dic = PropertyIDMap.getSummaryInformationProperties();
            }
            else if (DocumentSummaryInformation.FORMAT_ID[0].equals(fmt)) {
                dic = PropertyIDMap.getDocumentSummaryInformationProperties();
            }
        }
        return (dic != null && dic.containsKey(pid)) ? dic.get(pid) : "[undefined]";
    }
    
    public void clear() {
        for (final Property p : this.getProperties()) {
            this.removeProperty(p.getID());
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Section)) {
            return false;
        }
        final Section s = (Section)o;
        if (!s.getFormatID().equals(this.getFormatID())) {
            return false;
        }
        final Set<Long> propIds = new HashSet<Long>(this.properties.keySet());
        propIds.addAll(s.properties.keySet());
        propIds.remove(0L);
        propIds.remove(1L);
        for (final Long id : propIds) {
            final Property p1 = this.properties.get(id);
            final Property p2 = s.properties.get(id);
            if (p1 == null || !p1.equals(p2)) {
                return false;
            }
        }
        final Map<Long, String> d1 = this.getDictionary();
        final Map<Long, String> d2 = s.getDictionary();
        return (d1 == null && d2 == null) || (d1 != null && d1.equals(d2));
    }
    
    public void removeProperty(final long id) {
        if (this.properties.remove(id) != null) {
            this.sectionBytes.reset();
        }
    }
    
    public int write(final OutputStream out) throws WritingNotSupportedException, IOException {
        if (this.sectionBytes.size() > 0) {
            this.sectionBytes.writeTo(out);
            return this.sectionBytes.size();
        }
        int codepage = this.getCodepage();
        if (codepage == -1) {
            final String msg = "The codepage property is not set although a dictionary is present. Defaulting to ISO-8859-1.";
            Section.LOG.log(5, msg);
            codepage = 1252;
        }
        final int[][] offsets = new int[this.properties.size()][2];
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final LittleEndianOutputStream leos = new LittleEndianOutputStream(bos);
        leos.writeInt(-1);
        leos.writeInt(this.properties.size());
        int propCnt = 0;
        for (final Property p : this.properties.values()) {
            leos.writeUInt(p.getID());
            offsets[propCnt++][0] = bos.size();
            leos.writeInt(-1);
        }
        propCnt = 0;
        for (final Property p : this.properties.values()) {
            offsets[propCnt++][1] = bos.size();
            if (p.getID() != 0L) {
                p.write(bos, codepage);
            }
            else {
                this.writeDictionary(bos, codepage);
            }
        }
        final byte[] result = bos.toByteArray();
        LittleEndian.putInt(result, 0, bos.size());
        for (final int[] off : offsets) {
            LittleEndian.putUInt(result, off[0], off[1]);
        }
        out.write(result);
        return bos.size();
    }
    
    private boolean readDictionary(final LittleEndianByteArrayInputStream leis, final int length, final int codepage) {
        final Map<Long, String> dic = new HashMap<Long, String>();
        final long nrEntries = leis.readUInt();
        long id = -1L;
        boolean isCorrupted = false;
        for (int i = 0; i < nrEntries; ++i) {
            final String errMsg = "The property set's dictionary contains bogus data. All dictionary entries starting with the one with ID " + id + " will be ignored.";
            id = leis.readUInt();
            final long sLength = leis.readUInt();
            final int cp = (codepage == -1) ? 1252 : codepage;
            final int nrBytes = Math.toIntExact((sLength - 1L) * ((cp == 1200) ? 2 : 1));
            if (nrBytes > 16777215) {
                Section.LOG.log(5, errMsg);
                isCorrupted = true;
                break;
            }
            try {
                final byte[] buf = IOUtils.safelyAllocate(nrBytes, 100000);
                leis.readFully(buf, 0, nrBytes);
                final String str = CodePageUtil.getStringFromCodePage(buf, 0, nrBytes, cp);
                int pad = 1;
                if (cp == 1200) {
                    pad = 2 + (4 - (nrBytes + 2 & 0x3) & 0x3);
                }
                IOUtils.skipFully(leis, pad);
                dic.put(id, str);
            }
            catch (final RuntimeException | IOException ex) {
                Section.LOG.log(5, errMsg, ex);
                isCorrupted = true;
                break;
            }
        }
        this.setDictionary(dic);
        return !isCorrupted;
    }
    
    private void writeDictionary(final OutputStream out, final int codepage) throws IOException {
        final byte[] padding = new byte[4];
        final Map<Long, String> dic = this.getDictionary();
        LittleEndian.putUInt(dic.size(), out);
        int length = 4;
        for (final Map.Entry<Long, String> ls : dic.entrySet()) {
            LittleEndian.putUInt(ls.getKey(), out);
            length += 4;
            final String value = ls.getValue() + "\u0000";
            final byte[] bytes = CodePageUtil.getBytesInCodePage(value, codepage);
            final int len = (codepage == 1200) ? value.length() : bytes.length;
            LittleEndian.putUInt(len, out);
            length += 4;
            out.write(bytes);
            length += bytes.length;
            final int pad = (codepage == 1200) ? (4 - (length & 0x3) & 0x3) : 0;
            out.write(padding, 0, pad);
            length += pad;
        }
        final int pad2 = 4 - (length & 0x3) & 0x3;
        out.write(padding, 0, pad2);
    }
    
    public void setDictionary(final Map<Long, String> dictionary) throws IllegalPropertySetDataException {
        if (dictionary != null) {
            if (this.dictionary == null) {
                this.dictionary = new TreeMap<Long, String>();
            }
            this.dictionary.putAll(dictionary);
            final int cp = this.getCodepage();
            if (cp == -1) {
                this.setCodepage(1252);
            }
            this.setProperty(0, -1L, dictionary);
        }
        else {
            this.removeProperty(0L);
            this.dictionary = null;
        }
    }
    
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[] { this.getFormatID(), this.getProperties() });
    }
    
    @Override
    public String toString() {
        return this.toString(null);
    }
    
    public String toString(final PropertyIDMap idMap) {
        final StringBuilder b = new StringBuilder();
        final Property[] pa = this.getProperties();
        b.append("\n\n\n");
        b.append(this.getClass().getName());
        b.append('[');
        b.append("formatID: ");
        b.append(this.getFormatID());
        b.append(", offset: ");
        b.append(this.getOffset());
        b.append(", propertyCount: ");
        b.append(this.getPropertyCount());
        b.append(", size: ");
        b.append(this.getSize());
        b.append(", properties: [\n");
        int codepage = this.getCodepage();
        if (codepage == -1) {
            codepage = 1252;
        }
        for (final Property p : pa) {
            b.append(p.toString(codepage, idMap));
            b.append(",\n");
        }
        b.append(']');
        b.append(']');
        return b.toString();
    }
    
    public Map<Long, String> getDictionary() {
        if (this.dictionary == null) {
            this.dictionary = (Map)this.getProperty(0L);
        }
        return this.dictionary;
    }
    
    public int getCodepage() {
        final Integer codepage = (Integer)this.getProperty(1L);
        return (codepage == null) ? -1 : codepage;
    }
    
    public void setCodepage(final int codepage) {
        this.setProperty(1, 2L, codepage);
    }
    
    static {
        LOG = POILogFactory.getLogger(Section.class);
    }
}
