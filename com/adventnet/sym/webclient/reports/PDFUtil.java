package com.adventnet.sym.webclient.reports;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.lowagie.text.pdf.BaseFont;
import java.util.LinkedHashMap;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import java.util.HashMap;
import java.util.regex.Pattern;
import com.me.devicemanagement.framework.server.reportcriteria.CriteriaColumnValueUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilterCriteria;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilter;
import java.net.URLDecoder;
import com.adventnet.client.view.web.ViewContext;
import java.util.Properties;
import java.util.Enumeration;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.reports.MDMPDFCustomizationUtil;
import com.me.devicemanagement.framework.webclient.reports.SYMReportUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import com.lowagie.text.DocumentException;
import java.util.logging.Level;
import com.lowagie.text.HeaderFooter;
import com.adventnet.sym.webclient.common.SYMClientUtil;
import java.util.Date;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.lowagie.text.pdf.PdfPTable;
import com.adventnet.sym.server.util.SyMUtil;
import com.adventnet.i18n.I18N;
import java.util.List;
import java.util.Hashtable;
import com.me.mdm.webclient.reports.MDMPdfDocument;
import java.io.OutputStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.Color;
import java.util.logging.Logger;

public class PDFUtil extends com.me.mdm.webclient.reports.PDFUtil
{
    public static Logger logger;
    public static Color new_table_header_color;
    public static Color new_odd_row_color;
    public static Color new_even_row_color;
    
    public static ByteArrayOutputStream generateDocument(final String toolID, final Long resourceID, final Long userID, final HttpServletRequest request) {
        final Document document = new Document();
        final ByteArrayOutputStream streamToWritePdf = new ByteArrayOutputStream();
        PdfWriter pdfwriter = null;
        try {
            final int viewID = Integer.parseInt(toolID);
            pdfwriter = PdfWriter.getInstance(document, (OutputStream)streamToWritePdf);
            document.open();
            setThemeColorValue(request, userID);
            final String nameOfResource = "";
            setDocumentHeaderAndFooter(toolID, document, nameOfResource, null, null, userID, request);
            setReportData(toolID, document, resourceID, request);
            setEndOfDocument(document, request);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            document.close();
            pdfwriter.close();
        }
        return streamToWritePdf;
    }
    
    public static ByteArrayOutputStream createDocument(final String toolID, final Long resourceID, final Long userID, final HttpServletRequest request) {
        final Hashtable detailsHash = getTitleAndDescription(toolID, request);
        final ByteArrayOutputStream streamToWritePdf = new ByteArrayOutputStream();
        final MDMPdfDocument mdmPdfDoc = new MDMPdfDocument(streamToWritePdf, userID, detailsHash, request);
        mdmPdfDoc.close();
        return streamToWritePdf;
    }
    
    public static ByteArrayOutputStream generateDocument(final String toolID, final Long resourceID, final String selectedTab, final Long userID, final List conditions, final HttpServletRequest request) {
        final Document document = new Document();
        final ByteArrayOutputStream streamToWritePdf = new ByteArrayOutputStream();
        PdfWriter pdfwriter = null;
        try {
            final int viewID = Integer.parseInt(toolID);
            pdfwriter = PdfWriter.getInstance(document, (OutputStream)streamToWritePdf);
            document.open();
            setThemeColorValue(request, userID);
            setDocumentHeaderAndFooter(toolID, document, null, null, null, userID, request);
            if (selectedTab.equalsIgnoreCase("shares")) {
                setSharesData(document, resourceID, conditions);
            }
            else {
                setReportData(toolID, document, resourceID, selectedTab, request);
            }
            setEndOfDocument(document, request);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            document.close();
            pdfwriter.close();
        }
        return streamToWritePdf;
    }
    
    public static ByteArrayOutputStream generateDocument(final String toolID, final Long taskID, final Long userID, final HttpServletRequest request, final String taskName) {
        final Document document = new Document();
        final ByteArrayOutputStream streamToWritePdf = new ByteArrayOutputStream();
        PdfWriter pdfwriter = null;
        try {
            final int viewID = Integer.parseInt(toolID);
            pdfwriter = PdfWriter.getInstance(document, (OutputStream)streamToWritePdf);
            document.open();
            setThemeColorValue(request, userID);
            final Hashtable hash = getTitleAndDescription(toolID, request);
            final String title = hash.get("TITLE") + " - " + taskName;
            final String desc = hash.get("DESCRIPTION");
            setDocumentHeaderAndFooter(title, desc, document);
            setReportData(toolID, document, taskID);
            setEndOfDocument(document, request);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            document.close();
            pdfwriter.close();
        }
        return streamToWritePdf;
    }
    
    public static void setThemeColorValue(final HttpServletRequest request, final Long userID) throws Exception {
        final String theme = getSelectedTheme(request);
        setColorValues(theme);
    }
    
    public static void setDocumentHeaderAndFooter(final String reportName, final Document doc) {
        setDocumentHeaderAndFooter(reportName, null, doc);
    }
    
    public static void setColorValues(final String theme) {
        if (theme.equals("green")) {
            PDFUtil.stripcolor = PDFUtil.GREEN_STRIP;
            PDFUtil.oddrowcolor = PDFUtil.ODD_ROW_GREEN;
            PDFUtil.rowheadercolor = PDFUtil.ROW_HEADER_GREEN;
        }
        else if (theme.equals("sdp-blue") || theme.equals("dm-default")) {
            PDFUtil.stripcolor = PDFUtil.SDP_BLUE_STRIP;
            PDFUtil.oddrowcolor = PDFUtil.ODD_ROW_SDP_BLUE;
            PDFUtil.rowheadercolor = PDFUtil.ROW_HEADER_SDP_BLUE;
        }
        else {
            PDFUtil.stripcolor = PDFUtil.BLUE_STRIP;
            PDFUtil.oddrowcolor = PDFUtil.ODD_ROW_BLUE;
            PDFUtil.rowheadercolor = PDFUtil.ROW_HEADER_BLUE;
        }
    }
    
