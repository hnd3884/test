package java.awt;

import sun.awt.AWTAccessor;
import java.io.ObjectStreamException;
import java.io.Serializable;

public final class SystemColor extends Color implements Serializable
{
    public static final int DESKTOP = 0;
    public static final int ACTIVE_CAPTION = 1;
    public static final int ACTIVE_CAPTION_TEXT = 2;
    public static final int ACTIVE_CAPTION_BORDER = 3;
    public static final int INACTIVE_CAPTION = 4;
    public static final int INACTIVE_CAPTION_TEXT = 5;
    public static final int INACTIVE_CAPTION_BORDER = 6;
    public static final int WINDOW = 7;
    public static final int WINDOW_BORDER = 8;
    public static final int WINDOW_TEXT = 9;
    public static final int MENU = 10;
    public static final int MENU_TEXT = 11;
    public static final int TEXT = 12;
    public static final int TEXT_TEXT = 13;
    public static final int TEXT_HIGHLIGHT = 14;
    public static final int TEXT_HIGHLIGHT_TEXT = 15;
    public static final int TEXT_INACTIVE_TEXT = 16;
    public static final int CONTROL = 17;
    public static final int CONTROL_TEXT = 18;
    public static final int CONTROL_HIGHLIGHT = 19;
    public static final int CONTROL_LT_HIGHLIGHT = 20;
    public static final int CONTROL_SHADOW = 21;
    public static final int CONTROL_DK_SHADOW = 22;
    public static final int SCROLLBAR = 23;
    public static final int INFO = 24;
    public static final int INFO_TEXT = 25;
    public static final int NUM_COLORS = 26;
    private static int[] systemColors;
    public static final SystemColor desktop;
    public static final SystemColor activeCaption;
    public static final SystemColor activeCaptionText;
    public static final SystemColor activeCaptionBorder;
    public static final SystemColor inactiveCaption;
    public static final SystemColor inactiveCaptionText;
    public static final SystemColor inactiveCaptionBorder;
    public static final SystemColor window;
    public static final SystemColor windowBorder;
    public static final SystemColor windowText;
    public static final SystemColor menu;
    public static final SystemColor menuText;
    public static final SystemColor text;
    public static final SystemColor textText;
    public static final SystemColor textHighlight;
    public static final SystemColor textHighlightText;
    public static final SystemColor textInactiveText;
    public static final SystemColor control;
    public static final SystemColor controlText;
    public static final SystemColor controlHighlight;
    public static final SystemColor controlLtHighlight;
    public static final SystemColor controlShadow;
    public static final SystemColor controlDkShadow;
    public static final SystemColor scrollbar;
    public static final SystemColor info;
    public static final SystemColor infoText;
    private static final long serialVersionUID = 4503142729533789064L;
    private transient int index;
    private static SystemColor[] systemColorObjects;
    
    private static void updateSystemColors() {
        if (!GraphicsEnvironment.isHeadless()) {
            Toolkit.getDefaultToolkit().loadSystemColors(SystemColor.systemColors);
        }
        for (int i = 0; i < SystemColor.systemColors.length; ++i) {
            SystemColor.systemColorObjects[i].value = SystemColor.systemColors[i];
        }
    }
    
    private SystemColor(final byte index) {
        super(SystemColor.systemColors[index]);
        this.index = index;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[i=" + this.index + "]";
    }
    
    private Object readResolve() {
        return SystemColor.systemColorObjects[this.value];
    }
    
    private Object writeReplace() throws ObjectStreamException {
        final SystemColor systemColor = new SystemColor((byte)this.index);
        systemColor.value = this.index;
        return systemColor;
    }
    
    static {
        SystemColor.systemColors = new int[] { -16753572, -16777088, -1, -4144960, -8355712, -4144960, -4144960, -1, -16777216, -16777216, -4144960, -16777216, -4144960, -16777216, -16777088, -1, -8355712, -4144960, -16777216, -1, -2039584, -8355712, -16777216, -2039584, -2039808, -16777216 };
        desktop = new SystemColor((byte)0);
        activeCaption = new SystemColor((byte)1);
        activeCaptionText = new SystemColor((byte)2);
        activeCaptionBorder = new SystemColor((byte)3);
        inactiveCaption = new SystemColor((byte)4);
        inactiveCaptionText = new SystemColor((byte)5);
        inactiveCaptionBorder = new SystemColor((byte)6);
        window = new SystemColor((byte)7);
        windowBorder = new SystemColor((byte)8);
        windowText = new SystemColor((byte)9);
        menu = new SystemColor((byte)10);
        menuText = new SystemColor((byte)11);
        text = new SystemColor((byte)12);
        textText = new SystemColor((byte)13);
        textHighlight = new SystemColor((byte)14);
        textHighlightText = new SystemColor((byte)15);
        textInactiveText = new SystemColor((byte)16);
        control = new SystemColor((byte)17);
        controlText = new SystemColor((byte)18);
        controlHighlight = new SystemColor((byte)19);
        controlLtHighlight = new SystemColor((byte)20);
        controlShadow = new SystemColor((byte)21);
        controlDkShadow = new SystemColor((byte)22);
        scrollbar = new SystemColor((byte)23);
        info = new SystemColor((byte)24);
        infoText = new SystemColor((byte)25);
        SystemColor.systemColorObjects = new SystemColor[] { SystemColor.desktop, SystemColor.activeCaption, SystemColor.activeCaptionText, SystemColor.activeCaptionBorder, SystemColor.inactiveCaption, SystemColor.inactiveCaptionText, SystemColor.inactiveCaptionBorder, SystemColor.window, SystemColor.windowBorder, SystemColor.windowText, SystemColor.menu, SystemColor.menuText, SystemColor.text, SystemColor.textText, SystemColor.textHighlight, SystemColor.textHighlightText, SystemColor.textInactiveText, SystemColor.control, SystemColor.controlText, SystemColor.controlHighlight, SystemColor.controlLtHighlight, SystemColor.controlShadow, SystemColor.controlDkShadow, SystemColor.scrollbar, SystemColor.info, SystemColor.infoText };
        AWTAccessor.setSystemColorAccessor(SystemColor::updateSystemColors);
        updateSystemColors();
    }
}
