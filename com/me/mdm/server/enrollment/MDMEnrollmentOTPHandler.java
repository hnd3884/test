package com.me.mdm.server.enrollment;

import com.adventnet.persistence.DataAccess;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Random;
import java.math.BigInteger;
import java.security.SecureRandom;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class MDMEnrollmentOTPHandler
{
    public Logger logger;
    String sourceClass;
    private static MDMEnrollmentOTPHandler otpHandler;
    
    public MDMEnrollmentOTPHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.sourceClass = "MDMEnrollmentOTPHandler";
    }
    
    public static MDMEnrollmentOTPHandler getInstance() {
        if (MDMEnrollmentOTPHandler.otpHandler == null) {
            MDMEnrollmentOTPHandler.otpHandler = new MDMEnrollmentOTPHandler();
        }
        return MDMEnrollmentOTPHandler.otpHandler;
    }
    
    public DataObject addEntryInOTPTable(final Long enrollmentRequestID) throws Exception {
        final String otppassword = this.getOTPPassword();
        final long generatedTime = System.currentTimeMillis();
        final long validity = generatedTime + Long.parseLong(SyMUtil.getSyMParameter("EXPIRE_TIME"));
        final DataObject otpPasswordDO = this.getOTPPasswordDO(enrollmentRequestID);
        Row otpPasswordRow = null;
        if (otpPasswordDO.isEmpty()) {
            otpPasswordRow = new Row("OTPPassword");
            otpPasswordRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            otpPasswordRow.set("GENERATED_TIME", (Object)generatedTime);
            otpPasswordRow.set("EXPIRE_TIME", (Object)validity);
            otpPasswordRow.set("OTP_PASSWORD", (Object)otppassword);
            otpPasswordRow.set("FAILED_ATTEMPTS", (Object)0);
            otpPasswordDO.addRow(otpPasswordRow);
            MDMUtil.getPersistence().add(otpPasswordDO);
        }
        else {
            otpPasswordRow = otpPasswordDO.getRow("OTPPassword");
            otpPasswordRow.set("GENERATED_TIME", (Object)generatedTime);
            otpPasswordRow.set("EXPIRE_TIME", (Object)validity);
            otpPasswordRow.set("OTP_PASSWORD", (Object)otppassword);
            otpPasswordRow.set("FAILED_ATTEMPTS", (Object)0);
            otpPasswordDO.updateRow(otpPasswordRow);
            MDMUtil.getPersistence().update(otpPasswordDO);
        }
        SyMLogger.debug(this.logger, this.sourceClass, this.sourceClass, "Successfully generated the OTP Password for enrollmentRequestID : " + enrollmentRequestID);
        return otpPasswordDO;
    }
    
    public String generateMdmClientToken(final Long enrollmentRequestId) {
        String clientToken = null;
        try {
            final SecureRandom random = new SecureRandom();
            clientToken = new BigInteger(32, random).toString(16);
            final Criteria authCri = new Criteria(Column.getColumn("MdmClientToken", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
            final DataObject authDO = MDMUtil.getPersistence().get("MdmClientToken", authCri);
            Row authRow = null;
            if (authDO.isEmpty()) {
                authRow = new Row("MdmClientToken");
                authRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
                authRow.set("CLIENT_TOKEN", (Object)clientToken);
                authDO.addRow(authRow);
                MDMUtil.getPersistence().add(authDO);
            }
            else {
                authRow = authDO.getFirstRow("MdmClientToken");
                authRow.set("CLIENT_TOKEN", (Object)clientToken);
                authDO.updateRow(authRow);
                MDMUtil.getPersistence().update(authDO);
            }
            clientToken = (String)authDO.getFirstValue("MdmClientToken", "CLIENT_TOKEN");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in generateEnrollmentId ", ex);
        }
        return clientToken;
    }
    
    public void deleteOTPPassword(final Long enrollmentRequestID) {
        final String sourceMethod = "deleteOTPPassword";
        try {
            final Criteria criteria = new Criteria(Column.getColumn("OTPPassword", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
            DataAccess.delete(criteria);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while deleting the OTPPassword.", (Throwable)exp);
        }
    }
    
    public boolean isOTPPasswordValid(final Long enrollmentRequestID, final String otppassword) {
        boolean isPasswordValid = false;
        final String sourceMethod = "isOTPPasswordValid";
        try {
            final Criteria requestIDcriteria = new Criteria(Column.getColumn("OTPPassword", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
            final Criteria otppasswordCriteria = new Criteria(Column.getColumn("OTPPassword", "OTP_PASSWORD"), (Object)otppassword, 0, (boolean)Boolean.FALSE);
            final Criteria expiredCriteria = new Criteria(Column.getColumn("OTPPassword", "EXPIRE_TIME"), (Object)System.currentTimeMillis(), 5).and(new Criteria(Column.getColumn("OTPPassword", "FAILED_ATTEMPTS"), (Object)3, 6));
            Criteria criteria = requestIDcriteria.and(otppasswordCriteria);
            criteria = criteria.and(expiredCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get("OTPPassword", criteria);
            if (!dataObject.isEmpty()) {
                isPasswordValid = true;
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while checking isOTPPassword Valid or not.", (Throwable)exp);
        }
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "OTPPassword for enrollmentRequestID :  " + enrollmentRequestID + " Valid Status : " + isPasswordValid);
        return isPasswordValid;
    }
    
    public String getOTPPassword(final Long enrollmentRequestId) {
        final String sourceMethod = "getOTPPassword";
        String otp = null;
        try {
            final DataObject otpDO = this.getOTPPasswordDO(enrollmentRequestId);
            if (otpDO != null && !otpDO.isEmpty()) {
                final Row otpRow = otpDO.getFirstRow("OTPPassword");
                otp = (String)otpRow.get("OTP_PASSWORD");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while getting OTP for enrollment ID " + enrollmentRequestId, (Throwable)e);
        }
        return otp;
    }
    
    private String getOTPPassword() {
        final SecureRandom random = new SecureRandom();
        return new BigInteger(32, random).toString(16);
    }
    
    private DataObject getOTPPasswordDO(final Long enrollmentRequestID) {
        final String sourceMethod = "getOTPPasswordDO";
        DataObject dataObject = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("OTPPassword", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
            dataObject = MDMUtil.getPersistence().get("OTPPassword", criteria);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while getting OTP DO.", (Throwable)exp);
        }
        return dataObject;
    }
    
    public int getFailedAttempts(final Long enrollmentRequestID) {
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get("OTPPassword", new Criteria(Column.getColumn("OTPPassword", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0));
            final Row otpPassword = dataObject.getRow("OTPPassword");
            return (int)otpPassword.get("FAILED_ATTEMPTS");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred during fetching failed attempts for enrollment request", e);
            return 0;
        }
    }
    
    static {
        MDMEnrollmentOTPHandler.otpHandler = null;
    }
}
