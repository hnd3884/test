package org.apache.commons.net.ftp.parser;

import java.util.Date;
import java.text.ParsePosition;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import org.apache.commons.net.ftp.FTPFile;
import java.util.HashMap;
import org.apache.commons.net.ftp.FTPFileEntryParserImpl;

public class MLSxEntryParser extends FTPFileEntryParserImpl
{
    private static final MLSxEntryParser PARSER;
    private static final HashMap<String, Integer> TYPE_TO_INT;
    private static int[] UNIX_GROUPS;
    private static int[][] UNIX_PERMS;
    
    @Override
    public FTPFile parseFTPEntry(final String entry) {
        if (entry.startsWith(" ")) {
            if (entry.length() > 1) {
                final FTPFile file = new FTPFile();
                file.setRawListing(entry);
                file.setName(entry.substring(1));
                return file;
            }
            return null;
        }
        else {
            final String[] parts = entry.split(" ", 2);
            if (parts.length != 2 || parts[1].length() == 0) {
                return null;
            }
            final String factList = parts[0];
            if (!factList.endsWith(";")) {
                return null;
            }
            final FTPFile file2 = new FTPFile();
            file2.setRawListing(entry);
            file2.setName(parts[1]);
            final String[] facts = factList.split(";");
            final boolean hasUnixMode = parts[0].toLowerCase(Locale.ENGLISH).contains("unix.mode=");
            for (final String fact : facts) {
                final String[] factparts = fact.split("=", -1);
                if (factparts.length != 2) {
                    return null;
                }
                final String factname = factparts[0].toLowerCase(Locale.ENGLISH);
                final String factvalue = factparts[1];
                if (factvalue.length() != 0) {
                    final String valueLowerCase = factvalue.toLowerCase(Locale.ENGLISH);
                    if ("size".equals(factname)) {
                        file2.setSize(Long.parseLong(factvalue));
                    }
                    else if ("sizd".equals(factname)) {
                        file2.setSize(Long.parseLong(factvalue));
                    }
                    else if ("modify".equals(factname)) {
                        final Calendar parsed = parseGMTdateTime(factvalue);
                        if (parsed == null) {
                            return null;
                        }
                        file2.setTimestamp(parsed);
                    }
                    else if ("type".equals(factname)) {
                        final Integer intType = MLSxEntryParser.TYPE_TO_INT.get(valueLowerCase);
                        if (intType == null) {
                            file2.setType(3);
                        }
                        else {
                            file2.setType(intType);
                        }
                    }
                    else if (factname.startsWith("unix.")) {
                        final String unixfact = factname.substring("unix.".length()).toLowerCase(Locale.ENGLISH);
                        if ("group".equals(unixfact)) {
                            file2.setGroup(factvalue);
                        }
                        else if ("owner".equals(unixfact)) {
                            file2.setUser(factvalue);
                        }
                        else if ("mode".equals(unixfact)) {
                            final int off = factvalue.length() - 3;
                            for (int i = 0; i < 3; ++i) {
                                final int ch = factvalue.charAt(off + i) - '0';
                                if (ch >= 0 && ch <= 7) {
                                    for (final int p : MLSxEntryParser.UNIX_PERMS[ch]) {
                                        file2.setPermission(MLSxEntryParser.UNIX_GROUPS[i], p, true);
                                    }
                                }
                            }
                        }
                    }
                    else if (!hasUnixMode && "perm".equals(factname)) {
                        this.doUnixPerms(file2, valueLowerCase);
                    }
                }
            }
            return file2;
        }
    }
    
    public static Calendar parseGMTdateTime(final String timestamp) {
        SimpleDateFormat sdf;
        boolean hasMillis;
        if (timestamp.contains(".")) {
            sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
            hasMillis = true;
        }
        else {
            sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            hasMillis = false;
        }
        final TimeZone GMT = TimeZone.getTimeZone("GMT");
        sdf.setTimeZone(GMT);
        final GregorianCalendar gc = new GregorianCalendar(GMT);
        final ParsePosition pos = new ParsePosition(0);
        sdf.setLenient(false);
        final Date parsed = sdf.parse(timestamp, pos);
        if (pos.getIndex() != timestamp.length()) {
            return null;
        }
        gc.setTime(parsed);
        if (!hasMillis) {
            gc.clear(14);
        }
        return gc;
    }
    
    private void doUnixPerms(final FTPFile file, final String valueLowerCase) {
        for (final char c : valueLowerCase.toCharArray()) {
            switch (c) {
                case 'a': {
                    file.setPermission(0, 1, true);
                    break;
                }
                case 'c': {
                    file.setPermission(0, 1, true);
                    break;
                }
                case 'd': {
                    file.setPermission(0, 1, true);
                    break;
                }
                case 'e': {
                    file.setPermission(0, 0, true);
                }
                case 'l': {
                    file.setPermission(0, 2, true);
                    break;
                }
                case 'm': {
                    file.setPermission(0, 1, true);
                    break;
                }
                case 'p': {
                    file.setPermission(0, 1, true);
                    break;
                }
                case 'r': {
                    file.setPermission(0, 0, true);
                    break;
                }
                case 'w': {
                    file.setPermission(0, 1, true);
                    break;
                }
            }
        }
    }
    
    public static FTPFile parseEntry(final String entry) {
        return MLSxEntryParser.PARSER.parseFTPEntry(entry);
    }
    
    public static MLSxEntryParser getInstance() {
        return MLSxEntryParser.PARSER;
    }
    
    static {
        PARSER = new MLSxEntryParser();
        (TYPE_TO_INT = new HashMap<String, Integer>()).put("file", 0);
        MLSxEntryParser.TYPE_TO_INT.put("cdir", 1);
        MLSxEntryParser.TYPE_TO_INT.put("pdir", 1);
        MLSxEntryParser.TYPE_TO_INT.put("dir", 1);
        MLSxEntryParser.UNIX_GROUPS = new int[] { 0, 1, 2 };
        MLSxEntryParser.UNIX_PERMS = new int[][] { new int[0], { 2 }, { 1 }, { 2, 1 }, { 0 }, { 0, 2 }, { 0, 1 }, { 0, 1, 2 } };
    }
}
