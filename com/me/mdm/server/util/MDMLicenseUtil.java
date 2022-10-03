package com.me.mdm.server.util;

import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.ems.framework.common.api.utils.APIException;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Collection;
import org.json.JSONObject;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.mdm.server.enrollment.api.model.LicenseResolveModel;
import java.util.logging.Logger;

public class MDMLicenseUtil
{
    private static Logger logger;
    
    public void moveRemainingDevicesToWaitingForLicenseStatus(final LicenseResolveModel licenseResolveModel) throws Exception {
        try {
            final List givenDeviceIDs = licenseResolveModel.getMobileDeviceIDs();
            final Boolean isListToBeManaged = licenseResolveModel.getIsListToBeManaged();
            MDMLicenseUtil.logger.log(Level.INFO, "List of devices selected while license upgrade: {0} with decision isListToBeManaged: {1}", new Object[] { givenDeviceIDs.toString(), isListToBeManaged });
            final Criteria enrolledCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)givenDeviceIDs.toArray(), 8);
            final Criteria mdmDeviceTypeCriteria = MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck();
            final SelectQuery query = this.getDeviceDetailsQueryForLicense(givenDeviceIDs);
            final Boolean isGivenDeviceListManaged = this.validateGivenDevices(givenDeviceIDs);
            if (isListToBeManaged) {
                final Criteria licenseResolveCriteria = MDMApiFactoryProvider.getMDMUtilAPI().getLicenseResolveCriteria(givenDeviceIDs);
                query.setCriteria(licenseResolveCriteria);
            }
            else {
                if (!isGivenDeviceListManaged) {
                    MDMLicenseUtil.logger.log(Level.INFO, "Empty list is passed to move devices to waiting for license state");
                    throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.license.atleast_one_needed", new Object[0]) });
                }
                query.setCriteria(enrolledCriteria.and(deviceCriteria));
            }
            final DataObject dObj = DataAccess.get(query);
            final List remarksList = new ArrayList();
            final List toAwaitingDeviceIDs = new ArrayList();
            final Iterator toAwaitingLicenseDeviceIter = dObj.getRows("ManagedDevice");
            while (toAwaitingLicenseDeviceIter.hasNext()) {
                final Row managedDeviceRow = toAwaitingLicenseDeviceIter.next();
                final Long resourceID = (Long)managedDeviceRow.get("RESOURCE_ID");
                final String udid = (String)managedDeviceRow.get("UDID");
                final Row deviceInfoRow = dObj.getRow("MdDeviceInfo", new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0), new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                final String serialNo = (String)((deviceInfoRow.get("SERIAL_NUMBER") == null) ? "--" : deviceInfoRow.get("SERIAL_NUMBER"));
                final Row deviceExtnRow = dObj.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceID, 0), new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
                final String deviceName = (String)deviceExtnRow.get("NAME");
                toAwaitingDeviceIDs.add(resourceID);
                final String remarksArg = deviceName + "@@@" + serialNo + "@@@" + udid;
                remarksList.add(remarksArg);
            }
            this.validateIfDeviceExceedsRange(toAwaitingDeviceIDs.size());
            MDMLicenseUtil.logger.log(Level.INFO, "List of managed devices to be moved to waiting for license status: {0} ", toAwaitingDeviceIDs.toString());
            final JSONObject managedDeviceDetails = new JSONObject();
            managedDeviceDetails.put("MANAGED_STATUS", 6);
            managedDeviceDetails.put("REMARKS", (Object)"dc.db.mdm.managedStatus.waiting_for_license");
            managedDeviceDetails.put("resourceIds", (Collection)toAwaitingDeviceIDs);
            ManagedDeviceHandler.getInstance().bulkUpdateManagedDeviceDetails(managedDeviceDetails);
            MDMEventLogHandler.getInstance().addEvent(2001, licenseResolveModel.getUserName(), "mdm.license.moved_to_license_audit", remarksList, licenseResolveModel.getCustomerId(), System.currentTimeMillis());
            ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
            MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw ex;
            }
            MDMLicenseUtil.logger.log(Level.SEVERE, "Exception in moving remaining managed devices to waiting for license status ", ex);
            throw new APIException("COM0004");
        }
    }
    
    private SelectQuery getDeviceDetailsQueryForLicense(final List givenDeviceIDs) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        query.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "MODEL_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
        final Criteria enrolledCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)givenDeviceIDs.toArray(), 8);
        query.setCriteria(enrolledCriteria.and(deviceCriteria));
        return query;
    }
    
    private boolean validateGivenDevices(final List givenDeviceIDs) throws DataAccessException {
        if (givenDeviceIDs.size() <= 0) {
            return false;
        }
        final DataObject checkDO = DataAccess.get(this.getDeviceDetailsQueryForLicense(givenDeviceIDs));
        final Iterator deviceIter = checkDO.getRows("ManagedDevice");
        final List deviceIDs = DBUtil.getColumnValuesAsList(deviceIter, "RESOURCE_ID");
        final List devicesToBeValidated = new ArrayList(givenDeviceIDs);
        devicesToBeValidated.removeAll(deviceIDs);
        if (devicesToBeValidated.size() > 0) {
            MDMLicenseUtil.logger.log(Level.INFO, "List of devices which are not managed: {0} ", new Object[] { devicesToBeValidated });
            throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.license.devices_not_managed", new Object[] { devicesToBeValidated }) });
        }
        return true;
    }
    
    private void validateIfDeviceExceedsRange(final int toAwaitingCount) {
        final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount(MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck());
        final int allowedLimit = (int)(managedDeviceCount * 0.25);
        final Boolean isListIsOfAllowedRange = managedDeviceCount <= 25 || toAwaitingCount <= 25 || toAwaitingCount <= allowedLimit;
        if (!isListIsOfAllowedRange && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("BulkLicenseCountResolve")) {
            MDMLicenseUtil.logger.log(Level.INFO, "Given device list count exceeds the maximum allowed range of license resolving count:{0}", allowedLimit);
            throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.license.resolving_count_exceeds_range", new Object[] { allowedLimit }) });
        }
    }
    
    static {
        MDMLicenseUtil.logger = Logger.getLogger("MDMEnrollment");
    }
}
