package com.me.devicemanagement.framework.server.util;

import java.io.Reader;
import org.json.simple.JSONObject;
import java.io.FileReader;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.util.logging.Logger;

public class CommonUpdatesUtil
{
    static Logger logger;
    String outFileName;
    private static CommonUpdatesUtil handler;
    
    public CommonUpdatesUtil() throws Exception {
        this.outFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "UpdatesCommon.json";
    }
    
    public static CommonUpdatesUtil getInstance() {
        if (CommonUpdatesUtil.handler == null) {
            try {
                CommonUpdatesUtil.handler = new CommonUpdatesUtil();
            }
            catch (final Exception e) {
                CommonUpdatesUtil.logger.info("Exception while creating commonupdatesutil obj" + e);
            }
        }
        return CommonUpdatesUtil.handler;
    }
    
    public String getValue(final String key) {
        try {
            if (!new File(this.outFileName).exists()) {
                CommonUpdatesUtil.logger.info("UpdatesCommon.Json file is not present");
                return null;
            }
            final JSONParser jsonParser = new JSONParser();
            final FileReader updatesReader = new FileReader(this.outFileName);
            final JSONObject updatesJSON = (JSONObject)jsonParser.parse((Reader)updatesReader);
            if (updatesJSON.containsKey((Object)key)) {
                final String value = updatesJSON.get((Object)key).toString();
                return value;
            }
        }
        catch (final Exception e) {
            CommonUpdatesUtil.logger.info("Error while getting commonUpdates value" + e.getMessage());
        }
        return null;
    }
    
    static {
        CommonUpdatesUtil.logger = Logger.getLogger(CommonUpdatesUtil.class.getName());
        CommonUpdatesUtil.handler = null;
    }
}
