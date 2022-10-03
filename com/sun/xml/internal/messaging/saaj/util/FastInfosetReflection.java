package com.sun.xml.internal.messaging.saaj.util;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.OutputStream;
import org.w3c.dom.Node;
import java.io.InputStream;
import org.w3c.dom.Document;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class FastInfosetReflection
{
    static Constructor fiDOMDocumentParser_new;
    static Method fiDOMDocumentParser_parse;
    static Constructor fiDOMDocumentSerializer_new;
    static Method fiDOMDocumentSerializer_serialize;
    static Method fiDOMDocumentSerializer_setOutputStream;
    static Class fiFastInfosetSource_class;
    static Constructor fiFastInfosetSource_new;
    static Method fiFastInfosetSource_getInputStream;
    static Method fiFastInfosetSource_setInputStream;
    static Constructor fiFastInfosetResult_new;
    static Method fiFastInfosetResult_getOutputStream;
    
    public static Object DOMDocumentParser_new() throws Exception {
        if (FastInfosetReflection.fiDOMDocumentParser_new == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        return FastInfosetReflection.fiDOMDocumentParser_new.newInstance((Object[])null);
    }
    
    public static void DOMDocumentParser_parse(final Object parser, final Document d, final InputStream s) throws Exception {
        if (FastInfosetReflection.fiDOMDocumentParser_parse == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        FastInfosetReflection.fiDOMDocumentParser_parse.invoke(parser, d, s);
    }
    
    public static Object DOMDocumentSerializer_new() throws Exception {
        if (FastInfosetReflection.fiDOMDocumentSerializer_new == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        return FastInfosetReflection.fiDOMDocumentSerializer_new.newInstance((Object[])null);
    }
    
    public static void DOMDocumentSerializer_serialize(final Object serializer, final Node node) throws Exception {
        if (FastInfosetReflection.fiDOMDocumentSerializer_serialize == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        FastInfosetReflection.fiDOMDocumentSerializer_serialize.invoke(serializer, node);
    }
    
    public static void DOMDocumentSerializer_setOutputStream(final Object serializer, final OutputStream os) throws Exception {
        if (FastInfosetReflection.fiDOMDocumentSerializer_setOutputStream == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        FastInfosetReflection.fiDOMDocumentSerializer_setOutputStream.invoke(serializer, os);
    }
    
    public static boolean isFastInfosetSource(final Source source) {
        return source.getClass().getName().equals("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource");
    }
    
    public static Class getFastInfosetSource_class() {
        if (FastInfosetReflection.fiFastInfosetSource_class == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        return FastInfosetReflection.fiFastInfosetSource_class;
    }
    
    public static Source FastInfosetSource_new(final InputStream is) throws Exception {
        if (FastInfosetReflection.fiFastInfosetSource_new == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        return FastInfosetReflection.fiFastInfosetSource_new.newInstance(is);
    }
    
    public static InputStream FastInfosetSource_getInputStream(final Source source) throws Exception {
        if (FastInfosetReflection.fiFastInfosetSource_getInputStream == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        return (InputStream)FastInfosetReflection.fiFastInfosetSource_getInputStream.invoke(source, (Object[])null);
    }
    
    public static void FastInfosetSource_setInputStream(final Source source, final InputStream is) throws Exception {
        if (FastInfosetReflection.fiFastInfosetSource_setInputStream == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        FastInfosetReflection.fiFastInfosetSource_setInputStream.invoke(source, is);
    }
    
    public static boolean isFastInfosetResult(final Result result) {
        return result.getClass().getName().equals("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult");
    }
    
    public static Result FastInfosetResult_new(final OutputStream os) throws Exception {
        if (FastInfosetReflection.fiFastInfosetResult_new == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        return FastInfosetReflection.fiFastInfosetResult_new.newInstance(os);
    }
    
    public static OutputStream FastInfosetResult_getOutputStream(final Result result) throws Exception {
        if (FastInfosetReflection.fiFastInfosetResult_getOutputStream == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        return (OutputStream)FastInfosetReflection.fiFastInfosetResult_getOutputStream.invoke(result, (Object[])null);
    }
    
    static {
        try {
            Class clazz = Class.forName("com.sun.xml.internal.fastinfoset.dom.DOMDocumentParser");
            FastInfosetReflection.fiDOMDocumentParser_new = clazz.getConstructor((Class[])null);
            FastInfosetReflection.fiDOMDocumentParser_parse = clazz.getMethod("parse", Document.class, InputStream.class);
            clazz = Class.forName("com.sun.xml.internal.fastinfoset.dom.DOMDocumentSerializer");
            FastInfosetReflection.fiDOMDocumentSerializer_new = clazz.getConstructor((Class[])null);
            FastInfosetReflection.fiDOMDocumentSerializer_serialize = clazz.getMethod("serialize", Node.class);
            FastInfosetReflection.fiDOMDocumentSerializer_setOutputStream = clazz.getMethod("setOutputStream", OutputStream.class);
            clazz = (FastInfosetReflection.fiFastInfosetSource_class = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource"));
            FastInfosetReflection.fiFastInfosetSource_new = clazz.getConstructor(InputStream.class);
            FastInfosetReflection.fiFastInfosetSource_getInputStream = clazz.getMethod("getInputStream", (Class[])null);
            FastInfosetReflection.fiFastInfosetSource_setInputStream = clazz.getMethod("setInputStream", InputStream.class);
            clazz = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult");
            FastInfosetReflection.fiFastInfosetResult_new = clazz.getConstructor(OutputStream.class);
            FastInfosetReflection.fiFastInfosetResult_getOutputStream = clazz.getMethod("getOutputStream", (Class[])null);
        }
        catch (final Exception ex) {}
    }
}
