package com.lowagie.text.pdf.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

public class AESCipher
{
    private final PaddedBufferedBlockCipher bp;
    
    public AESCipher(final boolean forEncryption, final byte[] key, final byte[] iv) {
        final BlockCipher aes = (BlockCipher)new AESFastEngine();
        final BlockCipher cbc = (BlockCipher)new CBCBlockCipher(aes);
        this.bp = new PaddedBufferedBlockCipher(cbc);
        final KeyParameter kp = new KeyParameter(key);
        final ParametersWithIV piv = new ParametersWithIV((CipherParameters)kp, iv);
        this.bp.init(forEncryption, (CipherParameters)piv);
    }
    
    public byte[] update(final byte[] inp, final int inpOff, final int inpLen) {
        int neededLen = this.bp.getUpdateOutputSize(inpLen);
        byte[] outp = null;
        if (neededLen > 0) {
            outp = new byte[neededLen];
        }
        else {
            neededLen = 0;
        }
        this.bp.processBytes(inp, inpOff, inpLen, outp, 0);
        return outp;
    }
    
    public byte[] doFinal() {
        final int neededLen = this.bp.getOutputSize(0);
        final byte[] outp = new byte[neededLen];
        int n = 0;
        try {
            n = this.bp.doFinal(outp, 0);
        }
        catch (final Exception ex) {
            return outp;
        }
        if (n != outp.length) {
            final byte[] outp2 = new byte[n];
            System.arraycopy(outp, 0, outp2, 0, n);
            return outp2;
        }
        return outp;
    }
}
