package com.adventnet.client.components.schedule;

import java.util.Iterator;
import java.sql.Timestamp;
import java.util.HashMap;

public class ScheduledReportDetails
{
    String viewName;
    public HashMap parameters;
    boolean skipMissedSchedules;
    String formatOfReport;
    String typeOfSending;
    int maximumFileSize;
    String host;
    int port;
    String userName;
    String password;
    String toAddress;
    String fromAddress;
    String userMessage;
    String subject;
    String attachmentName;
    String mailContextName;
    String typeOfSchedule;
    long timeOfDay;
    int dayOfWeek;
    long weekOfMonth;
    int dateOfMonth;
    int monthOfYear;
    int yearOfDecade;
    String timeZone;
    int skipFrequency;
    String repeatFrequency;
    String unitOfTime;
    Timestamp startDate;
    Timestamp endDate;
    long timePeriod;
    boolean executeImmediatelyFlag;
    String ftpUserName;
    String ftpPassword;
    String ftpServer;
    String ftpRemoteDirectory;
    String info;
    
    public ScheduledReportDetails() {
        this.parameters = new HashMap(100, 0.75f);
        this.skipMissedSchedules = false;
        this.typeOfSending = "mail";
        this.maximumFileSize = 0;
        this.host = "";
        this.port = -1;
        this.userName = "";
        this.password = "";
        this.mailContextName = "null";
        this.typeOfSchedule = "CALENDAR";
        this.timeOfDay = -1L;
        this.dayOfWeek = -1;
        this.weekOfMonth = -1L;
        this.dateOfMonth = -1;
        this.monthOfYear = -1;
        this.yearOfDecade = -1;
        this.repeatFrequency = "NONE";
        this.unitOfTime = "seconds";
        this.timePeriod = -1L;
        this.executeImmediatelyFlag = false;
        this.ftpRemoteDirectory = "";
    }
    
    public ScheduledReportDetails(final String viewName, final String formatOfReport, final String typeOfSending, final String toAddress, final String fromAddress, final long timeOfDay, final int dayOfWeek, final int weekOfMonth, final int dateOfMonth, final int monthOfYear, final String timeZone, final int skipFrequency, final int yearOfDecade, final String repeatFrequency, final String userMessage, final String host, final int port, final String userName, final String password, final String unitOfTime) {
        this.parameters = new HashMap(100, 0.75f);
        this.skipMissedSchedules = false;
        this.typeOfSending = "mail";
        this.maximumFileSize = 0;
        this.host = "";
        this.port = -1;
        this.userName = "";
        this.password = "";
        this.mailContextName = "null";
        this.typeOfSchedule = "CALENDAR";
        this.timeOfDay = -1L;
        this.dayOfWeek = -1;
        this.weekOfMonth = -1L;
        this.dateOfMonth = -1;
        this.monthOfYear = -1;
        this.yearOfDecade = -1;
        this.repeatFrequency = "NONE";
        this.unitOfTime = "seconds";
        this.timePeriod = -1L;
        this.executeImmediatelyFlag = false;
        this.ftpRemoteDirectory = "";
        this.viewName = viewName;
        this.formatOfReport = formatOfReport;
        this.typeOfSending = typeOfSending;
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
        this.timeOfDay = timeOfDay;
        this.dayOfWeek = dayOfWeek;
        this.weekOfMonth = weekOfMonth;
        this.dateOfMonth = dateOfMonth;
        this.monthOfYear = monthOfYear;
        this.timeZone = timeZone;
        this.skipFrequency = skipFrequency;
        this.yearOfDecade = yearOfDecade;
        this.repeatFrequency = repeatFrequency;
        this.unitOfTime = unitOfTime;
        this.userMessage = userMessage;
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }
    
    public ScheduledReportDetails(final String viewName, final String formatOfReport, final String typeOfSending, final String ftpServer, final String ftpUserName, final String ftpPassword, final String ftpRemoteDirectory, final long timeOfDay, final int dayOfWeek, final int weekOfMonth, final int dateOfMonth, final int monthOfYear, final String repeatFrequency, final String unitOfTime) {
        this.parameters = new HashMap(100, 0.75f);
        this.skipMissedSchedules = false;
        this.typeOfSending = "mail";
        this.maximumFileSize = 0;
        this.host = "";
        this.port = -1;
        this.userName = "";
        this.password = "";
        this.mailContextName = "null";
        this.typeOfSchedule = "CALENDAR";
        this.timeOfDay = -1L;
        this.dayOfWeek = -1;
        this.weekOfMonth = -1L;
        this.dateOfMonth = -1;
        this.monthOfYear = -1;
        this.yearOfDecade = -1;
        this.repeatFrequency = "NONE";
        this.unitOfTime = "seconds";
        this.timePeriod = -1L;
        this.executeImmediatelyFlag = false;
        this.ftpRemoteDirectory = "";
        this.viewName = viewName;
        this.formatOfReport = formatOfReport;
        this.typeOfSending = typeOfSending;
        this.ftpServer = ftpServer;
        this.ftpUserName = ftpUserName;
        this.ftpPassword = ftpPassword;
        this.ftpRemoteDirectory = ftpRemoteDirectory;
        this.timeOfDay = timeOfDay;
        this.dayOfWeek = dayOfWeek;
        this.weekOfMonth = weekOfMonth;
        this.dateOfMonth = dateOfMonth;
        this.monthOfYear = monthOfYear;
        this.repeatFrequency = repeatFrequency;
        this.unitOfTime = unitOfTime;
    }
    
    public HashMap getParameters() {
        return this.parameters;
    }
    
