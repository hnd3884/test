package org.apache.tomcat.util.http.fileupload;

public interface ProgressListener
{
    void update(final long p0, final long p1, final int p2);
}
