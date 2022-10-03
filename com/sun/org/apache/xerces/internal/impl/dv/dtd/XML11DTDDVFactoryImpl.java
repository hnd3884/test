package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import java.util.Collections;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Map;

public class XML11DTDDVFactoryImpl extends DTDDVFactoryImpl
{
    static Map<String, DatatypeValidator> XML11BUILTINTYPES;
    
    @Override
    public DatatypeValidator getBuiltInDV(final String name) {
        if (XML11DTDDVFactoryImpl.XML11BUILTINTYPES.get(name) != null) {
            return XML11DTDDVFactoryImpl.XML11BUILTINTYPES.get(name);
        }
        return XML11DTDDVFactoryImpl.fBuiltInTypes.get(name);
    }
    
    @Override
    public Map<String, DatatypeValidator> getBuiltInTypes() {
        final HashMap<String, DatatypeValidator> toReturn = new HashMap<String, DatatypeValidator>(XML11DTDDVFactoryImpl.fBuiltInTypes);
        toReturn.putAll(XML11DTDDVFactoryImpl.XML11BUILTINTYPES);
        return toReturn;
    }
    
    static {
        final Map<String, DatatypeValidator> xml11BuiltInTypes = new HashMap<String, DatatypeValidator>();
        xml11BuiltInTypes.put("XML11ID", new XML11IDDatatypeValidator());
        DatatypeValidator dvTemp = new XML11IDREFDatatypeValidator();
        xml11BuiltInTypes.put("XML11IDREF", dvTemp);
        xml11BuiltInTypes.put("XML11IDREFS", new ListDatatypeValidator(dvTemp));
        dvTemp = new XML11NMTOKENDatatypeValidator();
        xml11BuiltInTypes.put("XML11NMTOKEN", dvTemp);
        xml11BuiltInTypes.put("XML11NMTOKENS", new ListDatatypeValidator(dvTemp));
        XML11DTDDVFactoryImpl.XML11BUILTINTYPES = Collections.unmodifiableMap((Map<? extends String, ? extends DatatypeValidator>)xml11BuiltInTypes);
    }
}
