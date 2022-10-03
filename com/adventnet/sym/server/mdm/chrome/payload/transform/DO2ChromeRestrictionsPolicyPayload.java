package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeRestrictionsPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeRestrictionsPolicyPayload implements DO2ChromePayload
{
    @Override
    public ChromeRestrictionsPayload createPayload(final DataObject dataObject) {
        ChromeRestrictionsPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("ChromeRestrictionPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new ChromeRestrictionsPayload("1.0", "Restrictions", "Restrictions");
                final Integer guestMode = (Integer)row.get("GUEST_MODE");
                final Integer ephemeralMode = (Integer)row.get("EPHEREMAL_MODE");
                final Integer forcedReEnrollment = (Integer)row.get("FORCED_REENROLLMENT");
                final Integer redirectToSAML = (Integer)row.get("REDIRECT_TO_SAML");
                final Integer transferSAMLCookies = (Integer)row.get("TRANSFER_SAML_COOKIES");
                final String allowedUsersSignin = (String)row.get("ALLOWED_USERS_TO_SIGNIN");
                final String autoCompleteDomain = (String)row.get("AUTOCOMPLETE_DOMAIN_NAME");
                final int timezoneMode = (int)row.get("TIME_ZONE_MODE");
                final String timeZone = (String)row.get("TIME_ZONE");
                final int timeZoneDetection = (int)row.get("TIME_ZONE_DETECTION");
                final boolean virtualMachinesAllowed = (boolean)row.get("VIRTUAL_MACHINES_ALLOWED");
                final boolean showUserNames = (boolean)row.get("SHOW_USERNAMES");
                payload.setGuestMode(guestMode);
                payload.setEphemeralMode(ephemeralMode);
                payload.setForcedReEnrollment(forcedReEnrollment);
                payload.setRedirectToSamlIdpAllowed(redirectToSAML);
                payload.setTransferSamlCookies(transferSAMLCookies);
                payload.setAllowedUsersToSignIn(allowedUsersSignin);
                payload.setAutoCompleteDomainName(autoCompleteDomain);
                payload.setTimeSettings(timezoneMode, timeZone, timeZoneDetection);
                payload.setVirtualMachinesAllowed(virtualMachinesAllowed);
                payload.setShowUserNamesOnSignInScreen(showUserNames);
            }
        }
        catch (final Exception ex) {
            DO2ChromeRestrictionsPolicyPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
}
