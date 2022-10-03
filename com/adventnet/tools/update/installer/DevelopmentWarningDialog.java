package com.adventnet.tools.update.installer;

import java.io.IOException;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.event.WindowListener;
import java.util.logging.Level;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import com.adventnet.tools.update.CommonUtil;
import java.awt.Window;
import java.awt.Dimension;
import java.util.logging.Logger;
import javax.swing.JDialog;

public class DevelopmentWarningDialog extends JDialog
{
    private static final Logger LOGGER;
    private static final long serialVersionUID = -3282192374951585709L;
    private boolean initialized;
    DevelopmentWarningPanel developmentWarningPanel;
    
    public DevelopmentWarningDialog() {
        this.initialized = false;
        this.developmentWarningPanel = new DevelopmentWarningPanel(DevelopmentWarningPanel.Context.PATCH);
        this.pack();
    }
    
    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            this.init();
        }
        super.setVisible(visible);
    }
    
    private void init() {
        if (this.initialized) {
            return;
        }
        this.setSize(this.getPreferredSize().width + 525, this.getPreferredSize().height + 250);
        this.setMinimumSize(new Dimension(this.getPreferredSize().width + 525, this.getPreferredSize().height + 250));
        Assorted.positionTheWindow(this, "Center");
        this.setResizable(true);
        this.setTitle(CommonUtil.getString(MessageConstants.WARNING));
        final Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(this.developmentWarningPanel, "Center");
        this.initActions();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                DevelopmentWarningDialog.this.setVisible(false);
                DevelopmentWarningDialog.LOGGER.log(Level.INFO, "Pressed Exit button");
                System.exit(0);
            }
        });
        this.setModal(true);
        this.initialized = true;
    }
    
    void initActions() {
        this.developmentWarningPanel.advancedButton.addActionListener(new AdvancedButtonAction());
        this.developmentWarningPanel.proceedButton.addActionListener(new ProceedButtonAction());
    }
    
    static {
        LOGGER = Logger.getLogger(DevelopmentWarningDialog.class.getName());
    }
    
    class AdvancedButtonAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (!DevelopmentWarningDialog.this.developmentWarningPanel.advancedDescription.isVisible()) {
                try {
                    UpdateManagerUtil.audit("Pressed Advanced button for L1 consent - self signed patch");
                }
                catch (final IOException e) {
                    DevelopmentWarningDialog.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
                final Dimension dimension = new Dimension(DevelopmentWarningDialog.this.getPreferredSize().width, DevelopmentWarningDialog.this.getPreferredSize().height + 95);
                DevelopmentWarningDialog.this.setSize(dimension);
                DevelopmentWarningDialog.this.setMinimumSize(dimension);
                DevelopmentWarningDialog.this.pack();
                DevelopmentWarningDialog.this.developmentWarningPanel.jSeparator.setVisible(true);
                DevelopmentWarningDialog.this.developmentWarningPanel.advancedDescription.setVisible(true);
                DevelopmentWarningDialog.this.developmentWarningPanel.buttonPanel.remove(1);
                DevelopmentWarningDialog.this.developmentWarningPanel.buttonPanel.add(DevelopmentWarningDialog.this.developmentWarningPanel.proceedButton);
            }
        }
    }
    
    class ProceedButtonAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            try {
                UpdateManagerUtil.audit("Pressed Proceed button for L2 consent - self signed patch");
            }
            catch (final IOException e) {
                DevelopmentWarningDialog.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            DevelopmentWarningDialog.this.dispose();
        }
    }
}
