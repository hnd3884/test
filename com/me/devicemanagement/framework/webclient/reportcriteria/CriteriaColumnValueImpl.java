package com.me.devicemanagement.framework.webclient.reportcriteria;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataObject;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.ArrayList;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Level;
import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.reportcriteria.CriteriaColumnValueUtil;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CriteriaColumnValueImpl implements CriteriaColumnValue
{
    private static Logger logger;
    
    @Override
    public List getColumnBrowseValues(final Long columnID, final Long viewID, final Map filterMap, final Long loginID) throws Exception {
        List searchValues;
        try {
            final List customSearchValuesList = filterMap.get("customSearchValues");
            Integer offset = filterMap.get("offset");
            Integer limit = filterMap.get("limit");
            final String filter = filterMap.get("filter");
            final DataObject browseColumnDetails = CriteriaColumnValueUtil.getInstance().crcolumnDetails(columnID);
            final String dataType = CriteriaColumnValueUtil.getInstance().getDataType(browseColumnDetails);
            Boolean useCustomRange = dataType.equalsIgnoreCase("I18N");
            final LinkedHashMap<Object, String> transformValues = new LinkedHashMap<Object, String>();
            transformValues.putAll((Map<?, ?>)CriteriaColumnValueUtil.getInstance().getTranformValueList(columnID, customSearchValuesList));
            if (!transformValues.isEmpty()) {
                searchValues = CriteriaColumnValueUtil.getInstance().getBrowseValuesWithTransformation(transformValues, filter);
                useCustomRange = true;
            }
            else {
                SelectQuery searchValueQuery;
                if ((searchValueQuery = CriteriaColumnValueUtil.getInstance().customBrowseValuesFetchQuery(columnID, viewID, filter, useCustomRange, customSearchValuesList)) == null) {
                    searchValueQuery = CriteriaColumnValueUtil.getInstance().defaultBrowseValuesFetchQuery(browseColumnDetails, filter, customSearchValuesList);
                }
                CriteriaColumnValueImpl.logger.log(Level.INFO, "getColumnBrowseValues: Query to fetch browse values: {0}", RelationalAPI.getInstance().getSelectSQL((Query)searchValueQuery));
                if (!useCustomRange || filter == null || filter.isEmpty()) {
                    searchValueQuery.setRange(new Range((int)offset, (int)limit));
                }
                searchValues = CriteriaColumnValueUtil.getInstance().getBrowseValueList(searchValueQuery, dataType, filter);
                useCustomRange = (filter != null && !filter.isEmpty() && useCustomRange);
            }
            if (useCustomRange) {
                limit = ((limit == 0) ? searchValues.size() : limit);
                offset = ((offset == 0) ? 0 : (offset - 1));
                searchValues = (ArrayList)searchValues.stream().skip(offset).limit(limit).collect(Collectors.toList());
            }
        }
        catch (final Exception ex) {
            CriteriaColumnValueImpl.logger.log(Level.WARNING, "Report Criteria:Exception in fetching browse values", ex);
            throw ex;
        }
        return searchValues;
    }
    
    static {
        CriteriaColumnValueImpl.logger = Logger.getLogger("ScheduleReportLogger");
    }
}
