package com.me.mdm.server.backup;

import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import java.util.ArrayList;

public class MDMBackupUtil
{
    private static MDMBackupUtil mdmBackupUtil;
    private static ArrayList<MDMBackupListener> mdmBackupListeners;
    public static final String FBACKUP_FILE_NAME = "fileName";
    public static final String FBACKUP_FILE_PATH = "filePath";
    public static final String FBACKUP_FILE_SIZE = "fileSize";
    public static final String FBACKUP_DIR_TO_COPY = "directoryToCopy";
    public static final String FBACKUP_CUSTOMER_ID = "customerId";
    public static final String VBACKUP_VIEW_NAME = "view_name";
    public static final String VBACKUP_FILE_NAME = "file_name";
    public static final String VBACKUP_TOOL_ID = "tool_id";
    
    private MDMBackupUtil() {
    }
    
    public static MDMBackupUtil getInstance() {
        if (MDMBackupUtil.mdmBackupUtil == null) {
            MDMBackupUtil.mdmBackupUtil = new MDMBackupUtil();
        }
        return MDMBackupUtil.mdmBackupUtil;
    }
    
    public static ArrayList<MDMBackupListener> getDSCleanupBackupListeners() {
        return MDMBackupUtil.mdmBackupListeners;
    }
    
    public JSONArray getViewBackupParams() throws Exception {
        final Properties properties = MDMUtil.getInstance().getMDMViewBackupParamsProperties();
        final HashMap<String, HashMap<String, String>> propertyFetch = new HashMap<String, HashMap<String, String>>();
        final Set<String> keys = properties.stringPropertyNames();
        for (final String key : keys) {
            final String view = key.substring(0, key.indexOf("_"));
            HashMap<String, String> viewDetails;
            if (propertyFetch.containsKey(view)) {
                viewDetails = propertyFetch.get(view);
            }
            else {
                viewDetails = new HashMap<String, String>();
            }
            final String viewDetailsKey = key.substring(key.indexOf("_") + 1);
            final String viewDetailsValue = properties.getProperty(key);
            viewDetails.put(viewDetailsKey, viewDetailsValue);
            propertyFetch.put(view, viewDetails);
        }
        final JSONArray data = new JSONArray();
        for (final Map.Entry<String, HashMap<String, String>> entry : propertyFetch.entrySet()) {
            final HashMap<String, String> viewDetails = entry.getValue();
            final JSONObject viewInfo = new JSONObject();
            for (final Map.Entry<String, String> viewDetailsEntry : viewDetails.entrySet()) {
                final String key2 = viewDetailsEntry.getKey();
                final String value = viewDetailsEntry.getValue();
                if (key2.equals("view_name") || key2.equals("file_name") || key2.equals("tool_id")) {
                    viewInfo.put(key2, (Object)value);
                }
            }
            data.put((Object)viewInfo);
        }
        return data;
    }
    
    static {
        MDMBackupUtil.mdmBackupUtil = null;
        (MDMBackupUtil.mdmBackupListeners = new ArrayList<MDMBackupListener>()).add(new MDMAppsBackupListener());
        MDMBackupUtil.mdmBackupListeners.add(new MDMDocsBackupListener());
    }
}
