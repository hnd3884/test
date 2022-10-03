package org.apache.poi.hpsf;

import org.apache.poi.util.NotImplemented;
import java.util.Collection;
import org.apache.poi.util.Removal;
import org.apache.poi.util.CodePageUtil;
import java.io.ByteArrayInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.util.LittleEndianOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.EmptyFileException;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import java.util.Collections;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import org.apache.poi.util.IOUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PropertySet
{
    public static final int OS_WIN16 = 0;
    public static final int OS_MACINTOSH = 1;
    public static final int OS_WIN32 = 2;
    static final int BYTE_ORDER_ASSERTION = 65534;
    static final int FORMAT_ASSERTION = 0;
    static final int OFFSET_HEADER = 28;
    private int byteOrder;
    private int format;
    private int osVersion;
    private ClassID classID;
    private final List<Section> sections;
    
    public PropertySet() {
        this.sections = new ArrayList<Section>();
        this.byteOrder = 65534;
        this.format = 0;
        this.osVersion = 133636;
        this.classID = new ClassID();
        this.addSection(new Section());
    }
    
    public PropertySet(final InputStream stream) throws NoPropertySetStreamException, IOException {
        this.sections = new ArrayList<Section>();
        if (!isPropertySetStream(stream)) {
            throw new NoPropertySetStreamException();
        }
        final byte[] buffer = IOUtils.toByteArray(stream);
        this.init(buffer, 0, buffer.length);
    }
    
    public PropertySet(final byte[] stream, final int offset, final int length) throws NoPropertySetStreamException, UnsupportedEncodingException {
        this.sections = new ArrayList<Section>();
        if (!isPropertySetStream(stream, offset, length)) {
            throw new NoPropertySetStreamException();
        }
        this.init(stream, offset, length);
    }
    
    public PropertySet(final byte[] stream) throws NoPropertySetStreamException, UnsupportedEncodingException {
        this(stream, 0, stream.length);
    }
    
    public PropertySet(final PropertySet ps) {
        this.sections = new ArrayList<Section>();
        this.setByteOrder(ps.getByteOrder());
        this.setFormat(ps.getFormat());
        this.setOSVersion(ps.getOSVersion());
        this.setClassID(ps.getClassID());
        for (final Section section : ps.getSections()) {
            this.sections.add(new Section(section));
        }
    }
    
    public int getByteOrder() {
        return this.byteOrder;
    }
    
    public void setByteOrder(final int byteOrder) {
        this.byteOrder = byteOrder;
    }
    
    public int getFormat() {
        return this.format;
    }
    
    public void setFormat(final int format) {
        this.format = format;
    }
    
    public int getOSVersion() {
        return this.osVersion;
    }
    
    public void setOSVersion(final int osVersion) {
        this.osVersion = osVersion;
    }
    
    public ClassID getClassID() {
        return this.classID;
    }
    
    public void setClassID(final ClassID classID) {
        this.classID = classID;
    }
    
    public int getSectionCount() {
        return this.sections.size();
    }
    
    public List<Section> getSections() {
        return Collections.unmodifiableList((List<? extends Section>)this.sections);
    }
    
    public void addSection(final Section section) {
        this.sections.add(section);
    }
    
    public void clearSections() {
        this.sections.clear();
    }
    
    public PropertyIDMap getPropertySetIDMap() {
        return null;
    }
    
    public static boolean isPropertySetStream(final InputStream stream) throws IOException {
        final int BUFFER_SIZE = 50;
        try {
            final byte[] buffer = IOUtils.peekFirstNBytes(stream, 50);
            return isPropertySetStream(buffer, 0, buffer.length);
        }
        catch (final EmptyFileException e) {
            return false;
        }
    }
    
    public static boolean isPropertySetStream(final byte[] src, final int offset, final int length) {
        final LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(src, offset, length);
        try {
            final int byteOrder = leis.readUShort();
            if (byteOrder != 65534) {
                return false;
            }
            final int format = leis.readUShort();
            if (format != 0) {
                return false;
            }
            final long osVersion = leis.readUInt();
            if (leis.skip(16L) != 16L) {
                return false;
            }
            final long sectionCount = leis.readUInt();
            return sectionCount >= 0L;
        }
        catch (final RuntimeException e) {
            return false;
        }
    }
    
    private void init(final byte[] src, final int offset, final int length) throws UnsupportedEncodingException {
        int o = offset;
        this.byteOrder = LittleEndian.getUShort(src, o);
        o += 2;
        this.format = LittleEndian.getUShort(src, o);
        o += 2;
        this.osVersion = (int)LittleEndian.getUInt(src, o);
        o += 4;
        this.classID = new ClassID(src, o);
        o += 16;
        final int sectionCount = LittleEndian.getInt(src, o);
        o += 4;
        if (sectionCount < 0) {
            throw new HPSFRuntimeException("Section count " + sectionCount + " is negative.");
        }
        for (int i = 0; i < sectionCount; ++i) {
            final Section s = new Section(src, o);
            o += 20;
            this.sections.add(s);
        }
    }
    
    public void write(final OutputStream out) throws IOException, WritingNotSupportedException {
        out.write(this.toBytes());
        out.close();
    }
    
    private byte[] toBytes() throws WritingNotSupportedException, IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final LittleEndianOutputStream leos = new LittleEndianOutputStream(bos);
        final int nrSections = this.getSectionCount();
        leos.writeShort(this.getByteOrder());
        leos.writeShort(this.getFormat());
        leos.writeInt(this.getOSVersion());
        putClassId(bos, this.getClassID());
        leos.writeInt(nrSections);
        assert bos.size() == 28;
        final int[][] offsets = new int[this.getSectionCount()][2];
        int secCnt = 0;
        for (final Section section : this.getSections()) {
            final ClassID formatID = section.getFormatID();
            if (formatID == null) {
                throw new NoFormatIDException();
            }
            putClassId(bos, formatID);
            offsets[secCnt++][0] = bos.size();
            leos.writeInt(-1);
        }
        secCnt = 0;
        for (final Section section : this.getSections()) {
            offsets[secCnt++][1] = bos.size();
            section.write(bos);
        }
        final byte[] result = bos.toByteArray();
        for (final int[] off : offsets) {
            LittleEndian.putInt(result, off[0], off[1]);
        }
        return result;
    }
    
    public void write(final DirectoryEntry dir, final String name) throws WritingNotSupportedException, IOException {
        if (dir.hasEntry(name)) {
            final Entry e = dir.getEntry(name);
            e.delete();
        }
        dir.createDocument(name, this.toInputStream());
    }
    
    public InputStream toInputStream() throws WritingNotSupportedException, IOException {
        return new ByteArrayInputStream(this.toBytes());
    }
    
    String getPropertyStringValue(final int propertyId) {
        final Object propertyValue = this.getProperty(propertyId);
        return getPropertyStringValue(propertyValue);
    }
    
    public static String getPropertyStringValue(final Object propertyValue) {
        if (propertyValue == null) {
            return null;
        }
        if (propertyValue instanceof String) {
            return (String)propertyValue;
        }
        if (propertyValue instanceof byte[]) {
            final byte[] b = (byte[])propertyValue;
            switch (b.length) {
                case 0: {
                    return "";
                }
                case 1: {
                    return Byte.toString(b[0]);
                }
                case 2: {
                    return Integer.toString(LittleEndian.getUShort(b));
                }
                case 4: {
                    return Long.toString(LittleEndian.getUInt(b));
                }
                default: {
                    try {
                        return CodePageUtil.getStringFromCodePage(b, 1252);
                    }
                    catch (final UnsupportedEncodingException e) {
                        return "";
                    }
                    break;
                }
            }
        }
        return propertyValue.toString();
    }
    
    public boolean isSummaryInformation() {
        return !this.sections.isEmpty() && matchesSummary(this.getFirstSection().getFormatID(), SummaryInformation.FORMAT_ID);
    }
    
    public boolean isDocumentSummaryInformation() {
        return !this.sections.isEmpty() && matchesSummary(this.getFirstSection().getFormatID(), DocumentSummaryInformation.FORMAT_ID);
    }
    
    static boolean matchesSummary(final ClassID actual, final ClassID... expected) {
        for (final ClassID sum : expected) {
            if (sum.equals(actual) || sum.equalsInverted(actual)) {
                return true;
            }
        }
        return false;
    }
    
    public Property[] getProperties() throws NoSingleSectionException {
        return this.getFirstSection().getProperties();
    }
    
    protected Object getProperty(final int id) throws NoSingleSectionException {
        return this.getFirstSection().getProperty(id);
    }
    
    boolean getPropertyBooleanValue(final int id) throws NoSingleSectionException {
        return this.getFirstSection().getPropertyBooleanValue(id);
    }
    
    int getPropertyIntValue(final int id) throws NoSingleSectionException {
        return this.getFirstSection().getPropertyIntValue(id);
    }
    
    public boolean wasNull() throws NoSingleSectionException {
        return this.getFirstSection().wasNull();
    }
    
    public Section getFirstSection() {
        if (this.sections.isEmpty()) {
            throw new MissingSectionException("Property set does not contain any sections.");
        }
        return this.sections.get(0);
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public Section getSingleSection() {
        final int sectionCount = this.getSectionCount();
        if (sectionCount != 1) {
            throw new NoSingleSectionException("Property set contains " + sectionCount + " sections.");
        }
        return this.sections.get(0);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PropertySet)) {
            return false;
        }
        final PropertySet ps = (PropertySet)o;
        final int byteOrder1 = ps.getByteOrder();
        final int byteOrder2 = this.getByteOrder();
        final ClassID classID1 = ps.getClassID();
        final ClassID classID2 = this.getClassID();
        final int format1 = ps.getFormat();
        final int format2 = this.getFormat();
        final int osVersion1 = ps.getOSVersion();
        final int osVersion2 = this.getOSVersion();
        final int sectionCount1 = ps.getSectionCount();
        final int sectionCount2 = this.getSectionCount();
        return byteOrder1 == byteOrder2 && classID1.equals(classID2) && format1 == format2 && osVersion1 == osVersion2 && sectionCount1 == sectionCount2 && this.getSections().containsAll(ps.getSections());
    }
    
    @NotImplemented
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("FIXME: Not yet implemented.");
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        final int sectionCount = this.getSectionCount();
        b.append(this.getClass().getName());
        b.append('[');
        b.append("byteOrder: ");
        b.append(this.getByteOrder());
        b.append(", classID: ");
        b.append(this.getClassID());
        b.append(", format: ");
        b.append(this.getFormat());
        b.append(", OSVersion: ");
        b.append(this.getOSVersion());
        b.append(", sectionCount: ");
        b.append(sectionCount);
        b.append(", sections: [\n");
        for (final Section section : this.getSections()) {
            b.append(section.toString(this.getPropertySetIDMap()));
        }
        b.append(']');
        b.append(']');
        return b.toString();
    }
    
    void remove1stProperty(final long id) {
        this.getFirstSection().removeProperty(id);
    }
    
    void set1stProperty(final long id, final String value) {
        this.getFirstSection().setProperty((int)id, value);
    }
    
    void set1stProperty(final long id, final int value) {
        this.getFirstSection().setProperty((int)id, value);
    }
    
    void set1stProperty(final long id, final boolean value) {
        this.getFirstSection().setProperty((int)id, value);
    }
    
    void set1stProperty(final long id, final byte[] value) {
        this.getFirstSection().setProperty((int)id, value);
    }
    
    private static void putClassId(final ByteArrayOutputStream out, final ClassID n) {
        final byte[] b = new byte[16];
        n.write(b, 0);
        out.write(b, 0, b.length);
    }
}
