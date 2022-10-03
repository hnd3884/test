package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Arrays;
import java.io.FileInputStream;
import com.unboundid.util.Validator;
import java.io.File;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReadFromFilePasswordProvider extends PasswordProvider
{
    private static final long serialVersionUID = -3343425971796985100L;
    private final File passwordFile;
    
    public ReadFromFilePasswordProvider(final String passwordFile) {
        Validator.ensureNotNull(passwordFile);
        this.passwordFile = new File(passwordFile);
    }
    
    public ReadFromFilePasswordProvider(final File passwordFile) {
        Validator.ensureNotNull(passwordFile);
        this.passwordFile = passwordFile;
    }
    
    @Override
    public byte[] getPasswordBytes() throws LDAPException {
        byte[] pwBytes = null;
        try {
            final int fileLength = (int)this.passwordFile.length();
            pwBytes = new byte[fileLength];
            final FileInputStream inputStream = new FileInputStream(this.passwordFile);
            try {
                int bytesRead;
                for (int pos = 0; pos < fileLength; pos += bytesRead) {
                    bytesRead = inputStream.read(pwBytes, pos, pwBytes.length - pos);
                    if (bytesRead < 0) {
                        break;
                    }
                }
            }
            finally {
                inputStream.close();
            }
            for (int i = 0; i < pwBytes.length; ++i) {
                if (pwBytes[i] == 10 || pwBytes[i] == 13) {
                    final byte[] pwWithoutEOL = new byte[i];
                    System.arraycopy(pwBytes, 0, pwWithoutEOL, 0, i);
                    Arrays.fill(pwBytes, (byte)0);
                    pwBytes = pwWithoutEOL;
                    break;
                }
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (pwBytes != null) {
                Arrays.fill(pwBytes, (byte)0);
            }
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_FILE_PW_PROVIDER_ERROR_READING_PW.get(this.passwordFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
        }
        if (pwBytes.length == 0) {
            throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_FILE_PW_PROVIDER_EMPTY_PW.get(this.passwordFile.getAbsolutePath()));
        }
        return pwBytes;
    }
}
