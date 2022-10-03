package com.adventnet.sym.server.medc;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.adventnet.sym.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;

public class MEDCTrackerI18NImpl implements MEDMTracker
{
    private Properties i18nTrackerProperties;
    private Logger logger;
    private String sourceClass;
    int totalNonEngUserCout;
    int japaneseUserCount;
    int chineseUserCount;
    int germanUserCount;
    int englishUserCount;
    int frenchUserCount;
    
    public MEDCTrackerI18NImpl() {
        this.i18nTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCI18NImpl";
        this.totalNonEngUserCout = 0;
        this.japaneseUserCount = 0;
        this.chineseUserCount = 0;
        this.germanUserCount = 0;
        this.englishUserCount = 0;
        this.frenchUserCount = 0;
    }
    
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "I18N implementation starts...");
        return this.getI18NTrackerProperties();
    }
    
    private Properties getI18NTrackerProperties() {
        this.updateI18NProperties();
        return this.i18nTrackerProperties;
    }
    
    private void updateI18NProperties() {
        try {
            if (!this.i18nTrackerProperties.isEmpty()) {
                this.i18nTrackerProperties = new Properties();
            }
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.i18nTrackerProperties);
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "I18N implementation ends...");
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "i18nTrackerProperties", "Exception : ", (Throwable)e);
        }
    }
    
    private void trackI18NUserDetails() {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            selectQuery.addJoin(new Join("AaaUserProfile", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addSelectColumn(new Column("AaaUserProfile", "*"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            this.totalNonEngUserCout = METrackerUtil.getDOSize(dataObject, "AaaLogin");
            Criteria criteria = new Criteria(new Column("AaaUserProfile", "LANGUAGE_CODE"), (Object)"ja", 0);
            this.japaneseUserCount = METrackerUtil.getDOSize(dataObject, "AaaLogin", criteria);
            criteria = new Criteria(new Column("AaaUserProfile", "LANGUAGE_CODE"), (Object)"zh", 0);
            this.chineseUserCount = METrackerUtil.getDOSize(dataObject, "AaaLogin", criteria);
            this.englishUserCount = this.totalNonEngUserCout - (this.japaneseUserCount + this.chineseUserCount);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "trackI18NUserDetails", "Exception : ", (Throwable)e);
        }
    }
}
