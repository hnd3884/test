package javax.swing;

import java.awt.IllegalComponentStateException;
import java.util.Locale;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleIcon;
import java.lang.reflect.Field;
import javax.accessibility.AccessibleContext;
import java.awt.image.PixelGrabber;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.image.ColorModel;
import java.io.ObjectInputStream;
import java.awt.Graphics;
import java.beans.Transient;
import sun.awt.AppContext;
import java.beans.ConstructorProperties;
import java.awt.Toolkit;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.PrivilegedAction;
import java.awt.MediaTracker;
import java.awt.Component;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.net.URL;
import javax.accessibility.Accessible;
import java.io.Serializable;

public class ImageIcon implements Icon, Serializable, Accessible
{
    private transient String filename;
    private transient URL location;
    transient Image image;
    transient int loadStatus;
    ImageObserver imageObserver;
    String description;
    @Deprecated
    protected static final Component component;
    @Deprecated
    protected static final MediaTracker tracker;
    private static int mediaTrackerID;
    private static final Object TRACKER_KEY;
    int width;
    int height;
    private AccessibleImageIcon accessibleContext;
    
    private static Component createNoPermsComponent() {
        return AccessController.doPrivileged((PrivilegedAction<Component>)new PrivilegedAction<Component>() {
            @Override
            public Component run() {
                return new Component() {};
            }
        }, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) }));
    }
    
    public ImageIcon(final String filename, final String description) {
        this.loadStatus = 0;
        this.description = null;
        this.width = -1;
        this.height = -1;
        this.accessibleContext = null;
        this.image = Toolkit.getDefaultToolkit().getImage(filename);
        if (this.image == null) {
            return;
        }
        this.filename = filename;
        this.description = description;
        this.loadImage(this.image);
    }
    
    @ConstructorProperties({ "description" })
    public ImageIcon(final String s) {
        this(s, s);
    }
    
    public ImageIcon(final URL location, final String description) {
        this.loadStatus = 0;
        this.description = null;
        this.width = -1;
        this.height = -1;
        this.accessibleContext = null;
        this.image = Toolkit.getDefaultToolkit().getImage(location);
        if (this.image == null) {
            return;
        }
        this.location = location;
        this.description = description;
        this.loadImage(this.image);
    }
    
    public ImageIcon(final URL url) {
        this(url, url.toExternalForm());
    }
    
    public ImageIcon(final Image image, final String description) {
        this(image);
        this.description = description;
    }
    
    public ImageIcon(final Image image) {
        this.loadStatus = 0;
        this.description = null;
        this.width = -1;
        this.height = -1;
        this.accessibleContext = null;
        this.image = image;
        final Object property = image.getProperty("comment", this.imageObserver);
        if (property instanceof String) {
            this.description = (String)property;
        }
        this.loadImage(image);
    }
    
    public ImageIcon(final byte[] array, final String description) {
        this.loadStatus = 0;
        this.description = null;
        this.width = -1;
        this.height = -1;
        this.accessibleContext = null;
        this.image = Toolkit.getDefaultToolkit().createImage(array);
        if (this.image == null) {
            return;
        }
        this.description = description;
        this.loadImage(this.image);
    }
    
    public ImageIcon(final byte[] array) {
        this.loadStatus = 0;
        this.description = null;
        this.width = -1;
        this.height = -1;
        this.accessibleContext = null;
        this.image = Toolkit.getDefaultToolkit().createImage(array);
        if (this.image == null) {
            return;
        }
        final Object property = this.image.getProperty("comment", this.imageObserver);
        if (property instanceof String) {
            this.description = (String)property;
        }
        this.loadImage(this.image);
    }
    
    public ImageIcon() {
        this.loadStatus = 0;
        this.description = null;
        this.width = -1;
        this.height = -1;
        this.accessibleContext = null;
    }
    
    protected void loadImage(final Image image) {
        final MediaTracker tracker = this.getTracker();
        synchronized (tracker) {
            final int nextID = this.getNextID();
            tracker.addImage(image, nextID);
            try {
                tracker.waitForID(nextID, 0L);
            }
            catch (final InterruptedException ex) {
                System.out.println("INTERRUPTED while loading Image");
            }
            this.loadStatus = tracker.statusID(nextID, false);
            tracker.removeImage(image, nextID);
            this.width = image.getWidth(this.imageObserver);
            this.height = image.getHeight(this.imageObserver);
        }
    }
    
    private int getNextID() {
        synchronized (this.getTracker()) {
            return ++ImageIcon.mediaTrackerID;
        }
    }
    
    private MediaTracker getTracker() {
        final AppContext appContext = AppContext.getAppContext();
        Object value;
        synchronized (appContext) {
            value = appContext.get(ImageIcon.TRACKER_KEY);
            if (value == null) {
                value = new MediaTracker(new Component() {});
                appContext.put(ImageIcon.TRACKER_KEY, value);
            }
        }
        return (MediaTracker)value;
    }
    
    public int getImageLoadStatus() {
        return this.loadStatus;
    }
    
    @Transient
    public Image getImage() {
        return this.image;
    }
    
    public void setImage(final Image image) {
        this.loadImage(this.image = image);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    @Override
    public synchronized void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        if (this.imageObserver == null) {
            graphics.drawImage(this.image, n, n2, component);
        }
        else {
            graphics.drawImage(this.image, n, n2, this.imageObserver);
        }
    }
    
    @Override
    public int getIconWidth() {
        return this.width;
    }
    
    @Override
    public int getIconHeight() {
        return this.height;
    }
    
    public void setImageObserver(final ImageObserver imageObserver) {
        this.imageObserver = imageObserver;
    }
    
    @Transient
    public ImageObserver getImageObserver() {
        return this.imageObserver;
    }
    
    @Override
    public String toString() {
        if (this.description != null) {
            return this.description;
        }
        return super.toString();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        final int int1 = objectInputStream.readInt();
        final int int2 = objectInputStream.readInt();
        final int[] array = (int[])objectInputStream.readObject();
        if (array != null) {
            this.loadImage(this.image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(int1, int2, ColorModel.getRGBdefault(), array, 0, int1)));
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        final int iconWidth = this.getIconWidth();
        final int iconHeight = this.getIconHeight();
        final int[] array = (int[])((this.image != null) ? new int[iconWidth * iconHeight] : null);
        if (this.image != null) {
            try {
                final PixelGrabber pixelGrabber = new PixelGrabber(this.image, 0, 0, iconWidth, iconHeight, array, 0, iconWidth);
                pixelGrabber.grabPixels();
                if ((pixelGrabber.getStatus() & 0x80) != 0x0) {
                    throw new IOException("failed to load image contents");
                }
            }
            catch (final InterruptedException ex) {
                throw new IOException("image load interrupted");
            }
        }
        objectOutputStream.writeInt(iconWidth);
        objectOutputStream.writeInt(iconHeight);
        objectOutputStream.writeObject(array);
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleImageIcon();
        }
        return this.accessibleContext;
    }
    
    static {
        component = AccessController.doPrivileged((PrivilegedAction<Component>)new PrivilegedAction<Component>() {
            @Override
            public Component run() {
                try {
                    final Component access$000 = createNoPermsComponent();
                    final Field declaredField = Component.class.getDeclaredField("appContext");
                    declaredField.setAccessible(true);
                    declaredField.set(access$000, null);
                    return access$000;
                }
                catch (final Throwable t) {
                    t.printStackTrace();
                    return null;
                }
            }
        });
        tracker = new MediaTracker(ImageIcon.component);
        TRACKER_KEY = new StringBuilder("TRACKER_KEY");
    }
    
    protected class AccessibleImageIcon extends AccessibleContext implements AccessibleIcon, Serializable
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.ICON;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            return null;
        }
        
        @Override
        public Accessible getAccessibleParent() {
            return null;
        }
        
        @Override
        public int getAccessibleIndexInParent() {
            return -1;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return 0;
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            return null;
        }
        
        @Override
        public Locale getLocale() throws IllegalComponentStateException {
            return null;
        }
        
        @Override
        public String getAccessibleIconDescription() {
            return ImageIcon.this.getDescription();
        }
        
        @Override
        public void setAccessibleIconDescription(final String description) {
            ImageIcon.this.setDescription(description);
        }
        
        @Override
        public int getAccessibleIconHeight() {
            return ImageIcon.this.height;
        }
        
        @Override
        public int getAccessibleIconWidth() {
            return ImageIcon.this.width;
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            objectInputStream.defaultReadObject();
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
        }
    }
}
