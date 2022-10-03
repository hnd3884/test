package com.me.devicemanagement.onpremise.tools.servertroubleshooter.difftool.util;

import java.io.IOException;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSToolUtil;
import java.util.Properties;
import java.io.FileWriter;
import java.io.File;
import org.json.JSONObject;
import java.util.logging.Level;
import org.json.JSONArray;
import java.util.Map;
import java.util.logging.Logger;

public class DiffToolUtil
{
    private static final Logger LOGGER;
    
    public static void writeAsJSONFile(final Map<String, JSONArray> diffMap, final String diffFilename) {
        DiffToolUtil.LOGGER.log(Level.INFO, "Going to write Map as JSON file");
        JSONObject diffJson = null;
        FileWriter writer = null;
        try {
            if (diffMap != null) {
                diffJson = new JSONObject((Map)diffMap);
                final String diffFilePath = System.getProperty("server.home") + File.separator + "logs" + File.separator + diffFilename;
                writer = new FileWriter(diffFilePath);
                writer.write(diffJson.toString());
            }
        }
        catch (final Exception ex) {
            DiffToolUtil.LOGGER.log(Level.WARNING, "Caught exception in writing JSON file", ex);
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
            catch (final Exception ex) {
                DiffToolUtil.LOGGER.log(Level.WARNING, "Caught exception in closing File writer : ", ex);
            }
        }
        finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
            catch (final Exception ex2) {
                DiffToolUtil.LOGGER.log(Level.WARNING, "Caught exception in closing File writer : ", ex2);
            }
        }
    }
    
    public static void deleteDiffJSONFile(final String diffFilename) {
        DiffToolUtil.LOGGER.log(Level.INFO, "Going to delete {0} file", diffFilename);
        final String diffFilePath = System.getProperty("server.home") + File.separator + "logs" + File.separator + diffFilename;
        File jsonFile = null;
        try {
            jsonFile = new File(diffFilePath);
            DiffToolUtil.LOGGER.log(Level.INFO, "{0} file deletion status : {1}", new Object[] { diffFilename, jsonFile.delete() });
        }
        catch (final Exception ex) {
            DiffToolUtil.LOGGER.log(Level.WARNING, "Caught exception in deleting JSON file", ex);
        }
    }
    
    public static int getDiffID(String diffName) {
        final String separator = "(";
        if (diffName.contains(separator)) {
            diffName = diffName.substring(0, diffName.indexOf(separator));
        }
        final String s = diffName;
        int diffID = 0;
        switch (s) {
            case "is_table_exists": {
                diffID = 1;
                break;
            }
            case "primary_key_columns": {
                diffID = 2;
                break;
            }
            case "is_column_exists": {
                diffID = 3;
                break;
            }
            case "column_data_type": {
                diffID = 4;
                break;
            }
            case "column_max_size": {
                diffID = 5;
                break;
            }
            case "column_precision": {
                diffID = 6;
                break;
            }
            case "column_default_value": {
                diffID = 7;
                break;
            }
            case "column_nullable": {
                diffID = 8;
                break;
            }
            case "is_fk_exists": {
                diffID = 9;
                break;
            }
            case "fk_delete_rule_name": {
                diffID = 10;
                break;
            }
            case "fk_parent_tablename": {
                diffID = 11;
                break;
            }
            case "fk_child_tablename": {
                diffID = 12;
                break;
            }
            case "is_index_exists": {
                diffID = 13;
                break;
            }
            case "index_columns": {
                diffID = 14;
                break;
            }
            case "is_unique_key_exists": {
                diffID = 15;
                break;
            }
            case "uk_columns": {
                diffID = 16;
                break;
            }
            case "abandoned_childrows": {
                diffID = 17;
                break;
            }
            default: {
                diffID = -1;
                break;
            }
        }
        return diffID;
    }
    
    public static Properties getDiffToolProps() throws IOException {
        final String filterPropsFilePath = System.getProperty("ststool.home") + File.separator + DiffToolConstants.DIFFTOOL_CONF_FILE;
        final Properties diffToolProperties = STSToolUtil.getPropsFromFile(filterPropsFilePath);
        return diffToolProperties;
    }
    
    public static boolean deleteFlagFile() {
        final String flagFilePath = System.getProperty("server.home") + File.separator + "bin" + File.separator + "UpdmgrInvoked.flag";
        final File flagFile = new File(flagFilePath);
        return flagFile.delete();
    }
    
    static {
        LOGGER = Logger.getLogger("DiffToolLogger");
    }
}
