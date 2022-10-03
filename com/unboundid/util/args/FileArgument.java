package com.unboundid.util.args;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class FileArgument extends Argument
{
    private static final long serialVersionUID = -8478637530068695898L;
    private final boolean fileMustExist;
    private final boolean mustBeDirectory;
    private final boolean mustBeFile;
    private final boolean parentMustExist;
    private final ArrayList<File> values;
    private File relativeBaseDirectory;
    private final List<ArgumentValueValidator> validators;
    private final List<File> defaultValues;
    
    public FileArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, 1, null, description);
    }
    
    public FileArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, false, false, false, false, null);
    }
    
    public FileArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final boolean fileMustExist, final boolean parentMustExist, final boolean mustBeFile, final boolean mustBeDirectory) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, fileMustExist, parentMustExist, mustBeFile, mustBeDirectory, null);
    }
    
    public FileArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final boolean fileMustExist, final boolean parentMustExist, final boolean mustBeFile, final boolean mustBeDirectory, final List<File> defaultValues) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, maxOccurrences, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_PATH.get() : valuePlaceholder, description);
        if (mustBeFile && mustBeDirectory) {
            throw new ArgumentException(ArgsMessages.ERR_FILE_CANNOT_BE_FILE_AND_DIRECTORY.get(this.getIdentifierString()));
        }
        this.fileMustExist = fileMustExist;
        this.parentMustExist = parentMustExist;
        this.mustBeFile = mustBeFile;
        this.mustBeDirectory = mustBeDirectory;
        if (defaultValues == null || defaultValues.isEmpty()) {
            this.defaultValues = null;
        }
        else {
            this.defaultValues = Collections.unmodifiableList((List<? extends File>)defaultValues);
        }
        this.values = new ArrayList<File>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(5);
        this.relativeBaseDirectory = null;
    }
    
    private FileArgument(final FileArgument source) {
        super(source);
        this.fileMustExist = source.fileMustExist;
        this.mustBeDirectory = source.mustBeDirectory;
        this.mustBeFile = source.mustBeFile;
        this.parentMustExist = source.parentMustExist;
        this.defaultValues = source.defaultValues;
        this.relativeBaseDirectory = source.relativeBaseDirectory;
        this.validators = new ArrayList<ArgumentValueValidator>(source.validators);
        this.values = new ArrayList<File>(5);
    }
    
    public boolean fileMustExist() {
        return this.fileMustExist;
    }
    
    public boolean parentMustExist() {
        return this.parentMustExist;
    }
    
    public boolean mustBeFile() {
        return this.mustBeFile;
    }
    
    public boolean mustBeDirectory() {
        return this.mustBeDirectory;
    }
    
    public List<File> getDefaultValues() {
        return this.defaultValues;
    }
    
    public File getRelativeBaseDirectory() {
        return this.relativeBaseDirectory;
    }
    
    public void setRelativeBaseDirectory(final File relativeBaseDirectory) {
        this.relativeBaseDirectory = relativeBaseDirectory;
    }
    
    public void addValueValidator(final ArgumentValueValidator validator) {
        this.validators.add(validator);
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        File f = new File(valueString);
        if (!f.isAbsolute()) {
            if (this.relativeBaseDirectory == null) {
                f = new File(f.getAbsolutePath());
            }
            else {
                f = new File(new File(this.relativeBaseDirectory, valueString).getAbsolutePath());
            }
        }
        if (f.exists()) {
            if (this.mustBeFile && !f.isFile()) {
                throw new ArgumentException(ArgsMessages.ERR_FILE_VALUE_NOT_FILE.get(this.getIdentifierString(), f.getAbsolutePath()));
            }
            if (this.mustBeDirectory && !f.isDirectory()) {
                throw new ArgumentException(ArgsMessages.ERR_FILE_VALUE_NOT_DIRECTORY.get(this.getIdentifierString(), f.getAbsolutePath()));
            }
        }
        else {
            if (this.fileMustExist) {
                throw new ArgumentException(ArgsMessages.ERR_FILE_DOESNT_EXIST.get(f.getAbsolutePath(), this.getIdentifierString()));
            }
            if (this.parentMustExist) {
                final File parentFile = f.getAbsoluteFile().getParentFile();
                if (parentFile == null || !parentFile.exists() || !parentFile.isDirectory()) {
                    throw new ArgumentException(ArgsMessages.ERR_FILE_PARENT_DOESNT_EXIST.get(f.getAbsolutePath(), this.getIdentifierString()));
                }
            }
        }
        if (this.values.size() >= this.getMaxOccurrences()) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        for (final ArgumentValueValidator v : this.validators) {
            v.validateArgumentValue(this, valueString);
        }
        this.values.add(f);
    }
    
    public File getValue() {
        if (!this.values.isEmpty()) {
            return this.values.get(0);
        }
        if (this.defaultValues == null || this.defaultValues.isEmpty()) {
            return null;
        }
        return this.defaultValues.get(0);
    }
    
    public List<File> getValues() {
        if (this.values.isEmpty() && this.defaultValues != null) {
            return this.defaultValues;
        }
        return Collections.unmodifiableList((List<? extends File>)this.values);
    }
    
    public List<String> getFileLines() throws IOException {
        final File f = this.getValue();
        if (f == null) {
            return null;
        }
        final ArrayList<String> lines = new ArrayList<String>(20);
        final BufferedReader reader = new BufferedReader(new FileReader(f));
        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
            }
        }
        finally {
            reader.close();
        }
        return lines;
    }
    
    public List<String> getNonBlankFileLines() throws IOException {
        final File f = this.getValue();
        if (f == null) {
            return null;
        }
        final ArrayList<String> lines = new ArrayList<String>(20);
        final BufferedReader reader = new BufferedReader(new FileReader(f));
        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }
        finally {
            reader.close();
        }
        return lines;
    }
    
    public byte[] getFileBytes() throws IOException {
        final File f = this.getValue();
        if (f == null) {
            return null;
        }
        final byte[] fileData = new byte[(int)f.length()];
        final FileInputStream inputStream = new FileInputStream(f);
        try {
            int startPos = 0;
            for (int length = fileData.length, bytesRead = inputStream.read(fileData, startPos, length); bytesRead > 0 && startPos < fileData.length; startPos += bytesRead, length -= bytesRead, bytesRead = inputStream.read(fileData, startPos, length)) {}
            if (startPos < fileData.length) {
                throw new IOException(ArgsMessages.ERR_FILE_CANNOT_READ_FULLY.get(f.getAbsolutePath(), this.getIdentifierString()));
            }
            return fileData;
        }
        finally {
            inputStream.close();
        }
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        List<File> files;
        if (this.values.isEmpty()) {
            if (!useDefault) {
                return Collections.emptyList();
            }
            files = this.defaultValues;
        }
        else {
            files = this.values;
        }
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        final ArrayList<String> valueStrings = new ArrayList<String>(files.size());
        for (final File f : files) {
            valueStrings.add(f.getAbsolutePath());
        }
        return Collections.unmodifiableList((List<? extends String>)valueStrings);
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValues != null && !this.defaultValues.isEmpty();
    }
    
    @Override
    public String getDataTypeName() {
        if (this.mustBeDirectory) {
            return ArgsMessages.INFO_FILE_TYPE_PATH_DIRECTORY.get();
        }
        return ArgsMessages.INFO_FILE_TYPE_PATH_FILE.get();
    }
    
    @Override
    public String getValueConstraints() {
        final StringBuilder buffer = new StringBuilder();
        if (this.mustBeDirectory) {
            if (this.fileMustExist) {
                buffer.append(ArgsMessages.INFO_FILE_CONSTRAINTS_DIR_MUST_EXIST.get());
            }
            else if (this.parentMustExist) {
                buffer.append(ArgsMessages.INFO_FILE_CONSTRAINTS_DIR_PARENT_MUST_EXIST.get());
            }
            else {
                buffer.append(ArgsMessages.INFO_FILE_CONSTRAINTS_DIR_MAY_EXIST.get());
            }
        }
        else if (this.fileMustExist) {
            buffer.append(ArgsMessages.INFO_FILE_CONSTRAINTS_FILE_MUST_EXIST.get());
        }
        else if (this.parentMustExist) {
            buffer.append(ArgsMessages.INFO_FILE_CONSTRAINTS_FILE_PARENT_MUST_EXIST.get());
        }
        else {
            buffer.append(ArgsMessages.INFO_FILE_CONSTRAINTS_FILE_MAY_EXIST.get());
        }
        if (this.relativeBaseDirectory != null) {
            buffer.append("  ");
            buffer.append(ArgsMessages.INFO_FILE_CONSTRAINTS_RELATIVE_PATH_SPECIFIED_ROOT.get(this.relativeBaseDirectory.getAbsolutePath()));
        }
        return buffer.toString();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.values.clear();
    }
    
    @Override
    public FileArgument getCleanCopy() {
        return new FileArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.values != null) {
            for (final File f : this.values) {
                argStrings.add(this.getIdentifierString());
                if (this.isSensitive()) {
                    argStrings.add("***REDACTED***");
                }
                else {
                    argStrings.add(f.getAbsolutePath());
                }
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("FileArgument(");
        this.appendBasicToStringInfo(buffer);
        buffer.append(", fileMustExist=");
        buffer.append(this.fileMustExist);
        buffer.append(", parentMustExist=");
        buffer.append(this.parentMustExist);
        buffer.append(", mustBeFile=");
        buffer.append(this.mustBeFile);
        buffer.append(", mustBeDirectory=");
        buffer.append(this.mustBeDirectory);
        if (this.relativeBaseDirectory != null) {
            buffer.append(", relativeBaseDirectory='");
            buffer.append(this.relativeBaseDirectory.getAbsolutePath());
            buffer.append('\'');
        }
        if (this.defaultValues != null && !this.defaultValues.isEmpty()) {
            if (this.defaultValues.size() == 1) {
                buffer.append(", defaultValue='");
                buffer.append(this.defaultValues.get(0).toString());
            }
            else {
                buffer.append(", defaultValues={");
                final Iterator<File> iterator = this.defaultValues.iterator();
                while (iterator.hasNext()) {
                    buffer.append('\'');
                    buffer.append(iterator.next().toString());
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append('}');
            }
        }
        buffer.append(')');
    }
}
