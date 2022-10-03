package com.me.devicemanagement.onpremise.server.extensions.processbuilder;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;
import java.util.List;

public class DMProcessBuilder
{
    private List<String> command;
    private File directory;
    private Map<String, String> environment;
    private boolean redirectErrorStream;
    private Redirect[] redirects;
    
    public DMProcessBuilder(final List<String> command) {
        if (command == null) {
            throw new NullPointerException();
        }
        this.command = command;
    }
    
    public DMProcessBuilder(final String... command) {
        this.command = new ArrayList<String>(command.length);
        for (final String arg : command) {
            this.command.add(arg);
        }
    }
    
    public DMProcessBuilder command(final List<String> command) {
        if (command == null) {
            throw new NullPointerException();
        }
        this.command = command;
        return this;
    }
    
    public DMProcessBuilder command(final String... command) {
        this.command = new ArrayList<String>(command.length);
        for (final String arg : command) {
            this.command.add(arg);
        }
        return this;
    }
    
    public List<String> command() {
        return this.command;
    }
    
    public Map<String, String> environment() {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(new RuntimePermission("getenv.*"));
        }
        if (this.environment == null) {
            this.environment = DMProcessEnvironment.environment();
        }
        assert this.environment != null;
        return this.environment;
    }
    
    DMProcessBuilder environment(final String[] envp) {
        assert this.environment == null;
        if (envp != null) {
            this.environment = DMProcessEnvironment.emptyEnvironment(envp.length);
            assert this.environment != null;
            for (String envstring : envp) {
                if (envstring.indexOf(0) != -1) {
                    envstring = envstring.replaceFirst("\u0000.*", "");
                }
                final int eqlsign = envstring.indexOf(61, 1);
                if (eqlsign != -1) {
                    this.environment.put(envstring.substring(0, eqlsign), envstring.substring(eqlsign + 1));
                }
            }
        }
        return this;
    }
    
    public File directory() {
        return this.directory;
    }
    
    public DMProcessBuilder directory(final File directory) {
        this.directory = directory;
        return this;
    }
    
    private Redirect[] redirects() {
        if (this.redirects == null) {
            this.redirects = new Redirect[] { Redirect.PIPE, Redirect.PIPE, Redirect.PIPE };
        }
        return this.redirects;
    }
    
    public DMProcessBuilder redirectInput(final Redirect source) {
        if (source.type() == Redirect.Type.WRITE || source.type() == Redirect.Type.APPEND) {
            throw new IllegalArgumentException("Redirect invalid for reading: " + source);
        }
        this.redirects()[0] = source;
        return this;
    }
    
    public DMProcessBuilder redirectOutput(final Redirect destination) {
        if (destination.type() == Redirect.Type.READ) {
            throw new IllegalArgumentException("Redirect invalid for writing: " + destination);
        }
        this.redirects()[1] = destination;
        return this;
    }
    
    public DMProcessBuilder redirectError(final Redirect destination) {
        if (destination.type() == Redirect.Type.READ) {
            throw new IllegalArgumentException("Redirect invalid for writing: " + destination);
        }
        this.redirects()[2] = destination;
        return this;
    }
    
    public DMProcessBuilder redirectInput(final File file) {
        return this.redirectInput(Redirect.from(file));
    }
    
    public DMProcessBuilder redirectOutput(final File file) {
        return this.redirectOutput(Redirect.to(file));
    }
    
    public DMProcessBuilder redirectError(final File file) {
        return this.redirectError(Redirect.to(file));
    }
    
    public Redirect redirectInput() {
        return (this.redirects == null) ? Redirect.PIPE : this.redirects[0];
    }
    
    public Redirect redirectOutput() {
        return (this.redirects == null) ? Redirect.PIPE : this.redirects[1];
    }
    
    public Redirect redirectError() {
        return (this.redirects == null) ? Redirect.PIPE : this.redirects[2];
    }
    
    public DMProcessBuilder inheritIO() {
        Arrays.fill(this.redirects(), Redirect.INHERIT);
        return this;
    }
    
    public boolean redirectErrorStream() {
        return this.redirectErrorStream;
    }
    
    public DMProcessBuilder redirectErrorStream(final boolean redirectErrorStream) {
        this.redirectErrorStream = redirectErrorStream;
        return this;
    }
    
    public Process start() throws IOException {
        String[] cmdarray = this.command.toArray(new String[this.command.size()]);
        final String[] array;
        cmdarray = (array = cmdarray.clone());
        for (final String arg : array) {
            if (arg == null) {
                throw new NullPointerException();
            }
        }
        final String prog = cmdarray[0];
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkExec(prog);
        }
        final String dir = (this.directory == null) ? null : this.directory.toString();
        for (int i = 1; i < cmdarray.length; ++i) {
            if (cmdarray[i].indexOf(0) >= 0) {
                throw new IOException("invalid null character in command");
            }
        }
        try {
            return DMProcessImpl.start(cmdarray, this.environment, dir, this.redirects, this.redirectErrorStream);
        }
        catch (final IOException | IllegalArgumentException e) {
            String exceptionInfo = ": " + e.getMessage();
            Throwable cause = e;
            if (e instanceof IOException && security != null) {
                try {
                    security.checkRead(prog);
                }
                catch (final SecurityException se) {
                    exceptionInfo = "";
                    cause = se;
                }
            }
            throw new IOException("Cannot run program \"" + prog + "\"" + ((dir == null) ? "" : (" (in directory \"" + dir + "\")")) + exceptionInfo, cause);
        }
    }
    
    static class NullInputStream extends InputStream
    {
        static final NullInputStream INSTANCE;
        
        private NullInputStream() {
        }
        
        @Override
        public int read() {
            return -1;
        }
        
        @Override
        public int available() {
            return 0;
        }
        
        static {
            INSTANCE = new NullInputStream();
        }
    }
    
    static class NullOutputStream extends OutputStream
    {
        static final NullOutputStream INSTANCE;
        
        private NullOutputStream() {
        }
        
        @Override
        public void write(final int b) throws IOException {
            throw new IOException("Stream closed");
        }
        
        static {
            INSTANCE = new NullOutputStream();
        }
    }
    
    public abstract static class Redirect
    {
        public static final Redirect PIPE;
        public static final Redirect INHERIT;
        
        public abstract Type type();
        
        public File file() {
            return null;
        }
        
        boolean append() {
            throw new UnsupportedOperationException();
        }
        
        public static Redirect from(final File file) {
            if (file == null) {
                throw new NullPointerException();
            }
            return new Redirect() {
                @Override
                public Type type() {
                    return Type.READ;
                }
                
                @Override
                public File file() {
                    return file;
                }
                
                @Override
                public String toString() {
                    return "redirect to read from file \"" + file + "\"";
                }
            };
        }
        
        public static Redirect to(final File file) {
            if (file == null) {
                throw new NullPointerException();
            }
            return new Redirect() {
                @Override
                public Type type() {
                    return Type.WRITE;
                }
                
                @Override
                public File file() {
                    return file;
                }
                
                @Override
                public String toString() {
                    return "redirect to write to file \"" + file + "\"";
                }
                
                @Override
                boolean append() {
                    return false;
                }
            };
        }
        
        public static Redirect appendTo(final File file) {
            if (file == null) {
                throw new NullPointerException();
            }
            return new Redirect() {
                @Override
                public Type type() {
                    return Type.APPEND;
                }
                
                @Override
                public File file() {
                    return file;
                }
                
                @Override
                public String toString() {
                    return "redirect to append to file \"" + file + "\"";
                }
                
                @Override
                boolean append() {
                    return true;
                }
            };
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Redirect)) {
                return false;
            }
            final Redirect r = (Redirect)obj;
            if (r.type() != this.type()) {
                return false;
            }
            assert this.file() != null;
            return this.file().equals(r.file());
        }
        
        @Override
        public int hashCode() {
            final File file = this.file();
            if (file == null) {
                return super.hashCode();
            }
            return file.hashCode();
        }
        
        private Redirect() {
        }
        
        static {
            PIPE = new Redirect() {
                @Override
                public Type type() {
                    return Type.PIPE;
                }
                
                @Override
                public String toString() {
                    return this.type().toString();
                }
            };
            INHERIT = new Redirect() {
                @Override
                public Type type() {
                    return Type.INHERIT;
                }
                
                @Override
                public String toString() {
                    return this.type().toString();
                }
            };
        }
        
        public enum Type
        {
            PIPE, 
            INHERIT, 
            READ, 
            WRITE, 
            APPEND;
        }
    }
}
