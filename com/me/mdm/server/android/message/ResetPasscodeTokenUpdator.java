package com.me.mdm.server.android.message;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ResetPasscodeTokenUpdator implements UpdateTokenHandler.TokenUpdator
{
    public Logger logger;
    
    public ResetPasscodeTokenUpdator() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void updateToken(final Long resourceId, final JSONObject tokenDetails) {
        try {
            this.addOrUpdateMdDeviceResetPasscodeToken(resourceId, String.valueOf(tokenDetails.get("ResetToken")));
            this.logger.log(Level.INFO, "Reset passcode token was updated to reseource {0} successfully", resourceId);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, (Throwable)e, () -> "Unable to persist the reset passcode token for resource " + n);
        }
        catch (final JSONException e2) {
            this.logger.log(Level.WARNING, (Throwable)e2, () -> "Unable to persist the reset passcode token for resource " + n2);
        }
    }
    
    private void addOrUpdateMdDeviceResetPasscodeToken(final Long resourceId, final String token) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceResetPasscodeToken"));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(new Criteria(new Column("MdDeviceResetPasscodeToken", "RESOURCE_ID"), (Object)resourceId, 0));
        final DataObject dO = DataAccess.get(selectQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("MdDeviceResetPasscodeToken");
            row.set("RESOURCE_ID", (Object)resourceId);
            row.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("RESET_TOKEN", (Object)token);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("MdDeviceResetPasscodeToken");
            row.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("RESET_TOKEN", (Object)token);
            dO.updateRow(row);
        }
        DataAccess.update(dO);
    }
    
    public String getResetPasscodeToken(final Long resourceId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceResetPasscodeToken"));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(new Criteria(new Column("MdDeviceResetPasscodeToken", "RESOURCE_ID"), (Object)resourceId, 0));
        final DataObject dO = DataAccess.get(selectQuery);
        if (!dO.isEmpty()) {
            return (String)dO.getRow("MdDeviceResetPasscodeToken").get("RESET_TOKEN");
        }
        return null;
    }
}
