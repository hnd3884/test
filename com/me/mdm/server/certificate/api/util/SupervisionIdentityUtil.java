package com.me.mdm.server.certificate.api.util;

import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class SupervisionIdentityUtil
{
    private static SupervisionIdentityUtil supervisionIdentityUtil;
    public static Logger logger;
    
    public static SupervisionIdentityUtil getInstance() {
        return (SupervisionIdentityUtil.supervisionIdentityUtil == null) ? new SupervisionIdentityUtil() : SupervisionIdentityUtil.supervisionIdentityUtil;
    }
    
    public DataObject getSupervisionIdentityCertDataObject(final Criteria criteria) throws Exception {
        SupervisionIdentityUtil.logger.log(Level.INFO, "getSupervisionIdentityCertDataObject()");
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("Certificates"));
            selectQuery.addJoin(new Join("Certificates", "CredentialCertificateInfo", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "CERTIFICATE_ID" }, 2));
            final Criteria isActiveCriteria = new Criteria(Column.getColumn("Certificates", "IS_ACTIVE"), (Object)Boolean.TRUE, 0);
            final Criteria certTypeCriteria = new Criteria(Column.getColumn("Certificates", "CERTIFICATE_TYPE"), (Object)6, 0);
            Criteria certCrit = isActiveCriteria.and(certTypeCriteria);
            if (criteria != null) {
                certCrit = certCrit.and(criteria);
            }
            selectQuery.setCriteria(certCrit);
            selectQuery.addSelectColumn(Column.getColumn("Certificates", "CERTIFICATE_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_NOTBEFORE"));
            selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_PASSWORD"));
            selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_FILE_NAME"));
            return MDMUtil.getPersistence().get((SelectQuery)selectQuery);
        }
        catch (final Exception e) {
            SupervisionIdentityUtil.logger.log(Level.SEVERE, "getSupervisionIdentityCertDataObject():- Exception is ", e);
            throw e;
        }
    }
    
    public Long getSupervisionCertificateId(final Long customerID) throws Exception {
        SupervisionIdentityUtil.logger.log(Level.INFO, "getSupervisionCertificateId():- for customerID={0}", customerID);
        try {
            final Criteria customerCriteria = new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject dataObject = getInstance().getSupervisionIdentityCertDataObject(customerCriteria);
            Long certificateID = null;
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("CredentialCertificateInfo");
                certificateID = (Long)row.get("CERTIFICATE_ID");
            }
            SupervisionIdentityUtil.logger.log(Level.INFO, "getSupervisionCertificateId():- certificateID is : {0}", certificateID);
            return certificateID;
        }
        catch (final Exception e) {
            SupervisionIdentityUtil.logger.log(Level.SEVERE, "getSupervisionCertificateId():- Exception is ", e);
            throw e;
        }
    }
    
    static {
        SupervisionIdentityUtil.supervisionIdentityUtil = null;
        SupervisionIdentityUtil.logger = Logger.getLogger("MDMEnrollment");
    }
}
