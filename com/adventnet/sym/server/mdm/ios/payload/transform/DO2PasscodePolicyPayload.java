package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.PasscodePolicyPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2PasscodePolicyPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2PasscodePolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        PasscodePolicyPayload payload = null;
        final IOSPayload[] payloadArray = new IOSPayload[2];
        try {
            final Iterator iterator = dataObject.getRows("PasscodePolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final boolean allowSimple = (boolean)row.get("ALLOW_SIMPLE_VALUE");
                final boolean alphaNumeric = (boolean)row.get("REQUIRE_ALPHANUMERIC");
                final Integer passcodeLength = (Integer)row.get("MIN_PASSCODE_LENGTH");
                final Integer complexChars = (Integer)row.get("MIN_COMPLEX_CHARS");
                final Integer passcodeAge = (Integer)row.get("MAX_PASSCODE_AGE");
                final Integer passcodeAutoLock = (Integer)row.get("AUTO_LOCK_IDLE_FOR");
                final Integer passcodeHistory = (Integer)row.get("NO_OF_PASSCODE_MAINTAINED");
                final Integer passcodeGracePeriod = (Integer)row.get("MAX_GRACE_PERIOD");
                final Integer passcodeFailedAttempts = (Integer)row.get("MAX_FAILED_ATTEMPTS");
                final Boolean forcePasscode = (Boolean)row.get("FORCE_PASSCODE");
                final Boolean disablePasscode = (Boolean)row.get("RESTRICT_PASSCODE");
                final Boolean changeAtNextAuth = (Boolean)row.get("CHANGE_AT_NEXT_AUTH");
                final Integer minsFailedLoginReset = (Integer)row.get("MINS_FAILED_LOGIN_RESET");
                payload = new PasscodePolicyPayload(1, "MDM", "com.mdm.mobiledevice.passcode", "Passcode Policy");
                if (!forcePasscode && disablePasscode) {
                    final DO2RestrictionsPolicyPayload restriction = new DO2RestrictionsPolicyPayload();
                    payloadArray[0] = restriction.createPasscodeRestrictionPayload(!disablePasscode);
                    if (passcodeAutoLock != -1) {
                        payload.setMaxInactivity(passcodeAutoLock);
                        payloadArray[1] = payload;
                    }
                    return payloadArray;
                }
                payload.setForcePIN(forcePasscode);
                payload.setAllowSimple(allowSimple);
                payload.setChangeAtNextAuth(changeAtNextAuth);
                if (alphaNumeric) {
                    payload.setRequireAlphanumeric(alphaNumeric);
                }
                if (passcodeLength != -1) {
                    payload.setMinLength(passcodeLength);
                }
                if (complexChars != -1) {
                    payload.setMinComplexChars(complexChars);
                }
                if (passcodeAge != -1 && passcodeAge != 0) {
                    payload.setMaxPINAgeInDays(passcodeAge);
                }
                if (passcodeAutoLock != -1 && passcodeAutoLock != 0) {
                    payload.setMaxInactivity(passcodeAutoLock);
                }
                if (passcodeHistory != -1 && passcodeHistory != 0) {
                    payload.setPinHistory(passcodeHistory);
                }
                if (passcodeGracePeriod != -1) {
                    payload.setMaxGracePeriod(passcodeGracePeriod);
                }
                if (passcodeFailedAttempts == -1) {
                    continue;
                }
                payload.setMaxFailedAttempts(passcodeFailedAttempts);
                if (minsFailedLoginReset == -1) {
                    continue;
                }
                payload.setMinutesUntilFailedLoginReset(minsFailedLoginReset);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in creating passcode policy", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
}
