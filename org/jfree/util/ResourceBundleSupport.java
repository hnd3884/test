package org.jfree.util;

import java.util.MissingResourceException;
import javax.swing.KeyStroke;
import java.text.MessageFormat;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import java.lang.reflect.Field;
import javax.swing.JMenu;
import java.net.URL;
import java.awt.Toolkit;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.util.Locale;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.ResourceBundle;

public class ResourceBundleSupport
{
    private ResourceBundle resources;
    private TreeMap cache;
    private TreeSet lookupPath;
    private String resourceBase;
    private Locale locale;
    static /* synthetic */ Class class$org$jfree$util$ResourceBundleSupport;
    static /* synthetic */ Class class$java$awt$event$KeyEvent;
    
    public ResourceBundleSupport(final String baseName) {
        this(Locale.getDefault(), ResourceBundle.getBundle(baseName), baseName);
    }
    
    public ResourceBundleSupport(final Locale locale, final String baseName) {
        this(locale, ResourceBundle.getBundle(baseName, locale), baseName);
    }
    
    public ResourceBundleSupport(final Locale locale, final ResourceBundle resourceBundle) {
        this(locale, resourceBundle, resourceBundle.toString());
    }
    
    protected ResourceBundleSupport(final Locale locale, final ResourceBundle resourceBundle, final String baseName) {
        if (locale == null) {
            throw new NullPointerException("Locale must not be null");
        }
        if (resourceBundle == null) {
            throw new NullPointerException("Resources must not be null");
        }
        if (baseName == null) {
            throw new NullPointerException("BaseName must not be null");
        }
        this.locale = locale;
        this.resources = resourceBundle;
        this.resourceBase = baseName;
        this.cache = new TreeMap();
        this.lookupPath = new TreeSet();
    }
    
    public ResourceBundleSupport(final ResourceBundle resourceBundle) {
        this(Locale.getDefault(), resourceBundle, resourceBundle.toString());
    }
    
