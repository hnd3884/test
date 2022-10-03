package com.adventnet.db.adapter;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.io.IOException;
import com.adventnet.persistence.json.Do2JsonConverter;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.io.File;
import java.util.logging.Logger;

public class BackupRestoreUtil
{
    private static final Logger LOGGER;
    
    public static String getDynamicColumnsInfoFileLocation(final String backupFolder) throws IOException, JSONException, DataAccessException {
        final String filePath = new File(backupFolder + File.separator + "dcinfo.json").getCanonicalPath();
        final Criteria dynamicColumnsCriteria = new Criteria(Column.getColumn("ColumnDetails", "IS_DYNAMIC"), true, 0);
        Do2JsonConverter.serialize(DataAccess.get("ColumnDetails", dynamicColumnsCriteria), filePath);
        return filePath;
    }
    
    static void executeCommand(final List<String> cmds, final Properties envProps, String errorMsgToIgnore) throws BackupRestoreException {
        if (errorMsgToIgnore != null) {
            errorMsgToIgnore = errorMsgToIgnore.toLowerCase(Locale.ENGLISH);
        }
        Process process = null;
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(cmds);
            final Map<String, String> environment = processBuilder.environment();
            if (envProps != null) {
                final Enumeration<Object> keys = ((Hashtable<Object, V>)envProps).keys();
                while (keys.hasMoreElements()) {
                    final String key = keys.nextElement();
                    environment.put(key, envProps.getProperty(key));
                }
            }
            process = processBuilder.start();
            try (final BufferedReader inputStreamBuff = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = null;
                while ((line = inputStreamBuff.readLine()) != null) {
                    BackupRestoreUtil.LOGGER.info(line);
                }
            }
            try (final BufferedReader errStreamBuff = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String errStreamString = null;
                while ((errStreamString = errStreamBuff.readLine()) != null) {
                    BackupRestoreUtil.LOGGER.warning(errStreamString);
                    errStreamString = errStreamString.toLowerCase(Locale.ENGLISH);
                    if (errorMsgToIgnore != null && !errStreamString.contains(errorMsgToIgnore)) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND);
                    }
                }
            }
            final int waitFor = process.waitFor();
            final int exitValue = process.exitValue();
            BackupRestoreUtil.LOGGER.log(Level.INFO, "Wait For returns :: [{0}] Process exitValue :: [{1}]", new Object[] { waitFor, exitValue });
            if (exitValue != 0) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e);
        }
        finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(BackupRestoreUtil.class.getName());
    }
}
