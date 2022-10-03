package com.adventnet.swissqlapi.sql.statement.update;

public class OptionalSpecifier
{
    private String low_priority;
    private String delayed;
    private String ignore;
    private String into;
    private String from;
    private String only;
    
    public OptionalSpecifier() {
        this.low_priority = null;
        this.delayed = null;
        this.ignore = null;
        this.into = null;
        this.from = null;
        this.only = null;
    }
    
    public void setLowPriority(final String s) {
        this.low_priority = s;
    }
    
    public String getLowPriority() {
        return this.low_priority;
    }
    
    public void setDelayed(final String s) {
        this.delayed = s;
    }
    
    public String getDelayed() {
        return this.delayed;
    }
    
    public String getFrom() {
        return this.from;
    }
    
    public void setFrom(final String s) {
        this.from = s;
    }
    
    public void setIgnore(final String s) {
        this.ignore = s;
    }
    
    public String getIgnore() {
        return this.ignore;
    }
    
    public void setInto(final String s) {
        this.into = s;
    }
    
    public String getInto() {
        return this.into;
    }
    
    public void setOnly(final String s) {
        this.only = s;
    }
    
    public String getOnly() {
        return this.only;
    }
    
    public void toPostgreSQL() {
        this.low_priority = null;
        this.delayed = null;
        this.ignore = null;
        this.into = null;
    }
    
    public void toMySQL() {
        this.only = null;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.low_priority != null) {
            sb.append(this.low_priority.toUpperCase());
            sb.append(" ");
        }
        if (this.delayed != null) {
            sb.append(this.delayed.toUpperCase());
            sb.append(" ");
        }
        if (this.ignore != null) {
            sb.append(this.ignore.toUpperCase());
            sb.append(" ");
        }
        if (this.into != null) {
            sb.append(this.into.toUpperCase());
            sb.append(" ");
        }
        if (this.from != null) {
            sb.append(this.from.toUpperCase());
            sb.append(" ");
        }
        if (this.only != null) {
            sb.append(this.only.toUpperCase());
            sb.append(" ");
        }
        return sb.toString();
    }
}
