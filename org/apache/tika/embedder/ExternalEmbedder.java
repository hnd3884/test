package org.apache.tika.embedder;

import java.io.File;
import org.apache.tika.exception.TikaException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayOutputStream;
import org.apache.tika.io.TikaInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.tika.metadata.Metadata;
import java.util.Collection;
import java.util.HashSet;
import org.apache.tika.parser.ParseContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import org.apache.tika.metadata.Property;
import java.util.Map;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import org.apache.tika.io.TemporaryResources;

public class ExternalEmbedder implements Embedder
{
    public static final String METADATA_COMMAND_ARGUMENTS_TOKEN = "${METADATA}";
    public static final String METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN = "${METADATA_SERIALIZED}";
    private static final long serialVersionUID = -2828829275642475697L;
    private final TemporaryResources tmp;
    private Set<MediaType> supportedEmbedTypes;
    private Map<Property, String[]> metadataCommandArguments;
    private String[] command;
    private String commandAssignmentOperator;
    private String commandAssignmentDelimeter;
    private String commandAppendOperator;
    private boolean quoteAssignmentValues;
    
    public ExternalEmbedder() {
        this.tmp = new TemporaryResources();
        this.supportedEmbedTypes = Collections.emptySet();
        this.metadataCommandArguments = null;
        this.command = new String[] { "sed", "-e", "$a\\\n${METADATA_SERIALIZED}", "${INPUT}" };
        this.commandAssignmentOperator = "=";
        this.commandAssignmentDelimeter = ", ";
        this.commandAppendOperator = "=";
        this.quoteAssignmentValues = false;
    }
    
    protected static String serializeMetadata(final List<String> metadataCommandArguments) {
        if (metadataCommandArguments != null) {
            return Arrays.toString(metadataCommandArguments.toArray());
        }
        return "";
    }
    
    public static boolean check(final String checkCmd, final int... errorValue) {
        return check(new String[] { checkCmd }, errorValue);
    }
    
    public static boolean check(final String[] checkCmd, int... errorValue) {
        if (errorValue.length == 0) {
            errorValue = new int[] { 127 };
        }
        try {
            Process process;
            if (checkCmd.length == 1) {
                process = Runtime.getRuntime().exec(checkCmd[0]);
            }
            else {
                process = Runtime.getRuntime().exec(checkCmd);
            }
            final int result = process.waitFor();
            for (final int err : errorValue) {
                if (result == err) {
                    return false;
                }
            }
            return true;
        }
        catch (final IOException | InterruptedException e) {
            return false;
        }
    }
    
    @Override
    public Set<MediaType> getSupportedEmbedTypes(final ParseContext context) {
        return this.getSupportedEmbedTypes();
    }
    
    public Set<MediaType> getSupportedEmbedTypes() {
        return this.supportedEmbedTypes;
    }
    
    public void setSupportedEmbedTypes(final Set<MediaType> supportedEmbedTypes) {
        this.supportedEmbedTypes = Collections.unmodifiableSet((Set<? extends MediaType>)new HashSet<MediaType>(supportedEmbedTypes));
    }
    
    public String[] getCommand() {
        return this.command;
    }
    
    public void setCommand(final String... command) {
        this.command = command;
    }
    
    public String getCommandAssignmentOperator() {
        return this.commandAssignmentOperator;
    }
    
    public void setCommandAssignmentOperator(final String commandAssignmentOperator) {
        this.commandAssignmentOperator = commandAssignmentOperator;
    }
    
    public String getCommandAssignmentDelimeter() {
        return this.commandAssignmentDelimeter;
    }
    
    public void setCommandAssignmentDelimeter(final String commandAssignmentDelimeter) {
        this.commandAssignmentDelimeter = commandAssignmentDelimeter;
    }
    
    public String getCommandAppendOperator() {
        return this.commandAppendOperator;
    }
    
    public void setCommandAppendOperator(final String commandAppendOperator) {
        this.commandAppendOperator = commandAppendOperator;
    }
    
    public boolean isQuoteAssignmentValues() {
        return this.quoteAssignmentValues;
    }
    
    public void setQuoteAssignmentValues(final boolean quoteAssignmentValues) {
        this.quoteAssignmentValues = quoteAssignmentValues;
    }
    
    public Map<Property, String[]> getMetadataCommandArguments() {
        return this.metadataCommandArguments;
    }
    
    public void setMetadataCommandArguments(final Map<Property, String[]> arguments) {
        this.metadataCommandArguments = arguments;
    }
    
    protected List<String> getCommandMetadataSegments(final Metadata metadata) {
        final List<String> commandMetadataSegments = new ArrayList<String>();
        if (metadata == null || metadata.names() == null) {
            return commandMetadataSegments;
        }
        for (final String metadataName : metadata.names()) {
            for (final Property property : this.getMetadataCommandArguments().keySet()) {
                if (metadataName.equals(property.getName())) {
                    final String[] metadataCommandArguments = this.getMetadataCommandArguments().get(property);
                    if (metadataCommandArguments == null) {
                        continue;
                    }
                    for (final String metadataCommandArgument : metadataCommandArguments) {
                        if (metadata.isMultiValued(metadataName)) {
                            for (String assignmentValue : metadata.getValues(metadataName)) {
                                final String metadataValue = assignmentValue;
                                if (this.quoteAssignmentValues) {
                                    assignmentValue = "'" + assignmentValue + "'";
                                }
                                commandMetadataSegments.add(metadataCommandArgument + this.commandAppendOperator + assignmentValue);
                            }
                        }
                        else {
                            String assignmentValue2 = metadata.get(metadataName);
                            if (this.quoteAssignmentValues) {
                                assignmentValue2 = "'" + assignmentValue2 + "'";
                            }
                            commandMetadataSegments.add(metadataCommandArgument + this.commandAssignmentOperator + assignmentValue2);
                        }
                    }
                }
            }
        }
        return commandMetadataSegments;
    }
    
