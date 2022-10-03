package com.unboundid.ldap.sdk;

import javax.crypto.Mac;
import com.unboundid.util.Base64;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class SCRAMClientFinalMessage implements Serializable
{
    private static final byte[] CLIENT_KEY_INPUT_BYTES;
    private static final byte[] ONE_BYTES;
    private static final long serialVersionUID = -5228385127923425294L;
    private final byte[] authMessageBytes;
    private final byte[] saltedPassword;
    private final SCRAMBindRequest bindRequest;
    private final SCRAMClientFirstMessage clientFirstMessage;
    private final SCRAMServerFirstMessage serverFirstMessage;
    private final String clientFinalMessage;
    private final String clientProofBase64;
    
    SCRAMClientFinalMessage(final SCRAMBindRequest bindRequest, final SCRAMClientFirstMessage clientFirstMessage, final SCRAMServerFirstMessage serverFirstMessage) throws LDAPBindException {
        this.bindRequest = bindRequest;
        this.clientFirstMessage = clientFirstMessage;
        this.serverFirstMessage = serverFirstMessage;
        this.saltedPassword = computeSaltedPassword(bindRequest, serverFirstMessage);
        final byte[] clientKey = bindRequest.mac(this.saltedPassword, SCRAMClientFinalMessage.CLIENT_KEY_INPUT_BYTES);
        final byte[] storedKey = bindRequest.digest(clientKey);
        final String clientFinalMessageWithoutProof = "c=" + clientFirstMessage.getGS2HeaderBase64() + ",r=" + serverFirstMessage.getCombinedNonce();
        final String authMessage = clientFirstMessage.getClientFirstMessageBare() + ',' + serverFirstMessage.getServerFirstMessage() + ',' + clientFinalMessageWithoutProof;
        this.authMessageBytes = StaticUtils.getBytes(authMessage);
        final byte[] clientSignature = bindRequest.mac(storedKey, this.authMessageBytes);
        final byte[] clientProof = new byte[clientKey.length];
        for (int i = 0; i < clientProof.length; ++i) {
            clientProof[i] = (byte)(clientKey[i] ^ clientSignature[i]);
        }
        this.clientProofBase64 = Base64.encode(clientProof);
        this.clientFinalMessage = clientFinalMessageWithoutProof + ",p=" + this.clientProofBase64;
    }
    
    private static byte[] computeSaltedPassword(final SCRAMBindRequest bindRequest, final SCRAMServerFirstMessage serverFirstMessage) throws LDAPBindException {
        final Mac mac = bindRequest.getMac(bindRequest.getPasswordBytes());
        final byte[] salt = serverFirstMessage.getSalt();
        byte[] dataToMAC = new byte[salt.length + SCRAMClientFinalMessage.ONE_BYTES.length];
        System.arraycopy(salt, 0, dataToMAC, 0, salt.length);
        System.arraycopy(SCRAMClientFinalMessage.ONE_BYTES, 0, dataToMAC, salt.length, SCRAMClientFinalMessage.ONE_BYTES.length);
        byte[] xorBytes = null;
        for (int i = 0; i < serverFirstMessage.getIterationCount(); ++i) {
            final byte[] macResult = mac.doFinal(dataToMAC);
            if (i == 0) {
                xorBytes = macResult;
            }
            else {
                for (int j = 0; j < macResult.length; ++j) {
                    final byte[] array = xorBytes;
                    final int n = j;
                    array[n] ^= macResult[j];
                }
            }
            dataToMAC = macResult;
        }
        return xorBytes;
    }
    
    SCRAMBindRequest getBindRequest() {
        return this.bindRequest;
    }
    
    SCRAMClientFirstMessage getClientFirstMessage() {
        return this.clientFirstMessage;
    }
    
    SCRAMServerFirstMessage getServerFirstMessage() {
        return this.serverFirstMessage;
    }
    
    byte[] getSaltedPassword() {
        return this.saltedPassword;
    }
    
    byte[] getAuthMessageBytes() {
        return this.authMessageBytes;
    }
    
    String getClientProofBase64() {
        return this.clientProofBase64;
    }
    
    String getClientFinalMessage() {
        return this.clientFinalMessage;
    }
    
    @Override
    public String toString() {
        return this.clientFinalMessage;
    }
    
    static {
        CLIENT_KEY_INPUT_BYTES = StaticUtils.getBytes("Client Key");
        ONE_BYTES = new byte[] { 0, 0, 0, 1 };
    }
}
