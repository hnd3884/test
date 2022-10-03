package com.me.mdm.server.certificate.api.service;

import com.me.mdm.server.adep.DEPEnrollmentUtil;
import com.adventnet.sym.server.mdm.encryption.MDMPKCS12CertificateHandler;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.mdm.server.certificate.api.util.SupervisionIdentityUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.mdm.server.certificate.api.model.SupervisionIdentityModel;
import java.util.logging.Logger;

public class SupervisionIdentityService
{
    public static Logger logger;
    
    public SupervisionIdentityModel getSupervisionIdentityInfo(final Long customerId) throws Exception {
        SupervisionIdentityService.logger.log(Level.INFO, "getSupervisionIdentityInfo():- for customerID={0}", customerId);
        final SupervisionIdentityModel supervisionIdentityModel = new SupervisionIdentityModel();
        try {
            final Criteria customerCriteria = new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dataObject = SupervisionIdentityUtil.getInstance().getSupervisionIdentityCertDataObject(customerCriteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("CredentialCertificateInfo");
                supervisionIdentityModel.setCertificateID((Long)row.get("CERTIFICATE_ID"));
                supervisionIdentityModel.setCertNotBeforeTime(Utils.getEventTime((Long)row.get("CERTIFICATE_NOTBEFORE")));
            }
            return supervisionIdentityModel;
        }
        catch (final Exception exception) {
            SupervisionIdentityService.logger.log(Level.SEVERE, "getSupervisionIdentityInfo():- Exception is ", exception);
            throw exception;
        }
    }
    
    public SupervisionIdentityModel getSupervisionIdentityCertPassword(final Long customerId, final String userName, final SupervisionIdentityModel supervisionIdentityModel) throws Exception {
        SupervisionIdentityService.logger.log(Level.INFO, "getSupervisionIdentityCertPassword():- for customerID={0} & certID={1}", new Object[] { customerId, supervisionIdentityModel.getCertificateID() });
        try {
            final Criteria customerCriteria = new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria certIdCriteria = new Criteria(Column.getColumn("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)supervisionIdentityModel.getCertificateID(), 0);
            final Criteria criteria = customerCriteria.and(certIdCriteria);
            final DataObject dataObject = SupervisionIdentityUtil.getInstance().getSupervisionIdentityCertDataObject(criteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("CredentialCertificateInfo");
                supervisionIdentityModel.setCertPassword((String)row.get("CERTIFICATE_PASSWORD"));
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2007, null, userName, I18N.getMsg("mdm.mgmt.certrepo.eventlog.identityCert_pwd_view", new Object[0]), null, customerId);
                MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Enrollment_Module", "SUPERVISION_IDENTITY_CERTIFICATE_PASSWORD_VIEWS");
                return supervisionIdentityModel;
            }
            SupervisionIdentityService.logger.log(Level.WARNING, "getSupervisionIdentityCertPassword():-  supervision identity cert not found");
            throw new APIHTTPException("COM0008", new Object[] { supervisionIdentityModel.getCertificateID() });
        }
        catch (final Exception exception) {
            SupervisionIdentityService.logger.log(Level.SEVERE, "getSupervisionIdentityCertPassword():- Exception is ", exception);
            throw exception;
        }
    }
    
    public SupervisionIdentityModel downloadSupervisionIdentityCert(final Long customerId, final String username, final SupervisionIdentityModel supervisionIdentityModel) throws Exception {
        SupervisionIdentityService.logger.log(Level.INFO, "downloadSupervisionIdentityCert():- for customerID={0} & certID={1}", new Object[] { customerId, supervisionIdentityModel.getCertificateID() });
        try {
            final Criteria isActiveCriteria = new Criteria(Column.getColumn("Certificates", "IS_ACTIVE"), (Object)Boolean.TRUE, 0);
            final Criteria certTypeCriteria = new Criteria(Column.getColumn("Certificates", "CERTIFICATE_TYPE"), (Object)6, 0);
            final Criteria criteria = isActiveCriteria.and(certTypeCriteria);
            final String fileContents = ProfileCertificateUtil.getInstance().getSupervisionIdentityCertFileContents(customerId, supervisionIdentityModel.getCertificateID(), criteria);
            supervisionIdentityModel.setFileContents(fileContents);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2006, null, username, "mdm.mgmt.certrepo.eventlog.identityCert_download", supervisionIdentityModel.getReason(), customerId);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Enrollment_Module", "SUPERVISION_IDENTITY_CERTIFICATE_DOWNLOAD");
            return supervisionIdentityModel;
        }
        catch (final Exception exception) {
            SupervisionIdentityService.logger.log(Level.SEVERE, "downloadSupervisionIdentityCert():- Exception is ", exception);
            throw exception;
        }
    }
    
    public void regenerateSupervisionIdentityCert(final Long customerId, final String username, final SupervisionIdentityModel supervisionIdentityModel) throws Exception {
        SupervisionIdentityService.logger.log(Level.INFO, "regenerateSupervisionIdentityCert():- for customerID={0} & reason={1}", new Object[] { customerId, supervisionIdentityModel.getReason() });
        try {
            final Criteria customerCriteria = new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dataObject = SupervisionIdentityUtil.getInstance().getSupervisionIdentityCertDataObject(customerCriteria);
            Long oldCertID = null;
            if (dataObject.isEmpty()) {
                SupervisionIdentityService.logger.log(Level.WARNING, "regenerateSupervisionIdentityCert():-  supervision identity cert not found");
                throw new APIHTTPException("COM0008", new Object[] { supervisionIdentityModel.getCertificateID() });
            }
            final Row identityRow = dataObject.getFirstRow("Certificates");
            oldCertID = (Long)identityRow.get("CERTIFICATE_RESOURCE_ID");
            ProfileCertificateUtil.getInstance().updateCertificateIsActive(oldCertID, Boolean.FALSE);
            MDMPKCS12CertificateHandler.getInstance().createSupervisionIdentityCertificate(customerId);
            final Criteria criteria = new Criteria(Column.getColumn("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            DEPEnrollmentUtil.processCreateAndAssignDEPProfileAsynchronously(criteria);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2008, null, username, "mdm.mgmt.certrepo.eventlog.identityCert_regenerate", supervisionIdentityModel.getReason() + "@@@", customerId);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Enrollment_Module", "SUPERVISION_IDENTITY_CERTIFICATE_REGENERATION");
        }
        catch (final Exception exception) {
            SupervisionIdentityService.logger.log(Level.SEVERE, "regenerateSupervisionIdentityCert():- Exception is ", exception);
            throw exception;
        }
    }
    
    static {
        SupervisionIdentityService.logger = Logger.getLogger("MDMEnrollment");
    }
}
