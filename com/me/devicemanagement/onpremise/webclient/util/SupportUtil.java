package com.me.devicemanagement.onpremise.webclient.util;

import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import org.json.simple.JSONObject;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.simple.parser.JSONParser;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import java.util.logging.Logger;

public class SupportUtil
{
    private static Logger logger;
    public static final String SUPPORT_FILE_CREATION = "supportFileCreation";
    public static final String SUPPORT_FILE_LOG_LIST = "logFileList";
    public static final String SUPPORT_FILE_LOG_ID = "logFileId";
    public static final String SUPPORT_FILE_DISPLAY_NAME = "displayName";
    public static final String SUPPORT_FILE_LOG_PATH = "logFilePath";
    public static final String SUPPORT_FILE_NEW_FOLDER_NAME = "newFolderName";
    public static final String SUPPORT_FILE_NEGATE = "negate";
    public static final String SUPPORT_FILE_IS_MANDATORY = "isMandatory";
    
    public JSONArray getLogListFromConfFile() {
        final String supportConfFilename = this.getSupportConfFile();
        SupportUtil.logger.log(Level.FINE, "Support file conf path - " + supportConfFilename);
        if (supportConfFilename != null && !supportConfFilename.isEmpty()) {
            try {
                final JSONParser jsonParser = new JSONParser();
                final String outFileName = SyMUtil.getInstallationDir() + File.separator + supportConfFilename;
                final JSONObject jsonObject = (JSONObject)jsonParser.parse((Reader)new FileReader(outFileName));
                SupportUtil.logger.log(Level.FINE, "Reading logfile from the path - " + supportConfFilename);
                final JSONArray jsonArray = (JSONArray)jsonParser.parse(jsonObject.get((Object)"logFileList").toString());
                return jsonArray;
            }
            catch (final FileNotFoundException e) {
                SupportUtil.logger.log(Level.SEVERE, "Error in getting log file - " + supportConfFilename, e);
            }
            catch (final Exception e2) {
                SupportUtil.logger.log(Level.SEVERE, "Error in getting support-conf-file", e2);
            }
        }
        return null;
    }
    
    public JSONArray filterLogList(final JSONArray jsonArray, final String[] inpLogIDs) {
        SupportUtil.logger.log(Level.FINE, "Filtering logs list from the given user input");
        final List<String> inpIDList = Arrays.asList(inpLogIDs);
        final JSONArray retJsonArray = new JSONArray();
        for (final Object itr : jsonArray) {
            if (((JSONObject)itr).getOrDefault((Object)"isMandatory", (Object)false).equals(true) || inpIDList.contains(((JSONObject)itr).get((Object)"logFileId"))) {
                ((JSONObject)itr).put((Object)"isNegate", (Object)false);
                retJsonArray.add(itr);
            }
            else {
                if (!((JSONObject)itr).containsKey((Object)"negate")) {
                    continue;
                }
                ((JSONObject)itr).put((Object)"isNegate", (Object)true);
                retJsonArray.add(itr);
            }
        }
        return retJsonArray;
    }
    
    public String getSupportConfFile() {
        return ProductUrlLoader.getInstance().getValue("support_file_conf");
    }
    
    static {
        SupportUtil.logger = Logger.getLogger(SupportUtil.class.getName());
    }
}
