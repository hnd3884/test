package com.adventnet.tools.update.installer;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.io.ObjectInput;
import java.io.IOException;
import com.zoho.tools.AES256Util;
import java.io.ObjectOutput;
import java.io.Externalizable;

public class InstanceConfig implements Externalizable
{
    private static final long serialVersionUID = 1443274800971737285L;
    private static final char[] SYMBOLS;
    private String keyStorePassword;
    private String encryptionKey;
    
    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }
    
    public String getEncryptionKey() {
        return this.encryptionKey;
    }
    
    public void setEncryptionKey(final String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
    
    public void setKeyStorePassword(final String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.encryptionKey);
        out.writeObject(AES256Util.encrypt(this.keyStorePassword, this.encryptionKey));
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.encryptionKey = (String)in.readObject();
        this.keyStorePassword = AES256Util.decrypt((String)in.readObject(), this.encryptionKey);
    }
    
    public static InstanceConfig getNewInstance() {
        StringBuilder password = new StringBuilder();
        final SecureRandom random = new SecureRandom();
        for (int i = 0; i < 20; ++i) {
            password.append(InstanceConfig.SYMBOLS[random.nextInt(InstanceConfig.SYMBOLS.length)]);
        }
        final InstanceConfig instanceConfig = new InstanceConfig();
        instanceConfig.setEncryptionKey(password.toString());
        password = new StringBuilder();
        for (int j = 0; j < 20; ++j) {
            password.append(InstanceConfig.SYMBOLS[random.nextInt(InstanceConfig.SYMBOLS.length)]);
        }
        instanceConfig.setKeyStorePassword(password.toString());
        return instanceConfig;
    }
    
    public static InstanceConfig read(final String filePath) throws Exception {
        InstanceConfig instanceConfig;
        try (final FileInputStream fi = new FileInputStream(filePath);
             final ObjectInputStream oi = new ObjectInputStream(fi)) {
            instanceConfig = (InstanceConfig)oi.readObject();
        }
        return instanceConfig;
    }
    
    public static void write(final String filePath, final InstanceConfig instanceConfig) throws Exception {
        try (final FileOutputStream fOut = new FileOutputStream(filePath);
             final ObjectOutputStream out = new ObjectOutputStream(fOut)) {
            out.writeObject(instanceConfig);
        }
    }
    
    static {
        SYMBOLS = "ABCDEFGJKLMNPRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
    }
}
