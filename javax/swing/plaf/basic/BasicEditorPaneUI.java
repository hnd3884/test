package javax.swing.plaf.basic;

import javax.swing.plaf.UIResource;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import sun.swing.SwingUtilities2;
import javax.swing.text.Style;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingUtilities;
import java.beans.PropertyChangeEvent;
import javax.swing.TransferHandler;
import javax.swing.Action;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class BasicEditorPaneUI extends BasicTextUI
{
    private static final String FONT_ATTRIBUTE_KEY = "FONT_ATTRIBUTE_KEY";
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicEditorPaneUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "EditorPane";
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        this.updateDisplayProperties(component.getFont(), component.getForeground());
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.cleanDisplayProperties();
        super.uninstallUI(component);
    }
    
    @Override
    public EditorKit getEditorKit(final JTextComponent textComponent) {
        return ((JEditorPane)this.getComponent()).getEditorKit();
    }
    
    @Override
    ActionMap getActionMap() {
        final ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
        actionMapUIResource.put("requestFocus", new FocusAction());
        final EditorKit editorKit = this.getEditorKit(this.getComponent());
        if (editorKit != null) {
            final Action[] actions = editorKit.getActions();
            if (actions != null) {
                this.addActions(actionMapUIResource, actions);
            }
        }
        actionMapUIResource.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
        actionMapUIResource.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
        actionMapUIResource.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
        return actionMapUIResource;
    }
    
    @Override
    protected void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        super.propertyChange(propertyChangeEvent);
        final String propertyName = propertyChangeEvent.getPropertyName();
        if ("editorKit".equals(propertyName)) {
            final ActionMap uiActionMap = SwingUtilities.getUIActionMap(this.getComponent());
            if (uiActionMap != null) {
                final Object oldValue = propertyChangeEvent.getOldValue();
                if (oldValue instanceof EditorKit) {
                    final Action[] actions = ((EditorKit)oldValue).getActions();
                    if (actions != null) {
                        this.removeActions(uiActionMap, actions);
                    }
                }
                final Object newValue = propertyChangeEvent.getNewValue();
                if (newValue instanceof EditorKit) {
                    final Action[] actions2 = ((EditorKit)newValue).getActions();
                    if (actions2 != null) {
                        this.addActions(uiActionMap, actions2);
                    }
                }
            }
            this.updateFocusTraversalKeys();
        }
        else if ("editable".equals(propertyName)) {
            this.updateFocusTraversalKeys();
        }
        else if ("foreground".equals(propertyName) || "font".equals(propertyName) || "document".equals(propertyName) || "JEditorPane.w3cLengthUnits".equals(propertyName) || "JEditorPane.honorDisplayProperties".equals(propertyName)) {
            final JTextComponent component = this.getComponent();
            this.updateDisplayProperties(component.getFont(), component.getForeground());
            if ("JEditorPane.w3cLengthUnits".equals(propertyName) || "JEditorPane.honorDisplayProperties".equals(propertyName)) {
                this.modelChanged();
            }
            if ("foreground".equals(propertyName)) {
                final Object clientProperty = component.getClientProperty("JEditorPane.honorDisplayProperties");
                boolean booleanValue = false;
                if (clientProperty instanceof Boolean) {
                    booleanValue = (boolean)clientProperty;
                }
                if (booleanValue) {
                    this.modelChanged();
                }
            }
        }
    }
    
    void removeActions(final ActionMap actionMap, final Action[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            actionMap.remove(array[i].getValue("Name"));
        }
    }
    
    void addActions(final ActionMap actionMap, final Action[] array) {
        for (final Action action : array) {
            actionMap.put(action.getValue("Name"), action);
        }
    }
    
    void updateDisplayProperties(final Font font, final Color color) {
        final JTextComponent component = this.getComponent();
        final Object clientProperty = component.getClientProperty("JEditorPane.honorDisplayProperties");
        boolean booleanValue = false;
        final Object clientProperty2 = component.getClientProperty("JEditorPane.w3cLengthUnits");
        boolean booleanValue2 = false;
        if (clientProperty instanceof Boolean) {
            booleanValue = (boolean)clientProperty;
        }
        if (clientProperty2 instanceof Boolean) {
            booleanValue2 = (boolean)clientProperty2;
        }
        if (this instanceof BasicTextPaneUI || booleanValue) {
            final Document document = this.getComponent().getDocument();
            if (document instanceof StyledDocument) {
                if (document instanceof HTMLDocument && booleanValue) {
                    this.updateCSS(font, color);
                }
                else {
                    this.updateStyle(font, color);
                }
            }
        }
        else {
            this.cleanDisplayProperties();
        }
        if (booleanValue2) {
            final Document document2 = this.getComponent().getDocument();
            if (document2 instanceof HTMLDocument) {
                ((HTMLDocument)document2).getStyleSheet().addRule("W3C_LENGTH_UNITS_ENABLE");
            }
        }
        else {
            final Document document3 = this.getComponent().getDocument();
            if (document3 instanceof HTMLDocument) {
                ((HTMLDocument)document3).getStyleSheet().addRule("W3C_LENGTH_UNITS_DISABLE");
            }
        }
    }
    
    void cleanDisplayProperties() {
        final Document document = this.getComponent().getDocument();
        if (document instanceof HTMLDocument) {
            final StyleSheet styleSheet = ((HTMLDocument)document).getStyleSheet();
            final StyleSheet[] styleSheets = styleSheet.getStyleSheets();
            if (styleSheets != null) {
                for (final StyleSheet styleSheet2 : styleSheets) {
                    if (styleSheet2 instanceof StyleSheetUIResource) {
                        styleSheet.removeStyleSheet(styleSheet2);
                        styleSheet.addRule("BASE_SIZE_DISABLE");
                        break;
                    }
                }
            }
            final Style style = ((StyledDocument)document).getStyle("default");
            if (style.getAttribute("FONT_ATTRIBUTE_KEY") != null) {
                style.removeAttribute("FONT_ATTRIBUTE_KEY");
            }
        }
    }
    
    private void updateCSS(final Font font, final Color color) {
        final JTextComponent component = this.getComponent();
        final Document document = component.getDocument();
        if (document instanceof HTMLDocument) {
            final StyleSheetUIResource styleSheetUIResource = new StyleSheetUIResource();
            final StyleSheet styleSheet = ((HTMLDocument)document).getStyleSheet();
            final StyleSheet[] styleSheets = styleSheet.getStyleSheets();
            if (styleSheets != null) {
                for (final StyleSheet styleSheet2 : styleSheets) {
                    if (styleSheet2 instanceof StyleSheetUIResource) {
                        styleSheet.removeStyleSheet(styleSheet2);
                    }
                }
            }
            styleSheetUIResource.addRule(SwingUtilities2.displayPropertiesToCSS(font, color));
            styleSheet.addStyleSheet(styleSheetUIResource);
            styleSheet.addRule("BASE_SIZE " + component.getFont().getSize());
            final Style style = ((StyledDocument)document).getStyle("default");
            if (!font.equals(style.getAttribute("FONT_ATTRIBUTE_KEY"))) {
                style.addAttribute("FONT_ATTRIBUTE_KEY", font);
            }
        }
    }
    
    private void updateStyle(final Font font, final Color color) {
        this.updateFont(font);
        this.updateForeground(color);
    }
    
    private void updateForeground(final Color color) {
        final Style style = ((StyledDocument)this.getComponent().getDocument()).getStyle("default");
        if (style == null) {
            return;
        }
        if (color == null) {
            if (style.getAttribute(StyleConstants.Foreground) != null) {
                style.removeAttribute(StyleConstants.Foreground);
            }
        }
        else if (!color.equals(StyleConstants.getForeground(style))) {
            StyleConstants.setForeground(style, color);
        }
    }
    
    private void updateFont(final Font font) {
        final Style style = ((StyledDocument)this.getComponent().getDocument()).getStyle("default");
        if (style == null) {
            return;
        }
        final String s = (String)style.getAttribute(StyleConstants.FontFamily);
        final Integer n = (Integer)style.getAttribute(StyleConstants.FontSize);
        final Boolean b = (Boolean)style.getAttribute(StyleConstants.Bold);
        final Boolean b2 = (Boolean)style.getAttribute(StyleConstants.Italic);
        final Font font2 = (Font)style.getAttribute("FONT_ATTRIBUTE_KEY");
        if (font == null) {
            if (s != null) {
                style.removeAttribute(StyleConstants.FontFamily);
            }
            if (n != null) {
                style.removeAttribute(StyleConstants.FontSize);
            }
            if (b != null) {
                style.removeAttribute(StyleConstants.Bold);
            }
            if (b2 != null) {
                style.removeAttribute(StyleConstants.Italic);
            }
            if (font2 != null) {
                style.removeAttribute("FONT_ATTRIBUTE_KEY");
            }
        }
        else {
            if (!font.getName().equals(s)) {
                StyleConstants.setFontFamily(style, font.getName());
            }
            if (n == null || n != font.getSize()) {
                StyleConstants.setFontSize(style, font.getSize());
            }
            if (b == null || b != font.isBold()) {
                StyleConstants.setBold(style, font.isBold());
            }
            if (b2 == null || b2 != font.isItalic()) {
                StyleConstants.setItalic(style, font.isItalic());
            }
            if (!font.equals(font2)) {
                style.addAttribute("FONT_ATTRIBUTE_KEY", font);
            }
        }
    }
    
    static class StyleSheetUIResource extends StyleSheet implements UIResource
    {
    }
}
