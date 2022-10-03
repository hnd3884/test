package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class WsdlmodelMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(final Object arg0, final Object arg1, final Object arg2) {
        return WsdlmodelMessages.messageFactory.getMessage("wsdl.portaddress.epraddress.not.match", arg0, arg1, arg2);
    }
    
    public static String WSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(final Object arg0, final Object arg1, final Object arg2) {
        return WsdlmodelMessages.localizer.localize(localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(arg0, arg1, arg2));
    }
    
    public static Localizable localizableWSDL_IMPORT_SHOULD_BE_WSDL(final Object arg0) {
        return WsdlmodelMessages.messageFactory.getMessage("wsdl.import.should.be.wsdl", arg0);
    }
    
    public static String WSDL_IMPORT_SHOULD_BE_WSDL(final Object arg0) {
        return WsdlmodelMessages.localizer.localize(localizableWSDL_IMPORT_SHOULD_BE_WSDL(arg0));
    }
    
    public static Localizable localizableMEX_METADATA_SYSTEMID_NULL() {
        return WsdlmodelMessages.messageFactory.getMessage("Mex.metadata.systemid.null", new Object[0]);
    }
    
    public static String MEX_METADATA_SYSTEMID_NULL() {
        return WsdlmodelMessages.localizer.localize(localizableMEX_METADATA_SYSTEMID_NULL());
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.wsdlmodel");
        localizer = new Localizer();
    }
}
