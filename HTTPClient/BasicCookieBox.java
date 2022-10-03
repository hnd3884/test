package HTTPClient;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.awt.event.WindowListener;
import java.awt.Button;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.Panel;
import java.awt.Label;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Frame;

class BasicCookieBox extends Frame
{
    private static final String title = "Set Cookie Request";
    private Dimension screen;
    private GridBagConstraints constr;
    private Label name_value_label;
    private Label domain_value;
    private Label ports_label;
    private Label ports_value;
    private Label path_value;
    private Label expires_value;
    private Label discard_note;
    private Label secure_note;
    private Label c_url_note;
    private Panel left_panel;
    private Panel right_panel;
    private Label comment_label;
    private TextArea comment_value;
    private TextField domain;
    private Button default_focus;
    private boolean accept;
    private boolean accept_domain;
    
    BasicCookieBox() {
        super("Set Cookie Request");
        this.screen = this.getToolkit().getScreenSize();
        this.addNotify();
        this.addWindowListener(new Close());
        final GridBagLayout layout;
        this.setLayout(layout = new GridBagLayout());
        this.constr = new GridBagConstraints();
        this.constr.gridwidth = 0;
        this.constr.anchor = 17;
        this.add(new Label("The server would like to set the following cookie:"), this.constr);
        Panel p = new Panel();
        (this.left_panel = new Panel()).setLayout(new GridLayout(4, 1));
        this.left_panel.add(new Label("Name=Value:"));
        this.left_panel.add(new Label("Domain:"));
        this.left_panel.add(new Label("Path:"));
        this.left_panel.add(new Label("Expires:"));
        this.ports_label = new Label("Ports:");
        p.add(this.left_panel);
        (this.right_panel = new Panel()).setLayout(new GridLayout(4, 1));
        this.right_panel.add(this.name_value_label = new Label());
        this.right_panel.add(this.domain_value = new Label());
        this.right_panel.add(this.path_value = new Label());
        this.right_panel.add(this.expires_value = new Label());
        this.ports_value = new Label();
        p.add(this.right_panel);
        this.add(p, this.constr);
        this.secure_note = new Label("This cookie will only be sent over secure connections");
        this.discard_note = new Label("This cookie will be discarded at the end of the session");
        this.c_url_note = new Label("");
        this.comment_label = new Label("Comment:");
        (this.comment_value = new TextArea("", 3, 45, 1)).setEditable(false);
        this.add(new Panel(), this.constr);
        this.constr.gridwidth = 1;
        this.constr.anchor = 10;
        this.constr.weightx = 1.0;
        this.add(this.default_focus = new Button("Accept"), this.constr);
        this.default_focus.addActionListener(new Accept());
        this.constr.gridwidth = 0;
        Button b;
        this.add(b = new Button("Reject"), this.constr);
        b.addActionListener(new Reject());
        this.constr.weightx = 0.0;
        p = new Separator();
        this.constr.fill = 2;
        this.add(p, this.constr);
        this.constr.fill = 0;
        this.constr.anchor = 17;
        this.add(new Label("Accept/Reject all cookies from a host or domain:"), this.constr);
        p = new Panel();
        p.add(new Label("Host/Domain:"));
        p.add(this.domain = new TextField(30));
        this.add(p, this.constr);
        this.add(new Label("domains are characterized by a leading dot (`.');"), this.constr);
        this.add(new Label("an empty string matches all hosts"), this.constr);
        this.constr.anchor = 10;
        this.constr.gridwidth = 1;
        this.constr.weightx = 1.0;
        this.add(b = new Button("Accept All"), this.constr);
        b.addActionListener(new AcceptDomain());
        this.constr.gridwidth = 0;
        this.add(b = new Button("Reject All"), this.constr);
        b.addActionListener(new RejectDomain());
        this.pack();
        this.constr.anchor = 17;
        this.constr.gridwidth = 0;
    }
    
    public Dimension getMaximumSize() {
        return new Dimension(this.screen.width * 3 / 4, this.screen.height * 3 / 4);
    }
    
