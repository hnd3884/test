package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class CountryRecord extends StandardRecord
{
    public static final short sid = 140;
    private short field_1_default_country;
    private short field_2_current_country;
    
    public CountryRecord() {
    }
    
    public CountryRecord(final CountryRecord other) {
        super(other);
        this.field_1_default_country = other.field_1_default_country;
        this.field_2_current_country = other.field_2_current_country;
    }
    
    public CountryRecord(final RecordInputStream in) {
        this.field_1_default_country = in.readShort();
        this.field_2_current_country = in.readShort();
    }
    
    public void setDefaultCountry(final short country) {
        this.field_1_default_country = country;
    }
    
    public void setCurrentCountry(final short country) {
        this.field_2_current_country = country;
    }
    
    public short getDefaultCountry() {
        return this.field_1_default_country;
    }
    
    public short getCurrentCountry() {
        return this.field_2_current_country;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[COUNTRY]\n");
        buffer.append("    .defaultcountry  = ").append(Integer.toHexString(this.getDefaultCountry())).append("\n");
        buffer.append("    .currentcountry  = ").append(Integer.toHexString(this.getCurrentCountry())).append("\n");
        buffer.append("[/COUNTRY]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getDefaultCountry());
        out.writeShort(this.getCurrentCountry());
    }
    
    @Override
    protected int getDataSize() {
        return 4;
    }
    
    @Override
    public short getSid() {
        return 140;
    }
    
    @Override
    public CountryRecord copy() {
        return new CountryRecord(this);
    }
}
