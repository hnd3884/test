package com.adventnet.client.components.schedule;

import java.io.File;
import java.sql.Timestamp;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Iterator;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.taskengine.Scheduler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.client.util.LookUpUtil;
import java.util.logging.Logger;

public class ScheduledReportAPI
{
    private static Logger logger;
    
    public static void createScheduledReport(final String scheduleName, final ScheduledReportDetails details) throws Exception {
        final boolean isWorkEnginePresent = isWorkEnginePresent();
        final String viewName = details.getViewName();
        final Persistence persistence = LookUpUtil.getPersistence();
        final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
        if (isScheduleExists(scheduleName)) {
            throw new Exception("schedule " + scheduleName + " exists");
        }
        if ((details.getTypeOfSending().equals("mail") || details.getTypeOfSending().equals("slsl")) && details.getMailContextName().equals("null")) {
            if (!isWorkEnginePresent) {
                final Row rowACMAILCONFIG = new Row("ACMailConfig");
                final DataObject d2 = (DataObject)new WritableDataObject();
                rowACMAILCONFIG.set(2, (Object)scheduleName);
                rowACMAILCONFIG.set(3, (Object)details.getFromAddress());
                rowACMAILCONFIG.set(4, (Object)"");
                rowACMAILCONFIG.set(5, (Object)details.getHost());
                rowACMAILCONFIG.set(6, (Object)details.getUserName());
                rowACMAILCONFIG.set(7, (Object)details.getPassword());
                rowACMAILCONFIG.set(8, (Object)details.getPort());
                d2.addRow(rowACMAILCONFIG);
                per.update(d2);
            }
            else {
                final Row rowMAIL_SERVER = new Row("Mail_Server");
                final Row rowACCLIENTPROPS = new Row("ACClientProps");
                final DataObject d3 = (DataObject)new WritableDataObject();
                final DataObject d4 = (DataObject)new WritableDataObject();
                rowMAIL_SERVER.set("NAME", (Object)scheduleName);
                rowACCLIENTPROPS.set(2, (Object)(scheduleName + "_FromAddress"));
                rowACCLIENTPROPS.set(3, (Object)details.getFromAddress());
                rowMAIL_SERVER.set("SERVER_NAME", (Object)details.getHost());
                rowMAIL_SERVER.set("USER_NAME", (Object)details.getUserName());
                final String password = details.getPassword();
                if (password != null) {
                    rowMAIL_SERVER.set("PASSWORD", (Object)AuthUtil.encryptString(password));
                }
                rowMAIL_SERVER.set("PORT", (Object)details.getPort());
                d3.addRow(rowMAIL_SERVER);
                d4.addRow(rowACCLIENTPROPS);
                per.update(d3);
                per.update(d4);
            }
        }
        else if (isWorkEnginePresent) {
            final Row rowACCLIENTPROPS2 = new Row("ACClientProps");
            final DataObject d2 = (DataObject)new WritableDataObject();
            rowACCLIENTPROPS2.set(2, (Object)(details.getMailContextName() + "_FromAddress"));
            rowACCLIENTPROPS2.set(3, (Object)details.getFromAddress());
            d2.addRow(rowACCLIENTPROPS2);
            final Column columnProps = new Column("ACClientProps", "PARAMNAME");
            final Criteria criProps = new Criteria(columnProps, (Object)(details.getMailContextName() + "_FromAddress"), 0);
            final DataObject dobjProps = persistence.get("ACClientProps", criProps);
            if (dobjProps.isEmpty()) {
                per.update(d2);
            }
        }
        if (details.getTypeOfSending().equals("ftp")) {
            final DataObject d5 = (DataObject)new WritableDataObject();
            final Row rowACFTPDETAILS = new Row("ACFtpDetails");
            rowACFTPDETAILS.set(2, (Object)scheduleName);
            rowACFTPDETAILS.set(4, (Object)details.getFtpUserName());
            rowACFTPDETAILS.set(5, (Object)details.getFtpPassword());
            rowACFTPDETAILS.set(3, (Object)details.getFtpServer());
            rowACFTPDETAILS.set(6, (Object)details.getFtpRemoteDirectory());
            d5.addRow(rowACFTPDETAILS);
            per.update(d5);
        }
        final DataObject d6 = (DataObject)new WritableDataObject();
        final Row rowSCHEDULE = new Row("Schedule");
        rowSCHEDULE.set(2, (Object)scheduleName);
        d6.addRow(rowSCHEDULE);
        per.update(d6);
        final DataObject d7 = (DataObject)new WritableDataObject();
        final Row r = new Row("ScheduledReports");
        r.set(1, rowSCHEDULE.get(1));
        r.set(2, (Object)details.getFormatOfReport());
        r.set(3, (Object)details.getTypeOfSending());
        r.set(5, (Object)details.getUserMessage());
        r.set(4, (Object)details.getSubject());
        r.set(6, (Object)details.getAttachmentName());
        r.set(9, (Object)details.getMailContextName());
        if (details.getTypeOfSending().equals("mail") || details.getTypeOfSending().equals("slsl")) {
            r.set(10, (Object)details.getToAddress());
        }
        r.set(11, (Object)details.getInfo());
        final StringBuffer parameterNames = new StringBuffer();
        final StringBuffer parameterValues = new StringBuffer();
        final HashMap param = details.getParameters();
        for (final String key : param.keySet()) {
            if (param.get(key) == null) {
                param.put(key, "");
            }
            final String value = param.get(key);
            parameterNames.append(key);
            parameterNames.append(",");
            parameterValues.append(value);
            parameterValues.append(",");
        }
        r.set(7, (Object)parameterNames.toString());
        r.set(8, (Object)parameterValues.toString());
        r.set(12, (Object)details.getMaximumFileSize());
        d7.addRow(r);
        per.update(d7);
        final DataObject newdobj = (DataObject)new WritableDataObject();
        for (final String viewname : details.viewName.split(",")) {
            final Row scheduleViewRow = new Row("Schedule_View");
            scheduleViewRow.set(2, (Object)scheduleName);
            scheduleViewRow.set("VIEWNAME", (Object)viewname);
            newdobj.addRow(scheduleViewRow);
        }
        per.update(newdobj);
        if (!details.getTypeOfSchedule().equals("PERIODIC")) {
            final DataObject d8 = (DataObject)new WritableDataObject();
            final Row rowCALENDER = new Row("Calendar");
            rowCALENDER.set(1, rowSCHEDULE.get(1));
            rowCALENDER.set(3, (Object)details.getTimeOfDay());
            rowCALENDER.set(5, (Object)details.getDayOfWeek());
            rowCALENDER.set(6, (Object)details.getWeekOfMonth());
            rowCALENDER.set(7, (Object)details.getDateOfMonth());
            rowCALENDER.set(8, (Object)details.getMonthOfYear());
            rowCALENDER.set(10, (Object)details.getTimeZone());
            rowCALENDER.set(11, (Object)details.getSkipFrequency());
            rowCALENDER.set(9, (Object)details.getYearOfDecade());
            rowCALENDER.set(2, (Object)details.getRepeatFrequency());
            rowCALENDER.set(4, (Object)details.getUnitOfTime());
            d8.addRow(rowCALENDER);
            per.update(d8);
        }
        if (!details.getTypeOfSchedule().equals("CALENDAR")) {
            final DataObject periodicDO = (DataObject)new WritableDataObject();
            final Row periodicRow = new Row("Periodic");
            periodicRow.set(1, rowSCHEDULE.get(1));
            periodicRow.set("START_DATE", (Object)details.getStartDate());
            periodicRow.set("END_DATE", (Object)details.getEndDate());
            periodicRow.set("TIME_PERIOD", (Object)details.getTimePeriod());
            periodicRow.set("UNIT_OF_TIME", (Object)details.getUnitOfTime());
            periodicRow.set("EXECUTE_IMMEDIATELY", (Object)details.getExecuteImmediatelyFlag());
            periodicDO.addRow(periodicRow);
            per.update(periodicDO);
        }
        final DataObject d9 = (DataObject)new WritableDataObject();
        final Row rowTaskEngine_Task = new Row("TaskEngine_Task");
        final String taskName = scheduleName + "_task";
        rowTaskEngine_Task.set(2, (Object)taskName);
        rowTaskEngine_Task.set(3, (Object)"com.adventnet.client.components.schedule.SendReport");
        d9.addRow(rowTaskEngine_Task);
        per.update(d9);
        DataObject d10 = (DataObject)new WritableDataObject();
        final Row emptySTRow = new Row("Scheduled_Task");
        final boolean skipMissedSchedules = details.getSkipMissedSchedules();
        emptySTRow.set("SCHEDULE_ID", rowSCHEDULE.get(1));
        emptySTRow.set("SCHEDULE_MODE", (Object)new Integer(1));
        emptySTRow.set("RETRY_SCHEDULE_ID", rowSCHEDULE.get(1));
        emptySTRow.set(2, rowTaskEngine_Task.get(1));
        emptySTRow.set(9, (Object)skipMissedSchedules);
        d10.addRow(emptySTRow);
        per.update(d10);
        d10 = (DataObject)new WritableDataObject();
        final Row emptyTIRow = new Row("Task_Input");
        d10.addRow(emptyTIRow);
        final Scheduler sh = (Scheduler)BeanUtil.lookup("Scheduler");
        sh.scheduleTask(scheduleName, taskName, d10);
    }
    
