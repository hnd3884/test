package com.adventnet.client.components.schedule;

import java.util.Hashtable;
import java.util.ArrayList;
import javax.activation.DataSource;
import javax.mail.Multipart;
import java.util.Properties;
import javax.mail.SendFailedException;
import javax.mail.Transport;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Session;
import com.adventnet.client.components.table.json.TableJSONRenderer;
import com.adventnet.client.components.table.xls.ExportAsExcel;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.components.table.csv.TableCSVRenderer;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.pdf.PDFUtil;
import com.adventnet.client.tpl.TemplateAPI;
import com.adventnet.client.view.web.HttpReqWrapper;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.adventnet.taskengine.TaskExecutionException;
import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import java.net.InetAddress;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;

public class SendReport implements Task
{
    private static Logger logger;
    static final int BUFF_SIZE = 1024;
    static final byte[] BUFFER;
    
    public void executeTask(final TaskContext taskContext) throws TaskExecutionException {
        final long scheduleID = taskContext.getScheduleID();
        final long taskID = taskContext.getTaskID();
        String csvViewName = null;
        try {
            final Persistence persistence = LookUpUtil.getPersistence();
            Column id = new Column("ScheduledReports", "SCH_ID");
            Criteria cri = new Criteria(id, (Object)scheduleID, 0);
            DataObject dobj = persistence.get("ScheduledReports", cri);
            final Row SCHEDULEDREPORTSrow = dobj.getFirstRow("ScheduledReports");
            final String mailContextName = (String)SCHEDULEDREPORTSrow.get("MAIL_CONTEXT_NAME");
            id = new Column("Schedule", "SCHEDULE_ID");
            cri = new Criteria(id, (Object)scheduleID, 0);
            dobj = persistence.get("Schedule", cri);
            final Row SCHEDULErow = dobj.getFirstRow("Schedule");
            final String scheduleName = (String)SCHEDULErow.get("SCHEDULE_NAME");
            final Column viewColumn = new Column("Schedule_View", "SCHEDULE_NAME");
            final Criteria viewcri = new Criteria(viewColumn, (Object)scheduleName, 0);
            final DataObject viewDobj = persistence.get("Schedule_View", viewcri);
            final Iterator it = viewDobj.get("Schedule_View", "VIEWNAME");
            while (it.hasNext()) {
                if (csvViewName == null) {
                    csvViewName = it.next();
                }
                else {
                    csvViewName = csvViewName + "," + it.next();
                }
            }
            final Column id2 = new Column("Schedule", "SCHEDULE_ID");
            Criteria cri2 = new Criteria(id2, SCHEDULEDREPORTSrow.get("SCH_ID"), 0);
            final DataObject dobj2 = persistence.get("Schedule", cri2);
            if (((String)SCHEDULEDREPORTSrow.get(3)).equals("mail")) {
                if (!ScheduledReportAPI.isWorkEnginePresent()) {
                    Row ACMAILCONFIGrow = new Row("ACMailConfig");
                    final Column id3 = new Column("ACMailConfig", "CONTEXT_NAME");
                    Criteria cri3;
                    if (mailContextName.equals("null")) {
                        cri3 = new Criteria(id3, dobj2.getFirstRow("Schedule").get("SCHEDULE_NAME"), 0);
                    }
                    else {
                        cri3 = new Criteria(id3, (Object)mailContextName, 0);
                    }
                    final DataObject dobj3 = persistence.get("ACMailConfig", cri3);
                    ACMAILCONFIGrow = dobj3.getFirstRow("ACMailConfig");
                    try {
                        this.sendMail(scheduleID, taskID, SCHEDULEDREPORTSrow, ACMAILCONFIGrow, scheduleName, null, csvViewName);
                    }
                    catch (final Exception e) {
                        System.out.println("Exception in sending email");
                        e.printStackTrace();
                    }
                }
                else {
                    Row MAIL_SERVERrow = new Row("Mail_Server");
                    final Column id3 = new Column("Mail_Server", "NAME");
                    Criteria cri3;
                    String contextName;
                    if (mailContextName.equals("null")) {
                        cri3 = new Criteria(id3, dobj2.getFirstRow("Schedule").get("SCHEDULE_NAME"), 0);
                        contextName = (String)dobj2.getFirstRow("Schedule").get("SCHEDULE_NAME");
                    }
                    else {
                        cri3 = new Criteria(id3, (Object)mailContextName, 0);
                        contextName = mailContextName;
                    }
                    DataObject dobj4 = persistence.get("Mail_Server", cri3);
                    MAIL_SERVERrow = dobj4.getFirstRow("Mail_Server");
                    final Column col = new Column("ACClientProps", "PARAMNAME");
                    final Criteria crit = new Criteria(col, (Object)(contextName + "_FromAddress"), 0);
                    dobj4 = persistence.get("ACClientProps", crit);
                    final String fromAddress = (String)dobj4.getFirstRow("ACClientProps").get("PARAMVALUE");
                    try {
                        this.sendMail(scheduleID, taskID, SCHEDULEDREPORTSrow, MAIL_SERVERrow, scheduleName, fromAddress, csvViewName);
                    }
                    catch (final Exception e2) {
                        System.out.println("Exception in sending email");
                        e2.printStackTrace();
                    }
                }
            }
            else if (((String)SCHEDULEDREPORTSrow.get("SEND_TYPE")).equals("slsl")) {
                String fromAddress2;
                String proxyHost;
                String proxyUserName;
                String proxyPassword;
                String proxyPort;
                Object[] toAddress;
                if (!ScheduledReportAPI.isWorkEnginePresent()) {
                    Row ACMAILCONFIGrow2 = new Row("ACMailConfig");
                    final Column id4 = new Column("ACMailConfig", "CONTEXT_NAME");
                    Criteria cri4;
                    if (mailContextName.equals("null")) {
                        cri4 = new Criteria(id4, dobj2.getFirstRow("Schedule").get("SCHEDULE_NAME"), 0);
                    }
                    else {
                        cri4 = new Criteria(id4, (Object)mailContextName, 0);
                    }
                    final DataObject dobj5 = persistence.get("ACMailConfig", cri4);
                    ACMAILCONFIGrow2 = dobj5.getFirstRow("ACMailConfig");
                    fromAddress2 = (String)ACMAILCONFIGrow2.get("FROMADDRESS");
                    String toAddr = null;
                    toAddr = (String)SCHEDULEDREPORTSrow.get("TOADDRESS");
                    proxyHost = (String)ACMAILCONFIGrow2.get("SERVER");
                    proxyUserName = (String)ACMAILCONFIGrow2.get("USERNAME");
                    proxyPassword = (String)ACMAILCONFIGrow2.get("PASSWORD");
                    proxyPort = ACMAILCONFIGrow2.get("PORT").toString();
                    toAddress = this.seperateCommas(toAddr);
                }
                else {
                    Row MAIL_SERVERrow2 = new Row("Mail_Server");
                    Row ACCLIENTPROPSrow = new Row("ACClientProps");
                    final Column id5 = new Column("Mail_Server", "NAME");
                    final Column id6 = new Column("ACClientProps", "PARAMNAME");
                    Criteria cri5;
                    if (mailContextName.equals("null")) {
                        cri5 = new Criteria(id5, dobj2.getFirstRow("Schedule").get("SCHEDULE_NAME"), 0);
                        cri2 = new Criteria(id6, (Object)(dobj2.getFirstRow("Schedule").get("SCHEDULE_NAME") + "_FromAddress"), 0);
                    }
                    else {
                        cri5 = new Criteria(id5, (Object)mailContextName, 0);
                        cri2 = new Criteria(id6, (Object)(mailContextName + "_FromAddress"), 0);
                    }
                    final DataObject dobj6 = persistence.get("Mail_Server", cri5);
                    MAIL_SERVERrow2 = dobj6.getFirstRow("Mail_Server");
                    final DataObject dobj7 = persistence.get("ACClientProps", cri2);
                    ACCLIENTPROPSrow = dobj7.getFirstRow("ACClientProps");
                    fromAddress2 = (String)ACCLIENTPROPSrow.get("PARAMVALUE");
                    String toAddr = null;
                    toAddr = (String)SCHEDULEDREPORTSrow.get("TOADDRESS");
                    proxyHost = (String)MAIL_SERVERrow2.get("SERVER_NAME");
                    proxyUserName = (String)MAIL_SERVERrow2.get("USER_NAME");
                    proxyPassword = (String)MAIL_SERVERrow2.get("PASSWORD");
                    proxyPort = MAIL_SERVERrow2.get("PORT").toString();
                    toAddress = this.seperateCommas(toAddr);
                }
                final String fname = this.getFileName(SCHEDULEDREPORTSrow, csvViewName);
                final String nameString = (String)SCHEDULEDREPORTSrow.get("PARAMETER_NAMES");
                final String valueString = (String)SCHEDULEDREPORTSrow.get("PARAMETER_VALUES");
                final String mail_ContextName = (String)SCHEDULEDREPORTSrow.get("MAIL_CONTEXT_NAME");
                final HashMap paramMap = this.getParmeterMap(nameString, valueString);
                final String filePath = this.createFile(csvViewName, fname, (String)SCHEDULEDREPORTSrow.get("FORMAT"), paramMap, scheduleName);
                final int fileSize = ScheduledReportAPI.getFileSize(filePath);
                final int maxSize = (int)SCHEDULEDREPORTSrow.get("MAXFILESIZE");
                String userMessage = (String)SCHEDULEDREPORTSrow.get("MESSAGE");
                final String subject = (String)SCHEDULEDREPORTSrow.get("SUBJECT");
                String reportFolder = "reports";
                if (System.getProperty("reportFolder") != null) {
                    reportFolder = System.getProperty("reportFolder");
                }
                if (maxSize < fileSize) {
                    String serverName = InetAddress.getLocalHost().getHostName();
                    if (System.getProperty("bindaddress") != null) {
                        serverName = System.getProperty("bindaddress");
                    }
                    String link;
                    if (System.getProperty("contextDIR").equals("ROOT")) {
                        link = "http://" + serverName + ":" + System.getProperty("http.port") + "/" + reportFolder + "/" + fname;
                    }
                    else {
                        link = "http://" + serverName + ":" + System.getProperty("http.port") + "/" + System.getProperty("contextDIR") + "/" + reportFolder + "/" + fname;
                    }
                    userMessage = this.checkDynamicElement(userMessage, link);
                    this.sendEmail(fromAddress2, toAddress, subject, userMessage, null, null, proxyHost, proxyPort, proxyUserName, proxyPassword);
                }
                else {
                    this.sendEmail(fromAddress2, toAddress, subject, userMessage, filePath, fname, proxyHost, proxyPort, proxyUserName, proxyPassword);
                }
            }
        }
        catch (final Exception e3) {
            e3.printStackTrace();
        }
    }
    
