package com.sshtools.sftp;

import com.maverick.sftp.SftpFile;
import com.maverick.sftp.SftpStatusException;
import com.maverick.ssh.SshException;
import java.io.File;

public class NoRegExpMatching implements RegularExpressionMatching
{
    public String[] matchFileNamesWithPattern(final File[] array, final String s) throws SshException, SftpStatusException {
        return new String[] { array[0].getName() };
    }
    
    public SftpFile[] matchFilesWithPattern(final SftpFile[] array, final String s) throws SftpStatusException, SshException {
        return array;
    }
}
