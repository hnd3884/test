package sun.font;

public final class ScriptRun
{
    private char[] text;
    private int textStart;
    private int textLimit;
    private int scriptStart;
    private int scriptLimit;
    private int scriptCode;
    private int[] stack;
    private int parenSP;
    static final int SURROGATE_START = 65536;
    static final int LEAD_START = 55296;
    static final int LEAD_LIMIT = 56320;
    static final int TAIL_START = 56320;
    static final int TAIL_LIMIT = 57344;
    static final int LEAD_SURROGATE_SHIFT = 10;
    static final int SURROGATE_OFFSET = -56613888;
    static final int DONE = -1;
    private static int[] pairedChars;
    private static final int pairedCharPower;
    private static final int pairedCharExtra;
    
    public ScriptRun() {
    }
    
    public ScriptRun(final char[] array, final int n, final int n2) {
        this.init(array, n, n2);
    }
    
    public void init(final char[] text, final int textStart, final int n) {
        if (text == null || textStart < 0 || n < 0 || n > text.length - textStart) {
            throw new IllegalArgumentException();
        }
        this.text = text;
        this.textStart = textStart;
        this.textLimit = textStart + n;
        this.scriptStart = this.textStart;
        this.scriptLimit = this.textStart;
        this.scriptCode = -1;
        this.parenSP = 0;
    }
    
    public final int getScriptStart() {
        return this.scriptStart;
    }
    
    public final int getScriptLimit() {
        return this.scriptLimit;
    }
    
    public final int getScriptCode() {
        return this.scriptCode;
    }
    
    public final boolean next() {
        int i = this.parenSP;
        if (this.scriptLimit >= this.textLimit) {
            return false;
        }
        this.scriptCode = 0;
        this.scriptStart = this.scriptLimit;
        int nextCodePoint;
        while ((nextCodePoint = this.nextCodePoint()) != -1) {
            int script = ScriptRunData.getScript(nextCodePoint);
            final int n = (script == 0) ? getPairIndex(nextCodePoint) : -1;
            if (n >= 0) {
                if ((n & 0x1) == 0x0) {
                    if (this.stack == null) {
                        this.stack = new int[32];
                    }
                    else if (this.parenSP == this.stack.length) {
                        final int[] stack = new int[this.stack.length + 32];
                        System.arraycopy(this.stack, 0, stack, 0, this.stack.length);
                        this.stack = stack;
                    }
                    this.stack[this.parenSP++] = n;
                    this.stack[this.parenSP++] = this.scriptCode;
                }
                else if (this.parenSP > 0) {
                    final int n2 = n & 0xFFFFFFFE;
                    int parenSP;
                    do {
                        parenSP = this.parenSP - 2;
                        this.parenSP = parenSP;
                    } while (parenSP >= 0 && this.stack[this.parenSP] != n2);
                    if (this.parenSP >= 0) {
                        script = this.stack[this.parenSP + 1];
                    }
                    else {
                        this.parenSP = 0;
                    }
                    if (this.parenSP < i) {
                        i = this.parenSP;
                    }
                }
            }
            if (!sameScript(this.scriptCode, script)) {
                this.pushback(nextCodePoint);
                break;
            }
            if (this.scriptCode <= 1 && script > 1) {
                this.scriptCode = script;
                while (i < this.parenSP) {
                    this.stack[i + 1] = this.scriptCode;
                    i += 2;
                }
            }
            if (n <= 0 || (n & 0x1) == 0x0 || this.parenSP <= 0) {
                continue;
            }
            this.parenSP -= 2;
        }
        return true;
    }
    
    private final int nextCodePoint() {
        if (this.scriptLimit >= this.textLimit) {
            return -1;
        }
        int n = this.text[this.scriptLimit++];
        if (n >= 55296 && n < 56320 && this.scriptLimit < this.textLimit) {
            final char c = this.text[this.scriptLimit];
            if (c >= '\udc00' && c < '\ue000') {
                ++this.scriptLimit;
                n = (n << 10) + c - 56613888;
            }
        }
        return n;
    }
    
    private final void pushback(final int n) {
        if (n >= 0) {
            if (n >= 65536) {
                this.scriptLimit -= 2;
            }
            else {
                --this.scriptLimit;
            }
        }
    }
    
    private static boolean sameScript(final int n, final int n2) {
        return n == n2 || n <= 1 || n2 <= 1;
    }
    
    private static final byte highBit(int n) {
        if (n <= 0) {
            return -32;
        }
        byte b = 0;
        if (n >= 65536) {
            n >>= 16;
            b += 16;
        }
        if (n >= 256) {
            n >>= 8;
            b += 8;
        }
        if (n >= 16) {
            n >>= 4;
            b += 4;
        }
        if (n >= 4) {
            n >>= 2;
            b += 2;
        }
        if (n >= 2) {
            n >>= 1;
            ++b;
        }
        return b;
    }
    
    private static int getPairIndex(final int n) {
        int i = ScriptRun.pairedCharPower;
        int pairedCharExtra = 0;
        if (n >= ScriptRun.pairedChars[ScriptRun.pairedCharExtra]) {
            pairedCharExtra = ScriptRun.pairedCharExtra;
        }
        while (i > 1) {
            i >>= 1;
            if (n >= ScriptRun.pairedChars[pairedCharExtra + i]) {
                pairedCharExtra += i;
            }
        }
        if (ScriptRun.pairedChars[pairedCharExtra] != n) {
            pairedCharExtra = -1;
        }
        return pairedCharExtra;
    }
    
    static {
        ScriptRun.pairedChars = new int[] { 40, 41, 60, 62, 91, 93, 123, 125, 171, 187, 8216, 8217, 8220, 8221, 8249, 8250, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315 };
        pairedCharPower = 1 << highBit(ScriptRun.pairedChars.length);
        pairedCharExtra = ScriptRun.pairedChars.length - ScriptRun.pairedCharPower;
    }
}
