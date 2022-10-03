package com.sshtools.publickey;

import java.io.IOException;
import com.maverick.ssh.components.SshPublicKey;

public interface SshPublicKeyFile
{
    SshPublicKey toPublicKey() throws IOException;
    
    String getComment();
    
    byte[] getFormattedKey() throws IOException;
}
