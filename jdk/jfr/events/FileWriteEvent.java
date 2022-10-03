package jdk.jfr.events;

import jdk.jfr.DataAmount;
import jdk.jfr.Description;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("jdk.FileWrite")
@Label("File Write")
@Category({ "Java Application" })
@Description("Writing data to a file")
public final class FileWriteEvent extends AbstractJDKEvent
{
    public static final ThreadLocal<FileWriteEvent> EVENT;
    @Label("Path")
    @Description("Full path of the file")
    public String path;
    @Label("Bytes Written")
    @Description("Number of bytes written to the file")
    @DataAmount
    public long bytesWritten;
    
    public void reset() {
        this.path = null;
        this.bytesWritten = 0L;
    }
    
    static {
        EVENT = new ThreadLocal<FileWriteEvent>() {
            @Override
            protected FileWriteEvent initialValue() {
                return new FileWriteEvent();
            }
        };
    }
}
