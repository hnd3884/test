package org.apache.tika.mime;

import java.io.Serializable;

interface Clause extends Serializable
{
    boolean eval(final byte[] p0);
    
    int size();
}
