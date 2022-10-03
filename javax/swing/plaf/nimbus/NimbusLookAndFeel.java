package javax.swing.plaf.nimbus;

import javax.swing.plaf.UIResource;
import java.util.Iterator;
import java.util.HashMap;
import java.awt.LayoutManager;
import java.awt.Container;
import java.awt.BorderLayout;
import javax.swing.JToolBar;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import sun.swing.ImageIconUIResource;
import java.awt.Image;
import javax.swing.GrayFilter;
import java.awt.Graphics;
import java.awt.Component;
import sun.swing.plaf.synth.SynthIcon;
import javax.swing.Icon;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.beans.PropertyChangeEvent;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import sun.swing.plaf.GTKKeybindings;
import sun.swing.plaf.WindowsKeybindings;
import java.beans.PropertyChangeListener;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.Region;
import javax.swing.JComponent;
import javax.swing.plaf.synth.SynthStyleFactory;
import java.util.Map;
import javax.swing.UIDefaults;
import javax.swing.plaf.synth.SynthLookAndFeel;

public class NimbusLookAndFeel extends SynthLookAndFeel
{
    private static final String[] COMPONENT_KEYS;
    private NimbusDefaults defaults;
    private UIDefaults uiDefaults;
    private DefaultsListener defaultsListener;
    private Map<String, Map<String, Object>> compiledDefaults;
    private boolean defaultListenerAdded;
    
    public NimbusLookAndFeel() {
        this.defaultsListener = new DefaultsListener();
        this.compiledDefaults = null;
        this.defaultListenerAdded = false;
        this.defaults = new NimbusDefaults();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        this.defaults.initialize();
        SynthLookAndFeel.setStyleFactory(new SynthStyleFactory() {
            @Override
            public SynthStyle getStyle(final JComponent component, final Region region) {
                return NimbusLookAndFeel.this.defaults.getStyle(component, region);
            }
        });
    }
    
    @Override
    public void uninitialize() {
        super.uninitialize();
        this.defaults.uninitialize();
        ImageCache.getInstance().flush();
        UIManager.getDefaults().removePropertyChangeListener(this.defaultsListener);
    }
    
    @Override
    public UIDefaults getDefaults() {
        if (this.uiDefaults == null) {
            final String systemProperty = this.getSystemProperty("os.name");
            final boolean b = systemProperty != null && systemProperty.contains("Windows");
            this.uiDefaults = super.getDefaults();
            this.defaults.initializeDefaults(this.uiDefaults);
            if (b) {
                WindowsKeybindings.installKeybindings(this.uiDefaults);
            }
            else {
                GTKKeybindings.installKeybindings(this.uiDefaults);
            }
            this.uiDefaults.put("TitledBorder.titlePosition", 1);
            this.uiDefaults.put("TitledBorder.border", new BorderUIResource(new LoweredBorder()));
            this.uiDefaults.put("TitledBorder.titleColor", this.getDerivedColor("text", 0.0f, 0.0f, 0.23f, 0, true));
            this.uiDefaults.put("TitledBorder.font", new NimbusDefaults.DerivedFont("defaultFont", 1.0f, true, null));
            this.uiDefaults.put("OptionPane.isYesLast", !b);
            this.uiDefaults.put("Table.scrollPaneCornerComponent", new UIDefaults.ActiveValue() {
                @Override
                public Object createValue(final UIDefaults uiDefaults) {
                    return new TableScrollPaneCorner();
                }
            });
            this.uiDefaults.put("ToolBarSeparator[Enabled].backgroundPainter", new ToolBarSeparatorPainter());
            for (final String s : NimbusLookAndFeel.COMPONENT_KEYS) {
                final String string = s + ".foreground";
                if (!this.uiDefaults.containsKey(string)) {
                    this.uiDefaults.put(string, new NimbusProperty(s, "textForeground"));
                }
                final String string2 = s + ".background";
                if (!this.uiDefaults.containsKey(string2)) {
                    this.uiDefaults.put(string2, new NimbusProperty(s, "background"));
                }
                final String string3 = s + ".font";
                if (!this.uiDefaults.containsKey(string3)) {
                    this.uiDefaults.put(string3, new NimbusProperty(s, "font"));
                }
                final String string4 = s + ".disabledText";
                if (!this.uiDefaults.containsKey(string4)) {
                    this.uiDefaults.put(string4, new NimbusProperty(s, "Disabled", "textForeground"));
                }
                final String string5 = s + ".disabled";
                if (!this.uiDefaults.containsKey(string5)) {
                    this.uiDefaults.put(string5, new NimbusProperty(s, "Disabled", "background"));
                }
            }
            this.uiDefaults.put("FileView.computerIcon", new LinkProperty("FileChooser.homeFolderIcon"));
            this.uiDefaults.put("FileView.directoryIcon", new LinkProperty("FileChooser.directoryIcon"));
            this.uiDefaults.put("FileView.fileIcon", new LinkProperty("FileChooser.fileIcon"));
            this.uiDefaults.put("FileView.floppyDriveIcon", new LinkProperty("FileChooser.floppyDriveIcon"));
            this.uiDefaults.put("FileView.hardDriveIcon", new LinkProperty("FileChooser.hardDriveIcon"));
        }
        return this.uiDefaults;
    }
    
