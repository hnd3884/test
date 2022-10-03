package com.me.devicemanagement.onpremise.server.security;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.security.CryptoAPI;

public class DMCryptoImpl implements CryptoAPI
{
    private static int enc_type_poco;
    
    public String encrypt(final String plainText) {
        final String workingDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final String cipherText = WinAccessProvider.getInstance().encrypt(plainText, workingDir);
        final String decryptedText = this.decrypt(cipherText);
        if (!decryptedText.equals(plainText)) {
            SyMLogger.getSoMLogger().log(Level.SEVERE, "Encryption Failed.. Decrypted text obtained from Native, mismatches with the plainText..");
            SyMLogger.getSoMLogger().log(Level.INFO, "Cipher Text {0}", new Object[] { cipherText });
        }
        return cipherText;
    }
    
    public String decrypt(final String cipherText) {
        final String workingDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        return WinAccessProvider.getInstance().decrypt(cipherText, workingDir);
    }
    
    public String encrypt(final String plainText, final Integer crEncType) {
        final String workingDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        String cipherText = "--";
        if (crEncType == DMCryptoImpl.enc_type_poco) {
            cipherText = WinAccessProvider.getInstance().encryptaes(plainText, workingDir);
        }
        else {
            cipherText = WinAccessProvider.getInstance().encrypt(plainText, workingDir);
        }
        final String decryptedText = this.decrypt(cipherText, crEncType);
        if (!decryptedText.equals(plainText)) {
            SyMLogger.getSoMLogger().log(Level.SEVERE, "Encryption Failed.. Decrypted text obtained from Native, mismatches with the plainText..");
            SyMLogger.getSoMLogger().log(Level.INFO, "Cipher Text {0}", new Object[] { cipherText });
        }
        return cipherText;
    }
    
    public String decrypt(final String cipherText, final Integer crEncType) {
        final String workingDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        if (crEncType == DMCryptoImpl.enc_type_poco) {
            return WinAccessProvider.getInstance().decryptaes(cipherText, workingDir);
        }
        return WinAccessProvider.getInstance().decrypt(cipherText, workingDir);
    }
    
    public String encrypt(final String plainText, final String password, final String salt) {
        return WinAccessProvider.getInstance().getEncryptedString(plainText, password, salt);
    }
    
    public String decrypt(final String cipherText, final String password, final String salt) {
        return WinAccessProvider.getInstance().getDecryptedString(cipherText, password, salt);
    }
    
    static {
        DMCryptoImpl.enc_type_poco = 8;
    }
}
