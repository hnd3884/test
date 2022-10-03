package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;

public class AllNNIImpl extends XmlUnionImpl implements AllNNI, XmlNonNegativeInteger, Member
{
    private static final long serialVersionUID = 1L;
    
    public AllNNIImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected AllNNIImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
    
    public static class MemberImpl extends JavaStringEnumerationHolderEx implements Member
    {
        private static final long serialVersionUID = 1L;
        
        public MemberImpl(final SchemaType sType) {
            super(sType, false);
        }
        
        protected MemberImpl(final SchemaType sType, final boolean b) {
            super(sType, b);
        }
    }
}
