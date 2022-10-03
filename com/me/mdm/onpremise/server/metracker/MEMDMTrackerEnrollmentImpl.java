package com.me.mdm.onpremise.server.metracker;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.metracker.MEMDMTrackerConstants;

public class MEMDMTrackerEnrollmentImpl extends MEMDMTrackerConstants implements MEDMTracker
{
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerEnrollmentImpl() {
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackerEnrollmentImpl";
    }
    
    public Properties getTrackerProperties() {
        final com.me.mdm.server.metracker.MEMDMTrackerEnrollmentImpl mdmCoreDataCollector = new com.me.mdm.server.metracker.MEMDMTrackerEnrollmentImpl();
        final Properties mdmTrackerProperties = mdmCoreDataCollector.getTrackerProperties();
        if (CustomerInfoUtil.isDC()) {
            ((Hashtable<String, String>)mdmTrackerProperties).put("Mac_Windows_Dc_Mdm_And_Both_Managed_Device_Summary", this.addManagedDcMdmAndBothMacWinDeviceCount());
        }
        return mdmTrackerProperties;
    }
    
    private String addManagedDcMdmAndBothMacWinDeviceCount() {
        final JSONObject deviceCountData = new JSONObject();
        try {
            final SelectQuery mainQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            mainQuery.addJoin(new Join("Resource", "ManagedComputer", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            mainQuery.addJoin(new Join("ManagedComputer", "Computer", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            mainQuery.addJoin(new Join("ManagedComputer", "InvComputer", new String[] { "RESOURCE_ID" }, new String[] { "COMPUTER_ID" }, 1));
            final Criteria dcDeviceStatusCrit = new Criteria(Column.getColumn("ManagedComputer", "MANAGED_STATUS"), (Object)61, 0);
            mainQuery.setCriteria(dcDeviceStatusCrit);
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            subQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            subQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            subQuery.addJoin(new Join("MdDeviceInfo", "InvComputer", new String[] { "SERIAL_NUMBER" }, new String[] { "SERVICETAG" }, 1));
            final Criteria mdmDeviceStatusCrit = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria mdmDeviceTypeCrit = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 3, 4 }, 8);
            subQuery.setCriteria(mdmDeviceStatusCrit.and(mdmDeviceTypeCrit));
            subQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            subQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            subQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            subQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            subQuery.addSelectColumn(Column.getColumn("InvComputer", "SERVICETAG"));
            final String subTableAlias = "subTable";
            final DerivedTable derivedTable = new DerivedTable(subTableAlias, (Query)subQuery);
            final Join derivedTableJoin = new Join(Table.getTable("Resource"), (Table)derivedTable, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            mainQuery.addJoin(derivedTableJoin);
            final Criteria subTableMdmDeviceStatusCrit = new Criteria(Column.getColumn(subTableAlias, "MANAGED_STATUS"), (Object)2, 0);
            mainQuery.setCriteria(mainQuery.getCriteria().or(subTableMdmDeviceStatusCrit));
            final Criteria subTableSerialNumEqalCrit = new Criteria(new Column(subTableAlias, "SERIAL_NUMBER"), (Object)new Column(subTableAlias, "SERVICETAG"), 0);
            final Criteria subTableResIdNotNullCrit = new Criteria(new Column(subTableAlias, "RESOURCE_ID"), (Object)null, 1);
            final Criteria subTableServiceTagNullCrit = new Criteria(new Column(subTableAlias, "SERVICETAG"), (Object)null, 0);
            final Criteria subTableMacPlatformCrit = new Criteria(new Column(subTableAlias, "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria subTableWinPlatformCrit = new Criteria(new Column(subTableAlias, "PLATFORM_TYPE"), (Object)3, 0);
            final Criteria managedCompResIdNotNullCrit = new Criteria(new Column("ManagedComputer", "RESOURCE_ID"), (Object)null, 1);
            final Criteria computerMacPlatformCrit = new Criteria(new Column("Computer", "OS_PLATFORM"), (Object)2, 0);
            final Criteria computerWinPlatformCrit = new Criteria(new Column("Computer", "OS_PLATFORM"), (Object)1, 0);
            final CaseExpression bothMacExp = new CaseExpression("mac_managed_in_both_count");
            bothMacExp.addWhen(subTableSerialNumEqalCrit.and(subTableMacPlatformCrit), (Object)1);
            bothMacExp.elseVal((Object)0);
            mainQuery.addSelectColumn(MDMCoreQuery.getInstance().getSumCaseExpressionColumn(bothMacExp, 4, "mac_managed_in_both_count"));
            final CaseExpression bothWinExp = new CaseExpression("win_managed_in_both_count");
            bothWinExp.addWhen(subTableSerialNumEqalCrit.and(subTableWinPlatformCrit), (Object)1);
            bothWinExp.elseVal((Object)0);
            mainQuery.addSelectColumn(MDMCoreQuery.getInstance().getSumCaseExpressionColumn(bothWinExp, 4, "win_managed_in_both_count"));
            final CaseExpression macMdmOnlyExp = new CaseExpression("mac_managed_in_mdm_only_count");
            macMdmOnlyExp.addWhen(subTableResIdNotNullCrit.and(subTableServiceTagNullCrit).and(subTableMacPlatformCrit), (Object)1);
            macMdmOnlyExp.elseVal((Object)0);
            mainQuery.addSelectColumn(MDMCoreQuery.getInstance().getSumCaseExpressionColumn(macMdmOnlyExp, 4, "mac_managed_in_mdm_only_count"));
            final CaseExpression winMdmOnlyExp = new CaseExpression("win_managed_in_mdm_only_count");
            winMdmOnlyExp.addWhen(subTableResIdNotNullCrit.and(subTableServiceTagNullCrit).and(subTableWinPlatformCrit), (Object)1);
            winMdmOnlyExp.elseVal((Object)0);
            mainQuery.addSelectColumn(MDMCoreQuery.getInstance().getSumCaseExpressionColumn(winMdmOnlyExp, 4, "win_managed_in_mdm_only_count"));
            final CaseExpression macDcExp = new CaseExpression("mac_managed_in_dc_count");
            macDcExp.addWhen(managedCompResIdNotNullCrit.and(computerMacPlatformCrit), (Object)1);
            macDcExp.elseVal((Object)0);
            mainQuery.addSelectColumn(MDMCoreQuery.getInstance().getSumCaseExpressionColumn(macDcExp, 4, "mac_managed_in_dc_count"));
            final CaseExpression winDcExp = new CaseExpression("win_managed_in_dc_count");
            winDcExp.addWhen(managedCompResIdNotNullCrit.and(computerWinPlatformCrit), (Object)1);
            winDcExp.elseVal((Object)0);
            mainQuery.addSelectColumn(MDMCoreQuery.getInstance().getSumCaseExpressionColumn(winDcExp, 4, "win_managed_in_dc_count"));
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)mainQuery);
            while (ds.next()) {
                deviceCountData.put("mac_managed_in_both_count", (Object)ds.getValue("mac_managed_in_both_count"));
                deviceCountData.put("win_managed_in_both_count", (Object)ds.getValue("win_managed_in_both_count"));
                deviceCountData.put("mac_managed_in_mdm_only_count", (Object)ds.getValue("mac_managed_in_mdm_only_count"));
                deviceCountData.put("win_managed_in_mdm_only_count", (Object)ds.getValue("win_managed_in_mdm_only_count"));
                deviceCountData.put("mac_managed_in_dc_count", (Object)ds.getValue("mac_managed_in_dc_count"));
                deviceCountData.put("win_managed_in_dc_count", (Object)ds.getValue("win_managed_in_dc_count"));
            }
            return deviceCountData.toString();
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addManagedDcMdmAndBothMacWinDeviceCount", "Exception : ", (Throwable)e);
            SyMLogger.info(this.logger, this.sourceClass, "addManagedDcMdmAndBothMacWinDeviceCount", "Onpremise device managed Details Summary : " + deviceCountData.toString());
            return deviceCountData.toString();
        }
    }
    
    public class OnPremiseConstants
    {
        public static final int MANAGED_BY_DC = 61;
        public static final int MAC_OS_PLATFORM_DC = 2;
        public static final int WINDOWS_OS_PLATFORM_DC = 1;
    }
}
