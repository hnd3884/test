package org.apache.xmlbeans.impl.jam.internal.parser;

import org.apache.xmlbeans.impl.jam.mutable.MParameter;
import org.apache.xmlbeans.impl.jam.mutable.MInvokable;

class ParamStruct
{
    private String mName;
    private String mType;
    
    public ParamStruct(final String type, final String name) {
        this.init(type, name);
    }
    
    public void init(final String type, final String name) {
        this.mType = type;
        this.mName = name;
    }
    
    public MParameter createParameter(final MInvokable e) {
        if (e == null) {
            throw new IllegalArgumentException("null invokable");
        }
        final MParameter param = e.addNewParameter();
        param.setSimpleName(this.mName);
        param.setUnqualifiedType(this.mType);
        return param;
    }
}
