package com.sun.java.swing.plaf.windows;

import sun.swing.UIClientPropertyKey;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import javax.swing.SwingUtilities;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.util.EnumMap;
import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import java.util.WeakHashMap;
import sun.awt.AppContext;
import javax.swing.Timer;
import javax.swing.JComponent;
import java.util.Map;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionListener;

class AnimationController implements ActionListener, PropertyChangeListener
{
    private static final boolean VISTA_ANIMATION_DISABLED;
    private static final Object ANIMATION_CONTROLLER_KEY;
    private final Map<JComponent, Map<TMSchema.Part, AnimationState>> animationStateMap;
    private final Timer timer;
    
    private static synchronized AnimationController getAnimationController() {
        final AppContext appContext = AppContext.getAppContext();
        Object value = appContext.get(AnimationController.ANIMATION_CONTROLLER_KEY);
        if (value == null) {
            value = new AnimationController();
            appContext.put(AnimationController.ANIMATION_CONTROLLER_KEY, value);
        }
        return (AnimationController)value;
    }
    
    private AnimationController() {
        this.animationStateMap = new WeakHashMap<JComponent, Map<TMSchema.Part, AnimationState>>();
        (this.timer = new Timer(33, this)).setRepeats(true);
        this.timer.setCoalesce(true);
        UIManager.addPropertyChangeListener(this);
    }
    
    private static void triggerAnimation(final JComponent component, final TMSchema.Part part, final TMSchema.State state) {
        if (component instanceof JTabbedPane || part == TMSchema.Part.TP_BUTTON) {
            return;
        }
        final AnimationController animationController = getAnimationController();
        TMSchema.State state2 = animationController.getState(component, part);
        if (state2 != state) {
            animationController.putState(component, part, state);
            if (state == TMSchema.State.DEFAULTED) {
                state2 = TMSchema.State.HOT;
            }
            if (state2 != null) {
                long n;
                if (state == TMSchema.State.DEFAULTED) {
                    n = 1000L;
                }
                else {
                    final XPStyle xp = XPStyle.getXP();
                    n = ((xp != null) ? xp.getThemeTransitionDuration(component, part, normalizeState(state2), normalizeState(state), TMSchema.Prop.TRANSITIONDURATIONS) : 1000L);
                }
                animationController.startAnimation(component, part, state2, state, n);
            }
        }
    }
    
    private static TMSchema.State normalizeState(final TMSchema.State state) {
        TMSchema.State state2 = null;
        switch (state) {
            case DOWNPRESSED:
            case LEFTPRESSED:
            case RIGHTPRESSED: {
                state2 = TMSchema.State.UPPRESSED;
                break;
            }
            case DOWNDISABLED:
            case LEFTDISABLED:
            case RIGHTDISABLED: {
                state2 = TMSchema.State.UPDISABLED;
                break;
            }
            case DOWNHOT:
            case LEFTHOT:
            case RIGHTHOT: {
                state2 = TMSchema.State.UPHOT;
                break;
            }
            case DOWNNORMAL:
            case LEFTNORMAL:
            case RIGHTNORMAL: {
                state2 = TMSchema.State.UPNORMAL;
                break;
            }
            default: {
                state2 = state;
                break;
            }
        }
        return state2;
    }
    
    private synchronized TMSchema.State getState(final JComponent component, final TMSchema.Part part) {
        TMSchema.State state = null;
        final Object clientProperty = component.getClientProperty(PartUIClientPropertyKey.getKey(part));
        if (clientProperty instanceof TMSchema.State) {
            state = (TMSchema.State)clientProperty;
        }
        return state;
    }
    
    private synchronized void putState(final JComponent component, final TMSchema.Part part, final TMSchema.State state) {
        component.putClientProperty(PartUIClientPropertyKey.getKey(part), state);
    }
    
    private synchronized void startAnimation(final JComponent component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.State state2, final long n) {
        boolean b = false;
        if (state2 == TMSchema.State.DEFAULTED) {
            b = true;
        }
        Map<TMSchema.Part, AnimationState> map = this.animationStateMap.get(component);
        if (n <= 0L) {
            if (map != null) {
                map.remove(part);
                if (map.size() == 0) {
                    this.animationStateMap.remove(component);
                }
            }
            return;
        }
        if (map == null) {
            map = new EnumMap<TMSchema.Part, AnimationState>(TMSchema.Part.class);
            this.animationStateMap.put(component, map);
        }
        map.put(part, new AnimationState(state, n, b));
        if (!this.timer.isRunning()) {
            this.timer.start();
        }
    }
    
    static void paintSkin(final JComponent component, final XPStyle.Skin skin, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final TMSchema.State state) {
        if (AnimationController.VISTA_ANIMATION_DISABLED) {
            skin.paintSkinRaw(graphics, n, n2, n3, n4, state);
            return;
        }
        triggerAnimation(component, skin.part, state);
        final AnimationController animationController = getAnimationController();
        synchronized (animationController) {
            AnimationState animationState = null;
            final Map map = animationController.animationStateMap.get(component);
            if (map != null) {
                animationState = (AnimationState)map.get(skin.part);
            }
            if (animationState != null) {
                animationState.paintSkin(skin, graphics, n, n2, n3, n4, state);
            }
            else {
                skin.paintSkinRaw(graphics, n, n2, n3, n4, state);
            }
        }
    }
    
