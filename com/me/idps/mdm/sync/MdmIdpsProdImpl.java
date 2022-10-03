package com.me.idps.mdm.sync;

import com.me.idps.core.util.IdpsUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Arrays;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.upgrade.AzureOAuth210902;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.simple.JSONObject;
import java.util.List;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQuery;
import java.text.MessageFormat;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.Criteria;
import java.util.Properties;
import java.sql.Connection;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.me.idps.core.sync.product.DirectoryProductAPI;

public abstract class MdmIdpsProdImpl extends DirectoryProductAPI
{
    public static final int AZURE_OP_OAUTH_FIX_BUILD = 101210902;
    public static final int MDM_IDPS_REPO_SPLIT_BUILD = 101211102;
    
    @Override
    public void handleProductOps(final DirProdImplRequest dirProdImplRequest) throws Exception {
        final Object[] args = dirProdImplRequest.args;
        final Long collationID = (Long)args[1];
        final Long aaaUserID = dirProdImplRequest.aaaUserID;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        MDMDirectoryDataPersistor.getInstance().handleProductOps((Connection)args[0], collationID, dmDomainProps, aaaUserID);
    }
    
    @Override
    public void resolveResourceDuplicates(final DirProdImplRequest dirProdImplRequest) throws Exception {
        final Long dmDomainID = (Long)dirProdImplRequest.args[1];
        final Long collationID = (Long)dirProdImplRequest.args[2];
        final Criteria resCri = (Criteria)dirProdImplRequest.args[3];
        final Criteria dirObjRegCri = (Criteria)dirProdImplRequest.args[4];
        DirectoryUserDuplicateHandler.getInstance().handleUserResDuplicates((Connection)dirProdImplRequest.args[0], resCri, dirObjRegCri, dmDomainID, collationID);
        DirectoryGroupDuplicateHandler.getInstance().handleGroupResDuplicates((Connection)dirProdImplRequest.args[0], resCri, dirObjRegCri, dmDomainID, collationID);
    }
    
    @Override
    public void bindRes(final DirProdImplRequest dirProdImplRequest) throws Exception {
        final String BLOCK = (String)dirProdImplRequest.args[3];
        final Long dmDomainID = (Long)dirProdImplRequest.args[1];
        final Long collationID = (Long)dirProdImplRequest.args[2];
        final UpdateQuery updateQuery = MDMDirectoryDataPersistor.getInstance().dirToManagedDeviceRel(dmDomainID, null);
        DirectoryQueryutil.getInstance().executeUpdateQuery((Connection)dirProdImplRequest.args[0], dmDomainID, collationID, updateQuery, BLOCK, MessageFormat.format("binding resource mappings for {0}", this.getClass().getSimpleName()), false);
    }
    
    @Override
    public void unBindDeletedRes(final DirProdImplRequest dirProdImplRequest) throws Exception {
        final String BLOCK = (String)dirProdImplRequest.args[3];
        final Long dmDomainID = (Long)dirProdImplRequest.args[1];
        final Long collationID = (Long)dirProdImplRequest.args[2];
        final UpdateQuery updateQuery = MDMDirectoryDataPersistor.getInstance().dirToManagedDeviceRel(dmDomainID, new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0));
        DirectoryQueryutil.getInstance().executeUpdateQuery((Connection)dirProdImplRequest.args[0], dmDomainID, collationID, updateQuery, BLOCK, MessageFormat.format("unbinding deleted resource for {0}", this.getClass().getSimpleName()), false);
    }
    
    @Override
    public Object customHandling(final DirProdImplRequest dirProdImplRequest) throws Exception {
        final Object[] args = dirProdImplRequest.args;
        final String taskType = (String)args[0];
        if (SyMUtil.isStringEmpty(taskType)) {
            final String s = taskType;
            switch (s) {
                case "CHECK_FOR_CG_CLEANUP": {
                    MDMDirectoryDataPersistor.getInstance().checkForCGcleanUp((Connection)args[1]);
                    break;
                }
                case "DO_CG_CLEAN_UP": {
                    MDMDirectoryDataPersistor.getInstance().cgCleanUp((Long)args[1], (String)args[2], (Long)args[3]);
                    break;
                }
                case "HANDLE_AZURE_USERS_POSTED_AS_CG": {
                    final Integer syncType = (Integer)args[2];
                    if (syncType != null && syncType == 1 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("HANDLE_AZURE_USERS_POSTED_AS_CG")) {
                        MDMDirectoryDataPersistor.getInstance().handleAzureUsersPostedAsGroups((Connection)args[1], (String)args[3], (Long)args[4], (Long)args[5], (Criteria)args[6]);
                        break;
                    }
                    break;
                }
                case "REM_DEFAULT_AZURE_DEVICE": {
                    if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("REM_DEFAULT_AZURE_DEVICE")) {
                        MDMDirectoryDataPersistor.getInstance().remDefaultAzureDevice((Connection)args[1]);
                        MDMFeatureParamsHandler.updateMDMFeatureParameter("REM_DEFAULT_AZURE_DEVICE", String.valueOf(true));
                        break;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    @Override
    public List<String> getProdSpecificMeTrackingKeys(final DirProdImplRequest dirProdImplRequest) {
        return MDMidpsMetricConstants.getTrackingKeys();
    }
    
    @Override
    public JSONObject doProdSpecificMEtracking(final Long customerID, final JSONObject input) {
        final JSONObject mdmMetrics = MDMdirMetricsDataHandler.getInstance().getMDMmetrackingDetails(customerID, input);
        return mdmMetrics;
    }
    
    protected void handleAzureOAuth() throws SyMException {
        final Long[] customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        final AzureOAuth210902 azureOAuthUpgradeHandler = new AzureOAuth210902();
        IDPSlogger.UPGRADE.log(Level.INFO, "handling azure oauth upgrade for following list : {0}", new Object[] { Arrays.toString(customerIDs) });
        for (int index = 0; customerIDs != null && index < customerIDs.length; ++index) {
            final Long customerID = customerIDs[index];
            IDPSlogger.UPGRADE.log(Level.INFO, "handling azure oauth upgrade for : {0}", new Object[] { customerID });
            azureOAuthUpgradeHandler.handleUpgrade(customerID);
        }
    }
    
    @Override
    public void handleUpgrade(final DirProdImplRequest dirProdImplRequest) throws Exception {
        final int idpsBuildNumberBeforeUpgrade = (int)dirProdImplRequest.args[0];
        IDPSlogger.UPGRADE.log(Level.INFO, "idpsBuildNumberBeforeUpgrade : {0}", new Object[] { idpsBuildNumberBeforeUpgrade });
        final String mdmBuildNumberBeforeUpgradeStr = SyMUtil.getSyMParameter("HANDLE_IDPS_MDM_UPGRADE");
        IDPSlogger.UPGRADE.log(Level.INFO, "mdmBuildNumberBeforeUpgradeStr : {0}", new Object[] { mdmBuildNumberBeforeUpgradeStr });
        if (!IdpsUtil.isStringEmpty(mdmBuildNumberBeforeUpgradeStr)) {
            final int mdmBuildNumberBeforeUpgrade = Integer.parseInt(mdmBuildNumberBeforeUpgradeStr);
            if (mdmBuildNumberBeforeUpgrade < 101210902) {
                this.handleAzureOAuth();
            }
            IdpsUtil.deleteSyMParameter("HANDLE_IDPS_MDM_UPGRADE");
        }
    }
}
