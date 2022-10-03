import java.awt.Image;
import java.net.URL;
import com.sun.jimi.core.component.JimiCanvas;
import javax.swing.ImageIcon;
import java.awt.Point;
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import javax.swing.AbstractButton;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import com.adventnet.utils.CLIUtils;
import java.util.Locale;
import java.util.StringTokenizer;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.awt.Toolkit;
import java.io.File;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import com.adventnet.cli.beans.CLIBrowser;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

public class CLIBrowserApplication extends JFrame implements WindowListener, ActionListener, MouseListener, FocusListener
{
    CLIBrowser browser;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenu helpMenu;
    JMenuItem exitMenuItem;
    JMenuItem aboutMenuItem;
    JToolBar toolBar;
    JButton bExit;
    String imageFile;
    private boolean borderPaintedFlag;
    static String[] values;
    private Rectangle browserPos;
    boolean exitDialogFlag;
    
    public CLIBrowserApplication() {
        this.imageFile = "CLIBrowser" + File.separator + "clilogo.jpg";
        this.borderPaintedFlag = false;
        this.browserPos = new Rectangle(0, 0, 770, 600);
        this.exitDialogFlag = false;
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.browserPos.height = (int)(screenSize.height * 0.9);
        this.browserPos.width = (int)(screenSize.width * 0.9);
        this.browserPos.x = (screenSize.width - this.browserPos.width) / 2;
        this.browserPos.y = (screenSize.height - this.browserPos.height) / 2;
        this.setSize(this.browserPos.width, this.browserPos.height);
        this.setLocation(this.browserPos.x, this.browserPos.y);
        String helpLocation = "";
        String s = "";
        String s2 = "";
        String s3 = "";
        if (CLIBrowserApplication.values[0] != null) {
            helpLocation = CLIBrowserApplication.values[0];
        }
        if (CLIBrowserApplication.values[1] != null) {
            s = CLIBrowserApplication.values[1];
        }
        if (CLIBrowserApplication.values[2] != null) {
            s2 = CLIBrowserApplication.values[2];
        }
        if (CLIBrowserApplication.values[6] != null) {
            s3 = CLIBrowserApplication.values[6];
        }
        if (CLIBrowserApplication.values[3].equals("Set")) {
            if (CLIBrowserApplication.values[4] == null) {
                JOptionPane.showMessageDialog(this, "Please specify the Locale for Internationalization", "Information", 1);
                System.exit(1);
            }
            else {
                final StringTokenizer stringTokenizer = new StringTokenizer(CLIBrowserApplication.values[4], ",");
                if (stringTokenizer.countTokens() != 2) {
                    JOptionPane.showMessageDialog(this, "Please specify the Language and Country name for Locale\nExample : en,US", "Information", 1);
                    System.exit(1);
                }
                CLIUtils.setLocale(new Locale(stringTokenizer.nextToken(), stringTokenizer.nextToken()));
                if (CLIBrowserApplication.values[5] != null) {
                    CLIUtils.setSearchPath(CLIBrowserApplication.values[5]);
                }
                try {
                    CLIUtils.INTERNATIONALIZE = true;
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }
        if (!s.equals("") && !s2.equals("")) {
            if (!s3.equals("")) {
                this.browser = new CLIBrowser(s3, s, s2, (JFrame)this);
            }
            else {
                this.browser = new CLIBrowser(s, s2, (JFrame)this);
            }
        }
        else if (!s.equals("")) {
            if (!s3.equals("")) {
                this.browser = new CLIBrowser(s3, s, (String)null, (JFrame)this);
            }
            else {
                this.browser = new CLIBrowser(s, (JFrame)this);
            }
        }
        else if (!s3.equals("")) {
            this.browser = new CLIBrowser(s3, (String)null, (String)null, (JFrame)this);
        }
        else {
            this.browser = new CLIBrowser((JFrame)this);
        }
        this.menuBar = this.browser.getMenuBar();
        this.fileMenu = this.menuBar.getMenu(0);
        (this.exitMenuItem = CLIUtils.createJMenuItem("Exit", 'x', 'x')).addActionListener(this);
        this.exitMenuItem.setFont(this.browser.getMenuFont());
        this.exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(115, 8));
        this.fileMenu.add(this.exitMenuItem);
        this.helpMenu = this.menuBar.getMenu(4);
        (this.aboutMenuItem = CLIUtils.createJMenuItem("About...", 'A', 'a')).addActionListener(this);
        this.aboutMenuItem.setFont(this.browser.getMenuFont());
        this.helpMenu.add(this.aboutMenuItem);
        this.toolBar = this.browser.getToolBar();
        (this.bExit = new JButton(this.getImageIcon("CLIBrowser" + File.separator + "exit.png"))).setBorderPainted(false);
        this.bExit.setBorder(new BevelBorder(0));
        this.bExit.addActionListener(this);
        this.bExit.addMouseListener(this);
        this.bExit.addFocusListener(this);
        this.bExit.setToolTipText(CLIUtils.getString("Exit from CLIBrowser"));
        this.toolBar.add(this.bExit);
        if (!helpLocation.equals("")) {
            this.browser.setHelpLocation(helpLocation);
        }
        this.addWindowListener(this);
        this.setDefaultCloseOperation(0);
        this.setJMenuBar(this.menuBar);
        this.setIconImage(this.getImage(this.imageFile));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add((Component)this.browser);
    }
    
    private boolean exitHandler() {
        this.exitDialogFlag = true;
        final int showConfirmDialog = JOptionPane.showConfirmDialog(this, CLIUtils.getString("Do you want to quit "), CLIUtils.getString("Confirm Exit"), 0);
        if (showConfirmDialog != 0) {
            this.exitDialogFlag = false;
        }
        return showConfirmDialog == 0;
    }
    
    public void windowOpened(final WindowEvent windowEvent) {
    }
    
    public void windowClosing(final WindowEvent windowEvent) {
        if (!this.exitDialogFlag && this.exitHandler()) {
            this.dispose();
            System.exit(0);
        }
    }
    
    public void windowClosed(final WindowEvent windowEvent) {
    }
    
    public void windowIconified(final WindowEvent windowEvent) {
    }
    
    public void windowDeiconified(final WindowEvent windowEvent) {
    }
    
    public void windowActivated(final WindowEvent windowEvent) {
    }
    
    public void windowDeactivated(final WindowEvent windowEvent) {
    }
    
    public void mouseEntered(final MouseEvent mouseEvent) {
        if (this.borderPaintedFlag) {
            return;
        }
        final Object source = mouseEvent.getSource();
        if (source instanceof AbstractButton) {
            final AbstractButton abstractButton = (AbstractButton)source;
            if (abstractButton.isEnabled()) {
                abstractButton.setBorderPainted(true);
            }
        }
    }
    
    public void mouseExited(final MouseEvent mouseEvent) {
        if (this.borderPaintedFlag) {
            return;
        }
        final Object source = mouseEvent.getSource();
        if (source instanceof AbstractButton) {
            ((AbstractButton)source).setBorderPainted(false);
        }
    }
    
    public void mouseReleased(final MouseEvent mouseEvent) {
    }
    
    public void mouseClicked(final MouseEvent mouseEvent) {
    }
    
    public void mousePressed(final MouseEvent mouseEvent) {
    }
    
    public void focusGained(final FocusEvent focusEvent) {
        if (this.borderPaintedFlag) {
            return;
        }
        final Object source = focusEvent.getSource();
        if (source instanceof AbstractButton) {
            final AbstractButton abstractButton = (AbstractButton)source;
            if (abstractButton.isEnabled()) {
                abstractButton.setBorderPainted(true);
            }
        }
    }
    
    public void focusLost(final FocusEvent focusEvent) {
        if (this.borderPaintedFlag) {
            return;
        }
        final Object source = focusEvent.getSource();
        if (source instanceof AbstractButton) {
            ((AbstractButton)source).setBorderPainted(false);
        }
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        final Object source = actionEvent.getSource();
        if (source == this.exitMenuItem || source == this.bExit) {
            if (!this.exitDialogFlag && this.exitHandler()) {
                this.dispose();
                System.exit(0);
            }
        }
        else if (source == this.aboutMenuItem) {
            this.about();
        }
    }
    
    private void about() {
        final JLabel label = new JLabel(this.getImageIcon("/com/adventnet/cli/beans/images/about.jpg"));
        final JDialog dialog = new JDialog();
        dialog.setResizable(false);
        dialog.getContentPane().add(label);
        dialog.setSize(500, 320);
        dialog.setTitle("About");
        centerWindow(dialog);
        dialog.setModal(true);
        dialog.show();
    }
    
    private static void centerWindow(final Component component) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Point point = new Point(screenSize.width / 2, screenSize.height / 2);
        int n = point.x - component.getSize().width / 2;
        int n2 = point.y - component.getSize().height / 2;
        if (n < 0) {
            n = 0;
        }
        if (n2 < 0) {
            n2 = 0;
        }
        component.setLocation(n, n2);
    }
    
