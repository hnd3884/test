package cryptix.jce.provider.md;

public abstract class SHA512Base extends PaddingMD
{
    private static final int BLOCK_SIZE = 128;
    private static final long[] K;
    private final long[] context;
    private final long[] buffer;
    
    protected abstract void loadInitialValues(final long[] p0);
    
    protected abstract void generateDigest(final long[] p0, final byte[] p1, final int p2);
    
    protected void coreDigest(final byte[] buf, final int off) {
        this.generateDigest(this.context, buf, off);
    }
    
    protected void coreReset() {
        this.loadInitialValues(this.context);
    }
    
    protected void coreUpdate(final byte[] block, int offset) {
        final long[] W = this.buffer;
        for (int i = 0; i < 16; ++i) {
            W[i] = ((long)block[offset++] << 56 | ((long)block[offset++] & 0xFFL) << 48 | ((long)block[offset++] & 0xFFL) << 40 | ((long)block[offset++] & 0xFFL) << 32 | ((long)block[offset++] & 0xFFL) << 24 | ((long)block[offset++] & 0xFFL) << 16 | ((long)block[offset++] & 0xFFL) << 8 | ((long)block[offset++] & 0xFFL));
        }
        for (int i = 16; i < 80; ++i) {
            W[i] = this.sig1(W[i - 2]) + W[i - 7] + this.sig0(W[i - 15]) + W[i - 16];
        }
        long a = this.context[0];
        long b = this.context[1];
        long c = this.context[2];
        long d = this.context[3];
        long e = this.context[4];
        long f = this.context[5];
        long g = this.context[6];
        long h = this.context[7];
        for (int j = 0; j < 80; ++j) {
            final long T1 = h + this.Sig1(e) + this.Ch(e, f, g) + SHA512Base.K[j] + W[j];
            final long T2 = this.Sig0(a) + this.Maj(a, b, c);
            h = g;
            g = f;
            f = e;
            e = d + T1;
            d = c;
            c = b;
            b = a;
            a = T1 + T2;
        }
        final long[] context = this.context;
        final int n = 0;
        context[n] += a;
        final long[] context2 = this.context;
        final int n2 = 1;
        context2[n2] += b;
        final long[] context3 = this.context;
        final int n3 = 2;
        context3[n3] += c;
        final long[] context4 = this.context;
        final int n4 = 3;
        context4[n4] += d;
        final long[] context5 = this.context;
        final int n5 = 4;
        context5[n5] += e;
        final long[] context6 = this.context;
        final int n6 = 5;
        context6[n6] += f;
        final long[] context7 = this.context;
        final int n7 = 6;
        context7[n7] += g;
        final long[] context8 = this.context;
        final int n8 = 7;
        context8[n8] += h;
    }
    
    private final long Ch(final long x, final long y, final long z) {
        return (x & y) ^ ((x ^ -1L) & z);
    }
    
    private final long Maj(final long x, final long y, final long z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }
    
    private final long Sig0(final long x) {
        return this.S(28, x) ^ this.S(34, x) ^ this.S(39, x);
    }
    
    private final long Sig1(final long x) {
        return this.S(14, x) ^ this.S(18, x) ^ this.S(41, x);
    }
    
    private final long sig0(final long x) {
        return this.S(1, x) ^ this.S(8, x) ^ this.R(7, x);
    }
    
    private final long sig1(final long x) {
        return this.S(19, x) ^ this.S(61, x) ^ this.R(6, x);
    }
    
    private final long R(final int off, final long x) {
        return x >>> off;
    }
    
    private final long S(final int off, final long x) {
        return x >>> off | x << 64 - off;
    }
    
    public SHA512Base(final int hashSize) {
        super(128, hashSize, 1);
        this.context = new long[8];
        this.buffer = new long[80];
        this.coreReset();
    }
    
    protected SHA512Base(final SHA512Base src) {
        super(src);
        this.context = src.context.clone();
        this.buffer = src.buffer.clone();
    }
    
    static {
        K = new long[] { 4794697086780616226L, 8158064640168781261L, -5349999486874862801L, -1606136188198331460L, 4131703408338449720L, 6480981068601479193L, -7908458776815382629L, -6116909921290321640L, -2880145864133508542L, 1334009975649890238L, 2608012711638119052L, 6128411473006802146L, 8268148722764581231L, -9160688886553864527L, -7215885187991268811L, -4495734319001033068L, -1973867731355612462L, -1171420211273849373L, 1135362057144423861L, 2597628984639134821L, 3308224258029322869L, 5365058923640841347L, 6679025012923562964L, 8573033837759648693L, -7476448914759557205L, -6327057829258317296L, -5763719355590565569L, -4658551843659510044L, -4116276920077217854L, -3051310485924567259L, 489312712824947311L, 1452737877330783856L, 2861767655752347644L, 3322285676063803686L, 5560940570517711597L, 5996557281743188959L, 7280758554555802590L, 8532644243296465576L, -9096487096722542874L, -7894198246740708037L, -6719396339535248540L, -6333637450476146687L, -4446306890439682159L, -4076793802049405392L, -3345356375505022440L, -2983346525034927856L, -860691631967231958L, 1182934255886127544L, 1847814050463011016L, 2177327727835720531L, 2830643537854262169L, 3796741975233480872L, 4115178125766777443L, 5681478168544905931L, 6601373596472566643L, 7507060721942968483L, 8399075790359081724L, 8693463985226723168L, -8878714635349349518L, -8302665154208450068L, -8016688836872298968L, -6606660893046293015L, -4685533653050689259L, -4147400797238176981L, -3880063495543823972L, -3348786107499101689L, -1523767162380948706L, -757361751448694408L, 500013540394364858L, 748580250866718886L, 1242879168328830382L, 1977374033974150939L, 2944078676154940804L, 3659926193048069267L, 4368137639120453308L, 4836135668995329356L, 5532061633213252278L, 6448918945643986474L, 6902733635092675308L, 7801388544844847127L };
    }
}
