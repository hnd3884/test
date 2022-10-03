package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import java.util.Collections;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Map;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;

public class DTDDVFactoryImpl extends DTDDVFactory
{
    static final Map<String, DatatypeValidator> fBuiltInTypes;
    
    @Override
    public DatatypeValidator getBuiltInDV(final String name) {
        return DTDDVFactoryImpl.fBuiltInTypes.get(name);
    }
    
    @Override
    public Map<String, DatatypeValidator> getBuiltInTypes() {
        return new HashMap<String, DatatypeValidator>(DTDDVFactoryImpl.fBuiltInTypes);
    }
    
    static {
        final Map<String, DatatypeValidator> builtInTypes = new HashMap<String, DatatypeValidator>();
        builtInTypes.put("string", new StringDatatypeValidator());
        builtInTypes.put("ID", new IDDatatypeValidator());
        DatatypeValidator dvTemp = new IDREFDatatypeValidator();
        builtInTypes.put("IDREF", dvTemp);
        builtInTypes.put("IDREFS", new ListDatatypeValidator(dvTemp));
        dvTemp = new ENTITYDatatypeValidator();
        builtInTypes.put("ENTITY", new ENTITYDatatypeValidator());
        builtInTypes.put("ENTITIES", new ListDatatypeValidator(dvTemp));
        builtInTypes.put("NOTATION", new NOTATIONDatatypeValidator());
        dvTemp = new NMTOKENDatatypeValidator();
        builtInTypes.put("NMTOKEN", dvTemp);
        builtInTypes.put("NMTOKENS", new ListDatatypeValidator(dvTemp));
        fBuiltInTypes = Collections.unmodifiableMap((Map<? extends String, ? extends DatatypeValidator>)builtInTypes);
    }
}
