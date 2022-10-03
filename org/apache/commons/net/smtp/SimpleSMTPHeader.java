package org.apache.commons.net.smtp;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SimpleSMTPHeader
{
    private final String __subject;
    private final String __from;
    private final String __to;
    private final StringBuffer __headerFields;
    private boolean hasHeaderDate;
    private StringBuffer __cc;
    
    public SimpleSMTPHeader(final String from, final String to, final String subject) {
        if (from == null) {
            throw new IllegalArgumentException("From cannot be null");
        }
        this.__to = to;
        this.__from = from;
        this.__subject = subject;
        this.__headerFields = new StringBuffer();
        this.__cc = null;
    }
    
    public void addHeaderField(final String headerField, final String value) {
        if (!this.hasHeaderDate && "Date".equals(headerField)) {
            this.hasHeaderDate = true;
        }
        this.__headerFields.append(headerField);
        this.__headerFields.append(": ");
        this.__headerFields.append(value);
        this.__headerFields.append('\n');
    }
    
    public void addCC(final String address) {
        if (this.__cc == null) {
            this.__cc = new StringBuffer();
        }
        else {
            this.__cc.append(", ");
        }
        this.__cc.append(address);
    }
    
    @Override
    public String toString() {
        final StringBuilder header = new StringBuilder();
        final String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
        final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        if (!this.hasHeaderDate) {
            this.addHeaderField("Date", format.format(new Date()));
        }
        if (this.__headerFields.length() > 0) {
            header.append(this.__headerFields.toString());
        }
        header.append("From: ").append(this.__from).append("\n");
        if (this.__to != null) {
            header.append("To: ").append(this.__to).append("\n");
        }
        if (this.__cc != null) {
            header.append("Cc: ").append(this.__cc.toString()).append("\n");
        }
        if (this.__subject != null) {
            header.append("Subject: ").append(this.__subject).append("\n");
        }
        header.append('\n');
        return header.toString();
    }
}
