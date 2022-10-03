package javax.swing;

import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.awt.event.WindowAdapter;
import java.awt.Container;
import java.util.Locale;
import java.awt.event.WindowListener;
import sun.swing.SwingUtilities2;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Color;

class ColorChooserDialog extends JDialog
{
    private Color initialColor;
    private JColorChooser chooserPane;
    private JButton cancelButton;
    
    public ColorChooserDialog(final Dialog dialog, final String s, final boolean b, final Component component, final JColorChooser colorChooser, final ActionListener actionListener, final ActionListener actionListener2) throws HeadlessException {
        super(dialog, s, b);
        this.initColorChooserDialog(component, colorChooser, actionListener, actionListener2);
    }
    
    public ColorChooserDialog(final Frame frame, final String s, final boolean b, final Component component, final JColorChooser colorChooser, final ActionListener actionListener, final ActionListener actionListener2) throws HeadlessException {
        super(frame, s, b);
        this.initColorChooserDialog(component, colorChooser, actionListener, actionListener2);
    }
    
    protected void initColorChooserDialog(final Component locationRelativeTo, final JColorChooser chooserPane, final ActionListener actionListener, final ActionListener actionListener2) {
        this.chooserPane = chooserPane;
        final Locale locale = this.getLocale();
        final String string = UIManager.getString("ColorChooser.okText", locale);
        final String string2 = UIManager.getString("ColorChooser.cancelText", locale);
        final String string3 = UIManager.getString("ColorChooser.resetText", locale);
        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(chooserPane, "Center");
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(1));
        final JButton defaultButton = new JButton(string);
        this.getRootPane().setDefaultButton(defaultButton);
        defaultButton.getAccessibleContext().setAccessibleDescription(string);
        defaultButton.setActionCommand("OK");
        defaultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                ColorChooserDialog.this.hide();
            }
        });
        if (actionListener != null) {
            defaultButton.addActionListener(actionListener);
        }
        panel.add(defaultButton);
        this.cancelButton = new JButton(string2);
        this.cancelButton.getAccessibleContext().setAccessibleDescription(string2);
        final AbstractAction abstractAction = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                ((AbstractButton)actionEvent.getSource()).fireActionPerformed(actionEvent);
            }
        };
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(27, 0);
        final InputMap inputMap = this.cancelButton.getInputMap(2);
        final ActionMap actionMap = this.cancelButton.getActionMap();
        if (inputMap != null && actionMap != null) {
            inputMap.put(keyStroke, "cancel");
            actionMap.put("cancel", abstractAction);
        }
        this.cancelButton.setActionCommand("cancel");
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                ColorChooserDialog.this.hide();
            }
        });
        if (actionListener2 != null) {
            this.cancelButton.addActionListener(actionListener2);
        }
        panel.add(this.cancelButton);
        final JButton button = new JButton(string3);
        button.getAccessibleContext().setAccessibleDescription(string3);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                ColorChooserDialog.this.reset();
            }
        });
        final int uiDefaultsInt = SwingUtilities2.getUIDefaultsInt("ColorChooser.resetMnemonic", locale, -1);
        if (uiDefaultsInt != -1) {
            button.setMnemonic(uiDefaultsInt);
        }
        panel.add(button);
        contentPane.add(panel, "South");
        if (JDialog.isDefaultLookAndFeelDecorated() && UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            this.getRootPane().setWindowDecorationStyle(5);
        }
        this.applyComponentOrientation(((locationRelativeTo == null) ? this.getRootPane() : locationRelativeTo).getComponentOrientation());
        this.pack();
        this.setLocationRelativeTo(locationRelativeTo);
        this.addWindowListener(new Closer());
    }
    
    @Override
    public void show() {
        this.initialColor = this.chooserPane.getColor();
        super.show();
    }
    
    public void reset() {
        this.chooserPane.setColor(this.initialColor);
    }
    
    class Closer extends WindowAdapter implements Serializable
    {
        @Override
        public void windowClosing(final WindowEvent windowEvent) {
            ColorChooserDialog.this.cancelButton.doClick(0);
            windowEvent.getWindow().hide();
        }
    }
    
    static class DisposeOnClose extends ComponentAdapter implements Serializable
    {
        @Override
        public void componentHidden(final ComponentEvent componentEvent) {
            ((Window)componentEvent.getComponent()).dispose();
        }
    }
}
