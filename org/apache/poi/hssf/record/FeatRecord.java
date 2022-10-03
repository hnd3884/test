package org.apache.poi.hssf.record;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.common.FeatSmartTag;
import org.apache.poi.hssf.record.common.FeatFormulaErr2;
import org.apache.poi.hssf.record.common.FeatProtection;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.poi.hssf.record.common.SharedFeature;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.record.common.FtrHeader;
import org.apache.poi.util.POILogger;

public final class FeatRecord extends StandardRecord
{
    private static final POILogger logger;
    public static final short sid = 2152;
    public static final short v11_sid = 2162;
    public static final short v12_sid = 2168;
    private final FtrHeader futureHeader;
    private int isf_sharedFeatureType;
    private byte reserved1;
    private long reserved2;
    private long cbFeatData;
    private int reserved3;
    private CellRangeAddress[] cellRefs;
    private SharedFeature sharedFeature;
    
    public FeatRecord() {
        (this.futureHeader = new FtrHeader()).setRecordType((short)2152);
    }
    
    public FeatRecord(final FeatRecord other) {
        super(other);
        this.futureHeader = other.futureHeader.copy();
        this.isf_sharedFeatureType = other.isf_sharedFeatureType;
        this.reserved1 = other.reserved1;
        this.reserved2 = other.reserved2;
        this.cbFeatData = other.cbFeatData;
        this.reserved3 = other.reserved3;
        this.cellRefs = (CellRangeAddress[])((other.cellRefs == null) ? null : ((CellRangeAddress[])Stream.of(other.cellRefs).map((Function<? super CellRangeAddress, ?>)CellRangeAddress::copy).toArray(CellRangeAddress[]::new)));
        this.sharedFeature = ((other.sharedFeature == null) ? null : other.sharedFeature.copy());
    }
    
    public FeatRecord(final RecordInputStream in) {
        this.futureHeader = new FtrHeader(in);
        this.isf_sharedFeatureType = in.readShort();
        this.reserved1 = in.readByte();
        this.reserved2 = in.readInt();
        final int cref = in.readUShort();
        this.cbFeatData = in.readInt();
        this.reserved3 = in.readShort();
        this.cellRefs = new CellRangeAddress[cref];
        for (int i = 0; i < this.cellRefs.length; ++i) {
            this.cellRefs[i] = new CellRangeAddress(in);
        }
        switch (this.isf_sharedFeatureType) {
            case 2: {
                this.sharedFeature = new FeatProtection(in);
                break;
            }
            case 3: {
                this.sharedFeature = new FeatFormulaErr2(in);
                break;
            }
            case 4: {
                this.sharedFeature = new FeatSmartTag(in);
                break;
            }
            default: {
                FeatRecord.logger.log(7, "Unknown Shared Feature " + this.isf_sharedFeatureType + " found!");
                break;
            }
        }
    }
    
    @Override
    public short getSid() {
        return 2152;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[SHARED FEATURE]\n");
        buffer.append("[/SHARED FEATURE]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        this.futureHeader.serialize(out);
        out.writeShort(this.isf_sharedFeatureType);
        out.writeByte(this.reserved1);
        out.writeInt((int)this.reserved2);
        out.writeShort(this.cellRefs.length);
        out.writeInt((int)this.cbFeatData);
        out.writeShort(this.reserved3);
        for (int i = 0; i < this.cellRefs.length; ++i) {
            this.cellRefs[i].serialize(out);
        }
        this.sharedFeature.serialize(out);
    }
    
    @Override
    protected int getDataSize() {
        return 27 + this.cellRefs.length * 8 + this.sharedFeature.getDataSize();
    }
    
    public int getIsf_sharedFeatureType() {
        return this.isf_sharedFeatureType;
    }
    
    public long getCbFeatData() {
        return this.cbFeatData;
    }
    
    public void setCbFeatData(final long cbFeatData) {
        this.cbFeatData = cbFeatData;
    }
    
    public CellRangeAddress[] getCellRefs() {
        return this.cellRefs;
    }
    
    public void setCellRefs(final CellRangeAddress[] cellRefs) {
        this.cellRefs = cellRefs;
    }
    
    public SharedFeature getSharedFeature() {
        return this.sharedFeature;
    }
    
    public void setSharedFeature(final SharedFeature feature) {
        this.sharedFeature = feature;
        if (feature instanceof FeatProtection) {
            this.isf_sharedFeatureType = 2;
        }
        if (feature instanceof FeatFormulaErr2) {
            this.isf_sharedFeatureType = 3;
        }
        if (feature instanceof FeatSmartTag) {
            this.isf_sharedFeatureType = 4;
        }
        if (this.isf_sharedFeatureType == 3) {
            this.cbFeatData = this.sharedFeature.getDataSize();
        }
        else {
            this.cbFeatData = 0L;
        }
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public FeatRecord clone() {
        return this.copy();
    }
    
    @Override
    public FeatRecord copy() {
        return new FeatRecord(this);
    }
    
    static {
        logger = POILogFactory.getLogger(FeatRecord.class);
    }
}
