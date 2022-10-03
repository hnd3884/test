package com.me.ems.framework.reports.core.pdf;

import java.util.Hashtable;
import com.lowagie.text.FontFactory;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Locale;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import com.lowagie.text.pdf.BaseFont;
import java.util.logging.Level;
import com.lowagie.text.pdf.PdfPCell;
import com.adventnet.i18n.I18N;
import com.lowagie.text.Phrase;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.Chunk;
import java.awt.Color;
import com.lowagie.text.Font;
import java.util.logging.Logger;

public class PDFUtil
{
    public static Logger out;
    public static Font black_text_small;
    public static Font copyright_text;
    public static Font black_text_medium;
    public static Font black_text_heading;
    public static Font black_text_bold;
    public static Font white_text_small;
    public static Font tab_title;
    public static final Color GREEN_STRIP;
    public static final Color BLUE_STRIP;
    public static final Color SDP_BLUE_STRIP;
    public static final Color ROW_HEADER_GREEN;
    public static final Color ROW_HEADER_BLUE;
    public static final Color ROW_HEADER_SDP_BLUE;
    public static final Color ODD_ROW_GREEN;
    public static final Color ODD_ROW_BLUE;
    public static final Color ODD_ROW_SDP_BLUE;
    public static final Color TAB_HEADER_BLUE;
    public static final Color WHITE;
    public static final Color BLUE_BORDER;
    public static Color stripcolor;
    public static Color oddrowcolor;
    public static Color rowheadercolor;
    public static Chunk chunk;
    public static final Color EVEN_ROW_BLUE;
    public static final Color BLUE_BACKGROUND;
    public static final Color DARK_BLUE;
    
    public static PdfPTable addCustomerNameToPDFReport(final PdfPTable table) {
        try {
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            if (isMSP) {
                final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
                if (customerID != null) {
                    final String customerName = ApiFactoryProvider.getAuthUtilAccessAPI().getCustomerNameFromID(customerID);
                    if (customerName != null && customerName.trim().length() > 0) {
                        final Phrase ph1 = new Phrase();
                        ph1.add((Object)new Chunk(I18N.getMsg("dc.msp.common.CUST_NAME", new Object[0]) + " : " + customerName, PDFUtil.black_text_medium));
                        final PdfPCell t1 = new PdfPCell(ph1);
                        t1.setHorizontalAlignment(2);
                        t1.setVerticalAlignment(6);
                        t1.setBorder(0);
                        table.addCell(t1);
                        return table;
                    }
                }
            }
            final Phrase ph2 = new Phrase();
            ph2.add((Object)new Chunk("", PDFUtil.black_text_medium));
            final PdfPCell t2 = new PdfPCell(ph2);
            t2.setHorizontalAlignment(2);
            t2.setVerticalAlignment(6);
            t2.setBorder(0);
            table.addCell(t2);
        }
        catch (final Exception e) {
            Logger.getLogger(PDFUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return table;
    }
    
    public static BaseFont getBaseFontForLocale() {
        final BaseFont bf = null;
        FileInputStream fileInputStream = null;
        try {
            final String languageName = getLocaleForExport();
            final Properties fontProps = new Properties();
            fileInputStream = new FileInputStream(System.getProperty("server.home") + File.separator + "conf" + File.separator + "fonts.properties");
            fontProps.load(fileInputStream);
            final String font_name = fontProps.getProperty(languageName + ".fontname");
            final String isExternal = fontProps.getProperty(languageName + ".isExternalFont");
            if (font_name == null) {
                return BaseFont.createFont("Helvetica", "Cp1252", false);
            }
            if (Boolean.valueOf(isExternal).equals(true)) {
                final String encoding = (fontProps.getProperty(languageName + ".encoding") != null) ? fontProps.getProperty(languageName + ".encoding") : "";
                final String font_url = fontProps.getProperty(languageName + ".url");
                return BaseFont.createFont(font_url, encoding, false);
            }
            final String encoding = (fontProps.getProperty(languageName + ".encoding") != null) ? fontProps.getProperty(languageName + ".encoding") : "";
            return BaseFont.createFont(font_name, encoding, false);
        }
        catch (final DocumentException e) {
            PDFUtil.out.log(Level.WARNING, "DocumentException occurred while setting fonts based on locale : " + e);
        }
        catch (final IOException e2) {
            PDFUtil.out.log(Level.WARNING, "IOException occurred while setting fonts based on locale : " + e2);
        }
        finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            catch (final IOException e3) {
                PDFUtil.out.log(Level.WARNING, "Exception while closing fileinputstream", e3);
            }
        }
        return bf;
    }
    
    public static String getLocaleForExport() {
        Locale locale = I18NUtil.getLocaleEN();
        try {
            locale = I18NUtil.getUserLocaleFromSession();
        }
        catch (final Exception e) {
            PDFUtil.out.log(Level.WARNING, "Exception occurred while getting Locale from session : " + e);
        }
        return locale.getLanguage();
    }
    
    public static String getCopyrightString() {
        String copyrightStr = "";
        if (CustomerInfoUtil.getInstance().isMSP()) {
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS()) {
                copyrightStr = SyMUtil.getSyMParameter("COPYRIGHT_TEXT");
                copyrightStr = ((copyrightStr == null) ? "" : copyrightStr);
                return copyrightStr;
            }
        }
        final Properties copyrightProps = SyMUtil.getCopyrightProps();
        copyrightStr = "\n(C) Copyright 2005-2012, ZOHO Corp.\n\n";
        if (copyrightProps != null) {
            copyrightStr = "\n" + ((Hashtable<K, Object>)copyrightProps).get("text") + " " + ((Hashtable<K, Object>)copyrightProps).get("copyright_year") + ", " + ((Hashtable<K, Object>)copyrightProps).get("company_name") + "\n\n";
        }
        return copyrightStr;
    }
    
    static {
        PDFUtil.out = Logger.getLogger(PDFUtil.class.getName());
        PDFUtil.black_text_small = FontFactory.getFont("Lato", 8.0f, 0, Color.BLACK);
        PDFUtil.copyright_text = FontFactory.getFont("Lato", 8.0f, 0, new Color(10790052));
        PDFUtil.black_text_medium = FontFactory.getFont("Lato", 9.0f, 0, Color.BLACK);
        PDFUtil.black_text_heading = FontFactory.getFont("Lato", 10.0f, 0, Color.BLACK);
        PDFUtil.black_text_bold = FontFactory.getFont("Lato", 8.0f, 1, Color.BLACK);
        PDFUtil.white_text_small = FontFactory.getFont("Lato", 8.0f, 0, Color.WHITE);
        PDFUtil.tab_title = FontFactory.getFont("Lato", 10.0f, 0, Color.WHITE);
        GREEN_STRIP = new Color(9740932);
        BLUE_STRIP = new Color(686010);
        SDP_BLUE_STRIP = new Color(13158600);
        ROW_HEADER_GREEN = new Color(14345167);
        ROW_HEADER_BLUE = new Color(12506096);
        ROW_HEADER_SDP_BLUE = new Color(7833746);
        ODD_ROW_GREEN = new Color(15331814);
        ODD_ROW_BLUE = new Color(15922682);
        ODD_ROW_SDP_BLUE = new Color(15658734);
        TAB_HEADER_BLUE = new Color(16777215);
        WHITE = new Color(255, 255, 255);
        BLUE_BORDER = new Color(16777215);
        EVEN_ROW_BLUE = new Color(16382457);
        BLUE_BACKGROUND = new Color(8631504);
        DARK_BLUE = new Color(2313353);
    }
}
