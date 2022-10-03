package com.adventnet.client.view.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.adventnet.client.util.pdf.PDFUtil;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.Font;
import java.awt.Color;
import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.lowagie.text.FontFactory;
import java.io.File;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import javax.servlet.ServletContext;
import java.util.HashMap;
import com.lowagie.text.PageSize;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import java.util.Map;
import com.lowagie.text.pdf.BaseFont;
import java.util.Properties;
import com.lowagie.text.pdf.FontSelector;

public class DefaultPDFTheme implements PDFTheme
{
    private FontSelector selector;
    private Properties fontProps;
    private BaseFont font;
    private Map<String, Boolean> extFontMap;
    private static final Logger OUT;
    
    @Override
    public Document getDocument(final ViewContext vc) {
        Document doc = null;
        final String landscapemode = vc.getRequest().getParameter("landscape");
        if (landscapemode != null && landscapemode.equals("true")) {
            doc = new Document(PageSize.A4.rotate());
        }
        else {
            doc = new Document();
        }
        return doc;
    }
    
    public DefaultPDFTheme() {
        this.selector = null;
        this.fontProps = null;
        this.font = null;
        this.extFontMap = new HashMap<String, Boolean>();
        this.init();
    }
    
    public void init() {
        this.includeFonts();
        this.font = this.getBaseFont();
    }
    
    @Override
    public void startPDFDoc(final ServletContext sc, final ViewContext viewContext, final Document doc, final PdfWriter pdfWriter) {
    }
    
    @Override
    public void endPDFDoc(final ServletContext sc, final ViewContext vc, final Document doc, final PdfWriter pdfWriter) {
    }
    
    @Override
    public PdfPCell renderCellToAddInLayout(final ServletContext sc, final ViewContext vc, final Document doc, final PdfWriter pdfWriter, final PdfPCell cell, final String boxType) {
        cell.setBorder(0);
        cell.setBorderWidth(0.0f);
        cell.setPadding(5.0f);
        return cell;
    }
    
    private void addFont(final FontSelector selector, final Properties props, final String language) {
        final String isExternalFont = props.getProperty(language + ".isExternalFont");
        if (isExternalFont != null && "true".equals(isExternalFont)) {
            final String filepath = props.getProperty(language + ".url");
            final boolean isFileExists = new File(filepath).exists();
            if (isFileExists) {
                FontFactory.register(filepath, props.getProperty(language + ".fontname"));
                selector.addFont(FontFactory.getFont(props.getProperty(language + ".fontname")));
                return;
            }
            DefaultPDFTheme.OUT.log(Level.SEVERE, "Font file doesn't exists on the given path : {0}", filepath);
        }
        else {
            final String encoding = props.getProperty(language + ".encoding");
            selector.addFont(FontFactory.getFont(props.getProperty(language + ".fontname"), encoding, false));
        }
    }
    