    private void sendMail(final long scheduleID, final long taskID, final Row srow, final Row arow, final String scheduleName, String fromAddress, final String csvViewName) throws Exception {
        final String viewName = csvViewName;
        final String format = (String)srow.get("FORMAT");
        final String mail_ContextName = (String)srow.get("MAIL_CONTEXT_NAME");
        if (fromAddress == null) {
            fromAddress = (String)arow.get("FROMADDRESS");
        }
        String toAddr = null;
        toAddr = (String)srow.get("TOADDRESS");
        String proxyHost;
        String proxyUserName;
        String proxyPassword;
        String proxyPort;
        if (!ScheduledReportAPI.isWorkEnginePresent()) {
            proxyHost = (String)arow.get("SERVER");
            proxyUserName = (String)arow.get("USERNAME");
            proxyPassword = (String)arow.get("PASSWORD");
            proxyPort = arow.get("PORT").toString();
        }
        else {
            proxyHost = (String)arow.get("SERVER_NAME");
            proxyUserName = (String)arow.get("USER_NAME");
            proxyPassword = (String)arow.get("PASSWORD");
            proxyPort = arow.get("PORT").toString();
        }
        final Object[] toAddress = this.seperateCommas(toAddr);
        final String sendType = (String)srow.get("FORMAT");
        String userMessage = (String)srow.get("MESSAGE");
        final String subject = (String)srow.get("SUBJECT");
        String fname = "";
        if (srow.get("ATTACHMENT_NAME") != null && !((String)srow.get("ATTACHMENT_NAME")).equals("")) {
            fname = (String)srow.get("ATTACHMENT_NAME") + "." + sendType;
            fname = this.checkDynamicElement(fname, "");
        }
        else {
            final SimpleDateFormat df = new SimpleDateFormat("MMM_dd_yyyy  HH_mm_ss");
            fname = viewName + "_" + df.format(new Date()) + "." + sendType;
        }
        final String nameString = (String)srow.get("PARAMETER_NAMES");
        final String valueString = (String)srow.get("PARAMETER_VALUES");
        final HashMap paramMap = this.getParmeterMap(nameString, valueString);
        final String filePath = this.createFile(viewName, fname, format, paramMap, scheduleName);
        if (userMessage == null) {
            userMessage = "";
        }
        else {
            userMessage = this.checkDynamicElement(userMessage, "");
        }
        this.sendEmail(fromAddress, toAddress, subject, userMessage, filePath, fname, proxyHost, proxyPort, proxyUserName, proxyPassword);
    }
    
