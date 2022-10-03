package com.adventnet.telnet.telnetwindow;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import com.adventnet.cli.ssh.sshwindow.SshAuthDialog;
import java.net.URL;
import javax.swing.ImageIcon;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JApplet;
import java.awt.Font;
import javax.swing.JScrollBar;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.Panel;
import java.applet.Applet;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class TelnetFrame extends JFrame implements Runnable, ActionListener, KeyListener
{
    private boolean initialized;
    private Applet applet;
    Panel Top;
    private boolean running;
    MenuBar menuBar;
    Menu FileMenu;
    Menu SettingsMenu;
    MenuItem ConnectMenuItem;
    MenuItem DisconnectMenuItem;
    MenuItem ExitMenuItem;
    MenuItem BackgroundItem;
    MenuItem ForegroundItem;
    MenuItem IncreaseBuffer;
    MenuItem DecreaseBuffer;
    MenuItem Encoding;
    boolean connected;
    private static final int debug = 0;
    Thread reader;
    private String serverName;
    private int rmiPort;
    String host;
    int portNo;
    Vt terminal;
    int fontSize;
    private boolean isMenuBarVisible;
    private String frameTitle;
    private Image iconImage;
    private int socketTimeout;
    private boolean sshRequired;
    private String terminalType;
    private int bufferSize;
    JScrollBar sbar;
    String username;
    String password;
    String loginPrompt;
    String passwordPrompt;
    String encoding;
    boolean closeOnDisconnect;
    TelnetClient client;
    
    public TelnetFrame() {
        this.initialized = false;
        this.applet = null;
        this.Top = null;
        this.running = false;
        this.menuBar = null;
        this.FileMenu = null;
        this.SettingsMenu = null;
        this.ConnectMenuItem = null;
        this.DisconnectMenuItem = null;
        this.ExitMenuItem = null;
        this.BackgroundItem = null;
        this.ForegroundItem = null;
        this.IncreaseBuffer = null;
        this.DecreaseBuffer = null;
        this.Encoding = null;
        this.connected = false;
        this.reader = null;
        this.serverName = null;
        this.rmiPort = 1099;
        this.host = "";
        this.portNo = 23;
        this.terminal = null;
        this.fontSize = 12;
        this.isMenuBarVisible = true;
        this.frameTitle = null;
        this.iconImage = null;
        this.socketTimeout = 0;
        this.sshRequired = false;
        this.terminalType = null;
        this.bufferSize = 30;
        this.sbar = null;
        this.encoding = "latin1";
        this.closeOnDisconnect = false;
        this.pack();
        this.setTitle(this.frameTitle);
        if (this.iconImage != null) {
            this.setIconImage(this.iconImage);
        }
    }
    
    public TelnetFrame(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.Top = null;
        this.running = false;
        this.menuBar = null;
        this.FileMenu = null;
        this.SettingsMenu = null;
        this.ConnectMenuItem = null;
        this.DisconnectMenuItem = null;
        this.ExitMenuItem = null;
        this.BackgroundItem = null;
        this.ForegroundItem = null;
        this.IncreaseBuffer = null;
        this.DecreaseBuffer = null;
        this.Encoding = null;
        this.connected = false;
        this.reader = null;
        this.serverName = null;
        this.rmiPort = 1099;
        this.host = "";
        this.portNo = 23;
        this.terminal = null;
        this.fontSize = 12;
        this.isMenuBarVisible = true;
        this.frameTitle = null;
        this.iconImage = null;
        this.socketTimeout = 0;
        this.sshRequired = false;
        this.terminalType = null;
        this.bufferSize = 30;
        this.sbar = null;
        this.encoding = "latin1";
        this.closeOnDisconnect = false;
        this.applet = applet;
        this.pack();
        this.setTitle(this.frameTitle);
        if (this.iconImage != null) {
            this.setIconImage(this.iconImage);
        }
        this.setDefaultCloseOperation(2);
    }
    
    public MenuBar getMenuBar() {
        return this.menuBar;
    }
    
    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    private void setMenuBarVisible(final boolean isMenuBarVisible) {
        this.isMenuBarVisible = isMenuBarVisible;
    }
    
    private boolean isMenuBarVisible() {
        return this.isMenuBarVisible;
    }
    
    private void setMenuFont(final Font font) {
        final MenuBar menuBar = this.getMenuBar();
        for (int menuCount = menuBar.getMenuCount(), i = 0; i < menuCount; ++i) {
            Menu menu = menuBar.getMenu(i);
            menu.setFont(font);
            for (int itemCount = menu.getItemCount(), j = 0; j < itemCount; ++j) {
                final MenuItem item = menu.getItem(j);
                if (item instanceof MenuItem) {
                    item.setFont(font);
                }
                if (item instanceof Menu) {
                    menu = (Menu)item;
                    for (int itemCount2 = menu.getItemCount(), k = 0; k < itemCount2; ++k) {
                        final MenuItem item2 = menu.getItem(k);
                        if (item2 instanceof MenuItem) {
                            item2.setFont(font);
                        }
                    }
                }
            }
        }
    }
    
    private Font getMenuFont() {
        return this.getMenuBar().getMenu(0).getFont();
    }
    
    public void setVisible(final boolean visible) {
        if (visible) {
            this.init();
            if (!this.initialized) {
                return;
            }
            this.start();
        }
        else {
            this.stop();
        }
        super.setVisible(visible);
    }
    
    public void init(final JApplet applet) {
        this.applet = applet;
    }
    
    private void init() {
        if (this.initialized) {
            return;
        }
        this.setSize(this.getPreferredSize().width + 600, this.getPreferredSize().height + 450);
        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        try {
            this.initVariables();
            if (this.applet != null) {
                this.getFrameParameters();
            }
            this.setUpGUI(contentPane);
            this.setUpConnections();
        }
        catch (final Exception ex) {
            this.showStatus("Error in init method", ex);
        }
        this.initialized = true;
        this.setUpMenus();
        this.setUpToolBar();
        if (this.applet != null && !this.getTelnetParameters()) {
            this.initialized = false;
            return;
        }
        if (this.frameTitle != null) {
            this.setTitle(this.frameTitle);
        }
        if (this.iconImage != null) {
            this.setIconImage(this.iconImage);
        }
        this.connect(this.host, this.portNo);
    }
    
    void getFrameParameters() {
        final String parameter = this.applet.getParameter("fontSize");
        if (parameter == null) {
            this.fontSize = 12;
        }
        else {
            try {
                final int int1 = Integer.parseInt(parameter);
                if (int1 > 0) {
                    this.setFontSize(int1);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Illegal value for font size '" + parameter + "' , so default size(12) set");
                }
            }
            catch (final Exception ex) {
                JOptionPane.showMessageDialog(null, "Illegal integer value for font size '" + parameter + "' , so default size(12) set");
            }
        }
        this.encoding = this.applet.getParameter("encoding");
        if (this.encoding == null) {
            this.encoding = "latin1";
        }
        if (this.applet.getParameter("frameTitle") != null) {
            this.frameTitle = this.applet.getParameter("frameTitle");
        }
        else {
            this.host = this.applet.getParameter("host");
            if (this.host == null) {
                this.host = this.applet.getCodeBase().getHost();
            }
            this.frameTitle = " AWTA: " + this.host;
        }
        final String parameter2 = this.applet.getParameter("iconImage");
        if (parameter2 != null) {
            final URL resource = this.getClass().getResource("" + parameter2);
            if (resource != null) {
                this.setFrameIconImage(new ImageIcon(resource).getImage());
            }
            else {
                JOptionPane.showMessageDialog(null, "Can not find file '" + parameter2 + "' to set the icon image ");
            }
        }
        final String parameter3 = this.applet.getParameter("socketTimeout");
        if (parameter3 != null) {
            try {
                final int int2 = Integer.parseInt(parameter3);
                if (int2 >= 0) {
                    this.setSocketTimeout(int2);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Illegal value for socket timeout '" + parameter3 + "' , so default value( infinite ) set");
                }
            }
            catch (final Exception ex2) {
                JOptionPane.showMessageDialog(null, "Illegal integer value for socket timeout '" + parameter3 + "' , so default value( infinite ) set");
            }
        }
    }
    
    boolean getTelnetParameters() {
        this.serverName = this.applet.getCodeBase().getHost();
        final String parameter = this.getParameter("RMI_REG_PORT");
        if (parameter == null) {
            this.rmiPort = 1099;
        }
        else {
            this.rmiPort = Integer.parseInt(parameter);
        }
        this.host = this.getParameter("host");
        if (this.host == null) {
            this.host = this.applet.getCodeBase().getHost();
        }
        final String parameter2 = this.getParameter("port");
        if (parameter2 == null) {
            this.portNo = 23;
        }
        else {
            this.portNo = Integer.parseInt(parameter2);
        }
        this.username = this.getParameter("username");
        this.password = this.getParameter("password");
        this.passwordPrompt = this.getParameter("passwordPrompt");
        this.loginPrompt = this.getParameter("loginPrompt");
        if (this.getParameter("closeOnDisconnect") != null && this.getParameter("closeOnDisconnect").toUpperCase().equals("TRUE")) {
            this.closeOnDisconnect = true;
        }
        if (this.getParameter("resizeWindow") != null && this.getParameter("resizeWindow").equalsIgnoreCase("false")) {
            this.setResizable(false);
        }
        this.terminalType = this.getParameter("terminalType");
        final String parameter3 = this.getParameter("bufferSize");
        if (parameter3 != null) {
            this.bufferSize = Integer.parseInt(parameter3);
        }
        return this.initSshParameters();
    }
    
    private boolean initSshParameters() {
        if (this.getParameter("sshRequired") != null && this.getParameter("sshRequired").equalsIgnoreCase("true")) {
            this.sshRequired = true;
        }
        if (this.sshRequired && (this.username == null || this.password == null)) {
            final SshAuthDialog sshAuthDialog = new SshAuthDialog(this.username, this.password);
            sshAuthDialog.setVisible(true);
            if (!sshAuthDialog.isAuth()) {
                return false;
            }
            this.username = sshAuthDialog.getUsername();
            this.password = sshAuthDialog.getPassword();
        }
        return true;
    }
    
    private void setUpConnections() throws Exception {
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent windowEvent) {
                TelnetFrame.this.disconnect();
                TelnetFrame.this.setVisible(false);
            }
        });
    }
    
    private void initVariables() throws Exception {
        this.Top = new Panel((LayoutManager)new BorderLayout()) {
            public void update(final Graphics graphics) {
                this.paint(graphics);
            }
            
            public void print(final Graphics graphics) {
                TelnetFrame.this.terminal.getSwingTerminal().print(graphics);
            }
        };
    }
    
    private void setUpToolBar() {
    }
    
    private void setUpGUI(final Container container) throws Exception {
        this.Top.setLayout(new BorderLayout());
        this.terminal = new Vt(this.fontSize);
        this.terminal.getSwingTerminal().addKeyListener((KeyListener)this);
        this.sbar = new JScrollBar();
        this.Top.add(this.sbar, "East");
        this.terminal.setScrollbar(this.sbar);
        this.terminal.setBufferSize(this.bufferSize);
        this.Top.add((Component)this.terminal.getSwingTerminal(), "Center");
        container.add(this.Top, "Center");
        container.setBackground(Color.black);
        this.pack();
    }
    
    private void setUpMenus() {
        this.menuBar = new MenuBar();
        this.FileMenu = this.createMenu("File", 'f', null);
        this.ConnectMenuItem = this.createMenuItem("Connect", 'C', null, this, "Connect");
        this.DisconnectMenuItem = this.createMenuItem("Disconnect", 'D', null, this, "Disconnect");
        this.ExitMenuItem = this.createMenuItem("Exit", 'x', null, this, "Exit");
        this.FileMenu.add(this.ConnectMenuItem);
        this.FileMenu.add(this.DisconnectMenuItem);
        this.FileMenu.addSeparator();
        this.FileMenu.add(this.ExitMenuItem);
        this.SettingsMenu = this.createMenu("Settings", 'S', null);
        this.BackgroundItem = this.createMenuItem("Background Color", 'B', null, this, "Background");
        this.ForegroundItem = this.createMenuItem("Foreground Color", 'g', null, this, "Foreground");
        this.IncreaseBuffer = this.createMenuItem("Buffer +50", '+', null, this, "Increase");
        this.DecreaseBuffer = this.createMenuItem("Buffer -50", '-', null, this, "Decrease");
        this.Encoding = this.createMenuItem("Encoding Type", 'E', null, this, "Encode");
        this.SettingsMenu.add(this.ForegroundItem);
        this.SettingsMenu.addSeparator();
        this.SettingsMenu.add(this.IncreaseBuffer);
        this.SettingsMenu.add(this.DecreaseBuffer);
        this.SettingsMenu.addSeparator();
        this.SettingsMenu.add(this.Encoding);
        this.menuBar.add(this.FileMenu);
        this.menuBar.add(this.SettingsMenu);
        this.setMenuFont(new Font("Arial", 0, 12));
        this.setMenuBar(this.menuBar);
    }
    
    private MenuItem createMenuItem(final String s, final char c, final ImageIcon imageIcon, final ActionListener actionListener, final String actionCommand) {
        final MenuItem menuItem = new MenuItem(s);
        if (c != ' ') {}
        if (imageIcon != null) {}
        menuItem.addActionListener(actionListener);
        menuItem.setActionCommand(actionCommand);
        return menuItem;
    }
    
    private Menu createMenu(final String s, final char c, final ImageIcon imageIcon) {
        final Menu menu = new Menu(s);
        if (c != ' ') {}
        if (imageIcon != null) {}
        return menu;
    }
    
    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
    }
    
    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
    }
    
    private String getParameter(final String s) {
        String parameter;
        if (this.applet != null) {
            parameter = this.applet.getParameter(s);
        }
        else {
            parameter = null;
        }
        if (parameter == null) {}
        return parameter;
    }
    
    private void showStatus(final String s) {
        System.out.println("Internal Error :" + s);
    }
    
    private void showStatus(final String s, final Exception ex) {
        System.out.println("Internal Error :" + s);
        ex.printStackTrace();
    }
    
    public void keyPressed(final KeyEvent keyEvent) {
        if (this.sbar.getValue() < this.sbar.getMaximum()) {
            this.sbar.setValue(this.sbar.getMaximum());
            this.terminal.setWindowBase(this.sbar.getMaximum());
        }
    }
    
    public void keyReleased(final KeyEvent keyEvent) {
    }
    
    public void keyTyped(final KeyEvent keyEvent) {
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("Connect")) {
            this.connect(this.host, this.portNo);
        }
        if (actionEvent.getActionCommand().equals("Exit")) {
            this.disconnect();
            this.setVisible(false);
        }
        if (actionEvent.getActionCommand().equals("Disconnect")) {
            this.disconnect();
        }
        if (actionEvent.getActionCommand().equals("Background")) {
            this.terminal.getSwingTerminal().setBackground(JColorChooser.showDialog(this, "Background Color", this.terminal.getSwingTerminal().getBackground()));
            this.Top.setBackground(this.terminal.getSwingTerminal().getBackground());
        }
        if (actionEvent.getActionCommand().equals("Foreground")) {
            this.terminal.getSwingTerminal().setForeground(JColorChooser.showDialog(this, "Foreground Color", this.terminal.getSwingTerminal().getForeground()));
        }
        if (actionEvent.getActionCommand().equals("Increase")) {
            this.terminal.setBufferSize(this.terminal.getBufferSize() + 50);
        }
        if (actionEvent.getActionCommand().equals("Decrease")) {
            this.terminal.setBufferSize(this.terminal.getBufferSize() - 50);
        }
        if (actionEvent.getActionCommand().equals("Encode")) {
            final Object showInputDialog = JOptionPane.showInputDialog(null, "Please input the encoding type", "Encoding Dialog", -1, null, null, this.encoding);
            if (showInputDialog != null) {
                this.encoding = showInputDialog.toString();
            }
        }
    }
    
    public void write(final String s) {
        if (s == null) {
            return;
        }
        if (s.equals("")) {
            return;
        }
        try {
            this.write(s.getBytes());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void setEditable(final boolean b) {
        this.connected = b;
        this.ConnectMenuItem.setEnabled(!b);
        this.DisconnectMenuItem.setEnabled(b);
    }
    
    void performLogin() {
        final byte[] array = new byte[256];
        try {
            if (this.username != null) {
                this.waitFor(this.loginPrompt.getBytes());
                this.write((this.username + "\n").getBytes());
            }
            if (this.password != null && !this.password.equalsIgnoreCase("null")) {
                this.waitFor(this.passwordPrompt.getBytes());
                this.write((this.password + "\n").getBytes());
            }
        }
        catch (final IOException ex) {
            this.showStatus("Unable to login with the applet parameters", ex);
        }
    }
    
    void waitFor(final byte[] array) throws IOException {
        int i = 0;
        final byte[] array2 = new byte[256];
        String string = "";
        while (i >= 0) {
            i = this.read(array2);
            if (i > 0) {
                string += new String(array2, 0, i);
                if (this.isMatch(string.getBytes(), array)) {
                    return;
                }
                continue;
            }
        }
    }
    
    boolean isMatch(final byte[] array, final byte[] array2) {
        int n = 0;
        final int length = array2.length;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == array2[n]) {
                if (++n == length) {
                    return true;
                }
            }
            else {
                if (i + length > array.length) {
                    break;
                }
                n = 0;
            }
        }
        return false;
    }
    
    public void run() {
        final byte[] array = new byte[256];
        int i = 0;
        if (!this.sshRequired && (this.loginPrompt != null || this.passwordPrompt != null)) {
            this.performLogin();
        }
        while (i >= 0) {
            try {
                i = this.read(array);
                if (i > 0) {
                    this.putString(new String(array, 0, i, this.encoding));
                }
                if (i < 0) {
                    this.reader = null;
                    if (this.closeOnDisconnect) {
                        this.setEditable(false);
                        this.setVisible(false);
                    }
                    else {
                        this.putString("Disconnected by Peer\n");
                        this.setEditable(false);
                    }
                }
                this.repaint();
            }
            catch (final IOException ex) {
                this.disconnect();
                this.reader = null;
                if (this.closeOnDisconnect) {
                    this.setEditable(false);
                    this.setVisible(false);
                    break;
                }
                this.putString("Disconnected by Peer\n");
                this.setEditable(false);
                break;
            }
        }
    }
    
    private void setTelnetClient(final TelnetClient client) {
        this.client = client;
    }
    
    private int read(final byte[] array) throws IOException {
        return this.client.read(array);
    }
    
    private void sendTelnetCommand(final byte b) {
    }
    
    private void write(final byte[] array) throws IOException {
        try {
            if (this.reader != null) {
                this.client.write(array);
            }
        }
        catch (final IOException ex) {
            this.reader = null;
        }
    }
    
    private void putString(final String s) {
        this.terminal.putString(s);
    }
    
    static int byteToInt(final byte b) {
        return b & 0xFF;
    }
    
    void connect(final String s, final int n) {
        try {
            this.client = new TelnetClientExt(this.serverName, this.rmiPort, s, n, this.sshRequired);
            this.terminal.setTelnetClient(this.client);
        }
        catch (final NotBoundException ex) {
            this.putString("For using this Telnet functionality to the device, you should start RMI Registry Before starting the Server, exception : " + ex + "\n");
            this.setEditable(false);
            return;
        }
        catch (final IOException ex2) {
            this.initialized = false;
            return;
        }
        catch (final Exception ex3) {
            if (this.serverName.equalsIgnoreCase("localhost")) {
                this.putString("Cannot Connect to the Server " + ex3 + " Please open the client with the <hostname:9090>/<IpAddress:9090> instead of localhost:9090, due to a limitation in RMI, for further details refer the Limitations page.\n");
            }
            else {
                this.putString("Unable to Connect to the Server " + ex3);
            }
            this.setEditable(false);
            return;
        }
        try {
            this.setEditable(true);
        }
        catch (final Exception ex4) {
            this.putString("Sorry, Could not connect: " + ex4 + "\r\n\r\n" + "Possible reasons might be\r\n   1. The Machine you are trying to connect doesn't support telnet. \r\n   2. Your are either behind a firewall or the server cannot contact the host: " + s + "\r\nIf unsure, please contact the administrator ");
            this.setEditable(false);
        }
        if (this.reader == null) {
            (this.reader = new Thread(this)).start();
        }
    }
    
    void disconnect() {
        if (this.client != null) {
            try {
                this.client.disconnect();
                this.terminal.client = null;
            }
            catch (final Exception ex) {}
            this.reader = null;
            this.setEditable(false);
        }
    }
    
    void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    void setRMIPort(final int rmiPort) {
        this.rmiPort = rmiPort;
    }
    
    public void setHostName(final String host) {
        this.host = host;
    }
    
    public void setPortNo(final int portNo) {
        this.portNo = portNo;
    }
    
    public void setFontSize(final int fontSize) {
        if (fontSize >= 1) {
            this.fontSize = fontSize;
        }
    }
    
    public int getFontSize() {
        return this.fontSize;
    }
    
    public void setEncoding(final String encoding) {
        if (encoding != null) {
            this.encoding = encoding;
        }
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setFrameTitle(final String frameTitle) {
        if (frameTitle != null) {
            this.frameTitle = frameTitle;
        }
    }
    
    public String getFrameTitle() {
        return this.frameTitle;
    }
    
    public void setFrameIconImage(final Image iconImage) {
        if (iconImage != null) {
            this.iconImage = iconImage;
        }
    }
    
    public Image getFrameIconImage() {
        return this.iconImage;
    }
    
    public void setSocketTimeout(final int socketTimeout) {
        if (socketTimeout >= 0) {
            this.socketTimeout = socketTimeout;
        }
    }
    
    public int getSocketTimeout() {
        return this.socketTimeout;
    }
    
    class TelnetClientExt extends TelnetClient
    {
        public TelnetClientExt(final String s, final int n, final String s2, final int n2, final boolean b) throws RemoteException, NotBoundException, MalformedURLException, IOException {
            super(s, n, b);
            try {
                super.connect(s2, n2, TelnetFrame.this.terminalType);
                if (TelnetFrame.this.loginPrompt == null && TelnetFrame.this.passwordPrompt == null && TelnetFrame.this.username != null && TelnetFrame.this.password != null) {
                    final String login = super.login(TelnetFrame.this.username, TelnetFrame.this.password);
                    if (login != null) {
                        TelnetFrame.this.putString(login);
                    }
                }
                super.setSocketTimeout(TelnetFrame.this.socketTimeout);
            }
            catch (final Exception ex) {
                JOptionPane.showMessageDialog(null, "Connect Failed : " + ex.getMessage());
                throw new IOException(ex.getMessage());
            }
        }
    }
}
