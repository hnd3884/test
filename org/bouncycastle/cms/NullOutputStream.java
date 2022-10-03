package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

class NullOutputStream extends OutputStream
{
    @Override
    public void write(final byte[] array) throws IOException {
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
    }
    
    @Override
    public void write(final int n) throws IOException {
    }
}
