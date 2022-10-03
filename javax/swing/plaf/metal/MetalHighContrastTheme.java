package javax.swing.plaf.metal;

import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

class MetalHighContrastTheme extends DefaultMetalTheme
{
    private static final ColorUIResource primary1;
    private static final ColorUIResource primary2;
    private static final ColorUIResource primary3;
    private static final ColorUIResource primaryHighlight;
    private static final ColorUIResource secondary2;
    private static final ColorUIResource secondary3;
    private static final ColorUIResource controlHighlight;
    
    @Override
    public String getName() {
        return "Contrast";
    }
    
    @Override
    protected ColorUIResource getPrimary1() {
        return MetalHighContrastTheme.primary1;
    }
    
    @Override
    protected ColorUIResource getPrimary2() {
        return MetalHighContrastTheme.primary2;
    }
    
    @Override
    protected ColorUIResource getPrimary3() {
        return MetalHighContrastTheme.primary3;
    }
    
    @Override
    public ColorUIResource getPrimaryControlHighlight() {
        return MetalHighContrastTheme.primaryHighlight;
    }
    
    @Override
    protected ColorUIResource getSecondary2() {
        return MetalHighContrastTheme.secondary2;
    }
    
    @Override
    protected ColorUIResource getSecondary3() {
        return MetalHighContrastTheme.secondary3;
    }
    
    @Override
    public ColorUIResource getControlHighlight() {
        return MetalHighContrastTheme.secondary2;
    }
    
    @Override
    public ColorUIResource getFocusColor() {
        return this.getBlack();
    }
    
    @Override
    public ColorUIResource getTextHighlightColor() {
        return this.getBlack();
    }
    
    @Override
    public ColorUIResource getHighlightedTextColor() {
        return this.getWhite();
    }
    
    @Override
    public ColorUIResource getMenuSelectedBackground() {
        return this.getBlack();
    }
    
    @Override
    public ColorUIResource getMenuSelectedForeground() {
        return this.getWhite();
    }
    
    @Override
    public ColorUIResource getAcceleratorForeground() {
        return this.getBlack();
    }
    
    @Override
    public ColorUIResource getAcceleratorSelectedForeground() {
        return this.getWhite();
    }
    
    @Override
    public void addCustomEntriesToTable(final UIDefaults uiDefaults) {
        final BorderUIResource borderUIResource = new BorderUIResource(new LineBorder(this.getBlack()));
        final BorderUIResource borderUIResource2 = new BorderUIResource(new LineBorder(this.getWhite()));
        final BorderUIResource borderUIResource3 = new BorderUIResource(new CompoundBorder(borderUIResource, new BasicBorders.MarginBorder()));
        uiDefaults.putDefaults(new Object[] { "ToolTip.border", borderUIResource, "TitledBorder.border", borderUIResource, "TextField.border", borderUIResource3, "PasswordField.border", borderUIResource3, "TextArea.border", borderUIResource3, "TextPane.border", borderUIResource3, "EditorPane.border", borderUIResource3, "ComboBox.background", this.getWindowBackground(), "ComboBox.foreground", this.getUserTextColor(), "ComboBox.selectionBackground", this.getTextHighlightColor(), "ComboBox.selectionForeground", this.getHighlightedTextColor(), "ProgressBar.foreground", this.getUserTextColor(), "ProgressBar.background", this.getWindowBackground(), "ProgressBar.selectionForeground", this.getWindowBackground(), "ProgressBar.selectionBackground", this.getUserTextColor(), "OptionPane.errorDialog.border.background", this.getPrimary1(), "OptionPane.errorDialog.titlePane.foreground", this.getPrimary3(), "OptionPane.errorDialog.titlePane.background", this.getPrimary1(), "OptionPane.errorDialog.titlePane.shadow", this.getPrimary2(), "OptionPane.questionDialog.border.background", this.getPrimary1(), "OptionPane.questionDialog.titlePane.foreground", this.getPrimary3(), "OptionPane.questionDialog.titlePane.background", this.getPrimary1(), "OptionPane.questionDialog.titlePane.shadow", this.getPrimary2(), "OptionPane.warningDialog.border.background", this.getPrimary1(), "OptionPane.warningDialog.titlePane.foreground", this.getPrimary3(), "OptionPane.warningDialog.titlePane.background", this.getPrimary1(), "OptionPane.warningDialog.titlePane.shadow", this.getPrimary2() });
    }
    
    @Override
    boolean isSystemTheme() {
        return this.getClass() == MetalHighContrastTheme.class;
    }
    
    static {
        primary1 = new ColorUIResource(0, 0, 0);
        primary2 = new ColorUIResource(204, 204, 204);
        primary3 = new ColorUIResource(255, 255, 255);
        primaryHighlight = new ColorUIResource(102, 102, 102);
        secondary2 = new ColorUIResource(204, 204, 204);
        secondary3 = new ColorUIResource(255, 255, 255);
        controlHighlight = new ColorUIResource(102, 102, 102);
    }
}
