package org.apache.poi.poifs.filesystem;

import java.io.IOException;

public class NotOLE2FileException extends IOException
{
    public NotOLE2FileException(final String s) {
        super(s);
    }
}
