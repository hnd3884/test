package com.zoho.clustering.util.logger;

import java.util.logging.Handler;
import java.util.logging.Level;
import com.zoho.clustering.util.ClassUtil;
import java.util.logging.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import com.zoho.clustering.util.MyProperties;

public class LogConfig
{
    private String prefix;
    private MyProperties props;
    
    public LogConfig(final String prefix, final MyProperties props) {
        this.prefix = null;
        this.props = null;
        this.prefix = prefix;
        this.props = props;
    }
    
    public String level() {
        return this.props.value(this.prefix + ".level", "INFO");
    }
    
    public String fileName() {
        return this.props.value(this.prefix + ".fileName");
    }
    
    public int sizeInBytes() {
        return this.props.intValue(this.prefix + ".sizeInBytes", 0);
    }
    
    public int count() {
        return this.props.intValue(this.prefix + ".noOfFiles", 1);
    }
    
    public boolean append() {
        return this.props.boolValue(this.prefix + ".append", false);
    }
    
    public String formatter() {
        return this.props.value(this.prefix + ".formatter", LogFormatter.class.getName());
    }
    
    public void registerLogger(final String loggerName) {
        final Logger logger = Logger.getLogger(loggerName);
        if (logger.getHandlers().length > 0) {
            throw new IllegalStateException("The Logger [" + loggerName + "] is already registered");
        }
        Handler handler = null;
        try {
            handler = new FileHandler(this.fileName(), this.sizeInBytes(), this.count(), this.append());
            handler.setFormatter(ClassUtil.New(this.formatter()));
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.parse(this.level().toUpperCase()));
        }
        catch (final Exception exp) {
            throw new RuntimeException("Problem while initializing the logger [" + loggerName + "]", exp);
        }
    }
}
