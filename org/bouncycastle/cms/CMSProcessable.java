package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

public interface CMSProcessable
{
    void write(final OutputStream p0) throws IOException, CMSException;
    
    Object getContent();
}
