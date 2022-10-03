package com.me.mdm.app;

import org.json.JSONObject;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.io.File;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.adventnet.iam.security.SecurityUtil;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.files.app.MDMAppDeviceAuthorizer;
import com.me.mdm.server.factory.FileDownloadRegulatorAPI;

public class AppDeviceAuthorizer implements FileDownloadRegulatorAPI
{
    private static AppDeviceAuthorizer appDeviceAuthorizer;
    
    public static AppDeviceAuthorizer getInstance() {
        if (AppDeviceAuthorizer.appDeviceAuthorizer == null) {
            AppDeviceAuthorizer.appDeviceAuthorizer = new AppDeviceAuthorizer();
        }
        return AppDeviceAuthorizer.appDeviceAuthorizer;
    }
    
    public String getModule(final String requestURI) {
        return MDMAppDeviceAuthorizer.getModule(requestURI);
    }
    
    public Long getCustomerID(final String requestURI) {
        return MDMAppDeviceAuthorizer.getCustomerID(requestURI);
    }
    
    public String getFileIDhint(final String requestURI) {
        return MDMAppDeviceAuthorizer.getFileIDhint(requestURI);
    }
    
    public String getInternalRedirectPath(final HttpServletRequest request) {
        String internalURI = SecurityUtil.getRequestPath(request);
        if ("apache".equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
            internalURI = AppMgmtConstants.APP_BASE_PATH + SecurityUtil.getRequestPath(request);
        }
        else if ("nginx".equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
            internalURI = File.separator + "agent-files" + SecurityUtil.getRequestPath(request);
        }
        internalURI = internalURI.replace("\\", "/");
        return internalURI;
    }
    
    public SelectQuery getLoginUserAuthorizationQuery(final List<Long> fileIDs) {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        Criteria criteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"*AppMgmt*", 2, false);
        final Criteria fileIDcri = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)fileIDs.toArray(), 8);
        final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("AaaRole"));
        sq.addJoin(new Join("AaaRole", "AaaAuthorizedRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
        sq.addJoin(new Join("AaaAuthorizedRole", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        sq.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        if (isMSP) {
            sq.addJoin(new Join("AaaLogin", "LoginUserCustomerMapping", new String[] { "USER_ID" }, new String[] { "DC_USER_ID" }, 2));
            sq.addJoin(new Join("LoginUserCustomerMapping", "MdAppDetails", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            sq.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"));
            sq.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"));
            criteria = criteria.and(fileIDcri);
        }
        else {
            sq.addJoin(new Join("AaaLogin", "MdAppDetails", fileIDcri, 2));
        }
        criteria = criteria.and(fileIDcri);
        sq.setCriteria(criteria);
        sq.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        sq.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        sq.addSelectColumn(Column.getColumn("MdAppDetails", "CUSTOMER_ID"));
        sq.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
        sq.setGroupByClause(new GroupByClause(sq.getSelectColumns()));
        return (SelectQuery)sq;
    }
    
    public JSONObject getDownloadRequestDetails(final HttpServletRequest request) {
        return null;
    }
    
    public SelectQuery getAgentAuthorizationQuery(final List<String> deviceUDIDs, final List<Long> fileID) {
        return new MDMAppDeviceAuthorizer().getAgentAppAuthorizationQuery((List)deviceUDIDs, (List)fileID);
    }
    
    public Boolean isServeFromAppServer(final HttpServletRequest request) {
        if (SecurityUtil.getRequestPath(request).endsWith("manifest.plist")) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    public Boolean isValidFileExtension(final String fileExtension) {
        if (AppMgmtConstants.APP_MGMT_ALLOWED_EXTENSIONS.contains(fileExtension)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    static {
        AppDeviceAuthorizer.appDeviceAuthorizer = null;
    }
}
