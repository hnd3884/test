package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.xb.xsdschema.SimpleDerivationSet.Member2;
import org.apache.xmlbeans.impl.values.XmlListImpl;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleDerivationSet;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;

public class SimpleDerivationSetImpl extends XmlUnionImpl implements SimpleDerivationSet, Member, Member2
{
    private static final long serialVersionUID = 1L;
    
    public SimpleDerivationSetImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected SimpleDerivationSetImpl(final SchemaType sType, final boolean b) {
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
    
    public static class MemberImpl2 extends XmlListImpl implements Member2
    {
        private static final long serialVersionUID = 1L;
        
        public MemberImpl2(final SchemaType sType) {
            super(sType, false);
        }
        
        protected MemberImpl2(final SchemaType sType, final boolean b) {
            super(sType, b);
        }
        
        public static class ItemImpl extends JavaStringEnumerationHolderEx implements Item
        {
            private static final long serialVersionUID = 1L;
            
            public ItemImpl(final SchemaType sType) {
                super(sType, false);
            }
            
            protected ItemImpl(final SchemaType sType, final boolean b) {
                super(sType, b);
            }
        }
    }
}
