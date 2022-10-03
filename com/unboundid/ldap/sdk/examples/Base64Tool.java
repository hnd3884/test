package com.unboundid.ldap.sdk.examples;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import com.unboundid.util.Base64;
import com.unboundid.util.Debug;
import java.io.FileInputStream;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.SubCommand;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.StringArgument;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import java.io.InputStream;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.CommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class Base64Tool extends CommandLineTool
{
    private static final int WRAP_COLUMN;
    private static final String ARG_NAME_ADD_TRAILING_LINE_BREAK = "addTrailingLineBreak";
    private static final String ARG_NAME_DATA = "data";
    private static final String ARG_NAME_IGNORE_TRAILING_LINE_BREAK = "ignoreTrailingLineBreak";
    private static final String ARG_NAME_INPUT_FILE = "inputFile";
    private static final String ARG_NAME_OUTPUT_FILE = "outputFile";
    private static final String ARG_NAME_URL = "url";
    private static final String SUBCOMMAND_NAME_DECODE = "decode";
    private static final String SUBCOMMAND_NAME_ENCODE = "encode";
    private volatile ArgumentParser parser;
    private final InputStream in;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.in, System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final InputStream in, final OutputStream out, final OutputStream err, final String... args) {
        final Base64Tool tool = new Base64Tool(in, out, err);
        return tool.runTool(args);
    }
    
    public Base64Tool(final OutputStream out, final OutputStream err) {
        this(null, out, err);
    }
    
    public Base64Tool(final InputStream in, final OutputStream out, final OutputStream err) {
        super(out, err);
        this.in = in;
        this.parser = null;
    }
    
    @Override
    public String getToolName() {
        return "base64";
    }
    
    @Override
    public String getToolDescription() {
        return "Base64 encode raw data, or base64-decode encoded data.  The data to encode or decode may be provided via an argument value, in a file, or read from standard input.  The output may be written to a file or standard output.";
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public boolean supportsInteractiveMode() {
        return true;
    }
    
    @Override
    public boolean defaultsToInteractiveMode() {
        return true;
    }
    
    @Override
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    protected boolean supportsOutputFile() {
        return false;
    }
    
    @Override
    public void addToolArguments(final ArgumentParser parser) throws ArgumentException {
        this.parser = parser;
        final ArgumentParser encodeParser = new ArgumentParser("encode", "Base64-encodes raw data.");
        final StringArgument encodeDataArgument = new StringArgument('d', "data", false, 1, "{data}", "The raw data to be encoded.  If neither the --data nor the --inputFile argument is provided, then the data will be read from standard input.");
        encodeDataArgument.addLongIdentifier("rawData", true);
        encodeDataArgument.addLongIdentifier("raw-data", true);
        encodeParser.addArgument(encodeDataArgument);
        final FileArgument encodeDataFileArgument = new FileArgument('f', "inputFile", false, 1, null, "The path to a file containing the raw data to be encoded.  If neither the --data nor the --inputFile argument is provided, then the data will be read from standard input.", true, true, true, false);
        encodeDataFileArgument.addLongIdentifier("rawDataFile", true);
        encodeDataFileArgument.addLongIdentifier("input-file", true);
        encodeDataFileArgument.addLongIdentifier("raw-data-file", true);
        encodeParser.addArgument(encodeDataFileArgument);
        final FileArgument encodeOutputFileArgument = new FileArgument('o', "outputFile", false, 1, null, "The path to a file to which the encoded data should be written.  If this is not provided, the encoded data will be written to standard output.", false, true, true, false);
        encodeOutputFileArgument.addLongIdentifier("toEncodedFile", true);
        encodeOutputFileArgument.addLongIdentifier("output-file", true);
        encodeOutputFileArgument.addLongIdentifier("to-encoded-file", true);
        encodeParser.addArgument(encodeOutputFileArgument);
        final BooleanArgument encodeURLArgument = new BooleanArgument(null, "url", "Encode the data with the base64url mechanism rather than the standard base64 mechanism.");
        encodeParser.addArgument(encodeURLArgument);
        final BooleanArgument encodeIgnoreTrailingEOLArgument = new BooleanArgument(null, "ignoreTrailingLineBreak", "Ignore any end-of-line marker that may be present at the end of the data to encode.");
        encodeIgnoreTrailingEOLArgument.addLongIdentifier("ignore-trailing-line-break", true);
        encodeParser.addArgument(encodeIgnoreTrailingEOLArgument);
        encodeParser.addExclusiveArgumentSet(encodeDataArgument, encodeDataFileArgument, new Argument[0]);
        final LinkedHashMap<String[], String> encodeExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(3));
        encodeExamples.put(new String[] { "encode", "--data", "Hello" }, "Base64-encodes the string 'Hello' and writes the result to standard output.");
        encodeExamples.put(new String[] { "encode", "--inputFile", "raw-data.txt", "--outputFile", "encoded-data.txt" }, "Base64-encodes the data contained in the 'raw-data.txt' file and writes the result to the 'encoded-data.txt' file.");
        encodeExamples.put(new String[] { "encode" }, "Base64-encodes data read from standard input and writes the result to standard output.");
        final SubCommand encodeSubCommand = new SubCommand("encode", "Base64-encodes raw data.", encodeParser, encodeExamples);
        parser.addSubCommand(encodeSubCommand);
        final ArgumentParser decodeParser = new ArgumentParser("decode", "Decodes base64-encoded data.");
        final StringArgument decodeDataArgument = new StringArgument('d', "data", false, 1, "{data}", "The base64-encoded data to be decoded.  If neither the --data nor the --inputFile argument is provided, then the data will be read from standard input.");
        decodeDataArgument.addLongIdentifier("encodedData", true);
        decodeDataArgument.addLongIdentifier("encoded-data", true);
        decodeParser.addArgument(decodeDataArgument);
        final FileArgument decodeDataFileArgument = new FileArgument('f', "inputFile", false, 1, null, "The path to a file containing the base64-encoded data to be decoded.  If neither the --data nor the --inputFile argument is provided, then the data will be read from standard input.", true, true, true, false);
        decodeDataFileArgument.addLongIdentifier("encodedDataFile", true);
        decodeDataFileArgument.addLongIdentifier("input-file", true);
        decodeDataFileArgument.addLongIdentifier("encoded-data-file", true);
        decodeParser.addArgument(decodeDataFileArgument);
        final FileArgument decodeOutputFileArgument = new FileArgument('o', "outputFile", false, 1, null, "The path to a file to which the decoded data should be written.  If this is not provided, the decoded data will be written to standard output.", false, true, true, false);
        decodeOutputFileArgument.addLongIdentifier("toRawFile", true);
        decodeOutputFileArgument.addLongIdentifier("output-file", true);
        decodeOutputFileArgument.addLongIdentifier("to-raw-file", true);
        decodeParser.addArgument(decodeOutputFileArgument);
        final BooleanArgument decodeURLArgument = new BooleanArgument(null, "url", "Decode the data with the base64url mechanism rather than the standard base64 mechanism.");
        decodeParser.addArgument(decodeURLArgument);
        final BooleanArgument decodeAddTrailingLineBreak = new BooleanArgument(null, "addTrailingLineBreak", "Add a line break to the end of the decoded data.");
        decodeAddTrailingLineBreak.addLongIdentifier("add-trailing-line-break", true);
        decodeParser.addArgument(decodeAddTrailingLineBreak);
        decodeParser.addExclusiveArgumentSet(decodeDataArgument, decodeDataFileArgument, new Argument[0]);
        final LinkedHashMap<String[], String> decodeExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(3));
        decodeExamples.put(new String[] { "decode", "--data", "SGVsbG8=" }, "Base64-decodes the string 'SGVsbG8=' and writes the result to standard output.");
        decodeExamples.put(new String[] { "decode", "--inputFile", "encoded-data.txt", "--outputFile", "decoded-data.txt" }, "Base64-decodes the data contained in the 'encoded-data.txt' file and writes the result to the 'raw-data.txt' file.");
        decodeExamples.put(new String[] { "decode" }, "Base64-decodes data read from standard input and writes the result to standard output.");
        final SubCommand decodeSubCommand = new SubCommand("decode", "Decodes base64-encoded data.", decodeParser, decodeExamples);
        parser.addSubCommand(decodeSubCommand);
    }
    
    @Override
    public ResultCode doToolProcessing() {
        final SubCommand subCommand = this.parser.getSelectedSubCommand();
        if (subCommand == null) {
            this.wrapErr(0, Base64Tool.WRAP_COLUMN, "No subcommand was selected.");
            return ResultCode.PARAM_ERROR;
        }
        if (subCommand.hasName("encode")) {
            return this.doEncode(subCommand.getArgumentParser());
        }
        return this.doDecode(subCommand.getArgumentParser());
    }
    
    private ResultCode doEncode(final ArgumentParser p) {
        final ByteStringBuffer rawDataBuffer = new ByteStringBuffer();
        final StringArgument dataArg = p.getStringArgument("data");
        if (dataArg != null && dataArg.isPresent()) {
            rawDataBuffer.append((CharSequence)dataArg.getValue());
        }
        else {
            try {
                final FileArgument inputFileArg = p.getFileArgument("inputFile");
                InputStream inputStream;
                if (inputFileArg != null && inputFileArg.isPresent()) {
                    inputStream = new FileInputStream(inputFileArg.getValue());
                }
                else {
                    inputStream = this.in;
                }
                final byte[] buffer = new byte[8192];
                while (true) {
                    final int bytesRead = inputStream.read(buffer);
                    if (bytesRead <= 0) {
                        break;
                    }
                    rawDataBuffer.append(buffer, 0, bytesRead);
                }
                inputStream.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.wrapErr(0, Base64Tool.WRAP_COLUMN, "An error occurred while attempting to read the data to encode:  ", StaticUtils.getExceptionMessage(e));
                return ResultCode.LOCAL_ERROR;
            }
        }
        final BooleanArgument ignoreEOLArg = p.getBooleanArgument("ignoreTrailingLineBreak");
        Label_0246: {
            if (ignoreEOLArg != null && ignoreEOLArg.isPresent()) {
                while (rawDataBuffer.length() > 0) {
                    switch (rawDataBuffer.getBackingArray()[rawDataBuffer.length() - 1]) {
                        case 10:
                        case 13: {
                            rawDataBuffer.delete(rawDataBuffer.length() - 1, 1);
                            continue;
                        }
                        default: {
                            break Label_0246;
                        }
                    }
                }
            }
        }
        final byte[] rawDataArray = rawDataBuffer.toByteArray();
        final ByteStringBuffer encodedDataBuffer = new ByteStringBuffer(4 * rawDataBuffer.length() / 3 + 3);
        final BooleanArgument urlArg = p.getBooleanArgument("url");
        if (urlArg != null && urlArg.isPresent()) {
            Base64.urlEncode(rawDataArray, 0, rawDataArray.length, encodedDataBuffer, false);
        }
        else {
            Base64.encode(rawDataArray, encodedDataBuffer);
        }
        final FileArgument outputFileArg = p.getFileArgument("outputFile");
        if (outputFileArg != null && outputFileArg.isPresent()) {
            try {
                final FileOutputStream outputStream = new FileOutputStream(outputFileArg.getValue(), false);
                encodedDataBuffer.write(outputStream);
                outputStream.write(StaticUtils.EOL_BYTES);
                outputStream.flush();
                outputStream.close();
                return ResultCode.SUCCESS;
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                this.wrapErr(0, Base64Tool.WRAP_COLUMN, "An error occurred while attempting to write the base64-encoded data to output file ", outputFileArg.getValue().getAbsolutePath(), ":  ", StaticUtils.getExceptionMessage(e2));
                this.err("Base64-encoded data:");
                this.err(encodedDataBuffer.toString());
                return ResultCode.LOCAL_ERROR;
            }
        }
        this.out(encodedDataBuffer.toString());
        return ResultCode.SUCCESS;
    }
    
    private ResultCode doDecode(final ArgumentParser p) {
        final ByteStringBuffer encodedDataBuffer = new ByteStringBuffer();
        final BooleanArgument urlArg = p.getBooleanArgument("url");
        final StringArgument dataArg = p.getStringArgument("data");
        if (dataArg != null && dataArg.isPresent()) {
            encodedDataBuffer.append((CharSequence)dataArg.getValue());
        }
        else {
            try {
                final FileArgument inputFileArg = p.getFileArgument("inputFile");
                BufferedReader reader;
                if (inputFileArg != null && inputFileArg.isPresent()) {
                    reader = new BufferedReader(new FileReader(inputFileArg.getValue()));
                }
                else {
                    reader = new BufferedReader(new InputStreamReader(this.in));
                }
                while (true) {
                    final String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (line.startsWith("#")) {
                        continue;
                    }
                    if (line.startsWith("-")) {
                        if (urlArg == null) {
                            continue;
                        }
                        if (!urlArg.isPresent()) {
                            continue;
                        }
                    }
                    encodedDataBuffer.append((CharSequence)line);
                }
                reader.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.wrapErr(0, Base64Tool.WRAP_COLUMN, "An error occurred while attempting to read the data to decode:  ", StaticUtils.getExceptionMessage(e));
                return ResultCode.LOCAL_ERROR;
            }
        }
        final ByteStringBuffer rawDataBuffer = new ByteStringBuffer(encodedDataBuffer.length());
        Label_0382: {
            if (urlArg != null && urlArg.isPresent()) {
                try {
                    rawDataBuffer.append(Base64.urlDecode(encodedDataBuffer.toString()));
                    break Label_0382;
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    this.wrapErr(0, Base64Tool.WRAP_COLUMN, "An error occurred while attempting to base64url-decode the provided data:  " + StaticUtils.getExceptionMessage(e2));
                    return ResultCode.LOCAL_ERROR;
                }
            }
            try {
                rawDataBuffer.append(Base64.decode(encodedDataBuffer.toString()));
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                this.wrapErr(0, Base64Tool.WRAP_COLUMN, "An error occurred while attempting to base64-decode the provided data:  " + StaticUtils.getExceptionMessage(e2));
                return ResultCode.LOCAL_ERROR;
            }
        }
        final BooleanArgument addEOLArg = p.getBooleanArgument("addTrailingLineBreak");
        if (addEOLArg != null && addEOLArg.isPresent()) {
            rawDataBuffer.append(StaticUtils.EOL_BYTES);
        }
        final FileArgument outputFileArg = p.getFileArgument("outputFile");
        if (outputFileArg != null && outputFileArg.isPresent()) {
            try {
                final FileOutputStream outputStream = new FileOutputStream(outputFileArg.getValue(), false);
                rawDataBuffer.write(outputStream);
                outputStream.flush();
                outputStream.close();
                return ResultCode.SUCCESS;
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                this.wrapErr(0, Base64Tool.WRAP_COLUMN, "An error occurred while attempting to write the base64-decoded data to output file ", outputFileArg.getValue().getAbsolutePath(), ":  ", StaticUtils.getExceptionMessage(e3));
                this.err("Base64-decoded data:");
                this.err(encodedDataBuffer.toString());
                return ResultCode.LOCAL_ERROR;
            }
        }
        final byte[] rawDataArray = rawDataBuffer.toByteArray();
        this.getOut().write(rawDataArray, 0, rawDataArray.length);
        this.getOut().flush();
        return ResultCode.SUCCESS;
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        examples.put(new String[] { "encode", "--data", "Hello" }, "Base64-encodes the string 'Hello' and writes the result to standard output.");
        examples.put(new String[] { "decode", "--inputFile", "encoded-data.txt", "--outputFile", "decoded-data.txt" }, "Base64-decodes the data contained in the 'encoded-data.txt' file and writes the result to the 'raw-data.txt' file.");
        return examples;
    }
    
    static {
        WRAP_COLUMN = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
    }
}
