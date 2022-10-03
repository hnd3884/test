package HTTPClient;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.event.WindowListener;
import java.awt.TextField;
import java.awt.Label;
import java.awt.Dimension;
import java.awt.Frame;

class SimpleAuthPopup implements AuthorizationPrompter
{
    private static BasicAuthBox inp;
    
    public NVPair getUsernamePassword(final AuthorizationInfo challenge, final boolean forProxy) {
        String line1;
        String line2;
        String line3;
        if (challenge.getScheme().equalsIgnoreCase("SOCKS5")) {
            line1 = "Enter username and password for SOCKS server on host";
            line2 = challenge.getHost();
            line3 = "Authentication Method: username/password";
        }
        else {
            line1 = "Enter username and password for realm `" + challenge.getRealm() + "'";
            line2 = "on host " + challenge.getHost() + ":" + challenge.getPort();
            line3 = "Authentication Scheme: " + challenge.getScheme();
        }
        synchronized (this.getClass()) {
            if (SimpleAuthPopup.inp == null) {
                SimpleAuthPopup.inp = new BasicAuthBox();
            }
            monitorexit(this.getClass());
        }
        return SimpleAuthPopup.inp.getInput(line1, line2, line3, challenge.getScheme());
    }
    
    static {
        SimpleAuthPopup.inp = null;
    }
    
    private static class BasicAuthBox extends Frame
    {
        private static final String title = "Authorization Request";
        private Dimension screen;
        private Label line1;
        private Label line2;
        private Label line3;
        private TextField user;
        private TextField pass;
        private int done;
        private static final int OK = 1;
        private static final int CANCEL = 0;
        
        BasicAuthBox() {
            super("Authorization Request");
            this.screen = this.getToolkit().getScreenSize();
            this.addNotify();
            this.addWindowListener(new Close());
            this.setLayout(new BorderLayout());
            Panel p = new Panel(new GridLayout(3, 1));
            p.add(this.line1 = new Label());
            p.add(this.line2 = new Label());
            p.add(this.line3 = new Label());
            this.add("North", p);
            p = new Panel(new GridLayout(2, 1));
            p.add(new Label("Username:"));
            p.add(new Label("Password:"));
            this.add("West", p);
            p = new Panel(new GridLayout(2, 1));
            p.add(this.user = new TextField(30));
            p.add(this.pass = new TextField(30));
            this.pass.addActionListener(new Ok());
            this.pass.setEchoChar('*');
            this.add("East", p);
            final GridBagLayout gb = new GridBagLayout();
            p = new Panel(gb);
            final GridBagConstraints constr = new GridBagConstraints();
            final Panel pp = new Panel();
            p.add(pp);
            constr.gridwidth = 0;
            gb.setConstraints(pp, constr);
            constr.gridwidth = 1;
            constr.weightx = 1.0;
            Button b;
            p.add(b = new Button("  OK  "));
            b.addActionListener(new Ok());
            constr.weightx = 1.0;
            gb.setConstraints(b, constr);
            p.add(b = new Button("Clear"));
            b.addActionListener(new Clear());
            constr.weightx = 2.0;
            gb.setConstraints(b, constr);
            p.add(b = new Button("Cancel"));
            b.addActionListener(new Cancel());
            constr.weightx = 1.0;
            gb.setConstraints(b, constr);
            this.add("South", p);
            this.pack();
        }
        
        synchronized NVPair getInput(final String l1, final String l2, final String l3, final String scheme) {
            this.line1.setText(l1);
            this.line2.setText(l2);
            this.line3.setText(l3);
            this.line1.invalidate();
            this.line2.invalidate();
            this.line3.invalidate();
            this.setResizable(true);
            this.pack();
            this.setResizable(false);
            this.setLocation((this.screen.width - this.getPreferredSize().width) / 2, (int)((this.screen.height - this.getPreferredSize().height) / 2 * 0.7));
            boolean user_focus = true;
            if (scheme.equalsIgnoreCase("NTLM")) {
                try {
                    this.user.setText(System.getProperty("user.name", ""));
                    user_focus = false;
                }
                catch (final SecurityException ex) {}
            }
            this.setVisible(true);
            if (user_focus) {
                this.user.requestFocus();
            }
            else {
                this.pass.requestFocus();
            }
            try {
                this.wait();
            }
            catch (final InterruptedException ex2) {}
            this.setVisible(false);
            final NVPair result = new NVPair(this.user.getText(), this.pass.getText());
            this.user.setText("");
            this.pass.setText("");
            if (this.done == 0) {
                return null;
            }
            return result;
        }
        
        static /* synthetic */ int access$0() {
            return 1;
        }
        
        static /* synthetic */ void access$2(final BasicAuthBox $0, final int $1) {
            $0.done = $1;
        }
        
        static /* synthetic */ int access$5() {
            return 0;
        }
        
        private class Ok implements ActionListener
        {
            public void actionPerformed(final ActionEvent ae) {
                BasicAuthBox.access$2(BasicAuthBox.this, BasicAuthBox.access$0());
                synchronized (BasicAuthBox.this) {
                    BasicAuthBox.this.notifyAll();
                    monitorexit(BasicAuthBox.this);
                }
            }
            
            Ok() {
            }
        }
        
        private class Clear implements ActionListener
        {
            public void actionPerformed(final ActionEvent ae) {
                BasicAuthBox.this.user.setText("");
                BasicAuthBox.this.pass.setText("");
                BasicAuthBox.this.user.requestFocus();
            }
            
            Clear() {
            }
        }
        
        private class Cancel implements ActionListener
        {
            public void actionPerformed(final ActionEvent ae) {
                BasicAuthBox.access$2(BasicAuthBox.this, BasicAuthBox.access$5());
                synchronized (BasicAuthBox.this) {
                    BasicAuthBox.this.notifyAll();
                    monitorexit(BasicAuthBox.this);
                }
            }
            
            Cancel() {
            }
        }
        
        private class Close extends WindowAdapter
        {
            public void windowClosing(final WindowEvent we) {
                new Cancel().actionPerformed(null);
            }
            
            Close() {
            }
        }
    }
}
