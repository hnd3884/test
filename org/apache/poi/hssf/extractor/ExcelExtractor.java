package org.apache.poi.hssf.extractor;

import java.util.Locale;
import java.io.File;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Row;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.PrintStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.POIDocument;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.extractor.POIOLE2TextExtractor;

public class ExcelExtractor extends POIOLE2TextExtractor implements org.apache.poi.ss.extractor.ExcelExtractor
{
    private final HSSFWorkbook _wb;
    private final HSSFDataFormatter _formatter;
    private boolean _includeSheetNames;
    private boolean _shouldEvaluateFormulas;
    private boolean _includeCellComments;
    private boolean _includeBlankCells;
    private boolean _includeHeadersFooters;
    
    public ExcelExtractor(final HSSFWorkbook wb) {
        super(wb);
        this._includeSheetNames = true;
        this._shouldEvaluateFormulas = true;
        this._includeHeadersFooters = true;
        this._wb = wb;
        this._formatter = new HSSFDataFormatter();
    }
    
    public ExcelExtractor(final POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }
    
    public ExcelExtractor(final DirectoryNode dir) throws IOException {
        this(new HSSFWorkbook(dir, true));
    }
    
    private static void printUsageMessage(final PrintStream ps) {
        ps.println("Use:");
        ps.println("    " + ExcelExtractor.class.getName() + " [<flag> <value> [<flag> <value> [...]]] [-i <filename.xls>]");
        ps.println("       -i <filename.xls> specifies input file (default is to use stdin)");
        ps.println("       Flags can be set on or off by using the values 'Y' or 'N'.");
        ps.println("       Following are available flags and their default values:");
        ps.println("       --show-sheet-names  Y");
        ps.println("       --evaluate-formulas Y");
        ps.println("       --show-comments     N");
        ps.println("       --show-blanks       Y");
        ps.println("       --headers-footers   Y");
    }
    
    public static void main(final String[] args) throws IOException {
        CommandArgs cmdArgs;
        try {
            cmdArgs = new CommandArgs(args);
        }
        catch (final CommandParseException e) {
            System.err.println(e.getMessage());
            printUsageMessage(System.err);
            System.exit(1);
            return;
        }
        if (cmdArgs.isRequestHelp()) {
            printUsageMessage(System.out);
            return;
        }
        try (final InputStream is = (cmdArgs.getInputFile() == null) ? System.in : new FileInputStream(cmdArgs.getInputFile());
             final HSSFWorkbook wb = new HSSFWorkbook(is);
             final ExcelExtractor extractor = new ExcelExtractor(wb)) {
            extractor.setIncludeSheetNames(cmdArgs.shouldShowSheetNames());
            extractor.setFormulasNotResults(!cmdArgs.shouldEvaluateFormulas());
            extractor.setIncludeCellComments(cmdArgs.shouldShowCellComments());
            extractor.setIncludeBlankCells(cmdArgs.shouldShowBlankCells());
            extractor.setIncludeHeadersFooters(cmdArgs.shouldIncludeHeadersFooters());
            System.out.println(extractor.getText());
        }
    }
    
    @Override
    public void setIncludeSheetNames(final boolean includeSheetNames) {
        this._includeSheetNames = includeSheetNames;
    }
    
    @Override
    public void setFormulasNotResults(final boolean formulasNotResults) {
        this._shouldEvaluateFormulas = !formulasNotResults;
    }
    
    @Override
    public void setIncludeCellComments(final boolean includeCellComments) {
        this._includeCellComments = includeCellComments;
    }
    
    public void setIncludeBlankCells(final boolean includeBlankCells) {
        this._includeBlankCells = includeBlankCells;
    }
    
    @Override
    public void setIncludeHeadersFooters(final boolean includeHeadersFooters) {
        this._includeHeadersFooters = includeHeadersFooters;
    }
    
