package com.adventnet.iam.security;

import java.io.IOException;
import org.apache.commons.csv.CSVParser;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import com.adventnet.iam.parser.Parser;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.ArrayList;
import org.apache.commons.csv.CSVFormat;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.List;
import org.apache.commons.csv.CSVRecord;
import java.util.logging.Logger;

public class CsvValidator extends DataFormatValidator
{
    private static final Logger LOGGER;
    private CSVRecord csvRecord;
    public static final List<String> CSV_SUPPORTED_FORMATS;
    private static final Pattern CSV_EMPTY_ALLOWED_CHECK_PATTERN;
    private List<String> csv_Header_List;
    private List<String> csv_Header_List_without_double_quote;
    private List<LinkedList<String>> validCSVRecords;
    private CSVFormat csvFormat;
    private String recordSeperator;
    private String delimiter;
    private List<String> csvParsedKeySet;
    private static final char COMMENT = '#';
    
    public CsvValidator() {
        this.csvRecord = null;
        this.csv_Header_List = null;
        this.csv_Header_List_without_double_quote = null;
        this.validCSVRecords = null;
        this.csvFormat = null;
        this.recordSeperator = null;
        this.delimiter = null;
        this.csvParsedKeySet = null;
    }
    
    CsvValidator parseAndValidateCSVFormat(final String paramName, final String parameterValue, final TemplateRule templateRule, final String csvFormatFromConf, final boolean allowEmptyValue) throws IOException {
        this.csv_Header_List = new ArrayList<String>();
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        this.validCSVRecords = new LinkedList<LinkedList<String>>();
        this.csvFormat = CSVFormat.valueOf(csvFormatFromConf);
        final char delimiterChar = this.csvFormat.getDelimiter();
        this.csvFormat = this.csvFormat.withCommentMarker('#');
        this.recordSeperator = (parameterValue.contains(this.csvFormat.getRecordSeparator()) ? this.csvFormat.getRecordSeparator() : "\n");
        this.delimiter = String.valueOf(delimiterChar);
        final String csv_Header_String = parameterValue.trim().split(this.recordSeperator, 2)[0];
        if (csv_Header_String.indexOf(delimiterChar) == -1) {
            CsvValidator.LOGGER.log(Level.SEVERE, "CSV content is not matching as per the mentioned Format : {0} , kindly provide the proper format ", new Object[] { csvFormatFromConf });
            throw new IAMSecurityException("CSV PARSE ERROR");
        }
        this.csv_Header_List = Arrays.asList(csv_Header_String.split(this.delimiter));
        this.csv_Header_List_without_double_quote = new LinkedList<String>();
        for (final String csvHeader : this.csv_Header_List) {
            this.csv_Header_List_without_double_quote.add(this.removeQuoteChar(csvHeader));
        }
        final List<String> csv_Header_List_Temp = new LinkedList<String>();
        for (final ParameterRule csvParameterRule : templateRule.getTemplateKeyRule().values()) {
            final String KeyNameFromRule = csvParameterRule.getParamName();
            if (!csv_Header_List_Temp.contains(KeyNameFromRule) && this.csv_Header_List_without_double_quote.contains(KeyNameFromRule)) {
                csv_Header_List_Temp.add(KeyNameFromRule);
            }
        }
        for (final ParameterRule csvParameterRule : templateRule.getTemplateParamInRegexRule().values()) {
            int noOfOccurance = 0;
            for (final String CSVHeader : this.csv_Header_List_without_double_quote) {
                if (!csv_Header_List_Temp.contains(CSVHeader) && (SecurityUtil.matchPattern(CSVHeader, csvParameterRule.getParamName(), (SecurityRequestWrapper)request) || SecurityUtil.matchPattern(this.removeQuoteChar(CSVHeader), csvParameterRule.getParamName(), (SecurityRequestWrapper)request))) {
                    csv_Header_List_Temp.add(CSVHeader);
                    ++noOfOccurance;
                }
            }
            templateRule.validateNoOfOccurence(noOfOccurance, csvParameterRule, request, templateRule.getTemplateName());
        }
        if (csv_Header_List_Temp.size() != this.csv_Header_List.size()) {
            if (csv_Header_List_Temp.size() == 0) {
                CsvValidator.LOGGER.log(Level.SEVERE, "First Record in the CSV File must be Header-Record. ");
            }
            CsvValidator.LOGGER.log(Level.SEVERE, "Header set \"{0}\" do not matches with the Headers configured in security file OR format : \"{1}\" mismatches with the CSV Content ", new Object[] { this.csv_Header_List.toString(), csvFormatFromConf });
            throw new IAMSecurityException("CSV PARSE ERROR");
        }
        this.csvFormat = this.csvFormat.withFirstRecordAsHeader();
        CsvValidator.LOGGER.log(Level.INFO, " CSV Header Record Validated ");
        CSVParser csvParser = null;
        csvParser = Parser.parseCSV(parameterValue.trim(), this.csvFormat);
        final Iterator<CSVRecord> csvRecordIterator = csvParser.iterator();
        final int expectedSizeOfRecord = this.csv_Header_List.size();
        try {
            this.csvParsedKeySet = new LinkedList<String>();
            final LinkedList<String> individualValidRecords = new LinkedList<String>();
            this.validCSVRecords.add((LinkedList)this.csv_Header_List_without_double_quote);
            final boolean emptyLinesAllowed = CsvValidator.CSV_EMPTY_ALLOWED_CHECK_PATTERN.matcher(csvFormatFromConf).matches();
            while (csvRecordIterator.hasNext()) {
                this.csvRecord = csvRecordIterator.next();
                if (this.csvRecord.size() != expectedSizeOfRecord) {
                    if (this.csvRecord.size() != 1) {
                        CsvValidator.LOGGER.log(Level.SEVERE, "record \"{0}\" \n is invalid because it contains \"{1}\" elements ,but as per security-configuration only \"{2}\" elements are allowed per record", new Object[] { this.csvRecord.toString(), this.csvRecord.size(), expectedSizeOfRecord });
                        throw new IAMSecurityException("CSV PARSE ERROR");
                    }
                    if (!emptyLinesAllowed) {
                        CsvValidator.LOGGER.log(Level.SEVERE, "empty lines or single valued records are not allowed in the \"{0}\" format ", csvFormatFromConf);
                        throw new IAMSecurityException("CSV PARSE ERROR");
                    }
                    this.validCSVRecords.add(new LinkedList<String>(Arrays.asList(this.recordSeperator)));
                }
                else {
                    templateRule.validateDataFormat(request, this);
                    for (int i = 0; i < expectedSizeOfRecord; ++i) {
                        individualValidRecords.add(this.csvRecord.get(i));
                    }
                    this.validCSVRecords.add((LinkedList)individualValidRecords.clone());
                    individualValidRecords.clear();
                    this.csvParsedKeySet.clear();
                }
            }
            return this;
        }
        catch (final Exception e) {
            CsvValidator.LOGGER.log(Level.SEVERE, "Exception while parsing the Record Number \"{0}\"  ", this.csvRecord.getRecordNumber());
            throw new IAMSecurityException("CSV PARSE ERROR");
        }
    }
    
