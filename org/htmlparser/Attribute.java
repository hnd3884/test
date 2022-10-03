package org.htmlparser;

import java.io.Serializable;

public class Attribute implements Serializable
{
    protected String mName;
    protected String mAssignment;
    protected String mValue;
    protected char mQuote;
    
    public Attribute(final String name, final String assignment, final String value, final char quote) {
        this.setName(name);
        this.setAssignment(assignment);
        if ('\0' == quote) {
            this.setRawValue(value);
        }
        else {
            this.setValue(value);
            this.setQuote(quote);
        }
    }
    
    public Attribute(final String name, final String value, final char quote) {
        this(name, (null == value) ? "" : "=", value, quote);
    }
    
    public Attribute(final String value) throws IllegalArgumentException {
        if (0 != value.trim().length()) {
            throw new IllegalArgumentException("non whitespace value");
        }
        this.setName(null);
        this.setAssignment(null);
        this.setValue(value);
        this.setQuote('\0');
    }
    
    public Attribute(final String name, final String value) {
        this(name, (null == value) ? "" : "=", value, '\0');
    }
    
    public Attribute(final String name, final String assignment, final String value) {
        this(name, assignment, value, '\0');
    }
    
    public Attribute() {
        this(null, null, null, '\0');
    }
    
    public String getName() {
        return this.mName;
    }
    
    public void getName(final StringBuffer buffer) {
        if (null != this.mName) {
            buffer.append(this.mName);
        }
    }
    
    public void setName(final String name) {
        this.mName = name;
    }
    
    public String getAssignment() {
        return this.mAssignment;
    }
    
    public void getAssignment(final StringBuffer buffer) {
        if (null != this.mAssignment) {
            buffer.append(this.mAssignment);
        }
    }
    
    public void setAssignment(final String assignment) {
        this.mAssignment = assignment;
    }
    
    public String getValue() {
        return this.mValue;
    }
    
    public void getValue(final StringBuffer buffer) {
        if (null != this.mValue) {
            buffer.append(this.mValue);
        }
    }
    
    public void setValue(final String value) {
        this.mValue = value;
    }
    
    public char getQuote() {
        return this.mQuote;
    }
    
    public void getQuote(final StringBuffer buffer) {
        if ('\0' != this.mQuote) {
            buffer.append(this.mQuote);
        }
    }
    
    public void setQuote(final char quote) {
        this.mQuote = quote;
    }
    
    public String getRawValue() {
        String ret;
        if (this.isValued()) {
            final char quote = this.getQuote();
            if ('\0' != quote) {
                final StringBuffer buffer = new StringBuffer();
                buffer.append(quote);
                this.getValue(buffer);
                buffer.append(quote);
                ret = buffer.toString();
            }
            else {
                ret = this.getValue();
            }
        }
        else {
            ret = null;
        }
        return ret;
    }
    
    public void getRawValue(final StringBuffer buffer) {
        this.getQuote(buffer);
        this.getValue(buffer);
        this.getQuote(buffer);
    }
    
    public void setRawValue(String value) {
        char quote = '\0';
        if (null != value && 0 != value.trim().length()) {
            if (value.startsWith("'") && value.endsWith("'") && 2 <= value.length()) {
                quote = '\'';
                value = value.substring(1, value.length() - 1);
            }
            else if (value.startsWith("\"") && value.endsWith("\"") && 2 <= value.length()) {
                quote = '\"';
                value = value.substring(1, value.length() - 1);
            }
            else {
                boolean needed = false;
                boolean singleq = true;
                boolean doubleq = true;
                for (int i = 0; i < value.length(); ++i) {
                    final char ch = value.charAt(i);
                    if ('\'' == ch) {
                        singleq = false;
                        needed = true;
                    }
                    else if ('\"' == ch) {
                        doubleq = false;
                        needed = true;
                    }
                    else if ('-' != ch && '.' != ch && '_' != ch && ':' != ch && !Character.isLetterOrDigit(ch)) {
                        needed = true;
                    }
                }
                if (needed) {
                    if (doubleq) {
                        quote = '\"';
                    }
                    else if (singleq) {
                        quote = '\'';
                    }
                    else {
                        quote = '\"';
                        final String ref = "&quot;";
                        final StringBuffer buffer = new StringBuffer(value.length() * (ref.length() - 1));
                        for (int i = 0; i < value.length(); ++i) {
                            final char ch = value.charAt(i);
                            if (quote == ch) {
                                buffer.append(ref);
                            }
                            else {
                                buffer.append(ch);
                            }
                        }
                        value = buffer.toString();
                    }
                }
            }
        }
        this.setValue(value);
        this.setQuote(quote);
    }
    
    public boolean isWhitespace() {
        return null == this.getName();
    }
    
    public boolean isStandAlone() {
        return null != this.getName() && null == this.getAssignment();
    }
    
    public boolean isEmpty() {
        return null != this.getAssignment() && null == this.getValue();
    }
    
    public boolean isValued() {
        return null != this.getValue();
    }
    
    public int getLength() {
        int ret = 0;
        final String name = this.getName();
        if (null != name) {
            ret += name.length();
        }
        final String assignment = this.getAssignment();
        if (null != assignment) {
            ret += assignment.length();
        }
        final String value = this.getValue();
        if (null != value) {
            ret += value.length();
        }
        final char quote = this.getQuote();
        if ('\0' != quote) {
            ret += 2;
        }
        return ret;
    }
    
    public String toString() {
        final int length = this.getLength();
        final StringBuffer ret = new StringBuffer(length);
        this.toString(ret);
        return ret.toString();
    }
    
    public void toString(final StringBuffer buffer) {
        this.getName(buffer);
        this.getAssignment(buffer);
        this.getRawValue(buffer);
    }
}
