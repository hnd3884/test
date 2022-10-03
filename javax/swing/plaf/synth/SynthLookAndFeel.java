package javax.swing.plaf.synth;

import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.Window;
import java.awt.Toolkit;
import java.lang.ref.WeakReference;
import javax.swing.plaf.InsetsUIResource;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingUtilities2;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Locale;
import javax.swing.UIDefaults;
import java.beans.PropertyChangeListener;
import java.awt.KeyboardFocusManager;
import sun.swing.DefaultLookup;
import java.io.IOException;
import java.text.ParseException;
import java.net.URL;
import java.util.HashMap;
import java.io.InputStream;
import sun.swing.plaf.synth.SynthFileChooserUI;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Container;
import javax.swing.JMenu;
import javax.swing.UIManager;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.LookAndFeel;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import sun.awt.AppContext;
import java.awt.Insets;
import javax.swing.plaf.basic.BasicLookAndFeel;

public class SynthLookAndFeel extends BasicLookAndFeel
{
    static final Insets EMPTY_UIRESOURCE_INSETS;
    private static final Object STYLE_FACTORY_KEY;
    private static final Object SELECTED_UI_KEY;
    private static final Object SELECTED_UI_STATE_KEY;
    private static SynthStyleFactory lastFactory;
    private static AppContext lastContext;
    private SynthStyleFactory factory;
    private Map<String, Object> defaultsMap;
    private Handler _handler;
    private static ReferenceQueue<LookAndFeel> queue;
    
    static ComponentUI getSelectedUI() {
        return (ComponentUI)AppContext.getAppContext().get(SynthLookAndFeel.SELECTED_UI_KEY);
    }
    
    static void setSelectedUI(final ComponentUI componentUI, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        final int n = 0;
        int n2;
        if (b) {
            n2 = 512;
            if (b2) {
                n2 |= 0x100;
            }
        }
        else if (b4 && b3) {
            n2 = (n | 0x3);
            if (b2) {
                n2 |= 0x100;
            }
        }
        else if (b3) {
            n2 = (n | 0x1);
            if (b2) {
                n2 |= 0x100;
            }
        }
        else {
            n2 = (n | 0x8);
        }
        final AppContext appContext = AppContext.getAppContext();
        appContext.put(SynthLookAndFeel.SELECTED_UI_KEY, componentUI);
        appContext.put(SynthLookAndFeel.SELECTED_UI_STATE_KEY, n2);
    }
    
    static int getSelectedUIState() {
        final Integer n = (Integer)AppContext.getAppContext().get(SynthLookAndFeel.SELECTED_UI_STATE_KEY);
        return (n == null) ? 0 : n;
    }
    
    static void resetSelectedUI() {
        AppContext.getAppContext().remove(SynthLookAndFeel.SELECTED_UI_KEY);
    }
    
    public static void setStyleFactory(final SynthStyleFactory lastFactory) {
        synchronized (SynthLookAndFeel.class) {
            final AppContext appContext = AppContext.getAppContext();
            SynthLookAndFeel.lastFactory = lastFactory;
            (SynthLookAndFeel.lastContext = appContext).put(SynthLookAndFeel.STYLE_FACTORY_KEY, lastFactory);
        }
    }
    
    public static SynthStyleFactory getStyleFactory() {
        synchronized (SynthLookAndFeel.class) {
            final AppContext appContext = AppContext.getAppContext();
            if (SynthLookAndFeel.lastContext == appContext) {
                return SynthLookAndFeel.lastFactory;
            }
            SynthLookAndFeel.lastContext = appContext;
            return SynthLookAndFeel.lastFactory = (SynthStyleFactory)appContext.get(SynthLookAndFeel.STYLE_FACTORY_KEY);
        }
    }
    
    static int getComponentState(final Component component) {
        if (!component.isEnabled()) {
            return 8;
        }
        if (component.isFocusOwner()) {
            return 257;
        }
        return 1;
    }
    
    public static SynthStyle getStyle(final JComponent component, final Region region) {
        return getStyleFactory().getStyle(component, region);
    }
    
