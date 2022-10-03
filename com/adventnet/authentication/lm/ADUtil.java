package com.adventnet.authentication.lm;

import java.util.Hashtable;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.adventnet.persistence.cache.CacheManager;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class ADUtil
{
    private static ADUtil adUtil;
    private Logger logger;
    
    public ADUtil() {
        this.logger = Logger.getLogger(ADUtil.class.getName());
    }
    
    public native String[] getADUsers(final String p0, final String p1, final String p2, final String p3) throws Exception;
    
    public native Properties getADUsersWithEmail(final String p0, final String p1, final String p2, final String p3);
    
    public static ADUtil getInstance() throws Exception {
        if (ADUtil.adUtil == null) {
            ADUtil.adUtil = new ADUtil();
        }
        return ADUtil.adUtil;
    }
    
    public void agentLog(final String str) {
        this.logger.log(Level.INFO, "Log message from Agent : {0}", str);
    }
    
    public int getAdventNetErrorCode(final String hexaCode) throws Exception {
        return 1;
    }
    
    public static int checkAndAddADUser(String loginName, final HttpServletRequest request, final String domainName, final String roleName) throws Exception {
        loginName = loginName.toLowerCase();
        final ArrayList list = new ArrayList();
        list.add("ActiveDirectoryInfo");
        DataObject dataObject = (DataObject)CacheManager.getCacheRepository().getFromCache((Object)("ADINFO_" + domainName), (List)list, false);
        if (dataObject == null) {
            final Column column = Column.getColumn("ActiveDirectoryInfo", "DEFAULTDOMAIN");
            final Criteria criteria = new Criteria(column, (Object)domainName, 0);
            dataObject = DataAccess.get("ActiveDirectoryInfo", criteria);
            CacheManager.getCacheRepository().addToCache((Object)("ADINFO_" + domainName), (Object)dataObject, (List)list);
        }
        final Row row = dataObject.getFirstRow("ActiveDirectoryInfo");
        final String serverName = (String)row.get("SERVERNAME");
        final String domain = (String)row.get("DEFAULTDOMAIN");
        final String userName = (String)row.get("USERNAME");
        String pword = (String)row.get("PASSWORD");
        pword = AuthUtil.decryptString(pword);
        final ADHandler adh = new ADHandler();
        final Properties props = adh.getADUserProperty(loginName, domain, serverName, userName, pword, false);
        if (props == null) {
            return 2;
        }
        DataObject accountDO = AuthDBUtil.getAccountDO(loginName, "System", domain);
        if (!accountDO.containsTable("AaaAccount")) {
            final WritableDataObject dobj = new WritableDataObject();
            final Row userRow = new Row("AaaUser");
            userRow.set(2, (Object)loginName);
            dobj.addRow(userRow);
            final Row loginRow = new Row("AaaLogin");
            loginRow.set(3, (Object)loginName);
            loginRow.set(2, userRow.get(1));
            loginRow.set(4, (Object)domainName);
            dobj.addRow(loginRow);
            final Row accRow = new Row("AaaAccount");
            accRow.set(3, (Object)AuthUtil.getServiceId("System"));
            accRow.set(4, (Object)AuthUtil.getAccountProfileId("Profile 1"));
            accRow.set(2, loginRow.get("LOGIN_ID"));
            final Row passwordRow = new Row("AaaPassword");
            passwordRow.set(2, (Object)loginName);
            passwordRow.set(5, (Object)AuthUtil.getPasswordProfileId("Profile 1"));
            dobj.addRow(passwordRow);
            final Row accPassRow = new Row("AaaAccPassword");
            accPassRow.set(1, accRow.get(1));
            accPassRow.set(2, passwordRow.get(1));
            dobj.addRow(accPassRow);
            String role = roleName;
            if (role == null) {
                role = request.getSession().getServletContext().getInitParameter("DEFAULT_ROLE");
            }
            if (role != null) {
                final Row accAuthRow = new Row("AaaAuthorizedRole");
                accAuthRow.set(1, accRow.get(1));
                accAuthRow.set(2, (Object)AuthUtil.getRoleId(role));
                dobj.addRow(accAuthRow);
            }
            dobj.addRow(accRow);
            final Properties generalProp = ((Hashtable<K, Properties>)props).get("General");
            if (generalProp != null) {
                final String emailAddress = generalProp.getProperty("emailAddress");
                if (emailAddress != null) {
                    final Row contactInfoRow = new Row("AaaContactInfo");
                    contactInfoRow.set(2, (Object)emailAddress);
                    dobj.addRow(contactInfoRow);
                    final Row userContactInfoRow = new Row("AaaUserContactInfo");
                    userContactInfoRow.set(1, userRow.get(1));
                    userContactInfoRow.set(2, contactInfoRow.get(1));
                    dobj.addRow(userContactInfoRow);
                }
            }
            final int noOfSubAccounts = -1;
            final Row accOwnerProfileRow = new Row("AaaAccOwnerProfile");
            accOwnerProfileRow.set(1, accRow.get(1));
            accOwnerProfileRow.set(2, (Object)new Integer(noOfSubAccounts));
            dobj.addRow(accOwnerProfileRow);
            try {
                AuthUtil.createUserAccount((DataObject)dobj);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                return 4;
            }
            accountDO = AuthDBUtil.getAccountDO(loginName, "System", domain);
            return 0;
        }
        final Row newrow = new Row("AaaUser");
        newrow.set("USER_ID", (Object)AuthUtil.getUserId(loginName));
        final DataObject dataObj = DataAccess.getForPersonality("AaaUserDetails", newrow);
        return 1;
    }
    
    static {
        ADUtil.adUtil = null;
        System.loadLibrary("ADUtil");
    }
}
