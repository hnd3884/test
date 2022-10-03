package com.me.mdm.server.profiles.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.apps.provisioningprofiles.ProvisioningProfilesDataHandler;
import java.util.Collection;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class MacSystemExtensionConfigHandler extends DefaultConfigHandler
{
    public static final Integer TEAM_ID_WHITELIST_TYPE;
    public static final Integer KERNEL_EXT_WHITELIST_TYPE;
    public static final String DRIVER_EXTENSION = "DriverExtension";
    public static final String NETWORK_EXTENSION = "NetworkExtension";
    public static final String ENDPOINT_SECURITY_EXTENSION = "EndpointSecurityExtension";
    public static final String PROVISIONING_PROFILE = "provisioning_profile";
    public static final Integer KERNEL_EXT_PERMISSION_PAYLOAD_TYPE;
    public static final Integer SYSTEM_EXT_PERMISSION_PAYLOAD_TYPE;
    
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            final JSONArray result = super.DOToAPIJSON(dataObject, configName);
            if (!dataObject.isEmpty()) {
                for (int i = 0; i < result.length(); ++i) {
                    final JSONObject payload = result.getJSONObject(i);
                    final Long payloadID = payload.getLong("payload_id");
                    final Criteria policyCriteria = new Criteria(Column.getColumn("MacSystemExtnPolicy", "CONFIG_DATA_ITEM_ID"), (Object)payloadID, 0);
                    final Row policyRow = dataObject.getRow("MacSystemExtnPolicy", policyCriteria);
                    final Long extensionID = (Long)policyRow.get("EXTENSION_POLICY_ID");
                    final Criteria configCriteria = new Criteria(Column.getColumn("MacSystemExtnConfig", "EXTENSION_POLICY_ID"), (Object)extensionID, 0);
                    final Row configRow = dataObject.getRow("MacSystemExtnConfig", configCriteria);
                    payload.put("ALLOW_USER_OVERRIDE", configRow.get("ALLOW_USER_OVERRIDE"));
                    final Iterator<Row> iterator = dataObject.getRows("MacSystemExtnPreference", new Criteria(Column.getColumn("MacSystemExtnPreference", "EXTENSION_POLICY_ID"), (Object)extensionID, 0));
                    final JSONArray extensions = new JSONArray();
                    while (iterator.hasNext()) {
                        final Row preferenceRow = iterator.next();
                        final JSONObject extension = new JSONObject();
                        final Long provID = (Long)preferenceRow.get("PROV_ID");
                        extension.put("EXTENSION_WHITELIST_TYPE", preferenceRow.get("EXTENSION_WHITELIST_TYPE"));
                        extension.put("PROV_ID", (Object)provID);
                        extension.put("ALLOWED_EXTENSIONS", (Object)new JSONArray((Collection)getSystemExtensionTypes((Integer)preferenceRow.get("ALLOWED_EXTENSIONS"))));
                        extension.put("provisioning_profile", (Object)new ProvisioningProfilesDataHandler().getAppleProvProfilesDetails(dataObject, provID));
                        extensions.put((Object)extension);
                    }
                    payload.put("EXTENSIONS", (Object)extensions);
                }
            }
            return result;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred while converting DO To APIJSON", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static List getSystemExtensionTypes(final Integer extension) {
        final List<String> permissionNames = new ArrayList<String>();
        final String binary = Integer.toBinaryString(extension);
        final int length = binary.length();
        if (binary.charAt(length - 1) == '1') {
            permissionNames.add("DriverExtension");
        }
        if (binary.length() > 1 && binary.charAt(length - 2) == '1') {
            permissionNames.add("NetworkExtension");
        }
        if (binary.length() > 2 && binary.charAt(length - 3) == '1') {
            permissionNames.add("EndpointSecurityExtension");
        }
        return permissionNames;
    }
    
    static {
        TEAM_ID_WHITELIST_TYPE = 1;
        KERNEL_EXT_WHITELIST_TYPE = 2;
        KERNEL_EXT_PERMISSION_PAYLOAD_TYPE = 0;
        SYSTEM_EXT_PERMISSION_PAYLOAD_TYPE = 1;
    }
}
