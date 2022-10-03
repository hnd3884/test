package org.owasp.esapi;

import java.io.IOException;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.codecs.Codec;

public interface Encoder
{
    @Deprecated
    public static final char[] CHAR_LOWERS = EncoderConstants.CHAR_LOWERS;
    @Deprecated
    public static final char[] CHAR_UPPERS = EncoderConstants.CHAR_UPPERS;
    @Deprecated
    public static final char[] CHAR_DIGITS = EncoderConstants.CHAR_DIGITS;
    @Deprecated
    public static final char[] CHAR_SPECIALS = EncoderConstants.CHAR_SPECIALS;
    @Deprecated
    public static final char[] CHAR_LETTERS = EncoderConstants.CHAR_LETTERS;
    @Deprecated
    public static final char[] CHAR_ALPHANUMERICS = EncoderConstants.CHAR_ALPHANUMERICS;
    @Deprecated
    public static final char[] CHAR_PASSWORD_LOWERS = EncoderConstants.CHAR_PASSWORD_LOWERS;
    @Deprecated
    public static final char[] CHAR_PASSWORD_UPPERS = EncoderConstants.CHAR_PASSWORD_UPPERS;
    @Deprecated
    public static final char[] CHAR_PASSWORD_DIGITS = EncoderConstants.CHAR_PASSWORD_DIGITS;
    @Deprecated
    public static final char[] CHAR_PASSWORD_SPECIALS = EncoderConstants.CHAR_PASSWORD_SPECIALS;
    @Deprecated
    public static final char[] CHAR_PASSWORD_LETTERS = EncoderConstants.CHAR_PASSWORD_LETTERS;
    
    String canonicalize(final String p0);
    
    String canonicalize(final String p0, final boolean p1);
    
    String canonicalize(final String p0, final boolean p1, final boolean p2);
    
    String encodeForCSS(final String p0);
    
    String encodeForHTML(final String p0);
    
    String decodeForHTML(final String p0);
    
    String encodeForHTMLAttribute(final String p0);
    
    String encodeForJavaScript(final String p0);
    
    String encodeForVBScript(final String p0);
    
    String encodeForSQL(final Codec p0, final String p1);
    
    String encodeForOS(final Codec p0, final String p1);
    
    String encodeForLDAP(final String p0);
    
    String encodeForDN(final String p0);
    
    String encodeForXPath(final String p0);
    
    String encodeForXML(final String p0);
    
    String encodeForXMLAttribute(final String p0);
    
    String encodeForURL(final String p0) throws EncodingException;
    
    String decodeFromURL(final String p0) throws EncodingException;
    
    String encodeForBase64(final byte[] p0, final boolean p1);
    
    byte[] decodeFromBase64(final String p0) throws IOException;
}
