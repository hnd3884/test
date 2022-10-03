package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.OutputLengthException;
import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import java.util.Hashtable;
import org.bouncycastle.util.Memoable;

public class SkeinEngine implements Memoable
{
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private static final int PARAM_TYPE_KEY = 0;
    private static final int PARAM_TYPE_CONFIG = 4;
    private static final int PARAM_TYPE_MESSAGE = 48;
    private static final int PARAM_TYPE_OUTPUT = 63;
    private static final Hashtable INITIAL_STATES;
    final ThreefishEngine threefish;
    private final int outputSizeBytes;
    long[] chain;
    private long[] initialState;
    private byte[] key;
    private Parameter[] preMessageParameters;
    private Parameter[] postMessageParameters;
    private final UBI ubi;
    private final byte[] singleByte;
    
    private static void initialState(final int n, final int n2, final long[] array) {
        SkeinEngine.INITIAL_STATES.put(variantIdentifier(n / 8, n2 / 8), array);
    }
    
    private static Integer variantIdentifier(final int n, final int n2) {
        return new Integer(n2 << 16 | n);
    }
    
    public SkeinEngine(final int n, final int n2) {
        this.singleByte = new byte[1];
        if (n2 % 8 != 0) {
            throw new IllegalArgumentException("Output size must be a multiple of 8 bits. :" + n2);
        }
        this.outputSizeBytes = n2 / 8;
        this.threefish = new ThreefishEngine(n);
        this.ubi = new UBI(this.threefish.getBlockSize());
    }
    
    public SkeinEngine(final SkeinEngine skeinEngine) {
        this(skeinEngine.getBlockSize() * 8, skeinEngine.getOutputSize() * 8);
        this.copyIn(skeinEngine);
    }
    
    private void copyIn(final SkeinEngine skeinEngine) {
        this.ubi.reset(skeinEngine.ubi);
        this.chain = Arrays.clone(skeinEngine.chain, this.chain);
        this.initialState = Arrays.clone(skeinEngine.initialState, this.initialState);
        this.key = Arrays.clone(skeinEngine.key, this.key);
        this.preMessageParameters = clone(skeinEngine.preMessageParameters, this.preMessageParameters);
        this.postMessageParameters = clone(skeinEngine.postMessageParameters, this.postMessageParameters);
    }
    
    private static Parameter[] clone(final Parameter[] array, Parameter[] array2) {
        if (array == null) {
            return null;
        }
        if (array2 == null || array2.length != array.length) {
            array2 = new Parameter[array.length];
        }
        System.arraycopy(array, 0, array2, 0, array2.length);
        return array2;
    }
    
    public Memoable copy() {
        return new SkeinEngine(this);
    }
    
    public void reset(final Memoable memoable) {
        final SkeinEngine skeinEngine = (SkeinEngine)memoable;
        if (this.getBlockSize() != skeinEngine.getBlockSize() || this.outputSizeBytes != skeinEngine.outputSizeBytes) {
            throw new IllegalArgumentException("Incompatible parameters in provided SkeinEngine.");
        }
        this.copyIn(skeinEngine);
    }
    
    public int getOutputSize() {
        return this.outputSizeBytes;
    }
    
    public int getBlockSize() {
        return this.threefish.getBlockSize();
    }
    
    public void init(final SkeinParameters skeinParameters) {
        this.chain = null;
        this.key = null;
        this.preMessageParameters = null;
        this.postMessageParameters = null;
        if (skeinParameters != null) {
            if (skeinParameters.getKey().length < 16) {
                throw new IllegalArgumentException("Skein key must be at least 128 bits.");
            }
            this.initParams(skeinParameters.getParameters());
        }
        this.createInitialState();
        this.ubiInit(48);
    }
    
