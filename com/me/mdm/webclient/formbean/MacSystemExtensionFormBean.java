package com.me.mdm.webclient.formbean;

import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.profiles.config.MacSystemExtensionConfigHandler;
import com.me.mdm.server.apps.provisioningprofiles.ProvisioningProfilesDataHandler;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MacSystemExtensionFormBean extends MDMDefaultFormBean
{
    public static Logger logger;
    
    @Override
    public DataObject getDataObject(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        this.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
        return dataObject;
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        try {
            for (int executionOrder = dynaActionForm.length, i = 0; i < executionOrder; ++i) {
                final JSONObject payload = dynaActionForm[i];
                final Boolean allowUserOverride = payload.optBoolean("ALLOW_USER_OVERRIDE", (boolean)Boolean.FALSE);
                final JSONArray extensions = payload.getJSONArray("EXTENSIONS");
                final Object extensionID = MDMDBUtil.updateRow(dataObject, "MacSystemExtnConfig", new Object[][] { { "ALLOW_USER_OVERRIDE", allowUserOverride } }).get("EXTENSION_POLICY_ID");
                this.addExtensions(dataObject, extensions, extensionID);
                payload.put("EXTENSION_POLICY_ID", extensionID);
                dynaActionForm[i] = payload;
            }
            super.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
        }
        catch (final Exception e) {
            MacSystemExtensionFormBean.logger.log(Level.SEVERE, "Exception occured at System Extension Form Bean", e);
            throw new SyMException(1002, e.getCause());
        }
    }
    
    private void deleteExistingPreferences(final DataObject dataObject, final Long extensionID) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("MacSystemExtnPreference", "EXTENSION_POLICY_ID"), (Object)extensionID, 0);
        dataObject.deleteRows("MacSystemExtnPreference", criteria);
    }
    
    private void addExtensions(final DataObject dataObject, final JSONArray extensions, final Object extensionID) throws Exception {
        try {
            if (!(extensionID instanceof UniqueValueHolder)) {
                this.deleteExistingPreferences(dataObject, (Long)extensionID);
            }
            for (int i = 0; i < extensions.length(); ++i) {
                final JSONObject extension = extensions.getJSONObject(i);
                Long provID = extension.optLong("PROV_ID", -1L);
                provID = ((provID == -1L) ? null : provID);
                if (provID == null) {
                    final Long collectionID = (Long)dataObject.getFirstRow("CfgDataToCollection").get("COLLECTION_ID");
                    final Long customerID = (Long)MDMDBUtil.getFirstRow("CollnToCustomerRel", new Object[][] { { "COLLECTION_ID", collectionID } }).get("CUSTOMER_ID");
                    provID = this.generateProvIDFromProvProfileDict(extension, customerID);
                }
                final Boolean isProvAppIDEmpty = ProvisioningProfilesDataHandler.isProvAppIDEmpty(provID);
                final Integer whiteListType = isProvAppIDEmpty ? MacSystemExtensionConfigHandler.TEAM_ID_WHITELIST_TYPE : MacSystemExtensionConfigHandler.KERNEL_EXT_WHITELIST_TYPE;
                JSONArray allowedExtensions = new JSONArray();
                if (extension.has("ALLOWED_EXTENSIONS")) {
                    allowedExtensions = extension.getJSONArray("ALLOWED_EXTENSIONS");
                }
                MDMDBUtil.updateRow(dataObject, "MacSystemExtnPreference", new Object[][] { { "EXTENSION_POLICY_ID", extensionID }, { "PROV_ID", provID }, { "EXTENSION_WHITELIST_TYPE", whiteListType }, { "ALLOWED_EXTENSIONS", this.getBitWiseForArray(JSONUtil.getInstance().convertStringJSONArrayTOList(allowedExtensions)) } });
            }
        }
        catch (final Exception e) {
            MacSystemExtensionFormBean.logger.log(Level.SEVERE, "Failed to add System extension payload", e);
            throw e;
        }
    }
    
    private Long generateProvIDFromProvProfileDict(final JSONObject extension, final Long customerID) throws Exception {
        final JSONObject provDict = extension.getJSONObject("provisioning_profile");
        provDict.put("CUSTOMER_ID", (Object)customerID);
        return new ProvisioningProfilesDataHandler().addOrUpdateAppleProvProfiles(provDict).getLong("PROV_ID");
    }
    
    private Integer getBitWiseForArray(final List permissions) {
        final String firstBit = permissions.contains("DriverExtension") ? "1" : "0";
        final String secondBit = permissions.contains("NetworkExtension") ? "1" : "0";
        final String thirdBit = permissions.contains("EndpointSecurityExtension") ? "1" : "0";
        final String binary = thirdBit + secondBit + firstBit;
        return Integer.parseInt(binary, 2);
    }
    
    static {
        MacSystemExtensionFormBean.logger = Logger.getLogger("MDMConfigLogger");
    }
}
