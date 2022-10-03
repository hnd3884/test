package sun.font;

import java.util.Objects;
import java.io.IOException;
import java.io.File;
import java.util.Locale;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class FontFamily
{
    private static ConcurrentHashMap<String, FontFamily> familyNameMap;
    private static HashMap<String, FontFamily> allLocaleNames;
    protected String familyName;
    protected Font2D plain;
    protected Font2D bold;
    protected Font2D italic;
    protected Font2D bolditalic;
    protected boolean logicalFont;
    protected int familyRank;
    private int familyWidth;
    
    public static FontFamily getFamily(final String s) {
        return FontFamily.familyNameMap.get(s.toLowerCase(Locale.ENGLISH));
    }
    
    public static String[] getAllFamilyNames() {
        return null;
    }
    
    static void remove(final Font2D font2D) {
        final String familyName = font2D.getFamilyName(Locale.ENGLISH);
        final FontFamily family = getFamily(familyName);
        if (family == null) {
            return;
        }
        if (family.plain == font2D) {
            family.plain = null;
        }
        if (family.bold == font2D) {
            family.bold = null;
        }
        if (family.italic == font2D) {
            family.italic = null;
        }
        if (family.bolditalic == font2D) {
            family.bolditalic = null;
        }
        if (family.plain == null && family.bold == null && family.plain == null && family.bold == null) {
            FontFamily.familyNameMap.remove(familyName);
        }
    }
    
    public FontFamily(final String familyName, final boolean logicalFont, final int familyRank) {
        this.logicalFont = false;
        this.familyWidth = 0;
        this.logicalFont = logicalFont;
        this.familyName = familyName;
        this.familyRank = familyRank;
        FontFamily.familyNameMap.put(familyName.toLowerCase(Locale.ENGLISH), this);
    }
    
    FontFamily(final String familyName) {
        this.logicalFont = false;
        this.familyWidth = 0;
        this.logicalFont = false;
        this.familyName = familyName;
        this.familyRank = 4;
    }
    
    public String getFamilyName() {
        return this.familyName;
    }
    
    public int getRank() {
        return this.familyRank;
    }
    
    private boolean isFromSameSource(final Font2D font2D) {
        if (!(font2D instanceof FileFont)) {
            return false;
        }
        FileFont fileFont = null;
        if (this.plain instanceof FileFont) {
            fileFont = (FileFont)this.plain;
        }
        else if (this.bold instanceof FileFont) {
            fileFont = (FileFont)this.bold;
        }
        else if (this.italic instanceof FileFont) {
            fileFont = (FileFont)this.italic;
        }
        else if (this.bolditalic instanceof FileFont) {
            fileFont = (FileFont)this.bolditalic;
        }
        if (fileFont == null) {
            return false;
        }
        File file = new File(fileFont.platName).getParentFile();
        File file2 = new File(((FileFont)font2D).platName).getParentFile();
        if (file != null) {
            try {
                file = file.getCanonicalFile();
            }
            catch (final IOException ex) {}
        }
        if (file2 != null) {
            try {
                file2 = file2.getCanonicalFile();
            }
            catch (final IOException ex2) {}
        }
        return Objects.equals(file2, file);
    }
    
    private boolean preferredWidth(final Font2D font2D) {
        final int width = font2D.getWidth();
        if (this.familyWidth == 0) {
            this.familyWidth = width;
            return true;
        }
        if (width == this.familyWidth) {
            return true;
        }
        if (Math.abs(5 - width) < Math.abs(5 - this.familyWidth)) {
            if (FontUtilities.debugFonts()) {
                FontUtilities.getLogger().info("Found more preferred width. New width = " + width + " Old width = " + this.familyWidth + " in font " + font2D + " nulling out fonts plain: " + this.plain + " bold: " + this.bold + " italic: " + this.italic + " bolditalic: " + this.bolditalic);
            }
            this.familyWidth = width;
            final Font2D font2D2 = null;
            this.bolditalic = font2D2;
            this.italic = font2D2;
            this.bold = font2D2;
            this.plain = font2D2;
            return true;
        }
        if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().info("Family rejecting font " + font2D + " of less preferred width " + width);
        }
        return false;
    }
    
    private boolean closerWeight(final Font2D font2D, final Font2D font2D2, final int n) {
        if (this.familyWidth != font2D2.getWidth()) {
            return false;
        }
        if (font2D == null) {
            return true;
        }
        if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().info("New weight for style " + n + ". Curr.font=" + font2D + " New font=" + font2D2 + " Curr.weight=" + font2D.getWeight() + " New weight=" + font2D2.getWeight());
        }
        final int weight = font2D2.getWeight();
        switch (n) {
            case 0:
            case 2: {
                return weight <= 400 && weight > font2D.getWeight();
            }
            case 1:
            case 3: {
                return Math.abs(weight - 700) < Math.abs(font2D.getWeight() - 700);
            }
            default: {
                return false;
            }
        }
    }
    
    public void setFont(final Font2D font2D, final int n) {
        if (FontUtilities.isLogging()) {
            String s;
            if (font2D instanceof CompositeFont) {
                s = "Request to add " + font2D.getFamilyName(null) + " with style " + n + " to family " + this.familyName;
            }
            else {
                s = "Request to add " + font2D + " with style " + n + " to family " + this;
            }
            FontUtilities.getLogger().info(s);
        }
        if (font2D.getRank() > this.familyRank && !this.isFromSameSource(font2D)) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().warning("Rejecting adding " + font2D + " of lower rank " + font2D.getRank() + " to family " + this + " of rank " + this.familyRank);
            }
            return;
        }
        switch (n) {
            case 0: {
                if (this.preferredWidth(font2D) && this.closerWeight(this.plain, font2D, n)) {
                    this.plain = font2D;
                    break;
                }
                break;
            }
            case 1: {
                if (this.preferredWidth(font2D) && this.closerWeight(this.bold, font2D, n)) {
                    this.bold = font2D;
                    break;
                }
                break;
            }
            case 2: {
                if (this.preferredWidth(font2D) && this.closerWeight(this.italic, font2D, n)) {
                    this.italic = font2D;
                    break;
                }
                break;
            }
            case 3: {
                if (this.preferredWidth(font2D) && this.closerWeight(this.bolditalic, font2D, n)) {
                    this.bolditalic = font2D;
                    break;
                }
                break;
            }
        }
    }
    
    public Font2D getFontWithExactStyleMatch(final int n) {
        switch (n) {
            case 0: {
                return this.plain;
            }
            case 1: {
                return this.bold;
            }
            case 2: {
                return this.italic;
            }
            case 3: {
                return this.bolditalic;
            }
            default: {
                return null;
            }
        }
    }
    
    public Font2D getFont(final int n) {
        switch (n) {
            case 0: {
                return this.plain;
            }
            case 1: {
                if (this.bold != null) {
                    return this.bold;
                }
                if (this.plain != null && this.plain.canDoStyle(n)) {
                    return this.plain;
                }
                return null;
            }
            case 2: {
                if (this.italic != null) {
                    return this.italic;
                }
                if (this.plain != null && this.plain.canDoStyle(n)) {
                    return this.plain;
                }
                return null;
            }
            case 3: {
                if (this.bolditalic != null) {
                    return this.bolditalic;
                }
                if (this.bold != null && this.bold.canDoStyle(n)) {
                    return this.bold;
                }
                if (this.italic != null && this.italic.canDoStyle(n)) {
                    return this.italic;
                }
                if (this.plain != null && this.plain.canDoStyle(n)) {
                    return this.plain;
                }
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    Font2D getClosestStyle(final int n) {
        switch (n) {
            case 0: {
                if (this.bold != null) {
                    return this.bold;
                }
                if (this.italic != null) {
                    return this.italic;
                }
                return this.bolditalic;
            }
            case 1: {
                if (this.plain != null) {
                    return this.plain;
                }
                if (this.bolditalic != null) {
                    return this.bolditalic;
                }
                return this.italic;
            }
            case 2: {
                if (this.bolditalic != null) {
                    return this.bolditalic;
                }
                if (this.plain != null) {
                    return this.plain;
                }
                return this.bold;
            }
            case 3: {
                if (this.italic != null) {
                    return this.italic;
                }
                if (this.bold != null) {
                    return this.bold;
                }
                return this.plain;
            }
            default: {
                return null;
            }
        }
    }
    
    static synchronized void addLocaleNames(final FontFamily fontFamily, final String[] array) {
        if (FontFamily.allLocaleNames == null) {
            FontFamily.allLocaleNames = new HashMap<String, FontFamily>();
        }
        for (int i = 0; i < array.length; ++i) {
            FontFamily.allLocaleNames.put(array[i].toLowerCase(), fontFamily);
        }
    }
    
    public static synchronized FontFamily getLocaleFamily(final String s) {
        if (FontFamily.allLocaleNames == null) {
            return null;
        }
        return FontFamily.allLocaleNames.get(s.toLowerCase());
    }
    
    public static FontFamily[] getAllFontFamilies() {
        return FontFamily.familyNameMap.values().toArray(new FontFamily[0]);
    }
    
    @Override
    public String toString() {
        return "Font family: " + this.familyName + " plain=" + this.plain + " bold=" + this.bold + " italic=" + this.italic + " bolditalic=" + this.bolditalic;
    }
    
    static {
        FontFamily.familyNameMap = new ConcurrentHashMap<String, FontFamily>();
    }
}
