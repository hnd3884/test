package com.me.idps.core.sync.synch;

import com.me.idps.core.util.IdpsUtil;
import com.me.idps.core.util.DirectoryUtil;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.simple.JSONObject;
import com.me.idps.core.util.DirectoryTestAPI;

public class DirectoryEndStatehandler
{
    public static DirectoryTestAPI dta;
    private static int isTestAdapterPresent;
    private static DirectoryEndStatehandler directoryEndStatehandler;
    
    public static DirectoryEndStatehandler getInstance() {
        if (DirectoryEndStatehandler.directoryEndStatehandler == null) {
            DirectoryEndStatehandler.directoryEndStatehandler = new DirectoryEndStatehandler();
        }
        return DirectoryEndStatehandler.directoryEndStatehandler;
    }
    
    private void initTestAdapter(final Long customerID, final String source, final JSONObject opsDetails) {
        try {
            if (DirectoryEndStatehandler.isTestAdapterPresent == 0) {
                DirectoryEndStatehandler.dta = (DirectoryTestAPI)Class.forName("com.me.idps.test.DirTestConductor").newInstance();
                DirectoryEndStatehandler.isTestAdapterPresent = 2;
            }
        }
        catch (final Exception ex) {
            DirectoryEndStatehandler.isTestAdapterPresent = 1;
        }
        try {
            if (DirectoryEndStatehandler.dta != null && DirectoryEndStatehandler.isTestAdapterPresent == 2) {
                DirectoryEndStatehandler.dta.init(customerID, source, opsDetails);
            }
        }
        catch (final Exception ex2) {}
    }
    
    void handleEndstate(final Long customerID, final String source, final JSONObject opsDetails) {
        IDPSlogger.SYNC.log(Level.INFO, "winding up!");
        IdpsFactoryProvider.getIdpsProdEnvAPI().normalizeDB();
        DirectoryMetricsDataHandler.getInstance().updateDirTrackingDetails(customerID);
        DirectoryUtil.getInstance().hideOrShowDirectoryMsg();
        this.initTestAdapter(customerID, source, opsDetails);
        IDPSlogger.SYNC.log(Level.INFO, "done!");
        try {
            if (IdpsUtil.isFeatureAvailable("PERPETUAL_DIFF_SYNC")) {
                DirectoryUtil.getInstance().syncAllDomains(customerID, false);
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
    
    static {
        DirectoryEndStatehandler.dta = null;
        DirectoryEndStatehandler.isTestAdapterPresent = 0;
        DirectoryEndStatehandler.directoryEndStatehandler = null;
    }
}
