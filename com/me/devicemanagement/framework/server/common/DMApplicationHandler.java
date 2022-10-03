package com.me.devicemanagement.framework.server.common;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class DMApplicationHandler
{
    private static final Logger logger;
    private static DMApplicationHandler dmAppHandler;
    private boolean desktopModuleState;
    private boolean mobileDeviceModuleState;
    private boolean osdModuleState;
    private boolean bspModuleState;
    
    private DMApplicationHandler() {
        this.initialiseVar();
    }
    
    public static DMApplicationHandler getInstance() {
        if (DMApplicationHandler.dmAppHandler == null) {
            DMApplicationHandler.dmAppHandler = new DMApplicationHandler();
        }
        return DMApplicationHandler.dmAppHandler;
    }
    
    public static void resetApplicationState() {
        DMApplicationHandler.dmAppHandler = new DMApplicationHandler();
    }
    
    private void initialiseVar() {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMApplication"));
            selectQuery.addSelectColumn(Column.getColumn("DMApplication", "*"));
            final DataObject resultDO = DataAccess.get(selectQuery);
            final Iterator appln = resultDO.getRows("DMApplication");
            while (appln.hasNext()) {
                final Row row = appln.next();
                final String applnName = (String)row.get("DMAPPLICATION_NAME");
                final boolean status = (boolean)row.get("DMAPPLICATION_STATUS");
                if (applnName.equalsIgnoreCase("dc")) {
                    this.desktopModuleState = status;
                }
                else if (applnName.equalsIgnoreCase("mdm")) {
                    this.mobileDeviceModuleState = status;
                }
                else if (applnName.equalsIgnoreCase("osd")) {
                    this.osdModuleState = status;
                }
                else {
                    if (!applnName.equalsIgnoreCase("bsp")) {
                        continue;
                    }
                    this.bspModuleState = status;
                }
            }
        }
        catch (final DataAccessException ex) {
            DMApplicationHandler.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    public boolean getDesktopModuleState() {
        return this.desktopModuleState;
    }
    
    public boolean getMobileDeviceModuleState() {
        return this.mobileDeviceModuleState;
    }
    
    public boolean getOSDModuleState() {
        return this.osdModuleState;
    }
    
    public boolean getBSPModuleState() {
        return this.bspModuleState;
    }
    
    public static boolean isMdmProduct() {
        return CustomerInfoUtil.isMDMP();
    }
    
    public static boolean isOSDProduct() {
        return CustomerInfoUtil.isOSDProduct();
    }
    
    public static boolean isBSPProduct() {
        return CustomerInfoUtil.isBSPProduct();
    }
    
    public static void setDCModulesInExtn() {
        try {
            DMApplicationHandler.logger.log(Level.INFO, "Inside setDCModulesInExtn()... The following Module_id(s) are added to extn table.");
            SyMUtil.getPersistence().delete(new Criteria(Column.getColumn("DCUserModuleExtn", "MODULE_ID"), (Object)null, 1));
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DMApplication"));
            sq.addJoin(new Join("DMApplication", "DMApplnToDCUserModRel", new String[] { "DMAPPLICATION_ID" }, new String[] { "DMAPPLICATION_ID" }, 2));
            sq.addJoin(new Join("DMApplnToDCUserModRel", "DCUserModule", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            sq.addSelectColumn(Column.getColumn("DCUserModule", "MODULE_ID"));
            sq.setCriteria(new Criteria(Column.getColumn("DMApplication", "DMAPPLICATION_STATUS"), (Object)true, 0));
            final DataObject dObj = SyMUtil.getPersistence().get(sq);
            final DataObject extObj = SyMUtil.getPersistence().constructDataObject();
            if (!dObj.isEmpty()) {
                final Iterator rows = dObj.getRows("DCUserModule");
                while (rows.hasNext()) {
                    final Row dcRow = rows.next();
                    final Row extRow = new Row("DCUserModuleExtn");
                    extRow.set("MODULE_ID", dcRow.get("MODULE_ID"));
                    extObj.addRow(extRow);
                    DMApplicationHandler.logger.log(Level.INFO, dcRow.get("MODULE_ID").toString());
                }
                SyMUtil.getPersistence().update(extObj);
                DMApplicationHandler.logger.log(Level.INFO, "Modules added successfully..");
            }
        }
        catch (final DataAccessException ex) {
            DMApplicationHandler.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    public static void updateDMApplicationStatus(final String productName, final Boolean state) throws DataAccessException {
        final UpdateQuery dmApplicationUpdateQuery = (UpdateQuery)new UpdateQueryImpl("DMApplication");
        final Criteria dcStateCriteria = new Criteria(new Column("DMApplication", "DMAPPLICATION_NAME"), (Object)productName, 0, false);
        dmApplicationUpdateQuery.setCriteria(dcStateCriteria);
        dmApplicationUpdateQuery.setUpdateColumn("DMAPPLICATION_STATUS", (Object)state);
        SyMUtil.getPersistence().update(dmApplicationUpdateQuery);
    }
    
    static {
        logger = Logger.getLogger(DMApplicationHandler.class.getName());
        DMApplicationHandler.dmAppHandler = null;
    }
}
