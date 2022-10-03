package com.adventnet.sym.webclient.mdm.config;

import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.apps.AppsUtil;

public class AppRepositoryUtil
{
    private static AppRepositoryUtil appsUtil;
    
    public static AppRepositoryUtil getInstance() {
        if (AppRepositoryUtil.appsUtil == null) {
            AppRepositoryUtil.appsUtil = new AppRepositoryUtil();
        }
        return AppRepositoryUtil.appsUtil;
    }
    
    public String getSupportedDevices(final int platformType, final Object data) throws Exception {
        String value = data.toString();
        if (platformType == 1) {
            value = AppsUtil.getInstance().getiOSSupportedDevicesString((int)data);
        }
        else if (platformType == 3) {
            switch ((int)data) {
                case 24: {
                    value = I18N.getMsg("mdm.actionlog.appmgmt.smartPhone_tablet_laptop", new Object[0]);
                    break;
                }
                case 8: {
                    value = I18N.getMsg("dc.mdm.actionlog.appmgmt.smartPhone", new Object[0]);
                    break;
                }
                case 16: {
                    value = I18N.getMsg("mdm.actionlog.appmgmt.tablet_laptop", new Object[0]);
                    break;
                }
            }
        }
        else if (platformType == 4) {
            value = I18N.getMsg("mdm.enroll.chromebook", new Object[0]);
        }
        else if ((int)data == 1) {
            value = I18N.getMsg("dc.mdm.actionlog.appmgmt.smartPhone_tablet", new Object[0]);
        }
        else if ((int)data == 2) {
            value = I18N.getMsg("dc.mdm.actionlog.appmgmt.smartPhone", new Object[0]);
        }
        else if ((int)data == 3) {
            value = I18N.getMsg("dc.mdm.graphs.tablet", new Object[0]);
        }
        return value;
    }
    
    static {
        AppRepositoryUtil.appsUtil = null;
    }
}
