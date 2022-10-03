package org.jfree.ui.about;

import java.awt.Container;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Component;
import javax.swing.JTabbedPane;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.Dialog;
import javax.swing.BorderFactory;
import java.util.List;
import java.awt.Image;
import java.util.ResourceBundle;
import javax.swing.border.Border;
import java.awt.Dimension;
import javax.swing.JDialog;

public class AboutDialog extends JDialog
{
    public static final Dimension PREFERRED_SIZE;
    public static final Border STANDARD_BORDER;
    private ResourceBundle resources;
    private String application;
    private String version;
    private String copyright;
    private String info;
    private Image logo;
    private List contributors;
    private String licence;
    
    static {
        PREFERRED_SIZE = new Dimension(560, 360);
        STANDARD_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    }
    
    public AboutDialog(final Dialog owner, final String title, final ProjectInfo project) {
        super(owner);
        this.init(title, project.getName(), "Version " + project.getVersion(), project.getInfo(), project.getLogo(), project.getCopyright(), project.getLicenceText(), project.getContributors(), project);
    }
    
    public AboutDialog(final Frame owner, final String title, final ProjectInfo project) {
        super(owner);
        this.init(title, project.getName(), "Version " + project.getVersion(), project.getInfo(), project.getLogo(), project.getCopyright(), project.getLicenceText(), project.getContributors(), project);
    }
    
    public AboutDialog(final String title, final ProjectInfo project) {
        this.init(title, project.getName(), "Version " + project.getVersion(), project.getInfo(), project.getLogo(), project.getCopyright(), project.getLicenceText(), project.getContributors(), project);
    }
    
    private JPanel createAboutPanel(final ProjectInfo info) {
        final JPanel about = new JPanel(new BorderLayout());
        final JPanel details = new AboutPanel(this.application, this.version, this.copyright, this.info, this.logo);
        boolean includetabs = false;
        final JTabbedPane tabs = new JTabbedPane();
        if (this.contributors != null) {
            final JPanel contributorsPanel = new ContributorsPanel(this.contributors);
            contributorsPanel.setBorder(AboutDialog.STANDARD_BORDER);
            final String contributorsTab = this.resources.getString("about-frame.tab.contributors");
            tabs.add(contributorsTab, contributorsPanel);
            includetabs = true;
        }
        if (this.licence != null) {
            final JPanel licencePanel = this.createLicencePanel();
            licencePanel.setBorder(AboutDialog.STANDARD_BORDER);
            final String licenceTab = this.resources.getString("about-frame.tab.licence");
            tabs.add(licenceTab, licencePanel);
            includetabs = true;
        }
        if (info != null) {
            final JPanel librariesPanel = new LibraryPanel(info);
            librariesPanel.setBorder(AboutDialog.STANDARD_BORDER);
            final String librariesTab = this.resources.getString("about-frame.tab.libraries");
            tabs.add(librariesTab, librariesPanel);
            includetabs = true;
        }
        about.add(details, "North");
        if (includetabs) {
            about.add(tabs);
        }
        return about;
    }
    
    private JPanel createLicencePanel() {
        final JPanel licencePanel = new JPanel(new BorderLayout());
        final JTextArea area = new JTextArea(this.licence);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);
        area.setEditable(false);
        licencePanel.add(new JScrollPane(area));
        return licencePanel;
    }
    
    private JTabbedPane createTabs(final ProjectInfo info) {
        final JTabbedPane tabs = new JTabbedPane();
        final JPanel aboutPanel = this.createAboutPanel(info);
        aboutPanel.setBorder(AboutDialog.STANDARD_BORDER);
        final String aboutTab = this.resources.getString("about-frame.tab.about");
        tabs.add(aboutTab, aboutPanel);
        final JPanel systemPanel = new SystemPropertiesPanel();
        systemPanel.setBorder(AboutDialog.STANDARD_BORDER);
        final String systemTab = this.resources.getString("about-frame.tab.system");
        tabs.add(systemTab, systemPanel);
        return tabs;
    }
    
    public Dimension getPreferredSize() {
        return AboutDialog.PREFERRED_SIZE;
    }
    
    private void init(final String title, final String application, final String version, final String info, final Image logo, final String copyright, final String licence, final List contributors, final ProjectInfo libraries) {
        this.setTitle(title);
        this.application = application;
        this.version = version;
        this.copyright = copyright;
        this.info = info;
        this.logo = logo;
        this.contributors = contributors;
        this.licence = licence;
        final String baseName = "org.jfree.ui.about.resources.AboutResources";
        this.resources = ResourceBundle.getBundle("org.jfree.ui.about.resources.AboutResources");
        final JPanel content = new JPanel(new BorderLayout());
        content.setBorder(AboutDialog.STANDARD_BORDER);
        final JTabbedPane tabs = this.createTabs(libraries);
        content.add(tabs);
        this.setContentPane(content);
        this.pack();
    }
}
