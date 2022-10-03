package com.sshtools.publickey;

import java.io.IOException;
import com.maverick.ssh.components.SshKeyPair;

public interface SshPrivateKeyFile
{
    boolean isPassphraseProtected();
    
    SshKeyPair toKeyPair(final String p0) throws IOException, InvalidPassphraseException;
    
    boolean supportsPassphraseChange();
    
    String getType();
    
    void changePassphrase(final String p0, final String p1) throws IOException, InvalidPassphraseException;
    
    byte[] getFormattedKey() throws IOException;
}
