package com.me.mdm.webclient.search;

import com.adventnet.sym.webclient.mdm.EnrollmentRequestSearchTRAction;
import com.adventnet.sym.webclient.mdm.inv.DeviceListTRAction;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.webclient.search.SearchCriteriaAPI;

public class MDMSearchCriteriaImpl implements SearchCriteriaAPI
{
    private static final String DEVICE_SEARCH_VIEW = "DeviceListSearch";
    private static final String ENROLL_DEVICE_SEARCH_VIEW = "EnrollmentRequestSearch";
    
    public SelectQuery setCorrespondingCriteria(final ViewContext viewCtx, final SelectQuery selectQuery, final String view_name) {
        if (view_name.equalsIgnoreCase("DeviceListSearch")) {
            final DeviceListTRAction device = new DeviceListTRAction();
            device.setCriteria(selectQuery, viewCtx);
        }
        if (view_name.equalsIgnoreCase("EnrollmentRequestSearch")) {
            final EnrollmentRequestSearchTRAction enroll = new EnrollmentRequestSearchTRAction();
            enroll.setCriteria(selectQuery, viewCtx);
        }
        return selectQuery;
    }
}
