package com.adventnet.ds.query;

import java.util.Objects;
import java.util.Locale;

public class LocaleColumn extends Column
{
    private Locale locale;
    private Column column;
    
    public LocaleColumn(final Column column, final Locale locale) {
        this.locale = null;
        this.locale = locale;
        this.column = column;
    }
    
    @Override
    public Column getColumn() {
        return this.column;
    }
    
    @Override
    public int getType() {
        return this.column.getType();
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LocaleColumn) {
            final LocaleColumn lc = (LocaleColumn)obj;
            return super.equals(obj) && Objects.equals(this.column, lc.column) && Objects.equals(this.locale, lc.locale);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.locale, this.column);
    }
}