    private void initParams(final Hashtable hashtable) {
        final Enumeration keys = hashtable.keys();
        final Vector vector = new Vector();
        final Vector vector2 = new Vector();
        while (keys.hasMoreElements()) {
            final Integer n = (Integer)keys.nextElement();
            final byte[] key = hashtable.get(n);
            if (n == 0) {
                this.key = key;
            }
            else if (n < 48) {
                vector.addElement(new Parameter(n, key));
            }
            else {
                vector2.addElement(new Parameter(n, key));
            }
        }
        vector.copyInto(this.preMessageParameters = new Parameter[vector.size()]);
        sort(this.preMessageParameters);
        vector2.copyInto(this.postMessageParameters = new Parameter[vector2.size()]);
        sort(this.postMessageParameters);
    }
    
    private static void sort(final Parameter[] array) {
        if (array == null) {
            return;
        }
        for (int i = 1; i < array.length; ++i) {
            Parameter parameter;
            int n;
            for (parameter = array[i], n = i; n > 0 && parameter.getType() < array[n - 1].getType(); --n) {
                array[n] = array[n - 1];
            }
            array[n] = parameter;
        }
    }
    
    private void createInitialState() {
        final long[] array = SkeinEngine.INITIAL_STATES.get(variantIdentifier(this.getBlockSize(), this.getOutputSize()));
        if (this.key == null && array != null) {
            this.chain = Arrays.clone(array);
        }
        else {
            this.chain = new long[this.getBlockSize() / 8];
            if (this.key != null) {
                this.ubiComplete(0, this.key);
            }
            this.ubiComplete(4, new Configuration(this.outputSizeBytes * 8).getBytes());
        }
        if (this.preMessageParameters != null) {
            for (int i = 0; i < this.preMessageParameters.length; ++i) {
                final Parameter parameter = this.preMessageParameters[i];
                this.ubiComplete(parameter.getType(), parameter.getValue());
            }
        }
        this.initialState = Arrays.clone(this.chain);
    }
    
    public void reset() {
        System.arraycopy(this.initialState, 0, this.chain, 0, this.chain.length);
        this.ubiInit(48);
    }
    
    private void ubiComplete(final int n, final byte[] array) {
        this.ubiInit(n);
        this.ubi.update(array, 0, array.length, this.chain);
        this.ubiFinal();
    }
    
    private void ubiInit(final int n) {
        this.ubi.reset(n);
    }
    
    private void ubiFinal() {
        this.ubi.doFinal(this.chain);
    }
    
    private void checkInitialised() {
        if (this.ubi == null) {
            throw new IllegalArgumentException("Skein engine is not initialised.");
        }
    }
    
    public void update(final byte b) {
        this.singleByte[0] = b;
        this.update(this.singleByte, 0, 1);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.checkInitialised();
        this.ubi.update(array, n, n2, this.chain);
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.checkInitialised();
        if (array.length < n + this.outputSizeBytes) {
            throw new OutputLengthException("Output buffer is too short to hold output");
        }
        this.ubiFinal();
        if (this.postMessageParameters != null) {
            for (int i = 0; i < this.postMessageParameters.length; ++i) {
                final Parameter parameter = this.postMessageParameters[i];
                this.ubiComplete(parameter.getType(), parameter.getValue());
            }
        }
        final int blockSize = this.getBlockSize();
        for (int n2 = (this.outputSizeBytes + blockSize - 1) / blockSize, j = 0; j < n2; ++j) {
            this.output(j, array, n + j * blockSize, Math.min(blockSize, this.outputSizeBytes - j * blockSize));
        }
        this.reset();
        return this.outputSizeBytes;
    }
    
    private void output(final long n, final byte[] array, final int n2, final int n3) {
        final byte[] array2 = new byte[8];
        ThreefishEngine.wordToBytes(n, array2, 0);
        final long[] array3 = new long[this.chain.length];
        this.ubiInit(63);
        this.ubi.update(array2, 0, array2.length, array3);
        this.ubi.doFinal(array3);
        for (int n4 = (n3 + 8 - 1) / 8, i = 0; i < n4; ++i) {
            final int min = Math.min(8, n3 - i * 8);
            if (min == 8) {
                ThreefishEngine.wordToBytes(array3[i], array, n2 + i * 8);
            }
            else {
                ThreefishEngine.wordToBytes(array3[i], array2, 0);
                System.arraycopy(array2, 0, array, n2 + i * 8, min);
            }
        }
    }
    
