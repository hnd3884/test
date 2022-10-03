package com.me.devicemanagement.framework.addons.crypto;

import com.zoho.framework.utils.crypto.CryptoUtil;
import com.me.devicemanagement.framework.server.security.CryptoAPI;

public class AddOnCryptoAPI implements CryptoAPI
{
    private static AddOnCryptoAPI addOnCryptoAPI;
    
    public static AddOnCryptoAPI getInstance() {
        if (AddOnCryptoAPI.addOnCryptoAPI == null) {
            AddOnCryptoAPI.addOnCryptoAPI = new AddOnCryptoAPI();
        }
        return AddOnCryptoAPI.addOnCryptoAPI;
    }
    
    public String encrypt(final String plainText) {
        return CryptoUtil.encrypt(plainText);
    }
    
    public String decrypt(final String cipherText) {
        return CryptoUtil.decrypt(cipherText);
    }
    
    public String encrypt(final String plainText, final Integer crEncType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String decrypt(final String cipherText, final Integer crEncType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String encrypt(final String plainText, final String password, final String salt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String decrypt(final String cipherText, final String password, final String salt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    static {
        AddOnCryptoAPI.addOnCryptoAPI = null;
    }
}
