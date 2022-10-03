package com.adventnet.client.view.common;

import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.mickey.exception.PasswordException;
import java.nio.file.Files;
import com.zoho.framework.utils.archive.SevenZipUtils;
import java.io.File;
import java.io.OutputStream;
import com.adventnet.client.view.web.ViewContext;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.zoho.mickey.crypto.PasswordProvider;
import java.util.logging.Logger;

public class ExportUtils
{
    private static final Logger OUT;
    private static PasswordProvider passwordProvider;
    
    public static Properties getExportMaskingConfig(final DataObject viewConfig) {
        final Properties exportRedactConfig = new Properties();
        try {
            final Iterator<Row> redactConfig = viewConfig.getRows("PIIRedactConfig");
            while (redactConfig.hasNext()) {
                final Row maskInfoRow = redactConfig.next();
                exportRedactConfig.setProperty(maskInfoRow.get("COLUMNALIAS").toString(), maskInfoRow.get("REDACT_TYPE").toString());
            }
            ExportUtils.OUT.log(Level.FINE, "Redact configuration for view {0 } : {1}", new Object[] { viewConfig.getFirstValue("ViewConfiguration", 2), exportRedactConfig });
        }
        catch (final DataAccessException dae) {
            throw new RuntimeException((Throwable)dae);
        }
        return exportRedactConfig;
    }
    
    public static void generateZipOnOS(final ViewContext viewCtx, final OutputStream response, final File exportFile, final String zipName) throws Exception {
        File zipFile = null;
        zipFile = new File(exportFile.getParentFile(), zipName + ".zip");
        String password = null;
        if (isExportPasswordProtected()) {
            password = getExportPassword(viewCtx);
        }
        SevenZipUtils.zip(zipFile, exportFile, password);
        Files.copy(zipFile.toPath(), response);
    }
    
    public static String getExportPassword(final ViewContext viewCtx) {
        if (isExportPasswordProtected()) {
            try {
                return ExportUtils.passwordProvider.getPassword((Object)viewCtx);
            }
            catch (final PasswordException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static boolean isExportPasswordProtected() {
        return Boolean.valueOf(PersistenceInitializer.getConfigurationValue("export.password.enabled"));
    }
    
    static {
        OUT = Logger.getLogger(ExportUtils.class.getName());
        ExportUtils.passwordProvider = null;
        String passwordProviderImpl = PersistenceInitializer.getConfigurationValue("export.password.handler");
        passwordProviderImpl = ((passwordProviderImpl != null) ? passwordProviderImpl : "com.adventnet.client.view.common.ExportPasswordProvider");
        ExportUtils.passwordProvider = (PasswordProvider)WebClientUtil.createInstance(passwordProviderImpl);
    }
}
