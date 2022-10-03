package org.apache.commons.net.nntp;

public class SimpleNNTPHeader
{
    private final String __subject;
    private final String __from;
    private final StringBuilder __newsgroups;
    private final StringBuilder __headerFields;
    private int __newsgroupCount;
    
    public SimpleNNTPHeader(final String from, final String subject) {
        this.__from = from;
        this.__subject = subject;
        this.__newsgroups = new StringBuilder();
        this.__headerFields = new StringBuilder();
        this.__newsgroupCount = 0;
    }
    
    public void addNewsgroup(final String newsgroup) {
        if (this.__newsgroupCount++ > 0) {
            this.__newsgroups.append(',');
        }
        this.__newsgroups.append(newsgroup);
    }
    
    public void addHeaderField(final String headerField, final String value) {
        this.__headerFields.append(headerField);
        this.__headerFields.append(": ");
        this.__headerFields.append(value);
        this.__headerFields.append('\n');
    }
    
    public String getFromAddress() {
        return this.__from;
    }
    
    public String getSubject() {
        return this.__subject;
    }
    
    public String getNewsgroups() {
        return this.__newsgroups.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder header = new StringBuilder();
        header.append("From: ");
        header.append(this.__from);
        header.append("\nNewsgroups: ");
        header.append(this.__newsgroups.toString());
        header.append("\nSubject: ");
        header.append(this.__subject);
        header.append('\n');
        if (this.__headerFields.length() > 0) {
            header.append(this.__headerFields.toString());
        }
        header.append('\n');
        return header.toString();
    }
}
