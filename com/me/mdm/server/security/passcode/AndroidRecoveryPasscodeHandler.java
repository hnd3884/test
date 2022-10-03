package com.me.mdm.server.security.passcode;

import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.totp.TotpGenerator;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AndroidRecoveryPasscodeHandler
{
    private static AndroidRecoveryPasscodeHandler androidRecoveryPasscodeHandler;
    public static Logger logger;
    public static String hmacsha256_algorithm;
    public static long passcodeValidityInSec;
    public static int noOfRecoveryCodePerDevice;
    
    public static AndroidRecoveryPasscodeHandler getInstance() {
        if (AndroidRecoveryPasscodeHandler.androidRecoveryPasscodeHandler == null) {
            AndroidRecoveryPasscodeHandler.androidRecoveryPasscodeHandler = new AndroidRecoveryPasscodeHandler();
        }
        return AndroidRecoveryPasscodeHandler.androidRecoveryPasscodeHandler;
    }
    
    public JSONObject getAndroidPasscodeRecoveryDetails(final Long resourceId) {
        JSONObject totpDetails = new JSONObject();
        try {
            final TotpGenerator totpGenerator = new TotpGenerator();
            final long totpId = totpGenerator.addTotpDetails(AndroidRecoveryPasscodeHandler.hmacsha256_algorithm, Long.valueOf(AndroidRecoveryPasscodeHandler.passcodeValidityInSec), Long.valueOf(0L));
            addOrUpdateResourceToTotpRel(resourceId, totpId);
            totpGenerator.addRecoveryCodeDetails(AndroidRecoveryPasscodeHandler.noOfRecoveryCodePerDevice, totpId);
            totpDetails = this.getTotpDetailsForResource(resourceId);
        }
        catch (final Exception e) {
            AndroidRecoveryPasscodeHandler.logger.log(Level.SEVERE, "Exception in totp details generation ", e);
        }
        return totpDetails;
    }
    
    public JSONObject getTotpDetailsForResource(final Long resourceId) {
        final JSONObject totpDetailsJson = new JSONObject();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "ResourceToTotpRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("ResourceToTotpRel", "TotpDetails", new String[] { "TOTP_ID" }, new String[] { "TOTP_ID" }, 2));
            query.addJoin(new Join("TotpDetails", "TotpRecoveryCode", new String[] { "TOTP_ID" }, new String[] { "TOTP_ID" }, 2));
            query.addSelectColumn(Column.getColumn("TotpDetails", "SECRET"));
            query.addSelectColumn(Column.getColumn("TotpDetails", "VALIDITY_TIME"));
            query.addSelectColumn(Column.getColumn("TotpDetails", "TOLERANCE"));
            query.addSelectColumn(Column.getColumn("TotpDetails", "ALGORITHM"));
            query.addSelectColumn(Column.getColumn("TotpDetails", "TOTP_ID"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("ResourceToTotpRel", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("TotpRecoveryCode", "TOTP_ID"));
            query.addSelectColumn(Column.getColumn("TotpRecoveryCode", "RECOVERY_CODE"));
            query.addSelectColumn(Column.getColumn("TotpRecoveryCode", "RECOVERY_CODE_ID"));
            final Criteria resourceCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            query.setCriteria(resourceCri);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row totpDetailsRow = dataObject.getFirstRow("TotpDetails");
                totpDetailsJson.put("SECRET", (Object)totpDetailsRow.get("SECRET"));
                totpDetailsJson.put("ALGORITHM", (Object)totpDetailsRow.get("ALGORITHM"));
                totpDetailsJson.put("TOLERANCE", (Object)totpDetailsRow.get("TOLERANCE"));
                totpDetailsJson.put("VALIDITY_TIME", (Object)totpDetailsRow.get("VALIDITY_TIME"));
                final Criteria recoveryCodeCri = new Criteria(Column.getColumn("TotpRecoveryCode", "TOTP_ID"), totpDetailsRow.get("TOTP_ID"), 0);
                final Iterator<Row> recoveryCodeIterator = dataObject.getRows("TotpRecoveryCode", recoveryCodeCri);
                final List totpRecoveryCodeList = new ArrayList();
                while (recoveryCodeIterator.hasNext()) {
                    final Row recoveryCodeRow = recoveryCodeIterator.next();
                    totpRecoveryCodeList.add(recoveryCodeRow.get("RECOVERY_CODE"));
                }
                final JSONObject totpRecoveryCodes = new JSONObject();
                totpRecoveryCodes.put("TOTPRECOVERYCODE1", totpRecoveryCodeList.get(0));
                totpRecoveryCodes.put("TOTPRECOVERYCODE2", totpRecoveryCodeList.get(1));
                totpRecoveryCodes.put("TOTPRECOVERYCODE3", totpRecoveryCodeList.get(2));
                totpDetailsJson.put("TOTPRECOVERYCODES", (Object)totpRecoveryCodes);
            }
        }
        catch (final Exception e) {
            AndroidRecoveryPasscodeHandler.logger.log(Level.SEVERE, "Exception in getting totp details using resourceId");
        }
        return totpDetailsJson;
    }
    
    public static void addOrUpdateResourceToTotpRel(final Long resourceId, final Long totpId) {
        if (resourceId != null) {
            try {
                final Row resourceToTotpRel = new Row("ResourceToTotpRel");
                resourceToTotpRel.set("RESOURCE_ID", (Object)resourceId);
                final DataObject dataObject = MDMUtil.getPersistence().get("ResourceToTotpRel", resourceToTotpRel);
                if (dataObject.isEmpty()) {
                    resourceToTotpRel.set("TOTP_ID", (Object)totpId);
                    dataObject.addRow(resourceToTotpRel);
                    MDMUtil.getPersistence().add(dataObject);
                }
                else {
                    resourceToTotpRel.set("TOTP_ID", (Object)totpId);
                    dataObject.updateRow(resourceToTotpRel);
                    MDMUtil.getPersistence().update(dataObject);
                }
            }
            catch (final Exception e) {
                AndroidRecoveryPasscodeHandler.logger.log(Level.SEVERE, "Exception while adding totp resource relation in table ", e);
            }
        }
    }
    
    public Long getTotpForResourceId(final Long resourceId) {
        Long totp = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ResourceToTotpRel"));
            query.addJoin(new Join("ResourceToTotpRel", "TotpDetails", new String[] { "TOTP_ID" }, new String[] { "TOTP_ID" }, 2));
            query.addSelectColumn(Column.getColumn("TotpDetails", "SECRET"));
            query.addSelectColumn(Column.getColumn("TotpDetails", "VALIDITY_TIME"));
            query.addSelectColumn(Column.getColumn("TotpDetails", "ALGORITHM"));
            query.addSelectColumn(Column.getColumn("TotpDetails", "TOTP_ID"));
            query.addSelectColumn(Column.getColumn("ResourceToTotpRel", "TOTP_ID"));
            query.addSelectColumn(Column.getColumn("ResourceToTotpRel", "RESOURCE_ID"));
            final Criteria resourceCri = new Criteria(Column.getColumn("ResourceToTotpRel", "RESOURCE_ID"), (Object)resourceId, 0);
            query.setCriteria(resourceCri);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row totpDetails = dataObject.getFirstRow("TotpDetails");
                final String secret = (String)totpDetails.get("SECRET");
                final Long timeInsec = (Long)totpDetails.get("VALIDITY_TIME");
                final String algorithm = (String)totpDetails.get("ALGORITHM");
                final TotpGenerator totpGenerator = new TotpGenerator();
                totp = totpGenerator.generateTOTP(secret, (long)timeInsec, algorithm);
            }
        }
        catch (final Exception e) {
            AndroidRecoveryPasscodeHandler.logger.log(Level.SEVERE, "Exception in generating TOTP ", e);
        }
        return totp;
    }
    
    static {
        AndroidRecoveryPasscodeHandler.androidRecoveryPasscodeHandler = null;
        AndroidRecoveryPasscodeHandler.logger = Logger.getLogger("MDMLogger");
        AndroidRecoveryPasscodeHandler.hmacsha256_algorithm = "HmacSHA256";
        AndroidRecoveryPasscodeHandler.passcodeValidityInSec = 1800L;
        AndroidRecoveryPasscodeHandler.noOfRecoveryCodePerDevice = 3;
    }
}
