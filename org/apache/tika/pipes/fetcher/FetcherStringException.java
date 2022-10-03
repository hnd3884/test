package org.apache.tika.pipes.fetcher;

import org.apache.tika.exception.TikaException;

public class FetcherStringException extends TikaException
{
    public FetcherStringException(final String msg) {
        super(msg);
    }
}
