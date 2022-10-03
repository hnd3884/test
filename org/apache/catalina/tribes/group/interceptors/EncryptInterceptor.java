package org.apache.catalina.tribes.group.interceptors;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.juli.logging.LogFactory;
import java.security.NoSuchProviderException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.ChannelInterceptor;
import java.security.GeneralSecurityException;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;

public class EncryptInterceptor extends ChannelInterceptorBase implements EncryptInterceptorMBean
{
    private static final Log log;
    protected static final StringManager sm;
    private static final String DEFAULT_ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
    private String providerName;
    private String encryptionAlgorithm;
    private byte[] encryptionKeyBytes;
    private String encryptionKeyString;
    private BaseEncryptionManager encryptionManager;
    private static final int[] DEC;
    
    public EncryptInterceptor() {
        this.encryptionAlgorithm = "AES/CBC/PKCS5Padding";
    }
    
    @Override
    public void start(final int svc) throws ChannelException {
        this.validateChannelChain();
        if (0x2 == (svc & 0x2)) {
            try {
                this.encryptionManager = createEncryptionManager(this.getEncryptionAlgorithm(), this.getEncryptionKeyInternal(), this.getProviderName());
            }
            catch (final GeneralSecurityException gse) {
                throw new ChannelException(EncryptInterceptor.sm.getString("encryptInterceptor.init.failed"), gse);
            }
        }
        super.start(svc);
    }
    
    private void validateChannelChain() throws ChannelException {
        for (ChannelInterceptor interceptor = this.getPrevious(); null != interceptor; interceptor = interceptor.getPrevious()) {
            if (interceptor instanceof TcpFailureDetector) {
                throw new ChannelConfigException(EncryptInterceptor.sm.getString("encryptInterceptor.tcpFailureDetector.ordering"));
            }
        }
    }
    
    @Override
    public void stop(final int svc) throws ChannelException {
        if (0x2 == (svc & 0x2)) {
            this.encryptionManager.shutdown();
        }
        super.stop(svc);
    }
    
    @Override
    public void sendMessage(final Member[] destination, final ChannelMessage msg, final InterceptorPayload payload) throws ChannelException {
        try {
            final byte[] data = msg.getMessage().getBytes();
            final byte[][] bytes = this.encryptionManager.encrypt(data);
            final XByteBuffer xbb = msg.getMessage();
            xbb.clear();
            xbb.append(bytes[0], 0, bytes[0].length);
            xbb.append(bytes[1], 0, bytes[1].length);
            super.sendMessage(destination, msg, payload);
        }
        catch (final GeneralSecurityException gse) {
            EncryptInterceptor.log.error((Object)EncryptInterceptor.sm.getString("encryptInterceptor.encrypt.failed"));
            throw new ChannelException(gse);
        }
    }
    
    @Override
    public void messageReceived(final ChannelMessage msg) {
        try {
            byte[] data = msg.getMessage().getBytes();
            data = this.encryptionManager.decrypt(data);
            final XByteBuffer xbb = msg.getMessage();
            xbb.clear();
            xbb.append(data, 0, data.length);
            super.messageReceived(msg);
        }
        catch (final GeneralSecurityException gse) {
            EncryptInterceptor.log.error((Object)EncryptInterceptor.sm.getString("encryptInterceptor.decrypt.failed"), (Throwable)gse);
        }
    }
    
    @Override
    public void setEncryptionAlgorithm(final String algorithm) {
        if (null == this.getEncryptionAlgorithm()) {
            throw new IllegalStateException(EncryptInterceptor.sm.getString("encryptInterceptor.algorithm.required"));
        }
        int pos = algorithm.indexOf(47);
        if (pos < 0) {
            throw new IllegalArgumentException(EncryptInterceptor.sm.getString("encryptInterceptor.algorithm.required"));
        }
        pos = algorithm.indexOf(47, pos + 1);
        if (pos < 0) {
            throw new IllegalArgumentException(EncryptInterceptor.sm.getString("encryptInterceptor.algorithm.required"));
        }
        this.encryptionAlgorithm = algorithm;
    }
    