    public static void deleteScheduledReport(final String scheduleName) throws Exception {
        final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
        if (!isScheduleExists(scheduleName)) {
            throw new Exception("schedule doesnot exists");
        }
        final boolean isWorkEnginePresent = isWorkEnginePresent();
        final Scheduler sh = (Scheduler)BeanUtil.lookup("Scheduler");
        sh.unscheduleTask(scheduleName, scheduleName + "_task");
        Column id = new Column("Schedule", "SCHEDULE_NAME");
        Criteria cri = new Criteria(id, (Object)scheduleName, 0);
        final DataObject dobj = per.get("Schedule", cri);
        final int ScheduleId = Integer.parseInt(dobj.getFirstRow("Schedule").get(1).toString());
        dobj.deleteRows("Schedule", cri);
        per.update(dobj);
        id = new Column("TaskEngine_Task", "TASK_NAME");
        cri = new Criteria(id, (Object)(scheduleName + "_task"), 0);
        final DataObject dobj2 = per.get("TaskEngine_Task", cri);
        dobj2.deleteRows("TaskEngine_Task", cri);
        per.update(dobj2);
        id = new Column("ScheduledReports", "SCH_ID");
        cri = new Criteria(id, (Object)ScheduleId, 0);
        final DataObject dobj3 = per.get("ScheduledReports", cri);
        final String reportingType = (String)dobj3.getFirstRow("ScheduledReports").get(3);
        final String mailContextName = (String)dobj3.getFirstRow("ScheduledReports").get(9);
        dobj3.deleteRows("ScheduledReports", cri);
        per.update(dobj3);
        final Column scCol = new Column("Schedule_View", "SCHEDULE_NAME");
        final Criteria delcri = new Criteria(scCol, (Object)scheduleName, 0);
        per.delete(delcri);
        if (reportingType.equals("ftp")) {
            id = new Column("ACFtpDetails", "SCHEDULE_NAME");
            cri = new Criteria(id, (Object)scheduleName, 0);
            final DataObject dobj4 = per.get("ACFtpDetails", cri);
            dobj4.deleteRows("ACFtpDetails", cri);
            per.update(dobj4);
        }
        if ((reportingType.equals("mail") || reportingType.equals("slsl")) && mailContextName.equals("null")) {
            if (!isWorkEnginePresent) {
                id = new Column("ACMailConfig", "CONTEXT_NAME");
                cri = new Criteria(id, (Object)scheduleName, 0);
                final DataObject dobj4 = per.get("ACMailConfig", cri);
                dobj4.deleteRows("ACMailConfig", cri);
                per.update(dobj4);
            }
            else {
                id = new Column("Mail_Server", "NAME");
                cri = new Criteria(id, (Object)scheduleName, 0);
                final DataObject dobj4 = per.get("Mail_Server", cri);
                dobj4.deleteRows("Mail_Server", cri);
                per.update(dobj4);
                id = new Column("ACClientProps", "PARAMNAME");
                cri = new Criteria(id, (Object)(scheduleName + "_FromAddress"), 0);
                per.delete(cri);
            }
        }
    }
    
