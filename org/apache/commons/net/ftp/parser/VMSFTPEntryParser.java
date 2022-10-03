package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPListParseEngine;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.StringTokenizer;
import java.text.ParseException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClientConfig;

public class VMSFTPEntryParser extends ConfigurableFTPFileEntryParserImpl
{
    private static final String DEFAULT_DATE_FORMAT = "d-MMM-yyyy HH:mm:ss";
    private static final String REGEX = "(.*?;[0-9]+)\\s*(\\d+)/\\d+\\s*(\\S+)\\s+(\\S+)\\s+\\[(([0-9$A-Za-z_]+)|([0-9$A-Za-z_]+),([0-9$a-zA-Z_]+))\\]?\\s*\\([a-zA-Z]*,([a-zA-Z]*),([a-zA-Z]*),([a-zA-Z]*)\\)";
    
    public VMSFTPEntryParser() {
        this((FTPClientConfig)null);
    }
    
    public VMSFTPEntryParser(final FTPClientConfig config) {
        super("(.*?;[0-9]+)\\s*(\\d+)/\\d+\\s*(\\S+)\\s+(\\S+)\\s+\\[(([0-9$A-Za-z_]+)|([0-9$A-Za-z_]+),([0-9$a-zA-Z_]+))\\]?\\s*\\([a-zA-Z]*,([a-zA-Z]*),([a-zA-Z]*),([a-zA-Z]*)\\)");
        this.configure(config);
    }
    
    @Override
    public FTPFile parseFTPEntry(final String entry) {
        final long longBlock = 512L;
        if (this.matches(entry)) {
            final FTPFile f = new FTPFile();
            f.setRawListing(entry);
            String name = this.group(1);
            final String size = this.group(2);
            final String datestr = this.group(3) + " " + this.group(4);
            final String owner = this.group(5);
            final String[] permissions = { this.group(9), this.group(10), this.group(11) };
            try {
                f.setTimestamp(super.parseTimestamp(datestr));
            }
            catch (final ParseException ex) {}
            final StringTokenizer t = new StringTokenizer(owner, ",");
            String grp = null;
            String user = null;
            switch (t.countTokens()) {
                case 1: {
                    grp = null;
                    user = t.nextToken();
                    break;
                }
                case 2: {
                    grp = t.nextToken();
                    user = t.nextToken();
                    break;
                }
                default: {
                    grp = null;
                    user = null;
                    break;
                }
            }
            if (name.lastIndexOf(".DIR") != -1) {
                f.setType(1);
            }
            else {
                f.setType(0);
            }
            if (this.isVersioning()) {
                f.setName(name);
            }
            else {
                name = name.substring(0, name.lastIndexOf(";"));
                f.setName(name);
            }
            final long sizeInBytes = Long.parseLong(size) * longBlock;
            f.setSize(sizeInBytes);
            f.setGroup(grp);
            f.setUser(user);
            for (int access = 0; access < 3; ++access) {
                final String permission = permissions[access];
                f.setPermission(access, 0, permission.indexOf(82) >= 0);
                f.setPermission(access, 1, permission.indexOf(87) >= 0);
                f.setPermission(access, 2, permission.indexOf(69) >= 0);
            }
            return f;
        }
        return null;
    }
    
    @Override
    public String readNextEntry(final BufferedReader reader) throws IOException {
        String line = reader.readLine();
        final StringBuilder entry = new StringBuilder();
        while (line != null) {
            if (line.startsWith("Directory") || line.startsWith("Total")) {
                line = reader.readLine();
            }
            else {
                entry.append(line);
                if (line.trim().endsWith(")")) {
                    break;
                }
                line = reader.readLine();
            }
        }
        return (entry.length() == 0) ? null : entry.toString();
    }
    
    protected boolean isVersioning() {
        return false;
    }
    
    @Override
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("VMS", "d-MMM-yyyy HH:mm:ss", null);
    }
    
    @Deprecated
    public FTPFile[] parseFileList(final InputStream listStream) throws IOException {
        final FTPListParseEngine engine = new FTPListParseEngine(this);
        engine.readServerList(listStream, null);
        return engine.getFiles();
    }
}
