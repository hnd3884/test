package com.adventnet.sym.webclient.reports;

import com.lowagie.text.pdf.BaseFont;
import java.util.Hashtable;
import java.util.regex.Matcher;
import com.lowagie.text.Phrase;
import java.util.regex.Pattern;
import com.lowagie.text.Chunk;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.adventnet.i18n.I18N;
import java.io.File;
import java.util.Properties;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import java.util.logging.Level;
import com.lowagie.text.pdf.PdfWriter;
import javax.servlet.ServletContext;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.server.util.SyMUtil;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.FontSelector;
import java.awt.Color;
import com.lowagie.text.Font;
import java.util.logging.Logger;
import com.me.mdm.webclient.reports.PDFThemeExt;

public class DCPDFTheme implements PDFThemeExt
{
    private static Logger logger;
    private Font f0;
    private Font f1;
    private Font f2;
    private Font f3;
    private Font f4;
    private Font fw;
    private Color greybg;
    private Color thColor;
    private Color white;
    private Color black;
    private Color repbg;
    private Color repborder;
    private Color repborderl;
    private Color reptitlebg;
    private Color repinnertitlebg;
    private Color replabelbg;
    private Color replabeltxt;
    private Font flt;
    private Font flh;
    private String themeDir;
    private Color bluestrip;
    private Color rowheaderblue;
    private Color oddrowblue;
    private Color tabheaderblue;
    private FontSelector selector;
    
    public DCPDFTheme() {
        this.f0 = FontFactory.getFont("Lato", 8.0f, 0, Color.BLACK);
        this.f1 = FontFactory.getFont("Lato", 9.0f, 0, Color.WHITE);
        this.f2 = FontFactory.getFont("Lato", 11.0f, 0, Color.BLACK);
        this.f3 = FontFactory.getFont("Lato", 10.0f, 0, Color.BLACK);
        this.f4 = FontFactory.getFont("Lato", 12.0f, 1, Color.BLACK);
        this.fw = FontFactory.getFont("Lato", 10.0f, 0, Color.RED);
        this.greybg = new Color(245, 245, 245);
        this.thColor = new Color(8822873);
        this.white = new Color(255, 255, 255);
        this.black = new Color(0);
        this.repbg = new Color(6518338);
        this.repborder = new Color(9740932);
        this.repborderl = new Color(9740932);
        this.reptitlebg = new Color(7117268);
        this.repinnertitlebg = new Color(14345167);
        this.replabelbg = new Color(15331814);
        this.replabeltxt = this.white;
        this.flt = FontFactory.getFont("Lato", 11.0f, 1, this.replabeltxt);
        this.flh = FontFactory.getFont("Lato", 14.0f, 1, this.replabeltxt);
        this.themeDir = "themes/dc";
        this.bluestrip = new Color(686010);
        this.rowheaderblue = new Color(12506096);
        this.oddrowblue = new Color(15922682);
        this.tabheaderblue = new Color(15725819);
        this.selector = null;
        final String theme = SyMUtil.getSyMParameter("THEME");
        if ((theme != null && theme.equals("sdp-blue")) || theme.equals("dm-default")) {
            this.replabelbg = PDFUtil.ODD_ROW_SDP_BLUE;
            this.repinnertitlebg = PDFUtil.ROW_HEADER_SDP_BLUE;
        }
        this.setFonts();
    }
    
    public Document getDocument(final ViewContext vc) {
        return new Document();
    }
    
    public void endPDFDoc(final ServletContext sc, final ViewContext vc, final Document doc, final PdfWriter pdfWriter) {
        try {
            PDFUtil.setEndOfDocument(doc, vc.getRequest());
        }
        catch (final Exception e) {
            DCPDFTheme.logger.log(Level.FINEST, "Exception while constructing EndPDFDoc ", e);
        }
    }
    
