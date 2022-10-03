package com.me.mdm.doc;

import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import org.json.JSONObject;
import java.io.File;
import com.me.mdm.server.doc.DocMgmtConstants;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.adventnet.iam.security.SecurityUtil;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.files.doc.MDMDocDeviceAuthorizer;
import com.me.mdm.server.factory.FileDownloadRegulatorAPI;

public class DocDeviceAuthorizer implements FileDownloadRegulatorAPI
{
    private static DocDeviceAuthorizer docDeviceAuthorizer;
    
    public static DocDeviceAuthorizer getInstance() {
        if (DocDeviceAuthorizer.docDeviceAuthorizer == null) {
            DocDeviceAuthorizer.docDeviceAuthorizer = new DocDeviceAuthorizer();
        }
        return DocDeviceAuthorizer.docDeviceAuthorizer;
    }
    
    public String getModule(final String requestURI) {
        return MDMDocDeviceAuthorizer.getModule(requestURI);
    }
    
    public Long getCustomerID(final String requestURI) {
        return MDMDocDeviceAuthorizer.getCustomerID(requestURI);
    }
    
    public String getFileIDhint(final String requestURI) {
        return MDMDocDeviceAuthorizer.getFileIDhint(requestURI);
    }
    
    public String getInternalRedirectPath(final HttpServletRequest request) {
        String internalURI = SecurityUtil.getRequestPath(request);
        if ("apache".equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
            internalURI = DocMgmtConstants.DOC_BASE_DIRECTORY + SecurityUtil.getRequestPath(request);
        }
        else if ("nginx".equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
            internalURI = File.separator + "agent-files" + SecurityUtil.getRequestPath(request);
        }
        internalURI = internalURI.replace("\\", "/");
        return internalURI;
    }
    
    public JSONObject getDownloadRequestDetails(final HttpServletRequest request) {
        return null;
    }
    
    public SelectQuery getAgentAuthorizationQuery(final List<String> deviceUDIDs, final List<Long> fileID) {
        return new MDMDocDeviceAuthorizer().getAgentDocAuthorizationQuery((List)deviceUDIDs, (List)fileID);
    }
    
    public SelectQuery getLoginUserAuthorizationQuery(final List<Long> fileIDs) {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        Criteria criteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"*ContentMgmt*", 2, false);
        final Criteria docIDcri = new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)fileIDs.toArray(new Long[fileIDs.size()]), 8);
        final Criteria docNotDelCri = new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1);
        final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("AaaRole"));
        sq.addJoin(new Join("AaaRole", "AaaAuthorizedRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
        sq.addJoin(new Join("AaaAuthorizedRole", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        sq.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        if (isMSP) {
            sq.addJoin(new Join("AaaLogin", "LoginUserCustomerMapping", new String[] { "USER_ID" }, new String[] { "DC_USER_ID" }, 2));
            sq.addJoin(new Join("LoginUserCustomerMapping", "DocumentDetails", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            sq.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"));
            sq.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"));
            criteria = criteria.and(docIDcri);
        }
        else {
            sq.addJoin(new Join("AaaLogin", "DocumentDetails", docIDcri, 2));
        }
        criteria = criteria.and(docNotDelCri);
        sq.setCriteria(criteria);
        sq.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        sq.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID"));
        sq.addSelectColumn(Column.getColumn("DocumentDetails", "CUSTOMER_ID"));
        sq.addSelectColumn(Column.getColumn("DocumentDetails", "MIME_TYPE"));
        sq.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
        sq.setGroupByClause(new GroupByClause(sq.getSelectColumns()));
        return (SelectQuery)sq;
    }
    
    public Boolean isServeFromAppServer(final HttpServletRequest request) {
        return Boolean.FALSE;
    }
    
    public Boolean isValidFileExtension(final String fileExtension) {
        if (".cms".contains(fileExtension)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    static {
        DocDeviceAuthorizer.docDeviceAuthorizer = null;
    }
}
