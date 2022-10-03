package com.adventnet.client.usermgmt.web;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.util.LookUpUtil;
import java.util.logging.Logger;

public class UserManagementUtil
{
    private static Logger log;
    
    public static void createUser(final String loginName, final String password, final String roleName, final String emailAddress) throws Exception {
        final Persistence persistence = LookUpUtil.getPersistence();
        final Criteria crt = new Criteria(new Column("AaaLogin", "NAME"), (Object)loginName, 0);
        final DataObject userExistDO = persistence.get("AaaLogin", crt);
        if (!userExistDO.isEmpty()) {
            return;
        }
        final DataObject dobj = persistence.constructDataObject();
        final Row userRow = new Row("AaaUser");
        userRow.set("FIRST_NAME", (Object)loginName);
        dobj.addRow(userRow);
        if (emailAddress != null) {
            final Row aciRow = new Row("AaaContactInfo");
            aciRow.set("EMAILID", (Object)emailAddress);
            dobj.addRow(aciRow);
            final Row auciRow = new Row("AaaUserContactInfo");
            auciRow.set("USER_ID", userRow.get("USER_ID"));
            auciRow.set("CONTACTINFO_ID", aciRow.get("CONTACTINFO_ID"));
            dobj.addRow(auciRow);
        }
        final Row loginRow = new Row("AaaLogin");
        loginRow.set("NAME", (Object)loginName);
        dobj.addRow(loginRow);
        final Row accRow = new Row("AaaAccount");
        accRow.set("SERVICE_ID", (Object)AuthUtil.getServiceId("System"));
        accRow.set("ACCOUNTPROFILE_ID", (Object)AuthUtil.getAccountProfileId("Profile 2"));
        dobj.addRow(accRow);
        final Row passwordRow = new Row("AaaPassword");
        passwordRow.set("PASSWORD", (Object)password);
        passwordRow.set("PASSWDPROFILE_ID", (Object)AuthUtil.getPasswordProfileId("Profile 2"));
        dobj.addRow(passwordRow);
        final Row accPassRow = new Row("AaaAccPassword");
        accPassRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
        accPassRow.set("PASSWORD_ID", passwordRow.get("PASSWORD_ID"));
        dobj.addRow(accPassRow);
        final Row authRoleRow1 = new Row("AaaAuthorizedRole");
        authRoleRow1.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
        authRoleRow1.set("ROLE_ID", (Object)AuthUtil.getRoleId(roleName));
        dobj.addRow(authRoleRow1);
        if ("Administrator".equals(roleName)) {
            final int noOfSubAccounts = -1;
            final Row accOwnerProfileRow = new Row("AaaAccOwnerProfile");
            accOwnerProfileRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(noOfSubAccounts));
            dobj.addRow(accOwnerProfileRow);
        }
        AuthUtil.createUserAccount(dobj);
    }
    
    public static void deleteUser(final Long userId, final Long contactInfoId) throws Exception {
        final Persistence persistence = LookUpUtil.getPersistence();
        final Criteria crt = new Criteria(new Column("AaaLogin", "USER_ID"), (Object)userId, 0);
        final DataObject dobj = persistence.get("AaaLogin", crt);
        final Row row = dobj.getFirstRow("AaaLogin");
        final String loginName = (String)row.get("NAME");
        if ("admin".equals(loginName)) {
            return;
        }
        final Criteria delCrt = new Criteria(new Column("AaaUser", "USER_ID"), (Object)userId, 0);
        persistence.delete(delCrt);
        final Criteria delCrt2 = new Criteria(new Column("AaaContactInfo", "CONTACTINFO_ID"), (Object)contactInfoId, 0);
        persistence.delete(delCrt2);
    }
    
    static {
        UserManagementUtil.log = Logger.getLogger(UserManagementUtil.class.getName());
    }
}
