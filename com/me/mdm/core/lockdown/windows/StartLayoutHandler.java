package com.me.mdm.core.lockdown.windows;

import org.json.JSONException;
import java.util.Iterator;
import com.me.mdm.core.lockdown.windows.data.LayoutModificationTemplate;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Collections;
import com.me.mdm.core.lockdown.data.LockdownApplication;
import java.util.List;

public class StartLayoutHandler
{
    private static final String MODERN_GROUP_NAME = "Modern Apps";
    private static final String LEGACY_GROUP_NAME = "Legacy Apps";
    private static final String SIZE_DEFAULT_VALUE = "2x2";
    private static final int GROUP_WIDTH = 6;
    
    public String getDefaultLayout(final List<LockdownApplication> applicationList) throws JSONException {
        Collections.sort(applicationList);
        final int numApps = applicationList.size();
        final Iterator iterator = applicationList.iterator();
        final JSONArray group1Apps = new JSONArray();
        final JSONArray group2Apps = new JSONArray();
        final JSONArray group3Apps = new JSONArray();
        int group1cnt = 0;
        int group2cnt = 0;
        int group3cnt = 0;
        int curApp = 0;
        while (iterator.hasNext()) {
            final LockdownApplication lockdownApplication = iterator.next();
            ++curApp;
            if (group1Apps.length() < 9 || (group2Apps.length() == 9 && group3Apps.length() == 9)) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_id", (Object)lockdownApplication.identifier);
                jsonObject.put("size", (Object)"2x2");
                jsonObject.put("row", (Object)new Integer(group1cnt / 6 * 2).toString());
                jsonObject.put("column", (Object)new Integer(group1cnt % 6).toString());
                group1Apps.put((Object)jsonObject);
                group1cnt += 2;
            }
            else if (group2Apps.length() < 9) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_id", (Object)lockdownApplication.identifier);
                jsonObject.put("size", (Object)"2x2");
                jsonObject.put("row", (Object)new Integer(group2cnt / 6 * 2).toString());
                jsonObject.put("column", (Object)new Integer(group2cnt % 6).toString());
                group2Apps.put((Object)jsonObject);
                group2cnt += 2;
            }
            else {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_id", (Object)lockdownApplication.identifier);
                jsonObject.put("size", (Object)"2x2");
                jsonObject.put("row", (Object)new Integer(group3cnt / 6 * 2).toString());
                jsonObject.put("column", (Object)new Integer(group3cnt % 6).toString());
                group3Apps.put((Object)jsonObject);
                group3cnt += 2;
            }
        }
        final JSONObject layout = new JSONObject();
        final JSONObject layoutOptions = new JSONObject();
        layoutOptions.put("group_width", 6);
        layout.put("layout_options", (Object)layoutOptions);
        final JSONObject startLayout = new JSONObject();
        startLayout.put("group_width", 6);
        final JSONArray groups = new JSONArray();
        final JSONObject group1 = new JSONObject();
        final JSONObject group2 = new JSONObject();
        final JSONObject group3 = new JSONObject();
        group1.put("group_name", (Object)"");
        group2.put("group_name", (Object)"");
        group3.put("group_name", (Object)"");
        if (group1Apps.length() > 0) {
            group1.put("apps", (Object)group1Apps);
            groups.put((Object)group1);
        }
        if (group2Apps.length() > 0) {
            group2.put("apps", (Object)group2Apps);
            groups.put((Object)group2);
        }
        if (group3Apps.length() > 0) {
            group3.put("apps", (Object)group3Apps);
            groups.put((Object)group3);
        }
        startLayout.put("groups", (Object)groups);
        layout.put("start_layout", (Object)startLayout);
        final String layoutString = new LayoutModificationTemplate(layout).getRootElement().toString();
        return layoutString;
    }
}
