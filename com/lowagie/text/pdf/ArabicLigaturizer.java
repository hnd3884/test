package com.lowagie.text.pdf;

@Deprecated
public class ArabicLigaturizer
{
    private static final char ALEF = '\u0627';
    private static final char ALEFHAMZA = '\u0623';
    private static final char ALEFHAMZABELOW = '\u0625';
    private static final char ALEFMADDA = '\u0622';
    private static final char LAM = '\u0644';
    private static final char HAMZA = '\u0621';
    private static final char TATWEEL = '\u0640';
    private static final char ZWJ = '\u200d';
    private static final char HAMZAABOVE = '\u0654';
    private static final char HAMZABELOW = '\u0655';
    private static final char WAWHAMZA = '\u0624';
    private static final char YEHHAMZA = '\u0626';
    private static final char WAW = '\u0648';
    private static final char ALEFMAKSURA = '\u0649';
    private static final char YEH = '\u064a';
    private static final char FARSIYEH = '\u06cc';
    private static final char SHADDA = '\u0651';
    private static final char KASRA = '\u0650';
    private static final char FATHA = '\u064e';
    private static final char DAMMA = '\u064f';
    private static final char MADDA = '\u0653';
    private static final char LAM_ALEF = '\ufefb';
    private static final char LAM_ALEFHAMZA = '\ufef7';
    private static final char LAM_ALEFHAMZABELOW = '\ufef9';
    private static final char LAM_ALEFMADDA = '\ufef5';
    private static final char[][] chartable;
    public static final int ar_nothing = 0;
    public static final int ar_novowel = 1;
    public static final int ar_composedtashkeel = 4;
    public static final int ar_lig = 8;
    public static final int DIGITS_EN2AN = 32;
    public static final int DIGITS_AN2EN = 64;
    public static final int DIGITS_EN2AN_INIT_LR = 96;
    public static final int DIGITS_EN2AN_INIT_AL = 128;
    private static final int DIGITS_RESERVED = 160;
    public static final int DIGITS_MASK = 224;
    public static final int DIGIT_TYPE_AN = 0;
    public static final int DIGIT_TYPE_AN_EXTENDED = 256;
    public static final int DIGIT_TYPE_MASK = 256;
    
    static boolean isVowel(final char s) {
        return (s >= '\u064b' && s <= '\u0655') || s == '\u0670';
    }
    
    static char charshape(final char s, final int which) {
        if (s >= '\u0621' && s <= '\u06d3') {
            int l = 0;
            int r = ArabicLigaturizer.chartable.length - 1;
            while (l <= r) {
                final int m = (l + r) / 2;
                if (s == ArabicLigaturizer.chartable[m][0]) {
                    return ArabicLigaturizer.chartable[m][which + 1];
                }
                if (s < ArabicLigaturizer.chartable[m][0]) {
                    r = m - 1;
                }
                else {
                    l = m + 1;
                }
            }
        }
        else if (s >= '\ufef5' && s <= '\ufefb') {
            return (char)(s + which);
        }
        return s;
    }
    
    static int shapecount(final char s) {
        if (s >= '\u0621' && s <= '\u06d3' && !isVowel(s)) {
            int l = 0;
            int r = ArabicLigaturizer.chartable.length - 1;
            while (l <= r) {
                final int m = (l + r) / 2;
                if (s == ArabicLigaturizer.chartable[m][0]) {
                    return ArabicLigaturizer.chartable[m].length - 1;
                }
                if (s < ArabicLigaturizer.chartable[m][0]) {
                    r = m - 1;
                }
                else {
                    l = m + 1;
                }
            }
        }
        else if (s == '\u200d') {
            return 4;
        }
        return 1;
    }
    