    public Element renderCellInBox(final ServletContext sc, final ViewContext vc, final Document doc, final PdfWriter pdfWriter, final PdfPCell cell, final String boxType) {
        return (Element)cell;
    }
    
    public PdfPCell renderCellToAddInLayout(final ServletContext sc, final ViewContext vc, final Document doc, final PdfWriter pdfWriter, final PdfPCell cell, final String boxType) {
        final PdfPTable tab = new PdfPTable(1);
        tab.addCell(cell);
        tab.setSpacingAfter(0.0f);
        tab.setSpacingBefore(0.0f);
        tab.setWidthPercentage(100.0f);
        final PdfPCell wrapCell = new PdfPCell(tab);
        wrapCell.setPadding(5.0f);
        wrapCell.setBorderWidth(0.0f);
        wrapCell.setBorder(0);
        return wrapCell;
    }
    
    public void startPDFDoc(final ServletContext sc, final ViewContext viewContext, final Document doc, final PdfWriter pdfWriter) {
        try {
            pdfWriter.setViewerPreferences(32768);
            final HttpServletRequest request = viewContext.getRequest();
            final String toolID = request.getParameter("toolID");
            final String domainName = request.getParameter("domainName");
            final Long userID = 0L;
            final String filterParam = null;
            final Enumeration reqParams = request.getParameterNames();
            if (filterParam == null) {
                int reportID = 0;
                if (toolID != null && !toolID.equals("")) {
                    reportID = Integer.parseInt(toolID);
                }
            }
            DCPDFTheme.logger.log(Level.FINEST, "toolID in generating the PDF document :{0} domainName : {1} filterParam : {2}", new Object[] { toolID, domainName, filterParam });
            if (toolID != null && !toolID.equals("")) {
                final int reportID = Integer.parseInt(toolID);
                PDFUtil.setDocumentHeaderAndFooter(toolID, doc, null, domainName, null, null, request);
            }
            PDFUtil.checkAndAddViewFilterDetails(viewContext, doc);
        }
        catch (final Exception e) {
            DCPDFTheme.logger.log(Level.WARNING, "Exception while constructing StartPDFDoc : {0}", e);
        }
    }
    
    private void addFont(final FontSelector selector, final Properties props, final String language) {
        DCPDFTheme.logger.info("addFont called for language -" + language);
        final String isExternalFont = props.getProperty(language + ".isExternalFont");
        final String font_name = props.getProperty(language + ".fontname");
        if (font_name != null) {
            if (isExternalFont != null && "true".equals(isExternalFont)) {
                final String filepath = props.getProperty(language + ".url");
                final boolean isFileExists = new File(filepath).exists();
                if (isFileExists) {
                    final String encoding = props.getProperty(language + ".encoding");
                    selector.addFont(FontFactory.getFont(props.getProperty(language + ".url"), encoding, false, (float)new Float(8.25)));
                    return;
                }
                DCPDFTheme.logger.severe("Font file doesn't exists on -" + filepath);
            }
            else {
                final String encoding2 = props.getProperty(language + ".encoding");
                selector.addFont(FontFactory.getFont(props.getProperty(language + ".fontname"), encoding2, false, (float)new Float(8.25)));
            }
        }
    }
    
