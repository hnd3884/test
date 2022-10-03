package sun.awt.windows;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.awt.event.InvocationEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputMethodEvent;
import java.awt.font.TextHitInfo;
import sun.awt.SunToolkit;
import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.awt.im.InputMethodHighlight;
import java.awt.event.ComponentEvent;
import java.awt.AWTEvent;
import java.awt.im.InputSubset;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.Locale;
import java.awt.Component;
import java.awt.im.spi.InputMethodContext;
import sun.awt.im.InputMethodAdapter;

final class WInputMethod extends InputMethodAdapter
{
    private InputMethodContext inputContext;
    private Component awtFocussedComponent;
    private WComponentPeer awtFocussedComponentPeer;
    private WComponentPeer lastFocussedComponentPeer;
    private boolean isLastFocussedActiveClient;
    private boolean isActive;
    private int context;
    private boolean open;
    private int cmode;
    private Locale currentLocale;
    private boolean statusWindowHidden;
    public static final byte ATTR_INPUT = 0;
    public static final byte ATTR_TARGET_CONVERTED = 1;
    public static final byte ATTR_CONVERTED = 2;
    public static final byte ATTR_TARGET_NOTCONVERTED = 3;
    public static final byte ATTR_INPUT_ERROR = 4;
    public static final int IME_CMODE_ALPHANUMERIC = 0;
    public static final int IME_CMODE_NATIVE = 1;
    public static final int IME_CMODE_KATAKANA = 2;
    public static final int IME_CMODE_LANGUAGE = 3;
    public static final int IME_CMODE_FULLSHAPE = 8;
    public static final int IME_CMODE_HANJACONVERT = 64;
    public static final int IME_CMODE_ROMAN = 16;
    private static final boolean COMMIT_INPUT = true;
    private static final boolean DISCARD_INPUT = false;
    private static Map<TextAttribute, Object>[] highlightStyles;
    