    static int ligature(final char newchar, final charstruct oldchar) {
        int retval = 0;
        if (oldchar.basechar == '\0') {
            return 0;
        }
        if (isVowel(newchar)) {
            retval = 1;
            if (oldchar.vowel != '\0' && newchar != '\u0651') {
                retval = 2;
            }
            Label_0313: {
                switch (newchar) {
                    case '\u0651': {
                        if (oldchar.mark1 == '\0') {
                            oldchar.mark1 = '\u0651';
                            break;
                        }
                        return 0;
                    }
                    case '\u0655': {
                        switch (oldchar.basechar) {
                            case '\u0627': {
                                oldchar.basechar = '\u0625';
                                retval = 2;
                                break Label_0313;
                            }
                            case '\ufefb': {
                                oldchar.basechar = '\ufef9';
                                retval = 2;
                                break Label_0313;
                            }
                            default: {
                                oldchar.mark1 = '\u0655';
                                break Label_0313;
                            }
                        }
                        break;
                    }
                    case '\u0654': {
                        switch (oldchar.basechar) {
                            case '\u0627': {
                                oldchar.basechar = '\u0623';
                                retval = 2;
                                break Label_0313;
                            }
                            case '\ufefb': {
                                oldchar.basechar = '\ufef7';
                                retval = 2;
                                break Label_0313;
                            }
                            case '\u0648': {
                                oldchar.basechar = '\u0624';
                                retval = 2;
                                break Label_0313;
                            }
                            case '\u0649':
                            case '\u064a':
                            case '\u06cc': {
                                oldchar.basechar = '\u0626';
                                retval = 2;
                                break Label_0313;
                            }
                            default: {
                                oldchar.mark1 = '\u0654';
                                break Label_0313;
                            }
                        }
                        break;
                    }
                    case '\u0653': {
                        switch (oldchar.basechar) {
                            case '\u0627': {
                                oldchar.basechar = '\u0622';
                                retval = 2;
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        oldchar.vowel = newchar;
                        break;
                    }
                }
            }
            if (retval == 1) {
                ++oldchar.lignum;
            }
            return retval;
        }
        if (oldchar.vowel != '\0') {
            return 0;
        }
        switch (oldchar.basechar) {
            case '\u0644': {
                switch (newchar) {
                    case '\u0627': {
                        oldchar.basechar = '\ufefb';
                        oldchar.numshapes = 2;
                        retval = 3;
                        break;
                    }
                    case '\u0623': {
                        oldchar.basechar = '\ufef7';
                        oldchar.numshapes = 2;
                        retval = 3;
                        break;
                    }
                    case '\u0625': {
                        oldchar.basechar = '\ufef9';
                        oldchar.numshapes = 2;
                        retval = 3;
                        break;
                    }
                    case '\u0622': {
                        oldchar.basechar = '\ufef5';
                        oldchar.numshapes = 2;
                        retval = 3;
                        break;
                    }
                }
                break;
            }
            case '\0': {
                oldchar.basechar = newchar;
                oldchar.numshapes = shapecount(newchar);
                retval = 1;
                break;
            }
        }
        return retval;
    }
    
    static void copycstostring(final StringBuffer string, final charstruct s, final int level) {
        if (s.basechar == '\0') {
            return;
        }
        string.append(s.basechar);
        --s.lignum;
        if (s.mark1 != '\0') {
            if ((level & 0x1) == 0x0) {
                string.append(s.mark1);
                --s.lignum;
            }
            else {
                --s.lignum;
            }
        }
        if (s.vowel != '\0') {
            if ((level & 0x1) == 0x0) {
                string.append(s.vowel);
                --s.lignum;
            }
            else {
                --s.lignum;
            }
        }
    }
    
    static void doublelig(final StringBuffer string, final int level) {
        final int olen;
        int len = olen = string.length();
        int j = 0;
        for (int si = 1; si < olen; ++si) {
            char lapresult = '\0';
            if ((level & 0x4) != 0x0) {
                switch (string.charAt(j)) {
                    case '\u0651': {
                        switch (string.charAt(si)) {
                            case '\u0650': {
                                lapresult = '\ufc62';
                                break;
                            }
                            case '\u064e': {
                                lapresult = '\ufc60';
                                break;
                            }
                            case '\u064f': {
                                lapresult = '\ufc61';
                                break;
                            }
                            case '\u064c': {
                                lapresult = '\ufc5e';
                                break;
                            }
                            case '\u064d': {
                                lapresult = '\ufc5f';
                                break;
                            }
                        }
                        break;
                    }
                    case '\u0650': {
                        if (string.charAt(si) == '\u0651') {
                            lapresult = '\ufc62';
                            break;
                        }
                        break;
                    }
                    case '\u064e': {
                        if (string.charAt(si) == '\u0651') {
                            lapresult = '\ufc60';
                            break;
                        }
                        break;
                    }
                    case '\u064f': {
                        if (string.charAt(si) == '\u0651') {
                            lapresult = '\ufc61';
                            break;
                        }
                        break;
                    }
                }
            }
            if ((level & 0x8) != 0x0) {
                switch (string.charAt(j)) {
                    case '\ufedf': {
                        switch (string.charAt(si)) {
                            case '\ufe9e': {
                                lapresult = '\ufc3f';
                                break;
                            }
                            case '\ufea0': {
                                lapresult = '\ufcc9';
                                break;
                            }
                            case '\ufea2': {
                                lapresult = '\ufc40';
                                break;
                            }
                            case '\ufea4': {
                                lapresult = '\ufcca';
                                break;
                            }
                            case '\ufea6': {
                                lapresult = '\ufc41';
                                break;
                            }
                            case '\ufea8': {
                                lapresult = '\ufccb';
                                break;
                            }
                            case '\ufee2': {
                                lapresult = '\ufc42';
                                break;
                            }
                            case '\ufee4': {
                                lapresult = '\ufccc';
                                break;
                            }
                        }
                        break;
                    }
                    case '\ufe97': {
                        switch (string.charAt(si)) {
                            case '\ufea0': {
                                lapresult = '\ufca1';
                                break;
                            }
                            case '\ufea4': {
                                lapresult = '\ufca2';
                                break;
                            }
                            case '\ufea8': {
                                lapresult = '\ufca3';
                                break;
                            }
                        }
                        break;
                    }
                    case '\ufe91': {
                        switch (string.charAt(si)) {
                            case '\ufea0': {
                                lapresult = '\ufc9c';
                                break;
                            }
                            case '\ufea4': {
                                lapresult = '\ufc9d';
                                break;
                            }
                            case '\ufea8': {
                                lapresult = '\ufc9e';
                                break;
                            }
                        }
                        break;
                    }
                    case '\ufee7': {
                        switch (string.charAt(si)) {
                            case '\ufea0': {
                                lapresult = '\ufcd2';
                                break;
                            }
                            case '\ufea4': {
                                lapresult = '\ufcd3';
                                break;
                            }
                            case '\ufea8': {
                                lapresult = '\ufcd4';
                                break;
                            }
                        }
                        break;
                    }
                    case '\ufee8': {
                        switch (string.charAt(si)) {
                            case '\ufeae': {
                                lapresult = '\ufc8a';
                                break;
                            }
                            case '\ufeb0': {
                                lapresult = '\ufc8b';
                                break;
                            }
                        }
                        break;
                    }
                    case '\ufee3': {
                        switch (string.charAt(si)) {
                            case '\ufea0': {
                                lapresult = '\ufcce';
                                break;
                            }
                            case '\ufea4': {
                                lapresult = '\ufccf';
                                break;
                            }
                            case '\ufea8': {
                                lapresult = '\ufcd0';
                                break;
                            }
                            case '\ufee4': {
                                lapresult = '\ufcd1';
                                break;
                            }
                        }
                        break;
                    }
                    case '\ufed3': {
                        switch (string.charAt(si)) {
                            case '\ufef2': {
                                lapresult = '\ufc32';
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            if (lapresult != '\0') {
                string.setCharAt(j, lapresult);
                --len;
            }
            else {
                ++j;
                string.setCharAt(j, string.charAt(si));
            }
        }
        string.setLength(len);
    }
    
    static boolean connects_to_left(final charstruct a) {
        return a.numshapes > 2;
    }
    
    static void shape(final char[] text, final StringBuffer string, final int level) {
        int p = 0;
        charstruct oldchar = new charstruct();
        charstruct curchar = new charstruct();
        while (p < text.length) {
            final char nextletter = text[p++];
            final int join = ligature(nextletter, curchar);
            if (join == 0) {
                final int nc = shapecount(nextletter);
                int which;
                if (nc == 1) {
                    which = 0;
                }
                else {
                    which = 2;
                }
                if (connects_to_left(oldchar)) {
                    ++which;
                }
                which %= curchar.numshapes;
                curchar.basechar = charshape(curchar.basechar, which);
                copycstostring(string, oldchar, level);
                oldchar = curchar;
                curchar = new charstruct();
                curchar.basechar = nextletter;
                curchar.numshapes = nc;
                final charstruct charstruct = curchar;
                ++charstruct.lignum;
            }
            else {
                if (join == 1) {
                    continue;
                }
                continue;
            }
        }
        int which;
        if (connects_to_left(oldchar)) {
            which = 1;
        }
        else {
            which = 0;
        }
        which %= curchar.numshapes;
        curchar.basechar = charshape(curchar.basechar, which);
        copycstostring(string, oldchar, level);
        copycstostring(string, curchar, level);
    }
    
    static int arabic_shape(final char[] src, final int srcoffset, final int srclength, final char[] dest, final int destoffset, final int destlength, final int level) {
        final char[] str = new char[srclength];
        for (int k = srclength + srcoffset - 1; k >= srcoffset; --k) {
            str[k - srcoffset] = src[k];
        }
        final StringBuffer string = new StringBuffer(srclength);
        shape(str, string, level);
        if ((level & 0xC) != 0x0) {
            doublelig(string, level);
        }
        System.arraycopy(string.toString().toCharArray(), 0, dest, destoffset, string.length());
        return string.length();
    }
    
    static void processNumbers(final char[] text, final int offset, final int length, final int options) {
        final int limit = offset + length;
        if ((options & 0xE0) != 0x0) {
            char digitBase = '0';
            switch (options & 0x100) {
                case 0: {
                    digitBase = '\u0660';
                    break;
                }
                case 256: {
                    digitBase = '\u06f0';
                    break;
                }
            }
            switch (options & 0xE0) {
                case 32: {
                    final int digitDelta = digitBase - '0';
                    for (int i = offset; i < limit; ++i) {
                        final char ch = text[i];
                        if (ch <= '9' && ch >= '0') {
                            final int n = i;
                            text[n] += (char)digitDelta;
                        }
                    }
                    break;
                }
                case 64: {
                    final char digitTop = (char)(digitBase + '\t');
                    final int digitDelta2 = '0' - digitBase;
                    for (int j = offset; j < limit; ++j) {
                        final char ch2 = text[j];
                        if (ch2 <= digitTop && ch2 >= digitBase) {
                            final int n2 = j;
                            text[n2] += (char)digitDelta2;
                        }
                    }
                }
                case 96: {}
            }
        }
    }
    
    static {
        chartable = new char[][] { { '\u0621', '\ufe80' }, { '\u0622', '\ufe81', '\ufe82' }, { '\u0623', '\ufe83', '\ufe84' }, { '\u0624', '\ufe85', '\ufe86' }, { '\u0625', '\ufe87', '\ufe88' }, { '\u0626', '\ufe89', '\ufe8a', '\ufe8b', '\ufe8c' }, { '\u0627', '\ufe8d', '\ufe8e' }, { '\u0628', '\ufe8f', '\ufe90', '\ufe91', '\ufe92' }, { '\u0629', '\ufe93', '\ufe94' }, { '\u062a', '\ufe95', '\ufe96', '\ufe97', '\ufe98' }, { '\u062b', '\ufe99', '\ufe9a', '\ufe9b', '\ufe9c' }, { '\u062c', '\ufe9d', '\ufe9e', '\ufe9f', '\ufea0' }, { '\u062d', '\ufea1', '\ufea2', '\ufea3', '\ufea4' }, { '\u062e', '\ufea5', '\ufea6', '\ufea7', '\ufea8' }, { '\u062f', '\ufea9', '\ufeaa' }, { '\u0630', '\ufeab', '\ufeac' }, { '\u0631', '\ufead', '\ufeae' }, { '\u0632', '\ufeaf', '\ufeb0' }, { '\u0633', '\ufeb1', '\ufeb2', '\ufeb3', '\ufeb4' }, { '\u0634', '\ufeb5', '\ufeb6', '\ufeb7', '\ufeb8' }, { '\u0635', '\ufeb9', '\ufeba', '\ufebb', '\ufebc' }, { '\u0636', '\ufebd', '\ufebe', '\ufebf', '\ufec0' }, { '\u0637', '\ufec1', '\ufec2', '\ufec3', '\ufec4' }, { '\u0638', '\ufec5', '\ufec6', '\ufec7', '\ufec8' }, { '\u0639', '\ufec9', '\ufeca', '\ufecb', '\ufecc' }, { '\u063a', '\ufecd', '\ufece', '\ufecf', '\ufed0' }, { '\u0640', '\u0640', '\u0640', '\u0640', '\u0640' }, { '\u0641', '\ufed1', '\ufed2', '\ufed3', '\ufed4' }, { '\u0642', '\ufed5', '\ufed6', '\ufed7', '\ufed8' }, { '\u0643', '\ufed9', '\ufeda', '\ufedb', '\ufedc' }, { '\u0644', '\ufedd', '\ufede', '\ufedf', '\ufee0' }, { '\u0645', '\ufee1', '\ufee2', '\ufee3', '\ufee4' }, { '\u0646', '\ufee5', '\ufee6', '\ufee7', '\ufee8' }, { '\u0647', '\ufee9', '\ufeea', '\ufeeb', '\ufeec' }, { '\u0648', '\ufeed', '\ufeee' }, { '\u0649', '\ufeef', '\ufef0', '\ufbe8', '\ufbe9' }, { '\u064a', '\ufef1', '\ufef2', '\ufef3', '\ufef4' }, { '\u0671', '\ufb50', '\ufb51' }, { '\u0679', '\ufb66', '\ufb67', '\ufb68', '\ufb69' }, { '\u067a', '\ufb5e', '\ufb5f', '\ufb60', '\ufb61' }, { '\u067b', '\ufb52', '\ufb53', '\ufb54', '\ufb55' }, { '\u067e', '\ufb56', '\ufb57', '\ufb58', '\ufb59' }, { '\u067f', '\ufb62', '\ufb63', '\ufb64', '\ufb65' }, { '\u0680', '\ufb5a', '\ufb5b', '\ufb5c', '\ufb5d' }, { '\u0683', '\ufb76', '\ufb77', '\ufb78', '\ufb79' }, { '\u0684', '\ufb72', '\ufb73', '\ufb74', '\ufb75' }, { '\u0686', '\ufb7a', '\ufb7b', '\ufb7c', '\ufb7d' }, { '\u0687', '\ufb7e', '\ufb7f', '\ufb80', '\ufb81' }, { '\u0688', '\ufb88', '\ufb89' }, { '\u068c', '\ufb84', '\ufb85' }, { '\u068d', '\ufb82', '\ufb83' }, { '\u068e', '\ufb86', '\ufb87' }, { '\u0691', '\ufb8c', '\ufb8d' }, { '\u0698', '\ufb8a', '\ufb8b' }, { '\u06a4', '\ufb6a', '\ufb6b', '\ufb6c', '\ufb6d' }, { '\u06a6', '\ufb6e', '\ufb6f', '\ufb70', '\ufb71' }, { '\u06a9', '\ufb8e', '\ufb8f', '\ufb90', '\ufb91' }, { '\u06ad', '\ufbd3', '\ufbd4', '\ufbd5', '\ufbd6' }, { '\u06af', '\ufb92', '\ufb93', '\ufb94', '\ufb95' }, { '\u06b1', '\ufb9a', '\ufb9b', '\ufb9c', '\ufb9d' }, { '\u06b3', '\ufb96', '\ufb97', '\ufb98', '\ufb99' }, { '\u06ba', '\ufb9e', '\ufb9f' }, { '\u06bb', '\ufba0', '\ufba1', '\ufba2', '\ufba3' }, { '\u06be', '\ufbaa', '\ufbab', '\ufbac', '\ufbad' }, { '\u06c0', '\ufba4', '\ufba5' }, { '\u06c1', '\ufba6', '\ufba7', '\ufba8', '\ufba9' }, { '\u06c5', '\ufbe0', '\ufbe1' }, { '\u06c6', '\ufbd9', '\ufbda' }, { '\u06c7', '\ufbd7', '\ufbd8' }, { '\u06c8', '\ufbdb', '\ufbdc' }, { '\u06c9', '\ufbe2', '\ufbe3' }, { '\u06cb', '\ufbde', '\ufbdf' }, { '\u06cc', '\ufbfc', '\ufbfd', '\ufbfe', '\ufbff' }, { '\u06d0', '\ufbe4', '\ufbe5', '\ufbe6', '\ufbe7' }, { '\u06d2', '\ufbae', '\ufbaf' }, { '\u06d3', '\ufbb0', '\ufbb1' } };
    }
    
    static class charstruct
    {
        char basechar;
        char mark1;
        char vowel;
        int lignum;
        int numshapes;
        
        charstruct() {
            this.numshapes = 1;
        }
    }
}
