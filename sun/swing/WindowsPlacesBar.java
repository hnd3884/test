package sun.swing;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.AbstractButton;
import javax.swing.Box;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.ImageIcon;
import java.awt.Image;
import sun.awt.shell.ShellFolder;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import sun.awt.OSInfo;
import java.awt.Dimension;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JFileChooser;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionListener;
import javax.swing.JToolBar;

public class WindowsPlacesBar extends JToolBar implements ActionListener, PropertyChangeListener
{
    JFileChooser fc;
    JToggleButton[] buttons;
    ButtonGroup buttonGroup;
    File[] files;
    final Dimension buttonSize;
    
    public WindowsPlacesBar(final JFileChooser fc, final boolean b) {
        super(1);
        this.fc = fc;
        this.setFloatable(false);
        this.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        final boolean b2 = OSInfo.getOSType() == OSInfo.OSType.WINDOWS && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) >= 0;
        if (b) {
            this.buttonSize = new Dimension(83, 69);
            this.putClientProperty("XPStyle.subAppName", "placesbar");
            this.setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        else {
            this.buttonSize = new Dimension(83, b2 ? 65 : 54);
            this.setBorder(new BevelBorder(1, UIManager.getColor("ToolBar.highlight"), UIManager.getColor("ToolBar.background"), UIManager.getColor("ToolBar.darkShadow"), UIManager.getColor("ToolBar.shadow")));
        }
        this.setBackground(new Color(UIManager.getColor("ToolBar.shadow").getRGB()));
        final FileSystemView fileSystemView = fc.getFileSystemView();
        this.files = (File[])ShellFolder.get("fileChooserShortcutPanelFolders");
        this.buttons = new JToggleButton[this.files.length];
        this.buttonGroup = new ButtonGroup();
        for (int i = 0; i < this.files.length; ++i) {
            if (fileSystemView.isFileSystemRoot(this.files[i])) {
                this.files[i] = fileSystemView.createFileObject(this.files[i].getAbsolutePath());
            }
            String s = fileSystemView.getSystemDisplayName(this.files[i]);
            final int lastIndex = s.lastIndexOf(File.separatorChar);
            if (lastIndex >= 0 && lastIndex < s.length() - 1) {
                s = s.substring(lastIndex + 1);
            }
            Icon systemIcon;
            if (this.files[i] instanceof ShellFolder) {
                final ShellFolder shellFolder = (ShellFolder)this.files[i];
                Image icon = shellFolder.getIcon(true);
                if (icon == null) {
                    icon = (Image)ShellFolder.get("shell32LargeIcon 1");
                }
                systemIcon = ((icon == null) ? null : new ImageIcon(icon, shellFolder.getFolderType()));
            }
            else {
                systemIcon = fileSystemView.getSystemIcon(this.files[i]);
            }
            this.buttons[i] = new JToggleButton(s, systemIcon);
            if (b) {
                this.buttons[i].putClientProperty("XPStyle.subAppName", "placesbar");
            }
            else {
                final Color foreground = new Color(UIManager.getColor("List.selectionForeground").getRGB());
                this.buttons[i].setContentAreaFilled(false);
                this.buttons[i].setForeground(foreground);
            }
            this.buttons[i].setMargin(new Insets(3, 2, 1, 2));
            this.buttons[i].setFocusPainted(false);
            this.buttons[i].setIconTextGap(0);
            this.buttons[i].setHorizontalTextPosition(0);
            this.buttons[i].setVerticalTextPosition(3);
            this.buttons[i].setAlignmentX(0.5f);
            this.buttons[i].setPreferredSize(this.buttonSize);
            this.buttons[i].setMaximumSize(this.buttonSize);
            this.buttons[i].addActionListener(this);
            this.add(this.buttons[i]);
            if (i < this.files.length - 1 && b) {
                this.add(Box.createRigidArea(new Dimension(1, 1)));
            }
            this.buttonGroup.add(this.buttons[i]);
        }
        this.doDirectoryChanged(fc.getCurrentDirectory());
    }
    
    protected void doDirectoryChanged(final File file) {
        for (int i = 0; i < this.buttons.length; ++i) {
            final JToggleButton toggleButton = this.buttons[i];
            if (this.files[i].equals(file)) {
                toggleButton.setSelected(true);
                break;
            }
            if (toggleButton.isSelected()) {
                this.buttonGroup.remove(toggleButton);
                toggleButton.setSelected(false);
                this.buttonGroup.add(toggleButton);
            }
        }
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName() == "directoryChanged") {
            this.doDirectoryChanged(this.fc.getCurrentDirectory());
        }
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        final JToggleButton toggleButton = (JToggleButton)actionEvent.getSource();
        for (int i = 0; i < this.buttons.length; ++i) {
            if (toggleButton == this.buttons[i]) {
                this.fc.setCurrentDirectory(this.files[i]);
                break;
            }
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        final Dimension minimumSize = super.getMinimumSize();
        Dimension preferredSize = super.getPreferredSize();
        int height = minimumSize.height;
        if (this.buttons != null && this.buttons.length > 0 && this.buttons.length < 5) {
            final JToggleButton toggleButton = this.buttons[0];
            if (toggleButton != null) {
                final int n = 5 * (toggleButton.getPreferredSize().height + 1);
                if (n > height) {
                    height = n;
                }
            }
        }
        if (height > preferredSize.height) {
            preferredSize = new Dimension(preferredSize.width, height);
        }
        return preferredSize;
    }
}
