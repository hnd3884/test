package org.apache.poi.ddf;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.common.Duplicatable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import java.util.Map;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.io.ByteArrayInputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogger;

public final class EscherMetafileBlip extends EscherBlipRecord
{
    private static final POILogger log;
    private static final int MAX_RECORD_LENGTH = 100000000;
    public static final short RECORD_ID_EMF;
    public static final short RECORD_ID_WMF;
    public static final short RECORD_ID_PICT;
    private static final int HEADER_SIZE = 8;
    private final byte[] field_1_UID;
    private final byte[] field_2_UID;
    private int field_2_cb;
    private int field_3_rcBounds_x1;
    private int field_3_rcBounds_y1;
    private int field_3_rcBounds_x2;
    private int field_3_rcBounds_y2;
    private int field_4_ptSize_w;
    private int field_4_ptSize_h;
    private int field_5_cbSave;
    private byte field_6_fCompression;
    private byte field_7_fFilter;
    private byte[] raw_pictureData;
    private byte[] remainingData;
    
    public EscherMetafileBlip() {
        this.field_1_UID = new byte[16];
        this.field_2_UID = new byte[16];
    }
    
    public EscherMetafileBlip(final EscherMetafileBlip other) {
        super(other);
        this.field_1_UID = new byte[16];
        this.field_2_UID = new byte[16];
        System.arraycopy(other.field_1_UID, 0, this.field_1_UID, 0, this.field_1_UID.length);
        System.arraycopy(other.field_2_UID, 0, this.field_2_UID, 0, this.field_2_UID.length);
        this.field_2_cb = other.field_2_cb;
        this.field_3_rcBounds_x1 = other.field_3_rcBounds_x1;
        this.field_3_rcBounds_y1 = other.field_3_rcBounds_y1;
        this.field_3_rcBounds_x2 = other.field_3_rcBounds_x2;
        this.field_3_rcBounds_y2 = other.field_3_rcBounds_y2;
        this.field_4_ptSize_h = other.field_4_ptSize_h;
        this.field_4_ptSize_w = other.field_4_ptSize_w;
        this.field_5_cbSave = other.field_5_cbSave;
        this.field_6_fCompression = other.field_6_fCompression;
        this.field_7_fFilter = other.field_7_fFilter;
        this.raw_pictureData = (byte[])((other.raw_pictureData == null) ? null : ((byte[])other.raw_pictureData.clone()));
        this.remainingData = (byte[])((other.remainingData == null) ? null : ((byte[])other.remainingData.clone()));
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        final int bytesAfterHeader = this.readHeader(data, offset);
        int pos = offset + 8;
        System.arraycopy(data, pos, this.field_1_UID, 0, 16);
        pos += 16;
        if ((this.getOptions() ^ this.getSignature()) == 0x10) {
            System.arraycopy(data, pos, this.field_2_UID, 0, 16);
            pos += 16;
        }
        this.field_2_cb = LittleEndian.getInt(data, pos);
        pos += 4;
        this.field_3_rcBounds_x1 = LittleEndian.getInt(data, pos);
        pos += 4;
        this.field_3_rcBounds_y1 = LittleEndian.getInt(data, pos);
        pos += 4;
        this.field_3_rcBounds_x2 = LittleEndian.getInt(data, pos);
        pos += 4;
        this.field_3_rcBounds_y2 = LittleEndian.getInt(data, pos);
        pos += 4;
        this.field_4_ptSize_w = LittleEndian.getInt(data, pos);
        pos += 4;
        this.field_4_ptSize_h = LittleEndian.getInt(data, pos);
        pos += 4;
        this.field_5_cbSave = LittleEndian.getInt(data, pos);
        pos += 4;
        this.field_6_fCompression = data[pos];
        ++pos;
        this.field_7_fFilter = data[pos];
        ++pos;
        System.arraycopy(data, pos, this.raw_pictureData = IOUtils.safelyAllocate(this.field_5_cbSave, 100000000), 0, this.field_5_cbSave);
        pos += this.field_5_cbSave;
        if (this.field_6_fCompression == 0) {
            super.setPictureData(inflatePictureData(this.raw_pictureData));
        }
        else {
            super.setPictureData(this.raw_pictureData);
        }
        final int remaining = bytesAfterHeader - pos + offset + 8;
        if (remaining > 0) {
            System.arraycopy(data, pos, this.remainingData = IOUtils.safelyAllocate(remaining, 100000000), 0, remaining);
        }
        return bytesAfterHeader + 8;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        int pos = offset;
        LittleEndian.putShort(data, pos, this.getOptions());
        pos += 2;
        LittleEndian.putShort(data, pos, this.getRecordId());
        pos += 2;
        LittleEndian.putInt(data, pos, this.getRecordSize() - 8);
        pos += 4;
        System.arraycopy(this.field_1_UID, 0, data, pos, this.field_1_UID.length);
        pos += this.field_1_UID.length;
        if ((this.getOptions() ^ this.getSignature()) == 0x10) {
            System.arraycopy(this.field_2_UID, 0, data, pos, this.field_2_UID.length);
            pos += this.field_2_UID.length;
        }
        LittleEndian.putInt(data, pos, this.field_2_cb);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_3_rcBounds_x1);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_3_rcBounds_y1);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_3_rcBounds_x2);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_3_rcBounds_y2);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_4_ptSize_w);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_4_ptSize_h);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_5_cbSave);
        pos += 4;
        data[pos] = this.field_6_fCompression;
        ++pos;
        data[pos] = this.field_7_fFilter;
        ++pos;
        System.arraycopy(this.raw_pictureData, 0, data, pos, this.raw_pictureData.length);
        pos += this.raw_pictureData.length;
        if (this.remainingData != null) {
            System.arraycopy(this.remainingData, 0, data, pos, this.remainingData.length);
            pos += this.remainingData.length;
        }
        listener.afterRecordSerialize(offset + this.getRecordSize(), this.getRecordId(), this.getRecordSize(), this);
        return this.getRecordSize();
    }
    
    private static byte[] inflatePictureData(final byte[] data) {
        try {
            final InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(data));
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final byte[] buf = new byte[4096];
            int readBytes;
            while ((readBytes = in.read(buf)) > 0) {
                out.write(buf, 0, readBytes);
            }
            return out.toByteArray();
        }
        catch (final IOException e) {
            EscherMetafileBlip.log.log(5, "Possibly corrupt compression or non-compressed data", e);
            return data;
        }
    }
    
    @Override
    public int getRecordSize() {
        int size = 58 + this.raw_pictureData.length;
        if (this.remainingData != null) {
            size += this.remainingData.length;
        }
        if ((this.getOptions() ^ this.getSignature()) == 0x10) {
            size += this.field_2_UID.length;
        }
        return size;
    }
    
    public byte[] getUID() {
        return this.field_1_UID;
    }
    
    public void setUID(final byte[] uid) {
        if (uid == null || uid.length != 16) {
            throw new IllegalArgumentException("uid must be byte[16]");
        }
        System.arraycopy(uid, 0, this.field_1_UID, 0, this.field_1_UID.length);
    }
    
    public byte[] getPrimaryUID() {
        return this.field_2_UID;
    }
    
    public void setPrimaryUID(final byte[] primaryUID) {
        if (primaryUID == null || primaryUID.length != 16) {
            throw new IllegalArgumentException("primaryUID must be byte[16]");
        }
        System.arraycopy(primaryUID, 0, this.field_2_UID, 0, this.field_2_UID.length);
    }
    
    public int getUncompressedSize() {
        return this.field_2_cb;
    }
    
    public void setUncompressedSize(final int uncompressedSize) {
        this.field_2_cb = uncompressedSize;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(this.field_3_rcBounds_x1, this.field_3_rcBounds_y1, this.field_3_rcBounds_x2 - this.field_3_rcBounds_x1, this.field_3_rcBounds_y2 - this.field_3_rcBounds_y1);
    }
    
    public void setBounds(final Rectangle bounds) {
        this.field_3_rcBounds_x1 = bounds.x;
        this.field_3_rcBounds_y1 = bounds.y;
        this.field_3_rcBounds_x2 = bounds.x + bounds.width;
        this.field_3_rcBounds_y2 = bounds.y + bounds.height;
    }
    
    public Dimension getSizeEMU() {
        return new Dimension(this.field_4_ptSize_w, this.field_4_ptSize_h);
    }
    
    public void setSizeEMU(final Dimension sizeEMU) {
        this.field_4_ptSize_w = sizeEMU.width;
        this.field_4_ptSize_h = sizeEMU.height;
    }
    
    public int getCompressedSize() {
        return this.field_5_cbSave;
    }
    
    public void setCompressedSize(final int compressedSize) {
        this.field_5_cbSave = compressedSize;
    }
    
    public boolean isCompressed() {
        return this.field_6_fCompression == 0;
    }
    
    public void setCompressed(final boolean compressed) {
        this.field_6_fCompression = (byte)(compressed ? 0 : -2);
    }
    
    public byte getFilter() {
        return this.field_7_fFilter;
    }
    
    public void setFilter(final byte filter) {
        this.field_7_fFilter = filter;
    }
    
    public byte[] getRemainingData() {
        return this.remainingData;
    }
    
    public short getSignature() {
        switch (EscherRecordTypes.forTypeID(this.getRecordId())) {
            case BLIP_EMF: {
                return 15680;
            }
            case BLIP_WMF: {
                return 8544;
            }
            case BLIP_PICT: {
                return 21536;
            }
            default: {
                if (EscherMetafileBlip.log.check(5)) {
                    EscherMetafileBlip.log.log(5, "Unknown metafile: " + this.getRecordId());
                }
                return 0;
            }
        }
    }
    
    @Override
    public void setPictureData(final byte[] pictureData) {
        super.setPictureData(pictureData);
        this.setUncompressedSize(pictureData.length);
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final DeflaterOutputStream dos = new DeflaterOutputStream(bos);
            dos.write(pictureData);
            dos.close();
            this.raw_pictureData = bos.toByteArray();
        }
        catch (final IOException e) {
            throw new RuntimeException("Can't compress metafile picture data", e);
        }
        this.setCompressedSize(this.raw_pictureData.length);
        this.setCompressed(true);
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        final Map<String, Supplier<?>> m = new LinkedHashMap<String, Supplier<?>>(super.getGenericProperties());
        m.put("uid", this::getUID);
        m.put("uncompressedSize", this::getUncompressedSize);
        m.put("bounds", this::getBounds);
        m.put("sizeInEMU", this::getSizeEMU);
        m.put("compressedSize", this::getCompressedSize);
        m.put("isCompressed", this::isCompressed);
        m.put("filter", this::getFilter);
        return Collections.unmodifiableMap((Map<? extends String, ? extends Supplier<?>>)m);
    }
    
    @Override
    public EscherMetafileBlip copy() {
        return new EscherMetafileBlip(this);
    }
    
    static {
        log = POILogFactory.getLogger(EscherMetafileBlip.class);
        RECORD_ID_EMF = EscherRecordTypes.BLIP_EMF.typeID;
        RECORD_ID_WMF = EscherRecordTypes.BLIP_WMF.typeID;
        RECORD_ID_PICT = EscherRecordTypes.BLIP_PICT.typeID;
    }
}
