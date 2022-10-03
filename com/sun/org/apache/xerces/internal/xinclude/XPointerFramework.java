package com.sun.org.apache.xerces.internal.xinclude;

import java.util.Stack;

public class XPointerFramework
{
    XPointerSchema[] fXPointerSchema;
    String[] fSchemaPointerName;
    String[] fSchemaPointerURI;
    String fSchemaPointer;
    String fCurrentSchemaPointer;
    Stack fSchemaNotAvailable;
    int fCountSchemaName;
    int schemaLength;
    XPointerSchema fDefaultXPointerSchema;
    
    public XPointerFramework() {
        this(null);
    }
    
    public XPointerFramework(final XPointerSchema[] xpointerschema) {
        this.fCountSchemaName = 0;
        this.schemaLength = 0;
        this.fXPointerSchema = xpointerschema;
        this.fSchemaNotAvailable = new Stack();
    }
    
    public void reset() {
        this.fXPointerSchema = null;
        this.fXPointerSchema = null;
        this.fCountSchemaName = 0;
        this.schemaLength = 0;
        this.fSchemaPointerName = null;
        this.fSchemaPointerURI = null;
        this.fDefaultXPointerSchema = null;
        this.fCurrentSchemaPointer = null;
    }
    
    public void setXPointerSchema(final XPointerSchema[] xpointerschema) {
        this.fXPointerSchema = xpointerschema;
    }
    
    public void setSchemaPointer(final String schemaPointer) {
        this.fSchemaPointer = schemaPointer;
    }
    
    public XPointerSchema getNextXPointerSchema() {
        int i = this.fCountSchemaName;
        if (this.fSchemaPointerName == null) {
            this.getSchemaNames();
        }
        if (this.fDefaultXPointerSchema == null) {
            this.getDefaultSchema();
        }
        if (this.fDefaultXPointerSchema.getXpointerSchemaName().equalsIgnoreCase(this.fSchemaPointerName[i])) {
            this.fDefaultXPointerSchema.reset();
            this.fDefaultXPointerSchema.setXPointerSchemaPointer(this.fSchemaPointerURI[i]);
            this.fCountSchemaName = ++i;
            return this.getDefaultSchema();
        }
        if (this.fXPointerSchema == null) {
            this.fCountSchemaName = ++i;
            return null;
        }
        final int fschemalength = this.fXPointerSchema.length;
        while (this.fSchemaPointerName[i] != null) {
            for (int j = 0; j < fschemalength; ++j) {
                if (this.fSchemaPointerName[i].equalsIgnoreCase(this.fXPointerSchema[j].getXpointerSchemaName())) {
                    this.fXPointerSchema[j].setXPointerSchemaPointer(this.fSchemaPointerURI[i]);
                    this.fCountSchemaName = ++i;
                    return this.fXPointerSchema[j];
                }
            }
            if (this.fSchemaNotAvailable == null) {
                this.fSchemaNotAvailable = new Stack();
            }
            this.fSchemaNotAvailable.push(this.fSchemaPointerName[i]);
            ++i;
        }
        return null;
    }
    
    public XPointerSchema getDefaultSchema() {
        if (this.fDefaultXPointerSchema == null) {
            this.fDefaultXPointerSchema = new XPointerElementHandler();
        }
        return this.fDefaultXPointerSchema;
    }
    
    public void getSchemaNames() {
        int count = 0;
        int index = 0;
        int lastindex = 0;
        int schemapointerindex = 0;
        int schemapointerURIindex = 0;
        final int length = this.fSchemaPointer.length();
        this.fSchemaPointerName = new String[5];
        this.fSchemaPointerURI = new String[5];
        index = this.fSchemaPointer.indexOf(40);
        if (index <= 0) {
            return;
        }
        this.fSchemaPointerName[schemapointerindex++] = this.fSchemaPointer.substring(0, index++).trim();
        lastindex = index;
        String tempURI = null;
        ++count;
        while (index < length) {
            final char c = this.fSchemaPointer.charAt(index);
            if (c == '(') {
                ++count;
            }
            if (c == ')') {
                --count;
            }
            if (count == 0) {
                tempURI = this.fSchemaPointer.substring(lastindex, index).trim();
                this.fSchemaPointerURI[schemapointerURIindex++] = this.getEscapedURI(tempURI);
                lastindex = index;
                if ((index = this.fSchemaPointer.indexOf(40, lastindex)) != -1) {
                    this.fSchemaPointerName[schemapointerindex++] = this.fSchemaPointer.substring(lastindex + 1, index).trim();
                    ++count;
                    lastindex = index + 1;
                }
                else {
                    index = lastindex;
                }
            }
            ++index;
        }
        this.schemaLength = schemapointerURIindex - 1;
    }
    
    public String getEscapedURI(final String URI) {
        return URI;
    }
    
    public int getSchemaCount() {
        return this.schemaLength;
    }
    
    public int getCurrentPointer() {
        return this.fCountSchemaName;
    }
}
