package org.apache.tika.fork;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public interface ForkResource
{
    Throwable process(final DataInputStream p0, final DataOutputStream p1) throws IOException;
}
