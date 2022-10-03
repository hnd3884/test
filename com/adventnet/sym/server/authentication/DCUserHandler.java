package com.adventnet.sym.server.authentication;

import java.util.Hashtable;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;

public class DCUserHandler extends DMUserHandler
{
    public static Long getLoginIDForUserID(final Long userID) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", criteria);
            if (!dataObject.isEmpty()) {
                final Row aaaLoginRow = dataObject.getRow("AaaLogin");
                Long loginID = (Long)aaaLoginRow.get("LOGIN_ID");
                if (isUserInRole(loginID, "Patch_Edition_Role")) {
                    loginID = getDummyTechId();
                }
                return loginID;
            }
        }
        catch (final Exception e) {
            DCUserHandler.logger.log(Level.WARNING, "Exception while getting LoginIDs for userID : ", e);
            return null;
        }
        return null;
    }
    
    public static ArrayList getDefaultAdministratorRoleLoginID() {
        final ArrayList<Hashtable> usersList = getUserListForRole("Administrator");
        final ArrayList returnList = new ArrayList();
        for (int i = 0; i < usersList.size(); ++i) {
            final Object loginId = usersList.get(i).get("LOGIN_ID");
            returnList.add(loginId);
        }
        return returnList;
    }
}
