package com.me.idps.core;

import com.me.idps.core.util.DirectoryUtil;
import com.me.idps.core.util.IdpsUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;

public class IdpsPostStartupUpgradeHandler
{
    private static void handleUpgrade(final int buildNumberBeforeUpgrade) throws Exception {
        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
        dirProdImplRequest.eventType = IdpEventConstants.HANDLE_UPGRADE;
        dirProdImplRequest.args = new Object[] { buildNumberBeforeUpgrade };
        DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
    }
    
    public static void handleUpgrade() {
        try {
            int buildNumberBeforeUpgrade = 0;
            final String buildNumberBeforeUpgradeStr = String.valueOf(DBUtil.getValueFromDB("DirectoryMetrics", "KEY", (Object)"VERSION", "VALUE"));
            IDPSlogger.UPGRADE.log(Level.INFO, "buildNumberBeforeUpgradeStr : {0}", new Object[] { buildNumberBeforeUpgradeStr });
            if (!IdpsUtil.isStringEmpty(buildNumberBeforeUpgradeStr)) {
                buildNumberBeforeUpgrade = Integer.parseInt(buildNumberBeforeUpgradeStr);
            }
            IDPSlogger.UPGRADE.log(Level.INFO, "buildNumberBeforeUpgrade : {0} , DIR_SYNC_LATEST_BUILD : {1}", new Object[] { buildNumberBeforeUpgrade, 220506 });
            if (buildNumberBeforeUpgrade < 220506) {
                handleUpgrade(buildNumberBeforeUpgrade);
                DirectoryUtil.getInstance().updateDirectoryMetrics();
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
}
