package java.awt;

import java.util.ResourceBundle;
import java.util.Locale;
import java.io.Serializable;

public final class ComponentOrientation implements Serializable
{
    private static final long serialVersionUID = -4113291392143563828L;
    private static final int UNK_BIT = 1;
    private static final int HORIZ_BIT = 2;
    private static final int LTR_BIT = 4;
    public static final ComponentOrientation LEFT_TO_RIGHT;
    public static final ComponentOrientation RIGHT_TO_LEFT;
    public static final ComponentOrientation UNKNOWN;
    private int orientation;
    
    public boolean isHorizontal() {
        return (this.orientation & 0x2) != 0x0;
    }
    
    public boolean isLeftToRight() {
        return (this.orientation & 0x4) != 0x0;
    }
    
    public static ComponentOrientation getOrientation(final Locale locale) {
        final String language = locale.getLanguage();
        if ("iw".equals(language) || "ar".equals(language) || "fa".equals(language) || "ur".equals(language)) {
            return ComponentOrientation.RIGHT_TO_LEFT;
        }
        return ComponentOrientation.LEFT_TO_RIGHT;
    }
    
    @Deprecated
    public static ComponentOrientation getOrientation(final ResourceBundle resourceBundle) {
        ComponentOrientation componentOrientation = null;
        try {
            componentOrientation = (ComponentOrientation)resourceBundle.getObject("Orientation");
        }
        catch (final Exception ex) {}
        if (componentOrientation == null) {
            componentOrientation = getOrientation(resourceBundle.getLocale());
        }
        if (componentOrientation == null) {
            componentOrientation = getOrientation(Locale.getDefault());
        }
        return componentOrientation;
    }
    
    private ComponentOrientation(final int orientation) {
        this.orientation = orientation;
    }
    
    static {
        LEFT_TO_RIGHT = new ComponentOrientation(6);
        RIGHT_TO_LEFT = new ComponentOrientation(2);
        UNKNOWN = new ComponentOrientation(7);
    }
}
