package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.io.OutputStream;

public interface SMIMEStreamingProcessor
{
    void write(final OutputStream p0) throws IOException;
}
