package sun.security.provider;

import java.util.Objects;
import java.util.Arrays;

abstract class SHA5 extends DigestBase
{
    private static final int ITERATION = 80;
    private static final long[] ROUND_CONSTS;
    private long[] W;
    private long[] state;
    private final long[] initialHashes;
    
    SHA5(final String s, final int n, final long[] initialHashes) {
        super(s, n, 128);
        this.initialHashes = initialHashes;
        this.state = new long[8];
        this.W = new long[80];
        this.resetHashes();
    }
    
    @Override
    final void implReset() {
        this.resetHashes();
        Arrays.fill(this.W, 0L);
    }
    
    private void resetHashes() {
        System.arraycopy(this.initialHashes, 0, this.state, 0, this.state.length);
    }
    
    @Override
    final void implDigest(final byte[] array, final int n) {
        final long n2 = this.bytesProcessed << 3;
        final int n3 = (int)this.bytesProcessed & 0x7F;
        this.engineUpdate(SHA5.padding, 0, ((n3 < 112) ? (112 - n3) : (240 - n3)) + 8);
        ByteArrayAccess.i2bBig4((int)(n2 >>> 32), this.buffer, 120);
        ByteArrayAccess.i2bBig4((int)n2, this.buffer, 124);
        this.implCompress(this.buffer, 0);
        final int engineGetDigestLength = this.engineGetDigestLength();
        if (engineGetDigestLength == 28) {
            ByteArrayAccess.l2bBig(this.state, 0, array, n, 24);
            ByteArrayAccess.i2bBig4((int)(this.state[3] >> 32), array, n + 24);
        }
        else {
            ByteArrayAccess.l2bBig(this.state, 0, array, n, engineGetDigestLength);
        }
    }
    
    private static long lf_ch(final long n, final long n2, final long n3) {
        return (n & n2) ^ (~n & n3);
    }
    
    private static long lf_maj(final long n, final long n2, final long n3) {
        return (n & n2) ^ (n & n3) ^ (n2 & n3);
    }
    
    private static long lf_R(final long n, final int n2) {
        return n >>> n2;
    }
    
    private static long lf_S(final long n, final int n2) {
        return n >>> n2 | n << 64 - n2;
    }
    
    private static long lf_sigma0(final long n) {
        return lf_S(n, 28) ^ lf_S(n, 34) ^ lf_S(n, 39);
    }
    
    private static long lf_sigma1(final long n) {
        return lf_S(n, 14) ^ lf_S(n, 18) ^ lf_S(n, 41);
    }
    
    private static long lf_delta0(final long n) {
        return lf_S(n, 1) ^ lf_S(n, 8) ^ lf_R(n, 7);
    }
    
    private static long lf_delta1(final long n) {
        return lf_S(n, 19) ^ lf_S(n, 61) ^ lf_R(n, 6);
    }
    
    @Override
    final void implCompress(final byte[] array, final int n) {
        this.implCompressCheck(array, n);
        this.implCompress0(array, n);
    }
    
    private void implCompressCheck(final byte[] array, final int n) {
        Objects.requireNonNull(array);
        ByteArrayAccess.b2lBig128(array, n, this.W);
    }
    
    private final void implCompress0(final byte[] array, final int n) {
        for (int i = 16; i < 80; ++i) {
            this.W[i] = lf_delta1(this.W[i - 2]) + this.W[i - 7] + lf_delta0(this.W[i - 15]) + this.W[i - 16];
        }
        long n2 = this.state[0];
        long n3 = this.state[1];
        long n4 = this.state[2];
        long n5 = this.state[3];
        long n6 = this.state[4];
        long n7 = this.state[5];
        long n8 = this.state[6];
        long n9 = this.state[7];
        for (int j = 0; j < 80; ++j) {
            final long n10 = n9 + lf_sigma1(n6) + lf_ch(n6, n7, n8) + SHA5.ROUND_CONSTS[j] + this.W[j];
            final long n11 = lf_sigma0(n2) + lf_maj(n2, n3, n4);
            n9 = n8;
            n8 = n7;
            n7 = n6;
            n6 = n5 + n10;
            n5 = n4;
            n4 = n3;
            n3 = n2;
            n2 = n10 + n11;
        }
        final long[] state = this.state;
        final int n12 = 0;
        state[n12] += n2;
        final long[] state2 = this.state;
        final int n13 = 1;
        state2[n13] += n3;
        final long[] state3 = this.state;
        final int n14 = 2;
        state3[n14] += n4;
        final long[] state4 = this.state;
        final int n15 = 3;
        state4[n15] += n5;
        final long[] state5 = this.state;
        final int n16 = 4;
        state5[n16] += n6;
        final long[] state6 = this.state;
        final int n17 = 5;
        state6[n17] += n7;
        final long[] state7 = this.state;
        final int n18 = 6;
        state7[n18] += n8;
        final long[] state8 = this.state;
        final int n19 = 7;
        state8[n19] += n9;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final SHA5 sha5 = (SHA5)super.clone();
        sha5.state = sha5.state.clone();
        sha5.W = new long[80];
        return sha5;
    }
    
