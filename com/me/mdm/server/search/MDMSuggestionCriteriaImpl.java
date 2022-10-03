package com.me.mdm.server.search;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.search.SearchSuggestionAPI;

public class MDMSuggestionCriteriaImpl implements SearchSuggestionAPI
{
    public SelectQuery addSuggestionCriteria(final SelectQuery suggestionQuery, final String paramName, final int paramType) {
        if (paramName != null && paramName.equalsIgnoreCase("mdm.js.common.phonenumber")) {
            Criteria criteria = suggestionQuery.getCriteria();
            final Criteria emptySimCrit = new Criteria(Column.getColumn("MdSIMInfo", "PHONE_NUMBER"), (Object)"", 1);
            if (criteria == null) {
                criteria = emptySimCrit;
            }
            else {
                criteria = criteria.and(emptySimCrit);
            }
            suggestionQuery.setCriteria(criteria);
        }
        return suggestionQuery;
    }
}
