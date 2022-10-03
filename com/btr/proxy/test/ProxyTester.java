package com.btr.proxy.test;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import java.net.Proxy;
import java.util.List;
import java.net.URL;
import java.net.ProxySelector;
import javax.swing.JOptionPane;
import java.text.MessageFormat;
import com.btr.proxy.util.Logger;
import java.awt.Container;
import javax.swing.JScrollPane;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.btr.proxy.search.ProxySearch;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

public class ProxyTester extends JFrame
{
    private static final long serialVersionUID = 1L;
    private JComboBox modes;
    private JButton testButton;
    private JTextField urlField;
    private JTextArea logArea;
    
    public ProxyTester() {
        this.init();
    }
    
    private void init() {
        this.setTitle("Proxy Vole Tester");
        this.setDefaultCloseOperation(3);
        final JPanel p = new JPanel();
        p.add(new JLabel("Mode:"));
        p.add(this.modes = new JComboBox((E[])ProxySearch.Strategy.values()));
        p.add(new JLabel("URL:"));
        (this.urlField = new JTextField(30)).setText("http://proxy-vole.kenai.com");
        p.add(this.urlField);
        (this.testButton = new JButton("Test")).addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                ProxyTester.this.testUrl();
            }
        });
        p.add(this.testButton);
        this.logArea = new JTextArea(5, 50);
        final JPanel contenPane = new JPanel(new BorderLayout());
        contenPane.add(p, "North");
        contenPane.add(new JScrollPane(this.logArea), "Center");
        this.setContentPane(contenPane);
        this.pack();
        this.setLocationRelativeTo(null);
        this.installLogger();
    }
    
    private void installLogger() {
        Logger.setBackend(new Logger.LogBackEnd() {
            public void log(final Class<?> clazz, final Logger.LogLevel loglevel, final String msg, final Object... params) {
                ProxyTester.this.logArea.append(loglevel + "\t" + MessageFormat.format(msg, params) + "\n");
            }
            
            public boolean isLogginEnabled(final Logger.LogLevel logLevel) {
                return true;
            }
        });
    }
    
    protected void testUrl() {
        try {
            if (this.urlField.getText().trim().length() == 0) {
                JOptionPane.showMessageDialog(this, "Please enter an URL first.");
                return;
            }
            this.logArea.setText("");
            final ProxySearch.Strategy pss = (ProxySearch.Strategy)this.modes.getSelectedItem();
            final ProxySearch ps = new ProxySearch();
            ps.addStrategy(pss);
            final ProxySelector psel = ps.getProxySelector();
            if (psel == null) {
                JOptionPane.showMessageDialog(this, "No proxy settings available for this mode.");
                return;
            }
            ProxySelector.setDefault(psel);
            final URL url = new URL(this.urlField.getText().trim());
            final List<Proxy> result = psel.select(url.toURI());
            if (result == null || result.size() == 0) {
                JOptionPane.showMessageDialog(this, "No proxy found for this url.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Proxy Settings found using " + pss + " strategy.\n" + "Proxy used for URL is: " + result.get(0));
        }
        catch (final Exception e) {
            JOptionPane.showMessageDialog(this, "Error:" + e.getMessage(), "Error checking URL.", 0);
        }
    }
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setLookAndFeel();
                final ProxyTester mainFrame = new ProxyTester();
                mainFrame.setVisible(true);
            }
        });
    }
    
    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final Exception ex) {}
    }
}