    public WInputMethod() {
        this.awtFocussedComponentPeer = null;
        this.lastFocussedComponentPeer = null;
        this.isLastFocussedActiveClient = false;
        this.statusWindowHidden = false;
        this.context = this.createNativeContext();
        this.cmode = this.getConversionStatus(this.context);
        this.open = this.getOpenStatus(this.context);
        this.currentLocale = getNativeLocale();
        if (this.currentLocale == null) {
            this.currentLocale = Locale.getDefault();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (this.context != 0) {
            this.destroyNativeContext(this.context);
            this.context = 0;
        }
        super.finalize();
    }
    
    @Override
    public synchronized void setInputMethodContext(final InputMethodContext inputContext) {
        this.inputContext = inputContext;
    }
    
    @Override
    public final void dispose() {
    }
    
    @Override
    public Object getControlObject() {
        return null;
    }
    
    @Override
    public boolean setLocale(final Locale locale) {
        return this.setLocale(locale, false);
    }
    
    private boolean setLocale(final Locale locale, final boolean b) {
        final Locale[] availableLocalesInternal = WInputMethodDescriptor.getAvailableLocalesInternal();
        for (int i = 0; i < availableLocalesInternal.length; ++i) {
            final Locale currentLocale = availableLocalesInternal[i];
            if (locale.equals(currentLocale) || (currentLocale.equals(Locale.JAPAN) && locale.equals(Locale.JAPANESE)) || (currentLocale.equals(Locale.KOREA) && locale.equals(Locale.KOREAN))) {
                if (this.isActive) {
                    setNativeLocale(currentLocale.toLanguageTag(), b);
                }
                this.currentLocale = currentLocale;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Locale getLocale() {
        if (this.isActive) {
            this.currentLocale = getNativeLocale();
            if (this.currentLocale == null) {
                this.currentLocale = Locale.getDefault();
            }
        }
        return this.currentLocale;
    }
    
    @Override
    public void setCharacterSubsets(final Character.Subset[] array) {
        if (array == null) {
            this.setConversionStatus(this.context, this.cmode);
            this.setOpenStatus(this.context, this.open);
            return;
        }
        final Character.Subset subset = array[0];
        final Locale nativeLocale = getNativeLocale();
        if (nativeLocale == null) {
            return;
        }
        if (nativeLocale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
            if (subset == Character.UnicodeBlock.BASIC_LATIN || subset == InputSubset.LATIN_DIGITS) {
                this.setOpenStatus(this.context, false);
            }
            else {
                int n;
                if (subset == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || subset == InputSubset.KANJI || subset == Character.UnicodeBlock.HIRAGANA) {
                    n = 9;
                }
                else if (subset == Character.UnicodeBlock.KATAKANA) {
                    n = 11;
                }
                else if (subset == InputSubset.HALFWIDTH_KATAKANA) {
                    n = 3;
                }
                else {
                    if (subset != InputSubset.FULLWIDTH_LATIN) {
                        return;
                    }
                    n = 8;
                }
                this.setOpenStatus(this.context, true);
                this.setConversionStatus(this.context, n | (this.getConversionStatus(this.context) & 0x10));
            }
        }
        else if (nativeLocale.getLanguage().equals(Locale.KOREAN.getLanguage())) {
            if (subset == Character.UnicodeBlock.BASIC_LATIN || subset == InputSubset.LATIN_DIGITS) {
                this.setOpenStatus(this.context, false);
            }
            else {
                int n2;
                if (subset == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || subset == InputSubset.HANJA || subset == Character.UnicodeBlock.HANGUL_SYLLABLES || subset == Character.UnicodeBlock.HANGUL_JAMO || subset == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
                    n2 = 1;
                }
                else {
                    if (subset != InputSubset.FULLWIDTH_LATIN) {
                        return;
                    }
                    n2 = 8;
                }
                this.setOpenStatus(this.context, true);
                this.setConversionStatus(this.context, n2);
            }
        }
        else if (nativeLocale.getLanguage().equals(Locale.CHINESE.getLanguage())) {
            if (subset == Character.UnicodeBlock.BASIC_LATIN || subset == InputSubset.LATIN_DIGITS) {
                this.setOpenStatus(this.context, false);
            }
            else {
                int n3;
                if (subset == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || subset == InputSubset.TRADITIONAL_HANZI || subset == InputSubset.SIMPLIFIED_HANZI) {
                    n3 = 1;
                }
                else {
                    if (subset != InputSubset.FULLWIDTH_LATIN) {
                        return;
                    }
                    n3 = 8;
                }
                this.setOpenStatus(this.context, true);
                this.setConversionStatus(this.context, n3);
            }
        }
    }
    
    @Override
    public void dispatchEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof ComponentEvent) {
            final Component component = ((ComponentEvent)awtEvent).getComponent();
            if (component == this.awtFocussedComponent) {
                if (this.awtFocussedComponentPeer == null || this.awtFocussedComponentPeer.isDisposed()) {
                    this.awtFocussedComponentPeer = this.getNearestNativePeer(component);
                }
                if (this.awtFocussedComponentPeer != null) {
                    this.handleNativeIMEEvent(this.awtFocussedComponentPeer, awtEvent);
                }
            }
        }
    }
    
    @Override
    public void activate() {
        final boolean haveActiveClient = this.haveActiveClient();
        if (this.lastFocussedComponentPeer != this.awtFocussedComponentPeer || this.isLastFocussedActiveClient != haveActiveClient) {
            if (this.lastFocussedComponentPeer != null) {
                this.disableNativeIME(this.lastFocussedComponentPeer);
            }
            if (this.awtFocussedComponentPeer != null) {
                this.enableNativeIME(this.awtFocussedComponentPeer, this.context, !haveActiveClient);
            }
            this.lastFocussedComponentPeer = this.awtFocussedComponentPeer;
            this.isLastFocussedActiveClient = haveActiveClient;
        }
        this.isActive = true;
        if (this.currentLocale != null) {
            this.setLocale(this.currentLocale, true);
        }
        if (this.statusWindowHidden) {
            this.setStatusWindowVisible(this.awtFocussedComponentPeer, true);
            this.statusWindowHidden = false;
        }
    }
    
    @Override
    public void deactivate(final boolean b) {
        this.getLocale();
        if (this.awtFocussedComponentPeer != null) {
            this.lastFocussedComponentPeer = this.awtFocussedComponentPeer;
            this.isLastFocussedActiveClient = this.haveActiveClient();
        }
        this.isActive = false;
    }
    
    @Override
    public void disableInputMethod() {
        if (this.lastFocussedComponentPeer != null) {
            this.disableNativeIME(this.lastFocussedComponentPeer);
            this.lastFocussedComponentPeer = null;
            this.isLastFocussedActiveClient = false;
        }
    }
    
    @Override
    public String getNativeInputMethodInfo() {
        return this.getNativeIMMDescription();
    }
    
    @Override
    protected void stopListening() {
        this.disableInputMethod();
    }
    
    @Override
    protected void setAWTFocussedComponent(final Component awtFocussedComponent) {
        if (awtFocussedComponent == null) {
            return;
        }
        final WComponentPeer nearestNativePeer = this.getNearestNativePeer(awtFocussedComponent);
        if (this.isActive) {
            if (this.awtFocussedComponentPeer != null) {
                this.disableNativeIME(this.awtFocussedComponentPeer);
            }
            if (nearestNativePeer != null) {
                this.enableNativeIME(nearestNativePeer, this.context, !this.haveActiveClient());
            }
        }
        this.awtFocussedComponent = awtFocussedComponent;
        this.awtFocussedComponentPeer = nearestNativePeer;
    }
    
    @Override
    public void hideWindows() {
        if (this.awtFocussedComponentPeer != null) {
            this.setStatusWindowVisible(this.awtFocussedComponentPeer, false);
            this.statusWindowHidden = true;
        }
    }
    
    @Override
    public void removeNotify() {
        this.endCompositionNative(this.context, false);
        this.awtFocussedComponent = null;
        this.awtFocussedComponentPeer = null;
    }
    
    static Map<TextAttribute, ?> mapInputMethodHighlight(final InputMethodHighlight inputMethodHighlight) {
        final int state = inputMethodHighlight.getState();
        int n;
        if (state == 0) {
            n = 0;
        }
        else {
            if (state != 1) {
                return null;
            }
            n = 2;
        }
        if (inputMethodHighlight.isSelected()) {
            ++n;
        }
        return WInputMethod.highlightStyles[n];
    }
    
    @Override
    protected boolean supportsBelowTheSpot() {
        return true;
    }
    
    @Override
    public void endComposition() {
        this.endCompositionNative(this.context, this.haveActiveClient());
    }
    
    @Override
    public void setCompositionEnabled(final boolean b) {
        this.setOpenStatus(this.context, b);
    }
    
    @Override
    public boolean isCompositionEnabled() {
        return this.getOpenStatus(this.context);
    }
    
    public void sendInputMethodEvent(final int n, final long n2, final String s, final int[] array, final String[] array2, final int[] array3, final byte[] array4, final int n3, final int n4, final int n5) {
        AttributedCharacterIterator iterator = null;
        if (s != null) {
            final AttributedString attributedString = new AttributedString(s);
            attributedString.addAttribute(AttributedCharacterIterator.Attribute.LANGUAGE, Locale.getDefault(), 0, s.length());
            if (array != null && array2 != null && array2.length != 0 && array.length == array2.length + 1 && array[0] == 0 && array[array2.length] <= s.length()) {
                for (int i = 0; i < array.length - 1; ++i) {
                    attributedString.addAttribute(AttributedCharacterIterator.Attribute.INPUT_METHOD_SEGMENT, new Annotation(null), array[i], array[i + 1]);
                    attributedString.addAttribute(AttributedCharacterIterator.Attribute.READING, new Annotation(array2[i]), array[i], array[i + 1]);
                }
            }
            else {
                attributedString.addAttribute(AttributedCharacterIterator.Attribute.INPUT_METHOD_SEGMENT, new Annotation(null), 0, s.length());
                attributedString.addAttribute(AttributedCharacterIterator.Attribute.READING, new Annotation(""), 0, s.length());
            }
            if (array3 != null && array4 != null && array4.length != 0 && array3.length == array4.length + 1 && array3[0] == 0 && array3[array4.length] == s.length()) {
                for (int j = 0; j < array3.length - 1; ++j) {
                    InputMethodHighlight inputMethodHighlight = null;
                    switch (array4[j]) {
                        case 1: {
                            inputMethodHighlight = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
                            break;
                        }
                        case 2: {
                            inputMethodHighlight = InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
                            break;
                        }
                        case 3: {
                            inputMethodHighlight = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
                            break;
                        }
                        default: {
                            inputMethodHighlight = InputMethodHighlight.UNSELECTED_RAW_TEXT_HIGHLIGHT;
                            break;
                        }
                    }
                    attributedString.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, inputMethodHighlight, array3[j], array3[j + 1]);
                }
            }
            else {
                attributedString.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT, 0, s.length());
            }
            iterator = attributedString.getIterator();
        }
        final Component clientComponent = this.getClientComponent();
        if (clientComponent == null) {
            return;
        }
        SunToolkit.postEvent(SunToolkit.targetToAppContext(clientComponent), new InputMethodEvent(clientComponent, n, n2, iterator, n3, TextHitInfo.leading(n4), TextHitInfo.leading(n5)));
    }
    
