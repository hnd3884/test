package sun.font;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Locale;

public final class CompositeFont extends Font2D
{
    private boolean[] deferredInitialisation;
    String[] componentFileNames;
    String[] componentNames;
    private PhysicalFont[] components;
    int numSlots;
    int numMetricsSlots;
    int[] exclusionRanges;
    int[] maxIndices;
    int numGlyphs;
    int localeSlot;
    boolean isStdComposite;
    
    public CompositeFont(final String fullName, final String[] componentFileNames, final String[] componentNames, final int numMetricsSlots, final int[] exclusionRanges, final int[] maxIndices, final boolean b, final SunFontManager sunFontManager) {
        this.numGlyphs = 0;
        this.localeSlot = -1;
        this.isStdComposite = true;
        this.handle = new Font2DHandle(this);
        this.fullName = fullName;
        this.componentFileNames = componentFileNames;
        this.componentNames = componentNames;
        if (componentNames == null) {
            this.numSlots = this.componentFileNames.length;
        }
        else {
            this.numSlots = this.componentNames.length;
        }
        this.numSlots = ((this.numSlots <= 254) ? this.numSlots : 254);
        this.numMetricsSlots = numMetricsSlots;
        this.exclusionRanges = exclusionRanges;
        this.maxIndices = maxIndices;
        if (sunFontManager.getEUDCFont() != null) {
            final int numMetricsSlots2 = this.numMetricsSlots;
            final int n = this.numSlots - numMetricsSlots2;
            ++this.numSlots;
            if (this.componentNames != null) {
                System.arraycopy(componentNames, 0, this.componentNames = new String[this.numSlots], 0, numMetricsSlots2);
                this.componentNames[numMetricsSlots2] = sunFontManager.getEUDCFont().getFontName(null);
                System.arraycopy(componentNames, numMetricsSlots2, this.componentNames, numMetricsSlots2 + 1, n);
            }
            if (this.componentFileNames != null) {
                System.arraycopy(componentFileNames, 0, this.componentFileNames = new String[this.numSlots], 0, numMetricsSlots2);
                System.arraycopy(componentFileNames, numMetricsSlots2, this.componentFileNames, numMetricsSlots2 + 1, n);
            }
            (this.components = new PhysicalFont[this.numSlots])[numMetricsSlots2] = sunFontManager.getEUDCFont();
            this.deferredInitialisation = new boolean[this.numSlots];
            if (b) {
                for (int i = 0; i < this.numSlots - 1; ++i) {
                    this.deferredInitialisation[i] = true;
                }
            }
        }
        else {
            this.components = new PhysicalFont[this.numSlots];
            this.deferredInitialisation = new boolean[this.numSlots];
            if (b) {
                for (int j = 0; j < this.numSlots; ++j) {
                    this.deferredInitialisation[j] = true;
                }
            }
        }
        this.fontRank = 2;
        final int index = this.fullName.indexOf(46);
        if (index > 0) {
            this.familyName = this.fullName.substring(0, index);
            if (index + 1 < this.fullName.length()) {
                final String substring = this.fullName.substring(index + 1);
                if ("plain".equals(substring)) {
                    this.style = 0;
                }
                else if ("bold".equals(substring)) {
                    this.style = 1;
                }
                else if ("italic".equals(substring)) {
                    this.style = 2;
                }
                else if ("bolditalic".equals(substring)) {
                    this.style = 3;
                }
            }
        }
        else {
            this.familyName = this.fullName;
        }
    }
    
    CompositeFont(final PhysicalFont[] array) {
        this.numGlyphs = 0;
        this.localeSlot = -1;
        this.isStdComposite = true;
        this.isStdComposite = false;
        this.handle = new Font2DHandle(this);
        this.fullName = array[0].fullName;
        this.familyName = array[0].familyName;
        this.style = array[0].style;
        this.numMetricsSlots = 1;
        this.numSlots = array.length;
        System.arraycopy(array, 0, this.components = new PhysicalFont[this.numSlots], 0, this.numSlots);
        this.deferredInitialisation = new boolean[this.numSlots];
    }
    
    CompositeFont(final PhysicalFont physicalFont, final CompositeFont compositeFont) {
        this.numGlyphs = 0;
        this.localeSlot = -1;
        this.isStdComposite = true;
        this.isStdComposite = false;
        this.handle = new Font2DHandle(this);
        this.fullName = physicalFont.fullName;
        this.familyName = physicalFont.familyName;
        this.style = physicalFont.style;
        this.numMetricsSlots = 1;
        this.numSlots = compositeFont.numSlots + 1;
        synchronized (FontManagerFactory.getInstance()) {
            (this.components = new PhysicalFont[this.numSlots])[0] = physicalFont;
            System.arraycopy(compositeFont.components, 0, this.components, 1, compositeFont.numSlots);
            if (compositeFont.componentNames != null) {
                (this.componentNames = new String[this.numSlots])[0] = physicalFont.fullName;
                System.arraycopy(compositeFont.componentNames, 0, this.componentNames, 1, compositeFont.numSlots);
            }
            if (compositeFont.componentFileNames != null) {
                (this.componentFileNames = new String[this.numSlots])[0] = null;
                System.arraycopy(compositeFont.componentFileNames, 0, this.componentFileNames, 1, compositeFont.numSlots);
            }
            (this.deferredInitialisation = new boolean[this.numSlots])[0] = false;
            System.arraycopy(compositeFont.deferredInitialisation, 0, this.deferredInitialisation, 1, compositeFont.numSlots);
        }
    }
    
