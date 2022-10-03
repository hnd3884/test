package eu.medsea.mimeutil.detector;

import java.io.File;
import java.util.TimerTask;

abstract class FileWatcher extends TimerTask
{
    private long timeStamp;
    private File file;
    
    public FileWatcher(final File file) {
        this.file = file;
        this.timeStamp = file.lastModified();
    }
    
    @Override
    public final void run() {
        final long timeStamp = this.file.lastModified();
        if (this.timeStamp != timeStamp) {
            this.timeStamp = timeStamp;
            this.onChange(this.file);
        }
    }
    
    protected abstract void onChange(final File p0);
}
