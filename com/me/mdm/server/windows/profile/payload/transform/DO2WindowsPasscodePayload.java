package com.me.mdm.server.windows.profile.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.WinMobilePasscodePayload;
import com.me.mdm.server.windows.profile.payload.WindowsPasscodePayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsPasscodePayload extends DO2WindowsPayload
{
    private static final int PASSCODE_LENGTH_MIN_VALUE = 4;
    private static final int PASSCODE_LENGTH_MAX_VALUE = 14;
    private static final int COMPLEX_CHARACTERS_MIN_VALUE = 1;
    private static final int COMPLEX_CHARACTERS_MAX_VALUE = 3;
    private static final int PASSWORD_HISTORY_MIN_VALUE = 0;
    private static final int PASSWORD_HISTORY_MAX_VALUE = 24;
    
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        Integer intDevicePasscodeEnabled = null;
        Integer intAllowSimpleDevicePassword = null;
        Integer intMinDevicePasswordLength = null;
        Integer intAlphanumericDevicePasswordRequired = null;
        Integer intDevicePasswordExpiration = null;
        Integer intPasswordHistory = null;
        Integer intMaxDevicePasswordFailedAttempts = null;
        Integer intMaxInactivityTimeDeviceLock = null;
        Integer intMinPasswordComplexChar = null;
        Integer intMinimumPasswordAge = null;
        WindowsPasscodePayload payload = null;
        WindowsPasscodePayload winPhonePasscodePayload = null;
        WinMobilePasscodePayload winMobilePasscodePayload = null;
        try {
            final Iterator iterator = dataObject.getRows("WpPasscodePolicy");
            payload = new WindowsPasscodePayload();
            payload.getReplacePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%passcode_payload_xml%"));
            winPhonePasscodePayload = new WindowsPasscodePayload();
            winMobilePasscodePayload = new WinMobilePasscodePayload();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                intDevicePasscodeEnabled = (Integer)row.get("PWD_ENABLED");
                intAllowSimpleDevicePassword = (Integer)row.get("ALLOW_SIMPLE_PWD");
                intMinDevicePasswordLength = (Integer)row.get("MIN_PWD_LENGTH");
                intAlphanumericDevicePasswordRequired = (Integer)row.get("ALPHANUMERIC_PWD_REQUIRED");
                intDevicePasswordExpiration = (Integer)row.get("PWD_EXPIRATION");
                intPasswordHistory = (Integer)row.get("PWD_HISTORY");
                intMaxDevicePasswordFailedAttempts = (Integer)row.get("MAX_PWD_FAILED_ATTEMPTS");
                intMaxInactivityTimeDeviceLock = (Integer)row.get("MAX_INACTIVITY_TIME_DEVLOCK");
                intMinPasswordComplexChar = (Integer)row.get("MIN_PWD_COMPLEX_CHAR");
                intMinimumPasswordAge = (Integer)row.get("MIN_PWD_AGE");
                if (intDevicePasscodeEnabled != null && intDevicePasscodeEnabled != -1) {
                    winPhonePasscodePayload.setDevicePasswordEnabled(intDevicePasscodeEnabled);
                    winMobilePasscodePayload.setDevicePasswordEnabled(intDevicePasscodeEnabled);
                }
                if (intAllowSimpleDevicePassword != null && intAllowSimpleDevicePassword != -1) {
                    winPhonePasscodePayload.setAllowSimpleDevicePassword(intAllowSimpleDevicePassword);
                    winMobilePasscodePayload.setAllowSimpleDevicePassword(intAllowSimpleDevicePassword);
                }
                if (intMinDevicePasswordLength != null && intMinDevicePasswordLength != -1 && intMinDevicePasswordLength >= 4 && intMinDevicePasswordLength <= 14) {
                    winPhonePasscodePayload.setMinDevicePasswordLength(intMinDevicePasswordLength);
                    winMobilePasscodePayload.setMinDevicePasswordLength(intMinDevicePasswordLength);
                }
                else if (intMinDevicePasswordLength != null && intMinDevicePasswordLength != -1) {
                    intMinDevicePasswordLength = ((intMinDevicePasswordLength < 4) ? 4 : 14);
                    winPhonePasscodePayload.setMinDevicePasswordLength(intMinDevicePasswordLength);
                    winMobilePasscodePayload.setMinDevicePasswordLength(intMinDevicePasswordLength);
                }
                if (intAlphanumericDevicePasswordRequired != null && intAlphanumericDevicePasswordRequired != -1) {
                    winPhonePasscodePayload.setAlphanumericDevicePasswordRequired(intAlphanumericDevicePasswordRequired);
                    winMobilePasscodePayload.setAlphanumericDevicePasswordRequired(intAlphanumericDevicePasswordRequired);
                }
                if (intDevicePasswordExpiration != null && intDevicePasswordExpiration != -1) {
                    winPhonePasscodePayload.setDevicePasswordExpiration(intDevicePasswordExpiration);
                    winMobilePasscodePayload.setDevicePasswordExpiration(intDevicePasswordExpiration);
                }
                if (intPasswordHistory != null && intPasswordHistory != -1 && intPasswordHistory >= 0 && intPasswordHistory <= 24) {
                    winPhonePasscodePayload.setDevicePasswordHistory(intPasswordHistory);
                    winMobilePasscodePayload.setDevicePasswordHistory(intPasswordHistory);
                }
                else if (intPasswordHistory != null && intPasswordHistory != -1) {
                    intPasswordHistory = ((intPasswordHistory < 0) ? 0 : 24);
                    winPhonePasscodePayload.setDevicePasswordHistory(intPasswordHistory);
                    winMobilePasscodePayload.setDevicePasswordHistory(intPasswordHistory);
                }
                if (intMaxDevicePasswordFailedAttempts != null && intMaxDevicePasswordFailedAttempts != -1) {
                    winPhonePasscodePayload.setMaxDevicePasswordFailedAttempts(intMaxDevicePasswordFailedAttempts);
                    winMobilePasscodePayload.setMaxDevicePasswordFailedAttempts(intMaxDevicePasswordFailedAttempts);
                }
                if (intMaxInactivityTimeDeviceLock != null && intMaxInactivityTimeDeviceLock != -1) {
                    winPhonePasscodePayload.setMaxInactivityTimeDeviceLock(intMaxInactivityTimeDeviceLock);
                    winMobilePasscodePayload.setMaxInactivityTimeDeviceLock(intMaxInactivityTimeDeviceLock);
                }
                if (intMinPasswordComplexChar != null && intMinPasswordComplexChar != -1 && intMinPasswordComplexChar >= 1 && intMinPasswordComplexChar <= 3) {
                    winPhonePasscodePayload.setMinDevicePasswordComplexCharacters(intMinPasswordComplexChar);
                    winMobilePasscodePayload.setMinDevicePasswordComplexCharacters(intMinPasswordComplexChar);
                }
                else if (intMinPasswordComplexChar != null && intMinPasswordComplexChar != -1) {
                    intMinPasswordComplexChar = ((intMinPasswordComplexChar < 1) ? 1 : 3);
                    winPhonePasscodePayload.setMinDevicePasswordComplexCharacters(intMinPasswordComplexChar);
                    winMobilePasscodePayload.setMinDevicePasswordComplexCharacters(intMinPasswordComplexChar);
                }
                if (intDevicePasswordExpiration != null && intDevicePasswordExpiration != -1 && intMinimumPasswordAge != null && intMinimumPasswordAge != -1 && intMinimumPasswordAge < intDevicePasswordExpiration) {
                    winPhonePasscodePayload.setMinimumPasswordAge(intMinimumPasswordAge);
                    winMobilePasscodePayload.setMinimumPasswordAge(intMinimumPasswordAge);
                }
                else {
                    if (intMinimumPasswordAge == null || intMinimumPasswordAge == -1 || (intDevicePasswordExpiration != null && intDevicePasswordExpiration != -1)) {
                        continue;
                    }
                    winPhonePasscodePayload.setMinimumPasswordAge(intMinimumPasswordAge);
                    winMobilePasscodePayload.setMinimumPasswordAge(intMinimumPasswordAge);
                }
            }
            this.packOsSpecificPayloadToXML(dataObject, winPhonePasscodePayload, "install", "WindowsPhone81Passcode");
            this.packOsSpecificPayloadToXML(dataObject, winMobilePasscodePayload, "install", "Windows10MobilePasscode");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Passcode payload ", ex);
        }
        return payload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        WindowsPasscodePayload payload = null;
        WindowsPasscodePayload winPhonePasscodePayload = null;
        WinMobilePasscodePayload winMobilePasscodePayload = null;
        try {
            payload = new WindowsPasscodePayload();
            payload.getDeletePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%passcode_payload_xml%"));
            winPhonePasscodePayload = new WindowsPasscodePayload();
            winMobilePasscodePayload = new WinMobilePasscodePayload();
            winPhonePasscodePayload.setRemoveProfilePayload();
            winMobilePasscodePayload.setRemoveProfilePayload();
            this.packOsSpecificPayloadToXML(dataObject, winPhonePasscodePayload, "remove", "WindowsPhone81Passcode");
            this.packOsSpecificPayloadToXML(dataObject, winMobilePasscodePayload, "remove", "Windows10MobilePasscode");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Passcode remove payload ", ex);
        }
        return payload;
    }
}
