package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.xs.datatypes.XSQName;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class QNameDV extends TypeValidator
{
    private static final String EMPTY_STRING;
    
    @Override
    public short getAllowedFacets() {
        return 2079;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        final int colonptr = content.indexOf(":");
        String prefix;
        String localpart;
        if (colonptr > 0) {
            prefix = context.getSymbol(content.substring(0, colonptr));
            localpart = content.substring(colonptr + 1);
        }
        else {
            prefix = QNameDV.EMPTY_STRING;
            localpart = content;
        }
        if (prefix.length() > 0 && !XMLChar.isValidNCName(prefix)) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "QName" });
        }
        if (!XMLChar.isValidNCName(localpart)) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "QName" });
        }
        final String uri = context.getURI(prefix);
        if (prefix.length() > 0 && uri == null) {
            throw new InvalidDatatypeValueException("UndeclaredPrefix", new Object[] { content, prefix });
        }
        return new XQName(prefix, context.getSymbol(localpart), context.getSymbol(content), uri);
    }
    
    @Override
    public int getDataLength(final Object value) {
        return ((XQName)value).rawname.length();
    }
    
    static {
        EMPTY_STRING = "".intern();
    }
    
    private static final class XQName extends QName implements XSQName
    {
        public XQName(final String prefix, final String localpart, final String rawname, final String uri) {
            this.setValues(prefix, localpart, rawname, uri);
        }
        
        @Override
        public boolean equals(final Object object) {
            if (object instanceof QName) {
                final QName qname = (QName)object;
                return this.uri == qname.uri && this.localpart == qname.localpart;
            }
            return false;
        }
        
        @Override
        public synchronized String toString() {
            return this.rawname;
        }
        
        @Override
        public javax.xml.namespace.QName getJAXPQName() {
            return new javax.xml.namespace.QName(this.uri, this.localpart, this.prefix);
        }
        
        @Override
        public QName getXNIQName() {
            return this;
        }
    }
}