    public static void updateScheduledReport(final String scheduleName, final ScheduledReportDetails details) throws Exception {
        final boolean isWorkEnginePresent = isWorkEnginePresent();
        if (!isScheduleExists(scheduleName)) {
            throw new Exception("schedule doesnot exist");
        }
        final Persistence persistence = LookUpUtil.getPersistence();
        final Column col = new Column("Schedule", "SCHEDULE_NAME");
        final Criteria crit = new Criteria(col, (Object)scheduleName, 0);
        DataObject dobj = persistence.get("Schedule", crit);
        final Iterator it = dobj.get("Schedule", "SCHEDULE_NAME");
        final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
        Column id = new Column("Schedule", "SCHEDULE_NAME");
        Criteria cri = new Criteria(id, (Object)scheduleName, 0);
        dobj = per.get("Schedule", cri);
        final long scheduleId = Integer.parseInt(dobj.getFirstRow("Schedule").get(1).toString());
        id = new Column("ScheduledReports", "SCH_ID");
        cri = new Criteria(id, (Object)scheduleId, 0);
        dobj = persistence.get("ScheduledReports", cri);
        final Row SCHEDULEDREPORTSrow = dobj.getFirstRow("ScheduledReports");
        String mailContextName = "null";
        if (SCHEDULEDREPORTSrow.get("MAIL_CONTEXT_NAME") != null) {
            mailContextName = (String)SCHEDULEDREPORTSrow.get("MAIL_CONTEXT_NAME");
        }
        else {
            mailContextName = details.getMailContextName();
        }
        if (!it.hasNext()) {
            throw new Exception("schedule " + scheduleName + " does not exists");
        }
        final Column column = new Column("ScheduledReports", "SCH_ID");
        final Criteria cr = new Criteria(column, (Object)scheduleId, 0);
        dobj = per.get("ScheduledReports", cr);
        boolean changeToFtp = false;
        boolean changeToMail = false;
        final String dbVal = (String)dobj.getFirstRow("ScheduledReports").get("SEND_TYPE");
        if (details.getTypeOfSending() != null && !details.getTypeOfSending().equals(dbVal)) {
            if (((String)dobj.getFirstRow("ScheduledReports").get("SEND_TYPE")).equals("mail") && !details.getTypeOfSending().equals("slsl")) {
                if (!isWorkEnginePresent) {
                    final Column idMail = new Column("ACMailConfig", "CONTEXT_NAME");
                    final Criteria criMail = new Criteria(idMail, (Object)scheduleName, 0);
                    dobj = per.get("ACMailConfig", criMail);
                    dobj.deleteRows("ACMailConfig", criMail);
                    per.update(dobj);
                    changeToFtp = true;
                }
                else {
                    Column idMail = new Column("Mail_Server", "NAME");
                    Criteria criMail = new Criteria(idMail, (Object)scheduleName, 0);
                    dobj = per.get("Mail_Server", criMail);
                    dobj.deleteRows("MailServer", criMail);
                    per.update(dobj);
                    idMail = new Column("ACClientProps", "PARAMNAME");
                    criMail = new Criteria(idMail, (Object)(scheduleName + "_FromAddress"), 0);
                    dobj = per.get("ACClientProps", criMail);
                    dobj.deleteRows("ACClientProps", criMail);
                    per.update(dobj);
                    changeToFtp = true;
                }
            }
            else if (((String)dobj.getFirstRow("ScheduledReports").get("SEND_TYPE")).equals("ftp")) {
                final Column idFtp = new Column("ACFtpDetails", "SCHEDULE_NAME");
                final Criteria criFtp = new Criteria(idFtp, (Object)scheduleName, 0);
                final DataObject dobj2 = per.get("ACFtpDetails", criFtp);
                dobj2.deleteRows("ACFtpDetails", criFtp);
                per.update(dobj2);
                changeToMail = true;
            }
        }
        else if (details.getTypeOfSending() == null) {
            details.setTypeOfSending(dbVal);
        }
        if (details.getTypeOfSending().equals("mail") || details.getTypeOfSending().equals("slsl")) {
            final DataObject d = (DataObject)new WritableDataObject();
            final DataObject d2 = (DataObject)new WritableDataObject();
            DataObject d3 = (DataObject)new WritableDataObject();
            if (!isWorkEnginePresent) {
                final Row rowACMAILCONFIG = new Row("ACMailConfig");
                id = new Column("ACMailConfig", "CONTEXT_NAME");
                cri = new Criteria(id, (Object)scheduleName, 0);
                if (!mailContextName.equals("null")) {
                    cri = new Criteria(id, (Object)mailContextName, 0);
                }
                dobj = per.get("ACMailConfig", cri);
                final UpdateQueryImpl uq = new UpdateQueryImpl("ACMailConfig");
                uq.setCriteria(cri);
                if (changeToMail) {
                    rowACMAILCONFIG.set(1, dobj.getFirstRow("ACMailConfig").get("CONTEXT_NAME_NO"));
                    if (!mailContextName.equals("null")) {
                        rowACMAILCONFIG.set(2, (Object)mailContextName);
                    }
                    else {
                        rowACMAILCONFIG.set(2, (Object)scheduleName);
                    }
                    rowACMAILCONFIG.set(3, (Object)details.getFromAddress());
                    rowACMAILCONFIG.set(5, (Object)details.getHost());
                    rowACMAILCONFIG.set(6, (Object)details.getUserName());
                    rowACMAILCONFIG.set(7, (Object)details.getPassword());
                    rowACMAILCONFIG.set(8, (Object)details.getPort());
                    d.addRow(rowACMAILCONFIG);
                    per.update(dobj.diff(d));
                }
                else {
                    uq.setUpdateColumn("CONTEXT_NAME_NO", dobj.getFirstRow("ACMailConfig").get("CONTEXT_NAME_NO"));
                    if (!mailContextName.equals("null")) {
                        uq.setUpdateColumn("CONTEXT_NAME", (Object)mailContextName);
                    }
                    else {
                        uq.setUpdateColumn("CONTEXT_NAME", (Object)scheduleName);
                    }
                    if (details.getFromAddress() != null) {
                        uq.setUpdateColumn("FROMADDRESS", (Object)details.getFromAddress());
                    }
                    if (details.getHost() != null) {
                        uq.setUpdateColumn("SERVER", (Object)details.getHost());
                    }
                    if (details.getUserName() != null) {
                        uq.setUpdateColumn("USERNAME", (Object)details.getUserName());
                    }
                    if (details.getPassword() != null) {
                        uq.setUpdateColumn("PASSWORD", (Object)details.getPassword());
                    }
                    if (details.getPort() != -1) {
                        uq.setUpdateColumn("PORT", (Object)details.getPort());
                    }
                    per.update((UpdateQuery)uq);
                }
            }
            else {
                final Row rowMAIL_SERVER = new Row("Mail_Server");
                final Row rowACCLIENTPROPS = new Row("ACClientProps");
                id = new Column("Mail_Server", "NAME");
                cri = new Criteria(id, (Object)scheduleName, 0);
                if (!mailContextName.equals("null")) {
                    cri = new Criteria(id, (Object)mailContextName, 0);
                }
                dobj = per.get("Mail_Server", cri);
                id = new Column("ACClientProps", "PARAMNAME");
                Criteria criter = new Criteria(id, (Object)(scheduleName + "_FromAddress"), 0);
                if (!mailContextName.equals("null")) {
                    criter = new Criteria(id, (Object)(mailContextName + "_FromAddress"), 0);
                }
                d3 = per.get("ACClientProps", criter);
                UpdateQueryImpl uq2 = new UpdateQueryImpl("Mail_Server");
                uq2.setCriteria(cri);
                if (changeToMail) {
                    rowMAIL_SERVER.set("SERVER_ID", dobj.getFirstRow("Mail_Server").get("SERVER_ID"));
                    if (!mailContextName.equals("null")) {
                        rowMAIL_SERVER.set("NAME", (Object)mailContextName);
                        rowACCLIENTPROPS.set(2, (Object)(mailContextName + "_FromAddress"));
                        rowACCLIENTPROPS.set(3, (Object)details.getFromAddress());
                    }
                    else {
                        rowMAIL_SERVER.set("NAME", (Object)scheduleName);
                        rowACCLIENTPROPS.set(2, (Object)(scheduleName + "_FromAddress"));
                        rowACCLIENTPROPS.set(3, (Object)details.getFromAddress());
                    }
                    rowMAIL_SERVER.set("SERVER_NAME", (Object)details.getHost());
                    rowMAIL_SERVER.set("USER_NAME", (Object)details.getUserName());
                    final String password = details.getPassword();
                    if (password != null) {
                        rowMAIL_SERVER.set("PASSWORD", (Object)AuthUtil.encryptString(password));
                    }
                    rowMAIL_SERVER.set("PORT", (Object)details.getPort());
                    d.addRow(rowMAIL_SERVER);
                    d2.addRow(rowACCLIENTPROPS);
                    per.update(dobj.diff(d));
                    per.update(d3.diff(d2));
                }
                else {
                    uq2.setUpdateColumn("SERVER_ID", dobj.getFirstRow("Mail_Server").get("SERVER_ID"));
                    String contextName;
                    if (!mailContextName.equals("null")) {
                        uq2.setUpdateColumn("NAME", (Object)mailContextName);
                        contextName = mailContextName;
                    }
                    else {
                        uq2.setUpdateColumn("NAME", (Object)scheduleName);
                        contextName = scheduleName;
                    }
                    if (details.getHost() != null) {}
                    uq2.setUpdateColumn("SERVER_NAME", (Object)details.getHost());
                    if (details.getUserName() != null) {
                        uq2.setUpdateColumn("USER_NAME", (Object)details.getUserName());
                    }
                    if (details.getPassword() != null) {
                        uq2.setUpdateColumn("PASSWORD", (Object)AuthUtil.encryptString(details.getPassword()));
                    }
                    if (details.getPort() != -1) {
                        uq2.setUpdateColumn("PORT", (Object)details.getPort());
                    }
                    per.update((UpdateQuery)uq2);
                    uq2 = new UpdateQueryImpl("ACClientProps");
                    uq2.setCriteria(criter);
                    uq2.setUpdateColumn("PARAMNAME", (Object)(contextName + "_FromAddress"));
                    if (details.getFromAddress() != null) {
                        uq2.setUpdateColumn("PARAMVALUE", (Object)details.getFromAddress());
                    }
                }
            }
        }
        if (details.getTypeOfSending().equals("ftp")) {
            if (changeToFtp) {
                id = new Column("ACFtpDetails", "SCHEDULE_NAME");
                cri = new Criteria(id, (Object)scheduleName, 0);
                dobj = per.get("ACFtpDetails", cri);
                final DataObject d = (DataObject)new WritableDataObject();
                final Row rowACFTPDETAILS = new Row("ACFtpDetails");
                if (!changeToFtp && dobj.getFirstRow("ACFtpDetails").get("ID") != null) {
                    rowACFTPDETAILS.set(1, dobj.getFirstRow("ACFtpDetails").get("ID"));
                }
                rowACFTPDETAILS.set(2, (Object)scheduleName);
                if (details.getFtpUserName() != null) {
                    rowACFTPDETAILS.set(4, (Object)details.getFtpUserName());
                }
                if (details.getFtpPassword() != null) {
                    rowACFTPDETAILS.set(5, (Object)details.getFtpPassword());
                }
                if (details.getFtpServer() != null) {
                    rowACFTPDETAILS.set(3, (Object)details.getFtpServer());
                }
                if (details.getFtpRemoteDirectory() != null) {
                    rowACFTPDETAILS.set(6, (Object)details.getFtpRemoteDirectory());
                }
                d.addRow(rowACFTPDETAILS);
                per.update(dobj.diff(d));
            }
            else {
                id = new Column("ACFtpDetails", "SCHEDULE_NAME");
                cri = new Criteria(id, (Object)scheduleName, 0);
                dobj = per.get("ACFtpDetails", cri);
                final UpdateQueryImpl uq3 = new UpdateQueryImpl("ACFtpDetails");
                uq3.setCriteria(cri);
                uq3.setUpdateColumn("SCHEDULE_NAME", (Object)scheduleName);
                if (details.getFtpUserName() != null) {
                    uq3.setUpdateColumn("USERNAME", (Object)details.getFtpUserName());
                }
                if (details.getFtpPassword() != null) {
                    uq3.setUpdateColumn("PASSWORD", (Object)details.getFtpPassword());
                }
                if (details.getFtpServer() != null) {
                    uq3.setUpdateColumn("SERVER", (Object)details.getFtpServer());
                }
                if (details.getFtpRemoteDirectory() != null) {
                    uq3.setUpdateColumn("REMOTE_DIRECTORY", (Object)details.getFtpRemoteDirectory());
                }
                per.update((UpdateQuery)uq3);
            }
        }
        if (details.getViewName() != null) {
            final Column scCol = new Column("Schedule_View", "SCHEDULE_NAME");
            final Criteria scCri = new Criteria(scCol, (Object)scheduleName, 0);
            persistence.delete(scCri);
            final String[] viewNames = details.getViewName().split(",");
            final DataObject temp = (DataObject)new WritableDataObject();
            for (int i = 0; i < viewNames.length; ++i) {
                final Row row = new Row("Schedule_View");
                row.set(2, (Object)scheduleName);
                row.set(3, (Object)viewNames[i]);
                temp.addRow(row);
            }
            persistence.add(temp);
        }
        id = new Column("ScheduledReports", "SCH_ID");
        cri = new Criteria(id, (Object)scheduleId, 0);
        UpdateQueryImpl uq3 = new UpdateQueryImpl("ScheduledReports");
        uq3.setCriteria(cri);
        uq3.setUpdateColumn("SCH_ID", (Object)scheduleId);
        if (details.getFormatOfReport() != null) {
            uq3.setUpdateColumn("FORMAT", (Object)details.getFormatOfReport());
        }
        if (details.getTypeOfSending() != null) {
            uq3.setUpdateColumn("SEND_TYPE", (Object)details.getTypeOfSending());
        }
        if (details.getUserMessage() != null) {
            uq3.setUpdateColumn("MESSAGE", (Object)details.getUserMessage());
        }
        if (details.getSubject() != null) {
            uq3.setUpdateColumn("SUBJECT", (Object)details.getSubject());
        }
        if (details.getAttachmentName() != null) {
            uq3.setUpdateColumn("ATTACHMENT_NAME", (Object)details.getAttachmentName());
        }
        if (details.getToAddress() != null) {
            uq3.setUpdateColumn("TOADDRESS", (Object)details.getToAddress());
        }
        if (details.getInfo() != null) {
            uq3.setUpdateColumn("SCHEDULEINFO", (Object)details.getInfo());
        }
        uq3.setUpdateColumn("MAXFILESIZE", (Object)details.getMaximumFileSize());
        final StringBuffer parameterNames = new StringBuffer();
        final StringBuffer parameterValues = new StringBuffer();
        final HashMap param = details.getParameters();
        for (final String key : param.keySet()) {
            if (param.get(key) == null) {
                param.put(key, "");
            }
            final String value = param.get(key);
            parameterNames.append(key);
            parameterNames.append(",");
            parameterValues.append(value);
            parameterValues.append(",");
        }
        uq3.setUpdateColumn("PARAMETER_NAMES", (Object)parameterNames.toString());
        uq3.setUpdateColumn("PARAMETER_VALUES", (Object)parameterValues.toString());
        per.update((UpdateQuery)uq3);
        final RelationalAPI rel = RelationalAPI.getInstance();
        ScheduledReportAPI.logger.log(Level.FINE, "details.getTypeOfSchedule :: [{0}]", details.getTypeOfSchedule());
        if (!details.getTypeOfSchedule().equals("PERIODIC")) {
            id = new Column("Calendar", "SCHEDULE_ID");
            cri = new Criteria(id, (Object)scheduleId, 0);
            dobj = per.get("Calendar", cri);
            ScheduledReportAPI.logger.log(Level.FINE, "CALENDAR dobj is :: [{0}]" + dobj);
            boolean isPeriodic = false;
            if (dobj.isEmpty()) {
                isPeriodic = true;
            }
            if (!isPeriodic) {
                uq3 = new UpdateQueryImpl("Calendar");
                uq3.setCriteria(cri);
                uq3.setUpdateColumn("SCHEDULE_ID", (Object)scheduleId);
                if (details.getTimeOfDay() != -1L) {
                    uq3.setUpdateColumn("TIME_OF_DAY", (Object)details.getTimeOfDay());
                }
                if (details.getDayOfWeek() != -1) {
                    uq3.setUpdateColumn("DAY_OF_WEEK", (Object)details.getDayOfWeek());
                }
                if (details.getWeekOfMonth() != -1L) {
                    uq3.setUpdateColumn("WEEK", (Object)details.getWeekOfMonth());
                }
                if (details.getDateOfMonth() != -1) {
                    uq3.setUpdateColumn("DATE_OF_MONTH", (Object)details.getDateOfMonth());
                }
                if (details.getMonthOfYear() != -1) {
                    uq3.setUpdateColumn("MONTH_OF_YEAR", (Object)details.getMonthOfYear());
                }
                if (details.getSkipFrequency() != 0) {
                    uq3.setUpdateColumn("SKIP_FREQUENCY", (Object)details.getSkipFrequency());
                }
                if (details.getTimeZone() != null) {
                    uq3.setUpdateColumn("TZ", (Object)details.getTimeZone());
                }
                if (details.getYearOfDecade() != -1) {
                    uq3.setUpdateColumn("YEAR_OF_DECADE", (Object)details.getYearOfDecade());
                }
                if (details.getRepeatFrequency() != null) {
                    uq3.setUpdateColumn("REPEAT_FREQUENCY", (Object)details.getRepeatFrequency());
                }
                if (details.getUnitOfTime() != null) {
                    uq3.setUpdateColumn("UNIT_OF_TIME", (Object)details.getUnitOfTime());
                }
                ScheduledReportAPI.logger.log(Level.FINE, "uq :: [{0}]", uq3);
                per.update((UpdateQuery)uq3);
            }
            else {
                final DataObject addCalDO = (DataObject)new WritableDataObject();
                final Row calRowToAdd = new Row("Calendar");
                calRowToAdd.set("SCHEDULE_ID", (Object)scheduleId);
                if (details.getTimeOfDay() != -1L) {
                    calRowToAdd.set("TIME_OF_DAY", (Object)details.getTimeOfDay());
                }
                if (details.getDayOfWeek() != -1) {
                    calRowToAdd.set("DAY_OF_WEEK", (Object)details.getDayOfWeek());
                }
                if (details.getWeekOfMonth() != -1L) {
                    calRowToAdd.set("WEEK", (Object)details.getWeekOfMonth());
                }
                if (details.getDateOfMonth() != -1) {
                    calRowToAdd.set("DATE_OF_MONTH", (Object)details.getDateOfMonth());
                }
                if (details.getMonthOfYear() != -1) {
                    calRowToAdd.set("MONTH_OF_YEAR", (Object)details.getMonthOfYear());
                }
                if (details.getSkipFrequency() != -1) {
                    calRowToAdd.set("SKIP_FREQUENCY", (Object)details.getSkipFrequency());
                }
                if (details.getTimeZone() != null) {
                    calRowToAdd.set("TZ", (Object)details.getTimeZone());
                }
                if (details.getYearOfDecade() != -1) {
                    calRowToAdd.set("YEAR_OF_DECADE", (Object)details.getYearOfDecade());
                }
                if (details.getRepeatFrequency() != null) {
                    calRowToAdd.set("REPEAT_FREQUENCY", (Object)details.getRepeatFrequency());
                }
                if (details.getUnitOfTime() != null) {
                    calRowToAdd.set("UNIT_OF_TIME", (Object)details.getUnitOfTime());
                }
                addCalDO.addRow(calRowToAdd);
                per.add(addCalDO);
                id = new Column("Periodic", "SCHEDULE_ID");
                cri = new Criteria(id, (Object)scheduleId, 0);
                dobj = per.get("Periodic", cri);
                dobj.deleteRows("Periodic", cri);
                ScheduledReportAPI.logger.log(Level.FINE, "update dobj :: [{0}]", dobj);
                per.update(dobj);
            }
        }
        if (!details.getTypeOfSchedule().equals("CALENDAR")) {
            id = new Column("Periodic", "SCHEDULE_ID");
            cri = new Criteria(id, (Object)scheduleId, 0);
            dobj = per.get("Periodic", cri);
            ScheduledReportAPI.logger.log(Level.FINE, "CALENDAR dobj :: [{0}]", dobj);
            boolean isCalendar = false;
            if (dobj.isEmpty()) {
                isCalendar = true;
            }
            if (!isCalendar) {
                uq3 = new UpdateQueryImpl("Periodic");
                uq3.setCriteria(cri);
                uq3.setUpdateColumn("SCHEDULE_ID", (Object)scheduleId);
                if (details.getStartDate() != null && !details.getStartDate().equals("")) {
                    uq3.setUpdateColumn("START_DATE", (Object)details.getStartDate());
                }
                uq3.setUpdateColumn("END_DATE", (Object)details.getEndDate());
                if (details.getTimePeriod() != -1L) {
                    uq3.setUpdateColumn("TIME_PERIOD", (Object)details.getTimePeriod());
                }
                if (details.getUnitOfTime() != null) {
                    uq3.setUpdateColumn("UNIT_OF_TIME", (Object)details.getUnitOfTime());
                }
                uq3.setUpdateColumn("EXECUTE_IMMEDIATELY", (Object)details.getExecuteImmediatelyFlag());
                ScheduledReportAPI.logger.log(Level.FINE, "uq :: [{0}]", uq3);
                per.update((UpdateQuery)uq3);
            }
            else {
                final DataObject addPerDO = (DataObject)new WritableDataObject();
                final Row perRowToAdd = new Row("Periodic");
                perRowToAdd.set("SCHEDULE_ID", (Object)scheduleId);
                if (details.getStartDate() != null && !details.getStartDate().equals("")) {
                    perRowToAdd.set("START_DATE", (Object)details.getStartDate());
                }
                if (details.getEndDate() != null && !details.getEndDate().equals("")) {
                    perRowToAdd.set("END_DATE", (Object)details.getEndDate());
                }
                if (details.getTimePeriod() != -1L) {
                    perRowToAdd.set("TIME_PERIOD", (Object)details.getTimePeriod());
                }
                if (details.getUnitOfTime() != null) {
                    perRowToAdd.set("UNIT_OF_TIME", (Object)details.getUnitOfTime());
                }
                perRowToAdd.set("EXECUTE_IMMEDIATELY", (Object)details.getExecuteImmediatelyFlag());
                addPerDO.addRow(perRowToAdd);
                per.add(addPerDO);
                id = new Column("Calendar", "SCHEDULE_ID");
                cri = new Criteria(id, (Object)scheduleId, 0);
                dobj = per.get("Calendar", cri);
                dobj.deleteRows("Calendar", cri);
                ScheduledReportAPI.logger.log(Level.FINE, "updating dobj :: [{0}]", dobj);
                per.update(dobj);
            }
        }
        id = new Column("TaskEngine_Task", "TASK_NAME");
        cri = new Criteria(id, (Object)(scheduleName + "_task"), 0);
        dobj = per.get("TaskEngine_Task", cri);
        uq3 = new UpdateQueryImpl("TaskEngine_Task");
        uq3.setCriteria(cri);
        uq3.setUpdateColumn("TASK_ID", dobj.getFirstRow("TaskEngine_Task").get("TASK_ID"));
        uq3.setUpdateColumn("TASK_NAME", (Object)(scheduleName + "_task"));
        per.update((UpdateQuery)uq3);
        final Scheduler sh = (Scheduler)BeanUtil.lookup("Scheduler");
        final List tableNames = new ArrayList();
        tableNames.add("Schedule");
        tableNames.add("Calendar");
        tableNames.add("Periodic");
        tableNames.add("Calendar_Periodicity");
        tableNames.add("Composite");
        tableNames.add("Scheduled_Task");
        tableNames.add("Task_Input");
        tableNames.add("TaskEngine_Task");
        final DataObject dobjSCHEDULE = persistence.get(tableNames, tableNames, crit);
        ScheduledReportAPI.logger.log(Level.FINE, "sh :: [{0}]", dobjSCHEDULE);
        sh.updateSchedule(dobjSCHEDULE);
    }
    