    static boolean shouldUpdateStyle(final PropertyChangeEvent propertyChangeEvent) {
        final LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        return lookAndFeel instanceof SynthLookAndFeel && ((SynthLookAndFeel)lookAndFeel).shouldUpdateStyleOnEvent(propertyChangeEvent);
    }
    
    static SynthStyle updateStyle(final SynthContext synthContext, final SynthUI synthUI) {
        final SynthStyle style = getStyle(synthContext.getComponent(), synthContext.getRegion());
        final SynthStyle style2 = synthContext.getStyle();
        if (style != style2) {
            if (style2 != null) {
                style2.uninstallDefaults(synthContext);
            }
            synthContext.setStyle(style);
            style.installDefaults(synthContext, synthUI);
        }
        return style;
    }
    
    public static void updateStyles(final Component component) {
        if (component instanceof JComponent) {
            final String name = component.getName();
            component.setName(null);
            if (name != null) {
                component.setName(name);
            }
            ((JComponent)component).revalidate();
        }
        Component[] array = null;
        if (component instanceof JMenu) {
            array = ((JMenu)component).getMenuComponents();
        }
        else if (component instanceof Container) {
            array = ((Container)component).getComponents();
        }
        if (array != null) {
            final Component[] array2 = array;
            for (int length = array2.length, i = 0; i < length; ++i) {
                updateStyles(array2[i]);
            }
        }
        component.repaint();
    }
    
    public static Region getRegion(final JComponent component) {
        return Region.getRegion(component);
    }
    
    static Insets getPaintingInsets(final SynthContext synthContext, Insets insets) {
        if (synthContext.isSubregion()) {
            insets = synthContext.getStyle().getInsets(synthContext, insets);
        }
        else {
            insets = synthContext.getComponent().getInsets(insets);
        }
        return insets;
    }
    
    static void update(final SynthContext synthContext, final Graphics graphics) {
        paintRegion(synthContext, graphics, null);
    }
    
    static void updateSubregion(final SynthContext synthContext, final Graphics graphics, final Rectangle rectangle) {
        paintRegion(synthContext, graphics, rectangle);
    }
    
    private static void paintRegion(final SynthContext synthContext, final Graphics graphics, final Rectangle rectangle) {
        final JComponent component = synthContext.getComponent();
        final SynthStyle style = synthContext.getStyle();
        int x;
        int y;
        int n;
        int n2;
        if (rectangle == null) {
            x = 0;
            y = 0;
            n = component.getWidth();
            n2 = component.getHeight();
        }
        else {
            x = rectangle.x;
            y = rectangle.y;
            n = rectangle.width;
            n2 = rectangle.height;
        }
        final boolean subregion = synthContext.isSubregion();
        if ((subregion && style.isOpaque(synthContext)) || (!subregion && component.isOpaque())) {
            graphics.setColor(style.getColor(synthContext, ColorType.BACKGROUND));
            graphics.fillRect(x, y, n, n2);
        }
    }
    
    static boolean isLeftToRight(final Component component) {
        return component.getComponentOrientation().isLeftToRight();
    }
    
