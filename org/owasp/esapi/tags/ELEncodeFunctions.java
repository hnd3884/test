package org.owasp.esapi.tags;

import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.ESAPI;
import java.io.UnsupportedEncodingException;

public class ELEncodeFunctions
{
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    private ELEncodeFunctions() {
    }
    
    public static String encodeForBase64(final String str) throws UnsupportedEncodingException {
        return encodeForBase64Charset("UTF-8", str);
    }
    
    public static String encodeForBase64Wrap(final String str) throws UnsupportedEncodingException {
        return encodeForBase64CharsetWrap("UTF-8", str);
    }
    
    public static String encodeForBase64Charset(final String charset, final String str) throws UnsupportedEncodingException {
        return ESAPI.encoder().encodeForBase64(str.getBytes(charset), false);
    }
    
    public static String encodeForBase64CharsetWrap(final String charset, final String str) throws UnsupportedEncodingException {
        return ESAPI.encoder().encodeForBase64(str.getBytes(charset), true);
    }
    
    public static String encodeForCSS(final String str) {
        return ESAPI.encoder().encodeForCSS(str);
    }
    
    public static String encodeForHTML(final String str) {
        return ESAPI.encoder().encodeForHTML(str);
    }
    
    public static String encodeForHTMLAttribute(final String str) {
        return ESAPI.encoder().encodeForHTMLAttribute(str);
    }
    
    public static String encodeForJavaScript(final String str) {
        return ESAPI.encoder().encodeForJavaScript(str);
    }
    
    public static String encodeForURL(final String str) throws EncodingException {
        return ESAPI.encoder().encodeForURL(str);
    }
    
    public static String encodeForVBScript(final String str) {
        return ESAPI.encoder().encodeForVBScript(str);
    }
    
    public static String encodeForXML(final String str) {
        return ESAPI.encoder().encodeForXML(str);
    }
    
    public static String encodeForXMLAttribute(final String str) {
        return ESAPI.encoder().encodeForXMLAttribute(str);
    }
    
    public static String encodeForXPath(final String str) {
        return ESAPI.encoder().encodeForXPath(str);
    }
}
