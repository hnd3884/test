package com.adventnet.sym.server.mdm.enroll;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Properties;
import java.util.ArrayList;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.search.SuggestQueryIfc;

public class MDMUserSearchSuggestionQuery implements SuggestQueryIfc
{
    private static Logger logger;
    
    public List getSuggestData(final String searchString, String domainName) {
        List dataList = null;
        Properties dataProperty = null;
        Criteria cri = null;
        try {
            if (domainName.equalsIgnoreCase("0")) {
                domainName = "MDM";
            }
            if (searchString != null && !searchString.equalsIgnoreCase("")) {
                final Criteria userCri = new Criteria(new Column("Resource", "RESOURCE_TYPE"), (Object)2, 0, false);
                final Criteria searchCri = new Criteria(new Column("Resource", "NAME"), (Object)searchString, 10, false);
                cri = userCri.and(searchCri);
            }
            final DataObject availableDO = MDMUtil.getPersistence().get("Resource", cri);
            final List availableList = MDMCustomGroupUtil.getInstance().getResourcePropertyList(availableDO);
            final Iterator colItr = availableList.iterator();
            dataList = new ArrayList();
            while (colItr.hasNext()) {
                dataProperty = new Properties();
                final HashMap userMap = colItr.next();
                ((Hashtable<String, Object>)dataProperty).put("dataValue", userMap.get("NAME"));
                ((Hashtable<String, Object>)dataProperty).put("dataId", userMap.get("RESOURCE_ID"));
                dataList.add(dataProperty);
            }
            return dataList;
        }
        catch (final Exception ex) {
            MDMUserSearchSuggestionQuery.logger.log(Level.WARNING, "User Search - MDMUserSearchSuggestionQuery : Exception occured - getSuggestData", ex);
            return dataList;
        }
    }
    
    static {
        MDMUserSearchSuggestionQuery.logger = Logger.getLogger(MDMUserSearchSuggestionQuery.class.getName());
    }
}
