package com.adventnet.persistence.json;

import com.zoho.framework.utils.crypto.CryptoUtil;

public class DefaultEnDecryptionValueHandler implements EnDecryptionValueHandler
{
    @Override
    public String encrypt(final String value) {
        return CryptoUtil.encrypt(value);
    }
    
    @Override
    public String decrypt(final String value) {
        return CryptoUtil.decrypt(value);
    }
}