    private String removeQuoteChar(final String csvHeader) {
        return (csvHeader.startsWith("\"") && csvHeader.endsWith("\"")) ? csvHeader.replaceAll("^\"|\"$", "") : csvHeader;
    }
    
    public List<LinkedList<String>> getValidCSVRecords() {
        return this.validCSVRecords;
    }
    
    public CSVRecord getCsvRecord() {
        return this.csvRecord;
    }
    
    @Override
    public String toString() {
        final StringBuilder csvRecordsAsString = new StringBuilder();
        int noOfColumns = 0;
        if (this.validCSVRecords.size() > 0) {
            noOfColumns = this.validCSVRecords.get(0).size();
            final String quote = String.valueOf(this.csvFormat.getQuoteCharacter());
            for (final List<String> ValidRecord : this.validCSVRecords) {
                int index = 1;
                for (final String columnVal : ValidRecord) {
                    if (columnVal.equals(this.recordSeperator)) {
                        csvRecordsAsString.append(columnVal);
                    }
                    else {
                        csvRecordsAsString.append(quote + columnVal + quote);
                        if (index < noOfColumns) {
                            csvRecordsAsString.append(this.delimiter);
                        }
                        else {
                            csvRecordsAsString.append(this.recordSeperator);
                        }
                    }
                    ++index;
                }
            }
            return csvRecordsAsString.toString();
        }
        return "Empty-CSV-File";
    }
    
    @Override
    List<String> getKeySet() {
        return this.csv_Header_List_without_double_quote;
    }
    
    @Override
    String get(final String key) {
        return this.csvRecord.get(key);
    }
    
    @Override
    void set(final String key, final String value) {
        this.csvParsedKeySet.add(key);
    }
    
    @Override
    boolean hasValidated(final String key) {
        return this.csvParsedKeySet.contains(key);
    }
    
    @Override
    ZSecConstants.DataType getDataFormatType() {
        return ZSecConstants.DataType.Csv;
    }
    
    static {
        LOGGER = Logger.getLogger(CsvValidator.class.getName());
        CSV_SUPPORTED_FORMATS = new ArrayList<String>(Arrays.asList("Default", "Excel", "InformixUnload", "InformixUnloadCsv", "MySQL", "RFC4180", "TDF"));
        CSV_EMPTY_ALLOWED_CHECK_PATTERN = Pattern.compile("^(Excel|RFC4180|MySQL)$");
    }
}
