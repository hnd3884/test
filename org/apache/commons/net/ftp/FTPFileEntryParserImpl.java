package org.apache.commons.net.ftp;

import java.util.List;
import java.io.IOException;
import java.io.BufferedReader;

public abstract class FTPFileEntryParserImpl implements FTPFileEntryParser
{
    @Override
    public String readNextEntry(final BufferedReader reader) throws IOException {
        return reader.readLine();
    }
    
    @Override
    public List<String> preParse(final List<String> original) {
        return original;
    }
}