    public static HashMap getSchedules() throws Exception {
        final Table table1 = new Table("Schedule");
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(table1);
        final Column c1 = new Column("Schedule", "SCHEDULE_ID");
        final Column c2 = new Column("Schedule", "SCHEDULE_NAME");
        final ArrayList colList = new ArrayList();
        colList.add(c1);
        colList.add(c2);
        sq.addSelectColumns((List)colList);
        final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
        final DataObject d = per.get(sq);
        final Iterator it = d.getRows("Schedule");
        final HashMap hm = new HashMap(100, 0.75f);
        String data = "";
        int cntr = 1;
        while (it.hasNext()) {
            final Row row = it.next();
            data = (String)row.get("SCHEDULE_NAME");
            hm.put(cntr, data);
            ++cntr;
        }
        return hm;
    }
    
    public static ScheduledReportDetails getScheduleDetails(final String scheduleName) throws Exception {
        if (!isScheduleExists(scheduleName)) {
            throw new Exception("schedule doesnot exist");
        }
        final ScheduledReportDetails details = new ScheduledReportDetails();
        final Persistence persistence = LookUpUtil.getPersistence();
        Column col = new Column("Schedule", "SCHEDULE_NAME");
        Criteria crit = new Criteria(col, (Object)scheduleName, 0);
        DataObject dobj = persistence.get("Schedule", crit);
        long scheduleId;
        try {
            final Row rowSchedule = dobj.getFirstRow("Schedule");
            scheduleId = (long)rowSchedule.get("SCHEDULE_ID");
            dobj.get("Schedule", "SCHEDULE_NAME");
        }
        catch (final Exception e) {
            throw new Exception("schedule " + scheduleName + "does not exists");
        }
        final Column scVCol = new Column("Schedule_View", "SCHEDULE_NAME");
        final Criteria scVCri = new Criteria(scVCol, (Object)scheduleName, 0);
        final DataObject scvDobj = persistence.get("Schedule_View", scVCri);
        final Iterator viewNameIt = scvDobj.getRows("Schedule_View");
        String viewNames = null;
        while (viewNameIt.hasNext()) {
            final Row scheduleViewRow = viewNameIt.next();
            if (viewNames != null) {
                viewNames = viewNames + "," + (String)scheduleViewRow.get("VIEWNAME");
            }
            else {
                viewNames = (String)scheduleViewRow.get("VIEWNAME");
            }
        }
        details.setViewName(viewNames);
        try {
            col = new Column("ScheduledReports", "SCH_ID");
            crit = new Criteria(col, (Object)scheduleId, 0);
            dobj = persistence.get("ScheduledReports", crit);
            final Row rowScheduledReports = dobj.getFirstRow("ScheduledReports");
            details.setFormatOfReport((String)rowScheduledReports.get("FORMAT"));
            details.setTypeOfSending((String)rowScheduledReports.get("SEND_TYPE"));
            details.setSubject((String)rowScheduledReports.get("SUBJECT"));
            details.setUserMessage((String)rowScheduledReports.get("MESSAGE"));
            details.setAttachmentName((String)rowScheduledReports.get("ATTACHMENT_NAME"));
            details.setMailContextName((String)rowScheduledReports.get("MAIL_CONTEXT_NAME"));
            details.setMaximumFileSize((int)rowScheduledReports.get("MAXFILESIZE"));
            if (rowScheduledReports.get("TOADDRESS") != null) {
                details.setToAddress((String)rowScheduledReports.get("TOADDRESS"));
            }
            if (rowScheduledReports.get("SCHEDULEINFO") != null) {
                details.setInfo((String)rowScheduledReports.get("SCHEDULEINFO"));
            }
            details.setParameters(getParamMap(rowScheduledReports));
        }
        catch (final Exception e2) {
            throw new Exception("error in scheduledreports table");
        }
        boolean hasSchedule = false;
        col = new Column("Periodic", "SCHEDULE_ID");
        crit = new Criteria(col, (Object)scheduleId, 0);
        dobj = persistence.get("Periodic", crit);
        Iterator it = dobj.get("Periodic", "SCHEDULE_ID");
        if (it.hasNext()) {
            details.setTypeOfSchedule("PERIODIC");
            final Row rowPeriodic = dobj.getFirstRow("Periodic");
            details.setStartDate((Timestamp)rowPeriodic.get("START_DATE"));
            details.setEndDate((Timestamp)rowPeriodic.get("END_DATE"));
            details.setTimePeriod((long)rowPeriodic.get("TIME_PERIOD"));
            details.setUnitOfTime((String)rowPeriodic.get("UNIT_OF_TIME"));
            details.setExecuteImmediatelyFlag(Boolean.valueOf(String.valueOf(rowPeriodic.get("EXECUTE_IMMEDIATELY"))));
            hasSchedule = true;
        }
        col = new Column("Calendar", "SCHEDULE_ID");
        crit = new Criteria(col, (Object)scheduleId, 0);
        dobj = persistence.get("Calendar", crit);
        it = dobj.get("Calendar", "SCHEDULE_ID");
        if (it.hasNext()) {
            details.setTypeOfSchedule("CALENDAR");
            final Row rowCalendar = dobj.getFirstRow("Calendar");
            details.setRepeatFrequency((String)rowCalendar.get("REPEAT_FREQUENCY"));
            details.setTimeOfDay((long)rowCalendar.get("TIME_OF_DAY"));
            details.setUnitOfTime((String)rowCalendar.get("UNIT_OF_TIME"));
            details.setdayOfWeek((int)rowCalendar.get("DAY_OF_WEEK"));
            details.setDateOfMonth((int)rowCalendar.get("DATE_OF_MONTH"));
            details.setWeekOfMonth((long)rowCalendar.get("WEEK"));
            details.setMonthOfYear((int)rowCalendar.get("MONTH_OF_YEAR"));
            details.setYearOfDecade((int)rowCalendar.get("YEAR_OF_DECADE"));
            details.setTimeZone((String)rowCalendar.get("TZ"));
            details.setSkipFrequency((int)rowCalendar.get("SKIP_FREQUENCY"));
            hasSchedule = true;
        }
        if (!hasSchedule) {
            throw new Exception("schedule neither calendar nor periodic");
        }
        if (!isWorkEnginePresent()) {
            col = new Column("ACMailConfig", "CONTEXT_NAME");
            if (details.getMailContextName().equals("null")) {
                crit = new Criteria(col, (Object)scheduleName, 0);
            }
            else {
                crit = new Criteria(col, (Object)details.getMailContextName(), 0);
            }
            dobj = persistence.get("ACMailConfig", crit);
            it = dobj.get("ACMailConfig", "CONTEXT_NAME");
            if (it.hasNext()) {
                final Row rowACMAILCONFIG = dobj.getFirstRow("ACMailConfig");
                details.setFromAddress((String)rowACMAILCONFIG.get("FROMADDRESS"));
                details.setHost((String)rowACMAILCONFIG.get("SERVER"));
                details.setUserName((String)rowACMAILCONFIG.get("USERNAME"));
                details.setPassword((String)rowACMAILCONFIG.get("PASSWORD"));
                details.setPort((int)rowACMAILCONFIG.get("PORT"));
            }
            else if (details.getMailContextName().equals("null")) {
                details.setMailContextName(scheduleName);
            }
        }
        else {
            col = new Column("Mail_Server", "NAME");
            String contextName;
            if (details.getMailContextName().equals("null")) {
                crit = new Criteria(col, (Object)scheduleName, 0);
                contextName = scheduleName;
            }
            else {
                crit = new Criteria(col, (Object)details.getMailContextName(), 0);
                contextName = details.getMailContextName();
            }
            dobj = persistence.get("Mail_Server", crit);
            it = dobj.get("Mail_Server", "NAME");
            col = new Column("ACClientProps", "PARAMNAME");
            crit = new Criteria(col, (Object)(contextName + "_FromAddress"), 0);
            final DataObject dobj2 = persistence.get("ACClientProps", crit);
            final String fromAddress = (String)dobj2.getFirstRow("ACClientProps").get("PARAMVALUE");
            if (it.hasNext()) {
                final Row rowMAIL_SERVER = dobj.getFirstRow("Mail_Server");
                details.setFromAddress(fromAddress);
                details.setHost((String)rowMAIL_SERVER.get("SERVER_NAME"));
                details.setUserName((String)rowMAIL_SERVER.get("USER_NAME"));
                final String password = (String)rowMAIL_SERVER.get("PASSWORD");
                if (password != null) {
                    details.setPassword(AuthUtil.decryptString(password));
                }
                details.setPort((int)rowMAIL_SERVER.get("PORT"));
            }
            else if (details.getMailContextName().equals("null")) {
                details.setMailContextName(scheduleName);
            }
        }
        col = new Column("ACFtpDetails", "SCHEDULE_NAME");
        crit = new Criteria(col, (Object)scheduleName, 0);
        dobj = persistence.get("ACFtpDetails", crit);
        it = dobj.get("ACFtpDetails", "SCHEDULE_NAME");
        if (it.hasNext()) {
            final Row rowACFTPDETAILS = dobj.getFirstRow("ACFtpDetails");
            details.setFtpUserName((String)rowACFTPDETAILS.get("USERNAME"));
            details.setFtpServer((String)rowACFTPDETAILS.get("SERVER"));
            details.setFtpPassword((String)rowACFTPDETAILS.get("PASSWORD"));
            details.setFtpRemoteDirectory((String)rowACFTPDETAILS.get("REMOTE_DIRECTORY"));
        }
        return details;
    }
    
