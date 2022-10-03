package com.adventnet.sym.logging;

import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

public class BackupLoggerUtil
{
    public void performLogCleanUp() {
        final String backupLogDir = System.getProperty("server.home") + File.separator + "logs" + File.separator + "backup_logs";
        try {
            Logger.getLogger(BackupLoggerUtil.class.getName()).log(Level.INFO, "Backup log cleanup initiated");
            final HashMap<String, Integer> logProp = DCExtendedFileHandler.getLogProp();
            final HashMap<String, Integer> backupProp = DCExtendedFileHandler.getBackupLogProp();
            final Set<String> names = backupProp.keySet();
            for (final String filepattern : names) {
                final int backup_count = logProp.get(filepattern);
                if (backupProp.get(filepattern) > backup_count) {
                    final File directory = new File(backupLogDir);
                    final File[] backfiles = directory.listFiles((d, s) -> s.contains(s2));
                    Arrays.sort(backfiles, new Comparator<File>() {
                        @Override
                        public int compare(final File file1, final File file2) {
                            final long result = file2.lastModified() - file1.lastModified();
                            if (result < 0L) {
                                return -1;
                            }
                            return (result > 0L) ? 1 : 0;
                        }
                    });
                    if (backfiles.length <= backup_count) {
                        continue;
                    }
                    for (int i = backup_count; i < backfiles.length; ++i) {
                        backfiles[i].delete();
                        DCExtendedFileHandler.setBackupLogProp(filepattern, -1);
                    }
                }
            }
            Logger.getLogger(BackupLoggerUtil.class.getName()).log(Level.INFO, "Backup log cleanup completed successfully");
        }
        catch (final Exception e) {
            Logger.getLogger(BackupLoggerUtil.class.getName()).log(Level.WARNING, "Exception occured in backup log cleanup", e);
        }
    }
}
