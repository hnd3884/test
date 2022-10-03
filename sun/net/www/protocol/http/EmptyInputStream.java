package sun.net.www.protocol.http;

import java.io.InputStream;

class EmptyInputStream extends InputStream
{
    @Override
    public int available() {
        return 0;
    }
    
    @Override
    public int read() {
        return -1;
    }
}
