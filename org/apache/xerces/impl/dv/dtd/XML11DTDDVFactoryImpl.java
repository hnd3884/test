package org.apache.xerces.impl.dv.dtd;

import java.util.Iterator;
import java.util.Map;
import org.apache.xerces.impl.dv.DatatypeValidator;
import java.util.Hashtable;

public class XML11DTDDVFactoryImpl extends DTDDVFactoryImpl
{
    static final Hashtable fXML11BuiltInTypes;
    
    public DatatypeValidator getBuiltInDV(final String s) {
        if (XML11DTDDVFactoryImpl.fXML11BuiltInTypes.get(s) != null) {
            return XML11DTDDVFactoryImpl.fXML11BuiltInTypes.get(s);
        }
        return XML11DTDDVFactoryImpl.fBuiltInTypes.get(s);
    }
    
    public Hashtable getBuiltInTypes() {
        final Hashtable hashtable = (Hashtable)XML11DTDDVFactoryImpl.fBuiltInTypes.clone();
        final Iterator iterator = XML11DTDDVFactoryImpl.fXML11BuiltInTypes.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry)iterator.next();
            hashtable.put(entry.getKey(), entry.getValue());
        }
        return hashtable;
    }
    
    static {
        (fXML11BuiltInTypes = new Hashtable()).put("XML11ID", new XML11IDDatatypeValidator());
        final XML11IDREFDatatypeValidator xml11IDREFDatatypeValidator = new XML11IDREFDatatypeValidator();
        XML11DTDDVFactoryImpl.fXML11BuiltInTypes.put("XML11IDREF", xml11IDREFDatatypeValidator);
        XML11DTDDVFactoryImpl.fXML11BuiltInTypes.put("XML11IDREFS", new ListDatatypeValidator(xml11IDREFDatatypeValidator));
        final XML11NMTOKENDatatypeValidator xml11NMTOKENDatatypeValidator = new XML11NMTOKENDatatypeValidator();
        XML11DTDDVFactoryImpl.fXML11BuiltInTypes.put("XML11NMTOKEN", xml11NMTOKENDatatypeValidator);
        XML11DTDDVFactoryImpl.fXML11BuiltInTypes.put("XML11NMTOKENS", new ListDatatypeValidator(xml11NMTOKENDatatypeValidator));
    }
}
