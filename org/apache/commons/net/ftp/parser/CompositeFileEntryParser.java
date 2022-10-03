package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPFileEntryParserImpl;

public class CompositeFileEntryParser extends FTPFileEntryParserImpl
{
    private final FTPFileEntryParser[] ftpFileEntryParsers;
    private FTPFileEntryParser cachedFtpFileEntryParser;
    
    public CompositeFileEntryParser(final FTPFileEntryParser[] ftpFileEntryParsers) {
        this.cachedFtpFileEntryParser = null;
        this.ftpFileEntryParsers = ftpFileEntryParsers;
    }
    
    @Override
    public FTPFile parseFTPEntry(final String listEntry) {
        if (this.cachedFtpFileEntryParser != null) {
            final FTPFile matched = this.cachedFtpFileEntryParser.parseFTPEntry(listEntry);
            if (matched != null) {
                return matched;
            }
        }
        else {
            for (final FTPFileEntryParser ftpFileEntryParser : this.ftpFileEntryParsers) {
                final FTPFile matched2 = ftpFileEntryParser.parseFTPEntry(listEntry);
                if (matched2 != null) {
                    this.cachedFtpFileEntryParser = ftpFileEntryParser;
                    return matched2;
                }
            }
        }
        return null;
    }
}
