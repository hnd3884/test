package com.me.ems.framework.reports.core.pdf;

import com.lowagie.text.FontFactory;
import com.lowagie.text.DocumentException;
import java.util.logging.Level;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Element;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import java.util.Date;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import java.util.Hashtable;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import java.awt.Color;
import com.lowagie.text.Font;
import java.util.logging.Logger;

public class PdfDocument
{
    public static Logger logger;
    public static Font black_text_small;
    public static Font black_text_medium;
    public static Font black_text_heading;
    public static Font copyright_text;
    public static Font black_text_bold;
    public static Font white_text_small;
    public static final Color GREEN_STRIP;
    public static final Color BLUE_STRIP;
    public static final Color ROW_HEADER_GREEN;
    public static final Color ROW_HEADER_BLUE;
    public static final Color ODD_ROW_GREEN;
    public static final Color ODD_ROW_BLUE;
    public static final Color TAB_HEADER_BLUE;
    public static final Color WHITE;
    public static final Color BLUE_BACKGROUND;
    public static final Color DARK_BLUE;
    public static final Color BLUE_BORDER;
    public static Font tab_title;
    public static Font dark_blue_text_big;
    public static Font dark_blue_text_small;
    public static Color stripColor;
    public static Color oddRowColor;
    public static Color rowHeaderColor;
    private Document document;
    private PdfWriter pdfwriter;
    public static final int ALIGN_UNDEFINED = -1;
    public static final int HORIZONTAL_ALIGN_LEFT = 0;
    public static final int HORIZONTAL_ALIGN_CENTER = 1;
    public static final int HORIZONTAL_ALIGN_RIGHT = 2;
    public static final int HORIZONTAL_ALIGN_JUSTIFIED = 3;
    public static final int VERTICAL_ALIGN_TOP = 4;
    public static final int VERTICAL_ALIGN_MIDDLE = 5;
    public static final int VERTICAL_ALIGN_BOTTOM = 6;
    public static final int VERTICAL_ALIGN_BASELINE = 7;
    public static final int ALIGN_JUSTIFIED_ALL = 8;
    
    public PdfDocument(final Document doc) {
        this.document = null;
        this.pdfwriter = null;
        this.document = doc;
        initializeFonts();
    }
    
    public static void initializeFonts() {
        final BaseFont bf = PDFUtil.getBaseFontForLocale();
        PdfDocument.tab_title = new Font(bf, 10.0f, 0, Color.WHITE);
        PdfDocument.dark_blue_text_big = new Font(bf, 10.0f, 1, PdfDocument.DARK_BLUE);
        PdfDocument.dark_blue_text_small = new Font(bf, 8.0f, 1, PdfDocument.DARK_BLUE);
        PdfDocument.black_text_bold = new Font(bf, 8.0f, 1, Color.BLACK);
        PdfDocument.black_text_small = new Font(bf, 8.0f, 0, Color.BLACK);
        PdfDocument.black_text_medium = new Font(bf, 9.0f, 0, Color.BLACK);
        PdfDocument.black_text_heading = new Font(bf, 10.0f, 0, Color.BLACK);
        PdfDocument.white_text_small = new Font(bf, 8.0f, 0, Color.WHITE);
        PdfDocument.copyright_text = new Font(bf, 8.0f, 0, new Color(10790052));
    }
    
