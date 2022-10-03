package com.me.mdm.server.search;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Properties;
import java.util.ArrayList;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.search.SuggestQueryIfc;

public class MDMDeviceSearchSuggestionQuery implements SuggestQueryIfc
{
    private static Logger logger;
    
    public List getSuggestData(final String searchString, final String domainName) {
        List dataList = null;
        Properties dataProperty = null;
        Criteria cri = null;
        Connection conn = null;
        DataSet ds = null;
        SelectQuery deviceModelResourceQuery = null;
        try {
            final Criteria searchCri = new Criteria(new Column("ManagedDeviceExtn", "NAME"), (Object)searchString, 12, false);
            deviceModelResourceQuery = MDMUtil.getInstance().getMDMDeviceResourceQuery();
            final Criteria androidPlatformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria iOSPlatformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria platform = androidPlatformCri.or(iOSPlatformCri);
            cri = deviceModelResourceQuery.getCriteria().and(searchCri).and(platform);
            deviceModelResourceQuery.setCriteria(cri);
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)deviceModelResourceQuery, conn);
            dataList = new ArrayList();
            while (ds.next()) {
                dataProperty = new Properties();
                final JSONObject deviceMap = new JSONObject();
                deviceMap.put("dataValue", ds.getValue("ManagedDeviceExtn.NAME"));
                deviceMap.put("dataId", (Object)("" + ds.getValue("RESOURCE_ID")));
                deviceMap.put("platformType", ds.getValue("PLATFORM_TYPE"));
                deviceMap.put("udid", ds.getValue("UDID"));
                deviceMap.put("customerId", (Object)("" + ds.getValue("CUSTOMER_ID")));
                ((Hashtable<String, Object>)dataProperty).put("dataValue", deviceMap.get("dataValue"));
                ((Hashtable<String, String>)dataProperty).put("dataId", deviceMap.toString());
                dataList.add(dataProperty);
            }
            return dataList;
        }
        catch (final Exception ex) {
            MDMDeviceSearchSuggestionQuery.logger.log(Level.WARNING, "Device Search - MDMDeviceSearchSuggestionQuery : Exception occured - getSuggestData", ex);
        }
        finally {
            MDMCustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return dataList;
    }
    
    static {
        MDMDeviceSearchSuggestionQuery.logger = Logger.getLogger(MDMDeviceSearchSuggestionQuery.class.getName());
    }
}
