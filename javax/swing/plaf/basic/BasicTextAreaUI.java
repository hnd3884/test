package javax.swing.plaf.basic;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.GlyphView;
import javax.swing.text.ViewFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.text.CompositeView;
import javax.swing.SizeRequirements;
import java.awt.Container;
import javax.swing.text.ParagraphView;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.text.BoxView;
import javax.swing.text.TabExpander;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainView;
import javax.swing.text.WrappedPlainView;
import javax.swing.JTextArea;
import javax.swing.text.View;
import javax.swing.text.Element;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class BasicTextAreaUI extends BasicTextUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new BasicTextAreaUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "TextArea";
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
    }
    
    @Override
    protected void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        super.propertyChange(propertyChangeEvent);
        if (propertyChangeEvent.getPropertyName().equals("lineWrap") || propertyChangeEvent.getPropertyName().equals("wrapStyleWord") || propertyChangeEvent.getPropertyName().equals("tabSize")) {
            this.modelChanged();
        }
        else if ("editable".equals(propertyChangeEvent.getPropertyName())) {
            this.updateFocusTraversalKeys();
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return super.getPreferredSize(component);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return super.getMinimumSize(component);
    }
    
    @Override
    public View create(final Element element) {
        final Object property = element.getDocument().getProperty("i18n");
        if (property != null && property.equals(Boolean.TRUE)) {
            return this.createI18N(element);
        }
        final JTextComponent component = this.getComponent();
        if (component instanceof JTextArea) {
            final JTextArea textArea = (JTextArea)component;
            TabExpander tabExpander;
            if (textArea.getLineWrap()) {
                tabExpander = new WrappedPlainView(element, textArea.getWrapStyleWord());
            }
            else {
                tabExpander = new PlainView(element);
            }
            return (View)tabExpander;
        }
        return null;
    }
    
    View createI18N(final Element element) {
        final String name = element.getName();
        if (name != null) {
            if (name.equals("content")) {
                return new PlainParagraph(element);
            }
            if (name.equals("paragraph")) {
                return new BoxView(element, 1);
            }
        }
        return null;
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, int n2) {
        super.getBaseline(component, n, n2);
        final Object property = ((JTextComponent)component).getDocument().getProperty("i18n");
        final Insets insets = component.getInsets();
        if (!Boolean.TRUE.equals(property)) {
            return insets.top + component.getFontMetrics(component.getFont()).getAscent();
        }
        final View rootView = this.getRootView((JTextComponent)component);
        if (rootView.getViewCount() <= 0) {
            return -1;
        }
        n2 = n2 - insets.top - insets.bottom;
        final int top = insets.top;
        final int baseline = BasicHTML.getBaseline(rootView.getView(0), n - insets.left - insets.right, n2);
        if (baseline < 0) {
            return -1;
        }
        return top + baseline;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    }
    
    static class PlainParagraph extends ParagraphView
    {
        PlainParagraph(final Element element) {
            super(element);
            (this.layoutPool = new LogicalView(element)).setParent(this);
        }
        
        @Override
        public void setParent(final View parent) {
            super.setParent(parent);
            if (parent != null) {
                this.setPropertiesFromAttributes();
            }
        }
        
        @Override
        protected void setPropertiesFromAttributes() {
            final Container container = this.getContainer();
            if (container != null && !container.getComponentOrientation().isLeftToRight()) {
                this.setJustification(2);
            }
            else {
                this.setJustification(0);
            }
        }
        
        @Override
        public int getFlowSpan(final int n) {
            final Container container = this.getContainer();
            if (container instanceof JTextArea && !((JTextArea)container).getLineWrap()) {
                return Integer.MAX_VALUE;
            }
            return super.getFlowSpan(n);
        }
        
        @Override
        protected SizeRequirements calculateMinorAxisRequirements(final int n, final SizeRequirements sizeRequirements) {
            final SizeRequirements calculateMinorAxisRequirements = super.calculateMinorAxisRequirements(n, sizeRequirements);
            final Container container = this.getContainer();
            if (container instanceof JTextArea) {
                if (!((JTextArea)container).getLineWrap()) {
                    calculateMinorAxisRequirements.minimum = calculateMinorAxisRequirements.preferred;
                }
                else {
                    calculateMinorAxisRequirements.minimum = 0;
                    calculateMinorAxisRequirements.preferred = this.getWidth();
                    if (calculateMinorAxisRequirements.preferred == Integer.MAX_VALUE) {
                        calculateMinorAxisRequirements.preferred = 100;
                    }
                }
            }
            return calculateMinorAxisRequirements;
        }
        
        @Override
        public void setSize(final float n, final float n2) {
            if ((int)n != this.getWidth()) {
                this.preferenceChanged(null, true, true);
            }
            super.setSize(n, n2);
        }
        
        static class LogicalView extends CompositeView
        {
            LogicalView(final Element element) {
                super(element);
            }
            
            @Override
            protected int getViewIndexAtPosition(final int n) {
                final Element element = this.getElement();
                if (element.getElementCount() > 0) {
                    return element.getElementIndex(n);
                }
                return 0;
            }
            
            @Override
            protected boolean updateChildren(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final ViewFactory viewFactory) {
                return false;
            }
            
            @Override
            protected void loadChildren(final ViewFactory viewFactory) {
                final Element element = this.getElement();
                if (element.getElementCount() > 0) {
                    super.loadChildren(viewFactory);
                }
                else {
                    this.append(new GlyphView(element));
                }
            }
            
            @Override
            public float getPreferredSpan(final int n) {
                if (this.getViewCount() != 1) {
                    throw new Error("One child view is assumed.");
                }
                return this.getView(0).getPreferredSpan(n);
            }
            
            @Override
            protected void forwardUpdateToView(final View view, final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
                view.setParent(this);
                super.forwardUpdateToView(view, documentEvent, shape, viewFactory);
            }
            
            @Override
            public void paint(final Graphics graphics, final Shape shape) {
            }
            
            @Override
            protected boolean isBefore(final int n, final int n2, final Rectangle rectangle) {
                return false;
            }
            
            @Override
            protected boolean isAfter(final int n, final int n2, final Rectangle rectangle) {
                return false;
            }
            
            @Override
            protected View getViewAtPoint(final int n, final int n2, final Rectangle rectangle) {
                return null;
            }
            
            @Override
            protected void childAllocation(final int n, final Rectangle rectangle) {
            }
        }
    }
}