    public HashMap getParameters(final String viewName) {
        final HashMap map = new HashMap();
        for (final Object key : this.parameters.keySet()) {
            if (key.toString().indexOf("::") != -1) {
                final String viewname = key.toString().split("::")[0];
                final String paramname = key.toString().split("::")[1];
                if (!viewName.equals(viewName)) {
                    continue;
                }
                map.put(paramname, this.parameters.get(key));
            }
        }
        return map;
    }
    
    public String getViewName() {
        return this.viewName;
    }
    
    public String getFormatOfReport() {
        return this.formatOfReport;
    }
    
    public String getTypeOfSending() {
        return this.typeOfSending;
    }
    
    public String getFromAddress() {
        return this.fromAddress;
    }
    
    public String getToAddress() {
        return this.toAddress;
    }
    
    public String getAttachmentName() {
        return this.attachmentName;
    }
    
    public long getTimeOfDay() {
        return this.timeOfDay;
    }
    
    public int getDayOfWeek() {
        return this.dayOfWeek;
    }
    
    public long getWeekOfMonth() {
        return this.weekOfMonth;
    }
    
    public int getDateOfMonth() {
        return this.dateOfMonth;
    }
    
    public int getMonthOfYear() {
        return this.monthOfYear;
    }
    
    public int getYearOfDecade() {
        return this.yearOfDecade;
    }
    
    public String getRepeatFrequency() {
        return this.repeatFrequency;
    }
    
    public String getUserMessage() {
        return this.userMessage;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getFtpServer() {
        return this.ftpServer;
    }
    
    public String getFtpUserName() {
        return this.ftpUserName;
    }
    
    public String getFtpPassword() {
        return this.ftpPassword;
    }
    
    public String getFtpRemoteDirectory() {
        return this.ftpRemoteDirectory;
    }
    
    public String getUnitOfTime() {
        return this.unitOfTime;
    }
    
    public Timestamp getStartDate() {
        return this.startDate;
    }
    
    public Timestamp getEndDate() {
        return this.endDate;
    }
    
    public long getTimePeriod() {
        return this.timePeriod;
    }
    
    public boolean getExecuteImmediatelyFlag() {
        return this.executeImmediatelyFlag;
    }
    
    public String getTypeOfSchedule() {
        return this.typeOfSchedule;
    }
    
    public String getMailContextName() {
        return this.mailContextName;
    }
    
    public String getInfo() {
        return this.info;
    }
    
    public int getMaximumFileSize() {
        return this.maximumFileSize;
    }
    
    public void setMaximumFileSize(final int maximumFileSize) {
        this.maximumFileSize = maximumFileSize;
    }
    
    public void setTypeOfSchedule(final String typeOfSchedule) {
        this.typeOfSchedule = typeOfSchedule;
    }
    
    public void setMailContextName(final String mailContextName) {
        this.mailContextName = mailContextName;
    }
    
    public void setStartDate(final Timestamp startDate) {
        this.startDate = startDate;
    }
    
    public void setEndDate(final Timestamp endDate) {
        this.endDate = endDate;
    }
    
    public void setTimePeriod(final long timePeriod) {
        this.timePeriod = timePeriod;
    }
    
    public void setExecuteImmediatelyFlag(final boolean executeImmediatelyFlag) {
        this.executeImmediatelyFlag = executeImmediatelyFlag;
    }
    
    public void setParameters(final HashMap map) {
        this.parameters = map;
    }
    
    public void setViewName(final String viewName) {
        this.viewName = viewName;
    }
    
    public void setFormatOfReport(final String formatOfReport) {
        this.formatOfReport = formatOfReport;
    }
    
    public void setTypeOfSending(final String typeOfSending) {
        this.typeOfSending = typeOfSending;
    }
    
    public void setFromAddress(final String fromAddress) {
        this.fromAddress = fromAddress;
    }
    
    public void setToAddress(final String toAddress) {
        this.toAddress = toAddress;
    }
    
    public void setAttachmentName(final String attachmentName) {
        this.attachmentName = attachmentName;
    }
    
    public void setTimeOfDay(final long timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
    
    public void setdayOfWeek(final int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public void setWeekOfMonth(final long weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }
    
    public void setDateOfMonth(final int dateOfMonth) {
        this.dateOfMonth = dateOfMonth;
    }
    
    public void setMonthOfYear(final int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }
    
    public void setYearOfDecade(final int yearOfDecade) {
        this.yearOfDecade = yearOfDecade;
    }
    
    public void setRepeatFrequency(final String repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }
    
    public void setUserMessage(final String userMessage) {
        this.userMessage = userMessage;
    }
    
    public void setSubject(final String subject) {
        this.subject = subject;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setFtpServer(final String ftpServer) {
        this.ftpServer = ftpServer;
    }
    
    public void setFtpUserName(final String userName) {
        this.ftpUserName = userName;
    }
    
    public void setFtpPassword(final String password) {
        this.ftpPassword = password;
    }
    
    public void setFtpRemoteDirectory(final String remoteDirectory) {
        this.ftpRemoteDirectory = remoteDirectory;
    }
    
    public void setUnitOfTime(final String unitOfTime) {
        this.unitOfTime = unitOfTime;
    }
    
    public void setInfo(final String info) {
        this.info = info;
    }
    
    public boolean getSkipMissedSchedules() {
        return this.skipMissedSchedules;
    }
    
    public void setSkipMissedSchedules(final boolean bool) {
        this.skipMissedSchedules = bool;
    }
    
    public void setSkipFrequency(final int f) {
        this.skipFrequency = f;
    }
    
    public void setTimeZone(final String tz) {
        this.timeZone = tz;
    }
    
    public int getSkipFrequency() {
        return this.skipFrequency;
    }
    
    public String getTimeZone() {
        return this.timeZone;
    }
}
