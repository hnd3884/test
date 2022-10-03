package com.adventnet.sym.server.admin;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.admin.DomainHandler;

public class SoMHandler extends DomainHandler
{
    private static SoMHandler somHandler;
    private Logger somLogger;
    
    public SoMHandler() {
        this.somLogger = Logger.getLogger("SoMLogger");
    }
    
    public static synchronized SoMHandler getInstance() {
        if (SoMHandler.somHandler == null) {
            SoMHandler.somHandler = new SoMHandler();
        }
        return SoMHandler.somHandler;
    }
    
    public String getDCServerIPDetected() throws SyMException {
        String ipAddress = null;
        try {
            final DataObject resultDO = SyMUtil.getPersistence().get("DCServerIPDetected", (Criteria)null);
            this.somLogger.log(Level.FINEST, "getDCServerIPDetected resultDO : {0}", resultDO);
            if (!resultDO.isEmpty()) {
                ipAddress = (String)resultDO.getFirstValue("DCServerIPDetected", "SERVER_IPADDR");
                this.somLogger.log(Level.INFO, "getDCServerIPDetected -IP address detected successfully ");
            }
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.SEVERE, "Caught exception while getting ipAddress from DCServerIPDetected details. ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return ipAddress;
    }
    
    public DataObject addOrUpdateDCServerIPDetected(final String ipAddress) throws SyMException {
        DataObject resultDO = null;
        try {
            resultDO = com.adventnet.sym.server.util.SyMUtil.getPersistence().get("DCServerIPDetected", (Criteria)null);
            if (resultDO.isEmpty()) {
                final Row row = new Row("DCServerIPDetected");
                this.constructDCServerIPDetectedRow(row, ipAddress);
                resultDO.addRow(row);
                resultDO = com.adventnet.sym.server.util.SyMUtil.getPersistence().add(resultDO);
            }
            else {
                final Row row = resultDO.getRow("DCServerIPDetected");
                this.constructDCServerIPDetectedRow(row, ipAddress);
                resultDO.updateRow(row);
                resultDO = com.adventnet.sym.server.util.SyMUtil.getPersistence().update(resultDO);
            }
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.SEVERE, ex, () -> "Caught exception while add/update of DCServerIPDetected details. Given IP: " + ipAddress);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    private Row constructDCServerIPDetectedRow(final Row row, final String ipAddress) throws Exception {
        if (ipAddress != null) {
            row.set("SERVER_IPADDR", (Object)ipAddress);
        }
        return row;
    }
    
    static {
        SoMHandler.somHandler = null;
    }
}
