package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonStreamContext;

public class TokenBufferReadContext extends JsonStreamContext
{
    protected final JsonStreamContext _parent;
    protected final JsonLocation _startLocation;
    protected String _currentName;
    protected Object _currentValue;
    
    protected TokenBufferReadContext(final JsonStreamContext base, final Object srcRef) {
        super(base);
        this._parent = base.getParent();
        this._currentName = base.getCurrentName();
        this._currentValue = base.getCurrentValue();
        if (base instanceof JsonReadContext) {
            final JsonReadContext rc = (JsonReadContext)base;
            this._startLocation = rc.getStartLocation(srcRef);
        }
        else {
            this._startLocation = JsonLocation.NA;
        }
    }
    
    protected TokenBufferReadContext(final JsonStreamContext base, final JsonLocation startLoc) {
        super(base);
        this._parent = base.getParent();
        this._currentName = base.getCurrentName();
        this._currentValue = base.getCurrentValue();
        this._startLocation = startLoc;
    }
    
    protected TokenBufferReadContext() {
        super(0, -1);
        this._parent = null;
        this._startLocation = JsonLocation.NA;
    }
    
    protected TokenBufferReadContext(final TokenBufferReadContext parent, final int type, final int index) {
        super(type, index);
        this._parent = parent;
        this._startLocation = parent._startLocation;
    }
    
    public Object getCurrentValue() {
        return this._currentValue;
    }
    
    public void setCurrentValue(final Object v) {
        this._currentValue = v;
    }
    
    public static TokenBufferReadContext createRootContext(final JsonStreamContext origContext) {
        if (origContext == null) {
            return new TokenBufferReadContext();
        }
        return new TokenBufferReadContext(origContext, null);
    }
    
    public TokenBufferReadContext createChildArrayContext() {
        ++this._index;
        return new TokenBufferReadContext(this, 1, -1);
    }
    
    public TokenBufferReadContext createChildObjectContext() {
        ++this._index;
        return new TokenBufferReadContext(this, 2, -1);
    }
    
    public TokenBufferReadContext parentOrCopy() {
        if (this._parent instanceof TokenBufferReadContext) {
            return (TokenBufferReadContext)this._parent;
        }
        if (this._parent == null) {
            return new TokenBufferReadContext();
        }
        return new TokenBufferReadContext(this._parent, this._startLocation);
    }
    
    public String getCurrentName() {
        return this._currentName;
    }
    
    public boolean hasCurrentName() {
        return this._currentName != null;
    }
    
    public JsonStreamContext getParent() {
        return this._parent;
    }
    
    public void setCurrentName(final String name) throws JsonProcessingException {
        this._currentName = name;
    }
    
    public void updateForValue() {
        ++this._index;
    }
}
