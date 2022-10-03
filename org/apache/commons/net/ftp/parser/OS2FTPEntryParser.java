package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClientConfig;

public class OS2FTPEntryParser extends ConfigurableFTPFileEntryParserImpl
{
    private static final String DEFAULT_DATE_FORMAT = "MM-dd-yy HH:mm";
    private static final String REGEX = "\\s*([0-9]+)\\s*(\\s+|[A-Z]+)\\s*(DIR|\\s+)\\s*(\\S+)\\s+(\\S+)\\s+(\\S.*)";
    
    public OS2FTPEntryParser() {
        this((FTPClientConfig)null);
    }
    
    public OS2FTPEntryParser(final FTPClientConfig config) {
        super("\\s*([0-9]+)\\s*(\\s+|[A-Z]+)\\s*(DIR|\\s+)\\s*(\\S+)\\s+(\\S+)\\s+(\\S.*)");
        this.configure(config);
    }
    
    @Override
    public FTPFile parseFTPEntry(final String entry) {
        final FTPFile f = new FTPFile();
        if (this.matches(entry)) {
            final String size = this.group(1);
            final String attrib = this.group(2);
            final String dirString = this.group(3);
            final String datestr = this.group(4) + " " + this.group(5);
            final String name = this.group(6);
            try {
                f.setTimestamp(super.parseTimestamp(datestr));
            }
            catch (final ParseException ex) {}
            if (dirString.trim().equals("DIR") || attrib.trim().equals("DIR")) {
                f.setType(1);
            }
            else {
                f.setType(0);
            }
            f.setName(name.trim());
            f.setSize(Long.parseLong(size.trim()));
            return f;
        }
        return null;
    }
    
    @Override
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("OS/2", "MM-dd-yy HH:mm", null);
    }
}