    static {
        INITIAL_STATES = new Hashtable();
        initialState(256, 128, new long[] { -2228972824489528736L, -8629553674646093540L, 1155188648486244218L, -3677226592081559102L });
        initialState(256, 160, new long[] { 1450197650740764312L, 3081844928540042640L, -3136097061834271170L, 3301952811952417661L });
        initialState(256, 224, new long[] { -4176654842910610933L, -8688192972455077604L, -7364642305011795836L, 4056579644589979102L });
        initialState(256, 256, new long[] { -243853671043386295L, 3443677322885453875L, -5531612722399640561L, 7662005193972177513L });
        initialState(512, 128, new long[] { -6288014694233956526L, 2204638249859346602L, 3502419045458743507L, -4829063503441264548L, 983504137758028059L, 1880512238245786339L, -6715892782214108542L, 7602827311880509485L });
        initialState(512, 160, new long[] { 2934123928682216849L, -4399710721982728305L, 1684584802963255058L, 5744138295201861711L, 2444857010922934358L, -2807833639722848072L, -5121587834665610502L, 118355523173251694L });
        initialState(512, 224, new long[] { -3688341020067007964L, -3772225436291745297L, -8300862168937575580L, 4146387520469897396L, 1106145742801415120L, 7455425944880474941L, -7351063101234211863L, -7048981346965512457L });
        initialState(512, 384, new long[] { -6631894876634615969L, -5692838220127733084L, -7099962856338682626L, -2911352911530754598L, 2000907093792408677L, 9140007292425499655L, 6093301768906360022L, 2769176472213098488L });
        initialState(512, 512, new long[] { 5261240102383538638L, 978932832955457283L, -8083517948103779378L, -7339365279355032399L, 6752626034097301424L, -1531723821829733388L, -7417126464950782685L, -5901786942805128141L });
    }
    
    private static class Configuration
    {
        private byte[] bytes;
        
        public Configuration(final long n) {
            (this.bytes = new byte[32])[0] = 83;
            this.bytes[1] = 72;
            this.bytes[2] = 65;
            this.bytes[3] = 51;
            this.bytes[4] = 1;
            this.bytes[5] = 0;
            ThreefishEngine.wordToBytes(n, this.bytes, 8);
        }
        
        public byte[] getBytes() {
            return this.bytes;
        }
    }
    
    public static class Parameter
    {
        private int type;
        private byte[] value;
        
        public Parameter(final int type, final byte[] value) {
            this.type = type;
            this.value = value;
        }
        
        public int getType() {
            return this.type;
        }
        
        public byte[] getValue() {
            return this.value;
        }
    }
    
    private class UBI
    {
        private final UbiTweak tweak;
        private byte[] currentBlock;
        private int currentOffset;
        private long[] message;
        
        public UBI(final int n) {
            this.tweak = new UbiTweak();
            this.currentBlock = new byte[n];
            this.message = new long[this.currentBlock.length / 8];
        }
        
        public void reset(final UBI ubi) {
            this.currentBlock = Arrays.clone(ubi.currentBlock, this.currentBlock);
            this.currentOffset = ubi.currentOffset;
            this.message = Arrays.clone(ubi.message, this.message);
            this.tweak.reset(ubi.tweak);
        }
        
        public void reset(final int type) {
            this.tweak.reset();
            this.tweak.setType(type);
            this.currentOffset = 0;
        }
        
        public void update(final byte[] array, final int n, final int i, final long[] array2) {
            int n2 = 0;
            while (i > n2) {
                if (this.currentOffset == this.currentBlock.length) {
                    this.processBlock(array2);
                    this.tweak.setFirst(false);
                    this.currentOffset = 0;
                }
                final int min = Math.min(i - n2, this.currentBlock.length - this.currentOffset);
                System.arraycopy(array, n + n2, this.currentBlock, this.currentOffset, min);
                n2 += min;
                this.currentOffset += min;
                this.tweak.advancePosition(min);
            }
        }
        
