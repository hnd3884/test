package com.me.mdm.server.windows.profile.payload.transform;

import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.windows.profile.payload.WindowsConfigurationPayload;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public abstract class DO2WindowsPayload
{
    protected final Logger logger;
    public static final String WIN_PHONE_8_1_RESTRICTIONS = "WindowsPhone81";
    public static final String WIN_PHONE_8_RESTRICTIONS = "WindowsPhone8";
    public static final String WIN_10_MOBILE_RESTRICTIONS = "Windows10Mobile";
    public static final String WIN_10_DESKTOP_RESTRICTIONS = "Windows10Desktop";
    public static final String WIN_PHONE_8_1_PASSCODE = "WindowsPhone81Passcode";
    public static final String WIN_10_MOBILE_PASSCODE = "Windows10MobilePasscode";
    public static final String WIN_PHONE_8_1_CERTIFICATE = "WindowsPhone81Certificate";
    public static final String WIN_10_MOBILE_CERTIFICATE = "Windows10MobileCertificate";
    public static final String WIN_PHONE_8_1_SCEP = "WindowsPhone81Scep";
    public static final String WIN_10_MOBILE_SCEP = "Windows10MobileScep";
    public static final String WIN_ACTIVESYNC = "WindowsActiveSync";
    public static final String WIN_EMAIL = "WindowsEmail";
    public static final String WIN_10_MOBILE_APP = "Windows10MobileApp";
    public static final String WIN_10_DESKTOP_APP = "Windows10DesktopApp";
    public static final String WIN_PHONE_8_1_APP = "WindowsPhone81App";
    public static final String WIN_LOCKDOWN_DESKTOP = "Windows10DesktopLockdown";
    public static final String WIN_LOCKDOWN_10_MOBILE = "Windows10MobileLockdown";
    public static final String WIN_LOCKDOWN_8_1_MOBILE = "WindowsPhone81Lockdown";
    public static final String INSTALL = "install";
    public static final String REMOVE = "remove";
    
    public DO2WindowsPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public abstract WindowsPayload createPayload(final DataObject p0);
    
    public abstract WindowsPayload createRemoveProfilePayload(final DataObject p0);
    
    public void packOsSpecificPayloadToXML(final DataObject dataObject, final WindowsPayload payload, final String type, final String osVersion) {
        try {
            final Row cfgCollRow = dataObject.getRow("CollnToCustomerRel");
            final Long collectionID = (Long)cfgCollRow.get("COLLECTION_ID");
            final Long customerID = (Long)cfgCollRow.get("CUSTOMER_ID");
            final String profileInstallDirectory = MDMMetaDataUtil.getInstance().getMdmProfileFolderPath(customerID, "profiles", collectionID);
            WindowsConfigurationPayload childPayload = new WindowsConfigurationPayload();
            if (type.equalsIgnoreCase("install")) {
                childPayload = payload.getOSSpecificInstallPayload(childPayload);
            }
            else if (type.equalsIgnoreCase("remove")) {
                childPayload = payload.getOSSpecificRemovePayload(childPayload);
            }
            else if (type.equalsIgnoreCase("update")) {
                childPayload = payload.getOSSpecificUpdatePayload(childPayload);
            }
            final String fileName = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + profileInstallDirectory + File.separator + osVersion + "_" + type + ".xml";
            final byte[] payloadByte = childPayload.toString().getBytes();
            try {
                ApiFactoryProvider.getFileAccessAPI().writeFile(fileName, payloadByte);
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception while packing Windows OS specific payload : ", ex);
            }
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
    }
}
