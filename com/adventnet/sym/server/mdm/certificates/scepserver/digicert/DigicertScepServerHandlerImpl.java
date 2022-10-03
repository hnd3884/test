package com.adventnet.sym.server.mdm.certificates.scepserver.digicert;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.certificates.templates.digicert.DigicertTemplateHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.certificates.csr.MdmCsrDbHandler;
import com.adventnet.sym.server.mdm.certificates.templates.MdmCertTemplate;
import com.adventnet.sym.server.mdm.certificates.templates.TemplateHandler;
import com.adventnet.sym.server.mdm.certificates.templates.digicert.DigicertCertTemplate;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCaDbHandler;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerHandler;

public class DigicertScepServerHandlerImpl implements ScepServerHandler
{
    private static final Logger LOGGER;
    
    @Override
    public void validateRelatedServerDetail(final long customerId, final JSONObject serverDetails) {
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Validating Digicert server details for customer: {0}", new Object[] { customerId });
        if (!serverDetails.has("ra_certificate_id")) {
            throw new APIHTTPException("COM0005", new Object[] { "RA Certificate" });
        }
        if (!serverDetails.has("profile_oid")) {
            throw new APIHTTPException("COM0005", new Object[] { "Certificate OID" });
        }
        if (!serverDetails.has("csr_id")) {
            throw new APIHTTPException("COM0005", new Object[] { "CSR ID" });
        }
        final long csrId = serverDetails.getLong("csr_id");
        final long raCertificateId = serverDetails.getLong("ra_certificate_id");
        try {
            DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Validating whether the RA certificate matches the CSR: RA cert - {0}, CSR - {1}", new Object[] { raCertificateId, csrId });
            final Certificate[] certificate = ThirdPartyCaDbHandler.getRaCertificate(customerId, raCertificateId);
            final PrivateKey privateKey = ThirdPartyCaDbHandler.getPrivateKeyForCsr(customerId, csrId);
            final boolean isValidCertForPrivateKey = CertificateUtil.getInstance().isValidCertificateAndPrivateKey((X509Certificate)certificate[0], privateKey);
            if (!isValidCertForPrivateKey) {
                DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: RA cert and CSR id does not match: RA cert - {0}, CSR - {1}", new Object[] { raCertificateId, csrId });
                throw new APIHTTPException("CERTAUTHDIGI002", new Object[0]);
            }
        }
        catch (final APIHTTPException e) {
            final String eMessage = "Exception while validating CSR " + csrId + ", RA certificate " + raCertificateId + " for customer: " + customerId;
            DigicertScepServerHandlerImpl.LOGGER.log(Level.SEVERE, eMessage, e);
            throw e;
        }
        catch (final Exception e2) {
            final String eMessage = "Exception while validating CSR " + csrId + ", RA certificate " + raCertificateId + " for customer: " + customerId;
            DigicertScepServerHandlerImpl.LOGGER.log(Level.SEVERE, eMessage, e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public void addRelatedServerDetail(final ScepServer scepServer) throws Exception {
        final DigicertScepServer digicertScepServer = (DigicertScepServer)scepServer;
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Adding Digicert SCEP server details for customer: {0}", new Object[] { scepServer.getCustomerId() });
        final String templateName = "DIGICERT_" + scepServer.getServerName();
        final DigicertCertTemplate digicertCertificateTemplate = new DigicertCertTemplate(templateName, ((DigicertScepServer)scepServer).certificateOID);
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Adding Digicert details: {0}", new Object[] { digicertCertificateTemplate.certificateOID });
        final long templateId = TemplateHandler.addOrUpdateTemplate(digicertCertificateTemplate, -1L, scepServer.getCustomerId());
        DigicertServerMappingDBHandler.addDigicertServerMapping(scepServer.getScepServerId(), ((DigicertScepServer)scepServer).raCertificateId, templateId);
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Adding RA cert to CSR mapping: RA cert - {0}, CSR {1}", new Object[] { digicertScepServer.raCertificateId, digicertScepServer.csrId });
        MdmCsrDbHandler.addCsrToCertRel(digicertScepServer.csrId, digicertScepServer.raCertificateId);
    }
    
    @Override
    public boolean modifyRelatedServerDetail(final long serverId, final long customerId, final ScepServer scepServer) throws Exception {
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Modifying Digicert SCEP server details: {0}", new Object[] { serverId });
        final DigicertScepServer digiScepServer = (DigicertScepServer)scepServer;
        final String newCertoid = digiScepServer.certificateOID;
        final long newRaCertId = digiScepServer.raCertificateId;
        final DigicertServerMapping existingDigicertMapping = DigicertServerMappingDBHandler.getDigicertServerMappingDetails(serverId);
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Modifying Digicert SCEP server details: {0}", new Object[] { serverId });
        final Criteria templateCriteria = new Criteria(Column.getColumn("MdmCertificateTemplate", "TEMPLATE_ID"), (Object)existingDigicertMapping.getTemplateId(), 0);
        final JSONObject templateDetail = DigicertTemplateHandler.getDigicertTemplates(templateCriteria).getJSONObject(0);
        final String existingCertOid = templateDetail.getString("CERTIFICATE_OID");
        boolean redistributionNeeded = false;
        if (!newCertoid.equals(existingCertOid)) {
            DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Modifying Certificate OID: {0} for server: {1}", new Object[] { newCertoid, serverId });
            final String templateName = "DIGICERT_" + digiScepServer.getServerName() + "_" + System.currentTimeMillis();
            final DigicertCertTemplate digicertCertTemplate = new DigicertCertTemplate(templateName, newCertoid);
            TemplateHandler.addOrUpdateTemplate(digicertCertTemplate, existingDigicertMapping.getTemplateId(), customerId);
            redistributionNeeded = true;
        }
        if (newRaCertId != existingDigicertMapping.getRaCertificateId()) {
            DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Modifying RA certificate: {0} for server: {1}", new Object[] { existingDigicertMapping.getRaCertificateId(), serverId });
            MdmCsrDbHandler.updateCsrToCertRel(digiScepServer.csrId, newRaCertId);
            this.deleteRaCertificateDetails(existingDigicertMapping.getRaCertificateId());
        }
        DigicertServerMappingDBHandler.updateDigicertServerMapping(serverId, newRaCertId, existingDigicertMapping.getTemplateId());
        return redistributionNeeded;
    }
    
    @Override
    public void deleteRelatedServerDetail(final long serverID) throws DataAccessException {
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Deleting Digicert server: {0}", new Object[] { serverID });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DigiCertServersMapping"));
        final Criteria serverIDCriteria = new Criteria(new Column("DigiCertServersMapping", "SERVER_ID"), (Object)serverID, 0);
        selectQuery.setCriteria(serverIDCriteria);
        selectQuery.addSelectColumn(new Column("DigiCertServersMapping", "*"));
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row serverDetailRow = dataObject.getFirstRow("DigiCertServersMapping");
            final Long raCertificateID = (Long)serverDetailRow.get("RA_CERT_ID");
            final Long templateID = (Long)serverDetailRow.get("TEMPLATE_ID");
            this.deleteCsrToRaCertMapping(serverID, raCertificateID);
            this.deleteRaCertificateDetails(raCertificateID);
            TemplateHandler.deleteTemplate(templateID);
        }
    }
    
