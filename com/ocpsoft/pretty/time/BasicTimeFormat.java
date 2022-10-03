package com.ocpsoft.pretty.time;

public class BasicTimeFormat implements TimeFormat
{
    private static final String NEGATIVE = "-";
    public static final String SIGN = "%s";
    public static final String QUANTITY = "%n";
    public static final String UNIT = "%u";
    private String pattern;
    private String futurePrefix;
    private String futureSuffix;
    private String pastPrefix;
    private String pastSuffix;
    private int roundingTolerance;
    
    public BasicTimeFormat() {
        this.pattern = "";
        this.futurePrefix = "";
        this.futureSuffix = "";
        this.pastPrefix = "";
        this.pastSuffix = "";
        this.roundingTolerance = 0;
    }
    
    public String format(final Duration duration) {
        final String sign = this.getSign(duration);
        final String unit = this.getGramaticallyCorrectName(duration);
        final long quantity = this.getQuantity(duration);
        String result = this.applyPattern(sign, unit, quantity);
        result = this.decorate(sign, result);
        return result;
    }
    
    private String decorate(final String sign, String result) {
        if ("-".equals(sign)) {
            result = String.valueOf(this.pastPrefix) + " " + result + " " + this.pastSuffix;
        }
        else {
            result = String.valueOf(this.futurePrefix) + " " + result + " " + this.futureSuffix;
        }
        return result.trim();
    }
    
    private String applyPattern(final String sign, final String unit, final long quantity) {
        String result = this.pattern.replaceAll("%s", sign);
        result = result.replaceAll("%n", String.valueOf(quantity));
        result = result.replaceAll("%u", unit);
        return result;
    }
    
    private long getQuantity(final Duration duration) {
        long quantity = Math.abs(duration.getQuantity());
        if (duration.getDelta() != 0L) {
            final double threshold = Math.abs(duration.getDelta() / (double)duration.getUnit().getMillisPerUnit() * 100.0);
            if (threshold < this.roundingTolerance) {
                ++quantity;
            }
        }
        return quantity;
    }
    
    private String getGramaticallyCorrectName(final Duration d) {
        String result = d.getUnit().getName();
        if (Math.abs(d.getQuantity()) == 0L || Math.abs(d.getQuantity()) > 1L) {
            result = d.getUnit().getPluralName();
        }
        return result;
    }
    
    private String getSign(final Duration d) {
        if (d.getQuantity() < 0L) {
            return "-";
        }
        return "";
    }
    
    public BasicTimeFormat setPattern(final String pattern) {
        this.pattern = pattern;
        return this;
    }
    
    public BasicTimeFormat setFuturePrefix(final String futurePrefix) {
        this.futurePrefix = futurePrefix.trim();
        return this;
    }
    
    public BasicTimeFormat setFutureSuffix(final String futureSuffix) {
        this.futureSuffix = futureSuffix.trim();
        return this;
    }
    
    public BasicTimeFormat setPastPrefix(final String pastPrefix) {
        this.pastPrefix = pastPrefix.trim();
        return this;
    }
    
    public BasicTimeFormat setPastSuffix(final String pastSuffix) {
        this.pastSuffix = pastSuffix.trim();
        return this;
    }
    
    public BasicTimeFormat setRoundingTolerance(final int roundingTolerance) {
        this.roundingTolerance = roundingTolerance;
        return this;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public String getFuturePrefix() {
        return this.futurePrefix;
    }
    
    public String getFutureSuffix() {
        return this.futureSuffix;
    }
    
    public String getPastPrefix() {
        return this.pastPrefix;
    }
    
    public String getPastSuffix() {
        return this.pastSuffix;
    }
    
    public int getRoundingTolerance() {
        return this.roundingTolerance;
    }
}
