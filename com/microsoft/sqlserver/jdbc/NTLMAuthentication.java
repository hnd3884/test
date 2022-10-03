package com.microsoft.sqlserver.jdbc;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import java.util.concurrent.ThreadLocalRandom;
import java.nio.charset.StandardCharsets;
import mssql.security.provider.MD4;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.text.MessageFormat;
import java.util.Arrays;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

final class NTLMAuthentication extends SSPIAuthentication
{
    private final Logger logger;
    private static final byte[] NTLM_HEADER_SIGNATURE;
    private static final int NTLM_MESSAGE_TYPE_NEGOTIATE = 1;
    private static final int NTLM_MESSAGE_TYPE_CHALLENGE = 2;
    private static final int NTLM_MESSAGE_TYPE_AUTHENTICATE = 3;
    private static final byte[] NTLM_CLIENT_CHALLENGE_RESPONSE_TYPE;
    private static final byte[] NTLM_CLIENT_CHALLENGE_RESERVED1;
    private static final byte[] NTLM_CLIENT_CHALLENGE_RESERVED2;
    private static final byte[] NTLM_CLIENT_CHALLENGE_RESERVED3;
    private static final byte[] NTLM_LMCHALLENAGERESPONSE;
    private static final byte[] NTLMSSP_VERSION;
    private static final long NTLMSSP_NEGOTIATE_UNICODE = 1L;
    private static final long NTLMSSP_REQUEST_TARGET = 4L;
    private static final long NTLMSSP_NEGOTIATE_OEM_DOMAIN_SUPPLIED = 4096L;
    private static final long NTLMSSP_NEGOTIATE_OEM_WORKSTATION_SUPPLIED = 8192L;
    private static final long NTLMSSP_NEGOTIATE_TARGET_INFO = 8388608L;
    private static final long NTLMSSP_NEGOTIATE_ALWAYS_SIGN = 32768L;
    private static final long NTLMSSP_NEGOTIATE_EXTENDED_SESSIONSECURITY = 524288L;
    private static final short NTLM_AVID_MSVAVEOL = 0;
    private static final short NTLM_AVID_MSVAVNBCOMPUTERNAME = 1;
    private static final short NTLM_AVID_MSVAVNBDOMAINNAME = 2;
    private static final short NTLM_AVID_MSVAVDNSCOMPUTERNAME = 3;
    private static final short NTLM_AVID_MSVAVDNSDOMAINNAME = 4;
    private static final short NTLM_AVID_MSVAVDNSTREENAME = 5;
    private static final short NTLM_AVID_MSVAVFLAGS = 6;
    private static final short NTLM_AVID_MSVAVTIMESTAMP = 7;
    private static final short NTLM_AVID_MSVAVSINGLEHOST = 8;
    private static final short NTLM_AVID_MSVAVTARGETNAME = 9;
    private static final int NTLM_AVID_LENGTH = 2;
    private static final int NTLM_AVLEN_LENGTH = 2;
    private static final int NTLM_AVFLAG_VALUE_MIC = 2;
    private static final int NTLM_MIC_LENGTH = 16;
    private static final int NTLM_AVID_MSVAVFLAGS_LEN = 4;
    private static final int NTLM_NEGOTIATE_PAYLOAD_OFFSET = 32;
    private static final int NTLM_AUTHENTICATE_PAYLOAD_OFFSET = 88;
    private static final int NTLM_CLIENT_NONCE_LENGTH = 8;
    private static final int NTLM_SERVER_CHALLENGE_LENGTH = 8;
    private static final int NTLM_TIMESTAMP_LENGTH = 8;
    private static final long WINDOWS_EPOCH_DIFF = 11644473600L;
    private NTLMContext context;
    
    NTLMAuthentication(final SQLServerConnection con, final String domainName, final String userName, final byte[] passwordHash, final String workstation) throws SQLServerException {
        this.logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.NTLMAuthentication");
        this.context = null;
        if (null == this.context) {
            this.context = new NTLMContext(con, domainName, userName, passwordHash, workstation);
        }
    }
    