    public static void setDocumentHeaderAndFooter(final String title, final String description, final Document doc) {
        try {
            setFonts();
            String desc = I18N.getMsg("dc.rep.pdf.cust_rep_for_inv", new Object[0]);
            if (description != null) {
                desc = description;
            }
            final String theme = SyMUtil.getInstance().getTheme();
            setColorValues(theme);
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(0.0f);
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            final String logoPath = CustomerInfoUtil.getInstance().getLogoPath(customerId);
            final Image i = Image.getInstance(logoPath);
            i.scaleToFit(272.0f, 40.0f);
            final Chunk chunk = new Chunk(i, 0.0f, 0.0f);
            final PdfPCell logo = new PdfPCell(new Phrase(chunk));
            logo.setBorder(0);
            logo.setVerticalAlignment(4);
            logo.setHorizontalAlignment(0);
            table.addCell(logo);
            table = addCustomerNameToPDFReport(table);
            doc.add((Element)table);
            final PdfPTable table_spaces = new PdfPTable(1);
            table_spaces.setWidthPercentage(100.0f);
            final PdfPCell emptycells = new PdfPCell(new Phrase(""));
            emptycells.setBackgroundColor(PDFUtil.WHITE);
            emptycells.setBorder(0);
            emptycells.setPaddingTop(1.0f);
            emptycells.setPaddingBottom(1.0f);
            emptycells.setPaddingRight(1.0f);
            emptycells.setPaddingLeft(1.0f);
            table_spaces.addCell(emptycells);
            doc.add((Element)table_spaces);
            final PdfPTable topline_img = new PdfPTable(1);
            topline_img.setWidthPercentage(100.0f);
            final PdfPCell emptyRow = new PdfPCell(new Phrase(""));
            emptyRow.setBackgroundColor(PDFUtil.stripcolor);
            emptyRow.setBorder(0);
            emptyRow.setPaddingRight(1.0f);
            emptyRow.setPaddingLeft(1.0f);
            emptyRow.setPaddingTop(1.0f);
            emptyRow.setPaddingBottom(0.0f);
            topline_img.addCell(emptyRow);
            doc.add((Element)topline_img);
            doc.add((Element)table_spaces);
            final PdfPTable table_title = new PdfPTable(2);
            table_title.setWidthPercentage(100.0f);
            final Phrase ph_title = new Phrase();
            ph_title.add((Object)new Chunk(title + "\n\n", PDFUtil.black_text_heading));
            final PdfPCell dataCell = new PdfPCell(ph_title);
            dataCell.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
            dataCell.setBorder(0);
            table_title.addCell(dataCell);
            table_title.setHorizontalAlignment(0);
            final Date generateDate = new Date();
            final long dateValue = generateDate.getTime();
            final String formattedDate = SYMClientUtil.getExportTimeString(dateValue);
            final Phrase ph = new Phrase();
            final String timeZoneMsg = getTimeZoneString();
            ph.add((Object)new Chunk(I18N.getMsg("dc.cfg.colln.rep.GENERATED_ON", new Object[0]) + " - " + formattedDate + timeZoneMsg, PDFUtil.black_text_small));
            final PdfPCell t = new PdfPCell(ph);
            t.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
            t.setHorizontalAlignment(2);
            t.setBorder(0);
            table_title.addCell(t);
            doc.add((Element)table_title);
            final PdfPTable table_desc = new PdfPTable(1);
            table_desc.setWidthPercentage(100.0f);
            final Phrase ph_desc = new Phrase();
            ph_desc.add((Object)new Chunk(desc + "\n\n", PDFUtil.black_text_small));
            final PdfPCell descCell = new PdfPCell(ph_desc);
            descCell.setBorder(0);
            table_desc.addCell(descCell);
            table_desc.setHorizontalAlignment(0);
            doc.add((Element)table_desc);
            final Phrase footerPhrase = new Phrase();
            footerPhrase.add((Object)new Chunk(I18N.getMsg("mdm.rep.pdf.page", new Object[0]) + " ", PDFUtil.black_text_small));
            final HeaderFooter footer = new HeaderFooter(footerPhrase, new Phrase(""));
            footer.setAlignment(1);
            footer.setBorderColor(Color.white);
            footer.getBefore().setFont(PDFUtil.black_text_small);
            doc.setFooter(footer);
        }
        catch (final DocumentException ex) {
            PDFUtil.logger.log(Level.WARNING, "DocumentException while setting the header and footer", (Throwable)ex);
        }
        catch (final MalformedURLException ex2) {
            PDFUtil.logger.log(Level.WARNING, "MalformedURLException while setting the header and footer", ex2);
        }
        catch (final IOException ex3) {
            PDFUtil.logger.log(Level.WARNING, "IOException while setting the header and footer", ex3);
        }
        catch (final Exception ex4) {
            PDFUtil.logger.log(Level.WARNING, "Exception while setting the header and footer", ex4);
        }
    }
    
