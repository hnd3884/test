package com.adventnet.sym.webclient.mdm.reports;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilterCriteria;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterHandler;

public class AppsDevicesViewFilterHandler implements DCViewFilterHandler
{
    public String getNativeDCViewFilterCriteria(final DCViewFilterCriteria criteriaDetails) {
        return null;
    }
    
    public Criteria getDCViewFilterCriteria(final DCViewFilterCriteria criteriaDetails) {
        Criteria criteria = null;
        JSONObject columnDetails = new JSONObject();
        try {
            final Long columnID = criteriaDetails.getColumnID();
            if (columnID != null) {
                columnDetails = MDMUtil.getInstance().getColumnDetails(columnID);
            }
            final String columnAlias = MDMUtil.getInstance().getColumnAliasName(columnDetails);
            if (columnAlias != null && columnAlias != "") {
                final List valueList = criteriaDetails.getSearchValue();
                if ("MdInstalledAppResourceRel.USER_INSTALLED_APPS".equals(columnAlias)) {
                    boolean isShowSystemApp = false;
                    boolean isShowUserInstalledApp = false;
                    boolean isShowManagedApp = false;
                    for (final Object value : valueList) {
                        final Integer param = Integer.valueOf(value.toString());
                        if (param == 1) {
                            isShowUserInstalledApp = true;
                        }
                        if (param == 2) {
                            isShowSystemApp = true;
                        }
                        if (param == 3) {
                            isShowManagedApp = true;
                        }
                    }
                    final Criteria catalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
                    final Criteria noncatalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
                    final Criteria userInstalledCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
                    final Criteria systemCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0);
                    if (isShowManagedApp && isShowUserInstalledApp && isShowSystemApp) {
                        criteria = null;
                    }
                    else if (isShowManagedApp && isShowUserInstalledApp) {
                        criteria = userInstalledCriteria.or(catalogCriteria);
                    }
                    else if (isShowManagedApp && isShowSystemApp) {
                        criteria = systemCriteria.or(catalogCriteria);
                    }
                    else if (isShowUserInstalledApp && isShowSystemApp) {
                        criteria = noncatalogCriteria;
                    }
                    else if (isShowManagedApp) {
                        criteria = catalogCriteria;
                    }
                    else if (isShowUserInstalledApp) {
                        criteria = userInstalledCriteria.and(noncatalogCriteria);
                    }
                    else if (isShowSystemApp) {
                        criteria = systemCriteria.and(noncatalogCriteria);
                    }
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, e, () -> "Exception getDCViewFilterCriteria criteriaDetailsJSON: " + dcViewFilterCriteria.toString());
        }
        return criteria;
    }
}
