package com.lowagie.text.error_messages;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import com.lowagie.text.pdf.BaseFont;
import java.io.Reader;
import java.io.IOException;
import java.util.HashMap;

public final class MessageLocalization
{
    private static HashMap defaultLanguage;
    private static HashMap currentLanguage;
    private static final String BASE_PATH = "com/lowagie/text/error_messages/";
    
    private MessageLocalization() {
    }
    
    public static String getMessage(final String key) {
        HashMap cl = MessageLocalization.currentLanguage;
        if (cl != null) {
            final String val = cl.get(key);
            if (val != null) {
                return val;
            }
        }
        cl = MessageLocalization.defaultLanguage;
        final String val = cl.get(key);
        if (val != null) {
            return val;
        }
        return "No message found for " + key;
    }
    
    public static String getComposedMessage(final String key) {
        return getComposedMessage(key, null, null, null, null);
    }
    
    public static String getComposedMessage(final String key, final Object p1) {
        return getComposedMessage(key, p1, null, null, null);
    }
    
    public static String getComposedMessage(final String key, final int p1) {
        return getComposedMessage(key, String.valueOf(p1), null, null, null);
    }
    
    public static String getComposedMessage(final String key, final Object p1, final Object p2) {
        return getComposedMessage(key, p1, p2, null, null);
    }
    
    public static String getComposedMessage(final String key, final Object p1, final Object p2, final Object p3) {
        return getComposedMessage(key, p1, p2, p3, null);
    }
    
    public static String getComposedMessage(final String key, final Object p1, final Object p2, final Object p3, final Object p4) {
        String msg = getMessage(key);
        if (p1 != null) {
            msg = msg.replaceAll("\\{1}", p1.toString());
        }
        if (p2 != null) {
            msg = msg.replaceAll("\\{2}", p2.toString());
        }
        if (p3 != null) {
            msg = msg.replaceAll("\\{3}", p3.toString());
        }
        if (p4 != null) {
            msg = msg.replaceAll("\\{4}", p4.toString());
        }
        return msg;
    }
    
    public static boolean setLanguage(final String language, final String country) throws IOException {
        final HashMap lang = getLanguageMessages(language, country);
        if (lang == null) {
            return false;
        }
        MessageLocalization.currentLanguage = lang;
        return true;
    }
    
    public static void setMessages(final Reader r) throws IOException {
        MessageLocalization.currentLanguage = readLanguageStream(r);
    }
    
    private static HashMap getLanguageMessages(final String language, final String country) throws IOException {
        if (language == null) {
            throw new IllegalArgumentException("The language cannot be null.");
        }
        InputStream is = null;
        try {
            String file;
            if (country != null) {
                file = language + "_" + country + ".lng";
            }
            else {
                file = language + ".lng";
            }
            is = BaseFont.getResourceStream("com/lowagie/text/error_messages/" + file, new MessageLocalization().getClass().getClassLoader());
            if (is != null) {
                return readLanguageStream(is);
            }
            if (country == null) {
                return null;
            }
            file = language + ".lng";
            is = BaseFont.getResourceStream("com/lowagie/text/error_messages/" + file, new MessageLocalization().getClass().getClassLoader());
            if (is != null) {
                return readLanguageStream(is);
            }
            return null;
        }
        finally {
            try {
                is.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    private static HashMap readLanguageStream(final InputStream is) throws IOException {
        return readLanguageStream(new InputStreamReader(is, StandardCharsets.UTF_8));
    }
    
    private static HashMap readLanguageStream(final Reader r) throws IOException {
        final HashMap lang = new HashMap();
        final BufferedReader br = new BufferedReader(r);
        String line;
        while ((line = br.readLine()) != null) {
            final int idxeq = line.indexOf(61);
            if (idxeq < 0) {
                continue;
            }
            final String key = line.substring(0, idxeq).trim();
            if (key.startsWith("#")) {
                continue;
            }
            lang.put(key, line.substring(idxeq + 1));
        }
        return lang;
    }
    
    static {
        MessageLocalization.defaultLanguage = new HashMap();
        try {
            MessageLocalization.defaultLanguage = getLanguageMessages("en", null);
        }
        catch (final Exception ex) {}
        if (MessageLocalization.defaultLanguage == null) {
            MessageLocalization.defaultLanguage = new HashMap();
        }
    }
}
