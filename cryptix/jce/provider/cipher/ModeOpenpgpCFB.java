package cryptix.jce.provider.cipher;

class ModeOpenpgpCFB extends ModeCFB
{
    private final long extraCrankCount;
    
    protected boolean needCrank() {
        if (super.byteCount > this.extraCrankCount) {
            return (super.byteCount - 2L) % super.CIPHER_BLOCK_SIZE == 0L;
        }
        return super.needCrank() || super.byteCount == this.extraCrankCount;
    }
    
    ModeOpenpgpCFB(final BlockCipher cipher) {
        super(cipher);
        this.extraCrankCount = super.CIPHER_BLOCK_SIZE + 2;
    }
}
