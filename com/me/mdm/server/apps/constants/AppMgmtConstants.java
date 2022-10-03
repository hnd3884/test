package com.me.mdm.server.apps.constants;

import java.util.Collections;
import java.util.Arrays;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.List;
import java.util.Map;

public class AppMgmtConstants
{
    public static final String APP_REPO_PATH = "apprepository";
    public static final String APP_BASE_PATH;
    public static final String OLD_APP_BASE_PATH;
    public static final String APP_FILE_DIRECTORY;
    public static final Integer APP_VERSION_APPROVED;
    public static final String FORCE_UPDATE_IN_LABEL = "force_update_in_label";
    public static final String HAS_APP_FILE = "hasAppFile";
    public static final String VERSION_LABEL = "version_label";
    public static final int PUBLIC_APP = 0;
    public static final int PRIVATE_APP = 1;
    public static final String[] ANDROID_CRITICAL_APPS;
    public static final Map<Integer, List<String>> CRITICAL_APPS;
    public static final List<String> APP_MGMT_ALLOWED_EXTENSIONS;
    public static final String APP_DETAILS_OBJECT = "appDetailsObject";
    
    static {
        APP_BASE_PATH = MDMAppMgmtHandler.getInstance().getAppRepositoryBaseFolderPath();
        OLD_APP_BASE_PATH = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        APP_FILE_DIRECTORY = File.separator + "MDM" + File.separator + "apprepository";
        APP_VERSION_APPROVED = 1;
        ANDROID_CRITICAL_APPS = new String[] { "com.sec.android.app.launcher", "com.google.android.gms", "com.android.vending", "com.android.settings" };
        CRITICAL_APPS = Collections.singletonMap(2, Arrays.asList(AppMgmtConstants.ANDROID_CRITICAL_APPS));
        APP_MGMT_ALLOWED_EXTENSIONS = Arrays.asList("appx", "apk", "ipa", "msi", "plist", "xap", "msix", "appxbundle", "pkg");
    }
}