    private ImageIcon getImageIcon(final String s) {
        try {
            final URL resource = this.getClass().getResource("" + s);
            if (resource != null) {
                final JimiCanvas jimiCanvas = new JimiCanvas();
                jimiCanvas.setImageLocation(resource);
                return new ImageIcon(jimiCanvas.getImage());
            }
        }
        catch (final Exception ex) {
            System.out.println(CLIUtils.getString("Exception while converting images : ") + ex);
        }
        return null;
    }
    
    private Image getImage(final String s) {
        final URL resource = this.getClass().getResource("" + s);
        if (resource == null) {
            return null;
        }
        return Toolkit.getDefaultToolkit().getImage(resource);
    }
    
    public static void main(final String[] array) {
        System.out.println("AdventNet CLI Browser Release 2.0");
        System.out.println("os = " + System.getProperty("os.name") + "( " + System.getProperty("os.version") + " )" + ", java version = " + System.getProperty("java.version") + "\n");
        final String s = "java CLIBrowserApplication [-h \"help location path \"] [-c \"command set path \"] [-d \"data set path \"] [-i \"Internationalize\"] [-L \"Locale language,country \"]  [-S \"CLIBrowser properties file path\"] [-p \"Directory path to load files\"] ";
        final String[] array2 = { "-h", "-c", "-d", "-i", "-L", "-S", "-p" };
        try {
            final ParseOptions parseOptions = new ParseOptions(array, array2, CLIBrowserApplication.values, s);
            if (parseOptions.remArgs.length != 0) {
                parseOptions.usage_error();
            }
        }
        catch (final Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
        }
        final CLIBrowserApplication cliBrowserApplication = new CLIBrowserApplication();
        cliBrowserApplication.setTitle(CLIUtils.getString("AdventNet CLIBrowser"));
        cliBrowserApplication.setVisible(true);
    }
    
    static {
        CLIBrowserApplication.values = new String[] { null, null, null, "None", null, null, null };
    }
}
