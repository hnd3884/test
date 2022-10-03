package com.adventnet.sym.webclient.mdm.config.formbean;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class WebClipsPolicyFormBean extends MDMDefaultFormBean
{
    private Logger logger;
    
    public WebClipsPolicyFormBean() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Long parentCustomerId = CloneGlobalConfigHandler.getInstance().getCustomerId(configDOFromDB);
        final Long childCustomerId = CloneGlobalConfigHandler.getInstance().getCustomerId(cloneConfigDO);
        if (!parentCustomerId.equals(childCustomerId)) {
            try {
                this.cloneWebClipsPolicies(configID, configDOFromDB, cloneConfigDO);
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in cloneConfigDO of WebClipsPolicyFormBean", ex);
            }
        }
    }
    
    private HashMap getParentChildWebClipUVHMap(final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        final HashMap<Long, Object> parentChildUVHMap = new HashMap<Long, Object>();
        final Long customerId = CloneGlobalConfigHandler.getInstance().getCustomerId(cloneConfigDO);
        final Iterator<Row> iterator = configDOFromDB.getRows("WebClipPolicies");
        while (iterator.hasNext()) {
            final Row parentRow = iterator.next();
            final Row childRow = new Row("WebClipPolicies");
            childRow.set("WEBCLIP_NAME", parentRow.get("WEBCLIP_NAME"));
            childRow.set("ICON_FILE_NAME", parentRow.get("ICON_FILE_NAME"));
            childRow.set("ALLOW_CLEAR_COOKIE", parentRow.get("ALLOW_CLEAR_COOKIE"));
            childRow.set("ALLOW_FULL_SCREEN", parentRow.get("ALLOW_FULL_SCREEN"));
            childRow.set("CREATE_HOMESCREEN_SHORTCUT", parentRow.get("CREATE_HOMESCREEN_SHORTCUT"));
            childRow.set("CUSTOMER_ID", (Object)customerId);
            childRow.set("IS_REMOVAL", parentRow.get("IS_REMOVAL"));
            childRow.set("REFRESH_MODE", parentRow.get("REFRESH_MODE"));
            childRow.set("SCREEN_ORIENTATION_OPTION", parentRow.get("SCREEN_ORIENTATION_OPTION"));
            childRow.set("USE_PRECOMP_ICON", parentRow.get("USE_PRECOMP_ICON"));
            childRow.set("WEBCLIP_LABEL", parentRow.get("WEBCLIP_LABEL"));
            childRow.set("WEBCLIP_URL", parentRow.get("WEBCLIP_URL"));
            cloneConfigDO.addRow(childRow);
            parentChildUVHMap.put((Long)parentRow.get("WEBCLIP_POLICY_ID"), childRow.get("WEBCLIP_POLICY_ID"));
        }
        return parentChildUVHMap;
    }
    
    HashMap cloneWebClipsPolicies(final Integer configId, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        if (cloneConfigDO.containsTable("WebClipToConfigRel")) {
            final HashMap<Long, Object> parentChildWebClipsUVH = this.getParentChildWebClipUVHMap(configDOFromDB, cloneConfigDO);
            final Object configDataItem = CloneGlobalConfigHandler.getInstance().getConfigDataItemId(configId, cloneConfigDO);
            final Iterator<Row> iterator = cloneConfigDO.getRows("WebClipToConfigRel", new Criteria(Column.getColumn("WebClipToConfigRel", "CONFIG_DATA_ITEM_ID"), configDataItem, 0));
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long parentWebClipPolicyId = (Long)row.get("WEBCLIP_POLICY_ID");
                row.set("WEBCLIP_POLICY_ID", parentChildWebClipsUVH.get(parentWebClipPolicyId));
                cloneConfigDO.updateRow(row);
            }
            return parentChildWebClipsUVH;
        }
        return new HashMap();
    }
}
