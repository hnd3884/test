package jdk.jfr.internal.dcmd;

import jdk.jfr.internal.SecuritySupport;
import java.nio.file.InvalidPathException;
import java.io.IOException;
import java.nio.file.Paths;
import jdk.jfr.Recording;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;

final class DCmdStop extends AbstractDCmd
{
    public String execute(final String s, final String s2) throws DCmdException {
        if (Logger.shouldLog(LogTag.JFR_DCMD, LogLevel.DEBUG)) {
            Logger.log(LogTag.JFR_DCMD, LogLevel.DEBUG, "Executing DCmdStart: name=" + s + ", filename=" + s2);
        }
        try {
            SecuritySupport.SafePath resolvePath = null;
            final Recording recording = this.findRecording(s);
            if (s2 != null) {
                try {
                    resolvePath = this.resolvePath(null, s2);
                    recording.setDestination(Paths.get(s2, new String[0]));
                }
                catch (final IOException | InvalidPathException ex) {
                    throw new DCmdException("Failed to stop %s. Could not set destination for \"%s\" to file %s", new Object[] { recording.getName(), s2, ((Throwable)ex).getMessage() });
                }
            }
            recording.stop();
            this.reportOperationComplete("Stopped", recording.getName(), resolvePath);
            recording.close();
            return this.getResult();
        }
        catch (final InvalidPathException | DCmdException ex2) {
            if (s2 != null) {
                throw new DCmdException("Could not write recording \"%s\" to file. %s", new Object[] { s, ((Throwable)ex2).getMessage() });
            }
            throw new DCmdException((Throwable)ex2, "Could not stop recording \"%s\".", new Object[] { s, ((Throwable)ex2).getMessage() });
        }
    }
}
