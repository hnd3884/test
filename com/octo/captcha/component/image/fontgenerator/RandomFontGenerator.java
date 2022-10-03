package com.octo.captcha.component.image.fontgenerator;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.awt.GraphicsEnvironment;
import java.security.SecureRandom;
import java.util.Random;
import java.awt.Font;

public class RandomFontGenerator extends AbstractFontGenerator
{
    private int[] STYLES;
    private String requiredCharacters;
    public static String[] defaultBadFontNamePrefixes;
    private String[] badFontNamePrefixes;
    private static final int GENERATED_FONTS_ARRAY_SIZE = 3000;
    private boolean mixStyles;
    private Font[] generatedFonts;
    protected Random myRandom;
    
    public RandomFontGenerator(final Integer n, final Integer n2) {
        super(n, n2);
        this.STYLES = new int[] { 0, 2, 1, 3 };
        this.requiredCharacters = "abcdefghijklmnopqrstuvwxyz0123456789";
        this.badFontNamePrefixes = RandomFontGenerator.defaultBadFontNamePrefixes;
        this.mixStyles = true;
        this.generatedFonts = null;
        this.myRandom = new SecureRandom();
        this.initializeFonts(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts());
    }
    
    public RandomFontGenerator(final Integer n, final Integer n2, final Font[] array) {
        super(n, n2);
        this.STYLES = new int[] { 0, 2, 1, 3 };
        this.requiredCharacters = "abcdefghijklmnopqrstuvwxyz0123456789";
        this.badFontNamePrefixes = RandomFontGenerator.defaultBadFontNamePrefixes;
        this.mixStyles = true;
        this.generatedFonts = null;
        this.myRandom = new SecureRandom();
        if (array == null || array.length < 1) {
            throw new IllegalArgumentException("fonts list cannot be null or empty");
        }
        this.initializeFonts(array);
    }
    
    public RandomFontGenerator(final Integer n, final Integer n2, final Font[] array, final boolean mixStyles) {
        super(n, n2);
        this.STYLES = new int[] { 0, 2, 1, 3 };
        this.requiredCharacters = "abcdefghijklmnopqrstuvwxyz0123456789";
        this.badFontNamePrefixes = RandomFontGenerator.defaultBadFontNamePrefixes;
        this.mixStyles = true;
        this.generatedFonts = null;
        this.myRandom = new SecureRandom();
        if (array == null || array.length < 1) {
            throw new IllegalArgumentException("fonts list cannot be null or empty");
        }
        this.mixStyles = mixStyles;
        this.initializeFonts(array);
    }
    
    public RandomFontGenerator(final Integer n, final Integer n2, final String[] badFontNamePrefixes) {
        super(n, n2);
        this.STYLES = new int[] { 0, 2, 1, 3 };
        this.requiredCharacters = "abcdefghijklmnopqrstuvwxyz0123456789";
        this.badFontNamePrefixes = RandomFontGenerator.defaultBadFontNamePrefixes;
        this.mixStyles = true;
        this.generatedFonts = null;
        this.myRandom = new SecureRandom();
        this.badFontNamePrefixes = badFontNamePrefixes;
        this.initializeFonts(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts());
    }
    
    private void initializeFonts(final Font[] array) {
        final List cleanFontList = this.cleanFontList(array);
        this.checkInitializedFontListSize(cleanFontList);
        this.generatedFonts = this.generateCustomStyleFontArray(cleanFontList);
    }
    
    private void checkInitializedFontListSize(final List list) {
        if (list.size() < 1) {
            throw new IllegalArgumentException("fonts list cannot be null or empty, some of your font are removed from the list by this class, Courrier and TimesRoman");
        }
    }
    
    public Font getFont() {
        return this.generatedFonts[Math.abs(this.myRandom.nextInt(3000))];
    }
    
    private Font[] generateCustomStyleFontArray(final List list) {
        final Font[] array = new Font[3000];
        for (int i = 0; i < 3000; ++i) {
            array[i] = this.applyCustomDeformationOnGeneratedFont(this.applyStyle(list.get(this.myRandom.nextInt(list.size()))));
        }
        return array;
    }
    
    protected Font applyStyle(final Font font) {
        int abs = 0;
        if (this.getFontSizeDelta() > 0) {
            abs = Math.abs(this.myRandom.nextInt(this.getFontSizeDelta()));
        }
        return font.deriveFont(this.mixStyles ? this.STYLES[this.myRandom.nextInt(this.STYLES.length)] : font.getStyle(), (float)(this.getMinFontSize() + abs));
    }
    
    private int getFontSizeDelta() {
        return this.getMaxFontSize() - this.getMinFontSize();
    }
    
    protected Font applyCustomDeformationOnGeneratedFont(final Font font) {
        return font;
    }
    
    protected List cleanFontList(final Font[] array) {
        final ArrayList list = new ArrayList(array.length);
        list.addAll(Arrays.asList(array));
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final Font font = (Font)iterator.next();
            if (!this.checkFontNamePrefix(iterator, font)) {
                this.checkFontCanDisplayCharacters(iterator, font);
            }
        }
        return list;
    }
    
    private boolean checkFontNamePrefix(final Iterator iterator, final Font font) {
        boolean b = false;
        for (int i = 0; i < this.badFontNamePrefixes.length; ++i) {
            final String s = this.badFontNamePrefixes[i];
            if (s != null && !"".equals(s) && font.getName().startsWith(s)) {
                iterator.remove();
                b = true;
                break;
            }
        }
        return b;
    }
    
    private boolean checkFontCanDisplayCharacters(final Iterator iterator, final Font font) {
        boolean b = false;
        for (int i = 0; i < this.requiredCharacters.length(); ++i) {
            if (!font.canDisplay(this.requiredCharacters.charAt(i))) {
                iterator.remove();
                b = true;
                break;
            }
        }
        return b;
    }
    
    public String getRequiredCharacters() {
        return this.requiredCharacters;
    }
    
    public void setRequiredCharacters(final String requiredCharacters) {
        this.requiredCharacters = requiredCharacters;
    }
    
    public String[] getBadFontNamePrefixes() {
        return this.badFontNamePrefixes;
    }
    
    public void setBadFontNamePrefixes(final String[] badFontNamePrefixes) {
        this.badFontNamePrefixes = badFontNamePrefixes;
    }
    
    static {
        RandomFontGenerator.defaultBadFontNamePrefixes = new String[] { "Courier", "Times Roman" };
    }
}
