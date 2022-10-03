package org.apache.xmlbeans.impl.schema;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;

public class XmlValueRef
{
    XmlAnySimpleType _obj;
    SchemaType.Ref _typeref;
    Object _initVal;
    
    public XmlValueRef(final XmlAnySimpleType xobj) {
        if (xobj == null) {
            throw new IllegalArgumentException();
        }
        this._obj = xobj;
    }
    
    XmlValueRef(final SchemaType.Ref typeref, final Object initVal) {
        if (typeref == null) {
            throw new IllegalArgumentException();
        }
        this._typeref = typeref;
        this._initVal = initVal;
    }
    
    synchronized XmlAnySimpleType get() {
        if (this._obj == null) {
            final SchemaType type = this._typeref.get();
            if (type.getSimpleVariety() != 3) {
                this._obj = type.newValue(this._initVal);
            }
            else {
                final List actualVals = new ArrayList();
                for (final XmlValueRef ref : (List)this._initVal) {
                    actualVals.add(ref.get());
                }
                this._obj = type.newValue(actualVals);
            }
        }
        return this._obj;
    }
}
