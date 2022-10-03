package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.DatatypeValidator;
import java.util.Hashtable;
import org.apache.xerces.impl.dv.DTDDVFactory;

public class DTDDVFactoryImpl extends DTDDVFactory
{
    static final Hashtable fBuiltInTypes;
    
    public DatatypeValidator getBuiltInDV(final String s) {
        return DTDDVFactoryImpl.fBuiltInTypes.get(s);
    }
    
    public Hashtable getBuiltInTypes() {
        return (Hashtable)DTDDVFactoryImpl.fBuiltInTypes.clone();
    }
    
    static void createBuiltInTypes() {
        DTDDVFactoryImpl.fBuiltInTypes.put("string", new StringDatatypeValidator());
        DTDDVFactoryImpl.fBuiltInTypes.put("ID", new IDDatatypeValidator());
        final IDREFDatatypeValidator idrefDatatypeValidator = new IDREFDatatypeValidator();
        DTDDVFactoryImpl.fBuiltInTypes.put("IDREF", idrefDatatypeValidator);
        DTDDVFactoryImpl.fBuiltInTypes.put("IDREFS", new ListDatatypeValidator(idrefDatatypeValidator));
        final ENTITYDatatypeValidator entityDatatypeValidator = new ENTITYDatatypeValidator();
        DTDDVFactoryImpl.fBuiltInTypes.put("ENTITY", new ENTITYDatatypeValidator());
        DTDDVFactoryImpl.fBuiltInTypes.put("ENTITIES", new ListDatatypeValidator(entityDatatypeValidator));
        DTDDVFactoryImpl.fBuiltInTypes.put("NOTATION", new NOTATIONDatatypeValidator());
        final NMTOKENDatatypeValidator nmtokenDatatypeValidator = new NMTOKENDatatypeValidator();
        DTDDVFactoryImpl.fBuiltInTypes.put("NMTOKEN", nmtokenDatatypeValidator);
        DTDDVFactoryImpl.fBuiltInTypes.put("NMTOKENS", new ListDatatypeValidator(nmtokenDatatypeValidator));
    }
    
    static {
        fBuiltInTypes = new Hashtable();
        createBuiltInTypes();
    }
}