    protected ResourceBundleSupport(final ResourceBundle resourceBundle, final String baseName) {
        this(Locale.getDefault(), resourceBundle, baseName);
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    private ImageIcon createIcon(final String resourceName, final boolean scale, final boolean large) {
        final URL in = ObjectUtilities.getResource(resourceName, (ResourceBundleSupport.class$org$jfree$util$ResourceBundleSupport != null) ? ResourceBundleSupport.class$org$jfree$util$ResourceBundleSupport : (ResourceBundleSupport.class$org$jfree$util$ResourceBundleSupport = class$("org.jfree.util.ResourceBundleSupport")));
        if (in == null) {
            Log.warn("Unable to find file in the class path: " + resourceName);
            return new ImageIcon(this.createTransparentImage(1, 1));
        }
        final Image img = Toolkit.getDefaultToolkit().createImage(in);
        if (img == null) {
            Log.warn("Unable to instantiate the image: " + resourceName);
            return new ImageIcon(this.createTransparentImage(1, 1));
        }
        if (!scale) {
            return new ImageIcon(img);
        }
        if (large) {
            return new ImageIcon(img.getScaledInstance(24, 24, 4));
        }
        return new ImageIcon(img.getScaledInstance(16, 16, 4));
    }
    
    public JMenu createMenu(final String keyPrefix) {
        final JMenu retval = new JMenu();
        retval.setText(this.getString(String.valueOf(keyPrefix) + ".name"));
        retval.setMnemonic(this.getMnemonic(String.valueOf(keyPrefix) + ".mnemonic"));
        return retval;
    }
    
    private Integer createMnemonic(final String keyString) {
        if (keyString == null) {
            throw new NullPointerException("Key is null.");
        }
        if (keyString.length() == 0) {
            throw new IllegalArgumentException("Key is empty.");
        }
        int character = keyString.charAt(0);
        if (keyString.startsWith("VK_")) {
            try {
                final Field f = ((ResourceBundleSupport.class$java$awt$event$KeyEvent != null) ? ResourceBundleSupport.class$java$awt$event$KeyEvent : (ResourceBundleSupport.class$java$awt$event$KeyEvent = class$("java.awt.event.KeyEvent"))).getField(keyString);
                final Integer keyCode = (Integer)f.get(null);
                character = keyCode;
            }
            catch (final Exception ex) {}
        }
        return new Integer(character);
    }
    
    public Icon createTransparentIcon(final int width, final int height) {
        return new ImageIcon(this.createTransparentImage(width, height));
    }
    
    private BufferedImage createTransparentImage(final int width, final int height) {
        final BufferedImage img = new BufferedImage(width, height, 2);
        final int[] data = img.getRGB(0, 0, width, height, null, 0, width);
        Arrays.fill(data, 0);
        img.setRGB(0, 0, width, height, data, 0, width);
        return img;
    }
    
    public String formatMessage(final String key, final Object parameter) {
        return this.formatMessage(this.getString(key), new Object[] { parameter });
    }
    
    public String formatMessage(final String key, final Object par1, final Object par2) {
        return this.formatMessage(this.getString(key), new Object[] { par1, par2 });
    }
    
    public String formatMessage(final String key, final Object[] parameters) {
        final MessageFormat format = new MessageFormat(this.getString(key));
        format.setLocale(this.getLocale());
        return format.format(parameters);
    }
    
    public Icon getIcon(final String key) {
        final String name = this.getString(key);
        return this.createIcon(name, false, false);
    }
    
    public Icon getIcon(final String key, final boolean large) {
        final String name = this.getString(key);
        return this.createIcon(name, true, large);
    }
    
    public KeyStroke getKeyStroke(final String key) {
        return this.getKeyStroke(key, this.getMenuKeyMask());
    }
    
    public KeyStroke getKeyStroke(final String key, final int mask) {
        final String name = this.getString(key);
        return KeyStroke.getKeyStroke(this.createMnemonic(name), mask);
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    private int getMenuKeyMask() {
        try {
            return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        }
        catch (final UnsupportedOperationException ex) {
            return 2;
        }
    }
    
    public Integer getMnemonic(final String key) {
        final String name = this.getString(key);
        return this.createMnemonic(name);
    }
    
    protected final String getResourceBase() {
        return this.resourceBase;
    }
    
    public URL getResourceURL(final String key) {
        final String name = this.getString(key);
        final URL in = ObjectUtilities.getResource(name, (ResourceBundleSupport.class$org$jfree$util$ResourceBundleSupport != null) ? ResourceBundleSupport.class$org$jfree$util$ResourceBundleSupport : (ResourceBundleSupport.class$org$jfree$util$ResourceBundleSupport = class$("org.jfree.util.ResourceBundleSupport")));
        if (in == null) {
            Log.warn("Unable to find file in the class path: " + name + "; key=" + key);
        }
        return in;
    }
    
    public synchronized String getString(final String key) {
        final String retval = this.cache.get(key);
        if (retval != null) {
            return retval;
        }
        this.lookupPath.clear();
        return this.internalGetString(key);
    }
    
    protected String internalGetString(final String key) {
        if (this.lookupPath.contains(key)) {
            throw new MissingResourceException("InfiniteLoop in resource lookup", this.getResourceBase(), this.lookupPath.toString());
        }
        final String fromResBundle = this.resources.getString(key);
        if (fromResBundle.startsWith("@@")) {
            final int idx = fromResBundle.indexOf(64, 2);
            if (idx == -1) {
                throw new MissingResourceException("Invalid format for global lookup key.", this.getResourceBase(), key);
            }
            try {
                final ResourceBundle res = ResourceBundle.getBundle(fromResBundle.substring(2, idx));
                return res.getString(fromResBundle.substring(idx + 1));
            }
            catch (final Exception e) {
                Log.error("Error during global lookup", e);
                throw new MissingResourceException("Error during global lookup", this.getResourceBase(), key);
            }
        }
        if (fromResBundle.startsWith("@")) {
            final String newKey = fromResBundle.substring(1);
            this.lookupPath.add(key);
            final String retval = this.internalGetString(newKey);
            this.cache.put(key, retval);
            return retval;
        }
        this.cache.put(key, fromResBundle);
        return fromResBundle;
    }
}
