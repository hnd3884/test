package sun.awt.windows;

import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import sun.awt.Mutex;

final class WToolkitThreadBlockedHandler extends Mutex implements ToolkitThreadBlockedHandler
{
    @Override
    public void enter() {
        if (!this.isOwned()) {
            throw new IllegalMonitorStateException();
        }
        this.unlock();
        this.startSecondaryEventLoop();
        this.lock();
    }
    
    @Override
    public void exit() {
        if (!this.isOwned()) {
            throw new IllegalMonitorStateException();
        }
        WToolkit.quitSecondaryEventLoop();
    }
    
    private native void startSecondaryEventLoop();
}
