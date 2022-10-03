package com.me.devicemanagement.framework.server.license;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class EMSProductMigration
{
    public static void migrateProduct(final License oldLicenseObject, final License newLicenseObject) {
        License.storeOldLicenseObject(oldLicenseObject);
        License.setNewLicenseObject(newLicenseObject);
        try {
            ApiFactoryProvider.getUtilAccessAPI().migrationHandling();
        }
        catch (final Exception exception) {
            Logger.getLogger(EMSProductMigration.class.getName()).log(Level.INFO, "Exception while gettings  Migration Flag", exception);
        }
    }
    
    public static void migrateProduct(final String oldProductCode, final String newProductCode) {
        try {
            ApiFactoryProvider.getUtilAccessAPI().migrationHandling(oldProductCode, newProductCode);
        }
        catch (final Exception exception) {
            Logger.getLogger(EMSProductMigration.class.getName()).log(Level.INFO, "Exception while gettings  Migration Flag", exception);
        }
    }
}
