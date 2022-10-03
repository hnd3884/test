package java.net;

import java.io.IOException;

class UnknownContentHandler extends ContentHandler
{
    static final ContentHandler INSTANCE;
    
    @Override
    public Object getContent(final URLConnection urlConnection) throws IOException {
        return urlConnection.getInputStream();
    }
    
    static {
        INSTANCE = new UnknownContentHandler();
    }
}
