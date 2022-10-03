package com.me.mdm.server.apps.businessstore;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.server.apps.AppTrashModeHandler;
import org.json.JSONObject;
import com.me.mdm.server.apps.ios.vpp.IOSVPPEnterpriseBusinessStore;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import com.me.mdm.server.apps.android.afw.AdvGooglePlayEnterpriseBusinessStore;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.apps.windows.WindowsEnterpriseBusinessStore;
import com.adventnet.persistence.WritableDataObject;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;

public abstract class BaseEnterpriseBusinessStore implements EnterpriseBusinessStore
{
    protected int platformType;
    protected int serviceType;
    protected Long businessStoreID;
    protected Long customerID;
    protected String defaultCategory;
    public String storeSyncKey;
    public DataObject pkgToAppDO;
    private static Logger logger;
    protected static Logger mdmLogger;
    protected static Logger bslogger;
    
    public BaseEnterpriseBusinessStore(final Long businessStoreID, final Long customerID) {
        this.pkgToAppDO = (DataObject)new WritableDataObject();
        this.businessStoreID = businessStoreID;
        this.customerID = customerID;
    }
    
    public BaseEnterpriseBusinessStore() {
        this.pkgToAppDO = (DataObject)new WritableDataObject();
    }
    
    public static EnterpriseBusinessStore getInstance(final int platformType, final Long businessStoreID, final Long customerID) {
        EnterpriseBusinessStore enterpriseBusinessStore = null;
        if (platformType == 3) {
            enterpriseBusinessStore = new WindowsEnterpriseBusinessStore();
        }
        else if (platformType == 2) {
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableAndroidDeviceAppPolicy")) {
                enterpriseBusinessStore = new AdvGooglePlayEnterpriseBusinessStore(businessStoreID, customerID);
            }
            else {
                enterpriseBusinessStore = new GooglePlayEnterpriseBusinessStore();
            }
        }
        else if (platformType == 1) {
            enterpriseBusinessStore = new IOSVPPEnterpriseBusinessStore(businessStoreID, customerID);
        }
        return enterpriseBusinessStore;
    }
    
    @Override
    public JSONObject processAppData(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    public Object getSyncStoreStatus(final JSONObject jsonObject) throws Exception {
        jsonObject.put("trashCount", new AppTrashModeHandler().getAccountAppsInTrash(this.platformType, this.customerID));
        return jsonObject;
    }
    
    public void setStoreAppsPkgToAppDO(final Criteria criteria) throws DataAccessException {
        final SelectQuery sq = MDMAppMgmtHandler.getInstance().getMDPackageAppDataQuery();
        sq.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria customerC = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)this.customerID, 0);
        final Criteria platformC = new Criteria(new Column("MdPackage", "PLATFORM_TYPE"), (Object)this.platformType, 0);
        final Criteria pkgTypeC = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)new Integer[] { 0, 1 }, 8);
        Criteria c = null;
        if (sq.getCriteria() != null) {
            c = sq.getCriteria();
        }
        if (c == null) {
            c = customerC.and(platformC).and(pkgTypeC);
        }
        else {
            c = c.and(customerC.and(platformC).and(pkgTypeC));
        }
        if (criteria != null) {
            c = c.and(criteria);
        }
        sq.setCriteria(c);
        sq.addSelectColumn(Column.getColumn("MdPackage", "*"));
        sq.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
        sq.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
        sq.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        sq.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        this.pkgToAppDO = MDMUtil.getPersistence().get(sq);
    }
    
    @Override
    public boolean isAccountActive() throws Exception {
        return true;
    }
    
    static {
        BaseEnterpriseBusinessStore.logger = Logger.getLogger("MDMAppMgmtLogger");
        BaseEnterpriseBusinessStore.mdmLogger = Logger.getLogger("MDMLogger");
        BaseEnterpriseBusinessStore.bslogger = Logger.getLogger("MDMBStoreLogger");
    }
}
