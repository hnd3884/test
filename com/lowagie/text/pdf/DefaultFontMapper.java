package com.lowagie.text.pdf;

import java.io.File;
import com.lowagie.text.ExceptionConverter;
import java.awt.Font;
import java.util.HashMap;

public class DefaultFontMapper implements FontMapper
{
    private HashMap aliases;
    private HashMap mapper;
    
    public DefaultFontMapper() {
        this.aliases = new HashMap();
        this.mapper = new HashMap();
    }
    
    @Override
    public BaseFont awtToPdf(final Font font) {
        try {
            final BaseFontParameters p = this.getBaseFontParameters(font.getFontName());
            if (p != null) {
                return BaseFont.createFont(p.fontName, p.encoding, p.embedded, p.cached, p.ttfAfm, p.pfb);
            }
            String fontKey = null;
            final String logicalName = font.getName();
            if (logicalName.equalsIgnoreCase("DialogInput") || logicalName.equalsIgnoreCase("Monospaced") || logicalName.equalsIgnoreCase("Courier")) {
                if (font.isItalic()) {
                    if (font.isBold()) {
                        fontKey = "Courier-BoldOblique";
                    }
                    else {
                        fontKey = "Courier-Oblique";
                    }
                }
                else if (font.isBold()) {
                    fontKey = "Courier-Bold";
                }
                else {
                    fontKey = "Courier";
                }
            }
            else if (logicalName.equalsIgnoreCase("Serif") || logicalName.equalsIgnoreCase("TimesRoman")) {
                if (font.isItalic()) {
                    if (font.isBold()) {
                        fontKey = "Times-BoldItalic";
                    }
                    else {
                        fontKey = "Times-Italic";
                    }
                }
                else if (font.isBold()) {
                    fontKey = "Times-Bold";
                }
                else {
                    fontKey = "Times-Roman";
                }
            }
            else if (font.isItalic()) {
                if (font.isBold()) {
                    fontKey = "Helvetica-BoldOblique";
                }
                else {
                    fontKey = "Helvetica-Oblique";
                }
            }
            else if (font.isBold()) {
                fontKey = "Helvetica-Bold";
            }
            else {
                fontKey = "Helvetica";
            }
            return BaseFont.createFont(fontKey, "Cp1252", false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    @Override
    public Font pdfToAwt(final BaseFont font, final int size) {
        final String[][] names = font.getFullFontName();
        if (names.length == 1) {
            return new Font(names[0][3], 0, size);
        }
        String name10 = null;
        String name3x = null;
        for (int k = 0; k < names.length; ++k) {
            final String[] name11 = names[k];
            if (name11[0].equals("1") && name11[1].equals("0")) {
                name10 = name11[3];
            }
            else if (name11[2].equals("1033")) {
                name3x = name11[3];
                break;
            }
        }
        String finalName = name3x;
        if (finalName == null) {
            finalName = name10;
        }
        if (finalName == null) {
            finalName = names[0][3];
        }
        return new Font(finalName, 0, size);
    }
    
    public void putName(final String awtName, final BaseFontParameters parameters) {
        this.mapper.put(awtName, parameters);
    }
    
    public void putAlias(final String alias, final String awtName) {
        this.aliases.put(alias, awtName);
    }
    
    public BaseFontParameters getBaseFontParameters(final String name) {
        final String alias = this.aliases.get(name);
        if (alias == null) {
            return this.mapper.get(name);
        }
        final BaseFontParameters p = this.mapper.get(alias);
        if (p == null) {
            return this.mapper.get(name);
        }
        return p;
    }
    
    public void insertNames(final Object[] allNames, final String path) {
        final String[][] names = (String[][])allNames[2];
        String main = null;
        for (int k = 0; k < names.length; ++k) {
            final String[] name = names[k];
            if (name[2].equals("1033")) {
                main = name[3];
                break;
            }
        }
        if (main == null) {
            main = names[0][3];
        }
        final BaseFontParameters p = new BaseFontParameters(path);
        this.mapper.put(main, p);
        for (int i = 0; i < names.length; ++i) {
            this.aliases.put(names[i][3], main);
        }
        this.aliases.put(allNames[0], main);
    }
    
    public int insertDirectory(final String dir) {
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) {
            return 0;
        }
        final File[] files = file.listFiles();
        if (files == null) {
            return 0;
        }
        int count = 0;
        for (int k = 0; k < files.length; ++k) {
            file = files[k];
            final String name = file.getPath().toLowerCase();
            try {
                if (name.endsWith(".ttf") || name.endsWith(".otf") || name.endsWith(".afm")) {
                    final Object[] allNames = BaseFont.getAllFontNames(file.getPath(), "Cp1252", null);
                    this.insertNames(allNames, file.getPath());
                    ++count;
                }
                else if (name.endsWith(".ttc")) {
                    final String[] ttcs = BaseFont.enumerateTTCNames(file.getPath());
                    for (int j = 0; j < ttcs.length; ++j) {
                        final String nt = file.getPath() + "," + j;
                        final Object[] allNames2 = BaseFont.getAllFontNames(nt, "Cp1252", null);
                        this.insertNames(allNames2, nt);
                    }
                    ++count;
                }
            }
            catch (final Exception ex) {}
        }
        return count;
    }
    
    public HashMap getMapper() {
        return this.mapper;
    }
    
    public HashMap getAliases() {
        return this.aliases;
    }
    
    public static class BaseFontParameters
    {
        public String fontName;
        public String encoding;
        public boolean embedded;
        public boolean cached;
        public byte[] ttfAfm;
        public byte[] pfb;
        
        public BaseFontParameters(final String fontName) {
            this.fontName = fontName;
            this.encoding = "Cp1252";
            this.embedded = true;
            this.cached = true;
        }
    }
}