    @Override
    public String getText() {
        final StringBuilder text = new StringBuilder();
        this._wb.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        for (int i = 0; i < this._wb.getNumberOfSheets(); ++i) {
            final HSSFSheet sheet = this._wb.getSheetAt(i);
            if (sheet != null) {
                if (this._includeSheetNames) {
                    final String name = this._wb.getSheetName(i);
                    if (name != null) {
                        text.append(name);
                        text.append("\n");
                    }
                }
                if (this._includeHeadersFooters) {
                    text.append(_extractHeaderFooter(sheet.getHeader()));
                }
                final int firstRow = sheet.getFirstRowNum();
                for (int lastRow = sheet.getLastRowNum(), j = firstRow; j <= lastRow; ++j) {
                    final HSSFRow row = sheet.getRow(j);
                    if (row != null) {
                        int firstCell = row.getFirstCellNum();
                        final int lastCell = row.getLastCellNum();
                        if (this._includeBlankCells) {
                            firstCell = 0;
                        }
                        for (int k = firstCell; k < lastCell; ++k) {
                            final HSSFCell cell = row.getCell(k);
                            boolean outputContents = true;
                            if (cell == null) {
                                outputContents = this._includeBlankCells;
                            }
                            else {
                                Label_0547: {
                                    switch (cell.getCellType()) {
                                        case STRING: {
                                            text.append(cell.getRichStringCellValue().getString());
                                            break;
                                        }
                                        case NUMERIC: {
                                            text.append(this._formatter.formatCellValue(cell));
                                            break;
                                        }
                                        case BOOLEAN: {
                                            text.append(cell.getBooleanCellValue());
                                            break;
                                        }
                                        case ERROR: {
                                            text.append(ErrorEval.getText(cell.getErrorCellValue()));
                                            break;
                                        }
                                        case FORMULA: {
                                            if (!this._shouldEvaluateFormulas) {
                                                text.append(cell.getCellFormula());
                                                break;
                                            }
                                            switch (cell.getCachedFormulaResultType()) {
                                                case STRING: {
                                                    final HSSFRichTextString str = cell.getRichStringCellValue();
                                                    if (str != null && str.length() > 0) {
                                                        text.append(str);
                                                        break Label_0547;
                                                    }
                                                    break Label_0547;
                                                }
                                                case NUMERIC: {
                                                    final HSSFCellStyle style = cell.getCellStyle();
                                                    final double nVal = cell.getNumericCellValue();
                                                    final short df = style.getDataFormat();
                                                    final String dfs = style.getDataFormatString();
                                                    text.append(this._formatter.formatRawCellContents(nVal, df, dfs));
                                                    break Label_0547;
                                                }
                                                case BOOLEAN: {
                                                    text.append(cell.getBooleanCellValue());
                                                    break Label_0547;
                                                }
                                                case ERROR: {
                                                    text.append(ErrorEval.getText(cell.getErrorCellValue()));
                                                    break Label_0547;
                                                }
                                                default: {
                                                    throw new IllegalStateException("Unexpected cell cached formula result type: " + cell.getCachedFormulaResultType());
                                                }
                                            }
                                            break;
                                        }
                                        default: {
                                            throw new RuntimeException("Unexpected cell type (" + cell.getCellType() + ")");
                                        }
                                    }
                                }
                                final HSSFComment comment = cell.getCellComment();
                                if (this._includeCellComments && comment != null) {
                                    final String commentText = comment.getString().getString().replace('\n', ' ');
                                    text.append(" Comment by ").append(comment.getAuthor()).append(": ").append(commentText);
                                }
                            }
                            if (outputContents && k < lastCell - 1) {
                                text.append("\t");
                            }
                        }
                        text.append("\n");
                    }
                }
                if (this._includeHeadersFooters) {
                    text.append(_extractHeaderFooter(sheet.getFooter()));
                }
            }
        }
        return text.toString();
    }
    
    public static String _extractHeaderFooter(final HeaderFooter hf) {
        final StringBuilder text = new StringBuilder();
        if (hf.getLeft() != null) {
            text.append(hf.getLeft());
        }
        if (hf.getCenter() != null) {
            if (text.length() > 0) {
                text.append("\t");
            }
            text.append(hf.getCenter());
        }
        if (hf.getRight() != null) {
            if (text.length() > 0) {
                text.append("\t");
            }
            text.append(hf.getRight());
        }
        if (text.length() > 0) {
            text.append("\n");
        }
        return text.toString();
    }
    
