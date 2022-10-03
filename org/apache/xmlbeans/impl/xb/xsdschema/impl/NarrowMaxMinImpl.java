package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.apache.xmlbeans.impl.values.JavaIntegerHolderEx;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.NarrowMaxMin;

public class NarrowMaxMinImpl extends LocalElementImpl implements NarrowMaxMin
{
    private static final long serialVersionUID = 1L;
    
    public NarrowMaxMinImpl(final SchemaType sType) {
        super(sType);
    }
    
    public static class MinOccursImpl extends JavaIntegerHolderEx implements MinOccurs
    {
        private static final long serialVersionUID = 1L;
        
        public MinOccursImpl(final SchemaType sType) {
            super(sType, false);
        }
        
        protected MinOccursImpl(final SchemaType sType, final boolean b) {
            super(sType, b);
        }
    }
    
    public static class MaxOccursImpl extends XmlUnionImpl implements MaxOccurs, XmlNonNegativeInteger, AllNNI.Member
    {
        private static final long serialVersionUID = 1L;
        
        public MaxOccursImpl(final SchemaType sType) {
            super(sType, false);
        }
        
        protected MaxOccursImpl(final SchemaType sType, final boolean b) {
            super(sType, b);
        }
    }
}
