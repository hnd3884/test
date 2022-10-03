package com.sun.xml.internal.stream.events;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Characters;

public class CharacterEvent extends DummyEvent implements Characters
{
    private String fData;
    private boolean fIsCData;
    private boolean fIsIgnorableWhitespace;
    private boolean fIsSpace;
    private boolean fCheckIfSpaceNeeded;
    
    public CharacterEvent() {
        this.fIsSpace = false;
        this.fCheckIfSpaceNeeded = true;
        this.fIsCData = false;
        this.init();
    }
    
    public CharacterEvent(final String data) {
        this.fIsSpace = false;
        this.fCheckIfSpaceNeeded = true;
        this.fIsCData = false;
        this.init();
        this.fData = data;
    }
    
    public CharacterEvent(final String data, final boolean flag) {
        this.fIsSpace = false;
        this.fCheckIfSpaceNeeded = true;
        this.init();
        this.fData = data;
        this.fIsCData = flag;
    }
    
    public CharacterEvent(final String data, final boolean flag, final boolean isIgnorableWhiteSpace) {
        this.fIsSpace = false;
        this.fCheckIfSpaceNeeded = true;
        this.init();
        this.fData = data;
        this.fIsCData = flag;
        this.fIsIgnorableWhitespace = isIgnorableWhiteSpace;
    }
    
    protected void init() {
        this.setEventType(4);
    }
    
    @Override
    public String getData() {
        return this.fData;
    }
    
    public void setData(final String data) {
        this.fData = data;
        this.fCheckIfSpaceNeeded = true;
    }
    
    @Override
    public boolean isCData() {
        return this.fIsCData;
    }
    
    @Override
    public String toString() {
        if (this.fIsCData) {
            return "<![CDATA[" + this.getData() + "]]>";
        }
        return this.fData;
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        if (this.fIsCData) {
            writer.write("<![CDATA[" + this.getData() + "]]>");
        }
        else {
            this.charEncode(writer, this.fData);
        }
    }
    
    @Override
    public boolean isIgnorableWhiteSpace() {
        return this.fIsIgnorableWhitespace;
    }
    
    @Override
    public boolean isWhiteSpace() {
        if (this.fCheckIfSpaceNeeded) {
            this.checkWhiteSpace();
            this.fCheckIfSpaceNeeded = false;
        }
        return this.fIsSpace;
    }
    
    private void checkWhiteSpace() {
        if (this.fData != null && this.fData.length() > 0) {
            this.fIsSpace = true;
            for (int i = 0; i < this.fData.length(); ++i) {
                if (!XMLChar.isSpace(this.fData.charAt(i))) {
                    this.fIsSpace = false;
                    break;
                }
            }
        }
    }
}