    private static final class CommandParseException extends Exception
    {
        public CommandParseException(final String msg) {
            super(msg);
        }
    }
    
    private static final class CommandArgs
    {
        private final boolean _requestHelp;
        private final File _inputFile;
        private final boolean _showSheetNames;
        private final boolean _evaluateFormulas;
        private final boolean _showCellComments;
        private final boolean _showBlankCells;
        private final boolean _headersFooters;
        
        public CommandArgs(final String[] args) throws CommandParseException {
            final int nArgs = args.length;
            File inputFile = null;
            boolean requestHelp = false;
            boolean showSheetNames = true;
            boolean evaluateFormulas = true;
            boolean showCellComments = false;
            boolean showBlankCells = false;
            boolean headersFooters = true;
            for (int i = 0; i < nArgs; ++i) {
                String arg = args[i];
                if ("-help".equalsIgnoreCase(arg)) {
                    requestHelp = true;
                    break;
                }
                if ("-i".equals(arg)) {
                    if (++i >= nArgs) {
                        throw new CommandParseException("Expected filename after '-i'");
                    }
                    arg = args[i];
                    if (inputFile != null) {
                        throw new CommandParseException("Only one input file can be supplied");
                    }
                    inputFile = new File(arg);
                    if (!inputFile.exists()) {
                        throw new CommandParseException("Specified input file '" + arg + "' does not exist");
                    }
                    if (inputFile.isDirectory()) {
                        throw new CommandParseException("Specified input file '" + arg + "' is a directory");
                    }
                }
                else if ("--show-sheet-names".equals(arg)) {
                    showSheetNames = parseBoolArg(args, ++i);
                }
                else if ("--evaluate-formulas".equals(arg)) {
                    evaluateFormulas = parseBoolArg(args, ++i);
                }
                else if ("--show-comments".equals(arg)) {
                    showCellComments = parseBoolArg(args, ++i);
                }
                else if ("--show-blanks".equals(arg)) {
                    showBlankCells = parseBoolArg(args, ++i);
                }
                else {
                    if (!"--headers-footers".equals(arg)) {
                        throw new CommandParseException("Invalid argument '" + arg + "'");
                    }
                    headersFooters = parseBoolArg(args, ++i);
                }
            }
            this._requestHelp = requestHelp;
            this._inputFile = inputFile;
            this._showSheetNames = showSheetNames;
            this._evaluateFormulas = evaluateFormulas;
            this._showCellComments = showCellComments;
            this._showBlankCells = showBlankCells;
            this._headersFooters = headersFooters;
        }
        
        private static boolean parseBoolArg(final String[] args, final int i) throws CommandParseException {
            if (i >= args.length) {
                throw new CommandParseException("Expected value after '" + args[i - 1] + "'");
            }
            final String value = args[i].toUpperCase(Locale.ROOT);
            if ("Y".equals(value) || "YES".equals(value) || "ON".equals(value) || "TRUE".equals(value)) {
                return true;
            }
            if ("N".equals(value) || "NO".equals(value) || "OFF".equals(value) || "FALSE".equals(value)) {
                return false;
            }
            throw new CommandParseException("Invalid value '" + args[i] + "' for '" + args[i - 1] + "'. Expected 'Y' or 'N'");
        }
        
        public boolean isRequestHelp() {
            return this._requestHelp;
        }
        
        public File getInputFile() {
            return this._inputFile;
        }
        
        public boolean shouldShowSheetNames() {
            return this._showSheetNames;
        }
        
        public boolean shouldEvaluateFormulas() {
            return this._evaluateFormulas;
        }
        
        public boolean shouldShowCellComments() {
            return this._showCellComments;
        }
        
        public boolean shouldShowBlankCells() {
            return this._showBlankCells;
        }
        
        public boolean shouldIncludeHeadersFooters() {
            return this._headersFooters;
        }
    }
}