    public static void setDocumentHeaderAndFooter(final String toolID, final Document doc, String nameOfResource, final String domainName, final String filterParam, final Long userID, final HttpServletRequest request) {
        try {
            setFonts();
            String title = "";
            String desc = "";
            if (toolID != null) {
                Hashtable hash = setTitleAndDescription(toolID, request);
                if (hash.isEmpty()) {
                    hash = SYMReportUtil.getViewParams(new Integer(toolID));
                }
                else {
                    nameOfResource = hash.get("NAME");
                }
                if (hash != null && !hash.isEmpty()) {
                    title = hash.get("TITLE");
                    desc = hash.get("DESCRIPTION");
                }
            }
            final String theme = getSelectedTheme(request);
            setThemeColorValue(request, userID);
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
            table = addCustomerNameToPDFReport(table);
            doc.add((Element)table);
            final PdfPTable table_spaces = new PdfPTable(1);
            table_spaces.setWidthPercentage(100.0f);
            final PdfPCell emptycells = new PdfPCell(new Phrase(""));
            emptycells.setBackgroundColor(PDFUtil.WHITE);
            emptycells.setBorder(0);
            emptycells.setPaddingTop(1.0f);
            emptycells.setPaddingBottom(1.0f);
            emptycells.setPaddingRight(1.0f);
            emptycells.setPaddingLeft(1.0f);
            table_spaces.addCell(emptycells);
            doc.add((Element)table_spaces);
            final PdfPTable topline_img = new PdfPTable(1);
            topline_img.setWidthPercentage(100.0f);
            final PdfPCell emptyRow = new PdfPCell(new Phrase(""));
            emptyRow.setBackgroundColor(PDFUtil.stripcolor);
            emptyRow.setBorder(0);
            emptyRow.setPaddingRight(1.0f);
            emptyRow.setPaddingLeft(1.0f);
            topline_img.addCell(emptyRow);
            emptyRow.setPaddingTop(1.0f);
            emptyRow.setPaddingBottom(0.0f);
            doc.add((Element)topline_img);
            doc.add((Element)table_spaces);
            final PdfPTable table_title = new PdfPTable(2);
            table_title.setWidthPercentage(100.0f);
            final Phrase ph_title = new Phrase();
            ph_title.add((Object)new Chunk(title + "\n\n", PDFUtil.black_text_heading));
            final PdfPCell dataCell = new PdfPCell(ph_title);
            dataCell.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
            dataCell.setBorder(0);
            table_title.addCell(dataCell);
            table_title.setHorizontalAlignment(0);
            final Date generateDate = new Date();
            final long dateValue = generateDate.getTime();
            final String formattedDate = SYMClientUtil.getExportTimeString(dateValue);
            final Phrase ph = new Phrase();
            final String timeZoneMsg = getTimeZoneString();
            ph.add((Object)new Chunk(I18N.getMsg("dc.cfg.colln.rep.GENERATED_ON", new Object[0]) + " - " + formattedDate + timeZoneMsg, PDFUtil.black_text_small));
            final PdfPCell t = new PdfPCell(ph);
            t.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
            t.setHorizontalAlignment(2);
            t.setBorder(0);
            table_title.addCell(t);
            doc.add((Element)table_title);
            final PdfPTable table_desc = new PdfPTable(1);
            table_desc.setWidthPercentage(100.0f);
            final Phrase ph_desc = new Phrase();
            ph_desc.add((Object)new Chunk(desc + "\n\n", PDFUtil.black_text_small));
            final PdfPCell descCell = new PdfPCell(ph_desc);
            descCell.setBorder(0);
            table_desc.addCell(descCell);
            table_desc.setHorizontalAlignment(0);
            doc.add((Element)table_desc);
            new MDMPDFCustomizationUtil().customizeMDMReport(doc, toolID, request);
            if (toolID != null && !toolID.equals("")) {
                final int reportID = Integer.parseInt(toolID);
                if ((reportID >= 5000 || reportID <= 1099) && filterParam != null && !filterParam.equals("") && !filterParam.equals(" - ")) {
                    final PdfPTable table_filterParam = new PdfPTable(1);
                    table_filterParam.setWidthPercentage(100.0f);
                    final Phrase ph_filterParam = new Phrase();
                    ph_filterParam.add((Object)new Chunk(I18N.getMsg("dc.rep.pdf.filt_chosen", new Object[0]) + " : ", PDFUtil.black_text_medium));
                    ph_filterParam.add((Object)new Chunk(filterParam + "\n\n", PDFUtil.black_text_small));
                    final PdfPCell filterParamCell = new PdfPCell(ph_filterParam);
                    filterParamCell.setBorder(0);
                    table_filterParam.addCell(filterParamCell);
                    table_filterParam.setHorizontalAlignment(0);
                    doc.add((Element)table_filterParam);
                }
            }
            final Phrase footerPhrase = new Phrase();
            footerPhrase.add((Object)new Chunk(I18N.getMsg("mdm.rep.pdf.page", new Object[0]), PDFUtil.black_text_medium));
            final HeaderFooter footer = new HeaderFooter(footerPhrase, new Phrase(""));
            footer.setAlignment(1);
            footer.setBorderColor(Color.white);
            footer.getBefore().setFont(PDFUtil.black_text_small);
            doc.setFooter(footer);
        }
        catch (final DocumentException ex) {
            PDFUtil.logger.log(Level.WARNING, "DocumentException while setting the header and footer", (Throwable)ex);
        }
        catch (final MalformedURLException ex2) {
            PDFUtil.logger.log(Level.WARNING, "MalformedURLException while setting the header and footer", ex2);
        }
        catch (final IOException ex3) {
            PDFUtil.logger.log(Level.WARNING, "IOException while setting the header and footer", ex3);
        }
        catch (final SyMException ex4) {
            PDFUtil.logger.log(Level.WARNING, "SyMException while setting the header and footer", (Throwable)ex4);
        }
        catch (final Exception ex5) {
            PDFUtil.logger.log(Level.WARNING, "Exception while setting the header and footer", ex5);
        }
    }
    
