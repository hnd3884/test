package com.sun.xml.internal.bind.marshaller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages
{
    public static final String NOT_MARSHALLABLE = "MarshallerImpl.NotMarshallable";
    public static final String UNSUPPORTED_RESULT = "MarshallerImpl.UnsupportedResult";
    public static final String UNSUPPORTED_ENCODING = "MarshallerImpl.UnsupportedEncoding";
    public static final String NULL_WRITER = "MarshallerImpl.NullWriterParam";
    public static final String ASSERT_FAILED = "SAXMarshaller.AssertFailed";
    @Deprecated
    public static final String ERR_MISSING_OBJECT = "SAXMarshaller.MissingObject";
    @Deprecated
    public static final String ERR_DANGLING_IDREF = "SAXMarshaller.DanglingIDREF";
    @Deprecated
    public static final String ERR_NOT_IDENTIFIABLE = "SAXMarshaller.NotIdentifiable";
    public static final String DOM_IMPL_DOESNT_SUPPORT_CREATELEMENTNS = "SAX2DOMEx.DomImplDoesntSupportCreateElementNs";
    
    public static String format(final String property) {
        return format(property, null);
    }
    
    public static String format(final String property, final Object arg1) {
        return format(property, new Object[] { arg1 });
    }
    
    public static String format(final String property, final Object arg1, final Object arg2) {
        return format(property, new Object[] { arg1, arg2 });
    }
    
    public static String format(final String property, final Object arg1, final Object arg2, final Object arg3) {
        return format(property, new Object[] { arg1, arg2, arg3 });
    }
    
    static String format(final String property, final Object[] args) {
        final String text = ResourceBundle.getBundle(Messages.class.getName()).getString(property);
        return MessageFormat.format(text, args);
    }
}
