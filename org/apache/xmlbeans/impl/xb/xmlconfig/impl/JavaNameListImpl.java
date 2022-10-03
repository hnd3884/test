package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.impl.values.XmlListImpl;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xmlconfig.JavaNameList;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;

public class JavaNameListImpl extends XmlUnionImpl implements JavaNameList, Member, Member2
{
    private static final long serialVersionUID = 1L;
    
    public JavaNameListImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected JavaNameListImpl(final SchemaType sType, final boolean b) {
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
    }
}
