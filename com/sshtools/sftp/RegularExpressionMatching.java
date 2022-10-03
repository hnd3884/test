package com.sshtools.sftp;

import java.io.File;
import com.maverick.ssh.SshException;
import com.maverick.sftp.SftpStatusException;
import com.maverick.sftp.SftpFile;

public interface RegularExpressionMatching
{
    SftpFile[] matchFilesWithPattern(final SftpFile[] p0, final String p1) throws SftpStatusException, SshException;
    
    String[] matchFileNamesWithPattern(final File[] p0, final String p1) throws SftpStatusException, SshException;
}
