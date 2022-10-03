package com.me.mdm.webclient.reports;

import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.HeaderFooter;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.IOException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.sql.SQLException;
import java.util.Date;
import com.adventnet.ds.query.DataSet;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import java.io.OutputStream;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import java.awt.Color;
import com.lowagie.text.Font;
import java.util.logging.Logger;

public class MDMPdfDocument
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
    
    public MDMPdfDocument(final ByteArrayOutputStream baos, final Long userID, final Hashtable details, final HttpServletRequest request) {
        this.document = null;
        this.pdfwriter = null;
        this.document = new Document();
        MDMPdfDocument.logger.log(Level.INFO, "Starting to create a document...");
        try {
            this.pdfwriter = PdfWriter.getInstance(this.document, (OutputStream)baos);
            this.document.open();
            initializeFonts();
            this.setThemeColorValue(request, userID);
            this.setLogo(request, userID);
            this.setTitleAndDescription(details);
        }
        catch (final Exception ex) {
            MDMPdfDocument.logger.log(Level.WARNING, "Exception occurred while creating PDF : ", ex);
        }
    }
    
    public MDMPdfDocument(final Document doc) {
        this.document = null;
        this.pdfwriter = null;
        this.document = doc;
        initializeFonts();
    }
    
    public void addHeading(final String heading) {
        try {
            final PdfPTable pTable = new PdfPTable(1);
            final PdfPCell cell = new PdfPCell((Phrase)new Paragraph(heading, MDMPdfDocument.tab_title));
            cell.setBackgroundColor(MDMPdfDocument.BLUE_BACKGROUND);
            cell.setColspan(1);
            cell.setBorder(0);
            pTable.addCell(cell);
            pTable.setWidthPercentage(100.0f);
            pTable.setSpacingBefore(10.0f);
            pTable.setSpacingAfter(5.0f);
            this.document.add((Element)pTable);
        }
        catch (final DocumentException e) {
            MDMPdfDocument.logger.log(Level.WARNING, "DocumentException occurred while adding heading to for a section for PDF : ", (Throwable)e);
        }
    }
    
    public void addHorizontalPdfSection(final MDMPdfHorizontalSection pdfSec, final DataSet ds) {
        try {
            int tableSize = 0;
            final String heading = pdfSec.heading;
            final String sectionHeading = pdfSec.title;
            if (!heading.equals("")) {
                this.addHeading(pdfSec.heading);
            }
            final Map cols = pdfSec.columnDetails;
            tableSize = cols.size();
            final PdfPTable pTable = new PdfPTable(tableSize);
            final float[] columnWidths = this.getSectionWidths(pdfSec.columnWidths, tableSize);
            pTable.setWidths(columnWidths);
            PdfPCell cell = null;
            if (!sectionHeading.equals("")) {
                cell = new PdfPCell((Phrase)new Paragraph(sectionHeading, MDMPdfDocument.white_text_small));
                cell.setBackgroundColor(MDMPdfDocument.rowHeaderColor);
                cell.setBorderColor(MDMPdfDocument.rowHeaderColor);
                cell.setColspan(tableSize);
                pTable.addCell(cell);
            }
            final Set set = cols.keySet();
            for (final String ele : set) {
                cell = new PdfPCell((Phrase)new Paragraph(cols.get(ele).toString(), MDMPdfDocument.black_text_bold));
                cell.setBackgroundColor(MDMPdfDocument.oddRowColor);
                cell.setBorderColor(MDMPdfDocument.BLUE_BORDER);
                pTable.addCell(cell);
            }
            final List dateColumns = pdfSec.dateColumns;
            try {
                int count = 0;
                while (ds.next()) {
                    final Iterator iter = set.iterator();
                    while (iter.hasNext()) {
                        final String column = iter.next().toString();
                        String columnVal = "";
                        if (dateColumns.contains(column)) {
                            columnVal = new Date(ds.getLong(column)).toString();
                        }
                        else {
                            columnVal = ds.getValue(column).toString();
                        }
                        cell = new PdfPCell((Phrase)new Paragraph(columnVal, MDMPdfDocument.black_text_small));
                        if (count % 2 != 0) {
                            cell.setBackgroundColor(MDMPdfDocument.oddRowColor);
                        }
                        cell.setBorderColor(MDMPdfDocument.BLUE_BORDER);
                        pTable.addCell(cell);
                    }
                    ++count;
                }
            }
            catch (final SQLException ex) {
                MDMPdfDocument.logger.log(Level.WARNING, "SQLException occurred while adding Horizontal Section for PDF : ", ex);
            }
            pTable.setWidthPercentage(pdfSec.sectionWidth);
            pTable.setSpacingAfter(10.0f);
            this.document.add((Element)pTable);
        }
        catch (final DocumentException e) {
            MDMPdfDocument.logger.log(Level.WARNING, "DocumentException occurred while adding Horizontal Section for PDF : ", (Throwable)e);
        }
    }
    
    public void addHorizontalPdfSection(final MDMPdfHorizontalSection pdfSec) {
        try {
            final List valuesList = pdfSec.valuesList;
            MDMPdfDocument.logger.log(Level.FINE, "valuesList : {0}", valuesList);
            if (valuesList == null || valuesList.isEmpty()) {
                MDMPdfDocument.logger.log(Level.WARNING, "Horizontal PDF Section should contain lists of column values or should have a DataSet as argument.");
                return;
            }
            int tableSize = 0;
            final String heading = pdfSec.heading;
            final String sectionHeading = pdfSec.title;
            if (!heading.equals("")) {
                this.addHeading(pdfSec.heading);
            }
            final Map cols = pdfSec.columnDetails;
            tableSize = cols.size();
            final PdfPTable pTable = new PdfPTable(tableSize);
            final float[] columnWidths = this.getSectionWidths(pdfSec.columnWidths, tableSize);
            pTable.setWidths(columnWidths);
            PdfPCell cell = null;
            if (sectionHeading != null && !sectionHeading.equals("")) {
                cell = new PdfPCell((Phrase)new Paragraph(sectionHeading, MDMPdfDocument.white_text_small));
                cell.setBackgroundColor(MDMPdfDocument.rowHeaderColor);
                cell.setBorderColor(MDMPdfDocument.rowHeaderColor);
                cell.setColspan(tableSize);
                pTable.addCell(cell);
            }
            final Set set = cols.keySet();
            for (final String ele : set) {
                cell = new PdfPCell((Phrase)new Paragraph(cols.get(ele).toString(), MDMPdfDocument.white_text_small));
                cell.setBackgroundColor(MDMPdfDocument.rowHeaderColor);
                cell.setPaddingBottom(5.0f);
                cell.setBorderColor(MDMPdfDocument.BLUE_BORDER);
                pTable.addCell(cell);
            }
            for (int q = 0; q < valuesList.size(); ++q) {
                final List columnValues = valuesList.get(q);
                for (int w = 0; w < columnValues.size(); ++w) {
                    String cellVal = "";
                    if (columnValues.get(w) != null) {
                        cellVal = columnValues.get(w).toString();
                    }
                    cell = new PdfPCell((Phrase)new Paragraph(cellVal, MDMPdfDocument.black_text_small));
                    if (q % 2 != 0) {
                        cell.setBackgroundColor(MDMPdfDocument.oddRowColor);
                    }
                    cell.setBorderColor(MDMPdfDocument.BLUE_BORDER);
                    pTable.addCell(cell);
                }
            }
            pTable.setWidthPercentage(pdfSec.sectionWidth);
            pTable.setSpacingAfter(10.0f);
            this.document.add((Element)pTable);
        }
        catch (final DocumentException e) {
            MDMPdfDocument.logger.log(Level.WARNING, "DocumentException occurred while adding Horizontal Section for PDF : ", (Throwable)e);
        }
    }
    
    public void addMultipleVerticalPdfSection(final MDMPdfVerticalSection[] pdfSections) {
        try {
            final int arrSize = pdfSections.length;
            final int os = arrSize * 2 - 1;
            final int secWidth = 100 - (os - 1) * 2;
            final float[] outerSize = new float[os];
            for (int size = 0; size < os; ++size) {
                if (size % 2 == 0) {
                    outerSize[size] = (float)secWidth;
                }
                else {
                    outerSize[size] = 2.0f;
                }
            }
            final PdfPTable pTable = new PdfPTable(os);
            pTable.setWidths(outerSize);
            for (int size2 = 0; size2 < pdfSections.length; ++size2) {
                final MDMPdfVerticalSection pdfSec = pdfSections[size2];
                int tableSize = 0;
                tableSize = pdfSec.verticalColumnCount * 2;
                final String heading = pdfSec.heading;
                final String sectionHeading = pdfSec.title;
                final PdfPTable innerTable = new PdfPTable(tableSize);
                final float[] columnWidths = this.getSectionWidths(pdfSec.columnWidths, tableSize);
                innerTable.setWidths(columnWidths);
                PdfPCell cell = null;
                if (!sectionHeading.equals("")) {
                    cell = new PdfPCell((Phrase)new Paragraph(sectionHeading, MDMPdfDocument.white_text_small));
                    cell.setBackgroundColor(MDMPdfDocument.rowHeaderColor);
                    cell.setBorderColor(MDMPdfDocument.rowHeaderColor);
                    cell.setColspan(tableSize);
                    innerTable.addCell(cell);
                }
                final LinkedHashMap cols = (LinkedHashMap)pdfSec.sectionDetails;
                final Set set = cols.keySet();
                for (final String ele : set) {
                    cell = new PdfPCell((Phrase)new Paragraph(ele, MDMPdfDocument.black_text_bold));
                    cell.setBackgroundColor(MDMPdfDocument.oddRowColor);
                    cell.setBorderColor(MDMPdfDocument.BLUE_BORDER);
                    innerTable.addCell(cell);
                    cell = new PdfPCell((Phrase)new Paragraph(cols.get(ele).toString(), MDMPdfDocument.black_text_small));
                    cell.setBorderColor(MDMPdfDocument.BLUE_BORDER);
                    innerTable.addCell(cell);
                }
                innerTable.setHorizontalAlignment(0);
                PdfPCell outerCell = new PdfPCell(innerTable);
                outerCell.setBorder(0);
                outerCell.setColspan(1);
                pTable.addCell(outerCell);
                outerCell = new PdfPCell((Phrase)new Paragraph("", MDMPdfDocument.tab_title));
                outerCell.setBorder(0);
                outerCell.setColspan(1);
                pTable.addCell(outerCell);
            }
            pTable.setWidthPercentage(100.0f);
            pTable.setSpacingAfter(10.0f);
            this.document.add((Element)pTable);
        }
        catch (final DocumentException e) {
            MDMPdfDocument.logger.log(Level.WARNING, "DocumentException while adding multiple Vertical Section for PDF : ", (Throwable)e);
        }
    }
    
    public void addVerticalPdfSection(final MDMPdfVerticalSection pdfSec) {
        try {
            int tableSize = 0;
            tableSize = pdfSec.verticalColumnCount * 2;
            final String heading = pdfSec.heading;
            final String sectionHeading = pdfSec.title;
            if (!heading.equals("")) {
                this.addHeading(heading);
            }
            final PdfPTable pTable = new PdfPTable(tableSize);
            final float[] columnWidths = this.getSectionWidths(pdfSec.columnWidths, tableSize);
            pTable.setWidths(columnWidths);
            PdfPCell cell = null;
            if (!sectionHeading.equals("")) {
                cell = new PdfPCell((Phrase)new Paragraph(sectionHeading, MDMPdfDocument.white_text_small));
                cell.setBackgroundColor(MDMPdfDocument.rowHeaderColor);
                cell.setBorderColor(MDMPdfDocument.rowHeaderColor);
                cell.setColspan(tableSize);
                pTable.addCell(cell);
            }
            final LinkedHashMap cols = (LinkedHashMap)pdfSec.sectionDetails;
            final Set set = cols.keySet();
            for (final String ele : set) {
                cell = new PdfPCell((Phrase)new Paragraph(ele, MDMPdfDocument.black_text_medium));
                cell.setBackgroundColor(MDMPdfDocument.oddRowColor);
                cell.setBorderColor(MDMPdfDocument.BLUE_BORDER);
                pTable.addCell(cell);
                cell = new PdfPCell((Phrase)new Paragraph(String.valueOf(cols.get(ele)), MDMPdfDocument.black_text_small));
                cell.setBorderColor(MDMPdfDocument.BLUE_BORDER);
                pTable.addCell(cell);
            }
            pTable.setWidthPercentage(pdfSec.sectionWidth);
            pTable.setSpacingAfter(10.0f);
            this.document.add((Element)pTable);
        }
        catch (final DocumentException ex) {
            MDMPdfDocument.logger.log(Level.WARNING, "DocumentException while adding Vertical Section for PDF : ", (Throwable)ex);
        }
    }
    
    public void addImage(final MDMPdfImageSection imageSec) {
        try {
            final Image img = Image.getInstance(imageSec.imageByteArrayStream.toByteArray());
            img.scalePercent(imageSec.imageScalePercentage);
            final Chunk chunk = new Chunk(img, imageSec.imageOffsetX, imageSec.imageOffsetY);
            final PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(0.0f);
            final PdfPCell image = new PdfPCell(new Phrase(chunk));
            image.setBorder(0);
            image.setVerticalAlignment(imageSec.verticalAlignment);
            image.setHorizontalAlignment(imageSec.horizontalAlignment);
            table.addCell(image);
            this.document.add((Element)table);
        }
        catch (final IOException e) {
            MDMPdfDocument.logger.log(Level.WARNING, "IOException while adding image Section for PDF : ", e);
        }
        catch (final DocumentException e2) {
            MDMPdfDocument.logger.log(Level.WARNING, "DocumentException while adding image Section for PDF : ", (Throwable)e2);
        }
    }
    
    public void setThemeColorValue(final HttpServletRequest request, final Long userID) throws Exception {
        final String theme = PDFUtil.getSelectedTheme(request);
        if (theme.equals("green")) {
            MDMPdfDocument.stripColor = PDFUtil.GREEN_STRIP;
            MDMPdfDocument.oddRowColor = PDFUtil.ODD_ROW_GREEN;
            MDMPdfDocument.rowHeaderColor = PDFUtil.ROW_HEADER_GREEN;
        }
        else if (theme.equals("sdp-blue") || theme.equals("dm-default")) {
            MDMPdfDocument.stripColor = PDFUtil.SDP_BLUE_STRIP;
            MDMPdfDocument.oddRowColor = PDFUtil.ODD_ROW_SDP_BLUE;
            MDMPdfDocument.rowHeaderColor = PDFUtil.ROW_HEADER_SDP_BLUE;
        }
        else {
            MDMPdfDocument.stripColor = PDFUtil.BLUE_STRIP;
            MDMPdfDocument.oddRowColor = PDFUtil.ODD_ROW_BLUE;
            MDMPdfDocument.rowHeaderColor = PDFUtil.ROW_HEADER_BLUE;
        }
    }
    
    public void setLogo(final HttpServletRequest request, final Long userID) {
        try {
            final String theme = PDFUtil.getSelectedTheme(request);
            this.setThemeColorValue(request, userID);
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            final String logoPath = CustomerInfoUtil.getInstance().getLogoPath(customerId);
            final Image i = Image.getInstance(logoPath);
            i.scaleToFit(272.0f, 40.0f);
            final Chunk chunk = new Chunk(i, 0.0f, 0.0f);
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(0.0f);
            final PdfPCell logo = new PdfPCell(new Phrase(chunk));
            logo.setBorder(0);
            logo.setVerticalAlignment(4);
            logo.setHorizontalAlignment(0);
            table.addCell(logo);
            table = PDFUtil.addCustomerNameToPDFReport(table);
            this.document.add((Element)table);
            final PdfPTable table_spaces = new PdfPTable(1);
            table_spaces.setWidthPercentage(100.0f);
            final PdfPCell emptycells = new PdfPCell(new Phrase(""));
            emptycells.setBackgroundColor(MDMPdfDocument.WHITE);
            emptycells.setBorder(0);
            emptycells.setPaddingTop(1.0f);
            emptycells.setPaddingBottom(1.0f);
            emptycells.setPaddingRight(1.0f);
            emptycells.setPaddingLeft(1.0f);
            table_spaces.addCell(emptycells);
            this.document.add((Element)table_spaces);
            final PdfPTable topline_img = new PdfPTable(1);
            topline_img.setWidthPercentage(100.0f);
            final PdfPCell emptyRow = new PdfPCell(new Phrase(""));
            emptyRow.setBackgroundColor(MDMPdfDocument.stripColor);
            emptyRow.setBorder(0);
            emptyRow.setPaddingRight(1.0f);
            emptyRow.setPaddingLeft(1.0f);
            emptyRow.setPaddingTop(1.0f);
            emptyRow.setPaddingBottom(0.0f);
            topline_img.addCell(emptyRow);
            this.document.add((Element)topline_img);
            this.document.add((Element)table_spaces);
        }
        catch (final Exception ex) {
            MDMPdfDocument.logger.log(Level.WARNING, "Exception while adding Logo : ", ex);
        }
    }
    
    public void setTitleAndDescription(final Hashtable details) {
        try {
            initializeFonts();
            final String title = details.get("TITLE").toString();
            final String description = details.get("DESCRIPTION").toString();
            final PdfPTable table_title = new PdfPTable(2);
            table_title.setWidthPercentage(100.0f);
            final Phrase ph_title = new Phrase();
            ph_title.add((Object)new Chunk(title + "\n\n", MDMPdfDocument.black_text_heading));
            final PdfPCell dataCell = new PdfPCell(ph_title);
            dataCell.setBackgroundColor(MDMPdfDocument.TAB_HEADER_BLUE);
            dataCell.setBorder(0);
            table_title.addCell(dataCell);
            table_title.setHorizontalAlignment(0);
            final Date generateDate = new Date();
            final long dateValue = generateDate.getTime();
            final String formattedDate = SYMClientUtil.getExportTimeString(dateValue);
            final Phrase ph = new Phrase();
            ph.add((Object)new Chunk(I18N.getMsg("dc.cfg.colln.rep.GENERATED_ON", new Object[0]) + " - " + formattedDate, MDMPdfDocument.black_text_small));
            final PdfPCell t = new PdfPCell(ph);
            t.setBackgroundColor(MDMPdfDocument.TAB_HEADER_BLUE);
            t.setHorizontalAlignment(2);
            t.setBorder(0);
            table_title.addCell(t);
            this.document.add((Element)table_title);
            final PdfPTable table_desc = new PdfPTable(1);
            table_desc.setWidthPercentage(100.0f);
            final Phrase ph_desc = new Phrase();
            ph_desc.add((Object)new Chunk(description + "\n\n", MDMPdfDocument.black_text_small));
            final PdfPCell descCell = new PdfPCell(ph_desc);
            descCell.setBorder(0);
            table_desc.addCell(descCell);
            table_desc.setHorizontalAlignment(0);
            this.document.add((Element)table_desc);
            final Phrase footerPhrase = new Phrase();
            footerPhrase.add((Object)new Chunk(I18N.getMsg("mdm.rep.pdf.page", new Object[0]) + " ", MDMPdfDocument.black_text_small));
            final HeaderFooter footer = new HeaderFooter(footerPhrase, new Phrase(""));
            footer.setAlignment(1);
            footer.setBorderColor(Color.white);
            footer.getBefore().setFont(MDMPdfDocument.black_text_small);
            this.document.setFooter(footer);
        }
        catch (final Exception ex) {
            MDMPdfDocument.logger.log(Level.WARNING, "Exception while setting title and description for PDF : {0}", ex);
        }
    }
    
    public void setEndOfDocument() {
        try {
            final String conf = PDFUtil.getCopyrightString();
            final PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(0.0f);
            final PdfPCell dataCell = new PdfPCell(new Phrase(conf, MDMPdfDocument.copyright_text));
            dataCell.setPaddingTop(10.0f);
            dataCell.setBackgroundColor(MDMPdfDocument.WHITE);
            dataCell.setBorder(0);
            dataCell.setVerticalAlignment(6);
            dataCell.setHorizontalAlignment(1);
            table.addCell(dataCell);
            this.document.add((Element)table);
        }
        catch (final DocumentException ex) {
            MDMPdfDocument.logger.log(Level.WARNING, "documentException while setting End of Document for PDF : {0}", (Throwable)ex);
        }
    }
    
    public static void initializeFonts() {
        final BaseFont bf = PDFUtil.getBaseFontForLocale();
        MDMPdfDocument.tab_title = new Font(bf, 10.0f, 0, Color.WHITE);
        MDMPdfDocument.dark_blue_text_big = new Font(bf, 10.0f, 1, MDMPdfDocument.DARK_BLUE);
        MDMPdfDocument.dark_blue_text_small = new Font(bf, 8.0f, 1, MDMPdfDocument.DARK_BLUE);
        MDMPdfDocument.black_text_bold = new Font(bf, 8.0f, 1, Color.BLACK);
        MDMPdfDocument.black_text_small = new Font(bf, 8.0f, 0, Color.BLACK);
        MDMPdfDocument.black_text_medium = new Font(bf, 9.0f, 0, Color.BLACK);
        MDMPdfDocument.black_text_heading = new Font(bf, 10.0f, 0, Color.BLACK);
        MDMPdfDocument.white_text_small = new Font(bf, 8.0f, 0, Color.WHITE);
        MDMPdfDocument.copyright_text = new Font(bf, 8.0f, 0, new Color(10790052));
    }
    
    public void addLine() {
        try {
            final PdfPTable topline_img = new PdfPTable(1);
            topline_img.setWidthPercentage(100.0f);
            final PdfPCell emptyRow = new PdfPCell(new Phrase(""));
            emptyRow.setBackgroundColor(MDMPdfDocument.stripColor);
            emptyRow.setBorder(0);
            emptyRow.setPaddingRight(1.0f);
            emptyRow.setPaddingLeft(1.0f);
            topline_img.addCell(emptyRow);
            this.document.add((Element)topline_img);
        }
        catch (final DocumentException e) {
            MDMPdfDocument.logger.log(Level.WARNING, "DocumentException while adding a line in PDF : {0}", (Throwable)e);
        }
    }
    
    public float[] getSectionWidths(float[] columnWidths, final int tableSize) {
        if (columnWidths == null) {
            final float width = (float)(100 / tableSize);
            columnWidths = new float[tableSize];
            for (int cs = 0; cs < tableSize; ++cs) {
                columnWidths[cs] = width;
            }
        }
        return columnWidths;
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
        MDMPdfDocument.logger = Logger.getLogger(MDMPdfDocument.class.getName());
        MDMPdfDocument.black_text_small = FontFactory.getFont("Lato", 8.0f, 0, Color.BLACK);
        MDMPdfDocument.black_text_medium = FontFactory.getFont("Lato", 9.0f, 0, Color.BLACK);
        MDMPdfDocument.black_text_heading = FontFactory.getFont("Lato", 10.0f, 0, Color.BLACK);
        MDMPdfDocument.copyright_text = FontFactory.getFont("Lato", 8.0f, 0, new Color(10790052));
        MDMPdfDocument.black_text_bold = FontFactory.getFont("Lato", 8.0f, 1, Color.BLACK);
        MDMPdfDocument.white_text_small = FontFactory.getFont("Lato", 8.0f, 0, Color.WHITE);
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
        MDMPdfDocument.tab_title = FontFactory.getFont("Lato", 10.0f, 0, Color.WHITE);
        MDMPdfDocument.dark_blue_text_big = FontFactory.getFont("Lato", 10.0f, 1, MDMPdfDocument.DARK_BLUE);
        MDMPdfDocument.dark_blue_text_small = FontFactory.getFont("Lato", 8.0f, 1, MDMPdfDocument.DARK_BLUE);
    }
}
