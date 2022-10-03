package sun.net.www.content.text;

import java.io.InputStream;
import java.io.FilterInputStream;

public class PlainTextInputStream extends FilterInputStream
{
    PlainTextInputStream(final InputStream inputStream) {
        super(inputStream);
    }
}
