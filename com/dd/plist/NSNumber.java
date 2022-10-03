package com.dd.plist;

import java.io.IOException;

public class NSNumber extends NSObject implements Comparable<Object>
{
    public static final int INTEGER = 0;
    public static final int REAL = 1;
    public static final int BOOLEAN = 2;
    private int type;
    private long longValue;
    private double doubleValue;
    private boolean boolValue;
    
    public NSNumber(final byte[] bytes, final int type) {
        this(bytes, 0, bytes.length, type);
    }
    
    public NSNumber(final byte[] bytes, final int startIndex, final int endIndex, final int type) {
        switch (type) {
            case 0: {
                final long long1 = BinaryPropertyListParser.parseLong(bytes, startIndex, endIndex);
                this.longValue = long1;
                this.doubleValue = (double)long1;
                break;
            }
            case 1: {
                this.doubleValue = BinaryPropertyListParser.parseDouble(bytes, startIndex, endIndex);
                this.longValue = Math.round(this.doubleValue);
                break;
            }
            default: {
                throw new IllegalArgumentException("Type argument is not valid.");
            }
        }
        this.type = type;
    }
    
    public NSNumber(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("The given string is null and cannot be parsed as number.");
        }
        try {
            long l;
            if (text.startsWith("0x")) {
                l = Long.parseLong(text.substring(2), 16);
            }
            else {
                l = Long.parseLong(text);
            }
            final long longValue = l;
            this.longValue = longValue;
            this.doubleValue = (double)longValue;
            this.type = 0;
        }
        catch (final Exception ex) {
            try {
                this.doubleValue = Double.parseDouble(text);
                this.longValue = Math.round(this.doubleValue);
                this.type = 1;
            }
            catch (final Exception ex2) {
                try {
                    this.boolValue = (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("yes"));
                    if (!this.boolValue && !text.equalsIgnoreCase("false") && !text.equalsIgnoreCase("no")) {
                        throw new Exception("not a boolean");
                    }
                    this.type = 2;
                    final boolean boolValue = this.boolValue;
                    this.longValue = (boolValue ? 1 : 0);
                    this.doubleValue = (boolValue ? 1 : 0);
                }
                catch (final Exception ex3) {
                    throw new IllegalArgumentException("The given string neither represents a double, an int nor a boolean value.");
                }
            }
        }
    }
    
    public NSNumber(final int i) {
        final long longValue = i;
        this.longValue = longValue;
        this.doubleValue = (double)longValue;
        this.type = 0;
    }
    
    public NSNumber(final long l) {
        this.longValue = l;
        this.doubleValue = (double)l;
        this.type = 0;
    }
    
    public NSNumber(final double d) {
        this.doubleValue = d;
        this.longValue = (long)d;
        this.type = 1;
    }
    
    public NSNumber(final boolean b) {
        this.boolValue = b;
        final boolean longValue = b;
        this.longValue = (longValue ? 1 : 0);
        this.doubleValue = (longValue ? 1 : 0);
        this.type = 2;
    }
    
    public int type() {
        return this.type;
    }
    
    public boolean isBoolean() {
        return this.type == 2;
    }
    
    public boolean isInteger() {
        return this.type == 0;
    }
    
    public boolean isReal() {
        return this.type == 1;
    }
    
    public boolean boolValue() {
        if (this.type == 2) {
            return this.boolValue;
        }
        return this.doubleValue() != 0.0;
    }
    
    public long longValue() {
        return this.longValue;
    }
    
    public int intValue() {
        return (int)this.longValue;
    }
    
    public double doubleValue() {
        return this.doubleValue;
    }
    
    public float floatValue() {
        return (float)this.doubleValue;
    }
    
    public String stringValue() {
        switch (this.type()) {
            case 0: {
                return String.valueOf(this.longValue());
            }
            case 1: {
                return String.valueOf(this.doubleValue());
            }
            case 2: {
                return String.valueOf(this.boolValue());
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final NSNumber n = (NSNumber)obj;
        return this.type == n.type && this.longValue == n.longValue && this.doubleValue == n.doubleValue && this.boolValue == n.boolValue;
    }
    
    @Override
    public int hashCode() {
        int hash = this.type;
        hash = 37 * hash + (int)(this.longValue ^ this.longValue >>> 32);
        hash = 37 * hash + (int)(Double.doubleToLongBits(this.doubleValue) ^ Double.doubleToLongBits(this.doubleValue) >>> 32);
        hash = 37 * hash + (this.boolValue() ? 1 : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        switch (this.type()) {
            case 0: {
                return String.valueOf(this.longValue());
            }
            case 1: {
                return String.valueOf(this.doubleValue());
            }
            case 2: {
                return String.valueOf(this.boolValue());
            }
            default: {
                return super.toString();
            }
        }
    }
    
    @Override
    void toXML(final StringBuilder xml, final int level) {
        this.indent(xml, level);
        switch (this.type()) {
            case 0: {
                xml.append("<integer>");
                xml.append(this.longValue());
                xml.append("</integer>");
                break;
            }
            case 1: {
                xml.append("<real>");
                xml.append(this.doubleValue());
                xml.append("</real>");
                break;
            }
            case 2: {
                if (this.boolValue()) {
                    xml.append("<true/>");
                    break;
                }
                xml.append("<false/>");
                break;
            }
        }
    }
    
    @Override
    void toBinary(final BinaryPropertyListWriter out) throws IOException {
        switch (this.type()) {
            case 0: {
                if (this.longValue() < 0L) {
                    out.write(19);
                    out.writeBytes(this.longValue(), 8);
                    break;
                }
                if (this.longValue() <= 255L) {
                    out.write(16);
                    out.writeBytes(this.longValue(), 1);
                    break;
                }
                if (this.longValue() <= 65535L) {
                    out.write(17);
                    out.writeBytes(this.longValue(), 2);
                    break;
                }
                if (this.longValue() <= 4294967295L) {
                    out.write(18);
                    out.writeBytes(this.longValue(), 4);
                    break;
                }
                out.write(19);
                out.writeBytes(this.longValue(), 8);
                break;
            }
            case 1: {
                out.write(35);
                out.writeDouble(this.doubleValue());
                break;
            }
            case 2: {
                out.write(this.boolValue() ? 9 : 8);
                break;
            }
        }
    }
    
    @Override
    protected void toASCII(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        if (this.isBoolean()) {
            ascii.append(this.boolValue() ? "YES" : "NO");
        }
        else {
            ascii.append(this.toString());
        }
    }
    
    @Override
    protected void toASCIIGnuStep(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        switch (this.type()) {
            case 0: {
                ascii.append("<*I");
                ascii.append(this.toString());
                ascii.append('>');
                break;
            }
            case 1: {
                ascii.append("<*R");
                ascii.append(this.toString());
                ascii.append('>');
                break;
            }
            case 2: {
                if (this.boolValue()) {
                    ascii.append("<*BY>");
                    break;
                }
                ascii.append("<*BN>");
                break;
            }
        }
    }
    
    @Override
    public int compareTo(final Object o) {
        final double x = this.doubleValue();
        if (o instanceof NSNumber) {
            final NSNumber num = (NSNumber)o;
            final double y = num.doubleValue();
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
        if (o instanceof Number) {
            final double y = ((Number)o).doubleValue();
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
        return -1;
    }
}
