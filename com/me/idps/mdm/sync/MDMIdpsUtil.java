package com.me.idps.mdm.sync;

import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.factory.IdpsFactoryConstant;
import com.me.mdm.directory.sync.mdm.MDMDirectoryProductImpl;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.me.idps.core.util.IdpsUtil;
import org.json.simple.JSONArray;
import java.sql.Connection;
import com.adventnet.ds.query.Column;
import java.util.HashMap;

public class MDMIdpsUtil
{
    private static final HashMap<Long, Column> USER_ATTR_COL_MAP;
    private static final HashMap<Long, Column> GROUP_ATTR_COL_MAP;
    
    public static JSONArray getDuplicatedGroupDomains(final Connection connection, final String dirResTablName, final String resIDcol) throws Exception {
        final Column resNameCol = Column.getColumn("Resource", "NAME", "RESOURCE.NAME");
        final Column domainNameCol = Column.getColumn("DMDomain", "NAME", "DMDOMAIN.NAME");
        final Column resNetbiosNameCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
        final Column countCol = IdpsUtil.getCountOfColumn(resNameCol, "count");
        final Criteria domainNameCri = new Criteria(resNetbiosNameCol, (Object)domainNameCol, 0, false).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Column.getColumn("DMDomain", "CUSTOMER_ID"), 0));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "DMDomain", domainNameCri, 2));
        selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomGroup", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomGroup", dirResTablName, new String[] { "RESOURCE_ID" }, new String[] { resIDcol }, 1));
        selectQuery.setCriteria(domainNameCri.and(DirectoryGrouper.getInstance().getCGcri()).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0)).and(new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)1, 1)).and(new Criteria(Column.getColumn(dirResTablName, resIDcol), (Object)null, 0)));
        selectQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(resNetbiosNameCol)), new Criteria(countCol, (Object)1, 5)));
        selectQuery.addSelectColumn(countCol);
        selectQuery.addSelectColumn(resNetbiosNameCol);
        final JSONArray jsonArray = IdpsUtil.executeSelectQuery(connection, selectQuery);
        return jsonArray;
    }
    
    public static HashMap<Long, Column> getObjAttrColMap(final int resType) {
        if (resType == 2) {
            return MDMIdpsUtil.USER_ATTR_COL_MAP;
        }
        if (resType == 101) {
            return MDMIdpsUtil.GROUP_ATTR_COL_MAP;
        }
        return null;
    }
    
    public static MDMDirectoryProductImpl getMDMdirProdImpl() {
        final Object dirProductImpl = IdpsFactoryProvider.getSingleImplClassInstance(IdpsFactoryConstant.PRODUCT_IMPL);
        if (dirProductImpl != null && dirProductImpl instanceof MDMDirectoryProductImpl) {
            return (MDMDirectoryProductImpl)dirProductImpl;
        }
        return null;
    }
    
    public static boolean isGroupInSync(final Long[] groupIds, final Long customerId) {
        try {
            final DataObject dObj = MDMUtil.getPersistenceLite().get(SyMUtil.formSelectQuery("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)groupIds, 8).and(new Criteria(Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"), (Object)new Integer[] { 951, 941 }, 8)), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("Resource", "RESOURCE_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("Resource", "DMDomain", new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)Column.getColumn("DMDomain", "NAME"), 0, false), 2), new Join("DMDomain", "DMDomainSyncDetails", new String[] { "DOMAIN_ID" }, new String[] { "DM_DOMAIN_ID" }, 2))), (Criteria)null));
            if (dObj != null && dObj.isEmpty()) {
                return false;
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    static {
        USER_ATTR_COL_MAP = new HashMap<Long, Column>() {
            {
                this.put(108L, Column.getColumn("ManagedUser", "LAST_NAME"));
                this.put(114L, Column.getColumn("ManagedUser", "PHONE_NUMBER"));
                this.put(106L, Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
                this.put(109L, Column.getColumn("ManagedUser", "FIRST_NAME"));
                this.put(110L, Column.getColumn("ManagedUser", "MIDDLE_NAME"));
                this.put(111L, Column.getColumn("ManagedUser", "DISPLAY_NAME"));
            }
        };
        GROUP_ATTR_COL_MAP = new HashMap<Long, Column>() {
            {
                this.put(103L, Column.getColumn("CustomGroupExtn", "GROUP_DESCRIPTION"));
            }
        };
    }
}
