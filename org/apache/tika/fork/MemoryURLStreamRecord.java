package org.apache.tika.fork;

import java.net.URL;
import java.lang.ref.WeakReference;

class MemoryURLStreamRecord
{
    public WeakReference<URL> url;
    public byte[] data;
}
