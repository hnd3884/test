package com.me.ems.framework.common.api.v1.service;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.me.ems.framework.common.api.v1.model.LiteFilter;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;

public class LiteFilterService
{
    private Logger filterLogger;
    
    public LiteFilterService() {
        this.filterLogger = Logger.getLogger(this.getClass().getName());
    }
    
    public static LiteFilterService getInstance() {
        return new LiteFilterService();
    }
    
    public Map<String, LiteFilter> getApplicableFiltersForPage(final Long pageID, final Long viewID, final User dcUser, final MultivaluedMap paramMap) throws APIException {
        final Map<String, LiteFilter> applicableFilterMap = new HashMap<String, LiteFilter>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCLiteFilter"));
        final Column viewIDColumn = Column.getColumn("DCLiteFilter", "VIEW_ID");
        final Column pageIDColumn = Column.getColumn("DCLiteFilter", "PAGE_ID");
        selectQuery.addSelectColumn(Column.getColumn("DCLiteFilter", "DCLITEFILTER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DCLiteFilter", "FILTER_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DCLiteFilter", "FETCH_VALUE_IMPL"));
        selectQuery.addSelectColumn(viewIDColumn);
        selectQuery.addSelectColumn(pageIDColumn);
        final Criteria pageIDCriteria = new Criteria(pageIDColumn, (Object)pageID, 0, (boolean)Boolean.FALSE);
        final Criteria viewIDCriteria = new Criteria(viewIDColumn, (Object)viewID, 0, (boolean)Boolean.FALSE);
        selectQuery.setCriteria(pageIDCriteria.and(viewIDCriteria));
        try {
            final DataObject dataObject = SyMUtil.getPersistenceLite().get(selectQuery);
            final Iterator liteFilterItr = dataObject.getRows("DCLiteFilter");
            if (dataObject == null || dataObject.isEmpty()) {
                throw new APIException("FILTER0001");
            }
            while (liteFilterItr.hasNext()) {
                final Row liteFilterRow = liteFilterItr.next();
                final String fetchValueImpl = (String)liteFilterRow.get("FETCH_VALUE_IMPL");
                final String filterName = (String)liteFilterRow.get("FILTER_NAME");
                final LiteFilterFetchValue liteFilterFetchValue = (LiteFilterFetchValue)Class.forName(fetchValueImpl).newInstance();
                final LiteFilter filter = liteFilterFetchValue.fetchValuesForFilter(filterName, dcUser, paramMap);
                applicableFilterMap.put(filter.getName(), filter);
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIException) {
                throw (APIException)ex;
            }
            this.filterLogger.log(Level.SEVERE, "Exception while fetching applicable filters for Page: " + pageID + " and viewID: " + viewID, ex);
            throw new APIException("FILTER0001");
        }
        return applicableFilterMap;
    }
}
