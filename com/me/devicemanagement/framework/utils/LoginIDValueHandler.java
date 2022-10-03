package com.me.devicemanagement.framework.utils;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.xml.DynamicValueHandlingException;
import com.adventnet.persistence.DataAccessException;
import java.util.StringTokenizer;
import java.util.Properties;
import com.adventnet.persistence.xml.DynamicValueHandler;

public class LoginIDValueHandler implements DynamicValueHandler
{
    public Object getColumnValue(final String tableName, final String columnName, final Properties properties, final String xmlAttrValue) throws DynamicValueHandlingException {
        Object colVal = null;
        if (xmlAttrValue != null && !xmlAttrValue.equalsIgnoreCase("")) {
            final StringTokenizer stoken = new StringTokenizer(xmlAttrValue, "/");
            final String loginName = stoken.nextToken();
            String domainName = "-";
            if (stoken.hasMoreTokens()) {
                domainName = stoken.nextToken();
            }
            try {
                colVal = this.getLoginId(loginName, domainName);
            }
            catch (final DataAccessException dae) {
                dae.printStackTrace();
            }
            return (colVal != null) ? colVal : xmlAttrValue;
        }
        return xmlAttrValue;
    }
    
    private Long getLoginId(final String loginName, final String domainName) throws DataAccessException {
        Long loginID = null;
        final Column nameCol = new Column("AaaLogin", "NAME");
        final Column domainNameCol = new Column("AaaLogin", "DOMAINNAME");
        final Criteria loginNameCri = new Criteria(nameCol, (Object)loginName, 0, true);
        final Criteria domainNameCri = new Criteria(domainNameCol, (Object)domainName, 0, true);
        final Criteria loginIdCri = loginNameCri.and(domainNameCri);
        final DataObject dataObject = DataAccess.get("AaaLogin", loginIdCri);
        if (dataObject != null && !dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("AaaLogin");
            loginID = (Long)row.get("LOGIN_ID");
        }
        return loginID;
    }
    
    public String getAttributeValue(final String s, final String s1, final Properties properties, final Object o) throws DynamicValueHandlingException {
        return null;
    }
    
    public void set(final Object o) {
    }
    
    public DataObject get() {
        return null;
    }
}