    public void setTitleAndDescription(final Hashtable details) {
        try {
            initializeFonts();
            final String title = details.get("TITLE").toString();
            final String description = details.get("DESCRIPTION").toString();
            final PdfPTable table_title = new PdfPTable(2);
            table_title.setWidthPercentage(100.0f);
            final Phrase ph_title = new Phrase();
            ph_title.add((Object)new Chunk(title + "\n\n", PdfDocument.black_text_heading));
            final PdfPCell dataCell = new PdfPCell(ph_title);
            dataCell.setBackgroundColor(PdfDocument.TAB_HEADER_BLUE);
            dataCell.setBorder(0);
            table_title.addCell(dataCell);
            table_title.setHorizontalAlignment(0);
            final Date generateDate = new Date();
            final long dateValue = generateDate.getTime();
            final String formattedDate = SYMClientUtil.getExportTimeString(dateValue);
            final Phrase ph = new Phrase();
            ph.add((Object)new Chunk(I18N.getMsg("dc.cfg.colln.rep.GENERATED_ON", new Object[0]) + " - " + formattedDate, PdfDocument.black_text_small));
            final PdfPCell t = new PdfPCell(ph);
            t.setBackgroundColor(PdfDocument.TAB_HEADER_BLUE);
            t.setHorizontalAlignment(2);
            t.setBorder(0);
            table_title.addCell(t);
            this.document.add((Element)table_title);
            final PdfPTable table_desc = new PdfPTable(1);
            table_desc.setWidthPercentage(100.0f);
            final Phrase ph_desc = new Phrase();
            ph_desc.add((Object)new Chunk(description + "\n\n", PdfDocument.black_text_small));
            final PdfPCell descCell = new PdfPCell(ph_desc);
            descCell.setBorder(0);
            table_desc.addCell(descCell);
            table_desc.setHorizontalAlignment(0);
            this.document.add((Element)table_desc);
            final Phrase footerPhrase = new Phrase();
            footerPhrase.add((Object)new Chunk(I18N.getMsg("dc.rep.pdf.page", new Object[0]) + " ", PdfDocument.black_text_small));
            final HeaderFooter footer = new HeaderFooter(footerPhrase, new Phrase(""));
            footer.setAlignment(1);
            footer.setBorderColor(Color.white);
            footer.getBefore().setFont(PdfDocument.black_text_small);
            this.document.setFooter(footer);
        }
        catch (final Exception ex) {
            PdfDocument.logger.log(Level.WARNING, "Exception while setting title and description for PDF : " + ex);
        }
    }
    
    public void setEndOfDocument() {
        try {
            final String conf = PDFUtil.getCopyrightString();
            final PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(0.0f);
            final PdfPCell dataCell = new PdfPCell(new Phrase(conf, PdfDocument.copyright_text));
            dataCell.setPaddingTop(10.0f);
            dataCell.setBackgroundColor(PdfDocument.WHITE);
            dataCell.setBorder(0);
            dataCell.setVerticalAlignment(6);
            dataCell.setHorizontalAlignment(1);
            table.addCell(dataCell);
            this.document.add((Element)table);
        }
        catch (final DocumentException ex) {
            PdfDocument.logger.log(Level.WARNING, "documentException while setting End of Document for PDF : " + ex);
        }
    }
    
    public void close() {
        this.setEndOfDocument();
        if (this.document != null) {
            this.document.close();
        }
        if (this.pdfwriter != null) {
            this.pdfwriter.close();
        }
    }
    
    static {
        PdfDocument.logger = Logger.getLogger(PdfDocument.class.getName());
        PdfDocument.black_text_small = FontFactory.getFont("Lato", 8.0f, 0, Color.BLACK);
        PdfDocument.black_text_medium = FontFactory.getFont("Lato", 9.0f, 0, Color.BLACK);
        PdfDocument.black_text_heading = FontFactory.getFont("Lato", 10.0f, 0, Color.BLACK);
        PdfDocument.copyright_text = FontFactory.getFont("Lato", 8.0f, 0, new Color(10790052));
        PdfDocument.black_text_bold = FontFactory.getFont("Lato", 8.0f, 1, Color.BLACK);
        PdfDocument.white_text_small = FontFactory.getFont("Lato", 8.0f, 0, Color.WHITE);
        GREEN_STRIP = new Color(9740932);
        BLUE_STRIP = new Color(686010);
        ROW_HEADER_GREEN = new Color(14345167);
        ROW_HEADER_BLUE = new Color(12506096);
        ODD_ROW_GREEN = new Color(15331814);
        ODD_ROW_BLUE = new Color(15922682);
        TAB_HEADER_BLUE = new Color(16777215);
        WHITE = new Color(255, 255, 255);
        BLUE_BACKGROUND = new Color(686010);
        DARK_BLUE = new Color(2313353);
        BLUE_BORDER = new Color(16777215);
        PdfDocument.tab_title = FontFactory.getFont("Lato", 10.0f, 0, Color.WHITE);
        PdfDocument.dark_blue_text_big = FontFactory.getFont("Lato", 10.0f, 1, PdfDocument.DARK_BLUE);
        PdfDocument.dark_blue_text_small = FontFactory.getFont("Lato", 8.0f, 1, PdfDocument.DARK_BLUE);
    }
}
