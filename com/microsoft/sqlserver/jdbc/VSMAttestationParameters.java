package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;

class VSMAttestationParameters extends BaseAttestationRequest
{
    private static byte[] ENCLAVE_TYPE;
    
    VSMAttestationParameters() throws SQLServerException {
        this.enclaveChallenge = new byte[] { 0, 0, 0, 0 };
        this.initBcryptECDH();
    }
    
    @Override
    byte[] getBytes() throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(VSMAttestationParameters.ENCLAVE_TYPE);
        os.write(this.enclaveChallenge);
        os.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(104).array());
        os.write(VSMAttestationParameters.ECDH_MAGIC);
        os.write(this.x);
        os.write(this.y);
        return os.toByteArray();
    }
    
    static {
        VSMAttestationParameters.ENCLAVE_TYPE = new byte[] { 3, 0, 0, 0 };
    }
}