    private void includeFonts() {
        if (this.selector == null) {
            this.selector = new FontSelector();
            this.fontProps = new Properties();
            try {
                final String userLanguage = I18N.getLocale().getLanguage();
                this.fontProps.load(new FileInputStream(System.getProperty("server.home") + File.separator + "conf" + File.separator + "fonts.properties"));
                final String[] languages = this.fontProps.getProperty("supported_languages").trim().split(",");
                this.selector.addFont(FontFactory.getFont("Helvetica", "Cp1252", false));
                this.addFont(this.selector, this.fontProps, userLanguage);
                for (final String language : languages) {
                    if (!userLanguage.equals(language)) {
                        this.addFont(this.selector, this.fontProps, language);
                    }
                }
            }
            catch (final FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (final IOException e2) {
                e2.printStackTrace();
            }
        }
    }
    
    @Override
    public Element updateThemeAttributes(final ServletContext sc, final ViewContext vc, final Document doc, final PdfWriter pdfWriter, final Element el, final String styleClass) {
        if (styleClass.equals("tableHeader")) {
            if (el instanceof Chunk) {
                final Chunk headerChunk = (Chunk)el;
                final Color col = new Color(Integer.parseInt("000099", 16));
                headerChunk.setFont(new Font(this.font, 11.0f, 1, col));
            }
            else if (el instanceof PdfPCell) {
                final PdfPCell headerCell = (PdfPCell)el;
                headerCell.setHorizontalAlignment(1);
                headerCell.setPadding(3.0f);
                headerCell.setBorder(0);
                final Color col = new Color(Integer.parseInt("E5E5E5", 16));
                headerCell.setBackgroundColor(col);
            }
        }
        else if (styleClass.equals("headerCell")) {
            if (el instanceof Chunk) {
                final Chunk headerChunk = (Chunk)el;
                headerChunk.setFont(new Font(this.font, 11.0f, 1));
            }
            else if (el instanceof PdfPCell) {
                final PdfPCell headerCell = (PdfPCell)el;
                headerCell.setHorizontalAlignment(0);
                headerCell.setBorder(0);
                headerCell.setPadding(1.0f);
                headerCell.enableBorderSide(2);
                headerCell.setBorderWidthBottom(2.0f);
                headerCell.setBorderColorBottom(Color.white);
                final Color col = new Color(Integer.parseInt("F5F5F5", 16));
                headerCell.setBackgroundColor(col);
            }
        }
        else if (styleClass.equals("labelCell")) {
            if (el instanceof Chunk) {
                final Chunk headerChunk = (Chunk)el;
                headerChunk.setFont(new Font(this.font, 12.0f, 1));
            }
            else if (el instanceof PdfPCell) {
                final PdfPCell headerCell = (PdfPCell)el;
                headerCell.setHorizontalAlignment(1);
                headerCell.setPadding(3.0f);
                headerCell.setBorderWidth(0.0f);
                headerCell.setBorder(0);
                final Color col = new Color(Integer.parseInt("BFD2E3", 16));
                headerCell.setBackgroundColor(col);
            }
        }
        else if (styleClass.equals("evenRow")) {
            if (el instanceof Chunk) {
                final Chunk headerChunk = (Chunk)el;
                headerChunk.setFont(new Font(this.font, 11.0f, 1));
            }
            else if (el instanceof PdfPCell) {
                final PdfPCell headerCell = (PdfPCell)el;
                headerCell.setHorizontalAlignment(0);
                headerCell.setPadding(3.0f);
                headerCell.setBorderWidth(0.0f);
                headerCell.setBorder(0);
                final Color col = new Color(Integer.parseInt("FFFFFF", 16));
                headerCell.setBackgroundColor(col);
            }
        }
        else if (styleClass.equals("oddRow")) {
            if (el instanceof Chunk) {
                final Chunk headerChunk = (Chunk)el;
                headerChunk.setFont(new Font(this.font, 11.0f, 1));
            }
            else if (el instanceof PdfPCell) {
                final PdfPCell headerCell = (PdfPCell)el;
                headerCell.setHorizontalAlignment(0);
                headerCell.setPadding(3.0f);
                headerCell.setBorderWidth(0.0f);
                headerCell.setBorder(0);
                final Color col = new Color(Integer.parseInt("F5F5F5", 16));
                headerCell.setBackgroundColor(col);
            }
        }
        else if (styleClass.equals("nodataRow")) {
            if (el instanceof Chunk) {
                final Chunk headerChunk = (Chunk)el;
                headerChunk.setFont(new Font(this.font, 11.0f, 1));
            }
            else if (el instanceof PdfPCell) {
                final PdfPCell headerCell = (PdfPCell)el;
                headerCell.setHorizontalAlignment(1);
                headerCell.setPadding(3.0f);
                headerCell.setBorderWidth(0.0f);
                headerCell.setBorder(0);
                final Color col = new Color(Integer.parseInt("FFFFFF", 16));
                headerCell.setBackgroundColor(col);
            }
        }
        else if (styleClass.equals("boxHeader")) {
            if (el instanceof Chunk) {
                final Chunk ck = (Chunk)el;
                ck.setFont(new Font(this.font, 13.0f, 1));
            }
            else if (el instanceof PdfPCell) {
                final PdfPCell cell = (PdfPCell)el;
                cell.setBorderWidth(0.0f);
                cell.setBorder(0);
                cell.setBackgroundColor(new Color(150, 190, 255));
            }
        }
        else if (styleClass.equals("PropSheetRowTable")) {
            if (el instanceof PdfPTable) {
                final PdfPTable table = (PdfPTable)el;
                table.setWidthPercentage(100.0f);
                table.setSpacingBefore(0.0f);
            }
        }
        else if (styleClass.equals("title")) {
            if (el instanceof Chunk) {
                final Chunk titleChunk = (Chunk)el;
                final Color col = new Color(Integer.parseInt("000000", 16));
                titleChunk.setFont(new Font(this.font, 13.0f, 1, col));
            }
            else if (el instanceof PdfPCell) {
                final PdfPCell headerCell = (PdfPCell)el;
                headerCell.setPadding(5.0f);
                headerCell.setBorder(0);
                headerCell.enableBorderSide(2);
                headerCell.setBorderColorBottom(Color.black);
                headerCell.setBorderWidthBottom(2.0f);
                final Color col = new Color(Integer.parseInt("FFFFFF", 16));
                headerCell.setBackgroundColor(col);
            }
        }
        if (el instanceof Chunk && !PDFUtil.isRightToLeft(pdfWriter)) {
            final String content = ((Chunk)el).getContent();
            if (content != null) {
                final Phrase ph = this.selector.process(content);
                return (Element)ph;
            }
        }
        return el;
    }
    
    public BaseFont getBaseFont() {
        final String languageName = I18N.getLocale().getLanguage();
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont("Helvetica", "Cp1252", false);
            final String fontName = this.fontProps.getProperty(languageName + ".fontname");
            final String isExternal = this.fontProps.getProperty(languageName + ".isExternalFont");
            if (fontName != null) {
                final String encoding = (this.fontProps.getProperty(languageName + ".encoding") != null) ? this.fontProps.getProperty(languageName + ".encoding") : "";
                if (!"true".equals(isExternal)) {
                    return BaseFont.createFont(fontName, encoding, false);
                }
                final String font_path = this.fontProps.getProperty(languageName + ".url");
                if (this.checkIfFontFilePresent(fontName, font_path)) {
                    return BaseFont.createFont(font_path, encoding, false);
                }
                DefaultPDFTheme.OUT.log(Level.WARNING, "Font file is not present on the specified URL: {0} ", font_path);
            }
        }
        catch (final DocumentException de) {
            DefaultPDFTheme.OUT.log(Level.WARNING, "DocumentException occurred while getting base_font for the user locale : " + de);
        }
        catch (final IOException ioe) {
            DefaultPDFTheme.OUT.log(Level.WARNING, "IOException occurred while getting base_font for the user locale : " + ioe);
        }
        return bf;
    }
    
    private boolean checkIfFontFilePresent(final String fontName, final String fontFilePath) {
        if (this.extFontMap.get(fontName) != null) {
            return this.extFontMap.get(fontName);
        }
        final boolean isFileExists = new File(fontFilePath).exists();
        this.extFontMap.put(fontName, isFileExists);
        return isFileExists;
    }
    
    static {
        OUT = Logger.getLogger(DefaultPDFTheme.class.getName());
    }
}
