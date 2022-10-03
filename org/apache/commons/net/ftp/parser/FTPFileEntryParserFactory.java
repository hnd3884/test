package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFileEntryParser;

public interface FTPFileEntryParserFactory
{
    FTPFileEntryParser createFileEntryParser(final String p0) throws ParserInitializationException;
    
    FTPFileEntryParser createFileEntryParser(final FTPClientConfig p0) throws ParserInitializationException;
}
