package org.apache.catalina.tribes.group.interceptors;

public interface EncryptInterceptorMBean
{
    int getOptionFlag();
    
    void setOptionFlag(final int p0);
    
    void setEncryptionAlgorithm(final String p0);
    
    String getEncryptionAlgorithm();
    
    void setEncryptionKey(final byte[] p0);
    
    byte[] getEncryptionKey();
    
    void setProviderName(final String p0);
    
    String getProviderName();
}
