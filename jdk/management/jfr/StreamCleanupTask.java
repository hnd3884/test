package jdk.management.jfr;

import java.util.TimerTask;

final class StreamCleanupTask extends TimerTask
{
    private final Stream stream;
    private final StreamManager manager;
    
    StreamCleanupTask(final StreamManager manager, final Stream stream) {
        this.stream = stream;
        this.manager = manager;
    }
    
    @Override
    public void run() {
        final long lastTouched = this.stream.getLastTouched();
        if (System.currentTimeMillis() - lastTouched >= StreamManager.TIME_OUT) {
            this.manager.destroy(this.stream);
        }
        else {
            this.manager.scheduleAbort(this.stream, lastTouched + StreamManager.TIME_OUT);
        }
    }
}
