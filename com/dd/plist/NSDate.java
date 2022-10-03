package com.dd.plist;

import java.util.TimeZone;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NSDate extends NSObject
{
    private Date date;
    private static final long EPOCH = 978307200000L;
    private static final SimpleDateFormat sdfDefault;
    private static final SimpleDateFormat sdfGnuStep;
    
    public NSDate(final byte[] bytes) {
        this(bytes, 0, bytes.length);
    }
    
    public NSDate(final byte[] bytes, final int startIndex, final int endIndex) {
        this.date = new Date(978307200000L + (long)(1000.0 * BinaryPropertyListParser.parseDouble(bytes, startIndex, endIndex)));
    }
    
    public NSDate(final String textRepresentation) throws ParseException {
        this.date = parseDateString(textRepresentation);
    }
    
    public NSDate(final Date d) {
        if (d == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = d;
    }
    
    private static synchronized Date parseDateString(final String textRepresentation) throws ParseException {
        try {
            return NSDate.sdfDefault.parse(textRepresentation);
        }
        catch (final ParseException ex) {
            return NSDate.sdfGnuStep.parse(textRepresentation);
        }
    }
    
    private static synchronized String makeDateString(final Date date) {
        return NSDate.sdfDefault.format(date);
    }
    
    private static synchronized String makeDateStringGnuStep(final Date date) {
        return NSDate.sdfGnuStep.format(date);
    }
    
    public Date getDate() {
        return this.date;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj.getClass().equals(this.getClass()) && this.date.equals(((NSDate)obj).getDate());
    }
    
    @Override
    public int hashCode() {
        return this.date.hashCode();
    }
    
    @Override
    void toXML(final StringBuilder xml, final int level) {
        this.indent(xml, level);
        xml.append("<date>");
        xml.append(makeDateString(this.date));
        xml.append("</date>");
    }
    
    public void toBinary(final BinaryPropertyListWriter out) throws IOException {
        out.write(51);
        out.writeDouble((this.date.getTime() - 978307200000L) / 1000.0);
    }
    
    @Override
    public String toString() {
        return this.date.toString();
    }
    
    @Override
    protected void toASCII(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append('\"');
        ascii.append(makeDateString(this.date));
        ascii.append('\"');
    }
    
    @Override
    protected void toASCIIGnuStep(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append("<*D");
        ascii.append(makeDateStringGnuStep(this.date));
        ascii.append('>');
    }
    
    static {
        sdfDefault = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdfGnuStep = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        NSDate.sdfDefault.setTimeZone(TimeZone.getTimeZone("GMT"));
        NSDate.sdfGnuStep.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
}
