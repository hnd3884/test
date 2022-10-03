package com.me.devicemanagement.onpremise.server.authentication.summaryserver.probe;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.authentication.PasswordException;
import com.adventnet.authentication.util.AuthUtil;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;

public class PSOnPremiseUserUtil extends DMOnPremiseUserUtil
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
        catch (final DataAccessException exception) {
            PSOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception occurred while updating AAALoginExtn table");
            throw exception;
        }
    }
    
    public static Long addPSUser(final DataObject addUserDO, final JSONObject addUserJObj, final JSONObject probeHandlerObject) throws DataAccessException, PasswordException {
        Long loginID = null;
        final String password = (String)addUserJObj.get("password");
        final Integer computerScope = addUserJObj.optInt("scope", 0);
        final Integer mdmScope = addUserJObj.optInt("mdmScope", 0);
        final Integer probeScope = addUserJObj.optInt("probeScope", 0);
        final boolean isTwoFactorEnabledGlobally = addUserJObj.optBoolean("isTwoFactorEnabledGlobaly", false);
        try {
            final Long passwordPolicyId = addUserJObj.has("passwordPolicyId") ? Long.valueOf(addUserJObj.getLong("passwordPolicyId")) : null;
            final Long passwordProfileId = addUserJObj.has("passwordProfileId") ? addUserJObj.getLong("passwordProfileId") : AuthUtil.getPasswordProfileId("Profile 2");
            loginID = probeHandlerObject.getLong("loginID");
            final Long userID = probeHandlerObject.getLong("userID");
            final Long accountID = probeHandlerObject.getLong("accountID");
            final Row passwordRow = new Row("AaaPassword");
            passwordRow.set("PASSWORD", (Object)password);
            if (passwordPolicyId != null) {
                passwordRow.set("PASSWDRULE_ID", (Object)passwordPolicyId);
            }
            passwordRow.set("PASSWDPROFILE_ID", (Object)passwordProfileId);
            addUserDO.addRow(passwordRow);
            final Row accPassRow = new Row("AaaAccPassword");
            accPassRow.set("ACCOUNT_ID", (Object)accountID);
            accPassRow.set("PASSWORD_ID", passwordRow.get("PASSWORD_ID"));
            addUserDO.addRow(accPassRow);
            final int noOfSubAccounts = -1;
            final Row accOwnerProfileRow = new Row("AaaAccOwnerProfile");
            accOwnerProfileRow.set("ACCOUNT_ID", (Object)accountID);
            accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(noOfSubAccounts));
            addUserDO.addRow(accOwnerProfileRow);
            final Long currentTime = System.currentTimeMillis();
            final Row aaaLoginExtn = new Row("AaaLoginExtn");
            aaaLoginExtn.set("LOGIN_ID", (Object)loginID);
            aaaLoginExtn.set("CREATION_TIME", (Object)currentTime);
            aaaLoginExtn.set("MODIFIED_TIME", (Object)currentTime);
            aaaLoginExtn.set("SCOPE", (Object)computerScope);
            aaaLoginExtn.set("MDM_SCOPE", (Object)mdmScope);
            aaaLoginExtn.set("PROBE_SCOPE", (Object)probeScope);
            addUserDO.addRow(aaaLoginExtn);
            final Row accUserTwoFactorDetailsRow = new Row("AaaUserTwoFactorDetails");
            accUserTwoFactorDetailsRow.set("USER_ID", (Object)userID);
            accUserTwoFactorDetailsRow.set("ENABLED", (Object)isTwoFactorEnabledGlobally);
            accUserTwoFactorDetailsRow.set("TWOFACTORAUTHIMPL", (Object)"com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword");
            accUserTwoFactorDetailsRow.set("TWOFACTORPASSWORD", (Object)" ");
            addUserDO.addRow(accUserTwoFactorDetailsRow);
            AuthUtil.createUserAccount(addUserDO);
            return loginID;
        }
        catch (final DataAccessException dataAccessException) {
            PSOnPremiseUserUtil.logger.log(Level.SEVERE, "DataAccessException in 'addPSUser()'");
            throw dataAccessException;
        }
        catch (final PasswordException passwordException) {
            PSOnPremiseUserUtil.logger.log(Level.SEVERE, "PasswordException in 'addPSUser()'");
            throw passwordException;
        }
    }
    
    public static Long addPSUserWithoutPassword(final DataObject addUserDO, final JSONObject addUserJObj, final JSONObject probeHandlerObject) throws DataAccessException, PasswordException {
        Long loginID = null;
        final Integer computerScope = addUserJObj.optInt("scope", 0);
        final Integer mdmScope = addUserJObj.optInt("mdmScope", 0);
        final Integer probeScope = addUserJObj.optInt("probeScope", 0);
        final boolean isTwoFactorEnabledGlobally = addUserJObj.optBoolean("isTwoFactorEnabledGlobaly", false);
        try {
            loginID = probeHandlerObject.getLong("loginID");
            final Long userID = probeHandlerObject.getLong("userID");
            final Long accountID = probeHandlerObject.getLong("accountID");
            final int noOfSubAccounts = -1;
            final Row accOwnerProfileRow = new Row("AaaAccOwnerProfile");
            accOwnerProfileRow.set("ACCOUNT_ID", (Object)accountID);
            accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(noOfSubAccounts));
            addUserDO.addRow(accOwnerProfileRow);
            final Long currentTime = System.currentTimeMillis();
            final Row aaaLoginExtn = new Row("AaaLoginExtn");
            aaaLoginExtn.set("LOGIN_ID", (Object)loginID);
            aaaLoginExtn.set("CREATION_TIME", (Object)currentTime);
            aaaLoginExtn.set("MODIFIED_TIME", (Object)currentTime);
            aaaLoginExtn.set("SCOPE", (Object)computerScope);
            aaaLoginExtn.set("MDM_SCOPE", (Object)mdmScope);
            aaaLoginExtn.set("PROBE_SCOPE", (Object)probeScope);
            addUserDO.addRow(aaaLoginExtn);
            final Row accUserTwoFactorDetailsRow = new Row("AaaUserTwoFactorDetails");
            accUserTwoFactorDetailsRow.set("USER_ID", (Object)userID);
            accUserTwoFactorDetailsRow.set("ENABLED", (Object)isTwoFactorEnabledGlobally);
            accUserTwoFactorDetailsRow.set("TWOFACTORAUTHIMPL", (Object)"com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword");
            accUserTwoFactorDetailsRow.set("TWOFACTORPASSWORD", (Object)" ");
            addUserDO.addRow(accUserTwoFactorDetailsRow);
            DMOnPremiseUserUtil.createUserAccountWithoutPassword(addUserDO);
            return loginID;
        }
        catch (final DataAccessException dataAccessException) {
            PSOnPremiseUserUtil.logger.log(Level.SEVERE, "DataAccessException in 'addPSUser()'");
            throw dataAccessException;
        }
        catch (final PasswordException passwordException) {
            PSOnPremiseUserUtil.logger.log(Level.SEVERE, "PasswordException in 'addPSUser()'");
            throw passwordException;
        }
    }
    
    public static void modifyPSUser(final JSONObject modifyUserJObj) throws Exception {
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
        catch (final Exception exception) {
            PSOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception occurred in 'modifyPSUser()'");
            throw exception;
        }
    }
    
    public static boolean addOrUpdatePSPasswordPolicy(final JSONObject policyDetails) {
        boolean isUpdated = Boolean.FALSE;
        try {
            final Integer minLength = Integer.parseInt((String)policyDetails.get("MIN_LENGTH"));
            Integer preventReuseFor = Integer.parseInt((String)policyDetails.get("PREVENT_REUSE_FOR"));
            final Boolean isComplexPassword = (Boolean)policyDetails.get("IS_COMPLEX_PASSWORD");
            final Boolean loginRestrictionEnabled = (Boolean)policyDetails.get("ENABLE_LOGIN_RESTRICTION");
            Integer badAttempt = Integer.parseInt((String)policyDetails.get("BAD_ATTEMPT"));
            Integer lockPeriod = Integer.parseInt((String)policyDetails.get("LOCK_PERIOD"));
            final Boolean loginNameIndep = policyDetails.has("IS_LOGIN_NAME_USAGE_RESTRICTED") && (boolean)policyDetails.get("IS_LOGIN_NAME_USAGE_RESTRICTED");
            final Long passPolicyID = Long.parseLong((String)policyDetails.get("passPolicyID"));
            final Long passProfileID = Long.parseLong((String)policyDetails.get("passProfileID"));
            final Long accProfileID = Long.parseLong((String)policyDetails.get("accProfileID"));
            policyDetails.remove("passPolicyID");
            policyDetails.remove("passProfileID");
            policyDetails.remove("accProfileID");
            PSOnPremiseUserUtil.logger.log(Level.INFO, "In addOrUpdatePasswordPolicy with policyDetails : {0}.", policyDetails);
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
                policyRow.set("PASSWDRULE_ID", (Object)passPolicyID);
                policyRow.set("NAME", (Object)"CUSTOM_POLICY");
                policyRow.set("MIN_LENGTH", (Object)minLength);
                policyRow.set("MAX_LENGTH", (Object)25);
                policyRow.set("LOGINNAME_INDEPNDT", (Object)loginNameIndep);
                Row passwdProfileRow = passwdProfileDO.getRow("AaaPasswordProfile");
                if (passwdProfileRow == null) {
                    passwdProfileRow = new Row("AaaPasswordProfile");
                }
                passwdProfileRow.set("PASSWDPROFILE_ID", (Object)passProfileID);
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
                accProfileRow.set("ACCOUNTPROFILE_ID", (Object)accProfileID);
                accProfileRow.set("NAME", (Object)"CUSTOM_PROFILE");
                accProfileRow.set("ALLOWED_BADLOGIN", (Object)badAttempt);
                final boolean isPolicyUpdated = DMOnPremiseUserUtil.checkAndUpdateDO(policyDO, policyRow);
                final boolean isPasswdProfileUpdated = DMOnPremiseUserUtil.checkAndUpdateDO(passwdProfileDO, passwdProfileRow);
                final boolean isAccProfileUpdated = DMOnPremiseUserUtil.checkAndUpdateDO(accProfileDO, accProfileRow);
                if (policyDO.isEmpty() || passwdProfileDO.isEmpty() || accProfileDO.isEmpty()) {
                    DMOnPremiseUserUtil.updatePasswordProfileForAllUsers(passProfileID, passPolicyID, accProfileID);
                }
                isUpdated = (isPolicyUpdated && isPasswdProfileUpdated && isAccProfileUpdated);
                final String userName = "From Summary Server";
                DCEventLogUtil.getInstance().addEvent(8000, userName, (HashMap)null, "dc.uac.PASSWORDPOLICY.UPDATE", (Object)null, true);
            }
        }
        catch (final Exception e) {
            isUpdated = false;
            PSOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in addOrUpdatePasswordPolicy", e);
        }
        return isUpdated;
    }
    
    public static Boolean isPasswordPolicyEnabled() {
        boolean isPasswordPolicyEnabled = Boolean.FALSE;
        try {
            final SelectQuery aaaPassRuleQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaPasswordRule"));
            aaaPassRuleQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria aaaPassRuleCriteria = new Criteria(Column.getColumn("AaaPasswordRule", "NAME"), (Object)"CUSTOM_POLICY", 0, (boolean)Boolean.FALSE);
            aaaPassRuleQuery.setCriteria(aaaPassRuleCriteria);
            final DataObject aaaPassRuleDO = SyMUtil.getPersistence().get(aaaPassRuleQuery);
            if (aaaPassRuleDO != null && !aaaPassRuleDO.isEmpty()) {
                isPasswordPolicyEnabled = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            PSOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in checking isPasswordPolicyEnabled", ex);
        }
        return isPasswordPolicyEnabled;
    }
    
    public static JSONObject getPasswordPolicyDetails() {
        final JSONObject policyDetails = new JSONObject();
        try {
            final DataObject policyDO = DMOnPremiseUserUtil.getCustomPasswordPolicyDO(false);
            final DataObject passwdProfileDO = DMOnPremiseUserUtil.getCustomPasswdProfileDO(false);
            final DataObject accProfileDO = DMOnPremiseUserUtil.getCustomAccountProfileDO(false);
            if (policyDO != null && !policyDO.isEmpty()) {
                final Row policyRow = policyDO.getFirstRow("AaaPasswordRule");
                final Integer minLength = (Integer)policyRow.get("MIN_LENGTH");
                final Boolean mixedCase = (Boolean)policyRow.get("REQ_MIXEDCASE");
                final Boolean loginNameIndep = (Boolean)policyRow.get("LOGINNAME_INDEPNDT");
                policyDetails.put("IS_LOGIN_NAME_USAGE_RESTRICTED", (Object)loginNameIndep);
                policyDetails.put("MIN_LENGTH", (Object)minLength);
                policyDetails.put("IS_COMPLEX_PASSWORD", (Object)mixedCase);
            }
            if (passwdProfileDO != null && !passwdProfileDO.isEmpty()) {
                final Row profileRow = passwdProfileDO.getFirstRow("AaaPasswordProfile");
                Integer preventReuseFor = (Integer)profileRow.get("NUMOF_OLDPASSWD");
                final Integer lockPeriod = (Integer)profileRow.get("UPDATE_INTERVAL");
                preventReuseFor = ((preventReuseFor == -1) ? 0 : preventReuseFor);
                policyDetails.put("PREVENT_REUSE_FOR", (Object)preventReuseFor);
                policyDetails.put("LOCK_PERIOD", (Object)lockPeriod);
            }
            if (accProfileDO != null && !accProfileDO.isEmpty()) {
                final Row profileRow = accProfileDO.getFirstRow("AaaAccAdminProfile");
                Integer badAttempt = (Integer)profileRow.get("ALLOWED_BADLOGIN");
                boolean loginRestrictionEnabled = true;
                if (badAttempt < 1) {
                    badAttempt = 1;
                    loginRestrictionEnabled = false;
                }
                policyDetails.put("BAD_ATTEMPT", (Object)badAttempt);
                policyDetails.put("ENABLE_LOGIN_RESTRICTION", loginRestrictionEnabled);
            }
        }
        catch (final Exception ex) {
            PSOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getPasswordPolicyDetails", ex);
        }
        return policyDetails;
    }
}