    public static void setReportData(final String toolID, final Document document, final Long resourceID, final HttpServletRequest request) {
        PDFUtil.logger.log(Level.WARNING, "In setReportData method..,", toolID);
    }
    
    public static void setReportData(final String toolID, final Document document, final Long taskId) {
        PDFUtil.logger.log(Level.WARNING, "In setReportData method..,", toolID);
    }
    
    public static void setReportData(final String toolID, final Document document, final Long resourceID, final String selectedTab, final HttpServletRequest request) {
        PDFUtil.logger.log(Level.WARNING, "In setReportData method..,", toolID);
    }
    
    private static void constructHeaderCell(final String tableHeading, final PdfPTable pTable) {
        final PdfPCell cell = new PdfPCell((Phrase)new Paragraph(tableHeading, PDFUtil.tab_title));
        cell.setBackgroundColor(PDFUtil.rowheadercolor);
        cell.setColspan(4);
        cell.setBorderColor(Color.white);
        pTable.addCell(cell);
    }
    
    private static void constructDataCell(final String cellContent, final Font font, final Color bgColor, final int horizAlign, final int leftBorder, final int rightBorder, final int colSpan, final PdfPTable pTable) {
        final PdfPCell cell = new PdfPCell(new Phrase(cellContent, font));
        if (horizAlign != -1) {
            cell.setHorizontalAlignment(horizAlign);
        }
        if (leftBorder != -1) {
            cell.setBorderWidthLeft((float)leftBorder);
        }
        if (rightBorder != -1) {
            cell.setBorderWidthRight((float)rightBorder);
        }
        if (bgColor != null) {
            cell.setBackgroundColor(bgColor);
        }
        if (colSpan != -1) {
            cell.setColspan(colSpan);
        }
        cell.setBorderColor(Color.white);
        pTable.addCell(cell);
    }
    
