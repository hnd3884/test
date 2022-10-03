package java.awt.im;

public final class InputSubset extends Character.Subset
{
    public static final InputSubset LATIN;
    public static final InputSubset LATIN_DIGITS;
    public static final InputSubset TRADITIONAL_HANZI;
    public static final InputSubset SIMPLIFIED_HANZI;
    public static final InputSubset KANJI;
    public static final InputSubset HANJA;
    public static final InputSubset HALFWIDTH_KATAKANA;
    public static final InputSubset FULLWIDTH_LATIN;
    public static final InputSubset FULLWIDTH_DIGITS;
    
    private InputSubset(final String s) {
        super(s);
    }
    
    static {
        LATIN = new InputSubset("LATIN");
        LATIN_DIGITS = new InputSubset("LATIN_DIGITS");
        TRADITIONAL_HANZI = new InputSubset("TRADITIONAL_HANZI");
        SIMPLIFIED_HANZI = new InputSubset("SIMPLIFIED_HANZI");
        KANJI = new InputSubset("KANJI");
        HANJA = new InputSubset("HANJA");
        HALFWIDTH_KATAKANA = new InputSubset("HALFWIDTH_KATAKANA");
        FULLWIDTH_LATIN = new InputSubset("FULLWIDTH_LATIN");
        FULLWIDTH_DIGITS = new InputSubset("FULLWIDTH_DIGITS");
    }
}
