package com.fasterxml.jackson.dataformat.xml.deser;

import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.JsonLocation;
import java.util.Set;
import com.fasterxml.jackson.core.JsonStreamContext;

public final class XmlReadContext extends JsonStreamContext
{
    protected final XmlReadContext _parent;
    protected int _lineNr;
    protected int _columnNr;
    protected String _currentName;
    protected Object _currentValue;
    protected Set<String> _namesToWrap;
    protected String _wrappedName;
    protected XmlReadContext _child;
    
    public XmlReadContext(final XmlReadContext parent, final int type, final int lineNr, final int colNr) {
        this._child = null;
        this._type = type;
        this._parent = parent;
        this._lineNr = lineNr;
        this._columnNr = colNr;
        this._index = -1;
    }
    
    protected final void reset(final int type, final int lineNr, final int colNr) {
        this._type = type;
        this._index = -1;
        this._lineNr = lineNr;
        this._columnNr = colNr;
        this._currentName = null;
        this._currentValue = null;
        this._namesToWrap = null;
    }
    
    public Object getCurrentValue() {
        return this._currentValue;
    }
    
    public void setCurrentValue(final Object v) {
        this._currentValue = v;
    }
    
    public static XmlReadContext createRootContext(final int lineNr, final int colNr) {
        return new XmlReadContext(null, 0, lineNr, colNr);
    }
    
    public static XmlReadContext createRootContext() {
        return new XmlReadContext(null, 0, 1, 0);
    }
    
    public final XmlReadContext createChildArrayContext(final int lineNr, final int colNr) {
        XmlReadContext ctxt = this._child;
        if (ctxt == null) {
            ctxt = (this._child = new XmlReadContext(this, 1, lineNr, colNr));
            return ctxt;
        }
        ctxt.reset(1, lineNr, colNr);
        return ctxt;
    }
    
    public final XmlReadContext createChildObjectContext(final int lineNr, final int colNr) {
        XmlReadContext ctxt = this._child;
        if (ctxt == null) {
            ctxt = (this._child = new XmlReadContext(this, 2, lineNr, colNr));
            return ctxt;
        }
        ctxt.reset(2, lineNr, colNr);
        return ctxt;
    }
    
    public final String getCurrentName() {
        return this._currentName;
    }
    
    public boolean hasCurrentName() {
        return this._currentName != null;
    }
    
    public final XmlReadContext getParent() {
        return this._parent;
    }
    
    public final boolean expectComma() {
        throw new UnsupportedOperationException();
    }
    
    public void setCurrentName(final String name) {
        this._currentName = name;
    }
    
    public final JsonLocation getStartLocation(final Object srcRef) {
        final long totalChars = -1L;
        return new JsonLocation(srcRef, totalChars, this._lineNr, this._columnNr);
    }
    
    public void setNamesToWrap(final Set<String> namesToWrap) {
        this._namesToWrap = namesToWrap;
    }
    
    @Deprecated
    public Set<String> getNamesToWrap() {
        return this._namesToWrap;
    }
    
    public boolean shouldWrap(final String localName) {
        return this._namesToWrap != null && this._namesToWrap.contains(localName);
    }
    
    protected void convertToArray() {
        this._type = 1;
    }
    
    public final String toString() {
        final StringBuilder sb = new StringBuilder(64);
        switch (this._type) {
            case 0: {
                sb.append("/");
                break;
            }
            case 1: {
                sb.append('[');
                sb.append(this.getCurrentIndex());
                sb.append(']');
                break;
            }
            case 2: {
                sb.append('{');
                if (this._currentName != null) {
                    sb.append('\"');
                    CharTypes.appendQuoted(sb, this._currentName);
                    sb.append('\"');
                }
                else {
                    sb.append('?');
                }
                sb.append('}');
                break;
            }
        }
        return sb.toString();
    }
}
