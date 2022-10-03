package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import org.bouncycastle.util.io.StreamOverflowException;
import java.io.FilterInputStream;
import java.util.zip.InflaterInputStream;
import java.io.InputStream;
import org.bouncycastle.operator.InputExpander;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputExpanderProvider;

public class ZlibExpanderProvider implements InputExpanderProvider
{
    private final long limit;
    
    public ZlibExpanderProvider() {
        this.limit = -1L;
    }
    
    public ZlibExpanderProvider(final long limit) {
        this.limit = limit;
    }
    
    public InputExpander get(final AlgorithmIdentifier algorithmIdentifier) {
        return new InputExpander() {
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier;
            }
            
            public InputStream getInputStream(final InputStream inputStream) {
                FilterInputStream filterInputStream = new InflaterInputStream(inputStream);
                if (ZlibExpanderProvider.this.limit >= 0L) {
                    filterInputStream = new LimitedInputStream(filterInputStream, ZlibExpanderProvider.this.limit);
                }
                return filterInputStream;
            }
        };
    }
    
    private static class LimitedInputStream extends FilterInputStream
    {
        private long remaining;
        
        public LimitedInputStream(final InputStream inputStream, final long remaining) {
            super(inputStream);
            this.remaining = remaining;
        }
        
        @Override
        public int read() throws IOException {
            if (this.remaining >= 0L) {
                final int read = super.in.read();
                if (read >= 0) {
                    final long remaining = this.remaining - 1L;
                    this.remaining = remaining;
                    if (remaining < 0L) {
                        throw new StreamOverflowException("expanded byte limit exceeded");
                    }
                }
                return read;
            }
            throw new StreamOverflowException("expanded byte limit exceeded");
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            if (n2 < 1) {
                return super.read(array, n, n2);
            }
            if (this.remaining < 1L) {
                this.read();
                return -1;
            }
            final int read = super.in.read(array, n, (this.remaining > n2) ? n2 : ((int)this.remaining));
            if (read > 0) {
                this.remaining -= read;
            }
            return read;
        }
    }
}
