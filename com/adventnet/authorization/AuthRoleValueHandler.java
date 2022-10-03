package com.adventnet.authorization;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authentication.util.AuthUtil;
import java.util.StringTokenizer;
import java.util.Properties;
import com.adventnet.persistence.xml.DynamicValueHandler;

public class AuthRoleValueHandler implements DynamicValueHandler
{
    public Object getColumnValue(final String tableName, final String columnName, final Properties params, final String xmlAttrValue) {
        Object colVal = null;
        if (xmlAttrValue.indexOf("/") != -1) {
            final StringTokenizer stoken = new StringTokenizer(xmlAttrValue, "/");
            final String loginName = stoken.nextToken();
            final String serviceName = stoken.nextToken();
            String domainName = null;
            if (stoken.hasMoreTokens()) {
                domainName = stoken.nextToken();
            }
            try {
                colVal = AuthUtil.getAccountId(loginName, serviceName, domainName);
            }
            catch (final DataAccessException dae) {
                dae.printStackTrace();
            }
            return colVal;
        }
        return new Long(xmlAttrValue);
    }
    
    public String getAttributeValue(final String tableName, final String columnName, final Properties params, final Object columnValue) {
        return "XYZ";
    }
    
    public void set(final Object obj) {
    }
    
    public DataObject get() {
        return null;
    }
}
