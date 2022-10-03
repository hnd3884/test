package com.sun.org.apache.xerces.internal.impl.dv;

import java.util.Map;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl;

public abstract class DTDDVFactory
{
    private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl";
    private static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
    
    public static final DTDDVFactory getInstance() throws DVFactoryException {
        return getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl");
    }
    
    public static final DTDDVFactory getInstance(final String factoryClass) throws DVFactoryException {
        try {
            if ("com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl".equals(factoryClass)) {
                return new DTDDVFactoryImpl();
            }
            if ("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl".equals(factoryClass)) {
                return new XML11DTDDVFactoryImpl();
            }
            return (DTDDVFactory)ObjectFactory.newInstance(factoryClass, true);
        }
        catch (final ClassCastException e) {
            throw new DVFactoryException("DTD factory class " + factoryClass + " does not extend from DTDDVFactory.");
        }
    }
    
    protected DTDDVFactory() {
    }
    
    public abstract DatatypeValidator getBuiltInDV(final String p0);
    
    public abstract Map<String, DatatypeValidator> getBuiltInTypes();
}
