package sun.swing;

import java.awt.print.PrinterException;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import java.awt.print.Printable;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import java.awt.event.WindowListener;
import javax.swing.JViewport;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.event.WindowAdapter;
import javax.swing.Action;
import java.util.concurrent.atomic.AtomicBoolean;
import java.text.MessageFormat;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.Component;
import java.awt.print.PrinterJob;

public class PrintingStatus
{
    private final PrinterJob job;
    private final Component parent;
    private JDialog abortDialog;
    private JButton abortButton;
    private JLabel statusLabel;
    private MessageFormat statusFormat;
    private final AtomicBoolean isAborted;
    private final Action abortAction;
    private final WindowAdapter closeListener;
    
    public static PrintingStatus createPrintingStatus(final Component component, final PrinterJob printerJob) {
        return new PrintingStatus(component, printerJob);
    }
    
    protected PrintingStatus(final Component parent, final PrinterJob job) {
        this.isAborted = new AtomicBoolean(false);
        this.abortAction = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                if (!PrintingStatus.this.isAborted.get()) {
                    PrintingStatus.this.isAborted.set(true);
                    PrintingStatus.this.abortButton.setEnabled(false);
                    PrintingStatus.this.abortDialog.setTitle(UIManager.getString("PrintingDialog.titleAbortingText"));
                    PrintingStatus.this.statusLabel.setText(UIManager.getString("PrintingDialog.contentAbortingText"));
                    PrintingStatus.this.job.cancel();
                }
            }
        };
        this.closeListener = new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                PrintingStatus.this.abortAction.actionPerformed(null);
            }
        };
        this.job = job;
        this.parent = parent;
    }
    
    private void init() {
        final String string = UIManager.getString("PrintingDialog.titleProgressText");
        final String string2 = UIManager.getString("PrintingDialog.contentInitialText");
        this.statusFormat = new MessageFormat(UIManager.getString("PrintingDialog.contentProgressText"));
        final String string3 = UIManager.getString("PrintingDialog.abortButtonText");
        final String string4 = UIManager.getString("PrintingDialog.abortButtonToolTipText");
        final int int1 = getInt("PrintingDialog.abortButtonMnemonic", -1);
        final int int2 = getInt("PrintingDialog.abortButtonDisplayedMnemonicIndex", -1);
        (this.abortButton = new JButton(string3)).addActionListener(this.abortAction);
        this.abortButton.setToolTipText(string4);
        if (int1 != -1) {
            this.abortButton.setMnemonic(int1);
        }
        if (int2 != -1) {
            this.abortButton.setDisplayedMnemonicIndex(int2);
        }
        this.statusLabel = new JLabel(string2);
        final JOptionPane optionPane = new JOptionPane(this.statusLabel, 1, -1, null, new Object[] { this.abortButton }, this.abortButton);
        optionPane.getActionMap().put("close", this.abortAction);
        if (this.parent != null && this.parent.getParent() instanceof JViewport) {
            this.abortDialog = optionPane.createDialog(this.parent.getParent(), string);
        }
        else {
            this.abortDialog = optionPane.createDialog(this.parent, string);
        }
        this.abortDialog.setDefaultCloseOperation(0);
        this.abortDialog.addWindowListener(this.closeListener);
    }
    
    public void showModal(final boolean b) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.showModalOnEDT(b);
        }
        else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        PrintingStatus.this.showModalOnEDT(b);
                    }
                });
            }
            catch (final InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            catch (final InvocationTargetException ex2) {
                final Throwable cause = ex2.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw new RuntimeException(cause);
            }
        }
    }
    
    private void showModalOnEDT(final boolean modal) {
        assert SwingUtilities.isEventDispatchThread();
        this.init();
        this.abortDialog.setModal(modal);
        this.abortDialog.setVisible(true);
    }
    
    public void dispose() {
        if (SwingUtilities.isEventDispatchThread()) {
            this.disposeOnEDT();
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    PrintingStatus.this.disposeOnEDT();
                }
            });
        }
    }
    
    private void disposeOnEDT() {
        assert SwingUtilities.isEventDispatchThread();
        if (this.abortDialog != null) {
            this.abortDialog.removeWindowListener(this.closeListener);
            this.abortDialog.dispose();
            this.abortDialog = null;
        }
    }
    
    public boolean isAborted() {
        return this.isAborted.get();
    }
    
    public Printable createNotificationPrintable(final Printable printable) {
        return new NotificationPrintable(printable);
    }
    
    static int getInt(final Object o, final int n) {
        final Object value = UIManager.get(o);
        if (value instanceof Integer) {
            return (int)value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            }
            catch (final NumberFormatException ex) {}
        }
        return n;
    }
    
    private class NotificationPrintable implements Printable
    {
        private final Printable printDelegatee;
        
        public NotificationPrintable(final Printable printDelegatee) {
            if (printDelegatee == null) {
                throw new NullPointerException("Printable is null");
            }
            this.printDelegatee = printDelegatee;
        }
        
        @Override
        public int print(final Graphics graphics, final PageFormat pageFormat, final int n) throws PrinterException {
            final int print = this.printDelegatee.print(graphics, pageFormat, n);
            if (print != 1 && !PrintingStatus.this.isAborted()) {
                if (SwingUtilities.isEventDispatchThread()) {
                    this.updateStatusOnEDT(n);
                }
                else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            NotificationPrintable.this.updateStatusOnEDT(n);
                        }
                    });
                }
            }
            return print;
        }
        
        private void updateStatusOnEDT(final int n) {
            assert SwingUtilities.isEventDispatchThread();
            PrintingStatus.this.statusLabel.setText(PrintingStatus.this.statusFormat.format(new Object[] { new Integer(n + 1) }));
        }
    }
}
