package com.adventnet.sym.webclient.mdm.reports;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.settings.location.GeoLocationFacade;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilterCriteria;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterHandler;

public class DeviceLocationViewFilterHandler implements DCViewFilterHandler
{
    public String getNativeDCViewFilterCriteria(final DCViewFilterCriteria dcViewFilterCriteria) {
        return null;
    }
    
    public Criteria getDCViewFilterCriteria(final DCViewFilterCriteria dcViewFilterCriteria) {
        Criteria criteria = null;
        JSONObject columnDetails = new JSONObject();
        String value = null;
        final String searchValue2 = null;
        try {
            final Long columnID = dcViewFilterCriteria.getColumnID();
            if (columnID != null) {
                columnDetails = MDMUtil.getInstance().getColumnDetails(columnID);
            }
            final String columnAlias = MDMUtil.getInstance().getColumnAliasName(columnDetails);
            if (columnAlias != null && columnAlias != "") {
                final String dataType = String.valueOf(columnDetails.get("dataType"));
                final List searchValue3 = dcViewFilterCriteria.getSearchValue();
                if (searchValue3 != null && !searchValue3.isEmpty()) {
                    value = searchValue3.get(0);
                }
                if ("MdDeviceLocationToErrCode.ERROR_CODE".equals(columnAlias)) {
                    final Integer geoStatus = Integer.valueOf(value);
                    final Criteria geoStatusCri = criteria = new GeoLocationFacade().getGeoStatusCrit(geoStatus, true);
                }
                if ("MdDeviceLocationDetails.LOCATED_TIME_INTERVAL".equalsIgnoreCase(columnAlias)) {
                    final ArrayList locationIds = new GeoLocationFacade().getLocationHistoryIds(null, null, null, null, null, null, value, CustomerInfoUtil.getInstance().getCustomerId());
                    Criteria locationIdCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)locationIds.toArray(), 8);
                    locationIdCri = (criteria = locationIdCri.or(new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)null, 0)));
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, e, () -> "Exception getDCViewFilterCriteria criteriaDetailsJSON: " + dcViewFilterCriteria2.toString());
        }
        return criteria;
    }
}
