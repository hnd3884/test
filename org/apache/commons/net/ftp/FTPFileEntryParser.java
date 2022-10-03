package org.apache.commons.net.ftp;

import java.util.List;
import java.io.IOException;
import java.io.BufferedReader;

public interface FTPFileEntryParser
{
    FTPFile parseFTPEntry(final String p0);
    
    String readNextEntry(final BufferedReader p0) throws IOException;
    
    List<String> preParse(final List<String> p0);
}
