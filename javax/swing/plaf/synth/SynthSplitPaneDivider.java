package javax.swing.plaf.synth;

import javax.swing.plaf.ComponentUI;
import sun.swing.DefaultLookup;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

class SynthSplitPaneDivider extends BasicSplitPaneDivider
{
    public SynthSplitPaneDivider(final BasicSplitPaneUI basicSplitPaneUI) {
        super(basicSplitPaneUI);
    }
    
    @Override
    protected void setMouseOver(final boolean mouseOver) {
        if (this.isMouseOver() != mouseOver) {
            this.repaint();
        }
        super.setMouseOver(mouseOver);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        super.propertyChange(propertyChangeEvent);
        if (propertyChangeEvent.getSource() == this.splitPane && propertyChangeEvent.getPropertyName() == "orientation") {
            if (this.leftButton instanceof SynthArrowButton) {
                ((SynthArrowButton)this.leftButton).setDirection(this.mapDirection(true));
            }
            if (this.rightButton instanceof SynthArrowButton) {
                ((SynthArrowButton)this.rightButton).setDirection(this.mapDirection(false));
            }
        }
    }
    
    @Override
    public void paint(final Graphics graphics) {
        final Graphics create = graphics.create();
        final SynthContext context = ((SynthSplitPaneUI)this.splitPaneUI).getContext(this.splitPane, Region.SPLIT_PANE_DIVIDER);
        final Rectangle bounds;
        final Rectangle rectangle2;
        final Rectangle rectangle = rectangle2 = (bounds = this.getBounds());
        final int n = 0;
        rectangle2.y = n;
        bounds.x = n;
        SynthLookAndFeel.updateSubregion(context, graphics, rectangle);
        context.getPainter().paintSplitPaneDividerBackground(context, graphics, 0, 0, rectangle.width, rectangle.height, this.splitPane.getOrientation());
        context.getPainter().paintSplitPaneDividerForeground(context, graphics, 0, 0, this.getWidth(), this.getHeight(), this.splitPane.getOrientation());
        context.dispose();
        for (int i = 0; i < this.getComponentCount(); ++i) {
            final Component component = this.getComponent(i);
            final Rectangle bounds2 = component.getBounds();
            final Graphics create2 = graphics.create(bounds2.x, bounds2.y, bounds2.width, bounds2.height);
            component.paint(create2);
            create2.dispose();
        }
        create.dispose();
    }
    
    private int mapDirection(final boolean b) {
        if (b) {
            if (this.splitPane.getOrientation() == 1) {
                return 7;
            }
            return 1;
        }
        else {
            if (this.splitPane.getOrientation() == 1) {
                return 3;
            }
            return 5;
        }
    }
    
    @Override
    protected JButton createLeftOneTouchButton() {
        final SynthArrowButton synthArrowButton = new SynthArrowButton(1);
        final int lookupOneTouchSize = this.lookupOneTouchSize();
        synthArrowButton.setName("SplitPaneDivider.leftOneTouchButton");
        synthArrowButton.setMinimumSize(new Dimension(lookupOneTouchSize, lookupOneTouchSize));
        synthArrowButton.setCursor(Cursor.getPredefinedCursor(0));
        synthArrowButton.setFocusPainted(false);
        synthArrowButton.setBorderPainted(false);
        synthArrowButton.setRequestFocusEnabled(false);
        synthArrowButton.setDirection(this.mapDirection(true));
        return synthArrowButton;
    }
    
    private int lookupOneTouchSize() {
        return DefaultLookup.getInt(this.splitPaneUI.getSplitPane(), this.splitPaneUI, "SplitPaneDivider.oneTouchButtonSize", 6);
    }
    
    @Override
    protected JButton createRightOneTouchButton() {
        final SynthArrowButton synthArrowButton = new SynthArrowButton(1);
        final int lookupOneTouchSize = this.lookupOneTouchSize();
        synthArrowButton.setName("SplitPaneDivider.rightOneTouchButton");
        synthArrowButton.setMinimumSize(new Dimension(lookupOneTouchSize, lookupOneTouchSize));
        synthArrowButton.setCursor(Cursor.getPredefinedCursor(0));
        synthArrowButton.setFocusPainted(false);
        synthArrowButton.setBorderPainted(false);
        synthArrowButton.setRequestFocusEnabled(false);
        synthArrowButton.setDirection(this.mapDirection(false));
        return synthArrowButton;
    }
}
