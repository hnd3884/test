package org.apache.tika.fork;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.Serializable;

public interface ForkProxy extends Serializable
{
    void init(final DataInputStream p0, final DataOutputStream p1);
}
