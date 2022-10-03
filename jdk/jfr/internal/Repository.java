package jdk.jfr.internal;

import java.util.Iterator;
import java.time.temporal.TemporalAccessor;
import java.time.LocalDateTime;
import java.time.Instant;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.time.format.DateTimeFormatter;

public final class Repository
{
    private static final int MAX_REPO_CREATION_RETRIES = 1000;
    private static final JVM jvm;
    private static final Repository instance;
    public static final DateTimeFormatter REPO_DATE_FORMAT;
    private final Set<SecuritySupport.SafePath> cleanupDirectories;
    private SecuritySupport.SafePath baseLocation;
    private SecuritySupport.SafePath repository;
    
    private Repository() {
        this.cleanupDirectories = new HashSet<SecuritySupport.SafePath>();
    }
    
    public static Repository getRepository() {
        return Repository.instance;
    }
    
    public synchronized void setBasePath(final SecuritySupport.SafePath baseLocation) throws Exception {
        this.repository = createRepository(baseLocation);
        try {
            SecuritySupport.delete(this.repository);
        }
        catch (final IOException ex) {
            Logger.log(LogTag.JFR, LogLevel.INFO, "Could not delete disk repository " + this.repository);
        }
        this.baseLocation = baseLocation;
    }
    
    synchronized void ensureRepository() throws Exception {
        if (this.baseLocation == null) {
            this.setBasePath(SecuritySupport.JAVA_IO_TMPDIR);
        }
    }
    
    synchronized RepositoryChunk newChunk(final Instant instant) {
        try {
            if (!SecuritySupport.existDirectory(this.repository)) {
                this.repository = createRepository(this.baseLocation);
                Repository.jvm.setRepositoryLocation(this.repository.toString());
                this.cleanupDirectories.add(this.repository);
            }
            return new RepositoryChunk(this.repository, instant);
        }
        catch (final Exception ex) {
            final String format = String.format("Could not create chunk in repository %s, %s", this.repository, ex.getMessage());
            Logger.log(LogTag.JFR, LogLevel.ERROR, format);
            Repository.jvm.abort(format);
            throw new InternalError("Could not abort after JFR disk creation error");
        }
    }
    
    private static SecuritySupport.SafePath createRepository(final SecuritySupport.SafePath safePath) throws Exception {
        final SecuritySupport.SafePath realBasePath = createRealBasePath(safePath);
        SecuritySupport.SafePath safePath2 = null;
        String s = Repository.REPO_DATE_FORMAT.format(LocalDateTime.now()) + "_" + JVM.getJVM().getPid();
        int i;
        for (i = 0; i < 1000; ++i) {
            safePath2 = new SecuritySupport.SafePath(realBasePath.toPath().resolve(s));
            if (tryToUseAsRepository(safePath2)) {
                break;
            }
            s = s + "_" + i;
        }
        if (i == 1000) {
            throw new Exception("Unable to create JFR repository directory using base location (" + safePath + ")");
        }
        return SecuritySupport.toRealPath(safePath2);
    }
    
    private static SecuritySupport.SafePath createRealBasePath(final SecuritySupport.SafePath safePath) throws Exception {
        if (!SecuritySupport.exists(safePath)) {
            return SecuritySupport.toRealPath(SecuritySupport.createDirectories(safePath));
        }
        if (!SecuritySupport.isWritable(safePath)) {
            throw new IOException("JFR repository directory (" + safePath.toString() + ") exists, but isn't writable");
        }
        return SecuritySupport.toRealPath(safePath);
    }
    
    private static boolean tryToUseAsRepository(final SecuritySupport.SafePath safePath) {
        if (safePath.toPath().getParent() == null) {
            return false;
        }
        try {
            try {
                SecuritySupport.createDirectories(safePath);
            }
            catch (final Exception ex) {}
            return SecuritySupport.exists(safePath) && SecuritySupport.isDirectory(safePath);
        }
        catch (final IOException ex2) {
            return false;
        }
    }
    
    synchronized void clear() {
        for (final SecuritySupport.SafePath safePath : this.cleanupDirectories) {
            try {
                SecuritySupport.clearDirectory(safePath);
                Logger.log(LogTag.JFR, LogLevel.INFO, "Removed repository " + safePath);
            }
            catch (final IOException ex) {
                Logger.log(LogTag.JFR, LogLevel.ERROR, "Repository " + safePath + " could not be removed at shutdown: " + ex.getMessage());
            }
        }
    }
    
    public synchronized SecuritySupport.SafePath getRepositoryPath() {
        return this.repository;
    }
    
    static {
        jvm = JVM.getJVM();
        instance = new Repository();
        REPO_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    }
}
