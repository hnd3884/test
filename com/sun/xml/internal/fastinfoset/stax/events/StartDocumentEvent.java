package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent extends EventBase implements StartDocument
{
    protected String _systemId;
    protected String _encoding;
    protected boolean _standalone;
    protected String _version;
    private boolean _encodingSet;
    private boolean _standaloneSet;
    
    public void reset() {
        this._encoding = "UTF-8";
        this._standalone = true;
        this._version = "1.0";
        this._encodingSet = false;
        this._standaloneSet = false;
    }
    
    public StartDocumentEvent() {
        this(null, null);
    }
    
    public StartDocumentEvent(final String encoding) {
        this(encoding, null);
    }
    
    public StartDocumentEvent(final String encoding, final String version) {
        this._encoding = "UTF-8";
        this._standalone = true;
        this._version = "1.0";
        this._encodingSet = false;
        this._standaloneSet = false;
        if (encoding != null) {
            this._encoding = encoding;
            this._encodingSet = true;
        }
        if (version != null) {
            this._version = version;
        }
        this.setEventType(7);
    }
    
    @Override
    public String getSystemId() {
        return super.getSystemId();
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return this._encoding;
    }
    
    @Override
    public boolean encodingSet() {
        return this._encodingSet;
    }
    
    @Override
    public boolean isStandalone() {
        return this._standalone;
    }
    
    @Override
    public boolean standaloneSet() {
        return this._standaloneSet;
    }
    
    @Override
    public String getVersion() {
        return this._version;
    }
    
    public void setStandalone(final boolean standalone) {
        this._standaloneSet = true;
        this._standalone = standalone;
    }
    
    public void setStandalone(final String s) {
        this._standaloneSet = true;
        if (s == null) {
            this._standalone = true;
            return;
        }
        if (s.equals("yes")) {
            this._standalone = true;
        }
        else {
            this._standalone = false;
        }
    }
    
    public void setEncoding(final String encoding) {
        this._encoding = encoding;
        this._encodingSet = true;
    }
    
    void setDeclaredEncoding(final boolean value) {
        this._encodingSet = value;
    }
    
    public void setVersion(final String s) {
        this._version = s;
    }
    
    void clear() {
        this._encoding = "UTF-8";
        this._standalone = true;
        this._version = "1.0";
        this._encodingSet = false;
        this._standaloneSet = false;
    }
    
    @Override
    public String toString() {
        String s = "<?xml version=\"" + this._version + "\"";
        s = s + " encoding='" + this._encoding + "'";
        if (this._standaloneSet) {
            if (this._standalone) {
                s += " standalone='yes'?>";
            }
            else {
                s += " standalone='no'?>";
            }
        }
        else {
            s += "?>";
        }
        return s;
    }
    
    @Override
    public boolean isStartDocument() {
        return true;
    }
}
