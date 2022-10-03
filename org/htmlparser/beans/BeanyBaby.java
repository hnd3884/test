package org.htmlparser.beans;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import java.util.Vector;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;

public class BeanyBaby extends JFrame implements PropertyChangeListener, ActionListener, MouseListener
{
    protected Vector mTrail;
    protected int mCrumb;
    private HTMLLinkBean mLinkBean;
    private JMenuItem mForward;
    private JMenuItem mBack;
    private JCheckBoxMenuItem mCollapse;
    private JTextField mTextField;
    private JSplitPane mSplitPane;
    private JCheckBoxMenuItem mLinks;
    private HTMLTextBean mStringBean;
    private JCheckBoxMenuItem mNobreak;
    
    public BeanyBaby() {
        this.initComponents();
        this.mTrail = new Vector();
        this.mCrumb = -1;
        this.setVisible(true);
        this.mSplitPane.setDividerLocation(0.5);
        this.setVisible(false);
        this.mLinkBean.addPropertyChangeListener(this);
        this.mStringBean.addPropertyChangeListener(this);
        this.mTextField.addActionListener(this);
        this.mLinkBean.addMouseListener(this);
        this.mLinks.setSelected(this.mStringBean.getLinks());
        this.mCollapse.setSelected(this.mStringBean.getCollapse());
        this.mNobreak.setSelected(this.mStringBean.getReplaceNonBreakingSpaces());
    }
    
    public void propertyChange(final PropertyChangeEvent event) {
        final Object source = event.getSource();
        if (source == this.mLinkBean) {
            if (!this.mLinkBean.getURL().equals(this.mStringBean.getURL())) {
                this.mStringBean.setURL(this.mLinkBean.getURL());
            }
        }
        else if (source == this.mStringBean) {
            if (!this.mStringBean.getURL().equals(this.mLinkBean.getURL())) {
                this.mLinkBean.setURL(this.mStringBean.getURL());
            }
            final String name = event.getPropertyName();
            if (name.equals("links")) {
                this.mLinks.setSelected((boolean)event.getNewValue());
            }
            else if (name.equals("collapse")) {
                this.mCollapse.setSelected((boolean)event.getNewValue());
            }
            else if (name.equals("replaceNonBreakingSpaces")) {
                this.mNobreak.setSelected((boolean)event.getNewValue());
            }
        }
    }
    
    public void actionPerformed(final ActionEvent event) {
        final Object source = event.getSource();
        if (source == this.mTextField) {
            final String url = this.mTextField.getText();
            this.mTextField.selectAll();
            this.setURL(url);
        }
        else if (source instanceof JCheckBoxMenuItem) {
            final JMenuItem item = (JMenuItem)source;
            final String name = item.getName();
            if ("Links".equals(name)) {
                this.mStringBean.setLinks(item.isSelected());
            }
            else if ("Collapse".equals(name)) {
                this.mStringBean.setCollapse(item.isSelected());
            }
            else if ("Nobreak".equals(name)) {
                this.mStringBean.setReplaceNonBreakingSpaces(item.isSelected());
            }
        }
        else if (source instanceof JMenuItem) {
            final String name = ((JMenuItem)source).getName();
            if ("Back".equals(name)) {
                if (this.mCrumb > 0) {
                    --this.mCrumb;
                    final String url = this.mTrail.elementAt(this.mCrumb);
                    --this.mCrumb;
                    this.setURL(url);
                }
            }
            else if ("Forward".equals(name) && this.mCrumb < this.mTrail.size()) {
                ++this.mCrumb;
                final String url = this.mTrail.elementAt(this.mCrumb);
                --this.mCrumb;
                this.setURL(url);
            }
        }
    }
    
    public void mouseClicked(final MouseEvent event) {
        if (2 == event.getClickCount()) {
            final int index = this.mLinkBean.locationToIndex(event.getPoint());
            final String url = this.mLinkBean.getModel().getElementAt(index).toString();
            this.setURL(url);
        }
    }
    
    public void mouseEntered(final MouseEvent event) {
    }
    
    public void mouseExited(final MouseEvent event) {
    }
    
