package org.htmlparser.lexer;

import org.htmlparser.Attribute;

public class PageAttribute extends Attribute
{
    protected Page mPage;
    protected int mNameStart;
    protected int mNameEnd;
    protected int mValueStart;
    protected int mValueEnd;
    
    public PageAttribute(final Page page, final int name_start, final int name_end, final int value_start, final int value_end, final char quote) {
        this.mPage = page;
        this.mNameStart = name_start;
        this.mNameEnd = name_end;
        this.mValueStart = value_start;
        this.mValueEnd = value_end;
        this.setName(null);
        this.setAssignment(null);
        this.setValue(null);
        this.setQuote(quote);
    }
    
    private void init() {
        this.mPage = null;
        this.mNameStart = -1;
        this.mNameEnd = -1;
        this.mValueStart = -1;
        this.mValueEnd = -1;
    }
    
    public PageAttribute(final String name, final String assignment, final String value, final char quote) {
        super(name, assignment, value, quote);
        this.init();
    }
    
    public PageAttribute(final String name, final String value, final char quote) {
        super(name, value, quote);
        this.init();
    }
    
    public PageAttribute(final String value) throws IllegalArgumentException {
        super(value);
        this.init();
    }
    
    public PageAttribute(final String name, final String value) {
        super(name, value);
        this.init();
    }
    
    public PageAttribute(final String name, final String assignment, final String value) {
        super(name, assignment, value);
        this.init();
    }
    
    public PageAttribute() {
        this.init();
    }
    
    public String getName() {
        String ret = super.getName();
        if (null == ret && null != this.mPage && 0 <= this.mNameStart) {
            ret = this.mPage.getText(this.mNameStart, this.mNameEnd);
            this.setName(ret);
        }
        return ret;
    }
    
    public void getName(final StringBuffer buffer) {
        final String name = super.getName();
        if (null == name) {
            if (null != this.mPage && 0 <= this.mNameStart) {
                this.mPage.getText(buffer, this.mNameStart, this.mNameEnd);
            }
        }
        else {
            buffer.append(name);
        }
    }
    
    public String getAssignment() {
        String ret = super.getAssignment();
        if (null == ret && null != this.mPage && 0 <= this.mNameEnd && 0 <= this.mValueStart) {
            ret = this.mPage.getText(this.mNameEnd, this.mValueStart);
            if (ret.endsWith("\"") || ret.endsWith("'")) {
                ret = ret.substring(0, ret.length() - 1);
            }
            this.setAssignment(ret);
        }
        return ret;
    }
    
    public void getAssignment(final StringBuffer buffer) {
        final String assignment = super.getAssignment();
        if (null == assignment) {
            if (null != this.mPage && 0 <= this.mNameEnd && 0 <= this.mValueStart) {
                this.mPage.getText(buffer, this.mNameEnd, this.mValueStart);
                final int length = buffer.length() - 1;
                final char ch = buffer.charAt(length);
                if ('\'' == ch || '\"' == ch) {
                    buffer.setLength(length);
                }
            }
        }
        else {
            buffer.append(assignment);
        }
    }
    
    public String getValue() {
        String ret = super.getValue();
        if (null == ret && null != this.mPage && 0 <= this.mValueEnd) {
            ret = this.mPage.getText(this.mValueStart, this.mValueEnd);
            this.setValue(ret);
        }
        return ret;
    }
    
    public void getValue(final StringBuffer buffer) {
        final String value = super.getValue();
        if (null == value) {
            if (null != this.mPage && 0 <= this.mValueEnd) {
                this.mPage.getText(buffer, this.mNameStart, this.mNameEnd);
            }
        }
        else {
            buffer.append(value);
        }
    }
    
    public String getRawValue() {
        String ret = this.getValue();
        final char quote;
        if (null != ret && '\0' != (quote = this.getQuote())) {
            final StringBuffer buffer = new StringBuffer(ret.length() + 2);
            buffer.append(quote);
            buffer.append(ret);
            buffer.append(quote);
            ret = buffer.toString();
        }
        return ret;
    }
    
    public void getRawValue(final StringBuffer buffer) {
        if (null == super.mValue) {
            if (0 <= this.mValueEnd) {
                final char quote;
                if ('\0' != (quote = this.getQuote())) {
                    buffer.append(quote);
                }
                if (this.mValueStart != this.mValueEnd) {
                    this.mPage.getText(buffer, this.mValueStart, this.mValueEnd);
                }
                if ('\0' != quote) {
                    buffer.append(quote);
                }
            }
        }
        else {
            final char quote;
            if ('\0' != (quote = this.getQuote())) {
                buffer.append(quote);
            }
            buffer.append(super.mValue);
            if ('\0' != quote) {
                buffer.append(quote);
            }
        }
    }
    
    public Page getPage() {
        return this.mPage;
    }
    
    public void setPage(final Page page) {
        this.mPage = page;
    }
    
    public int getNameStartPosition() {
        return this.mNameStart;
    }
    
    public void setNameStartPosition(final int start) {
        this.mNameStart = start;
        this.setName(null);
    }
    
    public int getNameEndPosition() {
        return this.mNameEnd;
    }
    
    public void setNameEndPosition(final int end) {
        this.mNameEnd = end;
        this.setName(null);
        this.setAssignment(null);
    }
    
    public int getValueStartPosition() {
        return this.mValueStart;
    }
    
    public void setValueStartPosition(final int start) {
        this.mValueStart = start;
        this.setAssignment(null);
        this.setValue(null);
    }
    
    public int getValueEndPosition() {
        return this.mValueEnd;
    }
    
    public void setValueEndPosition(final int end) {
        this.mValueEnd = end;
        this.setValue(null);
    }
    
    public boolean isWhitespace() {
        return (null == super.getName() && null == this.mPage) || (null != this.mPage && 0 > this.mNameStart);
    }
    
    public boolean isStandAlone() {
        return !this.isWhitespace() && null == super.getAssignment() && !this.isValued() && (null == this.mPage || (null != this.mPage && 0 <= this.mNameEnd && 0 > this.mValueStart));
    }
    
    public boolean isEmpty() {
        return !this.isWhitespace() && !this.isStandAlone() && null == super.getValue() && (null == this.mPage || (null != this.mPage && 0 > this.mValueEnd));
    }
    
    public boolean isValued() {
        return null != super.getValue() || (null != this.mPage && 0 <= this.mValueStart && 0 <= this.mValueEnd && this.mValueStart != this.mValueEnd);
    }
    
    public int getLength() {
        int ret = 0;
        final String name = super.getName();
        if (null != name) {
            ret += name.length();
        }
        else if (null != this.mPage && 0 <= this.mNameStart && 0 <= this.mNameEnd) {
            ret += this.mNameEnd - this.mNameStart;
        }
        final String assignment = super.getAssignment();
        if (null != assignment) {
            ret += assignment.length();
        }
        else if (null != this.mPage && 0 <= this.mNameEnd && 0 <= this.mValueStart) {
            ret += this.mValueStart - this.mNameEnd;
        }
        final String value = super.getValue();
        if (null != value) {
            ret += value.length();
        }
        else if (null != this.mPage && 0 <= this.mValueStart && 0 <= this.mValueEnd) {
            ret += this.mValueEnd - this.mValueStart;
        }
        final char quote = this.getQuote();
        if ('\0' != quote) {
            ret += 2;
        }
        return ret;
    }
}
