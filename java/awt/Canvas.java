package java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.awt.image.BufferStrategy;
import java.awt.peer.CanvasPeer;
import javax.accessibility.Accessible;

public class Canvas extends Component implements Accessible
{
    private static final String base = "canvas";
    private static int nameCounter;
    private static final long serialVersionUID = -2284879212465893870L;
    
    public Canvas() {
    }
    
    public Canvas(final GraphicsConfiguration graphicsConfiguration) {
        this();
        this.setGraphicsConfiguration(graphicsConfiguration);
    }
    
    @Override
    void setGraphicsConfiguration(GraphicsConfiguration appropriateGraphicsConfiguration) {
        synchronized (this.getTreeLock()) {
            final CanvasPeer canvasPeer = (CanvasPeer)this.getPeer();
            if (canvasPeer != null) {
                appropriateGraphicsConfiguration = canvasPeer.getAppropriateGraphicsConfiguration(appropriateGraphicsConfiguration);
            }
            super.setGraphicsConfiguration(appropriateGraphicsConfiguration);
        }
    }
    
    @Override
    String constructComponentName() {
        synchronized (Canvas.class) {
            return "canvas" + Canvas.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createCanvas(this);
            }
            super.addNotify();
        }
    }
    
    @Override
    public void paint(final Graphics graphics) {
        graphics.clearRect(0, 0, this.width, this.height);
    }
    
    @Override
    public void update(final Graphics graphics) {
        graphics.clearRect(0, 0, this.width, this.height);
        this.paint(graphics);
    }
    
    @Override
    boolean postsOldMouseEvents() {
        return true;
    }
    
    public void createBufferStrategy(final int n) {
        super.createBufferStrategy(n);
    }
    
    public void createBufferStrategy(final int n, final BufferCapabilities bufferCapabilities) throws AWTException {
        super.createBufferStrategy(n, bufferCapabilities);
    }
    
    public BufferStrategy getBufferStrategy() {
        return super.getBufferStrategy();
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTCanvas();
        }
        return this.accessibleContext;
    }
    
    static {
        Canvas.nameCounter = 0;
    }
    
    protected class AccessibleAWTCanvas extends AccessibleAWTComponent
    {
        private static final long serialVersionUID = -6325592262103146699L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CANVAS;
        }
    }
}
