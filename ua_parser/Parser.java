package ua_parser;

import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import java.io.InputStream;
import java.io.IOException;

public class Parser
{
    private static final String REGEX_YAML_PATH = "/ua_parser/regexes.yaml";
    private UserAgentParser uaParser;
    private OSParser osParser;
    private DeviceParser deviceParser;
    
    public Parser() {
        try (final InputStream is = Parser.class.getResourceAsStream("/ua_parser/regexes.yaml")) {
            this.initialize(is);
        }
        catch (final IOException e) {
            throw new RuntimeException("failed to initialize parser from regexes.yaml bundled in jar", e);
        }
    }
    
    public Parser(final InputStream regexYaml) {
        this.initialize(regexYaml);
    }
    
    public Client parse(final String agentString) {
        final UserAgent ua = this.parseUserAgent(agentString);
        final OS os = this.parseOS(agentString);
        final Device device = this.deviceParser.parse(agentString);
        return new Client(ua, os, device);
    }
    
    public UserAgent parseUserAgent(final String agentString) {
        return this.uaParser.parse(agentString);
    }
    
    public Device parseDevice(final String agentString) {
        return this.deviceParser.parse(agentString);
    }
    
    public OS parseOS(final String agentString) {
        return this.osParser.parse(agentString);
    }
    
    private void initialize(final InputStream regexYaml) {
        final Yaml yaml = new Yaml((BaseConstructor)new SafeConstructor());
        final Map<String, List<Map<String, String>>> regexConfig = (Map<String, List<Map<String, String>>>)yaml.load(regexYaml);
        final List<Map<String, String>> uaParserConfigs = regexConfig.get("user_agent_parsers");
        if (uaParserConfigs == null) {
            throw new IllegalArgumentException("user_agent_parsers is missing from yaml");
        }
        this.uaParser = UserAgentParser.fromList(uaParserConfigs);
        final List<Map<String, String>> osParserConfigs = regexConfig.get("os_parsers");
        if (osParserConfigs == null) {
            throw new IllegalArgumentException("os_parsers is missing from yaml");
        }
        this.osParser = OSParser.fromList(osParserConfigs);
        final List<Map<String, String>> deviceParserConfigs = regexConfig.get("device_parsers");
        if (deviceParserConfigs == null) {
            throw new IllegalArgumentException("device_parsers is missing from yaml");
        }
        this.deviceParser = DeviceParser.fromList(deviceParserConfigs);
    }
}
