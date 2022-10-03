package com.me.mdm.server.apps.AppDelegateScopeManagement;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.Collection;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONObject;
import com.me.mdm.server.device.api.model.apps.AppDelegateScopeModel;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.me.mdm.server.apps.AppFacade;
import java.util.logging.Logger;

public class AppDelegateScopeHandler
{
    protected static Logger logger;
    private AppFacade appFacade;
    private AppConfigDataHandler appConfigDataHandler;
    
    public AppDelegateScopeHandler() {
        this.appFacade = new AppFacade();
        this.appConfigDataHandler = new AppConfigDataHandler();
    }
    
    public JSONObject validateAppDelegateScope(final AppDelegateScopeModel appDelegateScopeModel) throws Exception {
        final Long packageId = appDelegateScopeModel.getAppId();
        final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
        if (2 != platform) {
            throw new APIHTTPException("COM0005", new Object[] { "Irrelevant operation for the package id " + packageId });
        }
        this.appFacade.validateIfAppFound(appDelegateScopeModel.getAppId(), appDelegateScopeModel.getCustomerId());
        this.appFacade.validateIfReleaseLabelFound(appDelegateScopeModel.getLabelId(), appDelegateScopeModel.getCustomerId());
        final Long configDataItem = this.appConfigDataHandler.getAppConfigDataItemId(appDelegateScopeModel.getLabelId(), appDelegateScopeModel.getAppId());
        this.appConfigDataHandler.validateIfAppInTrash(configDataItem);
        final Map<String, Integer> dataMap = appDelegateScopeModel.getDelegateScope();
        final JSONObject jsonObject = this.verifyIfValidDelegateScopes(dataMap);
        appDelegateScopeModel.setConfigDataItemId(configDataItem);
        return jsonObject;
    }
    
    private JSONObject verifyIfValidDelegateScopes(final Map<String, Integer> delegateMap) {
        final JSONObject jsonObject = new JSONObject();
        if (delegateMap.isEmpty() || delegateMap == null) {
            throw new APIHTTPException("COM0009", new Object[] { "Insufficient data field in delegate scope permission(s)" });
        }
        for (final Map.Entry<String, Integer> entry : delegateMap.entrySet()) {
            final String scopeName = entry.getKey();
            final Integer permissionState = Integer.parseInt(String.valueOf(entry.getValue()));
            if (!AppDelegateScopeConstants.DELEGATE_SCOPE_LIST.contains(scopeName.toUpperCase())) {
                throw new APIHTTPException("COM0005", new Object[] { "Incorrect requested field(s) for delegate scope" });
            }
            jsonObject.put(scopeName, (Object)permissionState);
        }
        return jsonObject;
    }
    
    public void constructDelegateScopeConfigJSON(final JSONObject appJSON, final Long configDataItemId) throws DataAccessException {
        final List grantScope = new ArrayList();
        final List deniedScope = new ArrayList();
        final Criteria cri = new Criteria(new Column("AppDelegateScopes", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
        final DataObject dObj = DataAccess.get("AppDelegateScopes", cri);
        final Row row = dObj.getRow("AppDelegateScopes");
        if (row != null) {
            final List list = AppDelegateScopeConstants.DELEGATE_SCOPE_LIST;
            for (int i = 0; i < list.size(); ++i) {
                final String scopeName = list.get(i).toString().toUpperCase();
                final Integer grantState = (Integer)row.get(scopeName);
                if (grantState == 1) {
                    grantScope.add(scopeName.toLowerCase().replace("_", "-"));
                }
                else {
                    deniedScope.add(scopeName.toLowerCase().replace("_", "-"));
                }
            }
        }
        final JSONObject jObj = new JSONObject();
        jObj.put("GrantedScopes", (Collection)grantScope);
        jObj.put("DeniedScopes", (Collection)deniedScope);
        appJSON.put("DelegatedScopes", (Object)jObj);
    }
    
    public Map<String, Object> constructResponseDelegateScope(final Long configDataItem) throws DataAccessException {
        final Criteria criteria = new Criteria(new Column("AppDelegateScopes", "CONFIG_DATA_ITEM_ID"), (Object)configDataItem, 0);
        final DataObject dataObject = SyMUtil.getPersistenceLite().get("AppDelegateScopes", criteria);
        final Map<String, Object> delegateScopeData = new HashMap<String, Object>();
        final Row row = dataObject.getRow("AppDelegateScopes");
        if (row != null) {
            final List dsList = AppDelegateScopeConstants.DELEGATE_SCOPE_LIST;
            for (int i = 0; i < dsList.size(); ++i) {
                final String scopeName = dsList.get(i).toString().toUpperCase();
                final Integer grantState = (Integer)row.get(scopeName);
                delegateScopeData.put(scopeName.toLowerCase(), grantState);
            }
        }
        return delegateScopeData;
    }
    
    static {
        AppDelegateScopeHandler.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
