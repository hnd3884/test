package com.lowagie.text;

import java.util.Set;
import java.io.File;
import java.util.Enumeration;
import java.util.Map;
import com.lowagie.text.html.Markup;
import java.util.Iterator;
import java.io.IOException;
import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Hashtable;
import java.util.Properties;

public class FontFactoryImp implements FontProvider
{
    private Properties trueTypeFonts;
    private static String[] TTFamilyOrder;
    private Hashtable fontFamilies;
    public String defaultEncoding;
    public boolean defaultEmbedding;
    
    public FontFactoryImp() {
        this.trueTypeFonts = new Properties();
        this.fontFamilies = new Hashtable();
        this.defaultEncoding = "Cp1252";
        this.defaultEmbedding = false;
        this.trueTypeFonts.setProperty("Courier".toLowerCase(Locale.ROOT), "Courier");
        this.trueTypeFonts.setProperty("Courier-Bold".toLowerCase(Locale.ROOT), "Courier-Bold");
        this.trueTypeFonts.setProperty("Courier-Oblique".toLowerCase(Locale.ROOT), "Courier-Oblique");
        this.trueTypeFonts.setProperty("Courier-BoldOblique".toLowerCase(Locale.ROOT), "Courier-BoldOblique");
        this.trueTypeFonts.setProperty("Helvetica".toLowerCase(Locale.ROOT), "Helvetica");
        this.trueTypeFonts.setProperty("Helvetica-Bold".toLowerCase(Locale.ROOT), "Helvetica-Bold");
        this.trueTypeFonts.setProperty("Helvetica-Oblique".toLowerCase(Locale.ROOT), "Helvetica-Oblique");
        this.trueTypeFonts.setProperty("Helvetica-BoldOblique".toLowerCase(Locale.ROOT), "Helvetica-BoldOblique");
        this.trueTypeFonts.setProperty("Symbol".toLowerCase(Locale.ROOT), "Symbol");
        this.trueTypeFonts.setProperty("Times-Roman".toLowerCase(Locale.ROOT), "Times-Roman");
        this.trueTypeFonts.setProperty("Times-Bold".toLowerCase(Locale.ROOT), "Times-Bold");
        this.trueTypeFonts.setProperty("Times-Italic".toLowerCase(Locale.ROOT), "Times-Italic");
        this.trueTypeFonts.setProperty("Times-BoldItalic".toLowerCase(Locale.ROOT), "Times-BoldItalic");
        this.trueTypeFonts.setProperty("ZapfDingbats".toLowerCase(Locale.ROOT), "ZapfDingbats");
        ArrayList tmp = new ArrayList();
        tmp.add("Courier");
        tmp.add("Courier-Bold");
        tmp.add("Courier-Oblique");
        tmp.add("Courier-BoldOblique");
        this.fontFamilies.put("Courier".toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList();
        tmp.add("Helvetica");
        tmp.add("Helvetica-Bold");
        tmp.add("Helvetica-Oblique");
        tmp.add("Helvetica-BoldOblique");
        this.fontFamilies.put("Helvetica".toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList();
        tmp.add("Symbol");
        this.fontFamilies.put("Symbol".toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList();
        tmp.add("Times-Roman");
        tmp.add("Times-Bold");
        tmp.add("Times-Italic");
        tmp.add("Times-BoldItalic");
        this.fontFamilies.put("Times".toLowerCase(Locale.ROOT), tmp);
        this.fontFamilies.put("Times-Roman".toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList();
        tmp.add("ZapfDingbats");
        this.fontFamilies.put("ZapfDingbats".toLowerCase(Locale.ROOT), tmp);
    }
    
    @Override
    public Font getFont(final String fontname, final String encoding, final boolean embedded, final float size, final int style, final Color color) {
        return this.getFont(fontname, encoding, embedded, size, style, color, true);
    }
    
    public Font getFont(String fontname, final String encoding, final boolean embedded, final float size, int style, final Color color, final boolean cached) {
        if (fontname == null) {
            return new Font(-1, size, style, color);
        }
        final String lowercasefontname = fontname.toLowerCase(Locale.ROOT);
        final ArrayList tmp = this.fontFamilies.get(lowercasefontname);
        if (tmp != null) {
            final int s = (style == -1) ? 0 : style;
            int fs = 0;
            boolean found = false;
            for (final String f : tmp) {
                final String lcf = f.toLowerCase(Locale.ROOT);
                fs = 0;
                if (lcf.indexOf("bold") != -1) {
                    fs |= 0x1;
                }
                if (lcf.indexOf("italic") != -1 || lcf.indexOf("oblique") != -1) {
                    fs |= 0x2;
                }
                if ((s & 0x3) == fs) {
                    fontname = f;
                    found = true;
                    break;
                }
            }
            if (style != -1 && found) {
                style &= ~fs;
            }
        }
        BaseFont basefont = null;
        try {
            try {
                basefont = BaseFont.createFont(fontname, encoding, embedded, cached, null, null, true);
            }
            catch (final DocumentException ex) {}
            if (basefont == null) {
                fontname = this.trueTypeFonts.getProperty(lowercasefontname);
                if (fontname == null) {
                    return new Font(-1, size, style, color);
                }
                basefont = BaseFont.createFont(fontname, encoding, embedded, cached, null, null);
            }
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
        catch (final IOException ioe) {
            return new Font(-1, size, style, color);
        }
        catch (final NullPointerException npe) {
            return new Font(-1, size, style, color);
        }
        return new Font(basefont, size, style, color);
    }
    
    public Font getFont(final Properties attributes) {
        String fontname = null;
        String encoding = this.defaultEncoding;
        boolean embedded = this.defaultEmbedding;
        float size = -1.0f;
        int style = 0;
        Color color = null;
        String value = attributes.getProperty("style");
        if (value != null && value.length() > 0) {
            final Properties styleAttributes = Markup.parseAttributes(value);
            if (styleAttributes.isEmpty()) {
                ((Hashtable<String, String>)attributes).put("style", value);
            }
            else {
                fontname = styleAttributes.getProperty("font-family");
                if (fontname != null) {
                    while (fontname.indexOf(44) != -1) {
                        final String tmp = fontname.substring(0, fontname.indexOf(44));
                        if (this.isRegistered(tmp)) {
                            fontname = tmp;
                        }
                        else {
                            fontname = fontname.substring(fontname.indexOf(44) + 1);
                        }
                    }
                }
                if ((value = styleAttributes.getProperty("font-size")) != null) {
                    size = Markup.parseLength(value);
                }
                if ((value = styleAttributes.getProperty("font-weight")) != null) {
                    style |= Font.getStyleValue(value);
                }
                if ((value = styleAttributes.getProperty("font-style")) != null) {
                    style |= Font.getStyleValue(value);
                }
                if ((value = styleAttributes.getProperty("color")) != null) {
                    color = Markup.decodeColor(value);
                }
                attributes.putAll(styleAttributes);
                final Enumeration e = styleAttributes.keys();
                while (e.hasMoreElements()) {
                    final Object o = e.nextElement();
                    attributes.put(o, ((Hashtable<K, Object>)styleAttributes).get(o));
                }
            }
        }
        if ((value = attributes.getProperty("encoding")) != null) {
            encoding = value;
        }
        if ("true".equals(attributes.getProperty("embedded"))) {
            embedded = true;
        }
        if ((value = attributes.getProperty("font")) != null) {
            fontname = value;
        }
        if ((value = attributes.getProperty("size")) != null) {
            size = Markup.parseLength(value);
        }
        if ((value = attributes.getProperty("style")) != null) {
            style |= Font.getStyleValue(value);
        }
        if ((value = attributes.getProperty("fontstyle")) != null) {
            style |= Font.getStyleValue(value);
        }
        final String r = attributes.getProperty("red");
        final String g = attributes.getProperty("green");
        final String b = attributes.getProperty("blue");
        if (r != null || g != null || b != null) {
            int red = 0;
            int green = 0;
            int blue = 0;
            if (r != null) {
                red = Integer.parseInt(r);
            }
            if (g != null) {
                green = Integer.parseInt(g);
            }
            if (b != null) {
                blue = Integer.parseInt(b);
            }
            color = new Color(red, green, blue);
        }
        else if ((value = attributes.getProperty("color")) != null) {
            color = Markup.decodeColor(value);
        }
        if (fontname == null) {
            return this.getFont(null, encoding, embedded, size, style, color);
        }
        return this.getFont(fontname, encoding, embedded, size, style, color);
    }
    
    public Font getFont(final String fontname, final String encoding, final boolean embedded, final float size, final int style) {
        return this.getFont(fontname, encoding, embedded, size, style, null);
    }
    
    public Font getFont(final String fontname, final String encoding, final boolean embedded, final float size) {
        return this.getFont(fontname, encoding, embedded, size, -1, null);
    }
    
    public Font getFont(final String fontname, final String encoding, final boolean embedded) {
        return this.getFont(fontname, encoding, embedded, -1.0f, -1, null);
    }
    
    public Font getFont(final String fontname, final String encoding, final float size, final int style, final Color color) {
        return this.getFont(fontname, encoding, this.defaultEmbedding, size, style, color);
    }
    
    public Font getFont(final String fontname, final String encoding, final float size, final int style) {
        return this.getFont(fontname, encoding, this.defaultEmbedding, size, style, null);
    }
    
    public Font getFont(final String fontname, final String encoding, final float size) {
        return this.getFont(fontname, encoding, this.defaultEmbedding, size, -1, null);
    }
    
    public Font getFont(final String fontname, final float size, final Color color) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, -1, color);
    }
    
    public Font getFont(final String fontname, final String encoding) {
        return this.getFont(fontname, encoding, this.defaultEmbedding, -1.0f, -1, null);
    }
    
    public Font getFont(final String fontname, final float size, final int style, final Color color) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, style, color);
    }
    
    public Font getFont(final String fontname, final float size, final int style) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, style, null);
    }
    
    public Font getFont(final String fontname, final float size) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, -1, null);
    }
    
    public Font getFont(final String fontname) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, -1.0f, -1, null);
    }
    
    public void registerFamily(final String familyName, final String fullName, final String path) {
        if (path != null) {
            this.trueTypeFonts.setProperty(fullName, path);
        }
        ArrayList tmp = this.fontFamilies.get(familyName);
        if (tmp == null) {
            tmp = new ArrayList();
            tmp.add(fullName);
            this.fontFamilies.put(familyName, tmp);
        }
        else {
            final int fullNameLength = fullName.length();
            boolean inserted = false;
            for (int j = 0; j < tmp.size(); ++j) {
                if (tmp.get(j).length() >= fullNameLength) {
                    tmp.add(j, fullName);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                tmp.add(fullName);
            }
        }
    }
    
    public void register(final String path) {
        this.register(path, null);
    }
    
    public void register(final String path, final String alias) {
        try {
            if (path.toLowerCase().endsWith(".ttf") || path.toLowerCase().endsWith(".otf") || path.toLowerCase().indexOf(".ttc,") > 0) {
                final Object[] allNames = BaseFont.getAllFontNames(path, "Cp1252", null);
                this.trueTypeFonts.setProperty(((String)allNames[0]).toLowerCase(), path);
                if (alias != null) {
                    this.trueTypeFonts.setProperty(alias.toLowerCase(), path);
                }
                String[][] names = (String[][])allNames[2];
                for (int i = 0; i < names.length; ++i) {
                    this.trueTypeFonts.setProperty(names[i][3].toLowerCase(), path);
                }
                String fullName = null;
                String familyName = null;
                names = (String[][])allNames[1];
                for (int k = 0; k < FontFactoryImp.TTFamilyOrder.length; k += 3) {
                    for (int j = 0; j < names.length; ++j) {
                        if (FontFactoryImp.TTFamilyOrder[k].equals(names[j][0]) && FontFactoryImp.TTFamilyOrder[k + 1].equals(names[j][1]) && FontFactoryImp.TTFamilyOrder[k + 2].equals(names[j][2])) {
                            familyName = names[j][3].toLowerCase();
                            k = FontFactoryImp.TTFamilyOrder.length;
                            break;
                        }
                    }
                }
                if (familyName != null) {
                    String lastName = "";
                    names = (String[][])allNames[2];
                    for (int j = 0; j < names.length; ++j) {
                        for (int l = 0; l < FontFactoryImp.TTFamilyOrder.length; l += 3) {
                            if (FontFactoryImp.TTFamilyOrder[l].equals(names[j][0]) && FontFactoryImp.TTFamilyOrder[l + 1].equals(names[j][1]) && FontFactoryImp.TTFamilyOrder[l + 2].equals(names[j][2])) {
                                fullName = names[j][3];
                                if (!fullName.equals(lastName)) {
                                    lastName = fullName;
                                    this.registerFamily(familyName, fullName, null);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else if (path.toLowerCase().endsWith(".ttc")) {
                if (alias != null) {
                    System.err.println("class FontFactory: You can't define an alias for a true type collection.");
                }
                final String[] names2 = BaseFont.enumerateTTCNames(path);
                for (int m = 0; m < names2.length; ++m) {
                    this.register(path + "," + m);
                }
            }
            else if (path.toLowerCase().endsWith(".afm") || path.toLowerCase().endsWith(".pfm")) {
                final BaseFont bf = BaseFont.createFont(path, "Cp1252", false);
                final String fullName2 = bf.getFullFontName()[0][3].toLowerCase();
                final String familyName2 = bf.getFamilyFontName()[0][3].toLowerCase();
                final String psName = bf.getPostscriptFontName().toLowerCase();
                this.registerFamily(familyName2, fullName2, null);
                this.trueTypeFonts.setProperty(psName, path);
                this.trueTypeFonts.setProperty(fullName2, path);
            }
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }
    
    public int registerDirectory(final String dir) {
        return this.registerDirectory(dir, false);
    }
    
    public int registerDirectory(final String dir, final boolean scanSubdirectories) {
        int count = 0;
        try {
            File file = new File(dir);
            if (!file.exists() || !file.isDirectory()) {
                return 0;
            }
            final String[] files = file.list();
            if (files == null) {
                return 0;
            }
            for (int k = 0; k < files.length; ++k) {
                try {
                    file = new File(dir, files[k]);
                    if (file.isDirectory()) {
                        if (scanSubdirectories) {
                            count += this.registerDirectory(file.getAbsolutePath(), true);
                        }
                    }
                    else {
                        final String name = file.getPath();
                        final String suffix = (name.length() < 4) ? null : name.substring(name.length() - 4).toLowerCase();
                        if (".afm".equals(suffix) || ".pfm".equals(suffix)) {
                            final File pfb = new File(name.substring(0, name.length() - 4) + ".pfb");
                            if (pfb.exists()) {
                                this.register(name, null);
                                ++count;
                            }
                        }
                        else if (".ttf".equals(suffix) || ".otf".equals(suffix) || ".ttc".equals(suffix)) {
                            this.register(name, null);
                            ++count;
                        }
                    }
                }
                catch (final Exception ex) {}
            }
        }
        catch (final Exception ex2) {}
        return count;
    }
    
    public int registerDirectories() {
        int count = 0;
        count += this.registerDirectory("c:/windows/fonts");
        count += this.registerDirectory("c:/winnt/fonts");
        count += this.registerDirectory("d:/windows/fonts");
        count += this.registerDirectory("d:/winnt/fonts");
        count += this.registerDirectory("/usr/share/X11/fonts", true);
        count += this.registerDirectory("/usr/X/lib/X11/fonts", true);
        count += this.registerDirectory("/usr/openwin/lib/X11/fonts", true);
        count += this.registerDirectory("/usr/share/fonts", true);
        count += this.registerDirectory("/usr/X11R6/lib/X11/fonts", true);
        count += this.registerDirectory("/Library/Fonts");
        count += this.registerDirectory("/System/Library/Fonts");
        return count;
    }
    
    public Set getRegisteredFonts() {
        return Utilities.getKeySet(this.trueTypeFonts);
    }
    
    public Set getRegisteredFamilies() {
        return Utilities.getKeySet(this.fontFamilies);
    }
    
    @Override
    public boolean isRegistered(final String fontname) {
        return this.trueTypeFonts.containsKey(fontname.toLowerCase());
    }
    
    static {
        FontFactoryImp.TTFamilyOrder = new String[] { "3", "1", "1033", "3", "0", "1033", "1", "0", "0", "0", "3", "0" };
    }
}
