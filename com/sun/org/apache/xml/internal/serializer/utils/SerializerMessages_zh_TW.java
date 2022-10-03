package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;

public class SerializerMessages_zh_TW extends ListResourceBundle
{
    public Object[][] getContents() {
        final Object[][] contents = { { "BAD_MSGKEY", "\u8a0a\u606f\u7d22\u5f15\u9375 ''{0}'' \u7684\u8a0a\u606f\u985e\u5225\u4e0d\u662f ''{1}''" }, { "BAD_MSGFORMAT", "\u8a0a\u606f\u985e\u5225 ''{1}'' \u4e2d\u7684\u8a0a\u606f ''{0}'' \u683c\u5f0f\u4e0d\u6b63\u78ba\u3002" }, { "ER_SERIALIZER_NOT_CONTENTHANDLER", "serializer \u985e\u5225 ''{0}'' \u4e0d\u5be6\u884c org.xml.sax.ContentHandler\u3002" }, { "ER_RESOURCE_COULD_NOT_FIND", "\u627e\u4e0d\u5230\u8cc7\u6e90 [ {0} ]\u3002\n{1}" }, { "ER_RESOURCE_COULD_NOT_LOAD", "\u7121\u6cd5\u8f09\u5165\u8cc7\u6e90 [ {0} ]: {1} \n {2} \t {3}" }, { "ER_BUFFER_SIZE_LESSTHAN_ZERO", "\u7de9\u885d\u5340\u5927\u5c0f <=0" }, { "ER_INVALID_UTF16_SURROGATE", "\u5075\u6e2c\u5230\u7121\u6548\u7684 UTF-16 \u4ee3\u7406: {0}\uff1f" }, { "ER_OIERROR", "IO \u932f\u8aa4" }, { "ER_ILLEGAL_ATTRIBUTE_POSITION", "\u5728\u7522\u751f\u5b50\u9805\u7bc0\u9ede\u4e4b\u5f8c\uff0c\u6216\u5728\u7522\u751f\u5143\u7d20\u4e4b\u524d\uff0c\u4e0d\u53ef\u65b0\u589e\u5c6c\u6027 {0}\u3002\u5c6c\u6027\u6703\u88ab\u5ffd\u7565\u3002" }, { "ER_NAMESPACE_PREFIX", "\u5b57\u9996 ''{0}'' \u7684\u547d\u540d\u7a7a\u9593\u5c1a\u672a\u5ba3\u544a\u3002" }, { "ER_STRAY_ATTRIBUTE", "\u5c6c\u6027 ''{0}'' \u5728\u5143\u7d20\u4e4b\u5916\u3002" }, { "ER_STRAY_NAMESPACE", "\u547d\u540d\u7a7a\u9593\u5ba3\u544a ''{0}''=''{1}'' \u8d85\u51fa\u5143\u7d20\u5916\u3002" }, { "ER_COULD_NOT_LOAD_RESOURCE", "\u7121\u6cd5\u8f09\u5165 ''{0}'' (\u6aa2\u67e5 CLASSPATH)\uff0c\u76ee\u524d\u53ea\u4f7f\u7528\u9810\u8a2d\u503c" }, { "ER_ILLEGAL_CHARACTER", "\u5617\u8a66\u8f38\u51fa\u6574\u6578\u503c {0} \u7684\u5b57\u5143\uff0c\u4f46\u662f\u5b83\u4e0d\u662f\u4ee5\u6307\u5b9a\u7684 {1} \u8f38\u51fa\u7de8\u78bc\u5448\u73fe\u3002" }, { "ER_COULD_NOT_LOAD_METHOD_PROPERTY", "\u7121\u6cd5\u8f09\u5165\u8f38\u51fa\u65b9\u6cd5 ''{1}'' \u7684\u5c6c\u6027\u6a94 ''{0}'' (\u6aa2\u67e5 CLASSPATH)" }, { "ER_INVALID_PORT", "\u7121\u6548\u7684\u9023\u63a5\u57e0\u865f\u78bc" }, { "ER_PORT_WHEN_HOST_NULL", "\u4e3b\u6a5f\u70ba\u7a7a\u503c\u6642\uff0c\u7121\u6cd5\u8a2d\u5b9a\u9023\u63a5\u57e0" }, { "ER_HOST_ADDRESS_NOT_WELLFORMED", "\u4e3b\u6a5f\u6c92\u6709\u5b8c\u6574\u7684\u4f4d\u5740" }, { "ER_SCHEME_NOT_CONFORMANT", "\u914d\u7f6e\u4e0d\u4e00\u81f4\u3002" }, { "ER_SCHEME_FROM_NULL_STRING", "\u7121\u6cd5\u5f9e\u7a7a\u503c\u5b57\u4e32\u8a2d\u5b9a\u914d\u7f6e" }, { "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", "\u8def\u5f91\u5305\u542b\u7121\u6548\u7684\u9041\u96e2\u5e8f\u5217" }, { "ER_PATH_INVALID_CHAR", "\u8def\u5f91\u5305\u542b\u7121\u6548\u7684\u5b57\u5143: {0}" }, { "ER_FRAG_INVALID_CHAR", "\u7247\u6bb5\u5305\u542b\u7121\u6548\u7684\u5b57\u5143" }, { "ER_FRAG_WHEN_PATH_NULL", "\u8def\u5f91\u70ba\u7a7a\u503c\u6642\uff0c\u7121\u6cd5\u8a2d\u5b9a\u7247\u6bb5" }, { "ER_FRAG_FOR_GENERIC_URI", "\u53ea\u80fd\u5c0d\u4e00\u822c URI \u8a2d\u5b9a\u7247\u6bb5" }, { "ER_NO_SCHEME_IN_URI", "\u5728 URI \u627e\u4e0d\u5230\u914d\u7f6e" }, { "ER_CANNOT_INIT_URI_EMPTY_PARMS", "\u7121\u6cd5\u4ee5\u7a7a\u767d\u53c3\u6578\u8d77\u59cb\u8a2d\u5b9a URI" }, { "ER_NO_FRAGMENT_STRING_IN_PATH", "\u8def\u5f91\u548c\u7247\u6bb5\u4e0d\u80fd\u540c\u6642\u6307\u5b9a\u7247\u6bb5" }, { "ER_NO_QUERY_STRING_IN_PATH", "\u5728\u8def\u5f91\u53ca\u67e5\u8a62\u5b57\u4e32\u4e2d\u4e0d\u53ef\u6307\u5b9a\u67e5\u8a62\u5b57\u4e32" }, { "ER_NO_PORT_IF_NO_HOST", "\u5982\u679c\u6c92\u6709\u6307\u5b9a\u4e3b\u6a5f\uff0c\u4e0d\u53ef\u6307\u5b9a\u9023\u63a5\u57e0" }, { "ER_NO_USERINFO_IF_NO_HOST", "\u5982\u679c\u6c92\u6709\u6307\u5b9a\u4e3b\u6a5f\uff0c\u4e0d\u53ef\u6307\u5b9a Userinfo" }, { "ER_XML_VERSION_NOT_SUPPORTED", "\u8b66\u544a:  \u8981\u6c42\u7684\u8f38\u51fa\u6587\u4ef6\u7248\u672c\u70ba ''{0}''\u3002\u4e0d\u652f\u63f4\u6b64\u7248\u672c\u7684 XML\u3002\u8f38\u51fa\u6587\u4ef6\u7684\u7248\u672c\u5c07\u6703\u662f ''1.0''\u3002" }, { "ER_SCHEME_REQUIRED", "\u5fc5\u9808\u6709\u914d\u7f6e\uff01" }, { "ER_FACTORY_PROPERTY_MISSING", "\u50b3\u905e\u7d66 SerializerFactory \u7684 Properties \u7269\u4ef6\u6c92\u6709 ''{0}'' \u5c6c\u6027\u3002" }, { "ER_ENCODING_NOT_SUPPORTED", "\u8b66\u544a:  Java Runtime \u4e0d\u652f\u63f4\u7de8\u78bc ''{0}''\u3002" } };
        return contents;
    }
}
