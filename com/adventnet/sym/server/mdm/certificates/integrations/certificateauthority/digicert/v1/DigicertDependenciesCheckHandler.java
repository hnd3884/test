package com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.digicert.v1;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCAUtil;
import java.io.FileNotFoundException;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.certificates.templates.TemplateDbHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.certificates.csr.MdmCsrDbHandler;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DigicertDependenciesCheckHandler
{
    public static Logger logger;
    
    public static JSONObject checkDependencies(final JSONObject json) {
        final JSONObject response = new JSONObject();
        try {
            final Long customerID = json.getLong("CUSTOMER_ID");
            response.put("IS_CSR_CREATED", isCsrCreated(customerID));
            response.put("IS_RA_CERTIFICATE_ADDED", isRACertificateAdded(customerID));
            response.put("IS_TEMPLATE_ADDED", isTemplateAdded(customerID));
            response.put("IS_DEPENDENCIES_PRESENT", checkIfDependencyFolerExists());
        }
        catch (final Exception e) {
            DigicertDependenciesCheckHandler.logger.log(Level.INFO, "Exception while getting certificate and template details ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    private static boolean isCsrCreated(final Long customerID) throws Exception {
        boolean isCsrCreated = false;
        final Criteria csrPurposeCriteria = MdmCsrDbHandler.getCsrPurposeCriteria(1);
        final Criteria customerIDCriteria = MdmCsrDbHandler.getMdmCsrInfoCustomerIDCriteria(customerID);
        final Criteria overAllCriteria = csrPurposeCriteria.and(customerIDCriteria);
        final DataObject dataObject = MdmCsrDbHandler.getMdmCsrDO(overAllCriteria);
        if (!dataObject.isEmpty()) {
            isCsrCreated = true;
        }
        return isCsrCreated;
    }
    
    private static boolean isTemplateAdded(final Long customerID) throws Exception {
        boolean isTemplateExists = false;
        final Criteria templateTypeCriteria = TemplateDbHandler.getTemplateTypeCriteria(1);
        final Criteria customerCriteria = TemplateDbHandler.getCustomerCriteria(customerID);
        final Criteria overAllCriteria = templateTypeCriteria.and(customerCriteria);
        final DataObject dataObject = TemplateDbHandler.getTemplateDO(overAllCriteria);
        if (!dataObject.isEmpty()) {
            isTemplateExists = true;
        }
        return isTemplateExists;
    }
    
    private static boolean isRACertificateAdded(final Long customerID) throws Exception {
        boolean isRaCertificateExists = false;
        final SelectQuery selectQuery = getSelectQuery();
        final Criteria criteria = new Criteria(new Column("MdmCsrInfo", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            isRaCertificateExists = true;
        }
        return isRaCertificateExists;
    }
    
    private static SelectQuery getSelectQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdmCsrInfo"));
        final Join join = new Join("MdmCsrInfo", "MdmCsrInfoExtn", new String[] { "CSR_ID" }, new String[] { "CSR_ID" }, 2);
        final Join join2 = new Join("MdmCsrInfoExtn", "MdmCsrInfotoCertRel", new String[] { "CSR_ID" }, new String[] { "CSR_ID" }, 2);
        final Join join3 = new Join("MdmCsrInfotoCertRel", "Certificates", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_RESOURCE_ID" }, 2);
        final Join join4 = new Join("Certificates", "CredentialCertificateInfo", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "CERTIFICATE_ID" }, 2);
        selectQuery.addSelectColumn(new Column("MdmCsrInfo", "*"));
        selectQuery.addSelectColumn(new Column("MdmCsrInfoExtn", "*"));
        selectQuery.addSelectColumn(new Column("MdmCsrInfotoCertRel", "*"));
        selectQuery.addSelectColumn(new Column("Certificates", "*"));
        selectQuery.addSelectColumn(new Column("CredentialCertificateInfo", "*"));
        selectQuery.addJoin(join);
        selectQuery.addJoin(join2);
        selectQuery.addJoin(join3);
        selectQuery.addJoin(join4);
        return selectQuery;
    }
    
    public static boolean checkIfDependencyFolerExists() throws Exception {
        boolean isDependencyPresent;
        try {
            final boolean isDependencyFolderExists = isDependencyFolderPresent();
            final boolean isJarsExists = isJarsPresent();
            isDependencyPresent = (isDependencyFolderExists && isJarsExists);
        }
        catch (final FileNotFoundException e) {
            throw new APIHTTPException("CERTAUTHDIGI001", new Object[0]);
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return isDependencyPresent;
    }
    
    public static boolean isDependencyFolderPresent() throws Exception {
        final String dependencyFolderPath = ThirdPartyCAUtil.getDigicertDepedencyFilesPath(1);
        return checkIfFolderExists(dependencyFolderPath);
    }
    
    private static boolean isJarsPresent() throws Exception {
        final String dependencyFolderPath = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("digicertdependencyfolderpath");
        dependencyFolderPath.replaceAll("/", "\\" + File.separator);
        final String jarsFolderPath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + dependencyFolderPath;
        return checkIfFolderExists(jarsFolderPath);
    }
    
    private static boolean checkIfFolderExists(final String directoryPath) throws Exception {
        boolean isExists = false;
        final File file = new File(directoryPath);
        final Path path = Paths.get(directoryPath, new String[0]);
        if (file.isDirectory() && !isDirEmpty(path)) {
            isExists = true;
        }
        return isExists;
    }
    
    private static boolean isDirEmpty(final Path directory) throws IOException {
        try (final DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
    
    static {
        DigicertDependenciesCheckHandler.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
