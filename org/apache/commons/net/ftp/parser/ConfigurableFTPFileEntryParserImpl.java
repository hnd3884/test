package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPClientConfig;
import java.text.ParseException;
import java.util.Calendar;
import org.apache.commons.net.ftp.Configurable;

public abstract class ConfigurableFTPFileEntryParserImpl extends RegexFTPFileEntryParserImpl implements Configurable
{
    private final FTPTimestampParser timestampParser;
    
    public ConfigurableFTPFileEntryParserImpl(final String regex) {
        super(regex);
        this.timestampParser = new FTPTimestampParserImpl();
    }
    
    public ConfigurableFTPFileEntryParserImpl(final String regex, final int flags) {
        super(regex, flags);
        this.timestampParser = new FTPTimestampParserImpl();
    }
    
    public Calendar parseTimestamp(final String timestampStr) throws ParseException {
        return this.timestampParser.parseTimestamp(timestampStr);
    }
    
    @Override
    public void configure(final FTPClientConfig config) {
        if (this.timestampParser instanceof Configurable) {
            final FTPClientConfig defaultCfg = this.getDefaultConfiguration();
            if (config != null) {
                if (null == config.getDefaultDateFormatStr()) {
                    config.setDefaultDateFormatStr(defaultCfg.getDefaultDateFormatStr());
                }
                if (null == config.getRecentDateFormatStr()) {
                    config.setRecentDateFormatStr(defaultCfg.getRecentDateFormatStr());
                }
                ((Configurable)this.timestampParser).configure(config);
            }
            else {
                ((Configurable)this.timestampParser).configure(defaultCfg);
            }
        }
    }
    
    protected abstract FTPClientConfig getDefaultConfiguration();
}
