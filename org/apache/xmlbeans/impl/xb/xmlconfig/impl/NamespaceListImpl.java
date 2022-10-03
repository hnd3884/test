package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.impl.xb.xmlconfig.NamespaceList.Member2.Item;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.impl.xb.xmlconfig.NamespaceList.Member2;
import org.apache.xmlbeans.impl.values.XmlListImpl;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xmlconfig.NamespaceList;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;

public class NamespaceListImpl extends XmlUnionImpl implements NamespaceList, Member, Member2
{
    private static final long serialVersionUID = 1L;
    
    public NamespaceListImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected NamespaceListImpl(final SchemaType sType, final boolean b) {
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
        
        public static class ItemImpl extends XmlUnionImpl implements Item, XmlAnyURI, Item.Member
        {
            private static final long serialVersionUID = 1L;
            
            public ItemImpl(final SchemaType sType) {
                super(sType, false);
            }
            
            protected ItemImpl(final SchemaType sType, final boolean b) {
                super(sType, b);
            }
            
            public static class MemberImpl extends JavaStringEnumerationHolderEx implements Item.Member
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
    }
}
