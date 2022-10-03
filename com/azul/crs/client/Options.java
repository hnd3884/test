package com.azul.crs.client;

import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.AbstractMap;
import java.util.Iterator;
import com.azul.crs.util.logging.Logger;
import java.io.File;
import java.util.LinkedList;
import java.util.Map;

enum Options
{
    props, 
    lifetimejfr, 
    stackRecordId, 
    useCRS, 
    forceSyncTimeout, 
    noDelayShutdown, 
    delayShutdown;
    
    private static final String DEFAULT_SHARED_PROPS_FILE = "crs.properties";
    private static final String DEFAULT_USER_PROPS_FILE;
    private String value;
    private static Map<Client.ClientProp, Object> clientProps;
    private static LinkedList<Map.Entry<String, String>> loggerOptions;
    
    String get() {
        return this.value;
    }
    
    boolean isSet() {
        return this.value != null;
    }
    
    int getInt() {
        return Integer.parseInt(this.value);
    }
    
    long getLong() {
        return Long.parseLong(this.value);
    }
    
    private void set(final String value) {
        this.value = value;
    }
    
    static void read(final String commandLineArgs) {
        Options.loggerOptions = new LinkedList<Map.Entry<String, String>>();
        final String envArgs = System.getenv("CRS_ARGUMENTS");
        String explicitPropsFile = getPropFileNameFromArgs(commandLineArgs);
        if (explicitPropsFile == null) {
            explicitPropsFile = getPropFileNameFromArgs(envArgs);
        }
        File propsFile;
        if (explicitPropsFile != null) {
            propsFile = new File(explicitPropsFile);
            if (!propsFile.exists()) {
                Logger.getLogger(Options.class).error("specified properties file %s does not exist", propsFile.getPath());
            }
        }
        else {
            propsFile = new File(System.getProperty("user.home") + File.separatorChar + Options.DEFAULT_USER_PROPS_FILE);
            if (!propsFile.exists()) {
                propsFile = new File(System.getProperty("java.home") + File.separatorChar + "lib" + File.separatorChar + "crs.properties");
            }
        }
        if (propsFile.exists()) {
            tryLoadingProps(propsFile);
        }
        readArgs(envArgs);
        readArgs(commandLineArgs);
        for (final Map.Entry<String, String> e : Options.loggerOptions) {
            Logger.parseOption(e.getKey(), e.getValue());
        }
        Options.loggerOptions = null;
    }
    
    private static String getPropFileNameFromArgs(final String args) {
        String propsFile = null;
        if (args != null && args.length() > 0) {
            int cPos = args.indexOf(44);
            if (cPos < 0) {
                cPos = args.length();
            }
            Label_0077: {
                if (args.charAt(0) != ',') {
                    final int ePos = args.indexOf(61);
                    if (ePos >= 0) {
                        if (ePos <= cPos) {
                            break Label_0077;
                        }
                    }
                    try {
                        valueOf(args.substring(0, cPos));
                    }
                    catch (final IllegalArgumentException theArgumentIsNotAnOptionName) {
                        propsFile = args.substring(0, cPos);
                    }
                }
            }
            int sPos = 0;
            final String propsName = Options.props.name() + "=";
            final int propsNameLength = propsName.length();
            do {
                cPos = args.indexOf(44, sPos);
                if (cPos < 0) {
                    cPos = args.length();
                }
                if (args.startsWith(propsName, sPos)) {
                    propsFile = args.substring(sPos + propsNameLength, cPos);
                    break;
                }
                sPos = cPos + 1;
            } while (sPos < args.length());
        }
        return propsFile;
    }
    
    private static void readArgs(final String args) {
        if (args == null) {
            return;
        }
        int sPos = 0;
        do {
            int cPos = args.indexOf(44, sPos);
            if (cPos == -1) {
                cPos = args.length();
            }
            int ePos = args.indexOf(61, sPos);
            if (ePos == -1 || ePos > cPos) {
                ePos = cPos;
            }
            final String name = args.substring(sPos, ePos);
            final String value = (ePos == cPos) ? "" : args.substring(ePos + 1, cPos);
            if (!Options.props.name().equals(name)) {
                process(name, value, sPos == 0 && ePos == cPos);
            }
            sPos = cPos + 1;
        } while (sPos < args.length());
    }
    
    private static void process(final String name, final String value, final boolean ignoreMaybePropsFile) {
        if (name.equals("log") || name.startsWith("log+")) {
            Options.loggerOptions.add(new AbstractMap.SimpleEntry<String, String>(name, value));
        }
        else if (Options.props.name().equals(name)) {
            tryLoadingProps(new File(value));
        }
        else if (name.length() > 0) {
            for (final Client.ClientProp p : Client.ClientProp.class.getEnumConstants()) {
                if (p.value().equals(name)) {
                    Options.clientProps.put(p, value);
                    return;
                }
            }
            try {
                valueOf(name).set(value);
            }
            catch (final IllegalArgumentException iae) {
                if (!ignoreMaybePropsFile) {
                    Logger.getLogger(Options.class).error("unrecognized CRS agent option %s ignored", name);
                }
            }
        }
    }
    
    private static void tryLoadingProps(final File file) {
        final Properties props = new Properties();
        try {
            props.load(new FileInputStream(file));
            for (final String name : props.stringPropertyNames()) {
                process(name, props.getProperty(name), false);
            }
        }
        catch (final IOException ex) {
            Logger.getLogger(Options.class).error("cannot load specified properties file %s: %s", file.getPath(), ex.getMessage());
        }
    }
    
    public static Map<Client.ClientProp, Object> getClientProps() {
        return Options.clientProps;
    }
    
    static {
        DEFAULT_USER_PROPS_FILE = ".crs" + File.separatorChar + "config.properties";
        Options.clientProps = new HashMap<Client.ClientProp, Object>();
    }
}
