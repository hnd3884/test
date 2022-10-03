package com.adventnet.sym.server.mdm.certificates.scep.request;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.util.logging.Level;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public abstract class MDMScepEnrollmentRequestValidator implements MdmScepEnrollmentCertificateIdentityProvider, ScepEnrollmentRequestValidator
{
    private final Logger logger;
    protected final MdmScepRequest mdmScepRequest;
    
    public MDMScepEnrollmentRequestValidator(final MdmScepRequest mdmScepRequest) {
        this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
        this.mdmScepRequest = mdmScepRequest;
    }
    
    @Override
    public boolean isEligibleForRenewal(final X509Certificate requesterCertificate) {
        this.logger.log(Level.INFO, "MDMScepEnrollmentRequestValidator-isEligibleForRenewal: Authorizing renewal for enrollment requst: {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
        try {
            final X500Name certificateIssuerDN = new JcaX509CertificateHolder(requesterCertificate).getIssuer();
            final BigInteger requesterCertificateSerialNumber = requesterCertificate.getSerialNumber();
            final String requestCertificateSignature = CertificateUtil.getSignatureFromX509Certificate(requesterCertificate);
            this.logger.log(Level.INFO, "MDMScepEnrollmentRequestValidator-isEligibleForRenewal: Enrollment req id - {0}, Issuer: {1}, serial - {2}, signature - {3}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId(), certificateIssuerDN, requesterCertificateSerialNumber, requestCertificateSignature });
            final DataObject dataObject = this.getMatchingCertificateDO(requesterCertificateSerialNumber, certificateIssuerDN, requestCertificateSignature);
            final boolean isEligibleForRenewal = !dataObject.isEmpty();
            this.logger.log(Level.INFO, "MDMScepEnrollmentRequestValidator-isEligibleForRenewal: Is valid renewal: {0}", new Object[] { isEligibleForRenewal });
            return isEligibleForRenewal;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "MDMScepEnrollmentRequestValidator-isEligibleForRenewal: Exception while authorizing renewal for " + this.mdmScepRequest.getEnrollmentRequestId());
            return false;
        }
    }
    
    @Override
    public boolean isValidPasscode(final String scepEnrollmentPasscode) {
        try {
            if (!scepEnrollmentPasscode.isEmpty()) {
                this.logger.log(Level.INFO, "MDMScepEnrollmentRequestValidator-isValidPasscode: Scep enrollment passcode is not empty for enrollment req id: {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
                final Criteria eridCriteria = new Criteria(Column.getColumn("MdmClientToken", "ENROLLMENT_REQUEST_ID"), (Object)this.mdmScepRequest.getEnrollmentRequestId(), 0);
                final Criteria passcodeCriteria = new Criteria(Column.getColumn("MdmClientToken", "CLIENT_TOKEN"), (Object)scepEnrollmentPasscode, 0);
                final DataObject mdmClientTokenDO = MDMUtil.getPersistence().get("MdmClientToken", eridCriteria.and(passcodeCriteria));
                if (!mdmClientTokenDO.isEmpty()) {
                    final Row row = mdmClientTokenDO.getRow("MdmClientToken");
                    if (row != null) {
                        this.logger.log(Level.INFO, "MDMScepEnrollmentRequestValidator-isValidPasscode: Validating passcode for enrollment req id: {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
                        final String clientToken = (String)row.get("CLIENT_TOKEN");
                        if (clientToken.equals(scepEnrollmentPasscode)) {
                            this.logger.log(Level.INFO, "MDMScepEnrollmentRequestValidator: Passcode valid: True for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
                            return true;
                        }
                    }
                }
            }
            this.logger.log(Level.SEVERE, "MDMScepEnrollmentRequestValidator: Is valid passcode: False for {0}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId() });
            return false;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MDMScepEnrollmentRequestValidator: Exception while validating passcode for {0}", this.mdmScepRequest.getEnrollmentRequestId());
            return false;
        }
    }
    
    private DataObject getMatchingCertificateDO(final BigInteger serialNumber, final X500Name issuerDN, final String certificateSignature) throws DataAccessException {
        final String certificateIdentifier = this.getCertificateIdentifier();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "MdCertificateResourceRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdCertificateResourceRel", "MdCertificateInfo", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 2));
        selectQuery.addSelectColumn(new Column("MdCertificateInfo", "*"));
        final Criteria mdmIdentityCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_SERIAL_NUMBER"), (Object)serialNumber, 0).and(new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_ISSUER_DN"), (Object)issuerDN, 0)).and(new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_SIGNATURE"), (Object)certificateSignature, 0)).and(new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_NAME"), (Object)certificateIdentifier, 0));
        selectQuery.setCriteria(mdmIdentityCriteria);
        this.logger.log(Level.FINE, "MDMScepEnrollmentRequestValidator: Select Query for Enrollment request - {0}, {1}", new Object[] { this.mdmScepRequest.getEnrollmentRequestId(), selectQuery });
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
}