    @Override
    public ScepServer getRelatedServerDetail(final long serverID) throws DataAccessException {
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Getting Digicert server details for: {0}", new Object[] { serverID });
        final Criteria serverIdCriteria = new Criteria(new Column("DigiCertServersMapping", "SERVER_ID"), (Object)serverID, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("DigiCertServersMapping", serverIdCriteria);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("DigiCertServersMapping");
            final long raCertId = (long)row.get("RA_CERT_ID");
            final long templateId = (long)row.get("TEMPLATE_ID");
            DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Digicert server present for: {0}", new Object[] { serverID });
            final Criteria templateIdCriteria = new Criteria(new Column("MdmTemplateToCertificateOID", "TEMPLATE_ID"), (Object)templateId, 0);
            final DataObject templateDO = SyMUtil.getPersistence().get("MdmTemplateToCertificateOID", templateIdCriteria);
            final Row row2 = templateDO.getFirstRow("MdmTemplateToCertificateOID");
            final String certificateOID = (String)row2.get("CERTIFICATE_OID");
            final long csrId = ThirdPartyCaDbHandler.getCsrIdForRaCertId(raCertId);
            return new DigicertScepServer(raCertId, csrId, certificateOID);
        }
        throw new APIHTTPException("COM0005", new Object[0]);
    }
    
    private void deleteCsrToRaCertMapping(final long serverID, final Long raCertificateID) throws DataAccessException {
        final long csrId = ThirdPartyCaDbHandler.getCsrIdForRaCertId(raCertificateID);
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Deleting CSR {0} for server: {1}", new Object[] { csrId, serverID });
        final Criteria csrIdCriteria = MdmCsrDbHandler.getMdmCsrInfoIDCriteria(csrId);
        MdmCsrDbHandler.deleteCsrToCertRel(raCertificateID);
        SyMUtil.getPersistence().delete(csrIdCriteria);
    }
    
    private void deleteRaCertificateDetails(final Long raCertificateID) throws DataAccessException {
        DigicertScepServerHandlerImpl.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Deleting RA cert {0} for server: {1}", new Object[] { raCertificateID });
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Certificates");
        updateQuery.setCriteria(new Criteria(Column.getColumn("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)raCertificateID, 0));
        updateQuery.setUpdateColumn("IS_ACTIVE", (Object)false);
        SyMUtil.getPersistence().update(updateQuery);
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
