package com.me.devicemanagement.framework.server.general;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.ems.framework.uac.api.v1.model.User;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.license.CommonServiceHandler;
import java.util.List;
import java.io.DataOutputStream;
import java.util.Map;
import com.adventnet.i18n.MultiplePropertiesResourceBundleControl;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import java.util.Properties;
import java.net.URL;

public interface UtilAccessAPI
{
    String getServerHome();
    
    String getServerBinUrl();
    
    Long getCustomerID();
    
    String getServerURL();
    
    String getServerURLForMailNotification();
    
    String getStaticServerURL();
    
    int getWebServerPort();
    
    int getSSLPort();
    
    int getHttpServerPort();
    
    String getHttpServerPingURL();
    
    HttpsURLConnection getCreatorConnection(final URL p0, final String p1, final boolean p2, final boolean p3, final int p4, final String p5, final Properties p6);
    
    void initSSLUtil();
    
    String getSecret(final String p0);
    
    String getStaticServerVersion();
    
    String getTrustRootCertificateFilePath() throws Exception;
    
    Properties getWebServerSettings() throws Exception;
    
    default void updateWebServerSettings(final Properties newProps, final boolean append) {
        throw new UnsupportedOperationException("not supported");
    }
    
    default void removeWebServerSettings(final ArrayList<String> keys) {
        throw new UnsupportedOperationException("not supported");
    }
    
    ResourceBundle newCombinedBundle(final Locale p0, final ClassLoader p1, final boolean p2) throws IOException;
    
    MultiplePropertiesResourceBundleControl getMultipleResourceBundleControl();
    
    void addAdditionalProductMetaProps(final Map<String, Object> p0);
    
    String getServerName();
    
    String setUserVariables(final String p0);
    
    String getCustomerRelatedInfo(final String p0, final DataOutputStream p1, final String p2, final Properties p3);
    
    String getCustomerRelatedInfoForHugeFile(final String p0, final DataOutputStream p1, final String p2, final Properties p3, final boolean p4, final boolean p5);
    
    String getRebrandLogoPath();
    
    String getServerFQDNName();
    
    boolean isMSP();
    
    String getStaticClientURL();
    
    List getProductList();
    
    String getCustomerProduct();
    
    String dbRangeCriteriaReplaceString(final String p0, final String p1) throws Exception;
    
    default boolean isProductColumnCriteriaEnabled() {
        return true;
    }
    
    default void migrationHandling(final String oldProductCode, final String newProductCode) {
        CommonServiceHandler.getInstance().migrationHandling(newProductCode, newProductCode);
    }
    
    default String getRootCertificatePath() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    default void migrationHandling() {
        CommonServiceHandler.getInstance().migrationHandling();
    }
    
    default Properties getGeneralProperties() {
        return ProductUrlLoader.getInstance().getGeneralPropertiesDefaultImpl();
    }
    
    default Properties getGeneralProperties(final String productCode) {
        return ProductUrlLoader.getInstance().getGeneralPropertiesDefaultImpl(productCode);
    }
    
    ArrayList getEMSProductCode();
    
    default User constructDCUserObject(final DataObject loginDO) {
        return new DMUserHandler().constructDCUserObject(loginDO);
    }
    
    default String getUserNameForCurrentUser() {
        return "";
    }
    
    default void truncateTable(final String tableName) throws Exception {
        DataAccess.delete(tableName, (Criteria)null);
    }
    
    boolean isSummaryServer();
    
    boolean isProbeServer();
    
    void invokeOnpremiseComponents();
    
    default String getServerType() {
        return "STANDALONE";
    }
    
    default String getCrsBaseUrl() {
        return SyMUtil.getSyMParameter("STATIC_FILE_SERVER_BASE_URL");
    }
    
    default String getCurrentSyncingComponentCacheName() {
        return "DMSStaticServerSync";
    }
    
    default Properties addAdditionalPropertiesForDMSFeatureTask(final Properties taskAdditionalProps) {
        return taskAdditionalProps;
    }
}
