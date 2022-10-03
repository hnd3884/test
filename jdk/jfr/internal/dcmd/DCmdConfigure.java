package jdk.jfr.internal.dcmd;

import jdk.jfr.internal.Options;
import jdk.jfr.internal.SecuritySupport;
import jdk.jfr.internal.Repository;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;

final class DCmdConfigure extends AbstractDCmd
{
    public String execute(final String s, final String s2, final Integer stackDepth, final Long n, final Long n2, final Long n3, final Long n4, final Long n5, final Boolean sampleThreads) throws DCmdException {
        if (Logger.shouldLog(LogTag.JFR_DCMD, LogLevel.DEBUG)) {
            Logger.log(LogTag.JFR_DCMD, LogLevel.DEBUG, "Executing DCmdConfigure: repositorypath=" + s + ", dumppath=" + s2 + ", stackdepth=" + stackDepth + ", globalbuffercount=" + n + ", globalbuffersize=" + n2 + ", thread_buffer_size" + n3 + ", memorysize" + n4 + ", maxchunksize=" + n5 + ", samplethreads" + sampleThreads);
        }
        boolean b = false;
        if (s != null) {
            try {
                Repository.getRepository().setBasePath(new SecuritySupport.SafePath(s));
                Logger.log(LogTag.JFR, LogLevel.INFO, "Base repository path set to " + s);
            }
            catch (final Exception ex) {
                throw new DCmdException("Could not use " + s + " as repository. " + ex.getMessage(), new Object[] { ex });
            }
            this.printRepositoryPath();
            b = true;
        }
        if (s2 != null) {
            Options.setDumpPath(new SecuritySupport.SafePath(s2));
            Logger.log(LogTag.JFR, LogLevel.INFO, "Emergency dump path set to " + s2);
            this.printDumpPath();
            b = true;
        }
        if (stackDepth != null) {
            Options.setStackDepth(stackDepth);
            Logger.log(LogTag.JFR, LogLevel.INFO, "Stack depth set to " + stackDepth);
            this.printStackDepth();
            b = true;
        }
        if (n != null) {
            Options.setGlobalBufferCount(n);
            Logger.log(LogTag.JFR, LogLevel.INFO, "Global buffer count set to " + n);
            this.printGlobalBufferCount();
            b = true;
        }
        if (n2 != null) {
            Options.setGlobalBufferSize(n2);
            Logger.log(LogTag.JFR, LogLevel.INFO, "Global buffer size set to " + n2);
            this.printGlobalBufferSize();
            b = true;
        }
        if (n3 != null) {
            Options.setThreadBufferSize(n3);
            Logger.log(LogTag.JFR, LogLevel.INFO, "Thread buffer size set to " + n3);
            this.printThreadBufferSize();
            b = true;
        }
        if (n4 != null) {
            Options.setMemorySize(n4);
            Logger.log(LogTag.JFR, LogLevel.INFO, "Memory size set to " + n4);
            this.printMemorySize();
            b = true;
        }
        if (n5 != null) {
            Options.setMaxChunkSize(n5);
            Logger.log(LogTag.JFR, LogLevel.INFO, "Max chunk size set to " + n5);
            this.printMaxChunkSize();
            b = true;
        }
        if (sampleThreads != null) {
            Options.setSampleThreads(sampleThreads);
            Logger.log(LogTag.JFR, LogLevel.INFO, "Sample threads set to " + sampleThreads);
            this.printSampleThreads();
            b = true;
        }
        if (!b) {
            this.println("Current configuration:", new Object[0]);
            this.println();
            this.printRepositoryPath();
            this.printStackDepth();
            this.printGlobalBufferCount();
            this.printGlobalBufferSize();
            this.printThreadBufferSize();
            this.printMemorySize();
            this.printMaxChunkSize();
            this.printSampleThreads();
        }
        return this.getResult();
    }
    
    private void printRepositoryPath() {
        this.print("Repository path: ");
        this.printPath(Repository.getRepository().getRepositoryPath());
        this.println();
    }
    
    private void printDumpPath() {
        this.print("Dump path: ");
        this.printPath(Options.getDumpPath());
        this.println();
    }
    
    private void printSampleThreads() {
        this.println("Sample threads: " + Options.getSampleThreads(), new Object[0]);
    }
    
    private void printStackDepth() {
        this.println("Stack depth: " + Options.getStackDepth(), new Object[0]);
    }
    
    private void printGlobalBufferCount() {
        this.println("Global buffer count: " + Options.getGlobalBufferCount(), new Object[0]);
    }
    
    private void printGlobalBufferSize() {
        this.print("Global buffer size: ");
        this.printBytes(Options.getGlobalBufferSize());
        this.println();
    }
    
    private void printThreadBufferSize() {
        this.print("Thread buffer size: ");
        this.printBytes(Options.getThreadBufferSize());
        this.println();
    }
    
    private void printMemorySize() {
        this.print("Memory size: ");
        this.printBytes(Options.getMemorySize());
        this.println();
    }
    
    private void printMaxChunkSize() {
        this.print("Max chunk size: ");
        this.printBytes(Options.getMaxChunkSize());
        this.println();
    }
}