    private String createFile(final String viewName, final String fname, final String format, final HashMap params, final String scheduleName) throws Exception {
        String reportFolder = "reports";
        if (System.getProperty("reportFolder") != null) {
            reportFolder = System.getProperty("reportFolder");
        }
        final String server_home = System.getProperty("server.dir");
        File FileHome = null;
        if (System.getProperty("contextDIR") != null) {
            FileHome = new File(server_home + "/webapps/" + System.getProperty("contextDIR") + "/" + reportFolder);
        }
        else {
            FileHome = new File(server_home + "/" + reportFolder);
        }
        if (!FileHome.exists()) {
            FileHome.mkdirs();
        }
        final String fileName = FileHome.getAbsolutePath() + "/" + fname;
        final File f = new File(fileName);
        final String filePath = f.toString();
        final FileOutputStream fos = new FileOutputStream(f);
        try {
            final HttpReqWrapper requestWrapper = new HttpReqWrapper("/" + System.getProperty("contextDIR"));
            for (final String key : params.keySet()) {
                if (params.get(key) == null) {
                    params.put(key, "");
                }
                String value = params.get(key);
                if (value.indexOf("${") != -1) {
                    final String handlerString = value.substring(value.indexOf("${") + 2, value.indexOf("}"));
                    final String handlerName = handlerString.substring(0, handlerString.indexOf(":"));
                    final String defaultValue = handlerString.substring(handlerString.indexOf(":") + 1, handlerString.length());
                    value = TemplateAPI.getVariableHandler(handlerName).getVariableValue(key, -1, (Object)new Object[] { viewName, scheduleName });
                    if (value == null) {
                        value = defaultValue;
                    }
                }
                requestWrapper.setParameter(key, value);
            }
            if (format.equals("pdf")) {
                PDFUtil.generatePDF(viewName, (HttpServletRequest)requestWrapper, (OutputStream)fos);
            }
            else if (format.equals("csv")) {
                ((TableCSVRenderer)WebClientUtil.createInstance("com.adventnet.client.components.table.csv.TableCSVRenderer")).generateCSVInOutputStream(viewName, (HttpServletRequest)requestWrapper, fos);
            }
            else if (format.equals("xlsx")) {
                ((ExportAsExcel)WebClientUtil.createInstance("com.adventnet.client.components.table.xls.ExportAsExcel")).generateXLS(viewName, (HttpServletRequest)requestWrapper, fos);
            }
            else {
                if (!format.equals("json")) {
                    throw new Exception("specified format not supported");
                }
                ((TableJSONRenderer)WebClientUtil.createInstance("com.adventnet.client.components.table.json.TableJSONRenderer")).generateJSONInOutputStream(viewName, (HttpServletRequest)requestWrapper, fos);
            }
            fos.close();
        }
        finally {
            if (fos != null) {
                fos.close();
            }
        }
        return filePath;
    }
    
