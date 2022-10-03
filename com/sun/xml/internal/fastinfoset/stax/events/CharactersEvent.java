package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import javax.xml.stream.events.Characters;

public class CharactersEvent extends EventBase implements Characters
{
    private String _text;
    private boolean isCData;
    private boolean isSpace;
    private boolean isIgnorable;
    private boolean needtoCheck;
    
    public CharactersEvent() {
        super(4);
        this.isCData = false;
        this.isSpace = false;
        this.isIgnorable = false;
        this.needtoCheck = true;
    }
    
    public CharactersEvent(final String data) {
        super(4);
        this.isCData = false;
        this.isSpace = false;
        this.isIgnorable = false;
        this.needtoCheck = true;
        this._text = data;
    }
    
    public CharactersEvent(final String data, final boolean isCData) {
        super(4);
        this.isCData = false;
        this.isSpace = false;
        this.isIgnorable = false;
        this.needtoCheck = true;
        this._text = data;
        this.isCData = isCData;
    }
    
    @Override
    public String getData() {
        return this._text;
    }
    
    public void setData(final String data) {
        this._text = data;
    }
    
    @Override
    public boolean isCData() {
        return this.isCData;
    }
    
    @Override
    public String toString() {
        if (this.isCData) {
            return "<![CDATA[" + this._text + "]]>";
        }
        return this._text;
    }
    
    @Override
    public boolean isIgnorableWhiteSpace() {
        return this.isIgnorable;
    }
    
    @Override
    public boolean isWhiteSpace() {
        if (this.needtoCheck) {
            this.checkWhiteSpace();
            this.needtoCheck = false;
        }
        return this.isSpace;
    }
    
    public void setSpace(final boolean isSpace) {
        this.isSpace = isSpace;
        this.needtoCheck = false;
    }
    
    public void setIgnorable(final boolean isIgnorable) {
        this.isIgnorable = isIgnorable;
        this.setEventType(6);
    }
    
    private void checkWhiteSpace() {
        if (!Util.isEmptyString(this._text)) {
            this.isSpace = true;
            for (int i = 0; i < this._text.length(); ++i) {
                if (!XMLChar.isSpace(this._text.charAt(i))) {
                    this.isSpace = false;
                    break;
                }
            }
        }
    }
}
