package org.apache.xmlbeans;

import javax.xml.namespace.QName;

public class BindingConfig
{
    private static final InterfaceExtension[] EMPTY_INTERFACE_EXT_ARRAY;
    private static final PrePostExtension[] EMPTY_PREPOST_EXT_ARRAY;
    private static final UserType[] EMPTY_USER_TYPE_ARRY;
    public static final int QNAME_TYPE = 1;
    public static final int QNAME_DOCUMENT_TYPE = 2;
    public static final int QNAME_ACCESSOR_ELEMENT = 3;
    public static final int QNAME_ACCESSOR_ATTRIBUTE = 4;
    
    public String lookupPackageForNamespace(final String uri) {
        return null;
    }
    
    public String lookupPrefixForNamespace(final String uri) {
        return null;
    }
    
    public String lookupSuffixForNamespace(final String uri) {
        return null;
    }
    
    @Deprecated
    public String lookupJavanameForQName(final QName qname) {
        return null;
    }
    
    public String lookupJavanameForQName(final QName qname, final int kind) {
        return null;
    }
    
    public InterfaceExtension[] getInterfaceExtensions() {
        return BindingConfig.EMPTY_INTERFACE_EXT_ARRAY;
    }
    
    public InterfaceExtension[] getInterfaceExtensions(final String fullJavaName) {
        return BindingConfig.EMPTY_INTERFACE_EXT_ARRAY;
    }
    
    public PrePostExtension[] getPrePostExtensions() {
        return BindingConfig.EMPTY_PREPOST_EXT_ARRAY;
    }
    
    public PrePostExtension getPrePostExtension(final String fullJavaName) {
        return null;
    }
    
    public UserType[] getUserTypes() {
        return BindingConfig.EMPTY_USER_TYPE_ARRY;
    }
    
    public UserType lookupUserTypeForQName(final QName qname) {
        return null;
    }
    
    static {
        EMPTY_INTERFACE_EXT_ARRAY = new InterfaceExtension[0];
        EMPTY_PREPOST_EXT_ARRAY = new PrePostExtension[0];
        EMPTY_USER_TYPE_ARRY = new UserType[0];
    }
}
