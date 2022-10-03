package com.me.devicemanagement.onpremise.server.authentication.summaryserver.summary;

import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.authentication.PasswordException;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.persistence.internal.UniqueValueHolder;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;

public class SSOnPremiseUserUtil extends DMOnPremiseUserUtil
{
    public static void addOrUpdateUserInLoginExtn(final Long loginID, final Integer computerScope, final Integer mdmScope, final Integer probeScope) throws DataAccessException {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AaaLoginExtn", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLoginExtn", criteria);
            if (dataObject.isEmpty()) {
                final Row row = new Row("AaaLoginExtn");
                row.set("LOGIN_ID", (Object)loginID);
                row.set("CREATION_TIME", (Object)new Long(System.currentTimeMillis()));
                row.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
                row.set("SCOPE", (Object)computerScope);
                row.set("MDM_SCOPE", (Object)mdmScope);
                row.set("PROBE_SCOPE", (Object)probeScope);
                dataObject.addRow(row);
                SyMUtil.getPersistence().add(dataObject);
            }
            else {
                final Row row = dataObject.getFirstRow("AaaLoginExtn");
                row.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
                row.set("SCOPE", (Object)computerScope);
                row.set("MDM_SCOPE", (Object)mdmScope);
                row.set("PROBE_SCOPE", (Object)probeScope);
                dataObject.updateRow(row);
                SyMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception ex) {
            SSOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception occurred while updating AAALoginExtn table");
            throw ex;
        }
    }
    
    public static Long addSSUser(final DataObject addUserDO, final JSONObject addUserJObj, final JSONObject probeHandlerObject) throws DataAccessException, PasswordException {
        Long loginID = null;
        final String password = (String)addUserJObj.get("password");
        final Integer computerScope = addUserJObj.optInt("scope", 0);
        final Integer mdmScope = addUserJObj.optInt("mdmScope", 0);
        final Integer probeScope = addUserJObj.optInt("probeScope", 0);
        final boolean isTwoFactorEnabledGlobally = addUserJObj.optBoolean("isTwoFactorEnabledGlobaly", false);
        try {
            final UniqueValueHolder uvhloginID = (UniqueValueHolder)addUserDO.getFirstValue("AaaLogin", "LOGIN_ID");
            final Long passwordPolicyId = addUserJObj.has("passwordPolicyId") ? Long.valueOf(addUserJObj.getLong("passwordPolicyId")) : null;
            final Long passwordProfileId = addUserJObj.has("passwordProfileId") ? addUserJObj.getLong("passwordProfileId") : AuthUtil.getPasswordProfileId("Profile 2");
            final Row passwordRow = new Row("AaaPassword");
            passwordRow.set("PASSWORD", (Object)password);
            if (passwordPolicyId != null) {
                passwordRow.set("PASSWDRULE_ID", (Object)passwordPolicyId);
            }
            passwordRow.set("PASSWDPROFILE_ID", (Object)passwordProfileId);
            addUserDO.addRow(passwordRow);
            final Row accRow = addUserDO.getRow("AaaAccount");
            final Row accPassRow = new Row("AaaAccPassword");
            accPassRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accPassRow.set("PASSWORD_ID", passwordRow.get("PASSWORD_ID"));
            addUserDO.addRow(accPassRow);
            final int noOfSubAccounts = -1;
            final Row accOwnerProfileRow = new Row("AaaAccOwnerProfile");
            accOwnerProfileRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(noOfSubAccounts));
            addUserDO.addRow(accOwnerProfileRow);
            final Row aaaLoginExtn = new Row("AaaLoginExtn");
            aaaLoginExtn.set("LOGIN_ID", (Object)uvhloginID);
            aaaLoginExtn.set("CREATION_TIME", (Object)new Long(System.currentTimeMillis()));
            aaaLoginExtn.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
            aaaLoginExtn.set("SCOPE", (Object)computerScope);
            aaaLoginExtn.set("MDM_SCOPE", (Object)mdmScope);
            aaaLoginExtn.set("PROBE_SCOPE", (Object)probeScope);
            addUserDO.addRow(aaaLoginExtn);
            final Row userRow = addUserDO.getRow("AaaUser");
            final Row accUserTwoFactorDetailsRow = new Row("AaaUserTwoFactorDetails");
            accUserTwoFactorDetailsRow.set("USER_ID", userRow.get("USER_ID"));
            accUserTwoFactorDetailsRow.set("ENABLED", (Object)isTwoFactorEnabledGlobally);
            accUserTwoFactorDetailsRow.set("TWOFACTORAUTHIMPL", (Object)"com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword");
            accUserTwoFactorDetailsRow.set("TWOFACTORPASSWORD", (Object)" ");
            addUserDO.addRow(accUserTwoFactorDetailsRow);
            AuthUtil.createUserAccount(addUserDO);
            probeHandlerObject.put("userID", addUserDO.getFirstValue("AaaUser", "USER_ID"));
            probeHandlerObject.put("loginID", addUserDO.getFirstValue("AaaLogin", "LOGIN_ID"));
            probeHandlerObject.put("accountID", addUserDO.getFirstValue("AaaAccount", "ACCOUNT_ID"));
            probeHandlerObject.put("contactInfoID", addUserDO.getFirstValue("AaaContactInfo", "CONTACTINFO_ID"));
            loginID = (Long)uvhloginID.getValue();
            return loginID;
        }
        catch (final DataAccessException dataAccessException) {
            SSOnPremiseUserUtil.logger.log(Level.SEVERE, "DataAccessException in 'addSSUser()'");
            throw dataAccessException;
        }
        catch (final PasswordException passwordException) {
            SSOnPremiseUserUtil.logger.log(Level.SEVERE, "PasswordException in 'addSSUser()'");
            throw passwordException;
        }
    }
    
    public static Map<String, Object> generateModifyUserProbeHandlerObject(final Long loginID) throws DataAccessException {
        final Map<String, Object> probeHandlerObject = new HashMap<String, Object>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
        final Criteria criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
        final Join aaaUserJoin = new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
        final Join aaaAccountJoin = new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
        final Join aaaUserContactJoin = new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
        final Join aaaContactJoin = new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2);
        final Join aaaAccPasswordJoin = new Join("AaaAccount", "AaaAccPassword", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
        final Join aaaPassword = new Join("AaaAccPassword", "AaaPassword", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 2);
        selectQuery.addJoin(aaaUserJoin);
        selectQuery.addJoin(aaaAccountJoin);
        selectQuery.addJoin(aaaUserContactJoin);
        selectQuery.addJoin(aaaContactJoin);
        selectQuery.addJoin(aaaAccPasswordJoin);
        selectQuery.addJoin(aaaPassword);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        DataObject dataObject = null;
        try {
            dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row aaaUserRow = dataObject.getFirstRow("AaaUser");
                final Row aaaAccountRow = dataObject.getFirstRow("AaaAccount");
                final Row aaaContactInfoRow = dataObject.getFirstRow("AaaContactInfo");
                final Row aaaPasswordRow = dataObject.getFirstRow("AaaPassword");
                final String password = (String)aaaPasswordRow.get("PASSWORD");
                final String salt = aaaPasswordRow.get("SALT").toString();
                int algorithm = 0;
                if (aaaPasswordRow.get("ALGORITHM").equals("MD5")) {
                    algorithm = 1;
                }
                else if (aaaPasswordRow.get("ALGORITHM").equals("bcrypt")) {
                    algorithm = 2;
                }
                final String createdTime = aaaPasswordRow.get("CREATEDTIME").toString();
                probeHandlerObject.put("loginID", loginID);
                probeHandlerObject.put("userID", aaaUserRow.get("USER_ID"));
                probeHandlerObject.put("accountID", aaaAccountRow.get("ACCOUNT_ID"));
                probeHandlerObject.put("contactInfoID", aaaContactInfoRow.get("CONTACTINFO_ID"));
                probeHandlerObject.put("password", password);
                probeHandlerObject.put("salt", salt);
                probeHandlerObject.put("algorithm", algorithm);
                probeHandlerObject.put("createdTime", createdTime);
            }
        }
        catch (final DataAccessException e) {
            SSOnPremiseUserUtil.logger.log(Level.SEVERE, () -> "Exception while getting probe handler object details for the user " + n);
            throw e;
        }
        return probeHandlerObject;
    }
    
    public static void modifySSUser(final JSONObject modifyUserJObj) throws Exception {
        try {
            final Long loginID = (Long)modifyUserJObj.get("loginID");
            final String contactinfoID = String.valueOf(modifyUserJObj.get("contactinfoID"));
            final String userName = (String)modifyUserJObj.get("userName");
            final String password = modifyUserJObj.optString("password", (String)null);
            final String emailID = modifyUserJObj.optString("USER_EMAIL_ID");
            final String phNum = modifyUserJObj.optString("USER_PH_NO");
            final String domainName = modifyUserJObj.optString("domainName", "-");
            final Integer computerScope = modifyUserJObj.optInt("scope", 0);
            final Integer mdmScope = modifyUserJObj.optInt("mdmScope", 0);
            final Integer probeScope = modifyUserJObj.optInt("probeScope", 0);
            final String oldUserName = DMUserHandler.getDCUser(loginID);
            final Long oldUserID = DMUserHandler.getDCUserID(loginID);
            Criteria criteria = new Criteria(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"), (Object)contactinfoID, 0);
            UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaContactInfo");
            if (emailID != null && !emailID.isEmpty()) {
                updateQuery.setUpdateColumn("EMAILID", (Object)emailID);
            }
            updateQuery.setUpdateColumn("LANDLINE", (Object)phNum);
            updateQuery.setCriteria(criteria);
            SyMUtil.getPersistence().update(updateQuery);
            if (!oldUserName.equalsIgnoreCase(userName) && userName.equalsIgnoreCase("admin")) {
                DMOnPremiseUserUtil.renameDummyAdmin(oldUserID, loginID);
            }
            addOrUpdateUserInLoginExtn(loginID, computerScope, mdmScope, probeScope);
            criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaLogin");
            updateQuery.setUpdateColumn("DOMAINNAME", (Object)domainName.toLowerCase());
            if (!oldUserName.equalsIgnoreCase(userName)) {
                updateQuery.setUpdateColumn("NAME", (Object)userName);
            }
            updateQuery.setCriteria(criteria);
            SyMUtil.getPersistence().update(updateQuery);
            if (!oldUserName.equalsIgnoreCase(userName)) {
                criteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)oldUserID, 0);
                updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaUser");
                updateQuery.setUpdateColumn("FIRST_NAME", (Object)userName);
                updateQuery.setCriteria(criteria);
                SyMUtil.getPersistence().update(updateQuery);
                DMOnPremiseUserUtil.createORRetainDefaultAdminUser(modifyUserJObj);
            }
            if (!oldUserName.equalsIgnoreCase(userName) && userName.equalsIgnoreCase("admin")) {
                DMOnPremiseUserUtil.deleteDummyAdmin(oldUserID, loginID);
            }
            if (domainName.equalsIgnoreCase("-") && password != null) {
                final String serviceName = SYMClientUtil.getServiceName(userName);
                AuthUtil.changePassword(userName, serviceName, password);
            }
        }
        catch (final Exception ex) {
            SSOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception Occurred in SSOnPremiseUserUtil - modifySSUser()");
            throw ex;
        }
    }
    
    public static boolean addOrUpdateSSPasswordPolicy(final JSONObject policyDetails) {
        boolean isUpdated = Boolean.FALSE;
        SSOnPremiseUserUtil.logger.log(Level.INFO, "In addOrUpdatePasswordPolicy with policyDetails : {0}", policyDetails);
        try {
            final Integer minLength = Integer.parseInt((String)policyDetails.get("MIN_LENGTH"));
            Integer preventReuseFor = Integer.parseInt((String)policyDetails.get("PREVENT_REUSE_FOR"));
            final Boolean isComplexPassword = (Boolean)policyDetails.get("IS_COMPLEX_PASSWORD");
            final Boolean loginRestrictionEnabled = (Boolean)policyDetails.get("ENABLE_LOGIN_RESTRICTION");
            Integer badAttempt = Integer.parseInt((String)policyDetails.get("BAD_ATTEMPT"));
            Integer lockPeriod = Integer.parseInt((String)policyDetails.get("LOCK_PERIOD"));
            final Boolean loginNameIndep = policyDetails.has("IS_LOGIN_NAME_USAGE_RESTRICTED") && (boolean)policyDetails.get("IS_LOGIN_NAME_USAGE_RESTRICTED");
            final DataObject policyDO = DMOnPremiseUserUtil.getCustomPasswordPolicyDO(true);
            final DataObject passwdProfileDO = DMOnPremiseUserUtil.getCustomPasswdProfileDO(true);
            final DataObject accProfileDO = DMOnPremiseUserUtil.getCustomAccountProfileDO(true);
            preventReuseFor = ((preventReuseFor == 0) ? -1 : preventReuseFor);
            if (Boolean.FALSE.equals(loginRestrictionEnabled)) {
                badAttempt = -1;
                lockPeriod = 0;
                unlockAccount();
            }
            if (policyDO != null && passwdProfileDO != null && accProfileDO != null) {
                Row policyRow = policyDO.getRow("AaaPasswordRule");
                if (policyRow == null) {
                    policyRow = new Row("AaaPasswordRule");
                }
                policyRow.set("NAME", (Object)"CUSTOM_POLICY");
                policyRow.set("MIN_LENGTH", (Object)minLength);
                policyRow.set("MAX_LENGTH", (Object)25);
                policyRow.set("LOGINNAME_INDEPNDT", (Object)loginNameIndep);
                Row passwdProfileRow = passwdProfileDO.getRow("AaaPasswordProfile");
                if (passwdProfileRow == null) {
                    passwdProfileRow = new Row("AaaPasswordProfile");
                }
                passwdProfileRow.set("NAME", (Object)"CUSTOM_PROFILE");
                passwdProfileRow.set("NUMOF_OLDPASSWD", (Object)preventReuseFor);
                passwdProfileRow.set("CHPASSWD_ONFL", (Object)false);
                passwdProfileRow.set("LOGIN_AFTEREXP", (Object)(-1));
                passwdProfileRow.set("UPDATE_INTERVAL", (Object)lockPeriod);
                if (Boolean.TRUE.equals(isComplexPassword)) {
                    policyRow.set("REQ_MIXEDCASE", (Object)Boolean.TRUE);
                    policyRow.set("NUMOF_SPLCHAR", (Object)1);
                }
                else {
                    policyRow.set("REQ_MIXEDCASE", (Object)Boolean.FALSE);
                    policyRow.set("NUMOF_SPLCHAR", (Object)(-1));
                }
                Row accProfileRow = accProfileDO.getRow("AaaAccAdminProfile");
                if (accProfileRow == null) {
                    accProfileRow = new Row("AaaAccAdminProfile");
                }
                accProfileRow.set("NAME", (Object)"CUSTOM_PROFILE");
                accProfileRow.set("ALLOWED_BADLOGIN", (Object)badAttempt);
                final boolean isPolicyUpdated = DMOnPremiseUserUtil.checkAndUpdateDO(policyDO, policyRow);
                final boolean isPasswdProfileUpdated = DMOnPremiseUserUtil.checkAndUpdateDO(passwdProfileDO, passwdProfileRow);
                final boolean isAccProfileUpdated = DMOnPremiseUserUtil.checkAndUpdateDO(accProfileDO, accProfileRow);
                if (policyDO.isEmpty() || passwdProfileDO.isEmpty() || accProfileDO.isEmpty()) {
                    final Long passPolicyID = (Long)policyRow.get("PASSWDRULE_ID");
                    final Long passProfileID = (Long)policyRow.get("PASSWDRULE_ID");
                    final Long accProfileID = (Long)accProfileRow.get("ACCOUNTPROFILE_ID");
                    DMOnPremiseUserUtil.updatePasswordProfileForAllUsers(passProfileID, passPolicyID, accProfileID);
                }
                isUpdated = (isPolicyUpdated && isPasswdProfileUpdated && isAccProfileUpdated);
                if (isUpdated) {
                    policyDetails.put("passPolicyID", policyRow.get("PASSWDRULE_ID"));
                    policyDetails.put("passProfileID", policyRow.get("PASSWDRULE_ID"));
                    policyDetails.put("accProfileID", accProfileRow.get("ACCOUNTPROFILE_ID"));
                }
                final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                DCEventLogUtil.getInstance().addEvent(8000, userName, (HashMap)null, "dc.uac.PASSWORDPOLICY.UPDATE", (Object)null, true);
            }
        }
        catch (final Exception e) {
            isUpdated = false;
            SSOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in addOrUpdatePasswordPolicy", e);
        }
        return isUpdated;
    }
}
