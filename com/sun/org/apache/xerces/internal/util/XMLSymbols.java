package com.sun.org.apache.xerces.internal.util;

public class XMLSymbols
{
    public static final String EMPTY_STRING;
    public static final String PREFIX_XML;
    public static final String PREFIX_XMLNS;
    public static final String fANYSymbol;
    public static final String fCDATASymbol;
    public static final String fIDSymbol;
    public static final String fIDREFSymbol;
    public static final String fIDREFSSymbol;
    public static final String fENTITYSymbol;
    public static final String fENTITIESSymbol;
    public static final String fNMTOKENSymbol;
    public static final String fNMTOKENSSymbol;
    public static final String fNOTATIONSymbol;
    public static final String fENUMERATIONSymbol;
    public static final String fIMPLIEDSymbol;
    public static final String fREQUIREDSymbol;
    public static final String fFIXEDSymbol;
    
    static {
        EMPTY_STRING = "".intern();
        PREFIX_XML = "xml".intern();
        PREFIX_XMLNS = "xmlns".intern();
        fANYSymbol = "ANY".intern();
        fCDATASymbol = "CDATA".intern();
        fIDSymbol = "ID".intern();
        fIDREFSymbol = "IDREF".intern();
        fIDREFSSymbol = "IDREFS".intern();
        fENTITYSymbol = "ENTITY".intern();
        fENTITIESSymbol = "ENTITIES".intern();
        fNMTOKENSymbol = "NMTOKEN".intern();
        fNMTOKENSSymbol = "NMTOKENS".intern();
        fNOTATIONSymbol = "NOTATION".intern();
        fENUMERATIONSymbol = "ENUMERATION".intern();
        fIMPLIEDSymbol = "#IMPLIED".intern();
        fREQUIREDSymbol = "#REQUIRED".intern();
        fFIXEDSymbol = "#FIXED".intern();
    }
}