    @Override
    public void embed(final Metadata metadata, final InputStream inputStream, final OutputStream outputStream, final ParseContext context) throws IOException, TikaException {
        boolean inputToStdIn = true;
        boolean outputFromStdOut = true;
        final boolean hasMetadataCommandArguments = this.metadataCommandArguments != null && !this.metadataCommandArguments.isEmpty();
        boolean serializeMetadataCommandArgumentsToken = false;
        boolean replacedMetadataCommandArgumentsToken = false;
        final TikaInputStream tikaInputStream = TikaInputStream.get(inputStream);
        File tempOutputFile = null;
        List<String> commandMetadataSegments = null;
        if (hasMetadataCommandArguments) {
            commandMetadataSegments = this.getCommandMetadataSegments(metadata);
        }
        final String[] origCmd = this.command;
        final List<String> cmd = new ArrayList<String>();
        for (String commandSegment : origCmd) {
            if (commandSegment.contains("${INPUT}")) {
                commandSegment = commandSegment.replace("${INPUT}", tikaInputStream.getFile().toString());
                inputToStdIn = false;
            }
            if (commandSegment.contains("${OUTPUT}")) {
                tempOutputFile = this.tmp.createTemporaryFile();
                commandSegment = commandSegment.replace("${OUTPUT}", tempOutputFile.toString());
                outputFromStdOut = false;
            }
            if (commandSegment.contains("${METADATA_SERIALIZED}")) {
                serializeMetadataCommandArgumentsToken = true;
            }
            if (commandSegment.contains("${METADATA}")) {
                if (hasMetadataCommandArguments) {
                    cmd.addAll(commandMetadataSegments);
                }
                replacedMetadataCommandArgumentsToken = true;
            }
            else {
                cmd.add(commandSegment);
            }
        }
        if (hasMetadataCommandArguments) {
            if (serializeMetadataCommandArgumentsToken) {
                int i = 0;
                for (String commandSegment2 : cmd) {
                    if (commandSegment2.contains("${METADATA_SERIALIZED}")) {
                        commandSegment2 = commandSegment2.replace("${METADATA_SERIALIZED}", serializeMetadata(commandMetadataSegments));
                        cmd.set(i, commandSegment2);
                    }
                    ++i;
                }
            }
            else if (!replacedMetadataCommandArgumentsToken && !serializeMetadataCommandArgumentsToken) {
                cmd.addAll(commandMetadataSegments);
            }
        }
        Process process;
        if (cmd.toArray().length == 1) {
            process = Runtime.getRuntime().exec(cmd.toArray(new String[0])[0]);
        }
        else {
            process = Runtime.getRuntime().exec(cmd.toArray(new String[0]));
        }
        final ByteArrayOutputStream stdErrOutputStream = new ByteArrayOutputStream();
        try {
            this.sendStdErrToOutputStream(process, stdErrOutputStream);
            if (inputToStdIn) {
                this.sendInputStreamToStdIn(inputStream, process);
            }
            else {
                process.getOutputStream().close();
            }
            if (outputFromStdOut) {
                this.sendStdOutToOutputStream(process, outputStream);
            }
            else {
                this.tmp.dispose();
                try {
                    process.waitFor();
                }
                catch (final InterruptedException ex) {}
                final InputStream tempOutputFileInputStream = (InputStream)TikaInputStream.get(tempOutputFile);
                IOUtils.copy(tempOutputFileInputStream, outputStream);
            }
        }
        finally {
            if (outputFromStdOut) {
                try {
                    process.waitFor();
                }
                catch (final InterruptedException ex2) {}
            }
            else {
                try {
                    tempOutputFile.delete();
                }
                catch (final Exception ex3) {}
            }
            if (!inputToStdIn) {
                IOUtils.closeQuietly((InputStream)tikaInputStream);
            }
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly((OutputStream)stdErrOutputStream);
            if (process.exitValue() != 0) {
                throw new TikaException("There was an error executing the command line\nExecutable Command:\n\n" + cmd + "\nExecutable Error:\n\n" + stdErrOutputStream.toString(StandardCharsets.UTF_8.name()));
            }
        }
    }
    
    private void multiThreadedStreamCopy(final InputStream inputStream, final OutputStream outputStream) {
        new Thread(() -> {
            try {
                IOUtils.copy(inputStream, outputStream);
            }
            catch (final IOException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }).start();
    }
    
    private void sendInputStreamToStdIn(final InputStream inputStream, final Process process) {
        this.multiThreadedStreamCopy(inputStream, process.getOutputStream());
    }
    
    private void sendStdOutToOutputStream(final Process process, final OutputStream outputStream) {
        try {
            IOUtils.copy(process.getInputStream(), outputStream);
        }
        catch (final IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
    
    private void sendStdErrToOutputStream(final Process process, final OutputStream outputStream) {
        this.multiThreadedStreamCopy(process.getErrorStream(), outputStream);
    }
}
