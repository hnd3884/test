package javax.swing;

import java.applet.Applet;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.awt.Window;
import java.awt.Container;
import sun.awt.EmbeddedFrame;
import java.security.AccessController;
import sun.awt.OSInfo;
import java.awt.GraphicsEnvironment;
import java.awt.Component;

public class PopupFactory
{
    private static final Object SharedInstanceKey;
    private static final int MAX_CACHE_SIZE = 5;
    static final int LIGHT_WEIGHT_POPUP = 0;
    static final int MEDIUM_WEIGHT_POPUP = 1;
    static final int HEAVY_WEIGHT_POPUP = 2;
    private int popupType;
    
    public PopupFactory() {
        this.popupType = 0;
    }
    
    public static void setSharedInstance(final PopupFactory popupFactory) {
        if (popupFactory == null) {
            throw new IllegalArgumentException("PopupFactory can not be null");
        }
        SwingUtilities.appContextPut(PopupFactory.SharedInstanceKey, popupFactory);
    }
    
    public static PopupFactory getSharedInstance() {
        PopupFactory sharedInstance = (PopupFactory)SwingUtilities.appContextGet(PopupFactory.SharedInstanceKey);
        if (sharedInstance == null) {
            sharedInstance = new PopupFactory();
            setSharedInstance(sharedInstance);
        }
        return sharedInstance;
    }
    
    void setPopupType(final int popupType) {
        this.popupType = popupType;
    }
    
    int getPopupType() {
        return this.popupType;
    }
    
    public Popup getPopup(final Component component, final Component component2, final int n, final int n2) throws IllegalArgumentException {
        if (component2 == null) {
            throw new IllegalArgumentException("Popup.getPopup must be passed non-null contents");
        }
        Popup popup = this.getPopup(component, component2, n, n2, this.getPopupType(component, component2, n, n2));
        if (popup == null) {
            popup = this.getPopup(component, component2, n, n2, 2);
        }
        return popup;
    }
    