    private static void constructGPOInheritanceTable(final List list, final PdfPTable pTable, final String tableHeading) {
        try {
            final PdfPCell cell = new PdfPCell((Phrase)new Paragraph(tableHeading, PDFUtil.tab_title));
            cell.setBackgroundColor(PDFUtil.rowheadercolor);
            cell.setColspan(5);
            cell.setBorderColor(Color.white);
            pTable.addCell(cell);
            final Hashtable tempHash = list.get(0);
            final List valueList = tempHash.get("InheritedGPOs");
            if (valueList != null && valueList.size() > 0) {
                constructDataCell(I18N.getMsg("dc.rep.pdf.gpo_name", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, 0, -1, pTable);
                constructDataCell(I18N.getMsg("dc.common.STATUS", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, -1, -1, pTable);
                constructDataCell(I18N.getMsg("dc.common.LOCATION", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, -1, -1, pTable);
                constructDataCell(I18N.getMsg("xml.report.Enforced", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, 0, -1, pTable);
                constructDataCell(I18N.getMsg("xml.report.Link_Enabled", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, -1, -1, pTable);
            }
            for (int i = 0; i < valueList.size(); ++i) {
                final Hashtable hash = valueList.get(i);
                constructDataCell(hash.get("DISPLAY_NAME").toString(), PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, 0, -1, pTable);
                constructDataCell(hash.get("GPO_STATUS").toString(), PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, -1, -1, pTable);
                constructDataCell(hash.get("CONTAINER_NAME").toString(), PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, -1, -1, pTable);
                constructDataCell(hash.get("IS_ENABLED").toString(), PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, -1, -1, pTable);
                constructDataCell(hash.get("NO_OVERRIDE").toString(), PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, -1, -1, pTable);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void constructLinkedGPOTable(final List list, final PdfPTable pTable, final String tableHeading) {
        try {
            setFonts();
            constructHeaderCell(tableHeading, pTable);
            final Hashtable tempHash = list.get(0);
            final List valueList = tempHash.get("DirectGPOs");
            if (valueList != null && valueList.size() > 0) {
                constructDataCell(I18N.getMsg("dc.rep.pdf.gpo_name", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, 0, -1, pTable);
                constructDataCell(I18N.getMsg("dc.common.STATUS", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, -1, -1, pTable);
                constructDataCell(I18N.getMsg("xml.report.Enforced", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, 0, -1, pTable);
                constructDataCell(I18N.getMsg("xml.report.Link_Enabled", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, -1, -1, pTable);
            }
            for (int i = 0; i < valueList.size(); ++i) {
                final Hashtable hash = valueList.get(i);
                constructDataCell(hash.get("DISPLAY_NAME").toString(), PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, 0, -1, pTable);
                constructDataCell(hash.get("GPO_STATUS").toString(), PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, -1, -1, pTable);
                constructDataCell(hash.get("IS_ENABLED").toString(), PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, -1, -1, pTable);
                constructDataCell(hash.get("NO_OVERRIDE").toString(), PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, -1, -1, pTable);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void constructContainersOfGPOTable(final List list, final PdfPTable pTable, final String tableHeading) {
        try {
            setFonts();
            constructHeaderCell(tableHeading, pTable);
            final Hashtable tempHash = list.get(0);
            final List valueList = tempHash.get("Containers");
            if (valueList != null && valueList.size() > 0) {
                constructDataCell(I18N.getMsg("dc.rep.pdf.container_name", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, 0, 2, pTable);
                constructDataCell(I18N.getMsg("dc.common.TYPE", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, -1, 2, pTable);
            }
            for (int i = 0; i < valueList.size(); ++i) {
                final Hashtable hash = valueList.get(i);
                final String name = hash.get("NAME");
                final Object type = hash.get("RESOURCE_TYPE");
                final String typeStr = null;
                constructDataCell(name, PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, 0, 2, pTable);
                constructDataCell(typeStr, PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, -1, 2, pTable);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void constructSubnetsOfSiteTable(final List list, final PdfPTable pTable, final String tableHeading) {
        try {
            constructHeaderCell(tableHeading, pTable);
            final Hashtable tempHash = list.get(0);
            final List valueList = tempHash.get("Subnets");
            for (int i = 0; i < valueList.size(); ++i) {
                final Hashtable subnetHash = valueList.get(i);
                Object obj = subnetHash.get("SUBNET");
                constructDataCell(I18N.getMsg("dc.common.SUBNET", new Object[0]) + " : " + obj, PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, 0, -1, pTable);
                obj = subnetHash.get("SUBNET_MASK");
                constructDataCell(I18N.getMsg("dc.rep.pdf.netmask", new Object[0]) + " : " + obj, PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, 0, 0, -1, pTable);
                obj = subnetHash.get("SUBNET_MEMBER_COUNT");
                constructDataCell(I18N.getMsg("dc.rep.pdf.no_of_comp", new Object[0]) + " : " + obj, PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, 0, 0, -1, pTable);
                obj = subnetHash.get("SUBNET_TYPE");
                constructDataCell(I18N.getMsg("desktopcentral.admin.som.addDomain.Network_Type", new Object[0]) + " : " + obj, PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, 0, -1, -1, pTable);
                final List computerList = subnetHash.get("SUBNET_MEMBER");
                if (computerList != null && computerList.size() > 0) {
                    constructDataCell(I18N.getMsg("dc.common.COMP_NAME", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, 0, 2, pTable);
                    constructDataCell(I18N.getMsg("dc.common.IP_ADDRESS", new Object[0]), PDFUtil.black_text_small, PDFUtil.oddrowcolor, 0, -1, -1, 2, pTable);
                    for (int j = 0; j < computerList.size(); ++j) {
                        final Hashtable computerHash = computerList.get(j);
                        final String COMPUTER_NAME = computerHash.get("COMPUTER_NAME");
                        final String IP_ADDRESS = computerHash.get("IP_ADDRESS");
                        constructDataCell(COMPUTER_NAME, PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, 0, 2, pTable);
                        constructDataCell(IP_ADDRESS, PDFUtil.black_text_small, PDFUtil.WHITE, 0, -1, -1, 2, pTable);
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void constructPropertyTable(final List list, final PdfPTable pTable, final String tableHeading) {
        constructHeaderCell(tableHeading, pTable);
        for (int i = 0; i < list.size(); ++i) {
            final Hashtable innerHash = list.get(i);
            final Enumeration enumeration1 = innerHash.keys();
            final String key = enumeration1.nextElement();
            final String value = innerHash.get(key).toString();
            Color bgColor = PDFUtil.WHITE;
            if (i % 4 == 0 || i % 4 == 1) {
                bgColor = PDFUtil.oddrowcolor;
            }
            constructDataCell(key, PDFUtil.black_text_medium, bgColor, 0, -1, 0, -1, pTable);
            constructDataCell(value, PDFUtil.black_text_small, bgColor, 0, 0, -1, -1, pTable);
            if (i == list.size() - 1 && i % 2 == 0) {
                bgColor = PDFUtil.WHITE;
                if (i % 4 == 0 || i % 4 == 1) {
                    bgColor = PDFUtil.oddrowcolor;
                }
                constructDataCell("", PDFUtil.black_text_small, bgColor, 0, -1, 0, -1, pTable);
                constructDataCell("", PDFUtil.black_text_small, bgColor, 0, 0, -1, -1, pTable);
            }
        }
    }
    
    private static void constructMembersTable(final List valueList, final PdfPTable pTable, final String tableHeading) {
        constructHeaderCell(tableHeading, pTable);
        for (int i = 0; i < valueList.size(); ++i) {
            final String value = valueList.get(i);
            int leftBorder = -1;
            int rightBorder = -1;
            if (i % 4 < 3) {
                rightBorder = 0;
            }
            if (i % 4 > 0) {
                leftBorder = 0;
            }
            Color bgColor = PDFUtil.WHITE;
            if (i % 8 == 0 || i % 8 == 1 || i % 8 == 2 || i % 8 == 3) {
                bgColor = PDFUtil.oddrowcolor;
            }
            constructDataCell(value, PDFUtil.black_text_small, bgColor, 0, leftBorder, rightBorder, -1, pTable);
            if (i == valueList.size() - 1 && i % 4 < 3) {
                for (int j = i % 4 + 1; j < 4; ++j) {
                    bgColor = PDFUtil.WHITE;
                    if (j % 8 == 0 || j % 8 == 1 || j % 8 == 2 || j % 8 == 3) {
                        bgColor = PDFUtil.oddrowcolor;
                    }
                    leftBorder = -1;
                    rightBorder = -1;
                    if (j != 3) {
                        leftBorder = 0;
                        rightBorder = 0;
                    }
                    if (j == 3) {
                        leftBorder = 0;
                    }
                    constructDataCell("", PDFUtil.black_text_small, bgColor, 0, leftBorder, rightBorder, -1, pTable);
                }
            }
        }
    }
    
    public static void setEndOfDocument(final Document doc, final HttpServletRequest request) {
        try {
            final String conf = getCopyrightString();
            final PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(0.0f);
            final PdfPCell dataCell = new PdfPCell(new Phrase(conf, PDFUtil.copyright_text));
            dataCell.setPaddingTop(10.0f);
            dataCell.setBackgroundColor(PDFUtil.WHITE);
            dataCell.setBorder(0);
            dataCell.setVerticalAlignment(6);
            dataCell.setHorizontalAlignment(1);
            table.addCell(dataCell);
            doc.add((Element)table);
        }
        catch (final Exception ex) {
            PDFUtil.logger.log(Level.WARNING, "Exception while setting the end of doc..", ex);
        }
    }
    
    public static void setSharesData(final Document document, final Long resourceID, final List conditions) {
        PDFUtil.logger.log(Level.INFO, "In setSharesData method..,");
    }
    
    public static void setCustomGroupMemberDetails(final Properties customGroupMemberProp, final Document doc) {
        try {
            final PdfPTable pTable = new PdfPTable(1);
            final PdfPTable innerTable1 = new PdfPTable(2);
            PdfPCell cell = new PdfPCell((Phrase)new Paragraph(I18N.getMsg("dc.admin.cg.CG", new Object[0]), PDFUtil.white_text_small));
            cell.setBackgroundColor(PDFUtil.rowheadercolor);
            cell.setBorderColor(PDFUtil.rowheadercolor);
            cell.setColspan(2);
            innerTable1.addCell(cell);
            final float[] innerWidths = { 30.0f, 70.0f };
            innerTable1.setWidths(innerWidths);
            cell = new PdfPCell((Phrase)new Paragraph(I18N.getMsg("dc.rep.pdf.cg_name", new Object[0]), PDFUtil.black_text_small));
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            innerTable1.addCell(cell);
            cell = new PdfPCell((Phrase)new Paragraph("" + customGroupMemberProp.getProperty("groupName"), PDFUtil.black_text_small));
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            innerTable1.addCell(cell);
            cell = new PdfPCell((Phrase)new Paragraph(I18N.getMsg("dc.common.DOMAIN_NAME", new Object[0]), PDFUtil.black_text_small));
            cell.setBackgroundColor(PDFUtil.oddrowcolor);
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            innerTable1.addCell(cell);
            cell = new PdfPCell((Phrase)new Paragraph("" + customGroupMemberProp.getProperty("domainNameCG"), PDFUtil.black_text_small));
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            cell.setBackgroundColor(PDFUtil.oddrowcolor);
            innerTable1.addCell(cell);
            cell = new PdfPCell((Phrase)new Paragraph(I18N.getMsg("dc.common.CREATED_TIME", new Object[0]), PDFUtil.black_text_small));
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            innerTable1.addCell(cell);
            cell = new PdfPCell((Phrase)new Paragraph("" + customGroupMemberProp.getProperty("createdTime"), PDFUtil.black_text_small));
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            innerTable1.addCell(cell);
            cell = new PdfPCell((Phrase)new Paragraph(I18N.getMsg("dc.common.LAST_MODIFIED_TIME", new Object[0]), PDFUtil.black_text_small));
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            cell.setBackgroundColor(PDFUtil.oddrowcolor);
            innerTable1.addCell(cell);
            cell = new PdfPCell((Phrase)new Paragraph("" + customGroupMemberProp.getProperty("modifiedTime"), PDFUtil.black_text_small));
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            cell.setBackgroundColor(PDFUtil.oddrowcolor);
            innerTable1.addCell(cell);
            cell = new PdfPCell((Phrase)new Paragraph(I18N.getMsg("desktopcentral.admin.customGroup.members_count", new Object[0]), PDFUtil.black_text_small));
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            innerTable1.addCell(cell);
            cell = new PdfPCell((Phrase)new Paragraph("" + customGroupMemberProp.getProperty("memberCount"), PDFUtil.black_text_small));
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
            innerTable1.addCell(cell);
            innerTable1.setHorizontalAlignment(0);
            pTable.addCell(innerTable1);
            pTable.setWidthPercentage(100.0f);
            pTable.setSpacingAfter(10.0f);
            doc.add((Element)pTable);
        }
        catch (final DocumentException ex) {
            PDFUtil.logger.log(Level.SEVERE, "Custom Group Member Details Export PDF : Exception occured while adding Custom Group Member Details", (Throwable)ex);
        }
        catch (final Exception ex2) {
            PDFUtil.logger.log(Level.SEVERE, "Custom Group Member Details Export PDF : Exception occured while adding Custom Group Member Details", ex2);
        }
    }
    
    public static void checkAndAddViewFilterDetails(final ViewContext viewContext, final Document document) {
        final HttpServletRequest request = viewContext.getRequest();
        String criteriaJSONString = request.getParameter("criteriaJSON");
        final String isDCViewFilterReset = request.getParameter("isDCViewFilterReset");
        criteriaJSONString = (String)((criteriaJSONString == null && isDCViewFilterReset == null) ? viewContext.getStateParameter("criteriaJSON") : criteriaJSONString);
        if (criteriaJSONString != null) {
            try {
                criteriaJSONString = URLDecoder.decode(criteriaJSONString, "UTF-8");
                final DCViewFilter dcViewFilter = DCViewFilter.dcViewFilterMapper(criteriaJSONString);
                if (dcViewFilter != null) {
                    if (viewContext.getUniqueId().equalsIgnoreCase("DeviceLocationHistoryList")) {
                        setViewFilterCriteriaDetails(viewContext, dcViewFilter, document, true);
                    }
                    else {
                        setViewFilterCriteriaDetails(viewContext, dcViewFilter, document, false);
                    }
                }
            }
            catch (final Exception ee) {
                ee.printStackTrace();
            }
        }
    }
    
    private static PdfPCell createFilterHeaderNewCell(final String text, final boolean isNew) {
        final PdfPCell cell = new PdfPCell((Phrase)new Paragraph(text, PDFUtil.white_text_small));
        if (isNew) {
            cell.setBackgroundColor(PDFUtil.new_table_header_color);
            cell.setMinimumHeight(20.0f);
            cell.setBorder(0);
        }
        else {
            cell.setBackgroundColor(PDFUtil.rowheadercolor);
            cell.setBorderColor(PDFUtil.rowheadercolor);
        }
        cell.setVerticalAlignment(5);
        return cell;
    }
    
    private static PdfPCell createFilterDetailsNewCell(final String text, final Color rowColor, final boolean isNew) {
        final PdfPCell cell = new PdfPCell((Phrase)new Paragraph(text, PDFUtil.black_text_small));
        cell.setBackgroundColor(rowColor);
        if (isNew) {
            cell.setBorder(0);
            cell.setMinimumHeight(20.0f);
        }
        else {
            cell.setBorderColor(PDFUtil.BLUE_BORDER);
        }
        cell.setVerticalAlignment(5);
        return cell;
    }
    
    private static PdfPTable emptySpace() {
        final PdfPTable table_spaces = new PdfPTable(1);
        table_spaces.setWidthPercentage(100.0f);
        final PdfPCell emptycells = new PdfPCell(new Phrase(""));
        emptycells.setBackgroundColor(Color.white);
        emptycells.setMinimumHeight(20.0f);
        emptycells.setBorder(0);
        emptycells.setPaddingTop(1.0f);
        emptycells.setPaddingBottom(1.0f);
        emptycells.setPaddingRight(1.0f);
        emptycells.setPaddingLeft(1.0f);
        table_spaces.addCell(emptycells);
        return table_spaces;
    }
    
    public static void setViewFilterCriteriaDetails(final ViewContext viewContext, final DCViewFilter dcViewFilter, final Document document, final boolean isNew) {
        try {
            final List<DCViewFilterCriteria> dcViewFilterCriteriaList = dcViewFilter.getDcViewFilterCriteriaList();
            final PdfPTable pTable = new PdfPTable(1);
            final PdfPTable innerTable1 = new PdfPTable(4);
            Color rowColour = PDFUtil.ODD_ROW_BLUE;
            innerTable1.setWidthPercentage(100.0f);
            for (int i = 0; i < dcViewFilterCriteriaList.size(); ++i) {
                if (i % 2 == 0) {
                    if (isNew) {
                        rowColour = PDFUtil.new_even_row_color;
                    }
                    else {
                        rowColour = PDFUtil.EVEN_ROW_BLUE;
                    }
                }
                else if (isNew) {
                    rowColour = PDFUtil.new_odd_row_color;
                }
                else {
                    rowColour = PDFUtil.ODD_ROW_BLUE;
                }
                final DCViewFilterCriteria dcViewFilterCriteria = dcViewFilterCriteriaList.get(i);
                final Long columnID = dcViewFilterCriteria.getColumnID();
                final String comparator = dcViewFilterCriteria.getComparator();
                final List searchValue = dcViewFilterCriteria.getSearchValue();
                final String logicalOperator = dcViewFilterCriteria.getLogicalOperator();
                String value = "";
                String searchValue2 = null;
                if (searchValue != null && !searchValue.isEmpty()) {
                    value = searchValue.get(0);
                    if (dcViewFilterCriteria.getComparator().equalsIgnoreCase("between")) {
                        searchValue2 = searchValue.get(1);
                    }
                    else {
                        value = (String)searchValue.stream().collect(Collectors.joining(","));
                    }
                    if (searchValue2 != null && !searchValue2.equalsIgnoreCase("null") && !searchValue2.equalsIgnoreCase("")) {
                        value = value + "," + searchValue2;
                    }
                }
                String columnName = "";
                String i18nColumnName = "";
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
                selectQuery.addSelectColumn(new Column("CRColumns", "COLUMN_ID"));
                selectQuery.addSelectColumn(new Column("CRColumns", "DISPLAY_NAME"));
                final Criteria columnCriteria = new Criteria(new Column("CRColumns", "COLUMN_ID"), (Object)columnID, 0);
                selectQuery.setCriteria(columnCriteria);
                final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Row columnRow = dataObject.getFirstRow("CRColumns");
                    columnName = (String)columnRow.get("DISPLAY_NAME");
                    columnName = I18N.getMsg(columnName, new Object[0]);
                    i18nColumnName = (String)columnRow.get("DISPLAY_NAME");
                }
                final LinkedHashMap transformedValueMap = CriteriaColumnValueUtil.getInstance().getTranformValueList(columnID, (List)null);
                if (!transformedValueMap.isEmpty()) {
                    if (value.contains(",")) {
                        final String[] values = value.split(Pattern.quote(","));
                        final StringBuilder valueBuilder = new StringBuilder();
                        for (int j = 0; j < values.length; ++j) {
                            valueBuilder.append(transformedValueMap.get(values[j]));
                            if (j != values.length - 1) {
                                valueBuilder.append(",");
                            }
                        }
                        value = valueBuilder.toString();
                    }
                    else {
                        value = transformedValueMap.get(value) + "";
                    }
                }
                else if (i18nColumnName.equalsIgnoreCase("dc.rep.reportIntroTable.group")) {
                    try {
                        final Long viewID = new Long(viewContext.getRequest().getParameter("viewId"));
                        final Map<String, Object> filterMap = new HashMap<String, Object>();
                        filterMap.put("offset", 0);
                        filterMap.put("limit", 0);
                        filterMap.put("filter", "");
                        filterMap.put("customSearchValues", searchValue);
                        final List columnValues = ReportCriteriaUtil.getInstance().getColumnValues(columnID, viewID, (Map)filterMap, (Long)null);
                        if (columnValues != null && !columnValues.isEmpty()) {
                            final HashMap<String, String> displayValues = new HashMap<String, String>();
                            for (int k = 0; k < columnValues.size(); ++k) {
                                final Map valuesMap = columnValues.get(k);
                                displayValues.put(valuesMap.get("searchValue").toString(), valuesMap.get("displayValue").toString());
                            }
                            final StringBuilder valueBuilder2 = new StringBuilder();
                            for (int l = 0; l < searchValue.size(); ++l) {
                                valueBuilder2.append(displayValues.get(searchValue.get(l).toString()));
                                if (l != searchValue.size() - 1) {
                                    valueBuilder2.append(",");
                                }
                            }
                            value = valueBuilder2.toString();
                        }
                    }
                    catch (final Exception ex) {
                        PDFUtil.logger.log(Level.WARNING, "Exception while setting filter rows : ", ex);
                    }
                }
                if (i == 0) {
                    PdfPCell cell = new PdfPCell((Phrase)new Paragraph(I18N.getMsg("dc.common.viewFilter.Filter_details", new Object[0]), PDFUtil.black_text_heading));
                    if (isNew) {
                        cell.setBackgroundColor(PDFUtil.WHITE);
                        cell.setBorder(0);
                        cell.setMinimumHeight(25.0f);
                        cell.setVerticalAlignment(5);
                    }
                    else {
                        cell.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
                        cell.setBorderColor(PDFUtil.WHITE);
                    }
                    cell.setColspan(4);
                    innerTable1.addCell(cell);
                    final float[] innerWidths = { 10.0f, 30.0f, 10.0f, 50.0f };
                    innerTable1.setWidths(innerWidths);
                    cell = createFilterHeaderNewCell(I18N.getMsg("dc.common.viewFilter.Filter_Logical_op", new Object[0]), isNew);
                    innerTable1.addCell(cell);
                    cell = createFilterHeaderNewCell(I18N.getMsg("dc.common.viewFilter.Filter_Column", new Object[0]), isNew);
                    innerTable1.addCell(cell);
                    cell = createFilterHeaderNewCell(I18N.getMsg("dc.common.viewFilter.Filter_Criteria", new Object[0]), isNew);
                    innerTable1.addCell(cell);
                    cell = createFilterHeaderNewCell(I18N.getMsg("dc.common.viewFilter.Filter_Value", new Object[0]), isNew);
                    innerTable1.addCell(cell);
                }
                if (i != 0) {
                    final PdfPCell cell = createFilterDetailsNewCell(logicalOperator, rowColour, isNew);
                    innerTable1.addCell(cell);
                }
                else {
                    final PdfPCell cell = createFilterDetailsNewCell("", rowColour, isNew);
                    innerTable1.addCell(cell);
                }
                PdfPCell cell = createFilterDetailsNewCell(columnName, rowColour, isNew);
                innerTable1.addCell(cell);
                cell = createFilterDetailsNewCell(comparator, rowColour, isNew);
                innerTable1.addCell(cell);
                cell = createFilterDetailsNewCell(value, rowColour, isNew);
                innerTable1.addCell(cell);
            }
            innerTable1.setHorizontalAlignment(0);
            if (isNew) {
                document.add((Element)innerTable1);
                document.add((Element)emptySpace());
            }
            else {
                pTable.addCell(innerTable1);
                pTable.setWidthPercentage(100.0f);
                pTable.setSpacingAfter(10.0f);
                document.add((Element)pTable);
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
    }
    
    public static Hashtable setTitleAndDescription(final String toolID, final HttpServletRequest request) {
        final Hashtable hash = new Hashtable();
        return hash;
    }
    
    public static Hashtable getTitleAndDescription(final String toolID, final HttpServletRequest request) {
        final Hashtable hash = new Hashtable();
        return hash;
    }
    
    public static void setFonts() {
        final BaseFont bf = getBaseFontForLocale();
        PDFUtil.black_text_bold = new Font(bf, 8.0f, 1, Color.BLACK);
        PDFUtil.black_text_small = new Font(bf, 8.0f, 0, Color.BLACK);
        PDFUtil.black_text_medium = new Font(bf, 9.0f, 0, Color.BLACK);
        PDFUtil.black_text_heading = new Font(bf, 10.0f, 0, Color.BLACK);
        PDFUtil.black_text_bold = new Font(bf, 8.0f, 1, Color.BLACK);
        PDFUtil.copyright_text = new Font(bf, 8.0f, 0, new Color(10790052));
        PDFUtil.white_text_small = new Font(bf, 8.0f, 0, Color.WHITE);
        PDFUtil.tab_title = new Font(bf, 10.0f, 0, Color.WHITE);
    }
    
    public static Color getNextColor(Color currentColor) {
        if (currentColor == PDFUtil.WHITE) {
            currentColor = PDFUtil.oddrowcolor;
        }
        else {
            currentColor = PDFUtil.WHITE;
        }
        return currentColor;
    }
    
    public static String getTimeZoneString() {
        final String timeZoneStr = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID();
        return " ( " + timeZoneStr + " )";
    }
    
    static {
        PDFUtil.logger = Logger.getLogger(PDFUtil.class.getName());
        PDFUtil.new_table_header_color = new Color(7637139);
        PDFUtil.new_odd_row_color = new Color(15921906);
        PDFUtil.new_even_row_color = new Color(16316664);
    }
}
