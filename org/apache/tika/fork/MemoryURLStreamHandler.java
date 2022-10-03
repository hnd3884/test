package org.apache.tika.fork;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.URLStreamHandler;

class MemoryURLStreamHandler extends URLStreamHandler
{
    private static final AtomicInteger counter;
    private static final List<MemoryURLStreamRecord> records;
    
    public static URL createURL(final byte[] data) {
        try {
            final int i = MemoryURLStreamHandler.counter.incrementAndGet();
            final URL url = new URL("tika-in-memory", "localhost", "/" + i);
            final MemoryURLStreamRecord record = new MemoryURLStreamRecord();
            record.url = new WeakReference<URL>(url);
            record.data = data;
            MemoryURLStreamHandler.records.add(record);
            return url;
        }
        catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        final Iterator<MemoryURLStreamRecord> iterator = MemoryURLStreamHandler.records.iterator();
        while (iterator.hasNext()) {
            final MemoryURLStreamRecord record = iterator.next();
            final URL url = record.url.get();
            if (url == null) {
                iterator.remove();
            }
            else {
                if (url == u) {
                    return new MemoryURLConnection(u, record.data);
                }
                continue;
            }
        }
        throw new IOException("Unknown URL: " + u);
    }
    
    static {
        counter = new AtomicInteger();
        records = new LinkedList<MemoryURLStreamRecord>();
    }
}
