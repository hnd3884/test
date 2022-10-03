package org.apache.commons.net.ftp.parser;

import java.io.File;
import java.text.ParseException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClientConfig;

public class OS400FTPEntryParser extends ConfigurableFTPFileEntryParserImpl
{
    private static final String DEFAULT_DATE_FORMAT = "yy/MM/dd HH:mm:ss";
    private static final String REGEX = "(\\S+)\\s+(?:(\\d+)\\s+)?(?:(\\S+)\\s+(\\S+)\\s+)?(\\*STMF|\\*DIR|\\*FILE|\\*MEM)\\s+(?:(\\S+)\\s*)?";
    
    public OS400FTPEntryParser() {
        this((FTPClientConfig)null);
    }
    
    public OS400FTPEntryParser(final FTPClientConfig config) {
        super("(\\S+)\\s+(?:(\\d+)\\s+)?(?:(\\S+)\\s+(\\S+)\\s+)?(\\*STMF|\\*DIR|\\*FILE|\\*MEM)\\s+(?:(\\S+)\\s*)?");
        this.configure(config);
    }
    
    @Override
    public FTPFile parseFTPEntry(final String entry) {
        final FTPFile file = new FTPFile();
        file.setRawListing(entry);
        if (this.matches(entry)) {
            final String usr = this.group(1);
            final String filesize = this.group(2);
            String datestr = "";
            if (!this.isNullOrEmpty(this.group(3)) || !this.isNullOrEmpty(this.group(4))) {
                datestr = this.group(3) + " " + this.group(4);
            }
            final String typeStr = this.group(5);
            String name = this.group(6);
            boolean mustScanForPathSeparator = true;
            try {
                file.setTimestamp(super.parseTimestamp(datestr));
            }
            catch (final ParseException ex) {}
            int type;
            if (typeStr.equalsIgnoreCase("*STMF")) {
                type = 0;
                if (this.isNullOrEmpty(filesize) || this.isNullOrEmpty(name)) {
                    return null;
                }
            }
            else if (typeStr.equalsIgnoreCase("*DIR")) {
                type = 1;
                if (this.isNullOrEmpty(filesize) || this.isNullOrEmpty(name)) {
                    return null;
                }
            }
            else if (typeStr.equalsIgnoreCase("*FILE")) {
                if (name == null || !name.toUpperCase().endsWith(".SAVF")) {
                    return null;
                }
                mustScanForPathSeparator = false;
                type = 0;
            }
            else if (typeStr.equalsIgnoreCase("*MEM")) {
                mustScanForPathSeparator = false;
                type = 0;
                if (this.isNullOrEmpty(name)) {
                    return null;
                }
                if (!this.isNullOrEmpty(filesize) || !this.isNullOrEmpty(datestr)) {
                    return null;
                }
                name = name.replace('/', File.separatorChar);
            }
            else {
                type = 3;
            }
            file.setType(type);
            file.setUser(usr);
            try {
                file.setSize(Long.parseLong(filesize));
            }
            catch (final NumberFormatException ex2) {}
            if (name.endsWith("/")) {
                name = name.substring(0, name.length() - 1);
            }
            if (mustScanForPathSeparator) {
                final int pos = name.lastIndexOf(47);
                if (pos > -1) {
                    name = name.substring(pos + 1);
                }
            }
            file.setName(name);
            return file;
        }
        return null;
    }
    
    private boolean isNullOrEmpty(final String string) {
        return string == null || string.length() == 0;
    }
    
    @Override
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("OS/400", "yy/MM/dd HH:mm:ss", null);
    }
}