    public synchronized boolean accept(final Cookie cookie, final DefaultCookiePolicyHandler h, final String server) {
        this.name_value_label.setText(String.valueOf(cookie.getName()) + "=" + cookie.getValue());
        this.domain_value.setText(cookie.getDomain());
        this.path_value.setText(cookie.getPath());
        if (cookie.expires() == null) {
            this.expires_value.setText("never");
        }
        else {
            this.expires_value.setText(cookie.expires().toString());
        }
        int pos = 2;
        if (cookie.isSecure()) {
            this.add(this.secure_note, this.constr, pos++);
        }
        if (cookie.discard()) {
            this.add(this.discard_note, this.constr, pos++);
        }
        if (cookie instanceof Cookie2) {
            final Cookie2 cookie2 = (Cookie2)cookie;
            if (cookie2.getPorts() != null) {
                ((GridLayout)this.left_panel.getLayout()).setRows(5);
                this.left_panel.add(this.ports_label, 2);
                ((GridLayout)this.right_panel.getLayout()).setRows(5);
                final int[] ports = cookie2.getPorts();
                final StringBuffer plist = new StringBuffer();
                plist.append(ports[0]);
                for (int idx = 1; idx < ports.length; ++idx) {
                    plist.append(", ");
                    plist.append(ports[idx]);
                }
                this.ports_value.setText(plist.toString());
                this.right_panel.add(this.ports_value, 2);
            }
            if (cookie2.getCommentURL() != null) {
                this.c_url_note.setText("For more info on this cookie see: " + cookie2.getCommentURL());
                this.add(this.c_url_note, this.constr, pos++);
            }
            if (cookie2.getComment() != null) {
                this.comment_value.setText(cookie2.getComment());
                this.add(this.comment_label, this.constr, pos++);
                this.add(this.comment_value, this.constr, pos++);
            }
        }
        this.name_value_label.invalidate();
        this.domain_value.invalidate();
        this.ports_value.invalidate();
        this.path_value.invalidate();
        this.expires_value.invalidate();
        this.left_panel.invalidate();
        this.right_panel.invalidate();
        this.secure_note.invalidate();
        this.discard_note.invalidate();
        this.c_url_note.invalidate();
        this.comment_value.invalidate();
        this.invalidate();
        this.domain.setText(cookie.getDomain());
        this.setResizable(true);
        this.pack();
        this.setResizable(false);
        this.setLocation((this.screen.width - this.getPreferredSize().width) / 2, (int)((this.screen.height - this.getPreferredSize().height) / 2 * 0.7));
        this.setVisible(true);
        this.default_focus.requestFocus();
        try {
            this.wait();
        }
        catch (final InterruptedException ex) {}
        this.setVisible(false);
        this.remove(this.secure_note);
        this.remove(this.discard_note);
        this.left_panel.remove(this.ports_label);
        ((GridLayout)this.left_panel.getLayout()).setRows(4);
        this.right_panel.remove(this.ports_value);
        ((GridLayout)this.right_panel.getLayout()).setRows(4);
        this.remove(this.c_url_note);
        this.remove(this.comment_label);
        this.remove(this.comment_value);
        if (this.accept_domain) {
            final String dom = this.domain.getText().trim().toLowerCase();
            if (this.accept) {
                h.addAcceptDomain(dom);
            }
            else {
                h.addRejectDomain(dom);
            }
        }
        return this.accept;
    }
    
    static /* synthetic */ void access$1(final BasicCookieBox $0, final boolean $1) {
        $0.accept = $1;
    }
    
    static /* synthetic */ void access$3(final BasicCookieBox $0, final boolean $1) {
        $0.accept_domain = $1;
    }
    
    private class Accept implements ActionListener
    {
        public void actionPerformed(final ActionEvent ae) {
            BasicCookieBox.access$1(BasicCookieBox.this, true);
            BasicCookieBox.access$3(BasicCookieBox.this, false);
            synchronized (BasicCookieBox.this) {
                BasicCookieBox.this.notifyAll();
                monitorexit(BasicCookieBox.this);
            }
        }
        
        Accept() {
        }
    }
    
    private class Reject implements ActionListener
    {
        public void actionPerformed(final ActionEvent ae) {
            BasicCookieBox.access$1(BasicCookieBox.this, false);
            BasicCookieBox.access$3(BasicCookieBox.this, false);
            synchronized (BasicCookieBox.this) {
                BasicCookieBox.this.notifyAll();
                monitorexit(BasicCookieBox.this);
            }
        }
        
        Reject() {
        }
    }
    
    private class AcceptDomain implements ActionListener
    {
        public void actionPerformed(final ActionEvent ae) {
            BasicCookieBox.access$1(BasicCookieBox.this, true);
            BasicCookieBox.access$3(BasicCookieBox.this, true);
            synchronized (BasicCookieBox.this) {
                BasicCookieBox.this.notifyAll();
                monitorexit(BasicCookieBox.this);
            }
        }
        
        AcceptDomain() {
        }
    }
    
    private class RejectDomain implements ActionListener
    {
        public void actionPerformed(final ActionEvent ae) {
            BasicCookieBox.access$1(BasicCookieBox.this, false);
            BasicCookieBox.access$3(BasicCookieBox.this, true);
            synchronized (BasicCookieBox.this) {
                BasicCookieBox.this.notifyAll();
                monitorexit(BasicCookieBox.this);
            }
        }
        
        RejectDomain() {
        }
    }
    
    private class Close extends WindowAdapter
    {
        public void windowClosing(final WindowEvent we) {
            new Reject().actionPerformed(null);
        }
        
        Close() {
        }
    }
}
