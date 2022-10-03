package com.adventnet.iam.parser;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import com.adventnet.iam.security.SecurityUtil;
import java.util.LinkedList;
import com.adventnet.iam.security.VcardProperty;
import java.util.List;
import java.util.logging.Logger;

public class Parser
{
    private static final Logger LOGGER;
    
    public static List<VcardProperty> parseVcard(final String parameterValue, final String lineDelimiter, final String keyValueDelimiter, final int splitLimit) {
        final List<VcardProperty> vcardArrayPropertyList = new LinkedList<VcardProperty>();
        String keyName = null;
        String value = null;
        VcardProperty vcardProperty = null;
        final String[] split;
        final String[] lines = split = parameterValue.split(lineDelimiter);
        for (String line : split) {
            line = line.trim();
            if (!line.isEmpty()) {
                if (line.contains(keyValueDelimiter)) {
                    final String[] results = line.split(keyValueDelimiter, splitLimit);
                    keyName = results[0].trim();
                    value = (SecurityUtil.isValid(results[1]) ? results[1].trim() : " ");
                    vcardProperty = new VcardProperty(keyName, value);
                    if (!SecurityUtil.isValid(keyName)) {
                        Parser.LOGGER.log(Level.SEVERE, "Invalid key/value is not allowed key=\"{0}\" in the vcardObject \"{1}\" ", new Object[] { keyName, parameterValue });
                        throw new IAMSecurityException("VCARD/VCARDARRAY PARSE ERROR");
                    }
                    vcardArrayPropertyList.add(vcardProperty);
                }
                else {
                    if (!SecurityUtil.isValid(keyName) || "BEGIN".equals(keyName) || "END".equals(keyName)) {
                        Parser.LOGGER.log(Level.SEVERE, " Valid Vcard Object/Array must start with the BEGIN:VCARD and end with END:VCARD \n");
                        throw new IAMSecurityException("VCARD/VCARDARRAY PARSE ERROR");
                    }
                    vcardProperty.setValue(vcardProperty.getValue() + line);
                }
            }
        }
        return vcardArrayPropertyList;
    }
    
    public static Properties parseProperties(final String parameterValue) throws IOException {
        final Properties parsedProperties = new Properties();
        parsedProperties.load(new StringReader(parameterValue));
        return parsedProperties;
    }
    
    public static CSVParser parseCSV(final String parameterValue, final CSVFormat csvFormat) throws IOException {
        final Reader reader = new StringReader(parameterValue);
        final CSVParser csvParser = new CSVParser(reader, csvFormat);
        return csvParser;
    }
    
    static {
        LOGGER = Logger.getLogger(Parser.class.getName());
    }
}
