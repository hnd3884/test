package com.me.idps.core.util;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;

public class DirectoryAttributeConstants
{
    public static final long RESOURCE_NAME = 2L;
    public static final long MEMBER_OF = 101L;
    public static final long MEMBER = 102L;
    public static final long DESCRIPTION = 103L;
    public static final long NAME = 104L;
    public static final long EMAIL = 106L;
    public static final long SAM_ACCOUNT_NAME = 107L;
    public static final long LAST_NAME = 108L;
    public static final long FIRST_NAME = 109L;
    public static final long MIDDLE_NAME = 110L;
    public static final long DISPLAY_NAME = 111L;
    public static final long USER_PRINCIPAL_NAME = 112L;
    public static final long MOBILE = 114L;
    public static final long DOMAIN_NETBIOS_NAME = 116L;
    public static final long DISTINGUISHED_NAME = 117L;
    public static final long STATUS = 118L;
    public static final long DEVICE_TRUST_TYPE = 119L;
    public static final long DEVICE_OS_TYPE = 120L;
    public static final long DEVICE_ID = 121L;
    public static final long AZURE_DEVICE_STATUS = 123L;
    public static final long PROFILE_TYPE = 124L;
    public static final long REGISTERED_OWNERS = 125L;
    public static final long REGISTERED_USERS = 126L;
    public static final long LAST_LOGON_TIME = 127L;
    public static final long DEPARTMENT = 128L;
    public static final long PEOPLE_EMAIL_ADDRESS = 201L;
    public static final long PEOPLE_FIRST_NAME = 202L;
    public static final long PEOPLE_MIDDLE_NAME = 203L;
    public static final long PEOPLE_LAST_NAME = 204L;
    public static final long PEOPLE_DISPLAY_NAME = 205L;
    public static final long PEOPLE_PHONE_NUMBER = 206L;
    public static final String KEY_DETAIL = "KEY_DETAIL";
    public static final int STRING_DATA_TYPE = 1;
    public static final int INT_DATA_TYPE = 2;
    
    private static Row getDirObjAttrKeyRow(Criteria criteria) throws DataAccessException {
        DataObject dObj = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("DirObjAttrKey", 1);
        if (dObj == null || (dObj != null && (dObj.isEmpty() || !dObj.containsTable("DirObjAttrKey")))) {
            IDPSlogger.ASYNCH.log(Level.INFO, "looking up attr consts from db");
            dObj = SyMUtil.getPersistenceLite().get("DirObjAttrKey", (Criteria)null);
            IDPSlogger.ASYNCH.log(Level.INFO, "adding attr consts to cache for 1 hr");
            ApiFactoryProvider.getCacheAccessAPI().putCache("DirObjAttrKey", (Object)dObj, 1, 3600);
            IDPSlogger.ASYNCH.log(Level.INFO, "added attr consts to cache");
        }
        final int curBuildNumber = 220506;
        final Criteria attrCri = new Criteria(Column.getColumn("DirObjAttrKey", "DEPRECATED_FROM_BUILD"), (Object)null, 0).or(new Criteria(Column.getColumn("DirObjAttrKey", "DEPRECATED_FROM_BUILD"), (Object)curBuildNumber, 5));
        criteria = ((attrCri != null) ? criteria.and(attrCri) : criteria);
        return dObj.getRow("DirObjAttrKey", criteria);
    }
    
    private static Object getAttr(final String critCol, final Object value, final String returnCol) throws DataAccessException {
        final Row row = getDirObjAttrKeyRow(new Criteria(Column.getColumn("DirObjAttrKey", critCol), value, 0));
        if (row != null) {
            return row.get(returnCol);
        }
        return null;
    }
    
    public static Long getAttrID(final String attrKey) throws DataAccessException {
        return (Long)getAttr("KEY", attrKey, "ATTR_ID");
    }
    
    public static String getAttrKey(final Long attrID) throws DataAccessException {
        return (String)getAttr("ATTR_ID", attrID, "KEY");
    }
    
    public static Integer getAttrType(final Long attrID) throws DataAccessException {
        return (Integer)getAttr("ATTR_ID", attrID, "ATTR_TYPE");
    }
    
    public static Integer getAttrDataType(final Long attrID) throws DataAccessException {
        return (Integer)getAttr("ATTR_ID", attrID, "ATTR_DATA_TYPE");
    }
    
    public static boolean isGroupOnOfType(final long attrID) {
        return attrID == 101L;
    }
}
