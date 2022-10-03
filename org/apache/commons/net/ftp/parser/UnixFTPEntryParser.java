package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPFile;
import java.util.ListIterator;
import java.util.List;
import org.apache.commons.net.ftp.FTPClientConfig;

public class UnixFTPEntryParser extends ConfigurableFTPFileEntryParserImpl
{
    static final String DEFAULT_DATE_FORMAT = "MMM d yyyy";
    static final String DEFAULT_RECENT_DATE_FORMAT = "MMM d HH:mm";
    static final String NUMERIC_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String JA_MONTH = "\u6708";
    private static final String JA_DAY = "\u65e5";
    private static final String JA_YEAR = "\u5e74";
    private static final String DEFAULT_DATE_FORMAT_JA = "M'\u6708' d'\u65e5' yyyy'\u5e74'";
    private static final String DEFAULT_RECENT_DATE_FORMAT_JA = "M'\u6708' d'\u65e5' HH:mm";
    public static final FTPClientConfig NUMERIC_DATE_CONFIG;
    private static final String REGEX = "([bcdelfmpSs-])(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s*(\\d+)\\s+(?:(\\S+(?:\\s\\S+)*?)\\s+)?(?:(\\S+(?:\\s\\S+)*)\\s+)?(\\d+(?:,\\s*\\d+)?)\\s+((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S{3}\\s+\\d{1,2})|(?:\\d{1,2}\\s+\\S{3})|(?:\\d{1,2}\u6708\\s+\\d{1,2}\u65e5))\\s+((?:\\d+(?::\\d+)?)|(?:\\d{4}\u5e74))\\s(.*)";
    final boolean trimLeadingSpaces;
    
    public UnixFTPEntryParser() {
        this((FTPClientConfig)null);
    }
    
    public UnixFTPEntryParser(final FTPClientConfig config) {
        this(config, false);
    }
    
    public UnixFTPEntryParser(final FTPClientConfig config, final boolean trimLeadingSpaces) {
        super("([bcdelfmpSs-])(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s*(\\d+)\\s+(?:(\\S+(?:\\s\\S+)*?)\\s+)?(?:(\\S+(?:\\s\\S+)*)\\s+)?(\\d+(?:,\\s*\\d+)?)\\s+((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S{3}\\s+\\d{1,2})|(?:\\d{1,2}\\s+\\S{3})|(?:\\d{1,2}\u6708\\s+\\d{1,2}\u65e5))\\s+((?:\\d+(?::\\d+)?)|(?:\\d{4}\u5e74))\\s(.*)");
        this.configure(config);
        this.trimLeadingSpaces = trimLeadingSpaces;
    }
    
    @Override
    public List<String> preParse(final List<String> original) {
        final ListIterator<String> iter = original.listIterator();
        while (iter.hasNext()) {
            final String entry = iter.next();
            if (entry.matches("^total \\d+$")) {
                iter.remove();
            }
        }
        return original;
    }
    
    @Override
    public FTPFile parseFTPEntry(final String entry) {
        final FTPFile file = new FTPFile();
        file.setRawListing(entry);
        boolean isDevice = false;
        if (this.matches(entry)) {
            final String typeStr = this.group(1);
            final String hardLinkCount = this.group(15);
            final String usr = this.group(16);
            final String grp = this.group(17);
            final String filesize = this.group(18);
            final String datestr = this.group(19) + " " + this.group(20);
            String name = this.group(21);
            if (this.trimLeadingSpaces) {
                name = name.replaceFirst("^\\s+", "");
            }
            try {
                if (this.group(19).contains("\u6708")) {
                    final FTPTimestampParserImpl jaParser = new FTPTimestampParserImpl();
                    jaParser.configure(new FTPClientConfig("UNIX", "M'\u6708' d'\u65e5' yyyy'\u5e74'", "M'\u6708' d'\u65e5' HH:mm"));
                    file.setTimestamp(jaParser.parseTimestamp(datestr));
                }
                else {
                    file.setTimestamp(super.parseTimestamp(datestr));
                }
            }
            catch (final ParseException ex) {}
            int type = 0;
            switch (typeStr.charAt(0)) {
                case 'd': {
                    type = 1;
                    break;
                }
                case 'e': {
                    type = 2;
                    break;
                }
                case 'l': {
                    type = 2;
                    break;
                }
                case 'b':
                case 'c': {
                    isDevice = true;
                    type = 0;
                    break;
                }
                case '-':
                case 'f': {
                    type = 0;
                    break;
                }
                default: {
                    type = 3;
                    break;
                }
            }
            file.setType(type);
            for (int g = 4, access = 0; access < 3; ++access, g += 4) {
                file.setPermission(access, 0, !this.group(g).equals("-"));
                file.setPermission(access, 1, !this.group(g + 1).equals("-"));
                final String execPerm = this.group(g + 2);
                if (!execPerm.equals("-") && !Character.isUpperCase(execPerm.charAt(0))) {
                    file.setPermission(access, 2, true);
                }
                else {
                    file.setPermission(access, 2, false);
                }
            }
            if (!isDevice) {
                try {
                    file.setHardLinkCount(Integer.parseInt(hardLinkCount));
                }
                catch (final NumberFormatException ex2) {}
            }
            file.setUser(usr);
            file.setGroup(grp);
            try {
                file.setSize(Long.parseLong(filesize));
            }
            catch (final NumberFormatException ex3) {}
            if (type == 2) {
                final int end = name.indexOf(" -> ");
                if (end == -1) {
                    file.setName(name);
                }
                else {
                    file.setName(name.substring(0, end));
                    file.setLink(name.substring(end + 4));
                }
            }
            else {
                file.setName(name);
            }
            return file;
        }
        return null;
    }
    
    @Override
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("UNIX", "MMM d yyyy", "MMM d HH:mm");
    }
    
    static {
        NUMERIC_DATE_CONFIG = new FTPClientConfig("UNIX", "yyyy-MM-dd HH:mm", null);
    }
}
