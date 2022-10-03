package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;

public class SerializerMessages_zh_CN extends ListResourceBundle
{
    public Object[][] getContents() {
        final Object[][] contents = { { "BAD_MSGKEY", "\u6d88\u606f\u5173\u952e\u5b57 ''{0}'' \u4e0d\u5728\u6d88\u606f\u7c7b ''{1}'' \u4e2d" }, { "BAD_MSGFORMAT", "\u6d88\u606f\u7c7b ''{1}'' \u4e2d\u6d88\u606f ''{0}'' \u7684\u683c\u5f0f\u5316\u5931\u8d25\u3002" }, { "ER_SERIALIZER_NOT_CONTENTHANDLER", "\u4e32\u884c\u5668\u7c7b ''{0}'' \u4e0d\u5b9e\u73b0 org.xml.sax.ContentHandler\u3002" }, { "ER_RESOURCE_COULD_NOT_FIND", "\u627e\u4e0d\u5230\u8d44\u6e90 [ {0} ]\u3002\n {1}" }, { "ER_RESOURCE_COULD_NOT_LOAD", "\u8d44\u6e90 [ {0} ] \u65e0\u6cd5\u52a0\u8f7d: {1} \n {2} \t {3}" }, { "ER_BUFFER_SIZE_LESSTHAN_ZERO", "\u7f13\u51b2\u533a\u5927\u5c0f <=0" }, { "ER_INVALID_UTF16_SURROGATE", "\u68c0\u6d4b\u5230\u65e0\u6548\u7684 UTF-16 \u4ee3\u7406: {0}?" }, { "ER_OIERROR", "IO \u9519\u8bef" }, { "ER_ILLEGAL_ATTRIBUTE_POSITION", "\u5728\u751f\u6210\u5b50\u8282\u70b9\u4e4b\u540e\u6216\u5728\u751f\u6210\u5143\u7d20\u4e4b\u524d\u65e0\u6cd5\u6dfb\u52a0\u5c5e\u6027 {0}\u3002\u5c06\u5ffd\u7565\u5c5e\u6027\u3002" }, { "ER_NAMESPACE_PREFIX", "\u6ca1\u6709\u8bf4\u660e\u540d\u79f0\u7a7a\u95f4\u524d\u7f00 ''{0}''\u3002" }, { "ER_STRAY_ATTRIBUTE", "\u5c5e\u6027 ''{0}'' \u5728\u5143\u7d20\u5916\u90e8\u3002" }, { "ER_STRAY_NAMESPACE", "\u540d\u79f0\u7a7a\u95f4\u58f0\u660e ''{0}''=''{1}'' \u5728\u5143\u7d20\u5916\u90e8\u3002" }, { "ER_COULD_NOT_LOAD_RESOURCE", "\u65e0\u6cd5\u52a0\u8f7d ''{0}'' (\u68c0\u67e5 CLASSPATH), \u73b0\u5728\u53ea\u4f7f\u7528\u9ed8\u8ba4\u503c" }, { "ER_ILLEGAL_CHARACTER", "\u5c1d\u8bd5\u8f93\u51fa\u672a\u4ee5{1}\u7684\u6307\u5b9a\u8f93\u51fa\u7f16\u7801\u8868\u793a\u7684\u6574\u6570\u503c {0} \u7684\u5b57\u7b26\u3002" }, { "ER_COULD_NOT_LOAD_METHOD_PROPERTY", "\u65e0\u6cd5\u4e3a\u8f93\u51fa\u65b9\u6cd5 ''{1}'' \u52a0\u8f7d\u5c5e\u6027\u6587\u4ef6 ''{0}'' (\u68c0\u67e5 CLASSPATH)" }, { "ER_INVALID_PORT", "\u65e0\u6548\u7684\u7aef\u53e3\u53f7" }, { "ER_PORT_WHEN_HOST_NULL", "\u4e3b\u673a\u4e3a\u7a7a\u65f6, \u65e0\u6cd5\u8bbe\u7f6e\u7aef\u53e3" }, { "ER_HOST_ADDRESS_NOT_WELLFORMED", "\u4e3b\u673a\u4e0d\u662f\u683c\u5f0f\u826f\u597d\u7684\u5730\u5740" }, { "ER_SCHEME_NOT_CONFORMANT", "\u65b9\u6848\u4e0d\u4e00\u81f4\u3002" }, { "ER_SCHEME_FROM_NULL_STRING", "\u65e0\u6cd5\u4ece\u7a7a\u5b57\u7b26\u4e32\u8bbe\u7f6e\u65b9\u6848" }, { "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", "\u8def\u5f84\u5305\u542b\u65e0\u6548\u7684\u8f6c\u4e49\u5e8f\u5217" }, { "ER_PATH_INVALID_CHAR", "\u8def\u5f84\u5305\u542b\u65e0\u6548\u7684\u5b57\u7b26: {0}" }, { "ER_FRAG_INVALID_CHAR", "\u7247\u6bb5\u5305\u542b\u65e0\u6548\u7684\u5b57\u7b26" }, { "ER_FRAG_WHEN_PATH_NULL", "\u8def\u5f84\u4e3a\u7a7a\u65f6, \u65e0\u6cd5\u8bbe\u7f6e\u7247\u6bb5" }, { "ER_FRAG_FOR_GENERIC_URI", "\u53ea\u80fd\u4e3a\u4e00\u822c URI \u8bbe\u7f6e\u7247\u6bb5" }, { "ER_NO_SCHEME_IN_URI", "\u5728 URI \u4e2d\u627e\u4e0d\u5230\u65b9\u6848" }, { "ER_CANNOT_INIT_URI_EMPTY_PARMS", "\u65e0\u6cd5\u4ee5\u7a7a\u53c2\u6570\u521d\u59cb\u5316 URI" }, { "ER_NO_FRAGMENT_STRING_IN_PATH", "\u8def\u5f84\u548c\u7247\u6bb5\u4e2d\u90fd\u65e0\u6cd5\u6307\u5b9a\u7247\u6bb5" }, { "ER_NO_QUERY_STRING_IN_PATH", "\u8def\u5f84\u548c\u67e5\u8be2\u5b57\u7b26\u4e32\u4e2d\u4e0d\u80fd\u6307\u5b9a\u67e5\u8be2\u5b57\u7b26\u4e32" }, { "ER_NO_PORT_IF_NO_HOST", "\u5982\u679c\u6ca1\u6709\u6307\u5b9a\u4e3b\u673a, \u5219\u4e0d\u53ef\u4ee5\u6307\u5b9a\u7aef\u53e3" }, { "ER_NO_USERINFO_IF_NO_HOST", "\u5982\u679c\u6ca1\u6709\u6307\u5b9a\u4e3b\u673a, \u5219\u4e0d\u53ef\u4ee5\u6307\u5b9a Userinfo" }, { "ER_XML_VERSION_NOT_SUPPORTED", "\u8b66\u544a: \u8f93\u51fa\u6587\u6863\u7684\u7248\u672c\u5e94\u4e3a ''{0}''\u3002\u4e0d\u652f\u6301\u6b64\u7248\u672c\u7684 XML\u3002\u8f93\u51fa\u6587\u6863\u7684\u7248\u672c\u5c06\u4e3a ''1.0''\u3002" }, { "ER_SCHEME_REQUIRED", "\u65b9\u6848\u662f\u5fc5\u9700\u7684!" }, { "ER_FACTORY_PROPERTY_MISSING", "\u4f20\u9012\u5230 SerializerFactory \u7684 Properties \u5bf9\u8c61\u6ca1\u6709 ''{0}'' \u5c5e\u6027\u3002" }, { "ER_ENCODING_NOT_SUPPORTED", "\u8b66\u544a: Java \u8fd0\u884c\u65f6\u4e0d\u652f\u6301\u7f16\u7801 ''{0}''\u3002" } };
        return contents;
    }
}
