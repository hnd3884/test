package sun.swing.plaf.synth;

import java.awt.Component;
import javax.swing.border.AbstractBorder;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.ComponentOrientation;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.synth.ColorType;
import java.awt.Graphics;
import javax.swing.ActionMap;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import java.awt.Insets;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.JFileChooser;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.filechooser.FileFilter;
import javax.swing.Action;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.JButton;
import javax.swing.plaf.synth.SynthUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

public abstract class SynthFileChooserUI extends BasicFileChooserUI implements SynthUI
{
    private JButton approveButton;
    private JButton cancelButton;
    private SynthStyle style;
    private Action fileNameCompletionAction;
    private FileFilter actualFileFilter;
    private GlobFilter globFilter;
    private String fileNameCompletionString;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthFileChooserUIImpl((JFileChooser)component);
    }
    
    public SynthFileChooserUI(final JFileChooser fileChooser) {
        super(fileChooser);
        this.fileNameCompletionAction = new FileNameCompletionAction();
        this.actualFileFilter = null;
        this.globFilter = null;
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return new SynthContext(component, Region.FILE_CHOOSER, this.style, this.getComponentState(component));
    }
    
    protected SynthContext getContext(final JComponent component, final int n) {
        SynthLookAndFeel.getRegion(component);
        return new SynthContext(component, Region.FILE_CHOOSER, this.style, n);
    }
    
    private Region getRegion(final JComponent component) {
        return SynthLookAndFeel.getRegion(component);
    }
    
    private int getComponentState(final JComponent component) {
        if (!component.isEnabled()) {
            return 8;
        }
        if (component.isFocusOwner()) {
            return 257;
        }
        return 1;
    }
    
    private void updateStyle(final JComponent component) {
        final SynthStyle style = SynthLookAndFeel.getStyleFactory().getStyle(component, Region.FILE_CHOOSER);
        if (style != this.style) {
            if (this.style != null) {
                this.style.uninstallDefaults(this.getContext(component, 1));
            }
            this.style = style;
            final SynthContext context = this.getContext(component, 1);
            this.style.installDefaults(context);
            final Border border = component.getBorder();
            if (border == null || border instanceof UIResource) {
                component.setBorder(new UIBorder(this.style.getInsets(context, null)));
            }
            this.directoryIcon = this.style.getIcon(context, "FileView.directoryIcon");
            this.fileIcon = this.style.getIcon(context, "FileView.fileIcon");
            this.computerIcon = this.style.getIcon(context, "FileView.computerIcon");
            this.hardDriveIcon = this.style.getIcon(context, "FileView.hardDriveIcon");
            this.floppyDriveIcon = this.style.getIcon(context, "FileView.floppyDriveIcon");
            this.newFolderIcon = this.style.getIcon(context, "FileChooser.newFolderIcon");
            this.upFolderIcon = this.style.getIcon(context, "FileChooser.upFolderIcon");
            this.homeFolderIcon = this.style.getIcon(context, "FileChooser.homeFolderIcon");
            this.detailsViewIcon = this.style.getIcon(context, "FileChooser.detailsViewIcon");
            this.listViewIcon = this.style.getIcon(context, "FileChooser.listViewIcon");
        }
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        SwingUtilities.replaceUIActionMap(component, this.createActionMap());
    }
    
    @Override
    public void installComponents(final JFileChooser fileChooser) {
        final SynthContext context = this.getContext(fileChooser, 1);
        (this.cancelButton = new JButton(this.cancelButtonText)).setName("SynthFileChooser.cancelButton");
        this.cancelButton.setIcon(context.getStyle().getIcon(context, "FileChooser.cancelIcon"));
        this.cancelButton.setMnemonic(this.cancelButtonMnemonic);
        this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
        this.cancelButton.addActionListener(this.getCancelSelectionAction());
        (this.approveButton = new JButton(this.getApproveButtonText(fileChooser))).setName("SynthFileChooser.approveButton");
        this.approveButton.setIcon(context.getStyle().getIcon(context, "FileChooser.okIcon"));
        this.approveButton.setMnemonic(this.getApproveButtonMnemonic(fileChooser));
        this.approveButton.setToolTipText(this.getApproveButtonToolTipText(fileChooser));
        this.approveButton.addActionListener(this.getApproveSelectionAction());
    }
    
    @Override
    public void uninstallComponents(final JFileChooser fileChooser) {
        fileChooser.removeAll();
    }
    
    @Override
    protected void installListeners(final JFileChooser fileChooser) {
        super.installListeners(fileChooser);
        this.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void contentsChanged(final ListDataEvent listDataEvent) {
                new DelayedSelectionUpdater();
            }
            
            @Override
            public void intervalAdded(final ListDataEvent listDataEvent) {
                new DelayedSelectionUpdater();
            }
            
            @Override
            public void intervalRemoved(final ListDataEvent listDataEvent) {
            }
        });
    }
    
    protected abstract ActionMap createActionMap();
    
    @Override
    protected void installDefaults(final JFileChooser fileChooser) {
        super.installDefaults(fileChooser);
        this.updateStyle(fileChooser);
    }
    
    @Override
    protected void uninstallDefaults(final JFileChooser fileChooser) {
        super.uninstallDefaults(fileChooser);
        this.style.uninstallDefaults(this.getContext(this.getFileChooser(), 1));
        this.style = null;
    }
    
    @Override
    protected void installIcons(final JFileChooser fileChooser) {
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        if (component.isOpaque()) {
            graphics.setColor(this.style.getColor(context, ColorType.BACKGROUND));
            graphics.fillRect(0, 0, component.getWidth(), component.getHeight());
        }
        this.style.getPainter(context).paintFileChooserBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        this.paint(this.getContext(component), graphics);
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
    }
    
    @Override
    public abstract void setFileName(final String p0);
    
    @Override
    public abstract String getFileName();
    
    protected void doSelectedFileChanged(final PropertyChangeEvent propertyChangeEvent) {
    }
    
    protected void doSelectedFilesChanged(final PropertyChangeEvent propertyChangeEvent) {
    }
    
    protected void doDirectoryChanged(final PropertyChangeEvent propertyChangeEvent) {
    }
    
    protected void doAccessoryChanged(final PropertyChangeEvent propertyChangeEvent) {
    }
    
    protected void doFileSelectionModeChanged(final PropertyChangeEvent propertyChangeEvent) {
    }
    
    protected void doMultiSelectionChanged(final PropertyChangeEvent propertyChangeEvent) {
        if (!this.getFileChooser().isMultiSelectionEnabled()) {
            this.getFileChooser().setSelectedFiles(null);
        }
    }
    
    protected void doControlButtonsChanged(final PropertyChangeEvent propertyChangeEvent) {
        if (this.getFileChooser().getControlButtonsAreShown()) {
            this.approveButton.setText(this.getApproveButtonText(this.getFileChooser()));
            this.approveButton.setToolTipText(this.getApproveButtonToolTipText(this.getFileChooser()));
            this.approveButton.setMnemonic(this.getApproveButtonMnemonic(this.getFileChooser()));
        }
    }
    
    protected void doAncestorChanged(final PropertyChangeEvent propertyChangeEvent) {
    }
    
    @Override
    public PropertyChangeListener createPropertyChangeListener(final JFileChooser fileChooser) {
        return new SynthFCPropertyChangeListener();
    }
    
    private void updateFileNameCompletion() {
        if (this.fileNameCompletionString != null && this.fileNameCompletionString.equals(this.getFileName())) {
            final String commonStartString = this.getCommonStartString(this.getModel().getFiles().toArray(new File[0]));
            if (commonStartString != null && commonStartString.startsWith(this.fileNameCompletionString)) {
                this.setFileName(commonStartString);
            }
            this.fileNameCompletionString = null;
        }
    }
    
    private String getCommonStartString(final File[] array) {
        String s = null;
        String substring = null;
        int n = 0;
        if (array.length == 0) {
            return null;
        }
        while (true) {
            for (int i = 0; i < array.length; ++i) {
                final String name = array[i].getName();
                if (i == 0) {
                    if (name.length() == n) {
                        return s;
                    }
                    substring = name.substring(0, n + 1);
                }
                if (!name.startsWith(substring)) {
                    return s;
                }
            }
            s = substring;
            ++n;
        }
    }
    
    private void resetGlobFilter() {
        if (this.actualFileFilter != null) {
            final JFileChooser fileChooser = this.getFileChooser();
            final FileFilter fileFilter = fileChooser.getFileFilter();
            if (fileFilter != null && fileFilter.equals(this.globFilter)) {
                fileChooser.setFileFilter(this.actualFileFilter);
                fileChooser.removeChoosableFileFilter(this.globFilter);
            }
            this.actualFileFilter = null;
        }
    }
    
    private static boolean isGlobPattern(final String s) {
        return (File.separatorChar == '\\' && s.indexOf(42) >= 0) || (File.separatorChar == '/' && (s.indexOf(42) >= 0 || s.indexOf(63) >= 0 || s.indexOf(91) >= 0));
    }
    
    public Action getFileNameCompletionAction() {
        return this.fileNameCompletionAction;
    }
    
    @Override
    protected JButton getApproveButton(final JFileChooser fileChooser) {
        return this.approveButton;
    }
    
    protected JButton getCancelButton(final JFileChooser fileChooser) {
        return this.cancelButton;
    }
    
    @Override
    public void clearIconCache() {
    }
    
    private class DelayedSelectionUpdater implements Runnable
    {
        DelayedSelectionUpdater() {
            SwingUtilities.invokeLater(this);
        }
        
        @Override
        public void run() {
            SynthFileChooserUI.this.updateFileNameCompletion();
        }
    }
    
    private class SynthFCPropertyChangeListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName.equals("fileSelectionChanged")) {
                SynthFileChooserUI.this.doFileSelectionModeChanged(propertyChangeEvent);
            }
            else if (propertyName.equals("SelectedFileChangedProperty")) {
                SynthFileChooserUI.this.doSelectedFileChanged(propertyChangeEvent);
            }
            else if (propertyName.equals("SelectedFilesChangedProperty")) {
                SynthFileChooserUI.this.doSelectedFilesChanged(propertyChangeEvent);
            }
            else if (propertyName.equals("directoryChanged")) {
                SynthFileChooserUI.this.doDirectoryChanged(propertyChangeEvent);
            }
            else if (propertyName == "MultiSelectionEnabledChangedProperty") {
                SynthFileChooserUI.this.doMultiSelectionChanged(propertyChangeEvent);
            }
            else if (propertyName == "AccessoryChangedProperty") {
                SynthFileChooserUI.this.doAccessoryChanged(propertyChangeEvent);
            }
            else if (propertyName == "ApproveButtonTextChangedProperty" || propertyName == "ApproveButtonToolTipTextChangedProperty" || propertyName == "DialogTypeChangedProperty" || propertyName == "ControlButtonsAreShownChangedProperty") {
                SynthFileChooserUI.this.doControlButtonsChanged(propertyChangeEvent);
            }
            else if (propertyName.equals("componentOrientation")) {
                final ComponentOrientation componentOrientation = (ComponentOrientation)propertyChangeEvent.getNewValue();
                final JFileChooser fileChooser = (JFileChooser)propertyChangeEvent.getSource();
                if (componentOrientation != propertyChangeEvent.getOldValue()) {
                    fileChooser.applyComponentOrientation(componentOrientation);
                }
            }
            else if (propertyName.equals("ancestor")) {
                SynthFileChooserUI.this.doAncestorChanged(propertyChangeEvent);
            }
        }
    }
    
    private class FileNameCompletionAction extends AbstractAction
    {
        protected FileNameCompletionAction() {
            super("fileNameCompletion");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JFileChooser fileChooser = SynthFileChooserUI.this.getFileChooser();
            String s = SynthFileChooserUI.this.getFileName();
            if (s != null) {
                s = s.trim();
            }
            SynthFileChooserUI.this.resetGlobFilter();
            if (s == null || s.equals("") || (fileChooser.isMultiSelectionEnabled() && s.startsWith("\""))) {
                return;
            }
            final FileFilter fileFilter = fileChooser.getFileFilter();
            if (SynthFileChooserUI.this.globFilter == null) {
                SynthFileChooserUI.this.globFilter = new GlobFilter();
            }
            try {
                SynthFileChooserUI.this.globFilter.setPattern(isGlobPattern(s) ? s : (s + "*"));
                if (!(fileFilter instanceof GlobFilter)) {
                    SynthFileChooserUI.this.actualFileFilter = fileFilter;
                }
                fileChooser.setFileFilter(null);
                fileChooser.setFileFilter(SynthFileChooserUI.this.globFilter);
                SynthFileChooserUI.this.fileNameCompletionString = s;
            }
            catch (final PatternSyntaxException ex) {}
        }
    }
    
    class GlobFilter extends FileFilter
    {
        Pattern pattern;
        String globPattern;
        
        public void setPattern(final String globPattern) {
            final char[] charArray = globPattern.toCharArray();
            final char[] array = new char[charArray.length * 2];
            final boolean b = File.separatorChar == '\\';
            int n = 0;
            int n2 = 0;
            this.globPattern = globPattern;
            if (b) {
                int length = charArray.length;
                if (globPattern.endsWith("*.*")) {
                    length -= 2;
                }
                for (int i = 0; i < length; ++i) {
                    if (charArray[i] == '*') {
                        array[n2++] = '.';
                    }
                    array[n2++] = charArray[i];
                }
            }
            else {
                for (int j = 0; j < charArray.length; ++j) {
                    switch (charArray[j]) {
                        case '*': {
                            if (n == 0) {
                                array[n2++] = '.';
                            }
                            array[n2++] = '*';
                            break;
                        }
                        case '?': {
                            array[n2++] = ((n != 0) ? '?' : '.');
                            break;
                        }
                        case '[': {
                            n = 1;
                            array[n2++] = charArray[j];
                            if (j < charArray.length - 1) {
                                switch (charArray[j + 1]) {
                                    case '!':
                                    case '^': {
                                        array[n2++] = '^';
                                        ++j;
                                        break;
                                    }
                                    case ']': {
                                        array[n2++] = charArray[++j];
                                        break;
                                    }
                                }
                                break;
                            }
                            break;
                        }
                        case ']': {
                            array[n2++] = charArray[j];
                            n = 0;
                            break;
                        }
                        case '\\': {
                            if (j == 0 && charArray.length > 1 && charArray[1] == '~') {
                                array[n2++] = charArray[++j];
                                break;
                            }
                            array[n2++] = '\\';
                            if (j < charArray.length - 1 && "*?[]".indexOf(charArray[j + 1]) >= 0) {
                                array[n2++] = charArray[++j];
                                break;
                            }
                            array[n2++] = '\\';
                            break;
                        }
                        default: {
                            if (!Character.isLetterOrDigit(charArray[j])) {
                                array[n2++] = '\\';
                            }
                            array[n2++] = charArray[j];
                            break;
                        }
                    }
                }
            }
            this.pattern = Pattern.compile(new String(array, 0, n2), 2);
        }
        
        @Override
        public boolean accept(final File file) {
            return file != null && (file.isDirectory() || this.pattern.matcher(file.getName()).matches());
        }
        
        @Override
        public String getDescription() {
            return this.globPattern;
        }
    }
    
    private class UIBorder extends AbstractBorder implements UIResource
    {
        private Insets _insets;
        
        UIBorder(final Insets insets) {
            if (insets != null) {
                this._insets = new Insets(insets.top, insets.left, insets.bottom, insets.right);
            }
            else {
                this._insets = null;
            }
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof JComponent)) {
                return;
            }
            final SynthContext context = SynthFileChooserUI.this.getContext((JComponent)component);
            final SynthStyle style = context.getStyle();
            if (style != null) {
                style.getPainter(context).paintFileChooserBorder(context, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, Insets insets) {
            if (insets == null) {
                insets = new Insets(0, 0, 0, 0);
            }
            if (this._insets != null) {
                insets.top = this._insets.top;
                insets.bottom = this._insets.bottom;
                insets.left = this._insets.left;
                insets.right = this._insets.right;
            }
            else {
                final Insets insets2 = insets;
                final Insets insets3 = insets;
                final Insets insets4 = insets;
                final Insets insets5 = insets;
                final int n = 0;
                insets5.left = n;
                insets4.right = n;
                insets3.bottom = n;
                insets2.top = n;
            }
            return insets;
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
