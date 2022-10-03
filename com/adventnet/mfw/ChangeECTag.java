package com.adventnet.mfw;

import com.zoho.mickey.tools.crypto.KeyModifier;
import com.adventnet.mfw.logging.LoggerUtil;
import java.util.logging.Logger;

public class ChangeECTag
{
    static Logger out;
    
    public static void main(final String[] args) throws Exception {
        Starter.loadSystemProperties();
        final String logFileName = "ChangeECTag";
        LoggerUtil.initLog(logFileName);
        Starter.initialize();
        Starter.LoadJars();
        final String className = System.getProperty("key.modifier.main.class", "com.zoho.mickey.tools.crypto.ECTagModifier");
        ChangeECTag.out.info("key.modifier.main.class ::: " + className);
        final KeyModifier modifier = (KeyModifier)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        modifier.changeKey((args.length > 0) ? args[0] : null);
    }
    
    static {
        ChangeECTag.out = Logger.getLogger(ChangeECTag.class.getName());
    }
}