    @Override
    byte[] generateClientContext(final byte[] inToken, final boolean[] done) throws SQLServerException {
        return this.initializeSecurityContext(inToken, done);
    }
    
    @Override
    void releaseClientContext() {
        this.context = null;
    }
    
    private void parseNtlmChallenge(final byte[] inToken) throws SQLServerException {
        final ByteBuffer token = ByteBuffer.wrap(inToken).order(ByteOrder.LITTLE_ENDIAN);
        final byte[] signature = new byte[NTLMAuthentication.NTLM_HEADER_SIGNATURE.length];
        token.get(signature);
        if (!Arrays.equals(signature, NTLMAuthentication.NTLM_HEADER_SIGNATURE)) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ntlmSignatureError"));
            final Object[] msgArgs = { signature };
            throw new SQLServerException(form.format(msgArgs), (Throwable)null);
        }
        final int messageType = token.getInt();
        if (messageType != 2) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_ntlmMessageTypeError"));
            final Object[] msgArgs2 = { messageType };
            throw new SQLServerException(form2.format(msgArgs2), (Throwable)null);
        }
        final int targetNameLen = token.getShort();
        token.getShort();
        token.getInt();
        token.getInt();
        token.get(this.context.serverChallenge);
        token.getLong();
        final int targetInfoLen = token.getShort();
        token.getShort();
        token.getInt();
        token.getLong();
        final byte[] targetName = new byte[targetNameLen];
        token.get(targetName);
        this.context.targetInfo = new byte[targetInfoLen];
        token.get(this.context.targetInfo);
        if (0 == this.context.targetInfo.length) {
            throw new SQLServerException(SQLServerException.getErrString("R_ntlmNoTargetInfo"), (Throwable)null);
        }
        final ByteBuffer targetInfoBuf = ByteBuffer.wrap(this.context.targetInfo).order(ByteOrder.LITTLE_ENDIAN);
        boolean done = false;
        final int i = 0;
        while (i < this.context.targetInfo.length && !done) {
            final int id = targetInfoBuf.getShort();
            final byte[] value = new byte[targetInfoBuf.getShort()];
            targetInfoBuf.get(value);
            switch (id) {
                case 7: {
                    if (value.length > 0) {
                        this.context.timestamp = new byte[8];
                        System.arraycopy(value, 0, this.context.timestamp, 0, 8);
                        break;
                    }
                    break;
                }
                case 0: {
                    done = true;
                    break;
                }
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 8:
                case 9: {
                    break;
                }
                default: {
                    final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_ntlmUnknownValue"));
                    final Object[] msgArgs3 = { value };
                    throw new SQLServerException(form3.format(msgArgs3), (Throwable)null);
                }
            }
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.toString() + " NTLM Challenge Message target info: AvId " + id);
            }
        }
        if (null == this.context.timestamp || 0 >= this.context.timestamp.length) {
            if (this.logger.isLoggable(Level.WARNING)) {
                this.logger.warning(this.toString() + " NTLM Challenge Message target info error: Missing timestamp.");
            }
        }
        else {
            this.context.challengeMsg = new byte[inToken.length];
            System.arraycopy(inToken, 0, this.context.challengeMsg, 0, inToken.length);
        }
    }
    
    private byte[] initializeSecurityContext(final byte[] inToken, final boolean[] done) throws SQLServerException {
        if (null == inToken || 0 == inToken.length) {
            return this.generateNtlmNegotiate();
        }
        this.parseNtlmChallenge(inToken);
        done[0] = true;
        return this.generateNtlmAuthenticate();
    }
    
    private byte[] generateClientChallengeBlob(final byte[] clientNonce) {
        final ByteBuffer time = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        time.putLong(TimeUnit.SECONDS.toNanos(Instant.now().getEpochSecond() + 11644473600L) / 100L);
        final byte[] currentTime = time.array();
        final ByteBuffer token = ByteBuffer.allocate(NTLMAuthentication.NTLM_CLIENT_CHALLENGE_RESPONSE_TYPE.length + NTLMAuthentication.NTLM_CLIENT_CHALLENGE_RESERVED1.length + NTLMAuthentication.NTLM_CLIENT_CHALLENGE_RESERVED2.length + currentTime.length + 8 + NTLMAuthentication.NTLM_CLIENT_CHALLENGE_RESERVED3.length + this.context.targetInfo.length + 2 + 2 + 4 + 2 + 2 + this.context.spnUbytes.length).order(ByteOrder.LITTLE_ENDIAN);
        token.put(NTLMAuthentication.NTLM_CLIENT_CHALLENGE_RESPONSE_TYPE);
        token.put(NTLMAuthentication.NTLM_CLIENT_CHALLENGE_RESERVED1);
        token.put(NTLMAuthentication.NTLM_CLIENT_CHALLENGE_RESERVED2);
        token.put(currentTime, 0, 8);
        token.put(clientNonce, 0, 8);
        token.put(NTLMAuthentication.NTLM_CLIENT_CHALLENGE_RESERVED3);
        if (null == this.context.timestamp || 0 >= this.context.timestamp.length) {
            token.put(this.context.targetInfo, 0, this.context.targetInfo.length);
            if (this.logger.isLoggable(Level.WARNING)) {
                this.logger.warning(this.toString() + " MsvAvTimestamp not recieved from SQL Server in Challenge Message. MIC field will not be set.");
            }
        }
        else {
            token.put(this.context.targetInfo, 0, this.context.targetInfo.length - 2 - 2);
            token.putShort((short)6);
            token.putShort((short)4);
            token.putInt(2);
        }
        token.putShort((short)9);
        token.putShort((short)this.context.spnUbytes.length);
        token.put(this.context.spnUbytes, 0, this.context.spnUbytes.length);
        token.putShort((short)0);
        token.putShort((short)0);
        return token.array();
    }
    
    private byte[] hmacMD5(final byte[] key, final byte[] data) throws InvalidKeyException {
        final SecretKeySpec keySpec = new SecretKeySpec(key, "HmacMD5");
        this.context.mac.init(keySpec);
        return this.context.mac.doFinal(data);
    }
    
    private static byte[] md4(final byte[] str) {
        final MD4 md = new MD4();
        md.reset();
        md.update(str);
        return md.digest();
    }
    
    private static byte[] unicode(final String str) {
        return (byte[])((null != str) ? str.getBytes(StandardCharsets.UTF_16LE) : null);
    }
    
    private byte[] concat(final byte[] arr1, final byte[] arr2) {
        if (null == arr1 || null == arr2) {
            return null;
        }
        final byte[] temp = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, temp, 0, arr1.length);
        System.arraycopy(arr2, 0, temp, arr1.length, arr2.length);
        return temp;
    }
    
    private int getByteArrayLength(final byte[] arr) {
        return (null == arr) ? 0 : arr.length;
    }
    
    private byte[] ntowfv2() throws InvalidKeyException {
        return this.hmacMD5(this.context.passwordHash, (null != this.context.upperUserName) ? unicode(this.context.upperUserName + this.context.domainName) : unicode(this.context.domainName));
    }
    
    private byte[] computeResponse(final byte[] responseKeyNT) throws InvalidKeyException {
        final byte[] clientNonce = new byte[8];
        ThreadLocalRandom.current().nextBytes(clientNonce);
        final byte[] temp = this.generateClientChallengeBlob(clientNonce);
        final byte[] ntProofStr = this.hmacMD5(responseKeyNT, this.concat(this.context.serverChallenge, temp));
        this.context.sessionBaseKey = this.hmacMD5(responseKeyNT, ntProofStr);
        return this.concat(ntProofStr, temp);
    }
    
    private byte[] getNtChallengeResp() throws InvalidKeyException {
        final byte[] responseKeyNT = this.ntowfv2();
        return this.computeResponse(responseKeyNT);
    }
    
    private byte[] generateNtlmAuthenticate() throws SQLServerException {
        final int domainNameLen = this.getByteArrayLength(this.context.domainUbytes);
        final int userNameLen = this.getByteArrayLength(this.context.userNameUbytes);
        final byte[] workstationBytes = unicode(this.context.workstation);
        final int workstationLen = this.getByteArrayLength(workstationBytes);
        byte[] msg = null;
        try {
            final byte[] ntChallengeResp = this.getNtChallengeResp();
            final int ntChallengeLen = this.getByteArrayLength(ntChallengeResp);
            final ByteBuffer token = ByteBuffer.allocate(88 + NTLMAuthentication.NTLM_LMCHALLENAGERESPONSE.length + ntChallengeLen + domainNameLen + userNameLen + workstationLen).order(ByteOrder.LITTLE_ENDIAN);
            token.put(NTLMAuthentication.NTLM_HEADER_SIGNATURE, 0, NTLMAuthentication.NTLM_HEADER_SIGNATURE.length);
            token.putInt(3);
            int offset = 88;
            token.putShort((short)0);
            token.putShort((short)0);
            token.putInt(offset);
            offset += NTLMAuthentication.NTLM_LMCHALLENAGERESPONSE.length;
            token.putShort((short)ntChallengeLen);
            token.putShort((short)ntChallengeLen);
            token.putInt(offset);
            offset += ntChallengeLen;
            token.putShort((short)domainNameLen);
            token.putShort((short)domainNameLen);
            token.putInt(offset);
            offset += domainNameLen;
            token.putShort((short)userNameLen);
            token.putShort((short)userNameLen);
            token.putInt(offset);
            offset += userNameLen;
            token.putShort((short)workstationLen);
            token.putShort((short)workstationLen);
            token.putInt(offset);
            offset += workstationLen;
            token.putShort((short)0);
            token.putShort((short)0);
            token.putInt(offset);
            token.putInt((int)this.context.negotiateFlags);
            token.put(NTLMAuthentication.NTLMSSP_VERSION, 0, NTLMAuthentication.NTLMSSP_VERSION.length);
            byte[] mic = new byte[16];
            final int micPosition = token.position();
            token.put(mic, 0, 16);
            token.put(NTLMAuthentication.NTLM_LMCHALLENAGERESPONSE, 0, NTLMAuthentication.NTLM_LMCHALLENAGERESPONSE.length);
            token.put(ntChallengeResp, 0, ntChallengeLen);
            token.put(this.context.domainUbytes, 0, domainNameLen);
            token.put(this.context.userNameUbytes, 0, userNameLen);
            token.put(workstationBytes, 0, workstationLen);
            msg = token.array();
            if (null != this.context.timestamp && 0 < this.context.timestamp.length) {
                final SecretKeySpec keySpec = new SecretKeySpec(this.context.sessionBaseKey, "HmacMD5");
                this.context.mac.init(keySpec);
                this.context.mac.update(this.context.negotiateMsg);
                this.context.mac.update(this.context.challengeMsg);
                mic = this.context.mac.doFinal(msg);
                System.arraycopy(mic, 0, msg, micPosition, 16);
            }
        }
        catch (final InvalidKeyException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ntlmAuthenticateError"));
            final Object[] msgArgs = { e.getMessage() };
            throw new SQLServerException(form.format(msgArgs), e);
        }
        return msg;
    }
    
    private byte[] generateNtlmNegotiate() {
        final int domainNameLen = this.getByteArrayLength(this.context.domainUbytes);
        final int workstationLen = this.getByteArrayLength(this.context.workstation.getBytes());
        ByteBuffer token = null;
        token = ByteBuffer.allocate(32 + domainNameLen + workstationLen).order(ByteOrder.LITTLE_ENDIAN);
        token.put(NTLMAuthentication.NTLM_HEADER_SIGNATURE, 0, NTLMAuthentication.NTLM_HEADER_SIGNATURE.length);
        token.putInt(1);
        this.context.negotiateFlags = 8957957L;
        token.putInt((int)this.context.negotiateFlags);
        int offset = 32;
        token.putShort((short)domainNameLen);
        token.putShort((short)domainNameLen);
        token.putInt(offset);
        offset += domainNameLen;
        token.putShort((short)workstationLen);
        token.putShort((short)workstationLen);
        token.putInt(offset);
        offset += workstationLen;
        token.put(this.context.domainUbytes, 0, domainNameLen);
        token.put(this.context.workstation.getBytes(), 0, workstationLen);
        final byte[] msg = token.array();
        this.context.negotiateMsg = new byte[msg.length];
        System.arraycopy(msg, 0, this.context.negotiateMsg, 0, msg.length);
        return msg;
    }
    
    public static byte[] getNtlmPasswordHash(final String password) throws SQLServerException {
        if (null == password) {
            throw new SQLServerException(SQLServerException.getErrString("R_NtlmNoUserPasswordDomain"), (Throwable)null);
        }
        return md4(unicode(password));
    }
    
    static {
        NTLM_HEADER_SIGNATURE = new byte[] { 78, 84, 76, 77, 83, 83, 80, 0 };
        NTLM_CLIENT_CHALLENGE_RESPONSE_TYPE = new byte[] { 1, 1 };
        NTLM_CLIENT_CHALLENGE_RESERVED1 = new byte[] { 0, 0 };
        NTLM_CLIENT_CHALLENGE_RESERVED2 = new byte[] { 0, 0, 0, 0 };
        NTLM_CLIENT_CHALLENGE_RESERVED3 = new byte[] { 0, 0, 0, 0 };
        NTLM_LMCHALLENAGERESPONSE = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        NTLMSSP_VERSION = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    }
    
    private class NTLMContext
    {
        private final String domainName;
        private final byte[] domainUbytes;
        private final String upperUserName;
        private final byte[] userNameUbytes;
        private final byte[] passwordHash;
        private String workstation;
        private final byte[] spnUbytes;
        private Mac mac;
        private long negotiateFlags;
        private byte[] sessionBaseKey;
        private byte[] timestamp;
        private byte[] targetInfo;
        private byte[] serverChallenge;
        private byte[] negotiateMsg;
        private byte[] challengeMsg;
        
        NTLMContext(final SQLServerConnection con, final String domainName, final String userName, final byte[] passwordHash, final String workstation) throws SQLServerException {
            this.mac = null;
            this.negotiateFlags = 0L;
            this.sessionBaseKey = null;
            this.timestamp = null;
            this.targetInfo = null;
            this.serverChallenge = new byte[8];
            this.negotiateMsg = null;
            this.challengeMsg = null;
            this.domainName = domainName.toUpperCase();
            this.domainUbytes = unicode(this.domainName);
            this.userNameUbytes = (byte[])((null != userName) ? unicode(userName) : null);
            this.upperUserName = ((null != userName) ? userName.toUpperCase() : null);
            this.passwordHash = passwordHash;
            this.workstation = workstation;
            final String spn = (null != con) ? NTLMAuthentication.this.getSpn(con) : null;
            this.spnUbytes = (byte[])((null != spn) ? unicode(spn) : null);
            if (NTLMAuthentication.this.logger.isLoggable(Level.FINEST)) {
                NTLMAuthentication.this.logger.finest(this.toString() + " SPN detected: " + spn);
            }
            try {
                this.mac = Mac.getInstance("HmacMD5");
            }
            catch (final NoSuchAlgorithmException e) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ntlmHmacMD5Error"));
                final Object[] msgArgs = { domainName, e.getMessage() };
                throw new SQLServerException(form.format(msgArgs), e);
            }
        }
    }
}
