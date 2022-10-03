package com.me.devicemanagement.framework.server.security;

import com.zoho.framework.utils.crypto.CryptoUtil;

public class CommonCryptoImpl implements CryptoAPI
{
    @Override
    public String encrypt(final String plainText) {
        return CryptoUtil.encrypt(plainText);
    }
    
    @Override
    public String decrypt(final String cipherText) {
        return CryptoUtil.decrypt(cipherText);
    }
    
    @Override
    public String encrypt(final String plainText, final Integer crEncType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String decrypt(final String cipherText, final Integer crEncType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String encrypt(final String plainText, final String password, final String salt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String decrypt(final String cipherText, final String password, final String salt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
