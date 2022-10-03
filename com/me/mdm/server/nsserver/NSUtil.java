package com.me.mdm.server.nsserver;

import com.me.devicemanagement.framework.winaccess.WinAccessProvider;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.net.ServerSocket;
import java.util.Enumeration;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Properties;
import com.me.devicemanagement.onpremise.start.util.NSStartUpUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class NSUtil
{
    private Logger logger;
    private static NSUtil nsutil;
    private boolean isNSCompEnabledStatus;
    private boolean isNSEnabledStatus;
    public static final Integer NS_STATUS_STARTED;
    public static final Integer NS_STATUS_STOPPED;
    public static final Integer NS_STATUS_PORT_EXP;
    public static final String SHOW_NS_PORT_FIREWALL_EXCEPTION = "SHOW_NS_PORT_FIREWALL_EXCEPTION";
    
    public NSUtil() {
        this.logger = Logger.getLogger("NSControllerLogger");
        this.isNSCompEnabledStatus = true;
        this.isNSEnabledStatus = true;
    }
    
    public static NSUtil getInstance() {
        if (NSUtil.nsutil == null) {
            NSUtil.nsutil = new NSUtil();
        }
        return NSUtil.nsutil;
    }
    
    public Boolean isMDMPServerImproperShutdown() {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCServerUptimeHistory"));
            query.addSelectColumn(new Column("DCServerUptimeHistory", "DC_UPTIME_RECORD_ID"));
            query.addSelectColumn(new Column("DCServerUptimeHistory", "START_TIME"));
            query.addSelectColumn(new Column("DCServerUptimeHistory", "SHUTDOWN_TIME"));
            final SortColumn sortLastStartTime = new SortColumn(Column.getColumn("DCServerUptimeHistory", "START_TIME"), false);
            query.addSortColumn(sortLastStartTime);
            final Range range = new Range(2, 1);
            query.setRange(range);
            final DataObject existingDO = SyMUtil.getPersistence().get(query);
            Row historyRow = null;
            if (existingDO.isEmpty()) {
                return Boolean.FALSE;
            }
            historyRow = existingDO.getFirstRow("DCServerUptimeHistory");
            final Long shutdownTime = (Long)historyRow.get("SHUTDOWN_TIME");
            if (shutdownTime < 0L) {
                return Boolean.TRUE;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in isMDMPServerImproperShutdown.", exp);
        }
        return Boolean.FALSE;
    }
    
    public void resetNSStatus() {
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (!existingDO.isEmpty()) {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
                final Boolean b = (Boolean)nsdetailrow.get("IS_NS_ENABLED");
                if (!b) {
                    nsdetailrow.set("IS_NS_ENABLED", (Object)true);
                    existingDO.updateRow(nsdetailrow);
                    SyMUtil.getPersistence().update(existingDO);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while reset the NS status.", exp);
        }
    }
    
    public void updateNSCompEnableStatusInCache() {
        final boolean isNSCompEnabled = this.getNSCompStatusFromDB();
        this.isNSCompEnabledStatus = isNSCompEnabled;
        if (isNSCompEnabled) {
            ApiFactoryProvider.getCacheAccessAPI().putCache("IS_NS_COMPONENT_ENABLED", (Object)Boolean.TRUE);
        }
        else {
            ApiFactoryProvider.getCacheAccessAPI().putCache("IS_NS_COMPONENT_ENABLED", (Object)Boolean.FALSE);
        }
        this.logger.log(Level.INFO, "Updating the NSDetails flag (ns comp) in Cache : {0}", isNSCompEnabled);
    }
    
    public boolean getNSCompStatusFromDB() {
        boolean isNSCompEnabled = true;
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (existingDO.isEmpty()) {
                final String nsCurrentPort = NSStartUpUtil.getNSPort();
                nsdetailrow = new Row("NSDetails");
                nsdetailrow.set("NS_PORT", (Object)Integer.valueOf(nsCurrentPort));
                existingDO.addRow(nsdetailrow);
                SyMUtil.getPersistence().add(existingDO);
            }
            else {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
                final Boolean b = (Boolean)nsdetailrow.get("IS_NS_COMPONENT_ENABLED");
                isNSCompEnabled = b;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while fetching the IS_NS_COMPONENT_ENABLED flag.", exp);
        }
        return isNSCompEnabled;
    }
    
    public void updateNSEnableStatusInCache() {
        final boolean isNSEnabled = this.getNSEnabledStatusFromDB();
        this.isNSEnabledStatus = isNSEnabled;
        if (isNSEnabled) {
            ApiFactoryProvider.getCacheAccessAPI().putCache("IS_NS_ENABLED", (Object)Boolean.TRUE);
        }
        else {
            ApiFactoryProvider.getCacheAccessAPI().putCache("IS_NS_ENABLED", (Object)Boolean.FALSE);
        }
        this.logger.log(Level.INFO, "Updating the NSDetails flag in Cache : {0}", isNSEnabled);
    }
    
    public boolean isNSEnabled() {
        return this.isNSEnabledStatus;
    }
    
    public boolean getNSEnabledStatusFromDB() {
        boolean isNSEnabled = false;
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (!existingDO.isEmpty()) {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
                final Boolean b = (Boolean)nsdetailrow.get("IS_NS_ENABLED");
                isNSEnabled = b;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while fetching the NSEnabled flag.", exp);
        }
        return isNSEnabled;
    }
    
    public boolean isNSComponentEnabled() {
        return this.isNSCompEnabledStatus;
    }
    
    public void disableNS() throws SyMException {
        final Properties nsprop = new Properties();
        nsprop.setProperty("IS_NS_ENABLED", Boolean.FALSE.toString());
        this.addOrUpdateNSDetails(nsprop);
        this.updateNSEnableStatusInCache();
    }
    
    public void addOrUpdateNSDetails(final Properties nsdetailsprop) throws SyMException {
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (existingDO.isEmpty()) {
                nsdetailrow = new Row("NSDetails");
                existingDO.addRow(nsdetailrow);
            }
            else {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
            }
            final Enumeration enumerator = nsdetailsprop.keys();
            while (enumerator.hasMoreElements()) {
                final String colName = enumerator.nextElement();
                if (colName.equalsIgnoreCase("NS_MONITORING_INTERVAL") || colName.equalsIgnoreCase("NS_MAX_FAILURE_COUNT") || colName.equalsIgnoreCase("NS_STATUS") || colName.equalsIgnoreCase("NS_PORT")) {
                    final Integer colValue = new Integer(nsdetailsprop.getProperty(colName));
                    nsdetailrow.set(colName, (Object)colValue);
                }
                else if (colName.equalsIgnoreCase("IS_NS_ENABLED")) {
                    final Boolean colValue2 = Boolean.valueOf(nsdetailsprop.getProperty(colName));
                    nsdetailrow.set(colName, (Object)colValue2);
                }
                else {
                    final Object colValue3 = nsdetailsprop.getProperty(colName);
                    nsdetailrow.set(colName, colValue3);
                }
            }
            existingDO.updateRow(nsdetailrow);
            SyMUtil.getPersistence().update(existingDO);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while updating NSDetails table ", exp);
            throw new SyMException(1002, (Throwable)exp);
        }
    }
    
    public int getNSPort() {
        int nsPort = 0;
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (!existingDO.isEmpty()) {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
                nsPort = (int)nsdetailrow.get("NS_PORT");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while fetching the NSPort value.", exp);
        }
        return nsPort;
    }
    
    public long getNSMonitoringInterval() {
        long nsMonitoringInterval = 0L;
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (!existingDO.isEmpty()) {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
                nsMonitoringInterval = (long)nsdetailrow.get("NS_MONITORING_INTERVAL");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while fetching the NSMonitoringInterval value.", exp);
        }
        return nsMonitoringInterval;
    }
    
    public int getMaxNSFailureCount() {
        int maxFailureCount = 0;
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (!existingDO.isEmpty()) {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
                final Integer b = (Integer)nsdetailrow.get("NS_MAX_FAILURE_COUNT");
                maxFailureCount = b;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while fetching the NSMaxFailureCount value.", exp);
        }
        return maxFailureCount;
    }
    
    public void enableNS() throws SyMException {
        final Properties nsprop = new Properties();
        nsprop.setProperty("IS_NS_ENABLED", Boolean.TRUE.toString());
        this.addOrUpdateNSDetails(nsprop);
        this.updateNSEnableStatusInCache();
    }
    
    public Integer getNSStatus() {
        int nsStatus = 0;
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (!existingDO.isEmpty()) {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
                final Integer status = (Integer)nsdetailrow.get("NS_STATUS");
                nsStatus = status;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while fetching the NSEnabled flag.", exp);
        }
        return nsStatus;
    }
    
    public int getNSPortStatus(final int portNumber) {
        int portstatus = -1;
        try {
            final ServerSocket sock = new ServerSocket(portNumber);
            sock.close();
        }
        catch (final Exception exp) {
            portstatus = 1;
            this.logger.log(Level.INFO, "Exception occurred while checking the port availability", exp);
        }
        return portstatus;
    }
    
    public void updateNSStatus(final Integer nsstatus, final int portNumber) {
        try {
            final Properties nsdetailsprop = new Properties();
            final String portNumberStr = Integer.toString(portNumber);
            nsdetailsprop.setProperty("NS_PORT", portNumberStr);
            nsdetailsprop.setProperty("NS_STATUS", nsstatus.toString());
            this.logger.log(Level.INFO, "NS Status= : {0}", nsdetailsprop);
            getInstance().addOrUpdateNSDetails(nsdetailsprop);
            getInstance().getNSFirewallPortDialogStatus();
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while updating NS status", exp);
        }
    }
    
    public int getNSMaxPortCheckRetryCountFromDB() {
        int startRetryCount = 0;
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (!existingDO.isEmpty()) {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
                final Integer count = (Integer)nsdetailrow.get("NS_PORT_CHECK_RETRY_COUNT");
                startRetryCount = count;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while fetching the NSEnabled flag.", exp);
        }
        return startRetryCount;
    }
    
    public long getNsPortCheckRetryIntervalFromDB() {
        long startRetryInterval = 0L;
        try {
            final DataObject existingDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            Row nsdetailrow = null;
            if (!existingDO.isEmpty()) {
                nsdetailrow = existingDO.getFirstRow("NSDetails");
                final Long count = (Long)nsdetailrow.get("NS_PORT_CHECK_RETRY_INTERVAL");
                startRetryInterval = count;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while fetching the NSEnabled flag.", exp);
        }
        return startRetryInterval;
    }
    
    public void generateNSEchoSettingsConf() throws SyMException {
        try {
            final String confFile = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "dcnsdbsettings.conf";
            final DataObject nsDetailsDO = SyMUtil.getPersistence().get("NSDetails", (Criteria)null);
            if (!nsDetailsDO.isEmpty()) {
                final Row nsDetailRow = nsDetailsDO.getRow("NSDetails");
                if (nsDetailRow != null) {
                    this.logger.log(Level.INFO, "Save the NS echo settings in conf file ");
                    final Properties props = new Properties();
                    props.setProperty("ns.lanTimeout", nsDetailRow.get("LAN_RE_REGISTER_TIMEOUT").toString());
                    props.setProperty("ns.wanTimeout", nsDetailRow.get("WAN_RE_REGISTER_TIMEOUT").toString());
                    props.setProperty("ns.echoTimeout", nsDetailRow.get("NS_ECHO_TIMEOUT").toString());
                    props.setProperty("ns.echoLimit", nsDetailRow.get("NS_ECHO_LIMIT").toString());
                    if (Boolean.valueOf(nsDetailRow.get("IS_NS_ECHO_ENABLED").toString())) {
                        props.setProperty("ns.echoEnable", "1");
                    }
                    else {
                        props.setProperty("ns.echoEnable", "0");
                    }
                    FileAccessUtil.storeProperties(props, confFile, true);
                }
                else {
                    this.logger.log(Level.INFO, "Empty row detected while save the NS echo settings. ");
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while save the NS echo settings : ", e);
            throw new SyMException(1002, (Throwable)e);
        }
    }
    
    public boolean getNSFirewallPortDialogStatus() {
        boolean showDialog = false;
        final String showDialogStr = SyMUtil.getSyMParameter("SHOW_NS_PORT_FIREWALL_EXCEPTION");
        if (showDialogStr == null) {
            showDialog = this.updateNSFirewallPortStatus();
        }
        else if (showDialogStr != null && !showDialogStr.equals("")) {
            showDialog = Boolean.parseBoolean(showDialogStr);
        }
        return showDialog;
    }
    
    public boolean updateNSFirewallPortStatus() {
        boolean nsPortStatus = true;
        try {
            final int nsPort = this.getNSPort();
            nsPortStatus = WinAccessProvider.getInstance().isFirewallEnabledInDCServer((long)nsPort);
            if (nsPortStatus) {
                this.logger.log(Level.INFO, "Notification Server Port ( {0} ) is blocked in Firewall.", nsPort);
                SyMUtil.updateSyMParameter("SHOW_NS_PORT_FIREWALL_EXCEPTION", String.valueOf(Boolean.TRUE));
            }
            else {
                this.logger.log(Level.INFO, "Notification Server Port ( {0} ) is opened in Firewall.", nsPort);
                SyMUtil.updateSyMParameter("SHOW_NS_PORT_FIREWALL_EXCEPTION", String.valueOf(Boolean.FALSE));
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while updating the NSFirewallPortStatus.", exp);
        }
        return nsPortStatus;
    }
    
    static {
        NSUtil.nsutil = null;
        NS_STATUS_STARTED = new Integer(1);
        NS_STATUS_STOPPED = new Integer(2);
        NS_STATUS_PORT_EXP = new Integer(3);
    }
}
