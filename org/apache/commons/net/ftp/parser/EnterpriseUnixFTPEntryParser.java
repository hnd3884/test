package org.apache.commons.net.ftp.parser;

import java.util.Calendar;
import org.apache.commons.net.ftp.FTPFile;

public class EnterpriseUnixFTPEntryParser extends RegexFTPFileEntryParserImpl
{
    private static final String MONTHS = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
    private static final String REGEX = "(([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z]))(\\S*)\\s*(\\S+)\\s*(\\S*)\\s*(\\d*)\\s*(\\d*)\\s*(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s*((?:[012]\\d*)|(?:3[01]))\\s*((\\d\\d\\d\\d)|((?:[01]\\d)|(?:2[0123])):([012345]\\d))\\s(\\S*)(\\s*.*)";
    
    public EnterpriseUnixFTPEntryParser() {
        super("(([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z]))(\\S*)\\s*(\\S+)\\s*(\\S*)\\s*(\\d*)\\s*(\\d*)\\s*(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s*((?:[012]\\d*)|(?:3[01]))\\s*((\\d\\d\\d\\d)|((?:[01]\\d)|(?:2[0123])):([012345]\\d))\\s(\\S*)(\\s*.*)");
    }
    
    @Override
    public FTPFile parseFTPEntry(final String entry) {
        final FTPFile file = new FTPFile();
        file.setRawListing(entry);
        if (this.matches(entry)) {
            final String usr = this.group(14);
            final String grp = this.group(15);
            final String filesize = this.group(16);
            final String mo = this.group(17);
            final String da = this.group(18);
            final String yr = this.group(20);
            final String hr = this.group(21);
            final String min = this.group(22);
            final String name = this.group(23);
            file.setType(0);
            file.setUser(usr);
            file.setGroup(grp);
            try {
                file.setSize(Long.parseLong(filesize));
            }
            catch (final NumberFormatException ex) {}
            final Calendar cal = Calendar.getInstance();
            cal.set(14, 0);
            cal.set(13, 0);
            cal.set(12, 0);
            cal.set(11, 0);
            final int pos = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)".indexOf(mo);
            final int month = pos / 4;
            try {
                int missingUnit;
                if (yr != null) {
                    cal.set(1, Integer.parseInt(yr));
                    missingUnit = 11;
                }
                else {
                    missingUnit = 13;
                    int year = cal.get(1);
                    if (cal.get(2) < month) {
                        --year;
                    }
                    cal.set(1, year);
                    cal.set(11, Integer.parseInt(hr));
                    cal.set(12, Integer.parseInt(min));
                }
                cal.set(2, month);
                cal.set(5, Integer.parseInt(da));
                cal.clear(missingUnit);
                file.setTimestamp(cal);
            }
            catch (final NumberFormatException ex2) {}
            file.setName(name);
            return file;
        }
        return null;
    }
}
