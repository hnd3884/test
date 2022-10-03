package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.config.TikaConfig;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class AutoDetectParserFactory extends ParserFactory
{
    public static final String TIKA_CONFIG_PATH = "tika_config_path";
    
    public AutoDetectParserFactory(final Map<String, String> args) {
        super(args);
    }
    
    @Override
    public Parser build() throws IOException, SAXException, TikaException {
        final String tikaConfigPath = this.args.remove("tika_config_path");
        TikaConfig tikaConfig = null;
        if (tikaConfigPath != null) {
            if (Files.isReadable(Paths.get(tikaConfigPath, new String[0]))) {
                tikaConfig = new TikaConfig(Paths.get(tikaConfigPath, new String[0]));
            }
            else if (this.getClass().getResource(tikaConfigPath) != null) {
                try (final InputStream is = this.getClass().getResourceAsStream(tikaConfigPath)) {
                    tikaConfig = new TikaConfig(is);
                }
            }
        }
        if (tikaConfig == null) {
            tikaConfig = TikaConfig.getDefaultConfig();
        }
        return new AutoDetectParser(tikaConfig);
    }
}