    public void inquireCandidatePosition() {
        final Component clientComponent = this.getClientComponent();
        if (clientComponent == null) {
            return;
        }
        SunToolkit.postEvent(SunToolkit.targetToAppContext(clientComponent), new InvocationEvent(clientComponent, new Runnable() {
            @Override
            public void run() {
                int n = 0;
                int n2 = 0;
                final Component access$000 = InputMethodAdapter.this.getClientComponent();
                if (access$000 != null) {
                    if (!access$000.isShowing()) {
                        return;
                    }
                    if (InputMethodAdapter.this.haveActiveClient()) {
                        final Rectangle textLocation = WInputMethod.this.inputContext.getTextLocation(TextHitInfo.leading(0));
                        n = textLocation.x;
                        n2 = textLocation.y + textLocation.height;
                    }
                    else {
                        final Point locationOnScreen = access$000.getLocationOnScreen();
                        final Dimension size = access$000.getSize();
                        n = locationOnScreen.x;
                        n2 = locationOnScreen.y + size.height;
                    }
                }
                WInputMethod.this.openCandidateWindow(WInputMethod.this.awtFocussedComponentPeer, n, n2);
            }
        }));
    }
    
    private WComponentPeer getNearestNativePeer(Component parent) {
        if (parent == null) {
            return null;
        }
        ComponentPeer componentPeer = parent.getPeer();
        if (componentPeer == null) {
            return null;
        }
        while (componentPeer instanceof LightweightPeer) {
            parent = parent.getParent();
            if (parent == null) {
                return null;
            }
            componentPeer = parent.getPeer();
            if (componentPeer == null) {
                return null;
            }
        }
        if (componentPeer instanceof WComponentPeer) {
            return (WComponentPeer)componentPeer;
        }
        return null;
    }
    
