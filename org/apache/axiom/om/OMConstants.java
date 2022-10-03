package org.apache.axiom.om;

public interface OMConstants
{
    @Deprecated
    public static final short PUSH_TYPE_BUILDER = 0;
    @Deprecated
    public static final short PULL_TYPE_BUILDER = 1;
    @Deprecated
    public static final String ARRAY_ITEM_NSURI = "http://axis.apache.org/encoding/Arrays";
    @Deprecated
    public static final String ARRAY_ITEM_LOCALNAME = "item";
    @Deprecated
    public static final String ARRAY_ITEM_NS_PREFIX = "arrays";
    @Deprecated
    public static final String ARRAY_ITEM_QNAME = "arrays:item";
    public static final String DEFAULT_CHAR_SET_ENCODING = "utf-8";
    public static final String DEFAULT_XML_VERSION = "1.0";
    public static final String XMLNS_URI = "http://www.w3.org/XML/1998/namespace";
    @Deprecated
    public static final String XMLNS_NS_URI = "http://www.w3.org/2000/xmlns/";
    @Deprecated
    public static final String XMLNS_NS_PREFIX = "xmlns";
    public static final String XMLNS_PREFIX = "xml";
    @Deprecated
    public static final String IS_BINARY = "Axiom.IsBinary";
    @Deprecated
    public static final String DATA_HANDLER = "Axiom.DataHandler";
    @Deprecated
    public static final String IS_DATA_HANDLERS_AWARE = "IsDatahandlersAwareParsing";
    @Deprecated
    public static final String DEFAULT_DEFAULT_NAMESPACE = "\"\"";
    public static final String XMLATTRTYPE_CDATA = "CDATA";
    public static final String XMLATTRTYPE_ID = "ID";
    public static final String XMLATTRTYPE_IDREF = "IDREF";
    public static final String XMLATTRTYPE_IDREFS = "IDREFS";
    public static final String XMLATTRTYPE_NMTOKEN = "NMTOKEN";
    public static final String XMLATTRTYPE_NMTOKENS = "NMTOKENS";
    public static final String XMLATTRTYPE_ENTITY = "ENTITY";
    public static final String XMLATTRTYPE_ENTITIES = "ENTITIES";
    public static final String XMLATTRTYPE_NOTATION = "NOTATION";
}
