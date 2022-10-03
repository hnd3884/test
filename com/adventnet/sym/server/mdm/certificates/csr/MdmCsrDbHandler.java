package com.adventnet.sym.server.mdm.certificates.csr;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class MdmCsrDbHandler
{
    public static Logger logger;
    
    public static JSONObject getMdmCsrInfo(final Criteria criteria) throws Exception {
        final JSONObject csrInfo = new JSONObject();
        final DataObject dataObject = getMdmCsrDO(criteria);
        if (!dataObject.isEmpty()) {
            MdmCsrDbHandler.logger.log(Level.FINE, "Getting CSR info");
            final Row csrInfoRow = dataObject.getFirstRow("MdmCsrInfo");
            csrInfo.put("CSR_ID", csrInfoRow.get("CSR_ID"));
            csrInfo.put("CUSTOMER_ID", csrInfoRow.get("CUSTOMER_ID"));
            csrInfo.put("CSR_PURPOSE", csrInfoRow.get("CSR_PURPOSE"));
            final Row csrDetailsRow = dataObject.getFirstRow("MdmCsrInfoExtn");
            csrInfo.put("EMAIL_ADDRESS", csrDetailsRow.get("EMAIL_ADDRESS"));
            csrInfo.put("ORGANIZATION_NAME", csrDetailsRow.get("ORGANIZATION_NAME"));
            csrInfo.put("CSR_LOCATION", csrDetailsRow.get("CSR_LOCATION"));
            csrInfo.put("PRIVATEKEY_LOCATION", csrDetailsRow.get("PRIVATEKEY_LOCATION"));
            csrInfo.put("CSR_CREATED_TIME", csrDetailsRow.get("CSR_CREATED_TIME"));
        }
        return csrInfo;
    }
    
    public static JSONObject addOrUpdateMdmCsrInfo(final JSONObject csrInfo) throws Exception {
        MdmCsrDbHandler.logger.log(Level.FINE, "Going to populate CSR details");
        final Long csrID = csrInfo.optLong("CSR_ID");
        final DataObject dataObject = getMdmCsrDO(getMdmCsrInfoIDCriteria(csrID));
        final boolean isNoRowPresent = dataObject.isEmpty();
        if (isNoRowPresent) {
            MdmCsrDbHandler.logger.log(Level.FINE, "Adding CSR info such as CSR_PURPOSE");
            final Row csrInfoRow = new Row("MdmCsrInfo");
            getMdmCsrInfoRow(csrInfoRow, csrInfo);
            dataObject.addRow(csrInfoRow);
            final Row csrDetailsRow = new Row("MdmCsrInfoExtn");
            csrDetailsRow.set("CSR_ID", dataObject.getRow("MdmCsrInfo").get("CSR_ID"));
            getMdmCsrDetailsRow(csrDetailsRow, csrInfo);
            dataObject.addRow(csrDetailsRow);
            MDMUtil.getPersistence().add(dataObject);
            MdmCsrDbHandler.logger.log(Level.FINE, "Populated MDMCSRINFO and MDMCSRINFOEXTN table");
        }
        else {
            final Row csrInfoRow = dataObject.getRow("MdmCsrInfo");
            getMdmCsrInfoRow(csrInfoRow, csrInfo);
            dataObject.updateRow(csrInfoRow);
            final Row csrDetailsRow = dataObject.getFirstRow("MdmCsrInfoExtn");
            getMdmCsrDetailsRow(csrDetailsRow, csrInfo);
            dataObject.updateRow(csrDetailsRow);
            MDMUtil.getPersistence().update(dataObject);
            MdmCsrDbHandler.logger.log(Level.FINE, "Populated MDMCSRINFO and MDMCSRINFOEXTN table");
        }
        csrInfo.put("CSR_ID", dataObject.getRow("MdmCsrInfo").get("CSR_ID"));
        MdmCsrDbHandler.logger.log(Level.FINE, "CSR info added");
        return csrInfo;
    }
    
    private static Row getMdmCsrInfoRow(final Row csrInfoRow, final JSONObject csrInfo) throws Exception {
        final Long customerID = (Long)csrInfo.get("CUSTOMER_ID");
        final int csrPurpose = csrInfo.getInt("CSR_PURPOSE");
        csrInfoRow.set("CUSTOMER_ID", (Object)customerID);
        csrInfoRow.set("CSR_PURPOSE", (Object)csrPurpose);
        return csrInfoRow;
    }
    
    private static Row getMdmCsrDetailsRow(final Row csrDetailsRow, final JSONObject csrInfo) {
        final String commonName = csrInfo.optString("COMMON_NAME");
        final String emailAddress = csrInfo.optString("EMAIL_ADDRESS");
        final String orgName = csrInfo.optString("ORGANIZATION_NAME");
        final String orgUnit = csrInfo.optString("ORGANIZATIONAL_UNIT");
        final String locality = csrInfo.optString("LOCALITY");
        final String street = csrInfo.optString("STREET");
        final String country = csrInfo.optString("COUNTRY");
        final String csrLocation = csrInfo.optString("CSR_LOCATION");
        final String privateKeyLocation = csrInfo.optString("PRIVATEKEY_LOCATION");
        csrDetailsRow.set("COMMON_NAME", (Object)commonName);
        csrDetailsRow.set("EMAIL_ADDRESS", (Object)emailAddress);
        csrDetailsRow.set("ORGANIZATION_NAME", (Object)orgName);
        csrDetailsRow.set("ORGANIZATIONAL_UNIT", (Object)orgUnit);
        csrDetailsRow.set("LOCALITY", (Object)locality);
        csrDetailsRow.set("STREET", (Object)street);
        csrDetailsRow.set("COUNTRY", (Object)country);
        csrDetailsRow.set("CSR_LOCATION", (Object)csrLocation);
        csrDetailsRow.set("PRIVATEKEY_LOCATION", (Object)privateKeyLocation);
        csrDetailsRow.set("CSR_CREATED_TIME", (Object)System.currentTimeMillis());
        return csrDetailsRow;
    }
    
    private static SelectQuery getMdmCsrSelectQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdmCsrInfo"));
        final Join join = new Join("MdmCsrInfo", "MdmCsrInfoExtn", new String[] { "CSR_ID" }, new String[] { "CSR_ID" }, 2);
        final Join join2 = new Join("MdmCsrInfoExtn", "MdmCsrInfotoCertRel", new String[] { "CSR_ID" }, new String[] { "CSR_ID" }, 1);
        final Join join3 = new Join("MdmCsrInfotoCertRel", "Certificates", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_RESOURCE_ID" }, 1);
        selectQuery.addSelectColumn(new Column("MdmCsrInfo", "*"));
        selectQuery.addSelectColumn(new Column("MdmCsrInfoExtn", "*"));
        selectQuery.addSelectColumn(new Column("MdmCsrInfotoCertRel", "*"));
        selectQuery.addSelectColumn(new Column("Certificates", "*"));
        selectQuery.addJoin(join);
        selectQuery.addJoin(join2);
        selectQuery.addJoin(join3);
        return selectQuery;
    }
    
    public static DataObject getMdmCsrDO(final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = getMdmCsrSelectQuery();
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    public static void deleteMdmCsr(final long csrId) throws DataAccessException {
        final Criteria csrIdCriteria = getMdmCsrInfoIDCriteria(csrId);
        MDMUtil.getPersistence().delete(csrIdCriteria);
    }
    
    public static void addCsrToCertRel(final Long csrID, final Long certificateID) throws Exception {
        final DataObject dataObject2 = (DataObject)new WritableDataObject();
        final Row row1 = new Row("MdmCsrInfotoCertRel");
        row1.set("CSR_ID", (Object)csrID);
        row1.set("CERTIFICATE_ID", (Object)certificateID);
        dataObject2.addRow(row1);
        SyMUtil.getPersistence().add(dataObject2);
    }
    
    public static void updateCsrToCertRel(final Long csrID, final Long certificateID) throws DataAccessException {
        final Criteria criteria = new Criteria(new Column("MdmCsrInfotoCertRel", "CSR_ID"), (Object)csrID, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("MdmCsrInfotoCertRel", criteria);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdmCsrInfotoCertRel");
            row.set("CSR_ID", (Object)csrID);
            row.set("CERTIFICATE_ID", (Object)certificateID);
            dataObject.updateRow(row);
            SyMUtil.getPersistence().update(dataObject);
            return;
        }
        throw new APIHTTPException("COM0005", new Object[] { "CSR" });
    }
    
    public static void deleteCsrToCertRel(final Long certificateID) {
        if (certificateID != null) {
            try {
                final Criteria certificateCriteria = new Criteria(new Column("MdmCsrInfotoCertRel", "CERTIFICATE_ID"), (Object)certificateID, 0);
                MDMUtil.getPersistence().delete(certificateCriteria);
            }
            catch (final DataAccessException e) {
                MdmCsrDbHandler.logger.log(Level.SEVERE, "Exception while deleting csr to cert rel ", (Throwable)e);
            }
        }
    }
    
    public static Criteria getMdmCsrInfoIDCriteria(final Long csrID) {
        final Criteria csrCriteria = new Criteria(new Column("MdmCsrInfo", "CSR_ID"), (Object)csrID, 0);
        return csrCriteria;
    }
    
    public static Criteria getMdmCsrInfoCustomerIDCriteria(final Long customerID) {
        final Criteria customerCriteria = new Criteria(new Column("MdmCsrInfo", "CUSTOMER_ID"), (Object)customerID, 0);
        return customerCriteria;
    }
    
    public static Criteria getCsrPurposeCriteria(final int csrPurpose) throws Exception {
        final Criteria csrPurposeCriteria = new Criteria(new Column("MdmCsrInfo", "CSR_PURPOSE"), (Object)csrPurpose, 0);
        return csrPurposeCriteria;
    }
    
    static {
        MdmCsrDbHandler.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