    static Object getUIOfType(final ComponentUI componentUI, final Class clazz) {
        if (clazz.isInstance(componentUI)) {
            return componentUI;
        }
        return null;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final String intern = component.getUIClassID().intern();
        if (intern == "ButtonUI") {
            return SynthButtonUI.createUI(component);
        }
        if (intern == "CheckBoxUI") {
            return SynthCheckBoxUI.createUI(component);
        }
        if (intern == "CheckBoxMenuItemUI") {
            return SynthCheckBoxMenuItemUI.createUI(component);
        }
        if (intern == "ColorChooserUI") {
            return SynthColorChooserUI.createUI(component);
        }
        if (intern == "ComboBoxUI") {
            return SynthComboBoxUI.createUI(component);
        }
        if (intern == "DesktopPaneUI") {
            return SynthDesktopPaneUI.createUI(component);
        }
        if (intern == "DesktopIconUI") {
            return SynthDesktopIconUI.createUI(component);
        }
        if (intern == "EditorPaneUI") {
            return SynthEditorPaneUI.createUI(component);
        }
        if (intern == "FileChooserUI") {
            return SynthFileChooserUI.createUI(component);
        }
        if (intern == "FormattedTextFieldUI") {
            return SynthFormattedTextFieldUI.createUI(component);
        }
        if (intern == "InternalFrameUI") {
            return SynthInternalFrameUI.createUI(component);
        }
        if (intern == "LabelUI") {
            return SynthLabelUI.createUI(component);
        }
        if (intern == "ListUI") {
            return SynthListUI.createUI(component);
        }
        if (intern == "MenuBarUI") {
            return SynthMenuBarUI.createUI(component);
        }
        if (intern == "MenuUI") {
            return SynthMenuUI.createUI(component);
        }
        if (intern == "MenuItemUI") {
            return SynthMenuItemUI.createUI(component);
        }
        if (intern == "OptionPaneUI") {
            return SynthOptionPaneUI.createUI(component);
        }
        if (intern == "PanelUI") {
            return SynthPanelUI.createUI(component);
        }
        if (intern == "PasswordFieldUI") {
            return SynthPasswordFieldUI.createUI(component);
        }
        if (intern == "PopupMenuSeparatorUI") {
            return SynthSeparatorUI.createUI(component);
        }
        if (intern == "PopupMenuUI") {
            return SynthPopupMenuUI.createUI(component);
        }
        if (intern == "ProgressBarUI") {
            return SynthProgressBarUI.createUI(component);
        }
        if (intern == "RadioButtonUI") {
            return SynthRadioButtonUI.createUI(component);
        }
        if (intern == "RadioButtonMenuItemUI") {
            return SynthRadioButtonMenuItemUI.createUI(component);
        }
        if (intern == "RootPaneUI") {
            return SynthRootPaneUI.createUI(component);
        }
        if (intern == "ScrollBarUI") {
            return SynthScrollBarUI.createUI(component);
        }
        if (intern == "ScrollPaneUI") {
            return SynthScrollPaneUI.createUI(component);
        }
        if (intern == "SeparatorUI") {
            return SynthSeparatorUI.createUI(component);
        }
        if (intern == "SliderUI") {
            return SynthSliderUI.createUI(component);
        }
        if (intern == "SpinnerUI") {
            return SynthSpinnerUI.createUI(component);
        }
        if (intern == "SplitPaneUI") {
            return SynthSplitPaneUI.createUI(component);
        }
        if (intern == "TabbedPaneUI") {
            return SynthTabbedPaneUI.createUI(component);
        }
        if (intern == "TableUI") {
            return SynthTableUI.createUI(component);
        }
        if (intern == "TableHeaderUI") {
            return SynthTableHeaderUI.createUI(component);
        }
        if (intern == "TextAreaUI") {
            return SynthTextAreaUI.createUI(component);
        }
        if (intern == "TextFieldUI") {
            return SynthTextFieldUI.createUI(component);
        }
        if (intern == "TextPaneUI") {
            return SynthTextPaneUI.createUI(component);
        }
        if (intern == "ToggleButtonUI") {
            return SynthToggleButtonUI.createUI(component);
        }
        if (intern == "ToolBarSeparatorUI") {
            return SynthSeparatorUI.createUI(component);
        }
        if (intern == "ToolBarUI") {
            return SynthToolBarUI.createUI(component);
        }
        if (intern == "ToolTipUI") {
            return SynthToolTipUI.createUI(component);
        }
        if (intern == "TreeUI") {
            return SynthTreeUI.createUI(component);
        }
        if (intern == "ViewportUI") {
            return SynthViewportUI.createUI(component);
        }
        return null;
    }
    
    public SynthLookAndFeel() {
        this.factory = new DefaultSynthStyleFactory();
        this._handler = new Handler();
    }
    
