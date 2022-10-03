package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent extends DummyEvent implements StartDocument
{
    protected String fSystemId;
    protected String fEncodingScheam;
    protected boolean fStandalone;
    protected String fVersion;
    private boolean fEncodingSchemeSet;
    private boolean fStandaloneSet;
    private boolean nestedCall;
    
    public StartDocumentEvent() {
        this.fEncodingSchemeSet = false;
        this.fStandaloneSet = false;
        this.nestedCall = false;
        this.init("UTF-8", "1.0", true, null);
    }
    
    public StartDocumentEvent(final String encoding) {
        this.fEncodingSchemeSet = false;
        this.fStandaloneSet = false;
        this.nestedCall = false;
        this.init(encoding, "1.0", true, null);
    }
    
    public StartDocumentEvent(final String encoding, final String version) {
        this.fEncodingSchemeSet = false;
        this.fStandaloneSet = false;
        this.nestedCall = false;
        this.init(encoding, version, true, null);
    }
    
    public StartDocumentEvent(final String encoding, final String version, final boolean standalone) {
        this.fEncodingSchemeSet = false;
        this.fStandaloneSet = false;
        this.nestedCall = false;
        this.fStandaloneSet = true;
        this.init(encoding, version, standalone, null);
    }
    
    public StartDocumentEvent(final String encoding, final String version, final boolean standalone, final Location loc) {
        this.fEncodingSchemeSet = false;
        this.fStandaloneSet = false;
        this.nestedCall = false;
        this.fStandaloneSet = true;
        this.init(encoding, version, standalone, loc);
    }
    
    protected void init(final String encoding, final String version, final boolean standalone, final Location loc) {
        this.setEventType(7);
        this.fEncodingScheam = encoding;
        this.fVersion = version;
        this.fStandalone = standalone;
        if (encoding != null && !encoding.equals("")) {
            this.fEncodingSchemeSet = true;
        }
        else {
            this.fEncodingSchemeSet = false;
            this.fEncodingScheam = "UTF-8";
        }
        this.fLocation = loc;
    }
    
    @Override
    public String getSystemId() {
        if (this.fLocation == null) {
            return "";
        }
        return this.fLocation.getSystemId();
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return this.fEncodingScheam;
    }
    
    @Override
    public boolean isStandalone() {
        return this.fStandalone;
    }
    
    @Override
    public String getVersion() {
        return this.fVersion;
    }
    
    public void setStandalone(final boolean flag) {
        this.fStandaloneSet = true;
        this.fStandalone = flag;
    }
    
    public void setStandalone(final String s) {
        this.fStandaloneSet = true;
        if (s == null) {
            this.fStandalone = true;
            return;
        }
        if (s.equals("yes")) {
            this.fStandalone = true;
        }
        else {
            this.fStandalone = false;
        }
    }
    
    @Override
    public boolean encodingSet() {
        return this.fEncodingSchemeSet;
    }
    
    @Override
    public boolean standaloneSet() {
        return this.fStandaloneSet;
    }
    
    public void setEncoding(final String encoding) {
        this.fEncodingScheam = encoding;
    }
    
    void setDeclaredEncoding(final boolean value) {
        this.fEncodingSchemeSet = value;
    }
    
    public void setVersion(final String s) {
        this.fVersion = s;
    }
    
    void clear() {
        this.fEncodingScheam = "UTF-8";
        this.fStandalone = true;
        this.fVersion = "1.0";
        this.fEncodingSchemeSet = false;
        this.fStandaloneSet = false;
    }
    
    @Override
    public String toString() {
        String s = "<?xml version=\"" + this.fVersion + "\"";
        s = s + " encoding='" + this.fEncodingScheam + "'";
        if (this.fStandaloneSet) {
            if (this.fStandalone) {
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
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write(this.toString());
    }
}
