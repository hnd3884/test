package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClientConfig;

public class NetwareFTPEntryParser extends ConfigurableFTPFileEntryParserImpl
{
    private static final String DEFAULT_DATE_FORMAT = "MMM dd yyyy";
    private static final String DEFAULT_RECENT_DATE_FORMAT = "MMM dd HH:mm";
    private static final String REGEX = "(d|-){1}\\s+\\[([-A-Z]+)\\]\\s+(\\S+)\\s+(\\d+)\\s+(\\S+\\s+\\S+\\s+((\\d+:\\d+)|(\\d{4})))\\s+(.*)";
    
    public NetwareFTPEntryParser() {
        this((FTPClientConfig)null);
    }
    
    public NetwareFTPEntryParser(final FTPClientConfig config) {
        super("(d|-){1}\\s+\\[([-A-Z]+)\\]\\s+(\\S+)\\s+(\\d+)\\s+(\\S+\\s+\\S+\\s+((\\d+:\\d+)|(\\d{4})))\\s+(.*)");
        this.configure(config);
    }
    
    @Override
    public FTPFile parseFTPEntry(final String entry) {
        final FTPFile f = new FTPFile();
        if (this.matches(entry)) {
            final String dirString = this.group(1);
            final String attrib = this.group(2);
            final String user = this.group(3);
            final String size = this.group(4);
            final String datestr = this.group(5);
            final String name = this.group(9);
            try {
                f.setTimestamp(super.parseTimestamp(datestr));
            }
            catch (final ParseException ex) {}
            if (dirString.trim().equals("d")) {
                f.setType(1);
            }
            else {
                f.setType(0);
            }
            f.setUser(user);
            f.setName(name.trim());
            f.setSize(Long.parseLong(size.trim()));
            if (attrib.indexOf("R") != -1) {
                f.setPermission(0, 0, true);
            }
            if (attrib.indexOf("W") != -1) {
                f.setPermission(0, 1, true);
            }
            return f;
        }
        return null;
    }
    
    @Override
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("NETWARE", "MMM dd yyyy", "MMM dd HH:mm");
    }
}
