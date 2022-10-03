package com.me.devicemanagement.framework.utils;

import java.util.Properties;
import java.io.IOException;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.List;

public class SanitizerUtil
{
    private static SanitizerUtil sanitizerUtil;
    private String replacementValue;
    public static final String EXPORT_SANITATION_CHARS = "export_sanitation_characters";
    public static final String EXPORT_SANITATION_REPLACE_CHAR = "export_sanitation_replacement_char";
    public static final String DEFAULT_TEXTS = "default_texts";
    private List replacedChars;
    private Logger logger;
    private List<String> defaultTextsList;
    
    private SanitizerUtil() {
        this.replacementValue = "'";
        this.replacedChars = Arrays.asList("+,@,=,\",-".split(","));
        this.logger = Logger.getLogger(SanitizerUtil.class.getName());
        this.defaultTextsList = new ArrayList<String>();
        this.logger.log(Level.INFO, "Inside constructor of SanitizerUtil");
        this.loadValuesToSanitize();
    }
    
    public static SanitizerUtil getInstance() {
        if (SanitizerUtil.sanitizerUtil == null) {
            SanitizerUtil.sanitizerUtil = new SanitizerUtil();
        }
        return SanitizerUtil.sanitizerUtil;
    }
    
    private void loadValuesToSanitize() {
        this.logger.log(Level.INFO, "Going into loadValuesToSanitize method...");
        final String filePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "customUserEntries.conf";
        final File customUserFile = new File(filePath);
        if (customUserFile.exists()) {
            try {
                final Properties props = FileUtils.readPropertyFile(customUserFile);
                if (props.containsKey("export_sanitation_characters") && props.getProperty("export_sanitation_characters") != null && !props.getProperty("export_sanitation_characters").isEmpty()) {
                    this.replacedChars = Arrays.asList(props.getProperty("export_sanitation_characters").split(","));
                    this.logger.log(Level.INFO, "export_sanitation_characters , replaced with" + this.replacedChars);
                }
                if (props.containsKey("export_sanitation_replacement_char") && props.getProperty("export_sanitation_replacement_char") != null) {
                    this.replacementValue = props.getProperty("export_sanitation_replacement_char");
                    this.logger.log(Level.INFO, "export_sanitation_replacement_char , replaced with" + this.replacementValue);
                }
                final String defaultTexts = props.getProperty("default_texts");
                if (defaultTexts != null) {
                    this.defaultTextsList = Arrays.asList(defaultTexts.split(","));
                }
                else {
                    this.defaultTextsList = new ArrayList<String>();
                }
            }
            catch (final IOException e) {
                this.logger.log(Level.SEVERE, "Exception while reading customUserEntries,hence proceeding with default values");
            }
        }
        else {
            this.logger.log(Level.SEVERE, "File " + filePath + "does'nt exist, hence proceeding with default values");
        }
    }
    
    public String sanitizeValue(String value) {
        if (value != null) {
            if (this.defaultTextsList.contains(value)) {
                value = "";
            }
            else if (!value.trim().isEmpty() && this.replacedChars.contains(value.trim().substring(0, 1))) {
                value = this.replacementValue + value;
            }
        }
        return value;
    }
    
    static {
        SanitizerUtil.sanitizerUtil = null;
    }
}
