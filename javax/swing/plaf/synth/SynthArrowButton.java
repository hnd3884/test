package javax.swing.plaf.synth;

import java.awt.Container;
import javax.swing.JComboBox;
import java.awt.Dimension;
import javax.swing.JComponent;
import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.UIResource;
import javax.swing.SwingConstants;
import javax.swing.JButton;

class SynthArrowButton extends JButton implements SwingConstants, UIResource
{
    private int direction;
    
    public SynthArrowButton(final int direction) {
        super.setFocusable(false);
        this.setDirection(direction);
        this.setDefaultCapable(false);
    }
    
    @Override
    public String getUIClassID() {
        return "ArrowButtonUI";
    }
    
    @Override
    public void updateUI() {
        this.setUI(new SynthArrowButtonUI());
    }
    
    public void setDirection(final int direction) {
        this.direction = direction;
        this.putClientProperty("__arrow_direction__", direction);
        this.repaint();
    }
    
    public int getDirection() {
        return this.direction;
    }
    
    @Override
    public void setFocusable(final boolean b) {
    }
    
    private static class SynthArrowButtonUI extends SynthButtonUI
    {
        @Override
        protected void installDefaults(final AbstractButton abstractButton) {
            super.installDefaults(abstractButton);
            this.updateStyle(abstractButton);
        }
        
        @Override
        protected void paint(final SynthContext synthContext, final Graphics graphics) {
            final SynthArrowButton synthArrowButton = (SynthArrowButton)synthContext.getComponent();
            synthContext.getPainter().paintArrowButtonForeground(synthContext, graphics, 0, 0, synthArrowButton.getWidth(), synthArrowButton.getHeight(), synthArrowButton.getDirection());
        }
        
        @Override
        void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
            synthContext.getPainter().paintArrowButtonBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
        }
        
        @Override
        public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            synthContext.getPainter().paintArrowButtonBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        public Dimension getMinimumSize() {
            return new Dimension(5, 5);
        }
        
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        
        @Override
        public Dimension getPreferredSize(final JComponent component) {
            final SynthContext context = this.getContext(component);
            Object o = null;
            if (context.getComponent().getName() == "ScrollBar.button") {
                o = context.getStyle().get(context, "ScrollBar.buttonSize");
            }
            if (o == null) {
                final int int1 = context.getStyle().getInt(context, "ArrowButton.size", 16);
                o = new Dimension(int1, int1);
            }
            final Container parent = context.getComponent().getParent();
            if (parent instanceof JComponent && !(parent instanceof JComboBox)) {
                final Object clientProperty = ((JComponent)parent).getClientProperty("JComponent.sizeVariant");
                if (clientProperty != null) {
                    if ("large".equals(clientProperty)) {
                        o = new Dimension((int)(((Dimension)o).width * 1.15), (int)(((Dimension)o).height * 1.15));
                    }
                    else if ("small".equals(clientProperty)) {
                        o = new Dimension((int)(((Dimension)o).width * 0.857), (int)(((Dimension)o).height * 0.857));
                    }
                    else if ("mini".equals(clientProperty)) {
                        o = new Dimension((int)(((Dimension)o).width * 0.714), (int)(((Dimension)o).height * 0.714));
                    }
                }
            }
            context.dispose();
            return (Dimension)o;
        }
    }
}
