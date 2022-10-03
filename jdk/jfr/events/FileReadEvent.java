package jdk.jfr.events;

import jdk.jfr.DataAmount;
import jdk.jfr.Description;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("jdk.FileRead")
@Label("File Read")
@Category({ "Java Application" })
@Description("Reading data from a file")
public final class FileReadEvent extends AbstractJDKEvent
{
    public static final ThreadLocal<FileReadEvent> EVENT;
    @Label("Path")
    @Description("Full path of the file")
    public String path;
    @Label("Bytes Read")
    @Description("Number of bytes read from the file (possibly 0)")
    @DataAmount
    public long bytesRead;
    @Label("End of File")
    @Description("If end of file was reached")
    public boolean endOfFile;
    
    public void reset() {
        this.path = null;
        this.endOfFile = false;
        this.bytesRead = 0L;
    }
    
    static {
        EVENT = new ThreadLocal<FileReadEvent>() {
            @Override
            protected FileReadEvent initialValue() {
                return new FileReadEvent();
            }
        };
    }
}
