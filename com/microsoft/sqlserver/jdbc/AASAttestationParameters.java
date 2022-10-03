package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.security.SecureRandom;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

class AASAttestationParameters extends BaseAttestationRequest
{
    private static final byte[] ENCLAVE_TYPE;
    private static byte[] NONCE_LENGTH;
    private byte[] nonce;
    
    AASAttestationParameters(final String attestationUrl) throws SQLServerException, IOException {
        this.nonce = new byte[256];
        final byte[] attestationUrlBytes = (attestationUrl + '\0').getBytes(StandardCharsets.UTF_16LE);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(attestationUrlBytes.length).array());
        os.write(attestationUrlBytes);
        os.write(AASAttestationParameters.NONCE_LENGTH);
        new SecureRandom().nextBytes(this.nonce);
        os.write(this.nonce);
        this.enclaveChallenge = os.toByteArray();
        this.initBcryptECDH();
    }
    
    @Override
    byte[] getBytes() throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(AASAttestationParameters.ENCLAVE_TYPE);
        os.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.enclaveChallenge.length).array());
        os.write(this.enclaveChallenge);
        os.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(104).array());
        os.write(AASAttestationParameters.ECDH_MAGIC);
        os.write(this.x);
        os.write(this.y);
        return os.toByteArray();
    }
    
    byte[] getNonce() {
        return this.nonce;
    }
    
    static {
        ENCLAVE_TYPE = new byte[] { 1, 0, 0, 0 };
        AASAttestationParameters.NONCE_LENGTH = new byte[] { 0, 1, 0, 0 };
    }
}