    private native int createNativeContext();
    
    private native void destroyNativeContext(final int p0);
    
    private native void enableNativeIME(final WComponentPeer p0, final int p1, final boolean p2);
    
    private native void disableNativeIME(final WComponentPeer p0);
    
    private native void handleNativeIMEEvent(final WComponentPeer p0, final AWTEvent p1);
    
    private native void endCompositionNative(final int p0, final boolean p1);
    
    private native void setConversionStatus(final int p0, final int p1);
    
    private native int getConversionStatus(final int p0);
    
    private native void setOpenStatus(final int p0, final boolean p1);
    
    private native boolean getOpenStatus(final int p0);
    
    private native void setStatusWindowVisible(final WComponentPeer p0, final boolean p1);
    
    private native String getNativeIMMDescription();
    
    static native Locale getNativeLocale();
    
    static native boolean setNativeLocale(final String p0, final boolean p1);
    
    private native void openCandidateWindow(final WComponentPeer p0, final int p1, final int p2);
    
    static {
        final Map[] highlightStyles = new Map[4];
        final HashMap hashMap = new HashMap(1);
        hashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
        highlightStyles[0] = Collections.unmodifiableMap((Map<?, ?>)hashMap);
        final HashMap hashMap2 = new HashMap(1);
        hashMap2.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
        highlightStyles[1] = Collections.unmodifiableMap((Map<?, ?>)hashMap2);
        final HashMap hashMap3 = new HashMap(1);
        hashMap3.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
        highlightStyles[2] = Collections.unmodifiableMap((Map<?, ?>)hashMap3);
        final HashMap hashMap4 = new HashMap(4);
        hashMap4.put(TextAttribute.FOREGROUND, new Color(0, 0, 128));
        hashMap4.put(TextAttribute.BACKGROUND, Color.white);
        hashMap4.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
        hashMap4.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        highlightStyles[3] = Collections.unmodifiableMap((Map<?, ?>)hashMap4);
        WInputMethod.highlightStyles = highlightStyles;
    }
}
