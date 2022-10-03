package javax.swing;

import javax.swing.text.AttributeSet;
import java.awt.Rectangle;
import java.awt.Point;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleComponent;
import java.awt.IllegalComponentStateException;
import java.util.Locale;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.accessibility.AccessibleText;
import java.awt.Container;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import javax.accessibility.AccessibleContext;
import java.awt.Component;
import javax.accessibility.Accessible;

public class ProgressMonitor implements Accessible
{
    private ProgressMonitor root;
    private JDialog dialog;
    private JOptionPane pane;
    private JProgressBar myBar;
    private JLabel noteLabel;
    private Component parentComponent;
    private String note;
    private Object[] cancelOption;
    private Object message;
    private long T0;
    private int millisToDecideToPopup;
    private int millisToPopup;
    private int min;
    private int max;
    protected AccessibleContext accessibleContext;
    private AccessibleContext accessibleJOptionPane;
    
    public ProgressMonitor(final Component component, final Object o, final String s, final int n, final int n2) {
        this(component, o, s, n, n2, null);
    }
    
    private ProgressMonitor(final Component parentComponent, final Object message, final String note, final int min, final int max, final ProgressMonitor progressMonitor) {
        this.cancelOption = null;
        this.millisToDecideToPopup = 500;
        this.millisToPopup = 2000;
        this.accessibleContext = null;
        this.accessibleJOptionPane = null;
        this.min = min;
        this.max = max;
        this.parentComponent = parentComponent;
        (this.cancelOption = new Object[1])[0] = UIManager.getString("OptionPane.cancelButtonText");
        this.message = message;
        this.note = note;
        if (progressMonitor != null) {
            this.root = ((progressMonitor.root != null) ? progressMonitor.root : progressMonitor);
            this.T0 = this.root.T0;
            this.dialog = this.root.dialog;
        }
        else {
            this.T0 = System.currentTimeMillis();
        }
    }
    
    public void setProgress(final int n) {
        if (n >= this.max) {
            this.close();
        }
        else if (this.myBar != null) {
            this.myBar.setValue(n);
        }
        else {
            final long n2 = (int)(System.currentTimeMillis() - this.T0);
            if (n2 >= this.millisToDecideToPopup) {
                int millisToPopup;
                if (n > this.min) {
                    millisToPopup = (int)(n2 * (this.max - this.min) / (n - this.min));
                }
                else {
                    millisToPopup = this.millisToPopup;
                }
                if (millisToPopup >= this.millisToPopup) {
                    (this.myBar = new JProgressBar()).setMinimum(this.min);
                    this.myBar.setMaximum(this.max);
                    this.myBar.setValue(n);
                    if (this.note != null) {
                        this.noteLabel = new JLabel(this.note);
                    }
                    this.pane = new ProgressOptionPane(new Object[] { this.message, this.noteLabel, this.myBar });
                    (this.dialog = this.pane.createDialog(this.parentComponent, UIManager.getString("ProgressMonitor.progressText"))).show();
                }
            }
        }
    }
    
    public void close() {
        if (this.dialog != null) {
            this.dialog.setVisible(false);
            this.dialog.dispose();
            this.dialog = null;
            this.pane = null;
            this.myBar = null;
        }
    }
    
    public int getMinimum() {
        return this.min;
    }
    
    public void setMinimum(final int n) {
        if (this.myBar != null) {
            this.myBar.setMinimum(n);
        }
        this.min = n;
    }
    
    public int getMaximum() {
        return this.max;
    }
    
    public void setMaximum(final int n) {
        if (this.myBar != null) {
            this.myBar.setMaximum(n);
        }
        this.max = n;
    }
    
    public boolean isCanceled() {
        if (this.pane == null) {
            return false;
        }
        final Object value = this.pane.getValue();
        return value != null && this.cancelOption.length == 1 && value.equals(this.cancelOption[0]);
    }
    
    public void setMillisToDecideToPopup(final int millisToDecideToPopup) {
        this.millisToDecideToPopup = millisToDecideToPopup;
    }
    
    public int getMillisToDecideToPopup() {
        return this.millisToDecideToPopup;
    }
    
    public void setMillisToPopup(final int millisToPopup) {
        this.millisToPopup = millisToPopup;
    }
    
    public int getMillisToPopup() {
        return this.millisToPopup;
    }
    
    public void setNote(final String s) {
        this.note = s;
        if (this.noteLabel != null) {
            this.noteLabel.setText(s);
        }
    }
    
