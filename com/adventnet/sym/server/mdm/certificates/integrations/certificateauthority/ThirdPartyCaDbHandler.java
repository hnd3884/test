package com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority;

import org.json.JSONObject;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.security.PrivateKey;
import java.io.InputStream;
import com.me.mdm.api.core.certificate.CredentialCertificate;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.security.cert.Certificate;
import com.adventnet.sym.server.mdm.certificates.csr.MdmCsrDbHandler;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.scep.PasswordResponse;
import java.util.Map;
import java.util.logging.Logger;

public class ThirdPartyCaDbHandler
{
    public static final Logger LOGGER;
    
    private ThirdPartyCaDbHandler() {
    }
    
    public static Map<Long, PasswordResponse> addOrUpdateUserPasscodes(final Map<Long, PasswordResponse> map, final Long scepId) throws DataAccessException {
        ThirdPartyCaDbHandler.LOGGER.log(Level.FINE, "Adding/ Updaing Enrollment passcodes obtained from Thirdparty CA: {0}", new Object[] { scepId });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ScepDevicePassword"));
        final Criteria criteria = new Criteria(new Column("ScepDevicePassword", "DEVICE_ID"), (Object)map.keySet().toArray(), 8);
        final Criteria criteria2 = new Criteria(new Column("ScepDevicePassword", "SCEP_ID"), (Object)scepId, 0);
        selectQuery.setCriteria(criteria.and(criteria2));
        selectQuery.addSelectColumn(new Column("ScepDevicePassword", "*"));
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            ThirdPartyCaDbHandler.LOGGER.log(Level.FINE, "Updating Enrollment passcodes for scep: {0}", new Object[] { scepId });
            final Iterator iterator = dataObject.getRows("ScepDevicePassword");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long resourceId = (Long)row.get("DEVICE_ID");
                final PasswordResponse passcodeResponse = map.get(resourceId);
                final String passcode = passcodeResponse.getPassword();
                if (passcode != null && !passcode.isEmpty()) {
                    row.set("SCEP_ID", (Object)scepId);
                    row.set("DEVICE_ID", (Object)resourceId);
                    row.set("CHALLENGE_PASSWORD", (Object)passcode);
                    dataObject.updateRow(row);
                    map.remove(resourceId);
                }
            }
            ThirdPartyCaDbHandler.LOGGER.log(Level.FINE, "Successfully updated enrollment passcode details for scep: {0}", new Object[] { scepId });
        }
        if (map.size() > 0) {
            ThirdPartyCaDbHandler.LOGGER.log(Level.FINE, "Newly adding enrollment passcodes for scep {0} the following users: {1}", new Object[] { scepId, map.keySet() });
            final Iterator<Long> deviceIter = map.keySet().iterator();
            while (deviceIter.hasNext()) {
                final Long key = deviceIter.next();
                final PasswordResponse passcodeResponse2 = map.get(key);
                final String passcode2 = passcodeResponse2.getPassword();
                if (passcode2 != null && !passcode2.isEmpty()) {
                    final Row row2 = new Row("ScepDevicePassword");
                    row2.set("SCEP_ID", (Object)scepId);
                    row2.set("DEVICE_ID", (Object)key);
                    row2.set("CHALLENGE_PASSWORD", (Object)passcode2);
                    dataObject.addRow(row2);
                    deviceIter.remove();
                }
            }
            ThirdPartyCaDbHandler.LOGGER.log(Level.FINE, "Successfully added enrollment passcode details for scep: {0}", new Object[] { scepId });
        }
        SyMUtil.getPersistence().update(dataObject);
        return map;
    }
    
    public static String getChallengePasscodeForResourceID(final Long scepId, final Long resourceId) {
        ThirdPartyCaDbHandler.LOGGER.log(Level.FINE, "Getting challenge passcode for resource ID : {0}", new Object[] { resourceId });
        String challengePassword = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ScepDevicePassword"));
            final Criteria criteria = new Criteria(new Column("ScepDevicePassword", "DEVICE_ID"), (Object)resourceId, 0);
            final Criteria criteria2 = new Criteria(new Column("ScepDevicePassword", "SCEP_ID"), (Object)scepId, 0);
            selectQuery.setCriteria(criteria.and(criteria2));
            selectQuery.addSelectColumn(new Column("ScepDevicePassword", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Row row = dataObject.getFirstRow("ScepDevicePassword");
            challengePassword = (String)row.get("CHALLENGE_PASSWORD");
        }
        catch (final Exception e) {
            ThirdPartyCaDbHandler.LOGGER.log(Level.FINE, "Exception while getting challenge passcode", e);
        }
        return challengePassword;
    }
    
    public static Long getCsrIdForRaCertId(final Long raCertificateId) throws DataAccessException {
        ThirdPartyCaDbHandler.LOGGER.log(Level.FINE, "Getting CSR ID for raCertificate ID: {0}", new Object[] { raCertificateId });
        final Criteria raCertCriteria = ProfileCertificateUtil.getCertificateCriteria(raCertificateId);
        final DataObject dataObject1 = MdmCsrDbHandler.getMdmCsrDO(raCertCriteria);
        final Long csrID = (Long)dataObject1.getFirstRow("MdmCsrInfotoCertRel").get("CSR_ID");
        ThirdPartyCaDbHandler.LOGGER.log(Level.FINE, "Obtained raCertificate ID: {0}", new Object[] { raCertificateId });
        return csrID;
    }
    
    public static Certificate[] getRaCertificate(final Long customerId, final long raCertificateId) throws Exception {
        final CredentialCertificate certDetails = ProfileCertificateUtil.getCACertDetails(customerId, raCertificateId);
        if (certDetails != null) {
            final String credntialCertFolder = MDMUtil.getCredentialCertificateFolder(customerId);
            final String raCertFileName = certDetails.getCertificateFileName();
            final String raCertFilePath = credntialCertFolder + File.separator + raCertFileName;
            final InputStream raCertificateStream = ApiFactoryProvider.getFileAccessAPI().readFile(raCertFilePath);
            return CertificateUtil.convertInputStreamToX509CertificateChain(raCertificateStream);
        }
        throw new APIHTTPException("COM0004", new Object[0]);
    }
    
    public static PrivateKey getPrivateKeyForCsr(final long customerId, final long csrId) throws Exception {
        final Criteria customerIDCriteria = MdmCsrDbHandler.getMdmCsrInfoCustomerIDCriteria(customerId);
        final Criteria csrIDCriteria = MdmCsrDbHandler.getMdmCsrInfoIDCriteria(csrId);
        final JSONObject csrInfo = MdmCsrDbHandler.getMdmCsrInfo(customerIDCriteria.and(csrIDCriteria));
        final String privateKeyLocation = (String)csrInfo.get("PRIVATEKEY_LOCATION");
        return CertificateUtils.loadPrivateKeyFromApiFactory(new File(privateKeyLocation));
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
