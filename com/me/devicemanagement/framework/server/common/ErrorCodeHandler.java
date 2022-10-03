package com.me.devicemanagement.framework.server.common;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import java.util.logging.Logger;

public class ErrorCodeHandler
{
    private static ErrorCodeHandler errorCodeHandler;
    public Logger logger;
    
    public ErrorCodeHandler() {
        this.logger = Logger.getLogger(ErrorCodeHandler.class.getName());
    }
    
    public static ErrorCodeHandler getInstance() {
        if (ErrorCodeHandler.errorCodeHandler == null) {
            ErrorCodeHandler.errorCodeHandler = new ErrorCodeHandler();
        }
        return ErrorCodeHandler.errorCodeHandler;
    }
    
    public String getKBURL(final long errorCode) {
        String kbURL = "--";
        try {
            final Object objKBURL = getValueFromDB("ErrorCodeToKBUrl", "ERROR_CODE", errorCode, "KB_URL");
            if (objKBURL != null) {
                kbURL = (String)objKBURL;
                kbURL = UrlReplacementUtil.replaceUrlAndAppendTrackCode(kbURL);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Failed to get KB URL for Error Code: " + errorCode, ex);
            return kbURL;
        }
        return kbURL;
    }
    
    public static Object getValueFromDB(final String tableName, final String criteriaColumnName, final long criteriaColumnValue, final String returnColumnName) throws DataAccessException {
        final Column col = Column.getColumn(tableName, criteriaColumnName);
        final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria(tableName);
        final Criteria criteria = productCodeCriteria.and(new Criteria(col, (Object)criteriaColumnValue, 0, false));
        final DataObject resDO = SyMUtil.getPersistence().get(tableName, criteria);
        if (resDO.isEmpty()) {
            return null;
        }
        return resDO.getFirstValue(tableName, returnColumnName);
    }
    
    static {
        ErrorCodeHandler.errorCodeHandler = null;
    }
}