        private void processBlock(final long[] array) {
            SkeinEngine.this.threefish.init(true, SkeinEngine.this.chain, this.tweak.getWords());
            for (int i = 0; i < this.message.length; ++i) {
                this.message[i] = ThreefishEngine.bytesToWord(this.currentBlock, i * 8);
            }
            SkeinEngine.this.threefish.processBlock(this.message, array);
            for (int j = 0; j < array.length; ++j) {
                final int n = j;
                array[n] ^= this.message[j];
            }
        }
        
        public void doFinal(final long[] array) {
            for (int i = this.currentOffset; i < this.currentBlock.length; ++i) {
                this.currentBlock[i] = 0;
            }
            this.tweak.setFinal(true);
            this.processBlock(array);
        }
    }
    
    private static class UbiTweak
    {
        private static final long LOW_RANGE = 9223372034707292160L;
        private static final long T1_FINAL = Long.MIN_VALUE;
        private static final long T1_FIRST = 4611686018427387904L;
        private long[] tweak;
        private boolean extendedPosition;
        
        public UbiTweak() {
            this.tweak = new long[2];
            this.reset();
        }
        
        public void reset(final UbiTweak ubiTweak) {
            this.tweak = Arrays.clone(ubiTweak.tweak, this.tweak);
            this.extendedPosition = ubiTweak.extendedPosition;
        }
        
        public void reset() {
            this.tweak[0] = 0L;
            this.tweak[1] = 0L;
            this.extendedPosition = false;
            this.setFirst(true);
        }
        
        public void setType(final int n) {
            this.tweak[1] = ((this.tweak[1] & 0xFFFFFFC000000000L) | ((long)n & 0x3FL) << 56);
        }
        
        public int getType() {
            return (int)(this.tweak[1] >>> 56 & 0x3FL);
        }
        
        public void setFirst(final boolean b) {
            if (b) {
                final long[] tweak = this.tweak;
                final int n = 1;
                tweak[n] |= 0x4000000000000000L;
            }
            else {
                final long[] tweak2 = this.tweak;
                final int n2 = 1;
                tweak2[n2] &= 0xBFFFFFFFFFFFFFFFL;
            }
        }
        
        public boolean isFirst() {
            return (this.tweak[1] & 0x4000000000000000L) != 0x0L;
        }
        
        public void setFinal(final boolean b) {
            if (b) {
                final long[] tweak = this.tweak;
                final int n = 1;
                tweak[n] |= Long.MIN_VALUE;
            }
            else {
                final long[] tweak2 = this.tweak;
                final int n2 = 1;
                tweak2[n2] &= Long.MAX_VALUE;
            }
        }
        
        public boolean isFinal() {
            return (this.tweak[1] & Long.MIN_VALUE) != 0x0L;
        }
        
        public void advancePosition(final int n) {
            if (this.extendedPosition) {
                final long[] array = { this.tweak[0] & 0xFFFFFFFFL, this.tweak[0] >>> 32 & 0xFFFFFFFFL, this.tweak[1] & 0xFFFFFFFFL };
                long n2 = n;
                for (int i = 0; i < array.length; ++i) {
                    final long n3 = n2 + array[i];
                    array[i] = n3;
                    n2 = n3 >>> 32;
                }
                this.tweak[0] = ((array[1] & 0xFFFFFFFFL) << 32 | (array[0] & 0xFFFFFFFFL));
                this.tweak[1] = ((this.tweak[1] & 0xFFFFFFFF00000000L) | (array[2] & 0xFFFFFFFFL));
            }
            else {
                final long n4 = this.tweak[0] + n;
                this.tweak[0] = n4;
                if (n4 > 9223372034707292160L) {
                    this.extendedPosition = true;
                }
            }
        }
        
        public long[] getWords() {
            return this.tweak;
        }
        
        @Override
        public String toString() {
            return this.getType() + " first: " + this.isFirst() + ", final: " + this.isFinal();
        }
    }
}
