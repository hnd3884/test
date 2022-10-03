package com.lowagie.text;

import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Set;
import java.util.Properties;
import java.awt.Color;

public final class FontFactory
{
    public static final String COURIER = "Courier";
    public static final String COURIER_BOLD = "Courier-Bold";
    public static final String COURIER_OBLIQUE = "Courier-Oblique";
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";
    public static final String HELVETICA = "Helvetica";
    public static final String HELVETICA_BOLD = "Helvetica-Bold";
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
    public static final String SYMBOL = "Symbol";
    public static final String TIMES = "Times";
    public static final String TIMES_ROMAN = "Times-Roman";
    public static final String TIMES_BOLD = "Times-Bold";
    public static final String TIMES_ITALIC = "Times-Italic";
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
    public static final String ZAPFDINGBATS = "ZapfDingbats";
    private static FontFactoryImp fontImp;
    public static String defaultEncoding;
    public static boolean defaultEmbedding;
    
    private FontFactory() {
    }
    
    public static Font getFont(final String fontname, final String encoding, final boolean embedded, final float size, final int style, final Color color) {
        return FontFactory.fontImp.getFont(fontname, encoding, embedded, size, style, color);
    }
    
    public static Font getFont(final String fontname, final String encoding, final boolean embedded, final float size, final int style, final Color color, final boolean cached) {
        return FontFactory.fontImp.getFont(fontname, encoding, embedded, size, style, color, cached);
    }
    
    public static Font getFont(final Properties attributes) {
        FontFactory.fontImp.defaultEmbedding = FontFactory.defaultEmbedding;
        FontFactory.fontImp.defaultEncoding = FontFactory.defaultEncoding;
        return FontFactory.fontImp.getFont(attributes);
    }
    
    public static Font getFont(final String fontname, final String encoding, final boolean embedded, final float size, final int style) {
        return getFont(fontname, encoding, embedded, size, style, null);
    }
    
    public static Font getFont(final String fontname, final String encoding, final boolean embedded, final float size) {
        return getFont(fontname, encoding, embedded, size, -1, null);
    }
    
    public static Font getFont(final String fontname, final String encoding, final boolean embedded) {
        return getFont(fontname, encoding, embedded, -1.0f, -1, null);
    }
    
    public static Font getFont(final String fontname, final String encoding, final float size, final int style, final Color color) {
        return getFont(fontname, encoding, FontFactory.defaultEmbedding, size, style, color);
    }
    
    public static Font getFont(final String fontname, final String encoding, final float size, final int style) {
        return getFont(fontname, encoding, FontFactory.defaultEmbedding, size, style, null);
    }
    
    public static Font getFont(final String fontname, final String encoding, final float size) {
        return getFont(fontname, encoding, FontFactory.defaultEmbedding, size, -1, null);
    }
    
    public static Font getFont(final String fontname, final String encoding) {
        return getFont(fontname, encoding, FontFactory.defaultEmbedding, -1.0f, -1, null);
    }
    
    public static Font getFont(final String fontname, final float size, final int style, final Color color) {
        return getFont(fontname, FontFactory.defaultEncoding, FontFactory.defaultEmbedding, size, style, color);
    }
    
    public static Font getFont(final String fontname, final float size, final Color color) {
        return getFont(fontname, FontFactory.defaultEncoding, FontFactory.defaultEmbedding, size, -1, color);
    }
    
    public static Font getFont(final String fontname, final float size, final int style) {
        return getFont(fontname, FontFactory.defaultEncoding, FontFactory.defaultEmbedding, size, style, null);
    }
    
    public static Font getFont(final String fontname, final float size) {
        return getFont(fontname, FontFactory.defaultEncoding, FontFactory.defaultEmbedding, size, -1, null);
    }
    
    public static Font getFont(final String fontname) {
        return getFont(fontname, FontFactory.defaultEncoding, FontFactory.defaultEmbedding, -1.0f, -1, null);
    }
    
    public void registerFamily(final String familyName, final String fullName, final String path) {
        FontFactory.fontImp.registerFamily(familyName, fullName, path);
    }
    
    public static void register(final String path) {
        register(path, null);
    }
    
    public static void register(final String path, final String alias) {
        FontFactory.fontImp.register(path, alias);
    }
    
    public static int registerDirectory(final String dir) {
        return FontFactory.fontImp.registerDirectory(dir);
    }
    
    public static int registerDirectory(final String dir, final boolean scanSubdirectories) {
        return FontFactory.fontImp.registerDirectory(dir, scanSubdirectories);
    }
    
    public static int registerDirectories() {
        return FontFactory.fontImp.registerDirectories();
    }
    
    public static Set getRegisteredFonts() {
        return FontFactory.fontImp.getRegisteredFonts();
    }
    
    public static Set getRegisteredFamilies() {
        return FontFactory.fontImp.getRegisteredFamilies();
    }
    
    public static boolean contains(final String fontname) {
        return FontFactory.fontImp.isRegistered(fontname);
    }
    
    public static boolean isRegistered(final String fontname) {
        return FontFactory.fontImp.isRegistered(fontname);
    }
    
    public static FontFactoryImp getFontImp() {
        return FontFactory.fontImp;
    }
    
    public static void setFontImp(final FontFactoryImp fontImp) {
        if (fontImp == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("fontfactoryimp.cannot.be.null"));
        }
        FontFactory.fontImp = fontImp;
    }
    
    static {
        FontFactory.fontImp = new FontFactoryImp();
        FontFactory.defaultEncoding = "Cp1252";
        FontFactory.defaultEmbedding = false;
    }
}
