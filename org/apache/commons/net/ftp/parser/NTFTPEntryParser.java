package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTPClientConfig;

public class NTFTPEntryParser extends ConfigurableFTPFileEntryParserImpl
{
    private static final String DEFAULT_DATE_FORMAT = "MM-dd-yy hh:mma";
    private static final String DEFAULT_DATE_FORMAT2 = "MM-dd-yy kk:mm";
    private final FTPTimestampParser timestampParser;
    private static final String REGEX = "(\\S+)\\s+(\\S+)\\s+(?:(<DIR>)|([0-9]+))\\s+(\\S.*)";
    
    public NTFTPEntryParser() {
        this((FTPClientConfig)null);
    }
    
    public NTFTPEntryParser(final FTPClientConfig config) {
        super("(\\S+)\\s+(\\S+)\\s+(?:(<DIR>)|([0-9]+))\\s+(\\S.*)", 32);
        this.configure(config);
        final FTPClientConfig config2 = new FTPClientConfig("WINDOWS", "MM-dd-yy kk:mm", null);
        config2.setDefaultDateFormatStr("MM-dd-yy kk:mm");
        this.timestampParser = new FTPTimestampParserImpl();
        ((Configurable)this.timestampParser).configure(config2);
    }
    
    @Override
    public FTPFile parseFTPEntry(final String entry) {
        final FTPFile f = new FTPFile();
        f.setRawListing(entry);
        if (!this.matches(entry)) {
            return null;
        }
        final String datestr = this.group(1) + " " + this.group(2);
        final String dirString = this.group(3);
        final String size = this.group(4);
        final String name = this.group(5);
        try {
            f.setTimestamp(super.parseTimestamp(datestr));
        }
        catch (final ParseException e) {
            try {
                f.setTimestamp(this.timestampParser.parseTimestamp(datestr));
            }
            catch (final ParseException ex) {}
        }
        if (null == name || name.equals(".") || name.equals("..")) {
            return null;
        }
        f.setName(name);
        if ("<DIR>".equals(dirString)) {
            f.setType(1);
            f.setSize(0L);
        }
        else {
            f.setType(0);
            if (null != size) {
                f.setSize(Long.parseLong(size));
            }
        }
        return f;
    }
    
    public FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("WINDOWS", "MM-dd-yy hh:mma", null);
    }
}
