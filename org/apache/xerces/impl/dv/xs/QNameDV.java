package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.xs.datatypes.XSQName;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.dv.ValidationContext;

public class QNameDV extends TypeValidator
{
    private static final String EMPTY_STRING;
    
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        final int index = s.indexOf(":");
        String s2;
        String substring;
        if (index > 0) {
            s2 = validationContext.getSymbol(s.substring(0, index));
            substring = s.substring(index + 1);
        }
        else {
            s2 = QNameDV.EMPTY_STRING;
            substring = s;
        }
        final boolean b = validationContext.getDatatypeXMLVersion() == 1;
        Label_0116: {
            if (s2.length() > 0) {
                if (b) {
                    if (XMLChar.isValidNCName(s2)) {
                        break Label_0116;
                    }
                }
                else if (XML11Char.isXML11ValidNCName(s2)) {
                    break Label_0116;
                }
                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "QName" });
            }
        }
        Label_0163: {
            if (b) {
                if (XMLChar.isValidNCName(substring)) {
                    break Label_0163;
                }
            }
            else if (XML11Char.isXML11ValidNCName(substring)) {
                break Label_0163;
            }
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "QName" });
        }
        final String uri = validationContext.getURI(s2);
        if (s2.length() > 0 && uri == null) {
            throw new InvalidDatatypeValueException("UndeclaredPrefix", new Object[] { s, s2 });
        }
        return new XQName(s2, validationContext.getSymbol(substring), validationContext.getSymbol(s), uri);
    }
    
    public int getDataLength(final Object o) {
        return ((XQName)o).rawname.length();
    }
    
    static {
        EMPTY_STRING = "".intern();
    }
    
    private static final class XQName extends QName implements XSQName
    {
        public XQName(final String s, final String s2, final String s3, final String s4) {
            this.setValues(s, s2, s3, s4);
        }
        
        public boolean equals(final Object o) {
            if (o instanceof QName) {
                final QName qName = (QName)o;
                return this.uri == qName.uri && this.localpart == qName.localpart;
            }
            return false;
        }
        
        public String toString() {
            return this.rawname;
        }
        
        public javax.xml.namespace.QName getJAXPQName() {
            return new javax.xml.namespace.QName(this.uri, this.localpart, this.prefix);
        }
        
        public QName getXNIQName() {
            return this;
        }
    }
}