    private void doDeferredInitialisation(final int n) {
        if (!this.deferredInitialisation[n]) {
            return;
        }
        final SunFontManager instance = SunFontManager.getInstance();
        synchronized (instance) {
            if (this.componentNames == null) {
                this.componentNames = new String[this.numSlots];
            }
            if (this.components[n] == null) {
                if (this.componentFileNames != null && this.componentFileNames[n] != null) {
                    this.components[n] = instance.initialiseDeferredFont(this.componentFileNames[n]);
                }
                if (this.components[n] == null) {
                    this.components[n] = instance.getDefaultPhysicalFont();
                }
                final String fontName = this.components[n].getFontName(null);
                if (this.componentNames[n] == null) {
                    this.componentNames[n] = fontName;
                }
                else if (!this.componentNames[n].equalsIgnoreCase(fontName)) {
                    try {
                        this.components[n] = (PhysicalFont)instance.findFont2D(this.componentNames[n], this.style, 1);
                    }
                    catch (final ClassCastException ex) {
                        this.components[n] = instance.getDefaultPhysicalFont();
                    }
                }
            }
            this.deferredInitialisation[n] = false;
        }
    }
    
    void replaceComponentFont(final PhysicalFont physicalFont, final PhysicalFont physicalFont2) {
        if (this.components == null) {
            return;
        }
        for (int i = 0; i < this.numSlots; ++i) {
            if (this.components[i] == physicalFont) {
                this.components[i] = physicalFont2;
                if (this.componentNames != null) {
                    this.componentNames[i] = physicalFont2.getFontName(null);
                }
            }
        }
    }
    
    public boolean isExcludedChar(final int n, final int n2) {
        if (this.exclusionRanges == null || this.maxIndices == null || n >= this.numMetricsSlots) {
            return false;
        }
        int n3 = 0;
        final int i = this.maxIndices[n];
        if (n > 0) {
            n3 = this.maxIndices[n - 1];
        }
        for (int n4 = n3; i > n4; n4 += 2) {
            if (n2 >= this.exclusionRanges[n4] && n2 <= this.exclusionRanges[n4 + 1]) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void getStyleMetrics(final float n, final float[] array, final int n2) {
        final PhysicalFont slotFont = this.getSlotFont(0);
        if (slotFont == null) {
            super.getStyleMetrics(n, array, n2);
        }
        else {
            slotFont.getStyleMetrics(n, array, n2);
        }
    }
    
    public int getNumSlots() {
        return this.numSlots;
    }
    
    public PhysicalFont getSlotFont(final int n) {
        if (this.deferredInitialisation[n]) {
            this.doDeferredInitialisation(n);
        }
        final SunFontManager instance = SunFontManager.getInstance();
        try {
            PhysicalFont defaultPhysicalFont = this.components[n];
            if (defaultPhysicalFont == null) {
                try {
                    defaultPhysicalFont = (PhysicalFont)instance.findFont2D(this.componentNames[n], this.style, 1);
                    this.components[n] = defaultPhysicalFont;
                }
                catch (final ClassCastException ex) {
                    defaultPhysicalFont = instance.getDefaultPhysicalFont();
                }
            }
            return defaultPhysicalFont;
        }
        catch (final Exception ex2) {
            return instance.getDefaultPhysicalFont();
        }
    }
    
    @Override
    FontStrike createStrike(final FontStrikeDesc fontStrikeDesc) {
        return new CompositeStrike(this, fontStrikeDesc);
    }
    
    public boolean isStdComposite() {
        return this.isStdComposite;
    }
    
    @Override
    protected int getValidatedGlyphCode(final int n) {
        final int n2 = n >>> 24;
        if (n2 >= this.numSlots) {
            return this.getMapper().getMissingGlyphCode();
        }
        final int n3 = n & 0xFFFFFF;
        final PhysicalFont slotFont = this.getSlotFont(n2);
        if (slotFont.getValidatedGlyphCode(n3) == slotFont.getMissingGlyphCode()) {
            return this.getMapper().getMissingGlyphCode();
        }
        return n;
    }
    
    public CharToGlyphMapper getMapper() {
        if (this.mapper == null) {
            this.mapper = new CompositeGlyphMapper(this);
        }
        return this.mapper;
    }
    
    @Override
    public boolean hasSupplementaryChars() {
        for (int i = 0; i < this.numSlots; ++i) {
            if (this.getSlotFont(i).hasSupplementaryChars()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getNumGlyphs() {
        if (this.numGlyphs == 0) {
            this.numGlyphs = this.getMapper().getNumGlyphs();
        }
        return this.numGlyphs;
    }
    
    @Override
    public int getMissingGlyphCode() {
        return this.getMapper().getMissingGlyphCode();
    }
    
    @Override
    public boolean canDisplay(final char c) {
        return this.getMapper().canDisplay(c);
    }
    
    @Override
    public boolean useAAForPtSize(final int n) {
        if (this.localeSlot == -1) {
            int n2 = this.numMetricsSlots;
            if (n2 == 1 && !this.isStdComposite()) {
                n2 = this.numSlots;
            }
            for (int i = 0; i < n2; ++i) {
                if (this.getSlotFont(i).supportsEncoding(null)) {
                    this.localeSlot = i;
                    break;
                }
            }
            if (this.localeSlot == -1) {
                this.localeSlot = 0;
            }
        }
        return this.getSlotFont(this.localeSlot).useAAForPtSize(n);
    }
    
    @Override
    public String toString() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("line.separator"));
        String string = "";
        for (int i = 0; i < this.numSlots; ++i) {
            string = string + "    Slot[" + i + "]=" + this.getSlotFont(i) + s;
        }
        return "** Composite Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + s + string;
    }
}