    public static NimbusStyle getStyle(final JComponent component, final Region region) {
        return (NimbusStyle)SynthLookAndFeel.getStyle(component, region);
    }
    
    @Override
    public String getName() {
        return "Nimbus";
    }
    
    @Override
    public String getID() {
        return "Nimbus";
    }
    
    @Override
    public String getDescription() {
        return "Nimbus Look and Feel";
    }
    
    @Override
    public boolean shouldUpdateStyleOnAncestorChanged() {
        return true;
    }
    
    @Override
    protected boolean shouldUpdateStyleOnEvent(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        if ("name" == propertyName || "ancestor" == propertyName || "Nimbus.Overrides" == propertyName || "Nimbus.Overrides.InheritDefaults" == propertyName || "JComponent.sizeVariant" == propertyName) {
            this.defaults.clearOverridesCache((JComponent)propertyChangeEvent.getSource());
            return true;
        }
        return super.shouldUpdateStyleOnEvent(propertyChangeEvent);
    }
    
    public void register(final Region region, final String s) {
        this.defaults.register(region, s);
    }
    
    private String getSystemProperty(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s));
    }
    
    @Override
    public Icon getDisabledIcon(final JComponent component, final Icon icon) {
        if (icon instanceof SynthIcon) {
            final SynthIcon synthIcon = (SynthIcon)icon;
            final BufferedImage compatibleTranslucentImage = EffectUtils.createCompatibleTranslucentImage(synthIcon.getIconWidth(), synthIcon.getIconHeight());
            final Graphics2D graphics = compatibleTranslucentImage.createGraphics();
            synthIcon.paintIcon(component, graphics, 0, 0);
            graphics.dispose();
            return new ImageIconUIResource(GrayFilter.createDisabledImage(compatibleTranslucentImage));
        }
        return super.getDisabledIcon(component, icon);
    }
    
    public Color getDerivedColor(final String s, final float n, final float n2, final float n3, final int n4, final boolean b) {
        return this.defaults.getDerivedColor(s, n, n2, n3, n4, b);
    }
    
    protected final Color getDerivedColor(final Color color, final Color color2, final float n, final boolean b) {
        final int deriveARGB = deriveARGB(color, color2, n);
        if (b) {
            return new ColorUIResource(deriveARGB);
        }
        return new Color(deriveARGB);
    }
    
    protected final Color getDerivedColor(final Color color, final Color color2, final float n) {
        return this.getDerivedColor(color, color2, n, true);
    }
    
    static Object resolveToolbarConstraint(final JToolBar toolBar) {
        if (toolBar != null) {
            final Container parent = toolBar.getParent();
            if (parent != null) {
                final LayoutManager layout = parent.getLayout();
                if (layout instanceof BorderLayout) {
                    final Object constraints = ((BorderLayout)layout).getConstraints(toolBar);
                    if (constraints == "South" || constraints == "East" || constraints == "West") {
                        return constraints;
                    }
                    return "North";
                }
            }
        }
        return "North";
    }
    
    static int deriveARGB(final Color color, final Color color2, final float n) {
        return (color.getAlpha() + Math.round((color2.getAlpha() - color.getAlpha()) * n) & 0xFF) << 24 | (color.getRed() + Math.round((color2.getRed() - color.getRed()) * n) & 0xFF) << 16 | (color.getGreen() + Math.round((color2.getGreen() - color.getGreen()) * n) & 0xFF) << 8 | (color.getBlue() + Math.round((color2.getBlue() - color.getBlue()) * n) & 0xFF);
    }
    
    static String parsePrefix(final String s) {
        if (s == null) {
            return null;
        }
        boolean b = false;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '\"') {
                b = !b;
            }
            else if ((char1 == '[' || char1 == '.') && !b) {
                return s.substring(0, i);
            }
        }
        return null;
    }
    
    Map<String, Object> getDefaultsForPrefix(final String s) {
        if (this.compiledDefaults == null) {
            this.compiledDefaults = new HashMap<String, Map<String, Object>>();
            for (final Map.Entry entry : UIManager.getDefaults().entrySet()) {
                if (entry.getKey() instanceof String) {
                    this.addDefault((String)entry.getKey(), entry.getValue());
                }
            }
            if (!this.defaultListenerAdded) {
                UIManager.getDefaults().addPropertyChangeListener(this.defaultsListener);
                this.defaultListenerAdded = true;
            }
        }
        return this.compiledDefaults.get(s);
    }
    
    private void addDefault(final String s, final Object o) {
        if (this.compiledDefaults == null) {
            return;
        }
        final String prefix = parsePrefix(s);
        if (prefix != null) {
            Map map = this.compiledDefaults.get(prefix);
            if (map == null) {
                map = new HashMap();
                this.compiledDefaults.put(prefix, map);
            }
            map.put(s, o);
        }
    }
    
    static {
        COMPONENT_KEYS = new String[] { "ArrowButton", "Button", "CheckBox", "CheckBoxMenuItem", "ColorChooser", "ComboBox", "DesktopPane", "DesktopIcon", "EditorPane", "FileChooser", "FormattedTextField", "InternalFrame", "InternalFrameTitlePane", "Label", "List", "Menu", "MenuBar", "MenuItem", "OptionPane", "Panel", "PasswordField", "PopupMenu", "PopupMenuSeparator", "ProgressBar", "RadioButton", "RadioButtonMenuItem", "RootPane", "ScrollBar", "ScrollBarTrack", "ScrollBarThumb", "ScrollPane", "Separator", "Slider", "SliderTrack", "SliderThumb", "Spinner", "SplitPane", "TabbedPane", "Table", "TableHeader", "TextArea", "TextField", "TextPane", "ToggleButton", "ToolBar", "ToolTip", "Tree", "Viewport" };
    }
    
    private class LinkProperty implements UIDefaults.ActiveValue, UIResource
    {
        private String dstPropName;
        
        private LinkProperty(final String dstPropName) {
            this.dstPropName = dstPropName;
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            return UIManager.get(this.dstPropName);
        }
    }
    
    private class NimbusProperty implements UIDefaults.ActiveValue, UIResource
    {
        private String prefix;
        private String state;
        private String suffix;
        private boolean isFont;
        
        private NimbusProperty(final String prefix, final String suffix) {
            this.state = null;
            this.prefix = prefix;
            this.suffix = suffix;
            this.isFont = "font".equals(suffix);
        }
        
        private NimbusProperty(final NimbusLookAndFeel nimbusLookAndFeel, final String s, final String state, final String s2) {
            this(nimbusLookAndFeel, s, s2);
            this.state = state;
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            Object o = null;
            if (this.state != null) {
                o = NimbusLookAndFeel.this.uiDefaults.get(this.prefix + "[" + this.state + "]." + this.suffix);
            }
            if (o == null) {
                o = NimbusLookAndFeel.this.uiDefaults.get(this.prefix + "[Enabled]." + this.suffix);
            }
            if (o == null) {
                if (this.isFont) {
                    o = NimbusLookAndFeel.this.uiDefaults.get("defaultFont");
                }
                else {
                    o = NimbusLookAndFeel.this.uiDefaults.get(this.suffix);
                }
            }
            return o;
        }
    }
    
    private class DefaultsListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if ("UIDefaults".equals(propertyName)) {
                NimbusLookAndFeel.this.compiledDefaults = null;
            }
            else {
                NimbusLookAndFeel.this.addDefault(propertyName, propertyChangeEvent.getNewValue());
            }
        }
    }
}
