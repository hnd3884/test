package javax.swing.plaf.synth;

import java.awt.event.FocusEvent;
import java.awt.Insets;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.plaf.SpinnerUI;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.plaf.UIResource;
import javax.swing.JFormattedTextField;
import java.awt.event.FocusListener;
import javax.swing.JSpinner;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class SynthSpinnerUI extends BasicSpinnerUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    private EditorFocusHandler editorFocusHandler;
    
    public SynthSpinnerUI() {
        this.editorFocusHandler = new EditorFocusHandler();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthSpinnerUI();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.spinner.addPropertyChangeListener(this);
        final JComponent editor = this.spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            final JFormattedTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
            if (textField != null) {
                textField.addFocusListener(this.editorFocusHandler);
            }
        }
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.spinner.removePropertyChangeListener(this);
        final JComponent editor = this.spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            final JFormattedTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
            if (textField != null) {
                textField.removeFocusListener(this.editorFocusHandler);
            }
        }
    }
    
    @Override
    protected void installDefaults() {
        final LayoutManager layout = this.spinner.getLayout();
        if (layout == null || layout instanceof UIResource) {
            this.spinner.setLayout(this.createLayout());
        }
        this.updateStyle(this.spinner);
    }
    
    private void updateStyle(final JSpinner spinner) {
        final SynthContext context = this.getContext(spinner, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style && style != null) {
            this.installKeyboardActions();
        }
        context.dispose();
    }
    
    @Override
    protected void uninstallDefaults() {
        if (this.spinner.getLayout() instanceof UIResource) {
            this.spinner.setLayout(null);
        }
        final SynthContext context = this.getContext(this.spinner, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    protected LayoutManager createLayout() {
        return new SpinnerLayout();
    }
    
    @Override
    protected Component createPreviousButton() {
        final SynthArrowButton synthArrowButton = new SynthArrowButton(5);
        synthArrowButton.setName("Spinner.previousButton");
        this.installPreviousButtonListeners(synthArrowButton);
        return synthArrowButton;
    }
    
    @Override
    protected Component createNextButton() {
        final SynthArrowButton synthArrowButton = new SynthArrowButton(1);
        synthArrowButton.setName("Spinner.nextButton");
        this.installNextButtonListeners(synthArrowButton);
        return synthArrowButton;
    }
    
    @Override
    protected JComponent createEditor() {
        final JComponent editor = this.spinner.getEditor();
        editor.setName("Spinner.editor");
        this.updateEditorAlignment(editor);
        return editor;
    }
    
    @Override
    protected void replaceEditor(final JComponent component, final JComponent component2) {
        this.spinner.remove(component);
        this.spinner.add(component2, "Editor");
        if (component instanceof JSpinner.DefaultEditor) {
            final JFormattedTextField textField = ((JSpinner.DefaultEditor)component).getTextField();
            if (textField != null) {
                textField.removeFocusListener(this.editorFocusHandler);
            }
        }
        if (component2 instanceof JSpinner.DefaultEditor) {
            final JFormattedTextField textField2 = ((JSpinner.DefaultEditor)component2).getTextField();
            if (textField2 != null) {
                textField2.addFocusListener(this.editorFocusHandler);
            }
        }
    }
    
    private void updateEditorAlignment(final JComponent component) {
        if (component instanceof JSpinner.DefaultEditor) {
            final SynthContext context = this.getContext(this.spinner);
            final Integer n = (Integer)context.getStyle().get(context, "Spinner.editorAlignment");
            final JFormattedTextField textField = ((JSpinner.DefaultEditor)component).getTextField();
            if (n != null) {
                textField.setHorizontalAlignment(n);
            }
            textField.putClientProperty("JComponent.sizeVariant", this.spinner.getClientProperty("JComponent.sizeVariant"));
        }
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintSpinnerBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintSpinnerBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final JSpinner spinner = (JSpinner)propertyChangeEvent.getSource();
        final SpinnerUI ui = spinner.getUI();
        if (ui instanceof SynthSpinnerUI) {
            final SynthSpinnerUI synthSpinnerUI = (SynthSpinnerUI)ui;
            if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
                synthSpinnerUI.updateStyle(spinner);
            }
        }
    }
    
    private static class SpinnerLayout implements LayoutManager, UIResource
    {
        private Component nextButton;
        private Component previousButton;
        private Component editor;
        
        private SpinnerLayout() {
            this.nextButton = null;
            this.previousButton = null;
            this.editor = null;
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component editor) {
            if ("Next".equals(s)) {
                this.nextButton = editor;
            }
            else if ("Previous".equals(s)) {
                this.previousButton = editor;
            }
            else if ("Editor".equals(s)) {
                this.editor = editor;
            }
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
            if (component == this.nextButton) {
                this.nextButton = null;
            }
            else if (component == this.previousButton) {
                this.previousButton = null;
            }
            else if (component == this.editor) {
                this.editor = null;
            }
        }
        
        private Dimension preferredSize(final Component component) {
            return (component == null) ? new Dimension(0, 0) : component.getPreferredSize();
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            final Dimension preferredSize = this.preferredSize(this.nextButton);
            final Dimension preferredSize2 = this.preferredSize(this.previousButton);
            final Dimension preferredSize3 = this.preferredSize(this.editor);
            preferredSize3.height = (preferredSize3.height + 1) / 2 * 2;
            final Dimension dimension2;
            final Dimension dimension = dimension2 = new Dimension(preferredSize3.width, preferredSize3.height);
            dimension2.width += Math.max(preferredSize.width, preferredSize2.width);
            final Insets insets = container.getInsets();
            final Dimension dimension3 = dimension;
            dimension3.width += insets.left + insets.right;
            final Dimension dimension4 = dimension;
            dimension4.height += insets.top + insets.bottom;
            return dimension;
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return this.preferredLayoutSize(container);
        }
        
        private void setBounds(final Component component, final int n, final int n2, final int n3, final int n4) {
            if (component != null) {
                component.setBounds(n, n2, n3, n4);
            }
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final Insets insets = container.getInsets();
            final int n = container.getWidth() - (insets.left + insets.right);
            final int n2 = container.getHeight() - (insets.top + insets.bottom);
            final Dimension preferredSize = this.preferredSize(this.nextButton);
            final Dimension preferredSize2 = this.preferredSize(this.previousButton);
            final int n3 = n2 / 2;
            final int n4 = n2 - n3;
            final int max = Math.max(preferredSize.width, preferredSize2.width);
            final int n5 = n - max;
            int left;
            int left2;
            if (container.getComponentOrientation().isLeftToRight()) {
                left = insets.left;
                left2 = left + n5;
            }
            else {
                left2 = insets.left;
                left = left2 + max;
            }
            final int n6 = insets.top + n3;
            this.setBounds(this.editor, left, insets.top, n5, n2);
            this.setBounds(this.nextButton, left2, insets.top, max, n3);
            this.setBounds(this.previousButton, left2, n6, max, n4);
        }
    }
    
    private class EditorFocusHandler implements FocusListener
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            SynthSpinnerUI.this.spinner.repaint();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            SynthSpinnerUI.this.spinner.repaint();
        }
    }
}
