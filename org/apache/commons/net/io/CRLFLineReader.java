package org.apache.commons.net.io;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;

public final class CRLFLineReader extends BufferedReader
{
    private static final char LF = '\n';
    private static final char CR = '\r';
    
    public CRLFLineReader(final Reader reader) {
        super(reader);
    }
    
    @Override
    public String readLine() throws IOException {
        final StringBuilder sb = new StringBuilder();
        boolean prevWasCR = false;
        synchronized (this.lock) {
            int intch;
            while ((intch = this.read()) != -1) {
                if (prevWasCR && intch == 10) {
                    return sb.substring(0, sb.length() - 1);
                }
                prevWasCR = (intch == 13);
                sb.append((char)intch);
            }
        }
        final String string = sb.toString();
        if (string.length() == 0) {
            return null;
        }
        return string;
    }
}
