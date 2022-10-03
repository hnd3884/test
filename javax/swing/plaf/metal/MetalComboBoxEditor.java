package javax.swing.plaf.metal;

import javax.swing.plaf.UIResource;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.Dimension;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class MetalComboBoxEditor extends BasicComboBoxEditor
{
    protected static Insets editorBorderInsets;
    
    public MetalComboBoxEditor() {
        (this.editor = new JTextField("", 9) {
            @Override
            public void setText(final String text) {
                if (this.getText().equals(text)) {
                    return;
                }
                super.setText(text);
            }
            
            @Override
            public Dimension getPreferredSize() {
                final Dimension preferredSize;
                final Dimension dimension = preferredSize = super.getPreferredSize();
                preferredSize.height += 4;
                return dimension;
            }
            
            @Override
            public Dimension getMinimumSize() {
                final Dimension minimumSize;
                final Dimension dimension = minimumSize = super.getMinimumSize();
                minimumSize.height += 4;
                return dimension;
            }
        }).setBorder(new EditorBorder());
    }
    
    static {
        MetalComboBoxEditor.editorBorderInsets = new Insets(2, 2, 2, 0);
    }
    
    class EditorBorder extends AbstractBorder
    {
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            graphics.translate(n, n2);
            if (MetalLookAndFeel.usingOcean()) {
                graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                graphics.drawRect(0, 0, n3, n4 - 1);
                graphics.setColor(MetalLookAndFeel.getControlShadow());
                graphics.drawRect(1, 1, n3 - 2, n4 - 3);
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                graphics.drawLine(0, 0, n3 - 1, 0);
                graphics.drawLine(0, 0, 0, n4 - 2);
                graphics.drawLine(0, n4 - 2, n3 - 1, n4 - 2);
                graphics.setColor(MetalLookAndFeel.getControlHighlight());
                graphics.drawLine(1, 1, n3 - 1, 1);
                graphics.drawLine(1, 1, 1, n4 - 1);
                graphics.drawLine(1, n4 - 1, n3 - 1, n4 - 1);
                graphics.setColor(MetalLookAndFeel.getControl());
                graphics.drawLine(1, n4 - 2, 1, n4 - 2);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(2, 2, 2, 0);
            return insets;
        }
    }
    
    public static class UIResource extends MetalComboBoxEditor implements javax.swing.plaf.UIResource
    {
    }
}
