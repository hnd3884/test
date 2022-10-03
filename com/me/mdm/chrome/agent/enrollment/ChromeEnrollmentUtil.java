package com.me.mdm.chrome.agent.enrollment;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class ChromeEnrollmentUtil
{
    public static Logger logger;
    public static final Integer CHROME_STATUS_ENROLLED;
    public static final Integer CHROME_STATUS_UNENROLLED;
    public static ChromeEnrollmentUtil chromeEnrollmentUtil;
    
    public static ChromeEnrollmentUtil getInstance() {
        if (ChromeEnrollmentUtil.chromeEnrollmentUtil == null) {
            ChromeEnrollmentUtil.chromeEnrollmentUtil = new ChromeEnrollmentUtil();
        }
        return ChromeEnrollmentUtil.chromeEnrollmentUtil;
    }
    
    public void addOrUpdateChromeDeviceStatus(final Long customerId, final List<String> deviceList, final int status) throws DataAccessException {
        ChromeEnrollmentUtil.logger.log(Level.INFO, "Going to addOrUpdateChromeDeviceStatus for deviceList {0}", deviceList);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ChromeDeviceManagedStatus"));
        final Criteria customerCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deviceListCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "UDID"), (Object)deviceList.toArray(), 8);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(customerCriteria.and(deviceListCriteria));
        final DataObject dO = DataAccess.get(sQuery);
        final Iterator it = dO.getRows("ChromeDeviceManagedStatus");
        while (it.hasNext()) {
            final Row row = it.next();
            row.set("MANAGED_STATUS", (Object)status);
            deviceList.remove(row.get("UDID"));
            dO.updateRow(row);
        }
        for (final String udid : deviceList) {
            final Row row2 = new Row("ChromeDeviceManagedStatus");
            row2.set("CUSTOMER_ID", (Object)customerId);
            row2.set("UDID", (Object)udid);
            row2.set("MANAGED_STATUS", (Object)status);
            dO.addRow(row2);
        }
        DataAccess.update(dO);
    }
    
    static {
        ChromeEnrollmentUtil.logger = Logger.getLogger("MDMEnrollment");
        CHROME_STATUS_ENROLLED = 1;
        CHROME_STATUS_UNENROLLED = 2;
        ChromeEnrollmentUtil.chromeEnrollmentUtil = null;
    }
}
