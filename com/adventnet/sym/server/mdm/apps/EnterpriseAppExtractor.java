package com.adventnet.sym.server.mdm.apps;

import org.json.JSONObject;
import com.me.mdm.server.windows.apps.WindowsAppExtractor;
import com.adventnet.sym.server.mdm.apps.android.APKExtractionHandler;
import com.adventnet.sym.server.mdm.apps.ios.IosIPAExtractor;

public abstract class EnterpriseAppExtractor
{
    protected static final String PACKAGE_NAME = "PackageName";
    protected static final String PHONE_PRODUCT_ID = "PhoneProductID";
    protected static final String VERSION_NAME = "VersionName";
    protected static final String ICON_NAME = "IconName";
    
    public static EnterpriseAppExtractor getNewInstance(final int platformType) {
        switch (platformType) {
            case 1: {
                return new IosIPAExtractor();
            }
            case 2: {
                return new APKExtractionHandler();
            }
            case 3: {
                return new WindowsAppExtractor();
            }
            default: {
                return null;
            }
        }
    }
    
    public abstract JSONObject getAppDetails(final String p0) throws Exception;
    
    public JSONObject getAppSignatureDetails(final String appFilePath) throws Exception {
        return null;
    }
}
