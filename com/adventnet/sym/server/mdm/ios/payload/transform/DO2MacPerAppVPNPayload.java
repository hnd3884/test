package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.MacPerAppMappingPolicyPayload;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;

public class DO2MacPerAppVPNPayload extends IOSPayload
{
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] perAppVpnPayload = { null };
        try {
            final MacPerAppMappingPolicyPayload perVPNPayload = new MacPerAppMappingPolicyPayload(1, "MDM", "com.apple.vpn.managed.appmapping", "Mac Per-app VPN app mapping Configuration");
            final Iterator iterator = dataObject.getRows("VpnPolicy");
            while (iterator.hasNext()) {
                final Row vpnRow = iterator.next();
                final String vpnUUID = (String)vpnRow.get("VPNUUID");
                final Iterator appIterator = dataObject.getRows("AppLockPolicyApps");
                while (appIterator.hasNext()) {
                    final Row policyApps = appIterator.next();
                    final Long appGroupId = (Long)policyApps.get("APP_GROUP_ID");
                    final Criteria appGroupCriteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
                    final Row appGroupDetailRow = dataObject.getRow("MdAppGroupDetails", appGroupCriteria);
                    final String bundleIdentifier = (String)appGroupDetailRow.get("IDENTIFIER");
                    final Row macAppRow = dataObject.getRow("MacAppProperties", appGroupCriteria);
                    final String designatedRequirement = (String)macAppRow.get("CODE_REQUIREMENT");
                    final String codeSignature = (String)macAppRow.get("SIGNING_IDENTIFIER");
                    perVPNPayload.createAppDictionary(bundleIdentifier, designatedRequirement, codeSignature, vpnUUID);
                }
                perVPNPayload.addAppMapping();
            }
            perAppVpnPayload[0] = perVPNPayload;
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in creating the mac per app vpn", e);
        }
        return perAppVpnPayload;
    }
}
