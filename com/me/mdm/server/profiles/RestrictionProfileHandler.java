package com.me.mdm.server.profiles;

import java.util.Iterator;
import java.util.Set;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.logging.Logger;

public class RestrictionProfileHandler
{
    private static final Logger LOGGER;
    
    public boolean isRestrictionConfigured(final Long collectionId, final HashMap supervisedRestrictionMap, final String tableName) {
        try {
            final SelectQuery restrictionQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
            restrictionQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            restrictionQuery.addJoin(new Join("ConfigDataItem", tableName, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            final Criteria collectionCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria restrictionCriteria = this.createCriteriaFromHashMap(supervisedRestrictionMap, tableName);
            final Criteria criteria = collectionCriteria.and(restrictionCriteria);
            restrictionQuery.setCriteria(criteria);
            restrictionQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            restrictionQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            restrictionQuery.addSelectColumn(new Column(tableName, "CONFIG_DATA_ITEM_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(restrictionQuery);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final Exception ex) {
            RestrictionProfileHandler.LOGGER.log(Level.SEVERE, "Exception while retrieving configured restriction", ex);
        }
        return false;
    }
    
    public Criteria createCriteriaFromHashMap(final HashMap supervisedRestrictionMap, final String tableName) {
        Criteria criteria = null;
        final Set<String> keySet = supervisedRestrictionMap.keySet();
        for (final Object key : keySet) {
            final String restrictionKey = key.toString();
            final Object value = supervisedRestrictionMap.get(restrictionKey);
            final List<String> restrictionKeyList = new ArrayList<String>();
            if (value instanceof String) {
                restrictionKeyList.add((String)value);
            }
            else {
                restrictionKeyList.addAll((Collection<? extends String>)value);
            }
            for (final String restrictionKeyValue : restrictionKeyList) {
                if (!MDMStringUtils.isEmpty(restrictionKeyValue)) {
                    if (criteria != null) {
                        criteria = criteria.or(new Criteria(new Column(tableName, restrictionKey), (Object)restrictionKeyValue, 0));
                    }
                    else {
                        criteria = new Criteria(new Column(tableName, restrictionKey), (Object)restrictionKeyValue, 0);
                    }
                }
            }
        }
        return criteria;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