    static {
        ROUND_CONSTS = new long[] { 4794697086780616226L, 8158064640168781261L, -5349999486874862801L, -1606136188198331460L, 4131703408338449720L, 6480981068601479193L, -7908458776815382629L, -6116909921290321640L, -2880145864133508542L, 1334009975649890238L, 2608012711638119052L, 6128411473006802146L, 8268148722764581231L, -9160688886553864527L, -7215885187991268811L, -4495734319001033068L, -1973867731355612462L, -1171420211273849373L, 1135362057144423861L, 2597628984639134821L, 3308224258029322869L, 5365058923640841347L, 6679025012923562964L, 8573033837759648693L, -7476448914759557205L, -6327057829258317296L, -5763719355590565569L, -4658551843659510044L, -4116276920077217854L, -3051310485924567259L, 489312712824947311L, 1452737877330783856L, 2861767655752347644L, 3322285676063803686L, 5560940570517711597L, 5996557281743188959L, 7280758554555802590L, 8532644243296465576L, -9096487096722542874L, -7894198246740708037L, -6719396339535248540L, -6333637450476146687L, -4446306890439682159L, -4076793802049405392L, -3345356375505022440L, -2983346525034927856L, -860691631967231958L, 1182934255886127544L, 1847814050463011016L, 2177327727835720531L, 2830643537854262169L, 3796741975233480872L, 4115178125766777443L, 5681478168544905931L, 6601373596472566643L, 7507060721942968483L, 8399075790359081724L, 8693463985226723168L, -8878714635349349518L, -8302665154208450068L, -8016688836872298968L, -6606660893046293015L, -4685533653050689259L, -4147400797238176981L, -3880063495543823972L, -3348786107499101689L, -1523767162380948706L, -757361751448694408L, 500013540394364858L, 748580250866718886L, 1242879168328830382L, 1977374033974150939L, 2944078676154940804L, 3659926193048069267L, 4368137639120453308L, 4836135668995329356L, 5532061633213252278L, 6448918945643986474L, 6902733635092675308L, 7801388544844847127L };
    }
    
    public static final class SHA512 extends SHA5
    {
        private static final long[] INITIAL_HASHES;
        
        public SHA512() {
            super("SHA-512", 64, SHA512.INITIAL_HASHES);
        }
        
        static {
            INITIAL_HASHES = new long[] { 7640891576956012808L, -4942790177534073029L, 4354685564936845355L, -6534734903238641935L, 5840696475078001361L, -7276294671716946913L, 2270897969802886507L, 6620516959819538809L };
        }
    }
    
    public static final class SHA384 extends SHA5
    {
        private static final long[] INITIAL_HASHES;
        
        public SHA384() {
            super("SHA-384", 48, SHA384.INITIAL_HASHES);
        }
        
        static {
            INITIAL_HASHES = new long[] { -3766243637369397544L, 7105036623409894663L, -7973340178411365097L, 1526699215303891257L, 7436329637833083697L, -8163818279084223215L, -2662702644619276377L, 5167115440072839076L };
        }
    }
    
    public static final class SHA512_224 extends SHA5
    {
        private static final long[] INITIAL_HASHES;
        
        public SHA512_224() {
            super("SHA-512/224", 28, SHA512_224.INITIAL_HASHES);
        }
        
        static {
            INITIAL_HASHES = new long[] { -8341449602262348382L, 8350123849800275158L, 2160240930085379202L, 7466358040605728719L, 1111592415079452072L, 8638871050018654530L, 4583966954114332360L, 1230299281376055969L };
        }
    }
    
    public static final class SHA512_256 extends SHA5
    {
        private static final long[] INITIAL_HASHES;
        
        public SHA512_256() {
            super("SHA-512/256", 32, SHA512_256.INITIAL_HASHES);
        }
        
        static {
            INITIAL_HASHES = new long[] { 2463787394917988140L, -6965556091613846334L, 2563595384472711505L, -7622211418569250115L, -7626776825740460061L, -4729309413028513390L, 3098927326965381290L, 1060366662362279074L };
        }
    }
}