    private int getPopupType(final Component component, final Component component2, final int n, final int n2) {
        int popupType = this.getPopupType();
        if (component == null || this.invokerInHeavyWeightPopup(component)) {
            popupType = 2;
        }
        else if (popupType == 0 && !(component2 instanceof JToolTip) && !(component2 instanceof JPopupMenu)) {
            popupType = 1;
        }
        for (Component parent = component; parent != null; parent = parent.getParent()) {
            if (parent instanceof JComponent && ((JComponent)parent).getClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP) == Boolean.TRUE) {
                popupType = 2;
                break;
            }
        }
        return popupType;
    }
    
    private Popup getPopup(final Component component, final Component component2, final int n, final int n2, final int n3) {
        if (GraphicsEnvironment.isHeadless()) {
            return this.getHeadlessPopup(component, component2, n, n2);
        }
        switch (n3) {
            case 0: {
                return this.getLightWeightPopup(component, component2, n, n2);
            }
            case 1: {
                return this.getMediumWeightPopup(component, component2, n, n2);
            }
            case 2: {
                final Popup heavyWeightPopup = this.getHeavyWeightPopup(component, component2, n, n2);
                if (AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.MACOSX && component != null && EmbeddedFrame.getAppletIfAncestorOf(component) != null) {
                    ((HeavyWeightPopup)heavyWeightPopup).setCacheEnabled(false);
                }
                return heavyWeightPopup;
            }
            default: {
                return null;
            }
        }
    }
    
    private Popup getHeadlessPopup(final Component component, final Component component2, final int n, final int n2) {
        return HeadlessPopup.getHeadlessPopup(component, component2, n, n2);
    }
    
    private Popup getLightWeightPopup(final Component component, final Component component2, final int n, final int n2) {
        return LightWeightPopup.getLightWeightPopup(component, component2, n, n2);
    }
    
    private Popup getMediumWeightPopup(final Component component, final Component component2, final int n, final int n2) {
        return MediumWeightPopup.getMediumWeightPopup(component, component2, n, n2);
    }
    
    private Popup getHeavyWeightPopup(final Component component, final Component component2, final int n, final int n2) {
        if (GraphicsEnvironment.isHeadless()) {
            return this.getMediumWeightPopup(component, component2, n, n2);
        }
        return HeavyWeightPopup.getHeavyWeightPopup(component, component2, n, n2);
    }
    
    private boolean invokerInHeavyWeightPopup(final Component component) {
        if (component != null) {
            for (Container container = component.getParent(); container != null; container = container.getParent()) {
                if (container instanceof Popup.HeavyWeightWindow) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        SharedInstanceKey = new StringBuffer("PopupFactory.SharedInstanceKey");
    }
    
    private static class HeavyWeightPopup extends Popup
    {
        private static final Object heavyWeightPopupCacheKey;
        private volatile boolean isCacheEnabled;
        
        private HeavyWeightPopup() {
            this.isCacheEnabled = true;
        }
        
        static Popup getHeavyWeightPopup(final Component component, final Component component2, final int n, final int n2) {
            final Window window = (component != null) ? SwingUtilities.getWindowAncestor(component) : null;
            HeavyWeightPopup recycledHeavyWeightPopup = null;
            if (window != null) {
                recycledHeavyWeightPopup = getRecycledHeavyWeightPopup(window);
            }
            boolean b = false;
            if (component2 != null && component2.isFocusable() && component2 instanceof JPopupMenu) {
                for (final Component component3 : ((JPopupMenu)component2).getComponents()) {
                    if (!(component3 instanceof MenuElement) && !(component3 instanceof JSeparator)) {
                        b = true;
                        break;
                    }
                }
            }
            if (recycledHeavyWeightPopup == null || ((JWindow)recycledHeavyWeightPopup.getComponent()).getFocusableWindowState() != b) {
                if (recycledHeavyWeightPopup != null) {
                    recycledHeavyWeightPopup._dispose();
                }
                recycledHeavyWeightPopup = new HeavyWeightPopup();
            }
            recycledHeavyWeightPopup.reset(component, component2, n, n2);
            if (b) {
                final JWindow window2 = (JWindow)recycledHeavyWeightPopup.getComponent();
                window2.setFocusableWindowState(true);
                window2.setName("###focusableSwingPopup###");
            }
            return recycledHeavyWeightPopup;
        }
        
        private static HeavyWeightPopup getRecycledHeavyWeightPopup(final Window window) {
            synchronized (HeavyWeightPopup.class) {
                final Map<Window, List<HeavyWeightPopup>> heavyWeightPopupCache = getHeavyWeightPopupCache();
                if (!heavyWeightPopupCache.containsKey(window)) {
                    return null;
                }
                final List list = heavyWeightPopupCache.get(window);
                if (list.size() > 0) {
                    final HeavyWeightPopup heavyWeightPopup = (HeavyWeightPopup)list.get(0);
                    list.remove(0);
                    return heavyWeightPopup;
                }
                return null;
            }
        }
        
        private static Map<Window, List<HeavyWeightPopup>> getHeavyWeightPopupCache() {
            synchronized (HeavyWeightPopup.class) {
                Map map = (Map)SwingUtilities.appContextGet(HeavyWeightPopup.heavyWeightPopupCacheKey);
                if (map == null) {
                    map = new HashMap(2);
                    SwingUtilities.appContextPut(HeavyWeightPopup.heavyWeightPopupCacheKey, map);
                }
                return map;
            }
        }
        
        private static void recycleHeavyWeightPopup(final HeavyWeightPopup heavyWeightPopup) {
            synchronized (HeavyWeightPopup.class) {
                final Window windowAncestor = SwingUtilities.getWindowAncestor(heavyWeightPopup.getComponent());
                final Map<Window, List<HeavyWeightPopup>> heavyWeightPopupCache = getHeavyWeightPopupCache();
                if (windowAncestor instanceof DefaultFrame || !windowAncestor.isVisible()) {
                    heavyWeightPopup._dispose();
                    return;
                }
                List list;
                if (heavyWeightPopupCache.containsKey(windowAncestor)) {
                    list = heavyWeightPopupCache.get(windowAncestor);
                }
                else {
                    list = new ArrayList();
                    heavyWeightPopupCache.put(windowAncestor, list);
                    final Window window = windowAncestor;
                    window.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(final WindowEvent windowEvent) {
                            final List list;
                            synchronized (HeavyWeightPopup.class) {
                                list = getHeavyWeightPopupCache().remove(window);
                            }
                            if (list != null) {
                                for (int i = list.size() - 1; i >= 0; --i) {
                                    ((HeavyWeightPopup)list.get(i))._dispose();
                                }
                            }
                        }
                    });
                }
                if (list.size() < 5) {
                    list.add(heavyWeightPopup);
                }
                else {
                    heavyWeightPopup._dispose();
                }
            }
        }
        
        void setCacheEnabled(final boolean isCacheEnabled) {
            this.isCacheEnabled = isCacheEnabled;
        }
        
        @Override
        public void hide() {
            super.hide();
            if (this.isCacheEnabled) {
                recycleHeavyWeightPopup(this);
            }
            else {
                this._dispose();
            }
        }
        
        @Override
        void dispose() {
        }
        
        void _dispose() {
            super.dispose();
        }
        
        static {
            heavyWeightPopupCacheKey = new StringBuffer("PopupFactory.heavyWeightPopupCache");
        }
    }
    
    private static class ContainerPopup extends Popup
    {
        Component owner;
        int x;
        int y;
        
        @Override
        public void hide() {
            final Component component = this.getComponent();
            if (component != null) {
                final Container parent = component.getParent();
                if (parent != null) {
                    final Rectangle bounds = component.getBounds();
                    parent.remove(component);
                    parent.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
                }
            }
            this.owner = null;
        }
        
        public void pack() {
            final Component component = this.getComponent();
            if (component != null) {
                component.setSize(component.getPreferredSize());
            }
        }
        
        @Override
        void reset(Component layeredPane, final Component component, final int x, final int y) {
            if (layeredPane instanceof JFrame || layeredPane instanceof JDialog || layeredPane instanceof JWindow) {
                layeredPane = ((RootPaneContainer)layeredPane).getLayeredPane();
            }
            super.reset(layeredPane, component, x, y);
            this.x = x;
            this.y = y;
            this.owner = layeredPane;
        }
        
        boolean overlappedByOwnedWindow() {
            final Component component = this.getComponent();
            if (this.owner != null && component != null) {
                final Window windowAncestor = SwingUtilities.getWindowAncestor(this.owner);
                if (windowAncestor == null) {
                    return false;
                }
                final Window[] ownedWindows = windowAncestor.getOwnedWindows();
                if (ownedWindows != null) {
                    final Rectangle bounds = component.getBounds();
                    for (final Window window : ownedWindows) {
                        if (window.isVisible() && bounds.intersects(window.getBounds())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        
        boolean fitsOnScreen() {
            boolean b = false;
            final Component component = this.getComponent();
            if (this.owner != null && component != null) {
                final int width = component.getWidth();
                final int height = component.getHeight();
                final Container container = (Container)SwingUtilities.getRoot(this.owner);
                if (container instanceof JFrame || container instanceof JDialog || container instanceof JWindow) {
                    final Rectangle bounds = container.getBounds();
                    final Insets insets = container.getInsets();
                    final Rectangle rectangle = bounds;
                    rectangle.x += insets.left;
                    final Rectangle rectangle2 = bounds;
                    rectangle2.y += insets.top;
                    final Rectangle rectangle3 = bounds;
                    rectangle3.width -= insets.left + insets.right;
                    final Rectangle rectangle4 = bounds;
                    rectangle4.height -= insets.top + insets.bottom;
                    if (JPopupMenu.canPopupOverlapTaskBar()) {
                        b = bounds.intersection(this.getContainerPopupArea(container.getGraphicsConfiguration())).contains(this.x, this.y, width, height);
                    }
                    else {
                        b = bounds.contains(this.x, this.y, width, height);
                    }
                }
                else if (container instanceof JApplet) {
                    final Rectangle bounds2 = container.getBounds();
                    final Point locationOnScreen = container.getLocationOnScreen();
                    bounds2.x = locationOnScreen.x;
                    bounds2.y = locationOnScreen.y;
                    b = bounds2.contains(this.x, this.y, width, height);
                }
            }
            return b;
        }
        
        Rectangle getContainerPopupArea(final GraphicsConfiguration graphicsConfiguration) {
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            Rectangle bounds;
            Insets screenInsets;
            if (graphicsConfiguration != null) {
                bounds = graphicsConfiguration.getBounds();
                screenInsets = defaultToolkit.getScreenInsets(graphicsConfiguration);
            }
            else {
                bounds = new Rectangle(defaultToolkit.getScreenSize());
                screenInsets = new Insets(0, 0, 0, 0);
            }
            final Rectangle rectangle = bounds;
            rectangle.x += screenInsets.left;
            final Rectangle rectangle2 = bounds;
            rectangle2.y += screenInsets.top;
            final Rectangle rectangle3 = bounds;
            rectangle3.width -= screenInsets.left + screenInsets.right;
            final Rectangle rectangle4 = bounds;
            rectangle4.height -= screenInsets.top + screenInsets.bottom;
            return bounds;
        }
    }
    
    private static class HeadlessPopup extends ContainerPopup
    {
        static Popup getHeadlessPopup(final Component component, final Component component2, final int n, final int n2) {
            final HeadlessPopup headlessPopup = new HeadlessPopup();
            headlessPopup.reset(component, component2, n, n2);
            return headlessPopup;
        }
        
        @Override
        Component createComponent(final Component component) {
            return new Panel(new BorderLayout());
        }
        
        @Override
        public void show() {
        }
        
        @Override
        public void hide() {
        }
    }
    
    private static class LightWeightPopup extends ContainerPopup
    {
        private static final Object lightWeightPopupCacheKey;
        
        static Popup getLightWeightPopup(final Component component, final Component component2, final int n, final int n2) {
            LightWeightPopup recycledLightWeightPopup = getRecycledLightWeightPopup();
            if (recycledLightWeightPopup == null) {
                recycledLightWeightPopup = new LightWeightPopup();
            }
            recycledLightWeightPopup.reset(component, component2, n, n2);
            if (!recycledLightWeightPopup.fitsOnScreen() || recycledLightWeightPopup.overlappedByOwnedWindow()) {
                recycledLightWeightPopup.hide();
                return null;
            }
            return recycledLightWeightPopup;
        }
        
        private static List<LightWeightPopup> getLightWeightPopupCache() {
            List list = (List)SwingUtilities.appContextGet(LightWeightPopup.lightWeightPopupCacheKey);
            if (list == null) {
                list = new ArrayList();
                SwingUtilities.appContextPut(LightWeightPopup.lightWeightPopupCacheKey, list);
            }
            return list;
        }
        
        private static void recycleLightWeightPopup(final LightWeightPopup lightWeightPopup) {
            synchronized (LightWeightPopup.class) {
                final List<LightWeightPopup> lightWeightPopupCache = getLightWeightPopupCache();
                if (lightWeightPopupCache.size() < 5) {
                    lightWeightPopupCache.add(lightWeightPopup);
                }
            }
        }
        
        private static LightWeightPopup getRecycledLightWeightPopup() {
            synchronized (LightWeightPopup.class) {
                final List<LightWeightPopup> lightWeightPopupCache = getLightWeightPopupCache();
                if (lightWeightPopupCache.size() > 0) {
                    final LightWeightPopup lightWeightPopup = lightWeightPopupCache.get(0);
                    lightWeightPopupCache.remove(0);
                    return lightWeightPopup;
                }
                return null;
            }
        }
        
        @Override
        public void hide() {
            super.hide();
            ((Container)this.getComponent()).removeAll();
            recycleLightWeightPopup(this);
        }
        
        @Override
        public void show() {
            Container layeredPane = null;
            if (this.owner != null) {
                layeredPane = (Container)((this.owner instanceof Container) ? this.owner : this.owner.getParent());
            }
            for (Container parent = layeredPane; parent != null; parent = parent.getParent()) {
                if (parent instanceof JRootPane) {
                    if (!(parent.getParent() instanceof JInternalFrame)) {
                        layeredPane = ((JRootPane)parent).getLayeredPane();
                    }
                }
                else if (parent instanceof Window) {
                    if (layeredPane == null) {
                        layeredPane = parent;
                        break;
                    }
                    break;
                }
                else if (parent instanceof JApplet) {
                    break;
                }
            }
            final Point convertScreenLocationToParent = SwingUtilities.convertScreenLocationToParent(layeredPane, this.x, this.y);
            final Component component = this.getComponent();
            component.setLocation(convertScreenLocationToParent.x, convertScreenLocationToParent.y);
            if (layeredPane instanceof JLayeredPane) {
                layeredPane.add(component, JLayeredPane.POPUP_LAYER, 0);
            }
            else {
                layeredPane.add(component);
            }
        }
        
        @Override
        Component createComponent(final Component component) {
            final JPanel panel = new JPanel(new BorderLayout(), true);
            panel.setOpaque(true);
            return panel;
        }
        
        @Override
        void reset(final Component component, final Component component2, final int n, final int n2) {
            super.reset(component, component2, n, n2);
            final JComponent component3 = (JComponent)this.getComponent();
            component3.setOpaque(component2.isOpaque());
            component3.setLocation(n, n2);
            component3.add(component2, "Center");
            component2.invalidate();
            this.pack();
        }
        
        static {
            lightWeightPopupCacheKey = new StringBuffer("PopupFactory.lightPopupCache");
        }
    }
    
    private static class MediumWeightPopup extends ContainerPopup
    {
        private static final Object mediumWeightPopupCacheKey;
        private JRootPane rootPane;
        
        static Popup getMediumWeightPopup(final Component component, final Component component2, final int n, final int n2) {
            MediumWeightPopup recycledMediumWeightPopup = getRecycledMediumWeightPopup();
            if (recycledMediumWeightPopup == null) {
                recycledMediumWeightPopup = new MediumWeightPopup();
            }
            recycledMediumWeightPopup.reset(component, component2, n, n2);
            if (!recycledMediumWeightPopup.fitsOnScreen() || recycledMediumWeightPopup.overlappedByOwnedWindow()) {
                recycledMediumWeightPopup.hide();
                return null;
            }
            return recycledMediumWeightPopup;
        }
        
        private static List<MediumWeightPopup> getMediumWeightPopupCache() {
            List list = (List)SwingUtilities.appContextGet(MediumWeightPopup.mediumWeightPopupCacheKey);
            if (list == null) {
                list = new ArrayList();
                SwingUtilities.appContextPut(MediumWeightPopup.mediumWeightPopupCacheKey, list);
            }
            return list;
        }
        
        private static void recycleMediumWeightPopup(final MediumWeightPopup mediumWeightPopup) {
            synchronized (MediumWeightPopup.class) {
                final List<MediumWeightPopup> mediumWeightPopupCache = getMediumWeightPopupCache();
                if (mediumWeightPopupCache.size() < 5) {
                    mediumWeightPopupCache.add(mediumWeightPopup);
                }
            }
        }
        
        private static MediumWeightPopup getRecycledMediumWeightPopup() {
            synchronized (MediumWeightPopup.class) {
                final List<MediumWeightPopup> mediumWeightPopupCache = getMediumWeightPopupCache();
                if (mediumWeightPopupCache.size() > 0) {
                    final MediumWeightPopup mediumWeightPopup = mediumWeightPopupCache.get(0);
                    mediumWeightPopupCache.remove(0);
                    return mediumWeightPopup;
                }
                return null;
            }
        }
        
        @Override
        public void hide() {
            super.hide();
            this.rootPane.getContentPane().removeAll();
            recycleMediumWeightPopup(this);
        }
        
        @Override
        public void show() {
            final Component component = this.getComponent();
            Container container = null;
            if (this.owner != null) {
                container = this.owner.getParent();
            }
            while (!(container instanceof Window) && !(container instanceof Applet) && container != null) {
                container = container.getParent();
            }
            if (container instanceof RootPaneContainer) {
                final JLayeredPane layeredPane = ((RootPaneContainer)container).getLayeredPane();
                final Point convertScreenLocationToParent = SwingUtilities.convertScreenLocationToParent(layeredPane, this.x, this.y);
                component.setVisible(false);
                component.setLocation(convertScreenLocationToParent.x, convertScreenLocationToParent.y);
                layeredPane.add(component, JLayeredPane.POPUP_LAYER, 0);
            }
            else {
                final Point convertScreenLocationToParent2 = SwingUtilities.convertScreenLocationToParent(container, this.x, this.y);
                component.setLocation(convertScreenLocationToParent2.x, convertScreenLocationToParent2.y);
                component.setVisible(false);
                container.add(component);
            }
            component.setVisible(true);
        }
        
        @Override
        Component createComponent(final Component component) {
            final MediumWeightComponent mediumWeightComponent = new MediumWeightComponent();
            (this.rootPane = new JRootPane()).setOpaque(true);
            mediumWeightComponent.add(this.rootPane, "Center");
            return mediumWeightComponent;
        }
        
        @Override
        void reset(final Component component, final Component component2, final int n, final int n2) {
            super.reset(component, component2, n, n2);
            final Component component3 = this.getComponent();
            component3.setLocation(n, n2);
            this.rootPane.getContentPane().add(component2, "Center");
            component2.invalidate();
            component3.validate();
            this.pack();
        }
        
        static {
            mediumWeightPopupCacheKey = new StringBuffer("PopupFactory.mediumPopupCache");
        }
        
        private static class MediumWeightComponent extends Panel implements SwingHeavyWeight
        {
            MediumWeightComponent() {
                super(new BorderLayout());
            }
        }
    }
}
