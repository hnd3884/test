package com.adventnet.tools.update.installer;

import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import java.util.HashMap;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.File;
import javax.swing.JRootPane;
import java.awt.event.MouseEvent;
import java.awt.Window;
import java.awt.Cursor;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Component;
import javax.swing.SwingUtilities;
import com.adventnet.tools.update.CommonUtil;
import java.awt.event.ActionEvent;
import javax.swing.Icon;
import java.applet.Applet;
import javax.swing.event.ChangeEvent;
import java.util.ResourceBundle;
import java.awt.Dimension;
import java.net.URL;
import java.util.Map;
import java.lang.ref.SoftReference;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public final class ContextSensitiveHelpButton extends JButton implements ActionListener, MouseMotionListener, MouseListener, ChangeListener
{
    private static Logger out;
    private ImageIcon helpIcon;
    private ImageIcon noHelpIcon;
    private JLabel displayLbl;
    private JComponent glassPnl;
    private SoftReference mapRef;
    private Map mapping;
    private URL contextHelpXml;
    private URL prefixURL;
    ContextSensitiveHelpWindow csWin;
    public static URL rootURL;
    private Dimension windowSize;
    private ResourceBundle myBundle;
    
    @Override
    public void stateChanged(final ChangeEvent e) {
        this.cleanUp();
    }
    
    public ContextSensitiveHelpButton() {
        this((ResourceBundle)null);
    }
    
    public ContextSensitiveHelpButton(final ResourceBundle bundle) {
        this("./images/help_contextual.png", "./images/nohelp_contextual.png", bundle);
    }
    
    public ContextSensitiveHelpButton(final String helpIconPath, final String noHelpAvailableIconPath) {
        this(helpIconPath, noHelpAvailableIconPath, null);
    }
    
    public ContextSensitiveHelpButton(final String helpIconPath, final String noHelpAvailableIconPath, final ResourceBundle bundle) {
        this.helpIcon = null;
        this.noHelpIcon = null;
        this.windowSize = new Dimension(450, 150);
        this.myBundle = null;
        this.myBundle = bundle;
        this.helpIcon = Utility.findImage(helpIconPath, null);
        this.noHelpIcon = Utility.findImage(noHelpAvailableIconPath, null);
        this.setIcon(this.helpIcon);
        this.addActionListener(this);
        this.setText(this.getString("Help"));
        this.setName("Help");
    }
    
    @Override
    public boolean isFocusTraversable() {
        return false;
    }
    
    public void setHelpAvailableIcon(final String helpIcon) {
        this.helpIcon = Utility.findImage(helpIcon, null);
    }
    
    public void setNoHelpAvailableIcon(final String noHelpIcon) {
        this.noHelpIcon = Utility.findImage(noHelpIcon, null);
    }
    
    public void setHelpWindowSize(final Dimension d) {
        if (d != null) {
            this.windowSize = d;
        }
    }
    
    public Dimension getHelpWindowSize() {
        return this.windowSize;
    }
    
    public void setPrefixURL(final URL prefixURLArg) {
        this.prefixURL = prefixURLArg;
    }
    
    public void setXmlURL(final URL contextHelpXmlArg) {
        this.contextHelpXml = contextHelpXmlArg;
    }
    
    @Override
    public void actionPerformed(final ActionEvent aEvtArg) {
        if (aEvtArg.getSource() == this) {
            try {
                this.mapping = this.parse(this.contextHelpXml);
                this.showGlassPanel();
            }
            catch (final Throwable ex) {
                this.displayError(CommonUtil.getString(MessageConstants.CANNOT_DISPLAY_CONTEXT_HELP), ex);
            }
        }
        else if (aEvtArg.getActionCommand().equals("DISAPPEARED")) {
            this.csWin.dispose();
            this.csWin = null;
            this.glassPnl.setVisible(false);
            this.cleanUp();
        }
    }
    
    public void showGlassPanel() {
        if (this.glassPnl == null) {
            final Window win = SwingUtilities.windowForComponent(this);
            if (win == null) {
                throw new RuntimeException("Cannot show context sensitive help. Not added to any window");
            }
            if (win instanceof JFrame) {
                this.glassPnl = (JComponent)((JFrame)win).getGlassPane();
            }
            else {
                if (!(win instanceof JDialog)) {
                    throw new RuntimeException("Parent window is not a JFrame or JDialog");
                }
                this.glassPnl = (JComponent)((JDialog)win).getGlassPane();
            }
            this.glassPnl.addMouseListener(this);
            this.glassPnl.addMouseMotionListener(this);
            (this.displayLbl = new JLabel()).setIcon(this.helpIcon);
            this.displayLbl.setVisible(false);
            this.glassPnl.add(this.displayLbl);
            final Dimension dimSize = this.displayLbl.getPreferredSize();
            final int width = this.helpIcon.getIconWidth();
            final int height = this.helpIcon.getIconHeight();
            dimSize.width += width;
            if (dimSize.height < height) {
                dimSize.height = height;
            }
            this.displayLbl.setSize(dimSize);
            this.glassPnl.setCursor(Cursor.getPredefinedCursor(0));
            this.glassPnl.setVisible(true);
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent mEvtArg) {
        this.displayLbl.setLocation(mEvtArg.getX() + 10, mEvtArg.getY());
        this.displayLbl.setVisible(true);
        final JComponent contentPane = (JComponent)((JRootPane)this.glassPnl.getParent()).getContentPane();
        mEvtArg = SwingUtilities.convertMouseEvent(this.glassPnl, mEvtArg, contentPane);
        final Component comp = SwingUtilities.getDeepestComponentAt(contentPane, mEvtArg.getX(), mEvtArg.getY());
        if (comp != null) {
            String name = comp.getName();
            for (Component parComp = comp.getParent(); name == null && parComp != null; name = parComp.getName(), parComp = parComp.getParent()) {}
            if (name != null) {
                final String message = this.mapping.get(name.toLowerCase());
                final String url = this.mapping.get(name.toLowerCase() + "_URL");
                if (message != null) {
                    this.displayLbl.setIcon(this.helpIcon);
                }
                else {
                    this.displayLbl.setIcon(this.noHelpIcon);
                }
            }
            else {
                this.displayLbl.setIcon(this.noHelpIcon);
            }
        }
        else {
            this.displayLbl.setIcon(this.noHelpIcon);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent mEvtArg) {
        if (this.csWin == null) {
            if (this.glassPnl != null && this.displayLbl != null) {
                this.glassPnl.remove(this.displayLbl);
                this.glassPnl.setCursor(Cursor.getPredefinedCursor(0));
                final JComponent contentPane = (JComponent)((JRootPane)this.glassPnl.getParent()).getContentPane();
                mEvtArg = SwingUtilities.convertMouseEvent(this.glassPnl, mEvtArg, contentPane);
                final Component comp = SwingUtilities.getDeepestComponentAt(contentPane, mEvtArg.getX(), mEvtArg.getY());
                if (comp != null) {
                    String name = comp.getName();
                    for (Component parComp = comp.getParent(); name == null && parComp != null; name = parComp.getName(), parComp = parComp.getParent()) {}
                    if (name != null) {
                        final String message = this.mapping.get(name.toLowerCase());
                        String url = this.mapping.get(name.toLowerCase() + "_URL");
                        if (message != null) {
                            final Window win = SwingUtilities.windowForComponent(this);
                            if (win instanceof JFrame) {
                                if (this.myBundle != null) {
                                    this.csWin = new ContextSensitiveHelpWindow((JFrame)win, this.myBundle);
                                }
                                else {
                                    this.csWin = new ContextSensitiveHelpWindow((JFrame)win);
                                }
                            }
                            else if (this.myBundle != null) {
                                this.csWin = new ContextSensitiveHelpWindow((JDialog)win, this.myBundle);
                            }
                            else {
                                this.csWin = new ContextSensitiveHelpWindow((JDialog)win);
                            }
                            this.csWin.setSize(this.windowSize);
                            this.csWin.setActionListener(this);
                            try {
                                URL mainHelp = null;
                                if (url != null && this.prefixURL != null) {
                                    if (url.startsWith("/")) {
                                        final String urlAbsPath = url.substring(url.indexOf(46));
                                        try {
                                            url = "file:////" + new File(urlAbsPath).getCanonicalPath();
                                        }
                                        catch (final IOException io) {
                                            url = "file:////" + new File(urlAbsPath).getAbsolutePath();
                                        }
                                    }
                                    mainHelp = new URL(this.prefixURL, url);
                                    if (!this.validateURL(mainHelp)) {
                                        mainHelp = null;
                                    }
                                }
                                else if (url != null) {
                                    mainHelp = new URL(url);
                                    if (!this.validateURL(mainHelp)) {
                                        mainHelp = null;
                                    }
                                }
                                else if (this.prefixURL != null) {
                                    mainHelp = this.prefixURL;
                                    if (!this.validateURL(mainHelp)) {
                                        mainHelp = null;
                                    }
                                }
                                this.csWin.showHelpMessage(message, mainHelp);
                            }
                            catch (final MalformedURLException e) {
                                this.displayError(CommonUtil.getString(MessageConstants.CANNOT_DISPLAY_CONTEXT_HELP), e);
                            }
                        }
                    }
                }
            }
        }
        else {
            this.csWin.disappear(true);
            this.csWin = null;
        }
        if (this.csWin == null && this.glassPnl != null) {
            this.glassPnl.setVisible(false);
            this.cleanUp();
        }
    }
    
    private void cleanUp() {
        this.glassPnl.setCursor(Cursor.getPredefinedCursor(0));
        this.glassPnl.removeMouseListener(this);
        this.glassPnl.removeMouseMotionListener(this);
        this.glassPnl.remove(this.displayLbl);
        this.mapping = null;
        this.glassPnl = null;
        this.displayLbl = null;
    }
    
    public Map parse(final URL urlArg) throws Exception {
        if (urlArg == null) {
            throw new RuntimeException("Xml file url not set");
        }
        if (this.mapRef == null || this.mapRef.get() == null) {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            docBuilderFactory.setIgnoringElementContentWhitespace(true);
            docBuilderFactory.setIgnoringComments(true);
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final InputSource source = new InputSource(urlArg.openStream());
            source.setSystemId(urlArg.toString());
            final Document doc = docBuilder.parse(source);
            final NodeList nl = doc.getDocumentElement().getChildNodes();
            final HashMap map = new HashMap();
            for (int i = 0, j = nl.getLength(); i < j; ++i) {
                if (nl.item(i) instanceof Element) {
                    final Element el = (Element)nl.item(i);
                    final NodeList childList = el.getChildNodes();
                    int k = 0;
                    final int m = childList.getLength();
                    while (k < m) {
                        if (childList.item(k) instanceof CDATASection) {
                            final CDATASection cds = (CDATASection)childList.item(k);
                            map.put(el.getAttribute("id").toLowerCase(), cds.getData());
                            final String attr = el.getAttribute("url");
                            if (attr != null && attr.length() != 0) {
                                map.put(el.getAttribute("id").toLowerCase() + "_URL", attr);
                                break;
                            }
                            break;
                        }
                        else {
                            ++k;
                        }
                    }
                }
            }
            this.mapRef = new SoftReference((T)map);
        }
        return this.mapRef.get();
    }
    
    public static ContextSensitiveHelpButton getHelpButton(final String xmlFileArg, final String helpFileArg) {
        return getHelpButton(xmlFileArg, helpFileArg, null);
    }
    
    public static ContextSensitiveHelpButton getHelpButton(final String xmlFileArg, final String helpFileArg, final ResourceBundle bundle) {
        return getHelpButton(xmlFileArg, helpFileArg, "./images/help_contextual.png", "./images/nohelp_contextual.png", bundle);
    }
    
    public static ContextSensitiveHelpButton getHelpButton(final String xmlFileArg, final String helpFileArg, final String helpIconPath, final String noHelpAvailableIconPath) {
        return getHelpButton(xmlFileArg, helpFileArg, helpIconPath, noHelpAvailableIconPath, null);
    }
    
    public static ContextSensitiveHelpButton getHelpButton(final String xmlFileArg, String helpFileArg, final String helpIconPath, final String noHelpAvailableIconPath, final ResourceBundle bundle) {
        final ContextSensitiveHelpButton cshButton = new ContextSensitiveHelpButton(helpIconPath, noHelpAvailableIconPath, bundle);
        try {
            if (helpFileArg != null) {
                try {
                    helpFileArg = new File(helpFileArg).getCanonicalPath();
                }
                catch (final IOException io) {
                    helpFileArg = new File(helpFileArg).getAbsolutePath();
                }
                cshButton.setPrefixURL(new URL("file", "", "//" + helpFileArg.replace('\\', '/')));
            }
            cshButton.setXmlURL(new URL(ContextSensitiveHelpButton.rootURL, xmlFileArg));
        }
        catch (final MalformedURLException e) {
            ContextSensitiveHelpButton.out.log(Level.SEVERE, "\"Unable to get proper urls for helpXMLFile which is :: " + xmlFileArg + " or " + " helpHtmlFile which is :: " + helpFileArg, e);
            ConsoleOut.println("Unable to get proper urls for helpXMLFile which is :: " + xmlFileArg + " or " + " helpHtmlFile which is :: " + helpFileArg);
            cshButton.setEnabled(false);
        }
        return cshButton;
    }
    
    private boolean validateURL(final URL url) {
        URLConnection connect = null;
        if (url != null) {
            try {
                connect = url.openConnection();
            }
            catch (final IOException exc) {
                this.displayError(CommonUtil.getString(MessageConstants.VALIDATING_HELP_FILE_URL), exc);
            }
            if (!(connect instanceof HttpURLConnection)) {
                final String fileStr = url.getFile();
                final File file = new File(fileStr);
                return file.exists();
            }
            int state = 0;
            try {
                state = ((HttpURLConnection)connect).getResponseCode();
                if (state == 200) {
                    return true;
                }
            }
            catch (final IOException exc2) {
                this.displayError(CommonUtil.getString(MessageConstants.VALIDATING_HELP_FILE_URL), exc2);
            }
        }
        return false;
    }
    
    private static URL getRootURL() {
        try {
            final String userDir = System.getProperty("user.dir");
            return new File(userDir).toURL();
        }
        catch (final Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void mouseReleased(final MouseEvent mEvtArg) {
    }
    
    @Override
    public void mouseDragged(final MouseEvent mEvtArg) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent mEvtArg) {
    }
    
    @Override
    public void mouseExited(final MouseEvent mEvtArg) {
    }
    
    @Override
    public void mousePressed(final MouseEvent mEvtArg) {
    }
    
    private String getString(final String key) {
        String ret = null;
        if (this.myBundle != null) {
            try {
                ret = this.myBundle.getString(key);
            }
            catch (final Exception ex) {}
        }
        if (ret != null && !ret.equals("")) {
            return ret;
        }
        return key.trim();
    }
    
    private void displayError(final String message, final Throwable ex) {
        ContextSensitiveHelpButton.out.log(Level.SEVERE, message, ex);
        UMOptionPane.showErroDialog(this.getParent(), message);
    }
    
    static {
        ContextSensitiveHelpButton.out = Logger.getLogger(ContextSensitiveHelpButton.class.getName());
        ContextSensitiveHelpButton.rootURL = getRootURL();
    }
}