    @Override
    public String getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }
    
    @Override
    public void setEncryptionKey(final byte[] key) {
        if (null == key) {
            this.encryptionKeyBytes = null;
        }
        else {
            this.encryptionKeyBytes = key.clone();
        }
    }
    
    public void setEncryptionKey(final String keyBytes) {
        this.encryptionKeyString = keyBytes;
        if (null == keyBytes) {
            this.setEncryptionKey((byte[])null);
        }
        else {
            this.setEncryptionKey(fromHexString(keyBytes.trim()));
        }
    }
    
    @Override
    public byte[] getEncryptionKey() {
        byte[] key = this.getEncryptionKeyInternal();
        if (null != key) {
            key = key.clone();
        }
        return key;
    }
    
    private byte[] getEncryptionKeyInternal() {
        return this.encryptionKeyBytes;
    }
    
    public String getEncryptionKeyString() {
        return this.encryptionKeyString;
    }
    
    public void setEncryptionKeyString(final String encryptionKeyString) {
        this.setEncryptionKey(encryptionKeyString);
    }
    
    @Override
    public void setProviderName(final String provider) {
        this.providerName = provider;
    }
    
    @Override
    public String getProviderName() {
        return this.providerName;
    }
    
    private static int getDec(final int index) {
        try {
            return EncryptInterceptor.DEC[index - 48];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return -1;
        }
    }
    
    private static byte[] fromHexString(final String input) {
        if (input == null) {
            return null;
        }
        if ((input.length() & 0x1) == 0x1) {
            throw new IllegalArgumentException(EncryptInterceptor.sm.getString("hexUtils.fromHex.oddDigits"));
        }
        final char[] inputChars = input.toCharArray();
        final byte[] result = new byte[input.length() >> 1];
        for (int i = 0; i < result.length; ++i) {
            final int upperNibble = getDec(inputChars[2 * i]);
            final int lowerNibble = getDec(inputChars[2 * i + 1]);
            if (upperNibble < 0 || lowerNibble < 0) {
                throw new IllegalArgumentException(EncryptInterceptor.sm.getString("hexUtils.fromHex.nonHex"));
            }
            result[i] = (byte)((upperNibble << 4) + lowerNibble);
        }
        return result;
    }
    
    private static BaseEncryptionManager createEncryptionManager(final String algorithm, final byte[] encryptionKey, final String providerName) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        if (null == encryptionKey) {
            throw new IllegalStateException(EncryptInterceptor.sm.getString("encryptInterceptor.key.required"));
        }
        final int pos = algorithm.indexOf(47);
        String algorithmName;
        String algorithmMode;
        if (pos >= 0) {
            algorithmName = algorithm.substring(0, pos);
            final int pos2 = algorithm.indexOf(47, pos + 1);
            if (pos2 >= 0) {
                algorithmMode = algorithm.substring(pos + 1, pos2);
            }
            else {
                algorithmMode = "CBC";
            }
        }
        else {
            algorithmName = algorithm;
            algorithmMode = "CBC";
        }
        if ("GCM".equalsIgnoreCase(algorithmMode)) {
            return new GCMEncryptionManager(algorithm, new SecretKeySpec(encryptionKey, algorithmName), providerName);
        }
        if ("CBC".equalsIgnoreCase(algorithmMode) || "OFB".equalsIgnoreCase(algorithmMode) || "CFB".equalsIgnoreCase(algorithmMode)) {
            return new BaseEncryptionManager(algorithm, new SecretKeySpec(encryptionKey, algorithmName), providerName);
        }
        throw new IllegalArgumentException(EncryptInterceptor.sm.getString("encryptInterceptor.algorithm.unsupported-mode", algorithmMode));
    }
    
    static {
        log = LogFactory.getLog((Class)EncryptInterceptor.class);
        sm = StringManager.getManager(EncryptInterceptor.class);
        DEC = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15 };
    }
    
    private static class BaseEncryptionManager
    {
        private final String algorithm;
        private final int blockSize;
        private final String providerName;
        private final SecretKeySpec secretKey;
        private final ConcurrentLinkedQueue<Cipher> cipherPool;
        private final ConcurrentLinkedQueue<SecureRandom> randomPool;
        
        public BaseEncryptionManager(final String algorithm, final SecretKeySpec secretKey, final String providerName) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
            this.algorithm = algorithm;
            this.providerName = providerName;
            this.secretKey = secretKey;
            this.cipherPool = new ConcurrentLinkedQueue<Cipher>();
            final Cipher cipher = this.createCipher();
            this.blockSize = cipher.getBlockSize();
            this.cipherPool.offer(cipher);
            this.randomPool = new ConcurrentLinkedQueue<SecureRandom>();
        }
        
        public void shutdown() {
            this.cipherPool.clear();
            this.randomPool.clear();
        }
        
        private String getAlgorithm() {
            return this.algorithm;
        }
        
        private SecretKeySpec getSecretKey() {
            return this.secretKey;
        }
        
        protected int getIVSize() {
            return this.blockSize;
        }
        
        private String getProviderName() {
            return this.providerName;
        }
        
        private Cipher createCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
            final String providerName = this.getProviderName();
            if (null == providerName) {
                return Cipher.getInstance(this.getAlgorithm());
            }
            return Cipher.getInstance(this.getAlgorithm(), providerName);
        }
        
        private Cipher getCipher() throws GeneralSecurityException {
            Cipher cipher = this.cipherPool.poll();
            if (null == cipher) {
                cipher = this.createCipher();
            }
            return cipher;
        }
        
        private void returnCipher(final Cipher cipher) {
            this.cipherPool.offer(cipher);
        }
        
        private SecureRandom getRandom() {
            SecureRandom random = this.randomPool.poll();
            if (null == random) {
                random = new SecureRandom();
            }
            return random;
        }
        
        private void returnRandom(final SecureRandom random) {
            this.randomPool.offer(random);
        }
        
        private byte[][] encrypt(final byte[] bytes) throws GeneralSecurityException {
            Cipher cipher = null;
            final byte[] iv = this.generateIVBytes();
            try {
                cipher = this.getCipher();
                cipher.init(1, this.getSecretKey(), this.generateIV(iv, 0, this.getIVSize()));
                final byte[][] data = { iv, cipher.doFinal(bytes) };
                return data;
            }
            finally {
                if (null != cipher) {
                    this.returnCipher(cipher);
                }
            }
        }
        
        private byte[] decrypt(final byte[] bytes) throws GeneralSecurityException {
            Cipher cipher = null;
            final int ivSize = this.getIVSize();
            final AlgorithmParameterSpec IV = this.generateIV(bytes, 0, ivSize);
            try {
                cipher = this.getCipher();
                cipher.init(2, this.getSecretKey(), IV);
                return cipher.doFinal(bytes, ivSize, bytes.length - ivSize);
            }
            finally {
                if (null != cipher) {
                    this.returnCipher(cipher);
                }
            }
        }
        
        protected byte[] generateIVBytes() {
            final byte[] ivBytes = new byte[this.getIVSize()];
            SecureRandom random = null;
            try {
                random = this.getRandom();
                random.nextBytes(ivBytes);
                return ivBytes;
            }
            finally {
                if (null != random) {
                    this.returnRandom(random);
                }
            }
        }
        
        protected AlgorithmParameterSpec generateIV(final byte[] ivBytes, final int offset, final int length) {
            return new IvParameterSpec(ivBytes, offset, length);
        }
    }
    
    private static class GCMEncryptionManager extends BaseEncryptionManager
    {
        public GCMEncryptionManager(final String algorithm, final SecretKeySpec secretKey, final String providerName) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
            super(algorithm, secretKey, providerName);
        }
        
        @Override
        protected int getIVSize() {
            return 12;
        }
        
        @Override
        protected AlgorithmParameterSpec generateIV(final byte[] bytes, final int offset, final int length) {
            return new GCMParameterSpec(128, bytes, offset, length);
        }
    }
    
    static class ChannelConfigException extends ChannelException
    {
        private static final long serialVersionUID = 1L;
        
        public ChannelConfigException(final String message) {
            super(message);
        }
    }
}
