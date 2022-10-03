package org.apache.tika.pipes.fetcher.url;

import org.apache.tika.exception.TikaException;
import java.io.IOException;
import org.apache.tika.io.TikaInputStream;
import java.net.URL;
import java.util.Locale;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.fetcher.AbstractFetcher;

public class UrlFetcher extends AbstractFetcher
{
    @Override
    public InputStream fetch(final String fetchKey, final Metadata metadata) throws IOException, TikaException {
        if (fetchKey.contains("\u0000")) {
            throw new IllegalArgumentException("URL must not contain \u0000. Please review the life decisions that led you to requesting a URL with this character in it.");
        }
        if (fetchKey.toLowerCase(Locale.US).trim().startsWith("file:")) {
            throw new IllegalArgumentException("The UrlFetcher does not fetch from file shares; please use the FileSystemFetcher");
        }
        return (InputStream)TikaInputStream.get(new URL(fetchKey), metadata);
    }
}
