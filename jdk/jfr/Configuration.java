package jdk.jfr;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;
import jdk.jfr.internal.jfc.JFC;
import java.nio.file.Files;
import jdk.jfr.internal.JVMSupport;
import java.util.Objects;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import jdk.Exported;

@Exported
public final class Configuration
{
    private final Map<String, String> settings;
    private final String label;
    private final String description;
    private final String provider;
    private final String contents;
    private final String name;
    
    Configuration(final String name, final String label, final String description, final String provider, final Map<String, String> settings, final String contents) {
        this.name = name;
        this.label = label;
        this.description = description;
        this.provider = provider;
        this.settings = settings;
        this.contents = contents;
    }
    
    public Map<String, String> getSettings() {
        return new LinkedHashMap<String, String>(this.settings);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getProvider() {
        return this.provider;
    }
    
    public String getContents() {
        return this.contents;
    }
    
    public static Configuration create(final Path path) throws IOException, ParseException {
        Objects.requireNonNull(path);
        JVMSupport.ensureWithIOException();
        try (final BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            return JFC.create(JFC.nameFromPath(path), bufferedReader);
        }
    }
    
    public static Configuration create(final Reader reader) throws IOException, ParseException {
        Objects.requireNonNull(reader);
        JVMSupport.ensureWithIOException();
        return JFC.create(null, reader);
    }
    
    public static Configuration getConfiguration(final String s) throws IOException, ParseException {
        JVMSupport.ensureWithIOException();
        return JFC.getPredefined(s);
    }
    
    public static List<Configuration> getConfigurations() {
        if (JVMSupport.isNotAvailable()) {
            return new ArrayList<Configuration>();
        }
        return Collections.unmodifiableList((List<? extends Configuration>)JFC.getConfigurations());
    }
}
