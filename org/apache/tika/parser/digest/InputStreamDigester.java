package org.apache.tika.parser.digest;

import java.io.FileInputStream;
import java.io.File;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.BoundedInputStream;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.metadata.Metadata;
import java.security.Provider;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import org.apache.tika.parser.DigestingParser;

public class InputStreamDigester implements DigestingParser.Digester
{
    private final String algorithm;
    private final String algorithmKeyName;
    private final DigestingParser.Encoder encoder;
    private final int markLimit;
    
    public InputStreamDigester(final int markLimit, final String algorithm, final DigestingParser.Encoder encoder) {
        this(markLimit, algorithm, algorithm, encoder);
    }
    
    public InputStreamDigester(final int markLimit, final String algorithm, final String algorithmKeyName, final DigestingParser.Encoder encoder) {
        this.algorithm = algorithm;
        this.algorithmKeyName = algorithmKeyName;
        this.encoder = encoder;
        this.markLimit = markLimit;
        if (markLimit < 0) {
            throw new IllegalArgumentException("markLimit must be >= 0");
        }
    }
    
    private static MessageDigest updateDigest(final MessageDigest digest, final InputStream data) throws IOException {
        final byte[] buffer = new byte[1024];
        for (int read = data.read(buffer, 0, 1024); read > -1; read = data.read(buffer, 0, 1024)) {
            digest.update(buffer, 0, read);
        }
        return digest;
    }
    
    private MessageDigest newMessageDigest() {
        try {
            final Provider provider = this.getProvider();
            if (provider == null) {
                return MessageDigest.getInstance(this.algorithm);
            }
            return MessageDigest.getInstance(this.algorithm, provider);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    protected Provider getProvider() {
        return null;
    }
    
    @Override
    public void digest(final InputStream is, final Metadata metadata, final ParseContext parseContext) throws IOException {
        final TikaInputStream tis = TikaInputStream.cast(is);
        if (tis != null && tis.hasFile()) {
            long sz = -1L;
            if (tis.hasFile()) {
                sz = tis.getLength();
            }
            if (sz > this.markLimit) {
                this.digestFile(tis.getFile(), metadata);
                return;
            }
        }
        final BoundedInputStream bis = new BoundedInputStream(this.markLimit, is);
        boolean finishedStream = false;
        bis.mark(this.markLimit + 1);
        finishedStream = this.digestStream(bis, metadata);
        bis.reset();
        if (finishedStream) {
            return;
        }
        if (tis != null) {
            this.digestFile(tis.getFile(), metadata);
        }
        else {
            final TemporaryResources tmp = new TemporaryResources();
            try {
                final TikaInputStream tmpTikaInputStream = TikaInputStream.get(is, tmp);
                this.digestFile(tmpTikaInputStream.getFile(), metadata);
            }
            finally {
                try {
                    tmp.dispose();
                }
                catch (final TikaException e) {
                    throw new IOException(e);
                }
            }
        }
    }
    
    private String getMetadataKey() {
        return "X-TIKA:digest:" + this.algorithmKeyName;
    }
    
    private void digestFile(final File f, final Metadata m) throws IOException {
        try (final InputStream is = new FileInputStream(f)) {
            this.digestStream(is, m);
        }
    }
    
    private boolean digestStream(final InputStream is, final Metadata metadata) throws IOException {
        final MessageDigest messageDigest = this.newMessageDigest();
        updateDigest(messageDigest, is);
        final byte[] digestBytes = messageDigest.digest();
        if (is instanceof BoundedInputStream && ((BoundedInputStream)is).hasHitBound()) {
            return false;
        }
        metadata.set(this.getMetadataKey(), this.encoder.encode(digestBytes));
        return true;
    }
}
