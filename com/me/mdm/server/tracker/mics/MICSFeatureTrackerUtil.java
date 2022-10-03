package com.me.mdm.server.tracker.mics;

import com.adventnet.ds.query.SelectQuery;
import java.util.HashSet;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import org.json.JSONObject;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MICSFeatureTrackerUtil
{
    public static final Logger LOGGER;
    
    public static void selfEnrollmentStart() {
        try {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "MICS Enrollment Tracking - Self Enrollment Start");
            final MICSSelfEnrollmentFeatureController selfEnrollmentFeatureController = new MICSSelfEnrollmentFeatureController();
            selfEnrollmentFeatureController.addTrackingData(selfEnrollmentFeatureController.getTrackingJSON(new ArrayList<MICSMailerAPI.MICSMailerSubFeature>(Arrays.asList(MICSEnrollmentFeatureController.EnrollmentStatus.START))));
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.SEVERE, "Exception while self enrollment start post ", e);
        }
    }
    
    public static void selfEnrollmentComplete() {
        try {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "MICS Enrollment Tracking - Self Enrollment Complete");
            final MICSSelfEnrollmentFeatureController selfEnrollmentFeatureController = new MICSSelfEnrollmentFeatureController();
            selfEnrollmentFeatureController.addTrackingData(selfEnrollmentFeatureController.getTrackingJSON(new ArrayList<MICSMailerAPI.MICSMailerSubFeature>(Arrays.asList(MICSEnrollmentFeatureController.EnrollmentStatus.COMPLETE))));
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.SEVERE, "Exception while self enrollment complete post ", e);
        }
    }
    
    public static void inviteEnrollmentStart(final String splatform) {
        try {
            switch (Integer.parseInt(splatform)) {
                case 1: {
                    appleInviteEnrollmentStart();
                    break;
                }
                case 2: {
                    androidInviteEnrollmentStart();
                    break;
                }
                case 3: {
                    windowsInviteEnrollmentStart();
                    break;
                }
            }
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.SEVERE, "Exception - ", e);
        }
    }
    
    public static void appleInviteEnrollmentStart() {
        try {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "MICS Enrollment Tracking - Apple Invite Enrollment Start");
            final MICSAppleEnrollmentFeatureController micsAppleEnrollmentFeatureController = new MICSAppleEnrollmentFeatureController();
            final ArrayList subFeatures = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAppleEnrollmentFeatureController.EnrollmentType.INVITE, MICSEnrollmentFeatureController.EnrollmentStatus.START));
            micsAppleEnrollmentFeatureController.addTrackingData(micsAppleEnrollmentFeatureController.getTrackingJSON(subFeatures));
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "Exception while ios invite Enrollment start post", e);
        }
    }
    
    public static void androidInviteEnrollmentStart() {
        try {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "MICS Enrollment Tracking - Android Invite Enrollment Start");
            final MICSAndroidEnrollmentFeatureController micsAndroidEnrollmentFeatureController = new MICSAndroidEnrollmentFeatureController();
            final ArrayList subFeatures = new ArrayList((Collection<? extends E>)Arrays.asList(MICSAndroidEnrollmentFeatureController.EnrollmentType.INVITE, MICSEnrollmentFeatureController.EnrollmentStatus.START));
            micsAndroidEnrollmentFeatureController.addTrackingData(micsAndroidEnrollmentFeatureController.getTrackingJSON(subFeatures));
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "Exception while android invite Enrollment start post", e);
        }
    }
    
    public static void windowsInviteEnrollmentStart() {
        try {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "MICS Enrollment Tracking - Windows Invite Enrollment Start");
            final MICSWindowsEnrollmentFeatureController micsWindowsEnrollmentFeatureController = new MICSWindowsEnrollmentFeatureController();
            final ArrayList subFeatures = new ArrayList((Collection<? extends E>)Arrays.asList(MICSWindowsEnrollmentFeatureController.EnrollmentType.INVITE, MICSEnrollmentFeatureController.EnrollmentStatus.START));
            micsWindowsEnrollmentFeatureController.addTrackingData(micsWindowsEnrollmentFeatureController.getTrackingJSON(subFeatures));
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "Exception while windows invite Enrollment start post", e);
        }
    }
    
    public static void appleAdminEnrollmentStart(final MICSMailerAPI.MICSMailerSubFeature enrollmentType) {
        try {
            final MICSAppleEnrollmentFeatureController micsAppleEnrollmentFeatureController = new MICSAppleEnrollmentFeatureController();
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "MICS Enrollment Tracking - Apple Android {0} Enrollment Start", ((MICSEnrollmentFeatureController.EnrollmentType)enrollmentType).getEnrollmentFeatureName());
            final ArrayList subFeatures = new ArrayList((Collection<? extends E>)Arrays.asList(enrollmentType, MICSEnrollmentFeatureController.EnrollmentStatus.START));
            micsAppleEnrollmentFeatureController.addTrackingData(micsAppleEnrollmentFeatureController.getTrackingJSON(subFeatures));
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.SEVERE, "Exception while apple admin enrollment data post ", e);
        }
    }
    
    public static void adminEnrollmentStart(final JSONObject enrollJSON, final int platform) {
        try {
            if (platform == 4) {
                MICSChromeEnrollmentFeatureController.addTrackingData(MICSEnrollmentFeatureController.EnrollmentStatus.START);
            }
            else {
                final String templateToken = String.valueOf(enrollJSON.get("TemplateToken"));
                final EnrollmentTemplateHandler enrollmentTemplateHandler = new EnrollmentTemplateHandler();
                final JSONObject templateJSON = enrollmentTemplateHandler.getEnrollmentTemplateForTemplateToken(templateToken);
                final MICSAndroidEnrollmentFeatureController micsAndroidEnrollmentFeatureController = new MICSAndroidEnrollmentFeatureController();
                final MICSEnrollmentFeatureController.EnrollmentType enrollmentType = MICSAndroidEnrollmentFeatureController.getEnrollmentType(templateJSON.getInt("TEMPLATE_TYPE"));
                if (enrollmentType != null) {
                    MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "MICS Enrollment Tracking - Android Admin {0} Enrollment Start ", enrollmentType.getEnrollmentFeatureName());
                    final ArrayList subFeatures = new ArrayList((Collection<? extends E>)Arrays.asList(enrollmentType, MICSEnrollmentFeatureController.EnrollmentStatus.START));
                    micsAndroidEnrollmentFeatureController.addTrackingData(micsAndroidEnrollmentFeatureController.getTrackingJSON(subFeatures));
                }
            }
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.SEVERE, "Exception while android admin data post ", e);
        }
    }
    
    public static void windowsAdminEnrollmentStart(final int templateType) {
        try {
            final MICSWindowsEnrollmentFeatureController micsWindowsEnrollmentFeatureController = new MICSWindowsEnrollmentFeatureController();
            final MICSEnrollmentFeatureController.EnrollmentType enrollmentType = MICSWindowsEnrollmentFeatureController.getEnrollmentType(templateType);
            if (enrollmentType != null) {
                MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "MICS Enrollment Tracking - Windows Admin {0} Enrollment Start", enrollmentType.getEnrollmentFeatureName());
                final ArrayList subFeatures = new ArrayList((Collection<? extends E>)Arrays.asList(enrollmentType, MICSEnrollmentFeatureController.EnrollmentStatus.START));
                micsWindowsEnrollmentFeatureController.addTrackingData(micsWindowsEnrollmentFeatureController.getTrackingJSON(subFeatures));
            }
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.SEVERE, "Exception while windows admin enrollment post", e);
        }
    }
    
    public static void addAppRepositoryAppDelete(final Long[] packageIds, final Long customerId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"));
            selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"));
            selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageIds, 8));
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            final HashSet<String> platforms = new HashSet<String>();
            while (dmDataSetWrapper.next()) {
                final Integer platformType = (Integer)dmDataSetWrapper.getValue("PLATFORM_TYPE");
                final Object filePath = dmDataSetWrapper.getValue("APP_FILE_LOC");
                final Integer packageType = (Integer)dmDataSetWrapper.getValue("PACKAGE_TYPE");
                if (platformType != null && packageType != null) {
                    final String msiApplication = (filePath != null && filePath.toString().endsWith("msi")) ? "msi" : "";
                    if (platforms.contains(platformType + msiApplication)) {
                        continue;
                    }
                    final boolean isEnterpriseApp = 2 == packageType;
                    MICSAppRepositoryFeatureController.addTrackingData(platformType, MICSAppRepositoryFeatureController.AppOperation.DELETE_APP, isEnterpriseApp, filePath.toString().endsWith("msi"));
                    platforms.add(platformType + msiApplication);
                }
            }
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.SEVERE, "Exception while app repository delete tracking", e);
        }
    }
    
    public static void addHomePageAccess() {
        try {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "MICS home page tracking");
            final MICSMailerFeatureController homePage = micsMailerSubFeature -> null;
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("feature", (Object)"HomePage");
            homePage.addTrackingData(jsonObject);
        }
        catch (final Exception e) {
            MICSFeatureTrackerUtil.LOGGER.log(Level.INFO, "Exception while tracking MICS home page ", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