    public static boolean isWorkEnginePresent() {
        boolean isWorkEnginePresent = true;
        try {
            final Row row = new Row("Mail_Server");
        }
        catch (final Exception e) {
            isWorkEnginePresent = false;
        }
        return isWorkEnginePresent;
    }
    
    public static int getFileSize(final String path) {
        final File f = new File(path);
        final long length = f.length();
        final int sizeInKb = (int)(length / 1024L);
        return sizeInKb;
    }
    
    public static boolean isScheduleExists(final String scheduleName) throws Exception {
        final Persistence persistence = LookUpUtil.getPersistence();
        final Column col = new Column("Schedule", "SCHEDULE_NAME");
        final Criteria crit = new Criteria(col, (Object)scheduleName, 0);
        final DataObject dobj = persistence.get("Schedule", crit);
        final Iterator it = dobj.get("Schedule", "SCHEDULE_NAME");
        return it.hasNext();
    }
    
    private static HashMap getParamMap(final Row rowScheduledReports) throws Exception {
        final HashMap paramMap = new HashMap();
        final String paramKeyString = rowScheduledReports.get("PARAMETER_NAMES").toString();
        final String paramValueString = rowScheduledReports.get("PARAMETER_VALUES").toString();
        final String[] keys = paramKeyString.split(",");
        final String[] values = paramValueString.split(",");
        if (keys.length != values.length) {
            ScheduledReportAPI.logger.log(Level.SEVERE, "param keys and values from db are not matching in nos");
            throw new IllegalStateException("param keys and values from db are not matching in nos");
        }
        final int no = keys.length;
        for (int i = 0; i < keys.length; ++i) {
            paramMap.put(keys[i], values[i]);
        }
        return paramMap;
    }
    
    static {
        ScheduledReportAPI.logger = Logger.getLogger(ScheduledReportAPI.class.getName());
    }
}
