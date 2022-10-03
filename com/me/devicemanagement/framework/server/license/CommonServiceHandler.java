package com.me.devicemanagement.framework.server.license;

import com.adventnet.persistence.DataAccessException;
import java.util.List;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import com.me.devicemanagement.framework.server.search.AdvSearchCommonUtil;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.dms.DMSDownloadUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class CommonServiceHandler implements ServiceHandler
{
    private static Logger logger;
    private static CommonServiceHandler commonServiceHandler;
    protected static String[] oldProducts;
    protected static String[] newProducts;
    
    public static CommonServiceHandler getInstance() {
        if (CommonServiceHandler.commonServiceHandler == null) {
            CommonServiceHandler.commonServiceHandler = new CommonServiceHandler();
        }
        return CommonServiceHandler.commonServiceHandler;
    }
    
    @Override
    public void migrate(final JSONObject licenseDiffChecker, final License oldLicenseobject, final License newLicenseObject) {
        LicenseDiffChecker.getInstance().clearLicenseChangeSet();
        LicenseProvider.getInstance().setmoduleProMap();
        LicenseDiffChecker.getInstance().getLicenseDiff();
    }
    
    @Override
    public void StartUp() {
        this.resetSchedulers();
    }
    
    @Override
    public void reset(final JSONObject licenseDiffChecker, final License oldLicenseobject, final License newLicenseObject) {
        try {
            this.resetSchedulers();
            this.resetAdvSearch();
            this.resetCustomerInfoUtil();
            DMSDownloadUtil.getInstance().clearLastModifiedForResync();
        }
        catch (final Exception e) {
            CommonServiceHandler.logger.log(Level.SEVERE, null, e);
        }
    }
    
    private void resetCustomerInfoUtil() {
        CustomerInfoUtil.resetCustomerInfoUtil();
    }
    
    private void resetAdvSearch() {
        if (EMSProductUtil.isEMSFlowSupportedForCurrentProduct()) {
            final String productCode = AdvSearchCommonUtil.productCode = EMSProductUtil.getEMSProductCode().get(0).toString().toLowerCase();
            AdvSearchCommonUtil.isProductInEMS = true;
            AdvSearchCommonUtil.search_index_home = AdvSearchCommonUtil.SERVER_HOME + File.separator + AdvSearchCommonUtil.getAdvSearchIndexDirectory() + File.separator + productCode + "_" + AdvSearchCommonUtil.getAdvSearchIndexDirectory();
            AdvSearchCommonUtil.search_main_index_file_name = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + productCode + "_" + "searchMainIndex" + ".json";
        }
        else {
            AdvSearchCommonUtil.search_index_home = AdvSearchCommonUtil.SERVER_HOME + File.separator + AdvSearchCommonUtil.getAdvSearchIndexDirectory();
            AdvSearchCommonUtil.search_main_index_file_name = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + "searchMainIndex" + ".json";
        }
        AdvSearchCommonUtil.static_action_index_dir = AdvSearchCommonUtil.search_index_home + File.separator + "staticactionindex";
        AdvSearchCommonUtil.doc_index_dir = AdvSearchCommonUtil.search_index_home + File.separator + "docindex";
        AdvSearchCommonUtil.spell_dir = AdvSearchCommonUtil.search_index_home + File.separator + "spellindex";
    }
    
    private void resetSchedulers() {
        for (final String str : CommonServiceHandler.oldProducts) {
            final ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(CommonServiceHandler.newProducts));
            if (!arrayList.contains(str)) {
                changeSchedulerStatus(true, str.toLowerCase());
            }
        }
        for (final String str : CommonServiceHandler.newProducts) {
            final ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(CommonServiceHandler.oldProducts));
            if (!arrayList.contains(str)) {
                changeSchedulerStatus(false, str.toLowerCase());
            }
        }
    }
    
    private static void changeSchedulerStatus(final boolean status, final String product) {
        final Properties tasksList = new Properties();
        try {
            String taskFilePath = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome()).getCanonicalPath();
            taskFilePath = taskFilePath + File.separator + "conf" + File.separator + product + "_" + "Unused_Scheduletasks.properties";
            File f = new File(taskFilePath);
            if (!f.exists()) {
                CommonServiceHandler.logger.log(Level.INFO, "File does not exist at path : {0}\n SCHEDULER STATUS NOT CHANGED", taskFilePath);
                return;
            }
            tasksList.load(new FileInputStream(taskFilePath));
            for (final String key : tasksList.stringPropertyNames()) {
                changeSchedulerState(key, status);
            }
            taskFilePath = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome()).getCanonicalPath();
            taskFilePath = taskFilePath + File.separator + "conf" + File.separator + product + "_" + "Unused_Schedules.properties";
            f = new File(taskFilePath);
            if (!f.exists()) {
                CommonServiceHandler.logger.log(Level.INFO, "File does not exist at path : {0}\n SCHEDULER STATUS NOT CHANGED", taskFilePath);
                return;
            }
            tasksList.clear();
            tasksList.load(new FileInputStream(taskFilePath));
            for (final String key : tasksList.stringPropertyNames()) {
                ApiFactoryProvider.getSchedulerAPI().setSchedulerState(status, key);
            }
        }
        catch (final Exception ex) {
            CommonServiceHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private static void changeSchedulerState(final String taskName, final boolean dcStatus) throws DataAccessException {
        final List schedulerList = getSchedulerList(taskName);
        CommonServiceHandler.logger.log(Level.SEVERE, "SuspendScheduler invoked.. taskName->{0}, scheduler list-{1} ", new Object[] { taskName, schedulerList });
        final Iterator list = schedulerList.iterator();
        while (list.hasNext()) {
            final String schedulerName = String.valueOf(list.next());
            ApiFactoryProvider.getSchedulerAPI().setSchedulerState(dcStatus, schedulerName);
        }
    }
    
    private static List getSchedulerList(final String taskName) {
        List schedulerList = new ArrayList();
        try {
            schedulerList = ApiFactoryProvider.getSchedulerAPI().getScheduleNamesForWorkflow(taskName);
        }
        catch (final Exception e) {
            CommonServiceHandler.logger.log(Level.SEVERE, "Exception occured while getting scheduler names for corresponding task name.taskName->{0} ,schedulerList->{1}", new Object[] { taskName, schedulerList });
            CommonServiceHandler.logger.log(Level.SEVERE, "", e);
        }
        return schedulerList;
    }
    
    public void migrationHandling() {
        CommonServiceHandler.oldProducts = new String[] { License.getOldLicenseObject().getProductCode() };
        CommonServiceHandler.newProducts = new String[] { License.getNewLicenseObject().getProductCode() };
        this.reset(null, null, null);
        this.migrate(null, null, null);
        this.callPreviousProductReset(CommonServiceHandler.oldProducts, CommonServiceHandler.newProducts, true);
        this.callMigratedProductMigrate(CommonServiceHandler.newProducts, true);
    }
    
    public void migrationHandling(final String oldProductCode, final String newProductCode) {
        CommonServiceHandler.oldProducts = new String[] { oldProductCode };
        CommonServiceHandler.newProducts = new String[] { newProductCode };
        this.reset(null, null, null);
        this.migrate(null, null, null);
        this.callPreviousProductReset(CommonServiceHandler.oldProducts, CommonServiceHandler.newProducts, false);
        this.callMigratedProductMigrate(CommonServiceHandler.newProducts, false);
    }
    
    private void callMigratedProductMigrate(final String[] newProducts, final boolean isDiffObjectNeeded) {
        final LicenseFactoryImpl licenseFactory = new LicenseFactoryImpl();
        JSONObject licenseDiff = null;
        License oldLicenseObject = null;
        License newLicenseObject = null;
        if (isDiffObjectNeeded) {
            licenseDiff = LicenseDiffChecker.getInstance().getLicenseDiff();
            oldLicenseObject = License.getOldLicenseObject();
            newLicenseObject = License.getNewLicenseObject();
        }
        for (final String prods : newProducts) {
            try {
                final ServiceHandler license = licenseFactory.getLicenseObject(prods);
                if (license != null) {
                    license.migrate(licenseDiff, oldLicenseObject, newLicenseObject);
                }
            }
            catch (final ClassNotFoundException e) {
                CommonServiceHandler.logger.log(Level.SEVERE, null, e);
            }
            catch (final IllegalAccessException e2) {
                CommonServiceHandler.logger.log(Level.SEVERE, null, e2);
            }
            catch (final InstantiationException e3) {
                CommonServiceHandler.logger.log(Level.SEVERE, null, e3);
            }
        }
    }
    
    private void callPreviousProductReset(final String[] oldProducts, final String[] newProducts, final boolean isDiffObjectReq) {
        final ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(newProducts));
        final LicenseFactoryImpl licenseFactory = new LicenseFactoryImpl();
        JSONObject licenseDiff = null;
        License oldLicenseObject = null;
        License newLicenseObject = null;
        if (isDiffObjectReq) {
            licenseDiff = LicenseDiffChecker.getInstance().getLicenseDiff();
            oldLicenseObject = License.getOldLicenseObject();
            newLicenseObject = License.getNewLicenseObject();
        }
        for (final String prods : oldProducts) {
            try {
                final ServiceHandler license = licenseFactory.getLicenseObject(prods);
                if (license != null) {
                    license.reset(licenseDiff, oldLicenseObject, newLicenseObject);
                }
            }
            catch (final ClassNotFoundException e) {
                CommonServiceHandler.logger.log(Level.SEVERE, null, e);
            }
            catch (final IllegalAccessException e2) {
                CommonServiceHandler.logger.log(Level.SEVERE, null, e2);
            }
            catch (final InstantiationException e3) {
                CommonServiceHandler.logger.log(Level.SEVERE, null, e3);
            }
        }
    }
    
    static {
        CommonServiceHandler.logger = Logger.getLogger(CommonServiceHandler.class.getName());
        CommonServiceHandler.commonServiceHandler = null;
        CommonServiceHandler.oldProducts = new String[0];
        CommonServiceHandler.newProducts = new String[0];
    }
}
