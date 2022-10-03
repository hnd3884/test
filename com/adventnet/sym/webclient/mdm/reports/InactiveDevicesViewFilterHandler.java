package com.adventnet.sym.webclient.mdm.reports;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilterCriteria;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterHandler;

public class InactiveDevicesViewFilterHandler implements DCViewFilterHandler
{
    public String getNativeDCViewFilterCriteria(final DCViewFilterCriteria dcViewFilterCriteria) {
        return null;
    }
    
    public Criteria getDCViewFilterCriteria(final DCViewFilterCriteria dcViewFilterCriteria) {
        Criteria criteria = null;
        JSONObject columnDetails = new JSONObject();
        String value = null;
        String searchValue2 = null;
        try {
            final Long columnID = dcViewFilterCriteria.getColumnID();
            if (columnID != null) {
                columnDetails = MDMUtil.getInstance().getColumnDetails(columnID);
            }
            final String columnAlias = MDMUtil.getInstance().getColumnAliasName(columnDetails);
            if (columnAlias != null && columnAlias != "") {
                final String dataType = String.valueOf(columnDetails.get("dataType"));
                final String tableName = String.valueOf(columnDetails.get("tableName"));
                final String columnName = String.valueOf(columnDetails.get("columnName"));
                final String comparator = dcViewFilterCriteria.getComparator();
                final String customComparator = dcViewFilterCriteria.getCustomComparator();
                final List searchValue3 = dcViewFilterCriteria.getSearchValue();
                if (searchValue3 != null && !searchValue3.isEmpty()) {
                    value = searchValue3.get(0);
                    if (dcViewFilterCriteria.getComparator().equalsIgnoreCase("between")) {
                        searchValue2 = searchValue3.get(1);
                    }
                    else {
                        value = (String)searchValue3.stream().collect(Collectors.joining("$@$"));
                    }
                }
                if ("AgentContact.LAST_CONTACT_TIME".equals(columnAlias)) {
                    criteria = DCViewFilterUtil.getInstance().getDCViewFilterCriteria(dataType, tableName, columnName, comparator, customComparator, value, searchValue2);
                    criteria = criteria.negate();
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, e, () -> "Exception getDCViewFilterCriteria criteriaDetailsJSON: " + dcViewFilterCriteria2.toString());
        }
        return criteria;
    }
}
