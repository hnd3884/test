package com.ocpsoft.pretty.time.units;

import com.ocpsoft.pretty.time.TimeFormat;
import java.util.Locale;
import com.ocpsoft.pretty.time.TimeUnit;
import com.ocpsoft.pretty.time.AbstractTimeUnit;

public class Day extends AbstractTimeUnit implements TimeUnit
{
    public Day(final Locale locale) {
        super(locale);
        this.millisPerUnit = 86400000L;
    }
    
    @Override
    protected String getResourceKeyPrefix() {
        return "Day";
    }
    
    public long getMillisPerUnit() {
        return this.millisPerUnit;
    }
    
    public TimeFormat getFormat() {
        return this.format;
    }
    
    public void setFormat(final TimeFormat format) {
        this.format = format;
    }
    
    public long getMaxQuantity() {
        return this.maxQuantity;
    }
    
    public void setMaxQuantity(final long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getPluralName() {
        return this.pluralName;
    }
    
    public void setPluralName(final String pluralName) {
        this.pluralName = pluralName;
    }
}
