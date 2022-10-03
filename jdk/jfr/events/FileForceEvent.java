package jdk.jfr.events;

import jdk.jfr.Description;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("jdk.FileForce")
@Label("File Force")
@Category({ "Java Application" })
@Description("Force updates to be written to file")
public final class FileForceEvent extends AbstractJDKEvent
{
    public static final ThreadLocal<FileForceEvent> EVENT;
    @Label("Path")
    @Description("Full path of the file")
    public String path;
    @Label("Update Metadata")
    @Description("Whether the file metadata is updated")
    public boolean metaData;
    
    public void reset() {
        this.path = null;
        this.metaData = false;
    }
    
    static {
        EVENT = new ThreadLocal<FileForceEvent>() {
            @Override
            protected FileForceEvent initialValue() {
                return new FileForceEvent();
            }
        };
    }
}
