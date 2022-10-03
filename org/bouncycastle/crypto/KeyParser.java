package org.bouncycastle.crypto;

import java.io.IOException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.InputStream;

public interface KeyParser
{
    AsymmetricKeyParameter readKey(final InputStream p0) throws IOException;
}