    public void stopTask() throws TaskExecutionException {
    }
    
    public void sendEmail(final String fromAddress, final Object[] toAdd, String subject, final String message, final String attachmentPath, final String attachmentName, final String hostname, final String proxyPort, final String userName, final String password) throws Exception {
        subject = this.checkDynamicElement(subject, "");
        final Properties properties = System.getProperties();
        ((Hashtable<String, String>)properties).put("mail.smtp.host", hostname);
        final Session session = Session.getInstance(properties, (Authenticator)null);
        final Message msg = (Message)new MimeMessage(session);
        final InternetAddress add = new InternetAddress(fromAddress);
        msg.setFrom((Address)add);
        final InternetAddress[] toAddress = new InternetAddress[toAdd.length];
        for (int i = 0; i < toAdd.length; ++i) {
            final String temp = toAdd[i].toString();
            toAddress[i] = new InternetAddress(temp);
        }
        msg.setRecipients(Message.RecipientType.TO, (Address[])toAddress);
        msg.setSubject(subject);
        MimeBodyPart body = new MimeBodyPart();
        body.setText(message);
        final Multipart multipart = (Multipart)new MimeMultipart();
        multipart.addBodyPart((BodyPart)body);
        body = new MimeBodyPart();
        if (attachmentPath != null && !attachmentPath.equals("") && attachmentName != null && !attachmentName.equals("")) {
            final DataSource src = new FileDataSource(attachmentPath);
            final DataHandler handler = new DataHandler(src);
            body.setDataHandler(handler);
            body.setFileName(attachmentName);
            multipart.addBodyPart((BodyPart)body);
        }
        msg.setContent(multipart);
        try {
            Transport.send(msg);
            System.out.println("Mail successfully send to:" + toAddress.toString());
        }
        catch (final SendFailedException e) {
            msg.setRecipients(Message.RecipientType.TO, e.getValidUnsentAddresses());
            Transport.send(msg);
            System.out.println("Mail successfully send to:" + toAddress.toString());
        }
    }
    
