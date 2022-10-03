package com.me.devicemanagement.onpremise.server.authentication;

import com.adventnet.persistence.DataAccessException;
import java.util.Collection;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import java.util.List;
import com.adventnet.ds.query.Join;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQuery;
import java.util.logging.Logger;
import com.adventnet.persistence.DataAccess;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;

public class DMOnPremiseUserHandler extends DMUserHandler
{
    public static boolean deleteDCUsersExceptCurrent(final Long currentloginID) {
        DMOnPremiseUserHandler.logger.log(Level.INFO, "deleteDCUsersExceptCurrent: Going to delete Extra Users; Action triggered by loginID {0}, user {1}", new Object[] { currentloginID, DMUserHandler.getUserName(currentloginID) });
        synchronized (DMOnPremiseUserHandler.USER_HANDLING_LOCK) {
            try {
                final Long adminLoginId = DBUtil.getUVHValue("AaaLogin:login_id:0");
                final Long dummyLoginId = DBUtil.getUVHValue("AaaLogin:login_id:3");
                final Long adminUserId = getDCUserID(adminLoginId);
                Boolean disableAdmin = false;
                Criteria defaultUserCriteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)dummyLoginId, 1);
                if (!adminLoginId.equals(currentloginID)) {
                    disableAdmin = true;
                    final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaUserStatus");
                    final Criteria criteria = new Criteria(Column.getColumn("AaaUserStatus", "USER_ID"), (Object)adminUserId, 0);
                    updateQuery.setUpdateColumn("STATUS", (Object)"DISABLED");
                    updateQuery.setCriteria(criteria);
                    SyMUtil.getPersistence().update(updateQuery);
                    deleteOldPasswordForLogin(adminLoginId);
                    final Criteria selectCriteria = new Criteria(new Column("UsersRoleMapping", "LOGIN_ID"), (Object)adminLoginId, 0);
                    SyMUtil.getPersistence().delete(selectCriteria);
                    defaultUserCriteria = defaultUserCriteria.and(new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)adminLoginId, 1));
                    addOrUpdateAPIKeyForLoginId(adminLoginId);
                }
                Criteria selectCriteria2 = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)currentloginID, 1);
                selectCriteria2 = selectCriteria2.and(defaultUserCriteria);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
                selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
                selectQuery.addSelectColumn(new Column("AaaLogin", "NAME"));
                selectQuery.addSelectColumn(new Column("AaaLogin", "DOMAINNAME"));
                selectQuery.setCriteria(selectCriteria2);
                final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
                final Iterator deletedUserItr = dataObject.getRows("AaaLogin");
                while (deletedUserItr.hasNext()) {
                    final Row deletedUser = deletedUserItr.next();
                    final String deletedUserName = deletedUser.get("NAME") + "";
                    String domainName = deletedUser.get("DOMAINNAME") + "";
                    if (domainName.equalsIgnoreCase("null") || domainName.equalsIgnoreCase("-")) {
                        domainName = I18N.getMsg("desktopcentral.configurations.config.LOCAL", new Object[0]);
                    }
                    DCEventLogUtil.getInstance().addEvent(705, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, "dc.admin.uac.USER_SUCCESSFULLY_DEL", (Object)(domainName + "@@@" + deletedUserName), true);
                }
                DataAccess.delete(selectCriteria2);
                DMOnPremiseUserHandler.logger.log(Level.INFO, "deleteDCUsersExceptCurrent: Going to delete Extra Users in login; Action triggered by loginID {0}, user {1}", new Object[] { currentloginID, DMUserHandler.getUserName(currentloginID) });
                final Long userIDs = getDCUserID(currentloginID);
                final Long dummyUserId = getDCUserID(dummyLoginId);
                final Long[] retainUserIds = { userIDs, dummyUserId, null };
                if (disableAdmin) {
                    retainUserIds[2] = adminUserId;
                }
                final Criteria crtiteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)retainUserIds, 9);
                DataAccess.delete(crtiteria);
                final Criteria twoFactorCriteria = new Criteria(Column.getColumn("AaaUserTwoFactorDetails", "USER_ID"), (Object)retainUserIds, 9);
                DataAccess.delete(twoFactorCriteria);
                DMOnPremiseUserHandler.logger.log(Level.INFO, "deleteDCUsersExceptCurrent: Going to delete Extra Users in MSP; Action triggered by loginID {0}, user {1}", new Object[] { currentloginID, DMUserHandler.getUserName(currentloginID) });
            }
            catch (final Exception ex) {
                Logger.getLogger(DMUserHandler.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }
    
    public static void deleteOldPasswordForLogin(final Long loginId) {
        try {
            final ArrayList passwordsToDelete = new ArrayList();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaAccount"));
            final Join oldPasswordJoin = new Join("AaaAccount", "AaaAccOldPassword", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
            final Join passwordJoin = new Join("AaaAccOldPassword", "AaaPassword", new String[] { "OLDPASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 2);
            final Criteria criteria = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)loginId, 0);
            selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "LOGIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaPassword", "PASSWORD_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccOldPassword", "ACCOUNT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccOldPassword", "OLDPASSWORD_ID"));
            selectQuery.addJoin(oldPasswordJoin);
            selectQuery.addJoin(passwordJoin);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator passwordItr = dataObject.getRows("AaaPassword");
                while (passwordItr.hasNext()) {
                    final Row passwordRow = passwordItr.next();
                    final Long passwordId = (Long)passwordRow.get("PASSWORD_ID");
                    passwordsToDelete.add(passwordId);
                }
                final Criteria passwordCriteria = new Criteria(Column.getColumn("AaaPassword", "PASSWORD_ID"), (Object)passwordsToDelete.toArray(), 8);
                SyMUtil.getPersistence().delete(passwordCriteria);
            }
        }
        catch (final Exception ex) {
            DMOnPremiseUserHandler.logger.log(Level.SEVERE, "Exception while Deleting Old Passwords for Login:" + loginId, ex);
        }
    }
    
    public static List<String> getEditionAndEnterpriseRoles() throws DataAccessException, Exception {
        final List<String> editionandEnterpriseRoles = new ArrayList<String>();
        final String addFeatureRolesVer = UserMgmtUtil.getUserMgmtParameter("EPR_FEATURE_VERSION");
        if (CustomerInfoUtil.isPMPOrPatchEdition()) {
            editionandEnterpriseRoles.add("Patch_Edition_Role");
        }
        else if (CustomerInfoUtil.getInstance().isRAP()) {
            getRemoteAccessRolesBasedOnLicense(editionandEnterpriseRoles);
        }
        final boolean isProfEdition = new LicenseProvider().isProfessionalEdition();
        if (!isProfEdition) {
            final List<String> featureRoleNames = getEnterpriseRoleNames((Criteria)null);
            editionandEnterpriseRoles.addAll(featureRoleNames);
        }
        else if (addFeatureRolesVer != null) {
            final Criteria cri = new Criteria(Column.getColumn("AaaRoleEdition", "VERSION"), (Object)Integer.parseInt(addFeatureRolesVer), 4);
            final List<String> featureRoleNames2 = getEnterpriseRoleNames(cri);
            editionandEnterpriseRoles.addAll(featureRoleNames2);
        }
        return editionandEnterpriseRoles;
    }
    
    private static void getRemoteAccessRolesBasedOnLicense(final List<String> rolesList) {
        if (CustomerInfoUtil.getInstance().isRAP()) {
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            rolesList.add("RA_Limited");
            final String productType = LicenseProvider.getInstance().getProductType();
            if (!productType.equalsIgnoreCase("Tools_Standard")) {
                rolesList.add("RA_Full");
            }
        }
    }
}