    @Override
    public synchronized void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if ("lookAndFeel" == propertyChangeEvent.getPropertyName() && !(propertyChangeEvent.getNewValue() instanceof WindowsLookAndFeel)) {
            this.dispose();
        }
    }
    
    @Override
    public synchronized void actionPerformed(final ActionEvent actionEvent) {
        List<JComponent> list = null;
        List list2 = null;
        for (final JComponent component : this.animationStateMap.keySet()) {
            component.repaint();
            if (list2 != null) {
                list2.clear();
            }
            final Map map = this.animationStateMap.get(component);
            if (!component.isShowing() || map == null || map.size() == 0) {
                if (list == null) {
                    list = new ArrayList<JComponent>();
                }
                list.add(component);
            }
            else {
                for (final TMSchema.Part part : map.keySet()) {
                    if (((AnimationState)map.get(part)).isDone()) {
                        if (list2 == null) {
                            list2 = new ArrayList();
                        }
                        list2.add(part);
                    }
                }
                if (list2 == null) {
                    continue;
                }
                if (list2.size() == map.size()) {
                    if (list == null) {
                        list = new ArrayList<JComponent>();
                    }
                    list.add(component);
                }
                else {
                    final Iterator iterator3 = list2.iterator();
                    while (iterator3.hasNext()) {
                        map.remove(iterator3.next());
                    }
                }
            }
        }
        if (list != null) {
            final Iterator<JComponent> iterator4 = list.iterator();
            while (iterator4.hasNext()) {
                this.animationStateMap.remove(iterator4.next());
            }
        }
        if (this.animationStateMap.size() == 0) {
            this.timer.stop();
        }
    }
    
    private synchronized void dispose() {
        this.timer.stop();
        UIManager.removePropertyChangeListener(this);
        synchronized (AnimationController.class) {
            AppContext.getAppContext().put(AnimationController.ANIMATION_CONTROLLER_KEY, null);
        }
    }
    
    static {
        VISTA_ANIMATION_DISABLED = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("swing.disablevistaanimation"));
        ANIMATION_CONTROLLER_KEY = new StringBuilder("ANIMATION_CONTROLLER_KEY");
    }
    
    private static class AnimationState
    {
        private final TMSchema.State startState;
        private final long duration;
        private long startTime;
        private boolean isForward;
        private boolean isForwardAndReverse;
        private float progress;
        
        AnimationState(final TMSchema.State startState, final long n, final boolean isForwardAndReverse) {
            this.isForward = true;
            assert startState != null && n > 0L;
            assert SwingUtilities.isEventDispatchThread();
            this.startState = startState;
            this.duration = n * 1000000L;
            this.startTime = System.nanoTime();
            this.isForwardAndReverse = isForwardAndReverse;
            this.progress = 0.0f;
        }
        
        private void updateProgress() {
            assert SwingUtilities.isEventDispatchThread();
            if (this.isDone()) {
                return;
            }
            final long nanoTime = System.nanoTime();
            this.progress = (nanoTime - this.startTime) / (float)this.duration;
            this.progress = Math.max(this.progress, 0.0f);
            if (this.progress >= 1.0f) {
                this.progress = 1.0f;
                if (this.isForwardAndReverse) {
                    this.startTime = nanoTime;
                    this.progress = 0.0f;
                    this.isForward = !this.isForward;
                }
            }
        }
        
        void paintSkin(final XPStyle.Skin skin, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final TMSchema.State state) {
            assert SwingUtilities.isEventDispatchThread();
            this.updateProgress();
            if (!this.isDone()) {
                final Graphics2D graphics2D = (Graphics2D)graphics.create();
                skin.paintSkinRaw(graphics2D, n, n2, n3, n4, this.startState);
                float progress;
                if (this.isForward) {
                    progress = this.progress;
                }
                else {
                    progress = 1.0f - this.progress;
                }
                graphics2D.setComposite(AlphaComposite.SrcOver.derive(progress));
                skin.paintSkinRaw(graphics2D, n, n2, n3, n4, state);
                graphics2D.dispose();
            }
            else {
                skin.paintSkinRaw(graphics, n, n2, n3, n4, state);
            }
        }
        
        boolean isDone() {
            assert SwingUtilities.isEventDispatchThread();
            return this.progress >= 1.0f;
        }
    }
    
    private static class PartUIClientPropertyKey implements UIClientPropertyKey
    {
        private static final Map<TMSchema.Part, PartUIClientPropertyKey> map;
        private final TMSchema.Part part;
        
        static synchronized PartUIClientPropertyKey getKey(final TMSchema.Part part) {
            PartUIClientPropertyKey partUIClientPropertyKey = PartUIClientPropertyKey.map.get(part);
            if (partUIClientPropertyKey == null) {
                partUIClientPropertyKey = new PartUIClientPropertyKey(part);
                PartUIClientPropertyKey.map.put(part, partUIClientPropertyKey);
            }
            return partUIClientPropertyKey;
        }
        
        private PartUIClientPropertyKey(final TMSchema.Part part) {
            this.part = part;
        }
        
        @Override
        public String toString() {
            return this.part.toString();
        }
        
        static {
            map = new EnumMap<TMSchema.Part, PartUIClientPropertyKey>(TMSchema.Part.class);
        }
    }
}
