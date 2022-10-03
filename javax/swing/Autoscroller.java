package javax.swing;

import java.awt.event.ActionEvent;
import java.awt.Point;
import sun.awt.AWTAccessor;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;

class Autoscroller implements ActionListener
{
    private static Autoscroller sharedInstance;
    private static MouseEvent event;
    private static Timer timer;
    private static JComponent component;
    
    public static void stop(final JComponent component) {
        Autoscroller.sharedInstance._stop(component);
    }
    
    public static boolean isRunning(final JComponent component) {
        return Autoscroller.sharedInstance._isRunning(component);
    }
    
    public static void processMouseDragged(final MouseEvent mouseEvent) {
        Autoscroller.sharedInstance._processMouseDragged(mouseEvent);
    }
    
    private void start(final JComponent component, final MouseEvent mouseEvent) {
        final Point locationOnScreen = component.getLocationOnScreen();
        if (Autoscroller.component != component) {
            this._stop(Autoscroller.component);
        }
        Autoscroller.component = component;
        Autoscroller.event = new MouseEvent(Autoscroller.component, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX() + locationOnScreen.x, mouseEvent.getY() + locationOnScreen.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
        final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
        mouseEventAccessor.setCausedByTouchEvent(Autoscroller.event, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
        if (Autoscroller.timer == null) {
            Autoscroller.timer = new Timer(100, this);
        }
        if (!Autoscroller.timer.isRunning()) {
            Autoscroller.timer.start();
        }
    }
    
    private void _stop(final JComponent component) {
        if (Autoscroller.component == component) {
            if (Autoscroller.timer != null) {
                Autoscroller.timer.stop();
            }
            Autoscroller.timer = null;
            Autoscroller.event = null;
            Autoscroller.component = null;
        }
    }
    
    private boolean _isRunning(final JComponent component) {
        return component == Autoscroller.component && Autoscroller.timer != null && Autoscroller.timer.isRunning();
    }
    
    private void _processMouseDragged(final MouseEvent mouseEvent) {
        final JComponent component = (JComponent)mouseEvent.getComponent();
        boolean contains = true;
        if (component.isShowing()) {
            contains = component.getVisibleRect().contains(mouseEvent.getX(), mouseEvent.getY());
        }
        if (contains) {
            this._stop(component);
        }
        else {
            this.start(component, mouseEvent);
        }
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        final JComponent component = Autoscroller.component;
        if (component == null || !component.isShowing() || Autoscroller.event == null) {
            this._stop(component);
            return;
        }
        final Point locationOnScreen = component.getLocationOnScreen();
        final MouseEvent mouseEvent = new MouseEvent(component, Autoscroller.event.getID(), Autoscroller.event.getWhen(), Autoscroller.event.getModifiers(), Autoscroller.event.getX() - locationOnScreen.x, Autoscroller.event.getY() - locationOnScreen.y, Autoscroller.event.getXOnScreen(), Autoscroller.event.getYOnScreen(), Autoscroller.event.getClickCount(), Autoscroller.event.isPopupTrigger(), 0);
        final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
        mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(Autoscroller.event));
        component.superProcessMouseMotionEvent(mouseEvent);
    }
    
    static {
        Autoscroller.sharedInstance = new Autoscroller();
    }
}
