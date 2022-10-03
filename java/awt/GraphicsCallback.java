package java.awt;

import java.awt.peer.LightweightPeer;
import sun.awt.SunGraphicsCallback;

abstract class GraphicsCallback extends SunGraphicsCallback
{
    static final class PaintCallback extends GraphicsCallback
    {
        private static PaintCallback instance;
        
        private PaintCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            component.paint(graphics);
        }
        
        static PaintCallback getInstance() {
            return PaintCallback.instance;
        }
        
        static {
            PaintCallback.instance = new PaintCallback();
        }
    }
    
    static final class PrintCallback extends GraphicsCallback
    {
        private static PrintCallback instance;
        
        private PrintCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            component.print(graphics);
        }
        
        static PrintCallback getInstance() {
            return PrintCallback.instance;
        }
        
        static {
            PrintCallback.instance = new PrintCallback();
        }
    }
    
    static final class PaintAllCallback extends GraphicsCallback
    {
        private static PaintAllCallback instance;
        
        private PaintAllCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            component.paintAll(graphics);
        }
        
        static PaintAllCallback getInstance() {
            return PaintAllCallback.instance;
        }
        
        static {
            PaintAllCallback.instance = new PaintAllCallback();
        }
    }
    
    static final class PrintAllCallback extends GraphicsCallback
    {
        private static PrintAllCallback instance;
        
        private PrintAllCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            component.printAll(graphics);
        }
        
        static PrintAllCallback getInstance() {
            return PrintAllCallback.instance;
        }
        
        static {
            PrintAllCallback.instance = new PrintAllCallback();
        }
    }
    
    static final class PeerPaintCallback extends GraphicsCallback
    {
        private static PeerPaintCallback instance;
        
        private PeerPaintCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            component.validate();
            if (component.peer instanceof LightweightPeer) {
                component.lightweightPaint(graphics);
            }
            else {
                component.peer.paint(graphics);
            }
        }
        
        static PeerPaintCallback getInstance() {
            return PeerPaintCallback.instance;
        }
        
        static {
            PeerPaintCallback.instance = new PeerPaintCallback();
        }
    }
    
    static final class PeerPrintCallback extends GraphicsCallback
    {
        private static PeerPrintCallback instance;
        
        private PeerPrintCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            component.validate();
            if (component.peer instanceof LightweightPeer) {
                component.lightweightPrint(graphics);
            }
            else {
                component.peer.print(graphics);
            }
        }
        
        static PeerPrintCallback getInstance() {
            return PeerPrintCallback.instance;
        }
        
        static {
            PeerPrintCallback.instance = new PeerPrintCallback();
        }
    }
    
    static final class PaintHeavyweightComponentsCallback extends GraphicsCallback
    {
        private static PaintHeavyweightComponentsCallback instance;
        
        private PaintHeavyweightComponentsCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            if (component.peer instanceof LightweightPeer) {
                component.paintHeavyweightComponents(graphics);
            }
            else {
                component.paintAll(graphics);
            }
        }
        
        static PaintHeavyweightComponentsCallback getInstance() {
            return PaintHeavyweightComponentsCallback.instance;
        }
        
        static {
            PaintHeavyweightComponentsCallback.instance = new PaintHeavyweightComponentsCallback();
        }
    }
    
    static final class PrintHeavyweightComponentsCallback extends GraphicsCallback
    {
        private static PrintHeavyweightComponentsCallback instance;
        
        private PrintHeavyweightComponentsCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            if (component.peer instanceof LightweightPeer) {
                component.printHeavyweightComponents(graphics);
            }
            else {
                component.printAll(graphics);
            }
        }
        
        static PrintHeavyweightComponentsCallback getInstance() {
            return PrintHeavyweightComponentsCallback.instance;
        }
        
        static {
            PrintHeavyweightComponentsCallback.instance = new PrintHeavyweightComponentsCallback();
        }
    }
}
