package com.me.mdm.server.tracker.mics;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class MICSManagedDeviceListener extends ManagedDeviceListener
{
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Entering MicsManagedDeviceListener.deviceRegistered...");
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)deviceEvent.enrollmentRequestId, 0);
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
            selectQuery.setCriteria(criteria);
            DataObject dataObject = null;
            switch (deviceEvent.platformType) {
                case 1: {
                    selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "AppleConfigDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                    selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                    selectQuery.addSelectColumn(Column.getColumn("AppleConfigDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                    selectQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                    selectQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"));
                    dataObject = MDMUtil.getPersistence().get(selectQuery);
                    final MICSAppleEnrollmentFeatureController micsAppleEnrollmentFeatureController = new MICSAppleEnrollmentFeatureController();
                    final int enrollmentType = getEnrollmentType(dataObject);
                    if (enrollmentType == 1) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for IOS - Invite");
                        final ArrayList subfeatures = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAppleEnrollmentFeatureController.EnrollmentType.INVITE, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsAppleEnrollmentFeatureController.addTrackingData(micsAppleEnrollmentFeatureController.getTrackingJSON(subfeatures));
                        break;
                    }
                    if (dataObject.size("AppleConfigDeviceForEnrollment") > 0) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for IOS - Apple Configurator");
                        final ArrayList subfeatures = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAppleEnrollmentFeatureController.EnrollmentType.APPLE_CONFIGURATOR, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsAppleEnrollmentFeatureController.addTrackingData(micsAppleEnrollmentFeatureController.getTrackingJSON(subfeatures));
                        break;
                    }
                    if (dataObject.size("AppleDEPDeviceForEnrollment") > 0) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for IOS - DEP");
                        final ArrayList subfeatures = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAppleEnrollmentFeatureController.EnrollmentType.DEP, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsAppleEnrollmentFeatureController.addTrackingData(micsAppleEnrollmentFeatureController.getTrackingJSON(subfeatures));
                        break;
                    }
                    if (enrollmentType == 2) {
                        MICSFeatureTrackerUtil.selfEnrollmentComplete();
                        break;
                    }
                    MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for IOS - Not matched. Enrollment Type {0}", enrollmentType);
                    break;
                }
                case 2: {
                    selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "AndroidQRDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                    selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "AndroidZTDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                    selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "AndroidNFCDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                    selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "KNOXMobileDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                    selectQuery.addSelectColumn(Column.getColumn("AndroidQRDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                    selectQuery.addSelectColumn(Column.getColumn("AndroidZTDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                    selectQuery.addSelectColumn(Column.getColumn("AndroidNFCDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                    selectQuery.addSelectColumn(Column.getColumn("KNOXMobileDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                    dataObject = MDMUtil.getPersistence().get(selectQuery);
                    final int enrollmentType = getEnrollmentType(dataObject);
                    final MICSAndroidEnrollmentFeatureController micsAndroidEnrollmentFeatureController = new MICSAndroidEnrollmentFeatureController();
                    if (enrollmentType == 1) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Android - Invite");
                        final ArrayList subfeatures2 = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAndroidEnrollmentFeatureController.EnrollmentType.INVITE, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsAndroidEnrollmentFeatureController.addTrackingData(micsAndroidEnrollmentFeatureController.getTrackingJSON(subfeatures2));
                        break;
                    }
                    if (dataObject.size("AndroidQRDeviceForEnrollment") > 0) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Android - Admin QR");
                        final ArrayList subfeatures2 = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAndroidEnrollmentFeatureController.EnrollmentType.EMM, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsAndroidEnrollmentFeatureController.addTrackingData(micsAndroidEnrollmentFeatureController.getTrackingJSON(subfeatures2));
                        break;
                    }
                    if (dataObject.size("AndroidZTDeviceForEnrollment") > 0) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Android - Zero Touch");
                        final ArrayList subfeatures2 = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAndroidEnrollmentFeatureController.EnrollmentType.ZERO_TOUCH, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsAndroidEnrollmentFeatureController.addTrackingData(micsAndroidEnrollmentFeatureController.getTrackingJSON(subfeatures2));
                        break;
                    }
                    if (dataObject.size("AndroidNFCDeviceForEnrollment") > 0) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Android - NFC");
                        final ArrayList subfeatures2 = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAndroidEnrollmentFeatureController.EnrollmentType.NFC, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsAndroidEnrollmentFeatureController.addTrackingData(micsAndroidEnrollmentFeatureController.getTrackingJSON(subfeatures2));
                        break;
                    }
                    if (dataObject.size("KNOXMobileDeviceForEnrollment") > 0) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Android - KNOX");
                        final ArrayList subfeatures2 = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAndroidEnrollmentFeatureController.EnrollmentType.KNOX, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsAndroidEnrollmentFeatureController.addTrackingData(micsAndroidEnrollmentFeatureController.getTrackingJSON(subfeatures2));
                        break;
                    }
                    if (enrollmentType == 2) {
                        MICSFeatureTrackerUtil.selfEnrollmentComplete();
                        break;
                    }
                    MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Android - Not matched. Enrollment Type {0}", enrollmentType);
                    break;
                }
                case 3: {
                    selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "WindowsICDDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                    selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "WindowsLaptopDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                    selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "WinAzureADDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                    selectQuery.addSelectColumn(Column.getColumn("WindowsICDDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                    selectQuery.addSelectColumn(Column.getColumn("WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                    selectQuery.addSelectColumn(Column.getColumn("WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                    dataObject = MDMUtil.getPersistence().get(selectQuery);
                    final int enrollmentType = getEnrollmentType(dataObject);
                    final MICSWindowsEnrollmentFeatureController micsWindowsEnrollmentFeatureController = new MICSWindowsEnrollmentFeatureController();
                    if (enrollmentType == 1) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Windows - Invite");
                        final ArrayList subfeatures3 = new ArrayList((Collection<? extends E>)Arrays.asList(MICSWindowsEnrollmentFeatureController.EnrollmentType.INVITE, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsWindowsEnrollmentFeatureController.addTrackingData(micsWindowsEnrollmentFeatureController.getTrackingJSON(subfeatures3));
                        break;
                    }
                    if (dataObject.size("WindowsICDDeviceForEnrollment") > 0) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Windows - Mobile");
                        final ArrayList subfeatures3 = new ArrayList((Collection<? extends E>)Arrays.asList(MICSWindowsEnrollmentFeatureController.EnrollmentType.MOBILE, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsWindowsEnrollmentFeatureController.addTrackingData(micsWindowsEnrollmentFeatureController.getTrackingJSON(subfeatures3));
                        break;
                    }
                    if (dataObject.size("WindowsLaptopDeviceForEnrollment") > 0) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Windows - Labtop");
                        final ArrayList subfeatures3 = new ArrayList((Collection<? extends E>)Arrays.asList(MICSWindowsEnrollmentFeatureController.EnrollmentType.LAPTOP, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsWindowsEnrollmentFeatureController.addTrackingData(micsWindowsEnrollmentFeatureController.getTrackingJSON(subfeatures3));
                        break;
                    }
                    if (dataObject.size("WinAzureADDeviceForEnrollment") > 0) {
                        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Windows - Azure");
                        final ArrayList subFeatures = new ArrayList((Collection<? extends E>)Arrays.asList(MICSWindowsEnrollmentFeatureController.EnrollmentType.AZURE, MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE));
                        micsWindowsEnrollmentFeatureController.addTrackingData(micsWindowsEnrollmentFeatureController.getTrackingJSON(subFeatures));
                        break;
                    }
                    if (enrollmentType == 2) {
                        MICSFeatureTrackerUtil.selfEnrollmentComplete();
                        break;
                    }
                    MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Mics Enrollment Tracking for Windows - Not matched. Enrollment Type {0}", enrollmentType);
                    break;
                }
                case 4: {
                    MICSChromeEnrollmentFeatureController.addTrackingData(MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE);
                    break;
                }
            }
        }
        catch (final Exception e) {
            MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Exception in MicsManagedDeviceListener ", e);
        }
        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Exiting MicsManagedDeviceListener.deviceRegistered...");
    }
    
    private static int getEnrollmentType(final DataObject dataObject) throws DataAccessException {
        Row row = dataObject.getFirstRow("DeviceEnrollmentRequest");
        final int enrollmentType = (int)row.get("ENROLLMENT_TYPE");
        if (dataObject.size("EnrollmentTemplate") > 0) {
            row = dataObject.getFirstRow("EnrollmentTemplate");
        }
        else {
            row = null;
        }
        MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Enrollment Type : {0} and row {1}", new Object[] { enrollmentType, row });
        return enrollmentType;
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"));
        final Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)deviceEvent.enrollmentRequestId, 0);
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_NAME"));
        selectQuery.setCriteria(criteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            getEnrollmentType(dataObject);
        }
        catch (final DataAccessException var5) {
            MICSManagedDeviceListener.mdmlogger.log(Level.INFO, "Exception while managed listener");
        }
    }
}