    public void mousePressed(final MouseEvent event) {
    }
    
    public void mouseReleased(final MouseEvent event) {
    }
    
    public void setURL(final String url) {
        this.mTextField.setText(url);
        ++this.mCrumb;
        if (this.mTrail.size() <= this.mCrumb) {
            this.mTrail.addElement(url);
        }
        else {
            this.mTrail.setElementAt(url, this.mCrumb);
        }
        this.mLinkBean.setURL(url);
        this.mBack.setEnabled(this.mCrumb > 0);
        this.mForward.setEnabled(this.mCrumb + 1 < this.mTrail.size());
    }
    
    private void initComponents() {
        final JMenuBar menubar = new JMenuBar();
        this.setJMenuBar(menubar);
        final JMenu go = new JMenu();
        this.mBack = new JMenuItem();
        this.mForward = new JMenuItem();
        final JMenu options = new JMenu();
        this.mLinks = new JCheckBoxMenuItem();
        this.mCollapse = new JCheckBoxMenuItem();
        this.mNobreak = new JCheckBoxMenuItem();
        final JPanel panel = new JPanel();
        this.mSplitPane = new JSplitPane();
        final JScrollPane pane1 = new JScrollPane();
        this.mLinkBean = new HTMLLinkBean();
        final JScrollPane pane2 = new JScrollPane();
        this.mStringBean = new HTMLTextBean();
        this.mTextField = new JTextField();
        go.setMnemonic('G');
        go.setText("Go");
        go.setToolTipText("crude URL navigation");
        this.mBack.setMnemonic('B');
        this.mBack.setText("Back");
        this.mBack.setToolTipText("back one URL");
        this.mBack.setName("Back");
        this.mBack.addActionListener(this);
        go.add(this.mBack);
        this.mForward.setMnemonic('F');
        this.mForward.setText("Forward");
        this.mForward.setToolTipText("forward one URL");
        this.mForward.setName("Forward");
        this.mForward.addActionListener(this);
        go.add(this.mForward);
        menubar.add(go);
        options.setMnemonic('O');
        options.setText("Options");
        options.setToolTipText("Bean settings");
        this.mLinks.setMnemonic('L');
        this.mLinks.setText("Links");
        this.mLinks.setToolTipText("show/hide links in text");
        this.mLinks.setName("Links");
        this.mLinks.addActionListener(this);
        options.add(this.mLinks);
        this.mCollapse.setMnemonic('C');
        this.mCollapse.setText("Collapse");
        this.mCollapse.setToolTipText("collapse/retain whitespace sequences");
        this.mCollapse.setName("Collapse");
        this.mCollapse.addActionListener(this);
        options.add(this.mCollapse);
        this.mNobreak.setMnemonic('N');
        this.mNobreak.setText("Non-breaking Spaces");
        this.mNobreak.setToolTipText("replace/retain non-breaking spaces");
        this.mNobreak.setName("Nobreak");
        this.mNobreak.addActionListener(this);
        options.add(this.mNobreak);
        menubar.add(options);
        this.setTitle("BeanyBaby");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent evt) {
                BeanyBaby.this.exitForm(evt);
            }
        });
        panel.setLayout(new BorderLayout());
        pane1.setViewportView(this.mLinkBean);
        this.mSplitPane.setLeftComponent(pane1);
        pane2.setViewportView(this.mStringBean);
        this.mSplitPane.setRightComponent(pane2);
        panel.add(this.mSplitPane, "Center");
        this.mTextField.setToolTipText("Enter the URL to view");
        panel.add(this.mTextField, "South");
        this.getContentPane().add(panel, "Center");
        this.pack();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(new Dimension(640, 480));
        this.setLocation((screenSize.width - 640) / 2, (screenSize.height - 480) / 2);
    }
    
    private void exitForm(final WindowEvent event) {
        System.exit(0);
    }
    
    public static void main(final String[] args) {
        final BeanyBaby bb = new BeanyBaby();
        bb.setVisible(true);
        if (0 >= args.length) {
            bb.setURL("http://www.slashdot.org");
        }
        else {
            bb.setURL(args[0]);
        }
    }
}