    public String getNote() {
        return this.note;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleProgressMonitor();
        }
        if (this.pane != null && this.accessibleJOptionPane == null && this.accessibleContext instanceof AccessibleProgressMonitor) {
            ((AccessibleProgressMonitor)this.accessibleContext).optionPaneCreated();
        }
        return this.accessibleContext;
    }
    
    private class ProgressOptionPane extends JOptionPane
    {
        ProgressOptionPane(final Object o) {
            super(o, 1, -1, null, ProgressMonitor.this.cancelOption, null);
        }
        
        @Override
        public int getMaxCharactersPerLineCount() {
            return 60;
        }
        
        @Override
        public JDialog createDialog(final Component locationRelativeTo, final String s) {
            final Window windowForComponent = JOptionPane.getWindowForComponent(locationRelativeTo);
            JDialog dialog;
            if (windowForComponent instanceof Frame) {
                dialog = new JDialog((Frame)windowForComponent, s, false);
            }
            else {
                dialog = new JDialog((Dialog)windowForComponent, s, false);
            }
            if (windowForComponent instanceof SwingUtilities.SharedOwnerFrame) {
                dialog.addWindowListener(SwingUtilities.getSharedOwnerFrameShutdownListener());
            }
            final Container contentPane = dialog.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(this, "Center");
            dialog.pack();
            dialog.setLocationRelativeTo(locationRelativeTo);
            dialog.addWindowListener(new WindowAdapter() {
                boolean gotFocus = false;
                
                @Override
                public void windowClosing(final WindowEvent windowEvent) {
                    ProgressOptionPane.this.setValue(ProgressMonitor.this.cancelOption[0]);
                }
                
                @Override
                public void windowActivated(final WindowEvent windowEvent) {
                    if (!this.gotFocus) {
                        ProgressOptionPane.this.selectInitialValue();
                        this.gotFocus = true;
                    }
                }
            });
            this.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                    if (dialog.isVisible() && propertyChangeEvent.getSource() == ProgressOptionPane.this && (propertyChangeEvent.getPropertyName().equals("value") || propertyChangeEvent.getPropertyName().equals("inputValue"))) {
                        dialog.setVisible(false);
                        dialog.dispose();
                    }
                }
            });
            return dialog;
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            return ProgressMonitor.this.getAccessibleContext();
        }
        
        private AccessibleContext getAccessibleJOptionPane() {
            return super.getAccessibleContext();
        }
    }
    
    protected class AccessibleProgressMonitor extends AccessibleContext implements AccessibleText, ChangeListener, PropertyChangeListener
    {
        private Object oldModelValue;
        
        private void optionPaneCreated() {
            ProgressMonitor.this.accessibleJOptionPane = ((ProgressOptionPane)ProgressMonitor.this.pane).getAccessibleJOptionPane();
            if (ProgressMonitor.this.myBar != null) {
                ProgressMonitor.this.myBar.addChangeListener(this);
            }
            if (ProgressMonitor.this.noteLabel != null) {
                ProgressMonitor.this.noteLabel.addPropertyChangeListener(this);
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            if (changeEvent == null) {
                return;
            }
            if (ProgressMonitor.this.myBar != null) {
                final Integer value = ProgressMonitor.this.myBar.getValue();
                this.firePropertyChange("AccessibleValue", this.oldModelValue, value);
                this.oldModelValue = value;
            }
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getSource() == ProgressMonitor.this.noteLabel && propertyChangeEvent.getPropertyName() == "text") {
                this.firePropertyChange("AccessibleText", null, 0);
            }
        }
        
        @Override
        public String getAccessibleName() {
            if (this.accessibleName != null) {
                return this.accessibleName;
            }
            if (ProgressMonitor.this.accessibleJOptionPane != null) {
                return ProgressMonitor.this.accessibleJOptionPane.getAccessibleName();
            }
            return null;
        }
        
        @Override
        public String getAccessibleDescription() {
            if (this.accessibleDescription != null) {
                return this.accessibleDescription;
            }
            if (ProgressMonitor.this.accessibleJOptionPane != null) {
                return ProgressMonitor.this.accessibleJOptionPane.getAccessibleDescription();
            }
            return null;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PROGRESS_MONITOR;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            if (ProgressMonitor.this.accessibleJOptionPane != null) {
                return ProgressMonitor.this.accessibleJOptionPane.getAccessibleStateSet();
            }
            return null;
        }
        
        @Override
        public Accessible getAccessibleParent() {
            return ProgressMonitor.this.dialog;
        }
        
        private AccessibleContext getParentAccessibleContext() {
            if (ProgressMonitor.this.dialog != null) {
                return ProgressMonitor.this.dialog.getAccessibleContext();
            }
            return null;
        }
        
        @Override
        public int getAccessibleIndexInParent() {
            if (ProgressMonitor.this.accessibleJOptionPane != null) {
                return ProgressMonitor.this.accessibleJOptionPane.getAccessibleIndexInParent();
            }
            return -1;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            final AccessibleContext panelAccessibleContext = this.getPanelAccessibleContext();
            if (panelAccessibleContext != null) {
                return panelAccessibleContext.getAccessibleChildrenCount();
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            final AccessibleContext panelAccessibleContext = this.getPanelAccessibleContext();
            if (panelAccessibleContext != null) {
                return panelAccessibleContext.getAccessibleChild(n);
            }
            return null;
        }
        
        private AccessibleContext getPanelAccessibleContext() {
            if (ProgressMonitor.this.myBar != null) {
                final Container parent = ProgressMonitor.this.myBar.getParent();
                if (parent instanceof Accessible) {
                    return parent.getAccessibleContext();
                }
            }
            return null;
        }
        
        @Override
        public Locale getLocale() throws IllegalComponentStateException {
            if (ProgressMonitor.this.accessibleJOptionPane != null) {
                return ProgressMonitor.this.accessibleJOptionPane.getLocale();
            }
            return null;
        }
        
        @Override
        public AccessibleComponent getAccessibleComponent() {
            if (ProgressMonitor.this.accessibleJOptionPane != null) {
                return ProgressMonitor.this.accessibleJOptionPane.getAccessibleComponent();
            }
            return null;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            if (ProgressMonitor.this.myBar != null) {
                return ProgressMonitor.this.myBar.getAccessibleContext().getAccessibleValue();
            }
            return null;
        }
        
        @Override
        public AccessibleText getAccessibleText() {
            if (this.getNoteLabelAccessibleText() != null) {
                return this;
            }
            return null;
        }
        
        private AccessibleText getNoteLabelAccessibleText() {
            if (ProgressMonitor.this.noteLabel != null) {
                return ProgressMonitor.this.noteLabel.getAccessibleContext().getAccessibleText();
            }
            return null;
        }
        
        @Override
        public int getIndexAtPoint(final Point point) {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null && this.sameWindowAncestor(ProgressMonitor.this.pane, ProgressMonitor.this.noteLabel)) {
                final Point convertPoint = SwingUtilities.convertPoint(ProgressMonitor.this.pane, point, ProgressMonitor.this.noteLabel);
                if (convertPoint != null) {
                    return noteLabelAccessibleText.getIndexAtPoint(convertPoint);
                }
            }
            return -1;
        }
        
        @Override
        public Rectangle getCharacterBounds(final int n) {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null && this.sameWindowAncestor(ProgressMonitor.this.pane, ProgressMonitor.this.noteLabel)) {
                final Rectangle characterBounds = noteLabelAccessibleText.getCharacterBounds(n);
                if (characterBounds != null) {
                    return SwingUtilities.convertRectangle(ProgressMonitor.this.noteLabel, characterBounds, ProgressMonitor.this.pane);
                }
            }
            return null;
        }
        
        private boolean sameWindowAncestor(final Component component, final Component component2) {
            return component != null && component2 != null && SwingUtilities.getWindowAncestor(component) == SwingUtilities.getWindowAncestor(component2);
        }
        
        @Override
        public int getCharCount() {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null) {
                return noteLabelAccessibleText.getCharCount();
            }
            return -1;
        }
        
        @Override
        public int getCaretPosition() {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null) {
                return noteLabelAccessibleText.getCaretPosition();
            }
            return -1;
        }
        
        @Override
        public String getAtIndex(final int n, final int n2) {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null) {
                return noteLabelAccessibleText.getAtIndex(n, n2);
            }
            return null;
        }
        
        @Override
        public String getAfterIndex(final int n, final int n2) {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null) {
                return noteLabelAccessibleText.getAfterIndex(n, n2);
            }
            return null;
        }
        
        @Override
        public String getBeforeIndex(final int n, final int n2) {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null) {
                return noteLabelAccessibleText.getBeforeIndex(n, n2);
            }
            return null;
        }
        
        @Override
        public AttributeSet getCharacterAttribute(final int n) {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null) {
                return noteLabelAccessibleText.getCharacterAttribute(n);
            }
            return null;
        }
        
        @Override
        public int getSelectionStart() {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null) {
                return noteLabelAccessibleText.getSelectionStart();
            }
            return -1;
        }
        
        @Override
        public int getSelectionEnd() {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null) {
                return noteLabelAccessibleText.getSelectionEnd();
            }
            return -1;
        }
        
        @Override
        public String getSelectedText() {
            final AccessibleText noteLabelAccessibleText = this.getNoteLabelAccessibleText();
            if (noteLabelAccessibleText != null) {
                return noteLabelAccessibleText.getSelectedText();
            }
            return null;
        }
    }
}