    private Object[] seperateCommas(String str) {
        int j = 0;
        final ArrayList arr = new ArrayList();
        for (int i = 0; i < str.length(); ++i) {
            if ((str.charAt(i) + "").equals(",")) {
                final String temp = str.substring(0, i);
                str = str.substring(i + 1, str.length());
                arr.add(temp.trim());
                ++j;
                i = 0;
            }
        }
        arr.add(str);
        final String[] string = new String[0];
        return arr.toArray(string);
    }
    
    private String getFileName(final Row row, final String csvViewName) {
        String fname = "";
        final SimpleDateFormat df = new SimpleDateFormat("MMM_dd_yyyy  HH_mm_ss");
        if (row.get("ATTACHMENT_NAME") != null && !((String)row.get("ATTACHMENT_NAME")).equals("")) {
            fname = (String)row.get("ATTACHMENT_NAME") + "." + (String)row.get("FORMAT");
            fname = this.checkDynamicElement(fname, "");
        }
        else {
            fname = csvViewName + "_" + df.format(new Date()) + "." + (String)row.get("FORMAT");
        }
        return fname;
    }
    
    private HashMap getParmeterMap(final String nameString, final String valueString) throws Exception {
        int i = 0;
        int j = 0;
        final HashMap paramMap = new HashMap(30, 0.75f);
        while (nameString.indexOf(",", i) != -1) {
            if (valueString.indexOf(",", j) == -1) {
                throw new Exception("No of parameters and values given does not match");
            }
            paramMap.put(nameString.substring(i, nameString.indexOf(",", i)), valueString.substring(j, valueString.indexOf(",", j)));
            i = nameString.indexOf(",", i) + 1;
            j = valueString.indexOf(",", j) + 1;
        }
        return paramMap;
    }
    
    private String checkDynamicElement(String fname, final String linkReplace) {
        final SimpleDateFormat date = new SimpleDateFormat("MMM_dd_yyyy");
        final SimpleDateFormat time = new SimpleDateFormat("HH_mm_ss");
        boolean linkReplaced = false;
        boolean exit = false;
        do {
            final int i = 0;
            final int index = fname.indexOf("${", i);
            final int index2 = fname.indexOf("}", index);
            if (index != -1 && index2 != -1) {
                String key = fname.substring(index + 2, index2);
                if (key.equals("date")) {
                    fname = fname.substring(0, index) + date.format(new Date()) + fname.substring(index2 + 1, fname.length());
                }
                else if (key.equals("time")) {
                    fname = fname.substring(0, index) + time.format(new Date()) + fname.substring(index2 + 1, fname.length());
                }
                else if (key.equals("link")) {
                    fname = fname.substring(0, index) + linkReplace + fname.substring(index2 + 1, fname.length());
                    linkReplaced = true;
                }
                else {
                    String param = "";
                    if (key.indexOf(":") != -1) {
                        param = key.substring(key.indexOf(":") + 1, key.length());
                        key = key.substring(0, key.indexOf(":"));
                    }
                    try {
                        key = TemplateAPI.getStringGenerator(key).getString(param);
                    }
                    catch (final Exception e) {
                        key = "";
                    }
                    fname = fname.substring(0, index) + key + fname.substring(index2 + 1, fname.length());
                }
            }
            else {
                exit = true;
            }
        } while (!exit);
        if (!linkReplace.equals("") && !linkReplaced) {
            fname += linkReplace;
        }
        return fname;
    }
    
    static {
        SendReport.logger = Logger.getLogger(SendReport.class.getName());
        BUFFER = new byte[1024];
    }
}
