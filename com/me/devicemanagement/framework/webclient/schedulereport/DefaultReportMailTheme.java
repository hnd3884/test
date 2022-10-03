package com.me.devicemanagement.framework.webclient.schedulereport;

import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import java.util.logging.Logger;

public class DefaultReportMailTheme
{
    private static Logger out;
    private String schedule_report;
    private String regards;
    private String download_report;
    private String mail_from;
    private String schedulereport_footer;
    private String note;
    private String schedulerContent_template;
    private static final String HTML_START_TAG = "<html>";
    private String download_url_template;
    private static final String BODY_START_TAG = "<body type=\"text/html\">";
    private static final String TABLE_START_TAG = "<table style=\"border: 1.0px solid rgb(204,204,204);\" width=\"500\" cellspacing=\"0\" cellpadding=\"5\" >";
    private static final String TABLE_BODY_TAG = "<tbody>";
    private static final String TABLE_TITLE_ROW = "<tr><th width=\"273\" style=\"text-align: left;background-color: rgb(102,102,102);color: rgb(255,255,255);\">Report Title</th><th width=\"201\" style=\"text-align: left;background-color: rgb(102,102,102);color: rgb(255,255,255);\">Download Link</th></tr>";
    private static final String EVEN_ROW_THEME = "<tr style=\"background-color:rgb(249,249,249);\">";
    private static final String ODD_ROW_THEME = "<tr style=\"background-color:rgb(255,255,255);\">";
    private static final String FILE_NAME_THEME = "<td style=\"font-size: 12.0px;\"><file_name>";
    private static final String CELL_END_TAG = "</td>";
    private static final String ROW_END_TAG = "</tr>";
    private static final String TABLE_END_TAG = "</table>";
    private static final String BODY_END_TAG = "</body>";
    private static final String HTML_END_TAG = "</html>";
    private static final String DOWNLOAD_LINK_TEMPLATE = "<td style=\"font-size: 12.0px;\"><a href=\"<download_link>\"> Download </a>";
    private static final String SPACE = "&nbsp;";
    private static final String FILE_SIZE_TEMPLATE = "<span style=\"color: rgb(153,153,153);\">(<file_size>)</span>;";
    
    public DefaultReportMailTheme() {
        this.schedule_report = "";
        this.regards = "";
        this.download_report = "";
        this.mail_from = "";
        this.schedulereport_footer = "";
        this.note = "";
        this.schedulerContent_template = "<pre><p style=\"font-size:14px;font-family:Lato;color=#000000\"><schedulerContent></p></pre>";
        this.download_url_template = "<br/><br/><p style=\"font-size: 13px;\"><b>" + this.download_report + ":</b></p> \n <urlMesage>";
        try {
            this.schedule_report = I18N.getMsg("dc.rep.scheduleReport.schedule_report", new Object[0]);
            this.regards = I18N.getMsg("dc.rep.scheduleReport.regards", new Object[0]);
            this.download_report = I18N.getMsg("dc.rep.scheduleReport.download_report", new Object[0]);
            this.mail_from = I18N.getMsg("dc.rep.scheduleReport.mail_from", new Object[0]);
            this.note = I18N.getMsg("dc.rep.scheduleReport.mail_note", new Object[0]);
        }
        catch (final Exception e) {
            DefaultReportMailTheme.out.log(Level.INFO, "I18N failed", e);
        }
    }
    
    public String getContent_template() {
        return "<p style=\"font-size: 12px;\">  <prod_name> " + this.schedule_report + "</p><p style=\"font-size: 12px;\"><font size=\"3\"><b><scheduler_name></b></font></p>";
    }
    
    public String getReportname_template() {
        return "<br/><p style=\"font-size: 14px;color:#555555\">" + this.mail_from + " - " + "<B><report_name></B></p>";
    }
    
    public String getSignature_template() {
        return "<p style=\"font-size: 12px;\">" + this.regards + ",<br/>  <prod_name>.</p>";
    }
    
    public String getMailFooter_template(final String productname, final String displayname) throws Exception {
        final String displaynameWithFormat = "<span style=\"color:#0669AC\">" + displayname + "</span>";
        this.schedulereport_footer = I18N.getMsg("dc.rep.scheduleReport.mail_footer", new Object[] { productname, displaynameWithFormat });
        return "<p class=\"footer\" style=\"font-size:14px;color:#555555\"><b>" + this.note + ":</b>" + this.schedulereport_footer + "</p>";
    }
    
    public String getSchedulerContent_template() {
        return this.schedulerContent_template;
    }
    
    public String getStyleTag() {
        return "<Style>.footer a{text-decoration: none;}</Style>";
    }
    
    public String getDownload_url_template() {
        return "<p style=\"font-size:14px;color:#555555\">" + this.download_report + ":</p> \n <urlMesage>";
    }
    
    public String getHtmlStartTag() {
        return "<html>";
    }
    
    public String getBodyStartTag() {
        return "<body type=\"text/html\">";
    }
    
    public String getTableStartTag() {
        return "<table style=\"border: 1.0px solid rgb(204,204,204);\" width=\"500\" cellspacing=\"0\" cellpadding=\"5\" >";
    }
    
    public String getTableBodyTag() {
        return "<tbody>";
    }
    
    public String getTableTitleRow() {
        return "<tr><th width=\"273\" style=\"text-align: left;background-color: rgb(102,102,102);color: rgb(255,255,255);\">Report Title</th><th width=\"201\" style=\"text-align: left;background-color: rgb(102,102,102);color: rgb(255,255,255);\">Download Link</th></tr>";
    }
    
    public String getEvenRowTheme() {
        return "<tr style=\"background-color:rgb(249,249,249);\">";
    }
    
    public String getOddRowTheme() {
        return "<tr style=\"background-color:rgb(255,255,255);\">";
    }
    
    public String getFileNameTheme() {
        return "<td style=\"font-size: 12.0px;\"><file_name>";
    }
    
    public String getCellEndTag() {
        return "</td>";
    }
    
    public String getRowEndTag() {
        return "</tr>";
    }
    
    public String getTableEndTag() {
        return "</table>";
    }
    
    public String getBodyEndTag() {
        return "</body>";
    }
    
    public String getHtmlEndTag() {
        return "</html>";
    }
    
    public String getDownloadLinkTemplate() {
        return "<td style=\"font-size: 12.0px;\"><a href=\"<download_link>\"> Download </a>";
    }
    
    public String getSpace() {
        return "&nbsp;";
    }
    
    public String getFileSizeTemplate() {
        return "<span style=\"color: rgb(153,153,153);\">(<file_size>)</span>;";
    }
    
    static {
        DefaultReportMailTheme.out = Logger.getLogger("DefaultReportMailTheme");
    }
}