    private void includeFonts() throws IOException {
        if (this.selector == null) {
            this.selector = new FontSelector();
            final Properties fontProps = new Properties();
            try {
                final String userLanguage = I18N.getLocale().getLanguage();
                fontProps.load(new FileInputStream(System.getProperty("server.home") + "/conf/fonts.properties"));
                final String[] languages = fontProps.getProperty("supported_languages").trim().split(",");
                this.addFont(this.selector, fontProps, userLanguage);
                this.selector.addFont(FontFactory.getFont("Lato", "Cp1252", false, (float)new Float(8.25)));
                for (final String language : languages) {
                    if (!userLanguage.equals(language)) {
                        this.addFont(this.selector, fontProps, language);
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
    
    public Element updateThemeAttributes(final ServletContext sc, final ViewContext vc, final Document doc, final PdfWriter pdfWriter, final Element el, final String styleClass) {
        try {
            this.includeFonts();
            this.setFonts();
            if (styleClass.equals("tableHeader")) {
                if (el instanceof Chunk) {
                    final Chunk headerChunk = (Chunk)el;
                    headerChunk.setFont(this.f1);
                }
                else if (el instanceof PdfPCell) {
                    final PdfPCell headerCell = (PdfPCell)el;
                    headerCell.setHorizontalAlignment(0);
                    headerCell.setPadding(2.0f);
                    headerCell.setBorderWidth(0.0f);
                    headerCell.setBackgroundColor(this.repinnertitlebg);
                }
            }
            else if (styleClass.equals("oddRow")) {
                if (el instanceof Chunk) {
                    final Chunk headerChunk = (Chunk)el;
                    headerChunk.setFont(this.f0);
                }
                else if (el instanceof PdfPCell) {
                    final PdfPCell headerCell = (PdfPCell)el;
                    headerCell.setHorizontalAlignment(0);
                    headerCell.setPadding(2.0f);
                    headerCell.setBorderWidth(0.0f);
                    headerCell.setBorder(0);
                    headerCell.setBackgroundColor(this.white);
                }
            }
            else if (styleClass.equals("evenRow")) {
                if (el instanceof Chunk) {
                    final Chunk headerChunk = (Chunk)el;
                    headerChunk.setFont(this.f0);
                }
                else if (el instanceof PdfPCell) {
                    final PdfPCell headerCell = (PdfPCell)el;
                    headerCell.setHorizontalAlignment(0);
                    headerCell.setPadding(2.0f);
                    headerCell.setBorderWidth(0.0f);
                    headerCell.setBorder(0);
                    headerCell.setBackgroundColor(this.replabelbg);
                }
            }
            else if (styleClass.equals("title")) {
                if (el instanceof Chunk) {
                    final Chunk ck = (Chunk)el;
                    ck.setFont(this.flt);
                }
                else if (el instanceof PdfPCell) {
                    final PdfPCell cell = (PdfPCell)el;
                    cell.setBorderWidth(0.0f);
                    cell.setBackgroundColor(this.reptitlebg);
                    cell.setPadding(2.0f);
                }
            }
            else if (styleClass.equals("nodataRow")) {
                if (el instanceof Chunk) {
                    final Chunk headerChunk = (Chunk)el;
                    headerChunk.setFont(this.f0);
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
            if (el instanceof Chunk) {
                String content = ((Chunk)el).getContent();
                try {
                    if (content != null) {
                        final Phrase ph = this.selector.process(content);
                        return (Element)ph;
                    }
                }
                catch (final Exception ex) {
                    final Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]", 194);
                    final Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(content);
                    content = unicodeOutlierMatcher.replaceAll("?");
                    final Phrase ph2 = this.selector.process(content);
                    return (Element)ph2;
                }
            }
            return el;
        }
        catch (final IOException ex2) {
            Logger.getLogger(DCPDFTheme.class.getName()).log(Level.SEVERE, null, ex2);
            return null;
        }
    }
    
    public Font getFont0() {
        return this.f0;
    }
    
    public Font getFont1() {
        return this.f1;
    }
    
    public Font getFont2() {
        return this.f2;
    }
    
    public Font getFont3() {
        return this.f3;
    }
    
    public Font getFont4() {
        return this.f4;
    }
    
    public Font getLabelTxtFont() {
        return this.flt;
    }
    
    public Font getLabelHeaderFont() {
        return this.flh;
    }
    
    public Color getthemeColor() {
        return this.thColor;
    }
    
    public Color getBgColor() {
        return this.repbg;
    }
    
    public Color getBorderColor() {
        return this.repborder;
    }
    
    public Color getBorderLightColor() {
        return this.repborderl;
    }
    
    public Color getLabelTxtColor() {
        return this.replabeltxt;
    }
    
    public Color getLabelBgColor() {
        return this.replabelbg;
    }
    
    public String getThemeDir() {
        return this.themeDir;
    }
    
    public Color getTitleBgColor() {
        return this.reptitlebg;
    }
    
    public Color getInnerTitleBgColor() {
        return this.repinnertitlebg;
    }
    
    private Hashtable getRequestParamHash() {
        final Hashtable paramHash = new Hashtable();
        try {
            paramHash.put("period", I18N.getMsg("desktopcentral.inventory.softwarereports.period", new Object[0]));
            paramHash.put("PERIOD", I18N.getMsg("desktopcentral.inventory.softwarereports.period", new Object[0]));
            paramHash.put("startDate", I18N.getMsg("dc.common.START_DATE", new Object[0]));
            paramHash.put("endDate", I18N.getMsg("dc.admin.logdatenTime.End_Date", new Object[0]));
            paramHash.put("userType", I18N.getMsg("dc.mdm.enroll.user_type", new Object[0]));
            paramHash.put("dialInType", I18N.getMsg("dc.rep.userreport.dial_in_permission", new Object[0]));
            paramHash.put("inActive", I18N.getMsg("dc.rep.pdf.days_inactive", new Object[0]));
            paramHash.put("userEnableDisable", I18N.getMsg("dc.rep.userreport.disabled_user", new Object[0]));
            paramHash.put("expired", I18N.getMsg("dc.db.config.status.Expired", new Object[0]));
            paramHash.put("osName", I18N.getMsg("dc.common.OS_NAME", new Object[0]));
            paramHash.put("OS_NAME", I18N.getMsg("dc.common.OS_NAME", new Object[0]));
            paramHash.put("servicePack", I18N.getMsg("dc.common.SERVICE_PACK", new Object[0]));
            paramHash.put("SERVICE_PACK", I18N.getMsg("dc.common.SERVICE_PACK", new Object[0]));
            paramHash.put("ouName", I18N.getMsg("xml.report.OU_Name", new Object[0]));
            paramHash.put("memberCount", I18N.getMsg("dc.rep.pdf.mem_count", new Object[0]));
            paramHash.put("memberRel", I18N.getMsg("dc.rep.pdf.mem_rel", new Object[0]));
            paramHash.put("type", I18N.getMsg("dc.rep.pdf.logon_off", new Object[0]));
            paramHash.put("configurationViewByUser", I18N.getMsg("dc.common.VIEW", new Object[0]));
            paramHash.put("configurationType", I18N.getMsg("dc.common.TYPE", new Object[0]));
            paramHash.put("configurationStatus", I18N.getMsg("dc.common.STATUS", new Object[0]));
            paramHash.put("eventModule", I18N.getMsg("dc.common.MODULE", new Object[0]));
            paramHash.put("user", I18N.getMsg("dc.common.USER", new Object[0]));
            paramHash.put("days", I18N.getMsg("dc.rep.pdf.no_of_days_back", new Object[0]));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return paramHash;
    }
    
    private void setFonts() {
        final BaseFont bf = PDFUtil.getBaseFontForLocale();
        this.f0 = new Font(bf, 8.0f, 0, Color.BLACK);
        this.f1 = new Font(bf, 9.0f, 0, Color.BLACK);
        this.f2 = new Font(bf, 11.0f, 0, Color.BLACK);
        this.f3 = new Font(bf, 10.0f, 1, Color.BLACK);
        this.f4 = new Font(bf, 12.0f, 1, Color.BLACK);
        this.fw = new Font(bf, 10.0f, 0, Color.RED);
        this.flt = new Font(bf, 11.0f, 1, this.replabeltxt);
        this.flh = new Font(bf, 14.0f, 1, this.replabeltxt);
    }
    
    static {
        DCPDFTheme.logger = Logger.getLogger(DCPDFTheme.class.getName());
    }
}