    public void load(final InputStream inputStream, final Class<?> clazz) throws ParseException {
        if (clazz == null) {
            throw new IllegalArgumentException("You must supply a valid resource base Class");
        }
        if (this.defaultsMap == null) {
            this.defaultsMap = new HashMap<String, Object>();
        }
        new SynthParser().parse(inputStream, (DefaultSynthStyleFactory)this.factory, null, clazz, this.defaultsMap);
    }
    
    public void load(final URL url) throws ParseException, IOException {
        if (url == null) {
            throw new IllegalArgumentException("You must supply a valid Synth set URL");
        }
        if (this.defaultsMap == null) {
            this.defaultsMap = new HashMap<String, Object>();
        }
        new SynthParser().parse(url.openStream(), (DefaultSynthStyleFactory)this.factory, url, null, this.defaultsMap);
    }
    
    @Override
    public void initialize() {
        super.initialize();
        DefaultLookup.setDefaultLookup(new SynthDefaultLookup());
        setStyleFactory(this.factory);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this._handler);
    }
    
    @Override
    public void uninitialize() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(this._handler);
        super.uninitialize();
    }
    
    @Override
    public UIDefaults getDefaults() {
        final UIDefaults uiDefaults = new UIDefaults(60, 0.75f);
        Region.registerUIs(uiDefaults);
        uiDefaults.setDefaultLocale(Locale.getDefault());
        uiDefaults.addResourceBundle("com.sun.swing.internal.plaf.basic.resources.basic");
        uiDefaults.addResourceBundle("com.sun.swing.internal.plaf.synth.resources.synth");
        uiDefaults.put("TabbedPane.isTabRollover", Boolean.TRUE);
        uiDefaults.put("ColorChooser.swatchesRecentSwatchSize", new Dimension(10, 10));
        uiDefaults.put("ColorChooser.swatchesDefaultRecentColor", Color.RED);
        uiDefaults.put("ColorChooser.swatchesSwatchSize", new Dimension(10, 10));
        uiDefaults.put("html.pendingImage", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/image-delayed.png"));
        uiDefaults.put("html.missingImage", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/image-failed.png"));
        uiDefaults.put("PopupMenu.selectedWindowInputMapBindings", new Object[] { "ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", "SPACE", "return" });
        uiDefaults.put("PopupMenu.selectedWindowInputMapBindings.RightToLeft", new Object[] { "LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent" });
        flushUnreferenced();
        uiDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, getAATextInfo());
        new AATextListener(this);
        if (this.defaultsMap != null) {
            uiDefaults.putAll(this.defaultsMap);
        }
        return uiDefaults;
    }
    
    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }
    
    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Synth look and feel";
    }
    
    @Override
    public String getName() {
        return "Synth look and feel";
    }
    
    @Override
    public String getID() {
        return "Synth";
    }
    
    public boolean shouldUpdateStyleOnAncestorChanged() {
        return false;
    }
    
    protected boolean shouldUpdateStyleOnEvent(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        return "name" == propertyName || "componentOrientation" == propertyName || ("ancestor" == propertyName && propertyChangeEvent.getNewValue() != null && this.shouldUpdateStyleOnAncestorChanged());
    }
    
    private static Object getAATextInfo() {
        final String language = Locale.getDefault().getLanguage();
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.desktop"));
        final boolean b = Locale.CHINESE.getLanguage().equals(language) || Locale.JAPANESE.getLanguage().equals(language) || Locale.KOREAN.getLanguage().equals(language);
        final boolean equals = "gnome".equals(s);
        return SwingUtilities2.AATextInfo.getAATextInfo(SwingUtilities2.isLocalDisplay() && (!equals || !b));
    }
    
    private static void flushUnreferenced() {
        AATextListener aaTextListener;
        while ((aaTextListener = (AATextListener)SynthLookAndFeel.queue.poll()) != null) {
            aaTextListener.dispose();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        throw new NotSerializableException(this.getClass().getName());
    }
    
    static {
        EMPTY_UIRESOURCE_INSETS = new InsetsUIResource(0, 0, 0, 0);
        STYLE_FACTORY_KEY = new StringBuffer("com.sun.java.swing.plaf.gtk.StyleCache");
        SELECTED_UI_KEY = new StringBuilder("selectedUI");
        SELECTED_UI_STATE_KEY = new StringBuilder("selectedUIState");
        SynthLookAndFeel.queue = new ReferenceQueue<LookAndFeel>();
    }
    
    private static class AATextListener extends WeakReference<LookAndFeel> implements PropertyChangeListener
    {
        private String key;
        private static boolean updatePending;
        
        AATextListener(final LookAndFeel lookAndFeel) {
            super(lookAndFeel, SynthLookAndFeel.queue);
            this.key = "awt.font.desktophints";
            Toolkit.getDefaultToolkit().addPropertyChangeListener(this.key, this);
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
            if (lookAndFeelDefaults.getBoolean("Synth.doNotSetTextAA")) {
                this.dispose();
                return;
            }
            final LookAndFeel lookAndFeel = this.get();
            if (lookAndFeel == null || lookAndFeel != UIManager.getLookAndFeel()) {
                this.dispose();
                return;
            }
            lookAndFeelDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, getAATextInfo());
            this.updateUI();
        }
        
        void dispose() {
            Toolkit.getDefaultToolkit().removePropertyChangeListener(this.key, this);
        }
        
        private static void updateWindowUI(final Window window) {
            SynthLookAndFeel.updateStyles(window);
            final Window[] ownedWindows = window.getOwnedWindows();
            for (int length = ownedWindows.length, i = 0; i < length; ++i) {
                updateWindowUI(ownedWindows[i]);
            }
        }
        
        private static void updateAllUIs() {
            final Frame[] frames = Frame.getFrames();
            for (int length = frames.length, i = 0; i < length; ++i) {
                updateWindowUI(frames[i]);
            }
        }
        
        private static synchronized void setUpdatePending(final boolean updatePending) {
            AATextListener.updatePending = updatePending;
        }
        
        private static synchronized boolean isUpdatePending() {
            return AATextListener.updatePending;
        }
        
        protected void updateUI() {
            if (!isUpdatePending()) {
                setUpdatePending(true);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateAllUIs();
                        setUpdatePending(false);
                    }
                });
            }
        }
    }
    
    private class Handler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            final Object newValue = propertyChangeEvent.getNewValue();
            final Object oldValue = propertyChangeEvent.getOldValue();
            if ("focusOwner" == propertyName) {
                if (oldValue instanceof JComponent) {
                    this.repaintIfBackgroundsDiffer((JComponent)oldValue);
                }
                if (newValue instanceof JComponent) {
                    this.repaintIfBackgroundsDiffer((JComponent)newValue);
                }
            }
            else if ("managingFocus" == propertyName) {
                final KeyboardFocusManager keyboardFocusManager = (KeyboardFocusManager)propertyChangeEvent.getSource();
                if (newValue.equals(Boolean.FALSE)) {
                    keyboardFocusManager.removePropertyChangeListener(SynthLookAndFeel.this._handler);
                }
                else {
                    keyboardFocusManager.addPropertyChangeListener(SynthLookAndFeel.this._handler);
                }
            }
        }
        
        private void repaintIfBackgroundsDiffer(final JComponent component) {
            final ComponentUI componentUI = (ComponentUI)component.getClientProperty(SwingUtilities2.COMPONENT_UI_PROPERTY_KEY);
            if (componentUI instanceof SynthUI) {
                final SynthContext context = ((SynthUI)componentUI).getContext(component);
                final SynthStyle style = context.getStyle();
                final int componentState = context.getComponentState();
                final Color color = style.getColor(context, ColorType.BACKGROUND);
                final int componentState2 = componentState ^ 0x100;
                context.setComponentState(componentState2);
                final Color color2 = style.getColor(context, ColorType.BACKGROUND);
                context.setComponentState(componentState2 ^ 0x100);
                if (color != null && !color.equals(color2)) {
                    component.repaint();
                }
                context.dispose();
            }
        }
    }
}
