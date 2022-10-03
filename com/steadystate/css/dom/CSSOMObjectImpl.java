package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import java.util.Hashtable;
import java.util.Map;
import java.io.Serializable;

public class CSSOMObjectImpl implements CSSOMObject, Serializable
{
    private static final long serialVersionUID = 0L;
    private Map<String, Object> userDataMap_;
    
    public Map<String, Object> getUserDataMap() {
        if (this.userDataMap_ == null) {
            this.userDataMap_ = new Hashtable<String, Object>();
        }
        return this.userDataMap_;
    }
    
    public void setUserDataMap(final Map<String, Object> userDataMap) {
        this.userDataMap_ = userDataMap;
    }
    
    public Object getUserData(final String key) {
        return this.getUserDataMap().get(key);
    }
    
    public Object setUserData(final String key, final Object data) {
        return this.getUserDataMap().put(key, data);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CSSOMObjectImpl)) {
            return false;
        }
        final CSSOMObjectImpl coi = (CSSOMObjectImpl)obj;
        return LangUtils.equals(this.userDataMap_, coi.userDataMap_);
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.userDataMap_);
        return hash;
    }
}
