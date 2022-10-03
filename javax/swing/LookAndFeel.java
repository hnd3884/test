package javax.swing;

import sun.swing.ImageIconUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.ColorUIResource;
import java.awt.Toolkit;
import java.awt.Component;
import sun.swing.DefaultLayoutStyle;
import sun.swing.SwingUtilities2;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.text.JTextComponent;
import sun.awt.SunToolkit;
import javax.swing.border.Border;
import java.awt.Font;
import java.awt.Color;
import javax.swing.plaf.UIResource;

public abstract class LookAndFeel
{
    public static void installColors(final JComponent component, final String s, final String s2) {
        final Color background = component.getBackground();
        if (background == null || background instanceof UIResource) {
            component.setBackground(UIManager.getColor(s));
        }
        final Color foreground = component.getForeground();
        if (foreground == null || foreground instanceof UIResource) {
            component.setForeground(UIManager.getColor(s2));
        }
    }
    
    public static void installColorsAndFont(final JComponent component, final String s, final String s2, final String s3) {
        final Font font = component.getFont();
        if (font == null || font instanceof UIResource) {
            component.setFont(UIManager.getFont(s3));
        }
        installColors(component, s, s2);
    }
    
    public static void installBorder(final JComponent component, final String s) {
        final Border border = component.getBorder();
        if (border == null || border instanceof UIResource) {
            component.setBorder(UIManager.getBorder(s));
        }
    }
    
    public static void uninstallBorder(final JComponent component) {
        if (component.getBorder() instanceof UIResource) {
            component.setBorder(null);
        }
    }
    
    public static void installProperty(final JComponent component, final String s, final Object o) {
        if (SunToolkit.isInstanceOf(component, "javax.swing.JPasswordField")) {
            if (!((JPasswordField)component).customSetUIProperty(s, o)) {
                component.setUIProperty(s, o);
            }
        }
        else {
            component.setUIProperty(s, o);
        }
    }
    
    public static JTextComponent.KeyBinding[] makeKeyBindings(final Object[] array) {
        final JTextComponent.KeyBinding[] array2 = new JTextComponent.KeyBinding[array.length / 2];
        for (int i = 0; i < array2.length; ++i) {
            final Object o = array[2 * i];
            array2[i] = new JTextComponent.KeyBinding((o instanceof KeyStroke) ? ((KeyStroke)o) : KeyStroke.getKeyStroke((String)o), (String)array[2 * i + 1]);
        }
        return array2;
    }
    
    public static InputMap makeInputMap(final Object[] array) {
        final InputMapUIResource inputMapUIResource = new InputMapUIResource();
        loadKeyBindings(inputMapUIResource, array);
        return inputMapUIResource;
    }
    
    public static ComponentInputMap makeComponentInputMap(final JComponent component, final Object[] array) {
        final ComponentInputMapUIResource componentInputMapUIResource = new ComponentInputMapUIResource(component);
        loadKeyBindings(componentInputMapUIResource, array);
        return componentInputMapUIResource;
    }
    
    public static void loadKeyBindings(final InputMap inputMap, final Object[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                final Object o = array[i++];
                inputMap.put((o instanceof KeyStroke) ? ((KeyStroke)o) : KeyStroke.getKeyStroke((String)o), array[i]);
            }
        }
    }
    
    public static Object makeIcon(final Class<?> clazz, final String s) {
        return SwingUtilities2.makeIcon(clazz, clazz, s);
    }
    
    public LayoutStyle getLayoutStyle() {
        return DefaultLayoutStyle.getInstance();
    }
    
    public void provideErrorFeedback(final Component component) {
        Toolkit toolkit;
        if (component != null) {
            toolkit = component.getToolkit();
        }
        else {
            toolkit = Toolkit.getDefaultToolkit();
        }
        toolkit.beep();
    }
    
    public static Object getDesktopPropertyValue(final String s, final Object o) {
        final Object desktopProperty = Toolkit.getDefaultToolkit().getDesktopProperty(s);
        if (desktopProperty == null) {
            return o;
        }
        if (desktopProperty instanceof Color) {
            return new ColorUIResource((Color)desktopProperty);
        }
        if (desktopProperty instanceof Font) {
            return new FontUIResource((Font)desktopProperty);
        }
        return desktopProperty;
    }
    
    public Icon getDisabledIcon(final JComponent component, final Icon icon) {
        if (icon instanceof ImageIcon) {
            return new ImageIconUIResource(GrayFilter.createDisabledImage(((ImageIcon)icon).getImage()));
        }
        return null;
    }
    
    public Icon getDisabledSelectedIcon(final JComponent component, final Icon icon) {
        return this.getDisabledIcon(component, icon);
    }
    
    public abstract String getName();
    
    public abstract String getID();
    
    public abstract String getDescription();
    
    public boolean getSupportsWindowDecorations() {
        return false;
    }
    
    public abstract boolean isNativeLookAndFeel();
    
    public abstract boolean isSupportedLookAndFeel();
    
    public void initialize() {
    }
    
    public void uninitialize() {
    }
    
    public UIDefaults getDefaults() {
        return null;
    }
    
    @Override
    public String toString() {
        return "[" + this.getDescription() + " - " + this.getClass().getName() + "]";
    }
}
