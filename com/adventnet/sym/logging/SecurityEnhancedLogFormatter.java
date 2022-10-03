package com.adventnet.sym.logging;

import java.util.Properties;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SecurityEnhancedLogFormatter extends SimpleFormatter
{
    private static int flag;
    private static String sensitiveDataFilePath;
    private static Logger logger;
    private static String[] sensitiveData;
    
    public static String restrictPasswordEntry(String message, final String str) {
        final String strPrePattern = "(?i)(?:\")*(?:[a-zA-Z0-9_])*";
        final String strMiddlePattern = "[\\W_]*[:=-](?:\\s)*(?:\\S)[^, \\n]*|";
        final String strEndPattern = ">(.+?)</";
        message = message.replaceAll(strPrePattern + str + strMiddlePattern + str + strEndPattern + str, "***********");
        return message;
    }
    
    @Override
    public synchronized String format(final LogRecord record) {
        String message = this.formatMessage(record);
        if (SecurityEnhancedLogFormatter.sensitiveData != null && SecurityEnhancedLogFormatter.sensitiveData.length > 0) {
            for (final Object object : SecurityEnhancedLogFormatter.sensitiveData) {
                final String str = (String)object;
                if (message != null && !message.trim().equals("") && message.toLowerCase().contains(str.toLowerCase())) {
                    message = restrictPasswordEntry(message, str);
                }
            }
        }
        else if (SecurityEnhancedLogFormatter.flag == -1) {
            SecurityEnhancedLogFormatter.flag = 0;
            SecurityEnhancedLogFormatter.logger.log(Level.WARNING, "SecurityEnhancedLogFormatter is not used because either file " + SecurityEnhancedLogFormatter.sensitiveDataFilePath + " not found or problem while reading the file or the file is empty, so proceeding with EnhancedLogFormatter");
        }
        return LoggerUtil.defaultLogFormatter(record, message);
    }
    
    static {
        SecurityEnhancedLogFormatter.flag = -1;
        SecurityEnhancedLogFormatter.sensitiveDataFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "sensitiveLogKeyStorage.properties";
        SecurityEnhancedLogFormatter.logger = Logger.getLogger("SecurityEnhancedLogFormatter");
        try {
            if (new File(SecurityEnhancedLogFormatter.sensitiveDataFilePath).exists()) {
                final Properties sensitiveDataList = FileUtils.readPropertyFile(new File(SecurityEnhancedLogFormatter.sensitiveDataFilePath));
                final String sensitiveData = sensitiveDataList.getProperty("sensitive_key");
                if (!"".equals(sensitiveData)) {
                    SecurityEnhancedLogFormatter.sensitiveData = sensitiveData.replaceAll(" ", "").split(",");
                }
            }
        }
        catch (final Exception ex) {}
    }
}
