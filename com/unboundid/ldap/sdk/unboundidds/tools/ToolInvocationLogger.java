package com.unboundid.ldap.sdk.unboundidds.tools;

import java.nio.channels.FileLock;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.channels.FileChannel;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.unboundid.util.ObjectPair;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import com.unboundid.util.Debug;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Collections;
import java.io.File;
import com.unboundid.util.StaticUtils;
import java.io.PrintStream;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ToolInvocationLogger
{
    private static final String LOG_MESSAGE_DATE_FORMAT = "dd/MMM/yyyy:HH:mm:ss.SSS Z";
    static final String PROPERTY_TEST_INSTANCE_ROOT;
    
    private ToolInvocationLogger() {
    }
    
    public static ToolInvocationLogDetails getLogMessageDetails(final String commandName, final boolean logByDefault, final PrintStream toolErrorStream) {
        String instanceRootPath = StaticUtils.getSystemProperty(ToolInvocationLogger.PROPERTY_TEST_INSTANCE_ROOT);
        if (instanceRootPath == null) {
            instanceRootPath = StaticUtils.getEnvironmentVariable("INSTANCE_ROOT");
            if (instanceRootPath == null) {
                return ToolInvocationLogDetails.createDoNotLogDetails(commandName);
            }
        }
        final File instanceRootDirectory = new File(instanceRootPath).getAbsoluteFile();
        if (!instanceRootDirectory.exists() || !instanceRootDirectory.isDirectory()) {
            return ToolInvocationLogDetails.createDoNotLogDetails(commandName);
        }
        final File defaultToolInvocationLogFile = StaticUtils.constructPath(instanceRootDirectory, "logs", "tools", "tool-invocation.log");
        boolean canUseDefaultLog;
        if (defaultToolInvocationLogFile.exists()) {
            canUseDefaultLog = defaultToolInvocationLogFile.isFile();
        }
        else {
            final File parentDirectory = defaultToolInvocationLogFile.getParentFile();
            canUseDefaultLog = (parentDirectory.exists() && parentDirectory.isDirectory());
        }
        final File invocationLoggingPropertiesFile = StaticUtils.constructPath(instanceRootDirectory, "config", "tool-invocation-logging.properties");
        if (!invocationLoggingPropertiesFile.exists()) {
            if (logByDefault && canUseDefaultLog) {
                return ToolInvocationLogDetails.createLogDetails(commandName, null, Collections.singleton(defaultToolInvocationLogFile), toolErrorStream);
            }
            return ToolInvocationLogDetails.createDoNotLogDetails(commandName);
        }
        else {
            final Properties loggingProperties = new Properties();
            try (final FileInputStream inputStream = new FileInputStream(invocationLoggingPropertiesFile)) {
                loggingProperties.load(inputStream);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                printError(ToolMessages.ERR_TOOL_LOGGER_ERROR_LOADING_PROPERTIES_FILE.get(invocationLoggingPropertiesFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), toolErrorStream);
                return ToolInvocationLogDetails.createDoNotLogDetails(commandName);
            }
            Boolean logInvocation = getBooleanProperty(commandName + ".log-tool-invocations", loggingProperties, invocationLoggingPropertiesFile, null, toolErrorStream);
            if (logInvocation == null) {
                logInvocation = getBooleanProperty("default.log-tool-invocations", loggingProperties, invocationLoggingPropertiesFile, null, toolErrorStream);
            }
            if (logInvocation == null) {
                logInvocation = logByDefault;
            }
            if (!logInvocation) {
                return ToolInvocationLogDetails.createDoNotLogDetails(commandName);
            }
            final Set<File> logFiles = new HashSet<File>(StaticUtils.computeMapCapacity(2));
            final String toolSpecificLogFilePathPropertyName = commandName + ".log-file-path";
            final File toolSpecificLogFile = getLogFileProperty(toolSpecificLogFilePathPropertyName, loggingProperties, invocationLoggingPropertiesFile, instanceRootDirectory, toolErrorStream);
            if (toolSpecificLogFile != null) {
                logFiles.add(toolSpecificLogFile);
            }
            if (getBooleanProperty(commandName + ".include-in-default-log", loggingProperties, invocationLoggingPropertiesFile, true, toolErrorStream)) {
                final String defaultLogFilePathPropertyName = "default.log-file-path";
                final File defaultLogFile = getLogFileProperty("default.log-file-path", loggingProperties, invocationLoggingPropertiesFile, instanceRootDirectory, toolErrorStream);
                if (defaultLogFile != null) {
                    logFiles.add(defaultLogFile);
                }
                else if (canUseDefaultLog) {
                    logFiles.add(defaultToolInvocationLogFile);
                }
                else {
                    printError(ToolMessages.ERR_TOOL_LOGGER_NO_LOG_FILES.get(commandName, invocationLoggingPropertiesFile.getAbsolutePath(), toolSpecificLogFilePathPropertyName, "default.log-file-path"), toolErrorStream);
                }
            }
            if (logFiles.isEmpty()) {
                return ToolInvocationLogDetails.createDoNotLogDetails(commandName);
            }
            return ToolInvocationLogDetails.createLogDetails(commandName, null, logFiles, toolErrorStream);
        }
    }
    
    private static Boolean getBooleanProperty(final String propertyName, final Properties properties, final File propertiesFilePath, final Boolean defaultValue, final PrintStream toolErrorStream) {
        final String propertyValue = properties.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        if (propertyValue.equalsIgnoreCase("true")) {
            return true;
        }
        if (propertyValue.equalsIgnoreCase("false")) {
            return false;
        }
        printError(ToolMessages.ERR_TOOL_LOGGER_CANNOT_PARSE_BOOLEAN_PROPERTY.get(propertyValue, propertyName, propertiesFilePath.getAbsolutePath()), toolErrorStream);
        return defaultValue;
    }
    
    private static File getLogFileProperty(final String propertyName, final Properties properties, final File propertiesFilePath, final File instanceRootDirectory, final PrintStream toolErrorStream) {
        final String propertyValue = properties.getProperty(propertyName);
        if (propertyValue == null) {
            return null;
        }
        final File configuredFile = new File(propertyValue);
        File absoluteFile;
        if (configuredFile.isAbsolute()) {
            absoluteFile = configuredFile;
        }
        else {
            absoluteFile = new File(instanceRootDirectory.getAbsolutePath() + File.separator + propertyValue);
        }
        if (absoluteFile.exists()) {
            if (absoluteFile.isFile()) {
                return absoluteFile;
            }
            printError(ToolMessages.ERR_TOOL_LOGGER_PATH_NOT_FILE.get(propertyValue, propertyName, propertiesFilePath.getAbsolutePath()), toolErrorStream);
        }
        else {
            final File parentFile = absoluteFile.getParentFile();
            if (parentFile.exists() && parentFile.isDirectory()) {
                return absoluteFile;
            }
            printError(ToolMessages.ERR_TOOL_LOGGER_PATH_PARENT_MISSING.get(propertyValue, propertyName, propertiesFilePath.getAbsolutePath(), parentFile.getAbsolutePath()), toolErrorStream);
        }
        return null;
    }
    
    public static void logLaunchMessage(final ToolInvocationLogDetails logDetails, final List<ObjectPair<String, String>> commandLineArguments, final List<ObjectPair<String, String>> propertiesFileArguments, final String propertiesFilePath) {
        final StringBuilder msgBuffer = new StringBuilder();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss.SSS Z");
        msgBuffer.append("# [");
        msgBuffer.append(dateFormat.format(new Date()));
        msgBuffer.append(']');
        msgBuffer.append(StaticUtils.EOL);
        msgBuffer.append("# Command Name: ");
        msgBuffer.append(logDetails.getCommandName());
        msgBuffer.append(StaticUtils.EOL);
        msgBuffer.append("# Invocation ID: ");
        msgBuffer.append(logDetails.getInvocationID());
        msgBuffer.append(StaticUtils.EOL);
        final String systemUserName = StaticUtils.getSystemProperty("user.name");
        if (systemUserName != null && !systemUserName.isEmpty()) {
            msgBuffer.append("# System User: ");
            msgBuffer.append(systemUserName);
            msgBuffer.append(StaticUtils.EOL);
        }
        if (!propertiesFileArguments.isEmpty()) {
            msgBuffer.append("# Arguments obtained from '");
            msgBuffer.append(propertiesFilePath);
            msgBuffer.append("':");
            msgBuffer.append(StaticUtils.EOL);
            for (final ObjectPair<String, String> argPair : propertiesFileArguments) {
                msgBuffer.append("#      ");
                final String name = argPair.getFirst();
                if (name.startsWith("-")) {
                    msgBuffer.append(name);
                }
                else {
                    msgBuffer.append(StaticUtils.cleanExampleCommandLineArgument(name));
                }
                final String value = argPair.getSecond();
                if (value != null) {
                    msgBuffer.append(' ');
                    msgBuffer.append(getCleanArgumentValue(name, value));
                }
                msgBuffer.append(StaticUtils.EOL);
            }
        }
        msgBuffer.append(logDetails.getCommandName());
        for (final ObjectPair<String, String> argPair : commandLineArguments) {
            msgBuffer.append(' ');
            final String name = argPair.getFirst();
            if (name.startsWith("-")) {
                msgBuffer.append(name);
            }
            else {
                msgBuffer.append(StaticUtils.cleanExampleCommandLineArgument(name));
            }
            final String value = argPair.getSecond();
            if (value != null) {
                msgBuffer.append(' ');
                msgBuffer.append(getCleanArgumentValue(name, value));
            }
        }
        msgBuffer.append(StaticUtils.EOL);
        msgBuffer.append(StaticUtils.EOL);
        final byte[] logMessageBytes = StaticUtils.getBytes(msgBuffer.toString());
        for (final File logFile : logDetails.getLogFiles()) {
            logMessageToFile(logMessageBytes, logFile, logDetails.getToolErrorStream());
        }
    }
    
    private static String getCleanArgumentValue(final String name, final String value) {
        final String lowerName = StaticUtils.toLowerCase(name);
        if ((lowerName.contains("password") || lowerName.contains("passphrase") || lowerName.endsWith("-pin") || name.endsWith("Pin") || name.endsWith("PIN")) && !lowerName.contains("passwordfile") && !lowerName.contains("password-file") && !lowerName.contains("passwordpath") && !lowerName.contains("password-path") && !lowerName.contains("passphrasefile") && !lowerName.contains("passphrase-file") && !lowerName.contains("passphrasepath") && !lowerName.contains("passphrase-path") && !StaticUtils.toLowerCase(value).contains("redacted")) {
            return "'*****REDACTED*****'";
        }
        return StaticUtils.cleanExampleCommandLineArgument(value);
    }
    
    public static void logCompletionMessage(final ToolInvocationLogDetails logDetails, final Integer exitCode, final String exitMessage) {
        final StringBuilder msgBuffer = new StringBuilder();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss.SSS Z");
        msgBuffer.append("# [");
        msgBuffer.append(dateFormat.format(new Date()));
        msgBuffer.append(']');
        msgBuffer.append(StaticUtils.EOL);
        msgBuffer.append("# Command Name: ");
        msgBuffer.append(logDetails.getCommandName());
        msgBuffer.append(StaticUtils.EOL);
        msgBuffer.append("# Invocation ID: ");
        msgBuffer.append(logDetails.getInvocationID());
        msgBuffer.append(StaticUtils.EOL);
        if (exitCode != null) {
            msgBuffer.append("# Exit Code: ");
            msgBuffer.append(exitCode);
            msgBuffer.append(StaticUtils.EOL);
        }
        if (exitMessage != null) {
            msgBuffer.append("# Exit Message: ");
            cleanMessage(exitMessage, msgBuffer);
            msgBuffer.append(StaticUtils.EOL);
        }
        msgBuffer.append(StaticUtils.EOL);
        final byte[] logMessageBytes = StaticUtils.getBytes(msgBuffer.toString());
        for (final File logFile : logDetails.getLogFiles()) {
            logMessageToFile(logMessageBytes, logFile, logDetails.getToolErrorStream());
        }
    }
    
    private static void cleanMessage(final String message, final StringBuilder buffer) {
        for (final char c : message.toCharArray()) {
            if (c >= ' ' && c <= '~') {
                buffer.append(c);
            }
            else {
                for (final byte b : StaticUtils.getBytes(Character.toString(c))) {
                    buffer.append('\\');
                    StaticUtils.toHex(b, buffer);
                }
            }
        }
    }
    
    private static void logMessageToFile(final byte[] logMessageBytes, final File logFile, final PrintStream toolErrorStream) {
        final Set<StandardOpenOption> openOptionsSet = EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.DSYNC);
        FileAttribute<?>[] fileAttributes;
        if (StaticUtils.isWindows()) {
            fileAttributes = new FileAttribute[0];
        }
        else {
            final Set<PosixFilePermission> filePermissionsSet = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
            final FileAttribute<Set<PosixFilePermission>> filePermissionsAttribute = PosixFilePermissions.asFileAttribute(filePermissionsSet);
            fileAttributes = new FileAttribute[] { filePermissionsAttribute };
        }
        try (final FileChannel fileChannel = FileChannel.open(logFile.toPath(), openOptionsSet, fileAttributes);
             final FileLock fileLock = acquireFileLock(fileChannel, logFile, toolErrorStream)) {
            if (fileLock != null) {
                try {
                    fileChannel.write(ByteBuffer.wrap(logMessageBytes));
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    printError(ToolMessages.ERR_TOOL_LOGGER_ERROR_WRITING_LOG_MESSAGE.get(logFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), toolErrorStream);
                }
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            printError(ToolMessages.ERR_TOOL_LOGGER_ERROR_OPENING_LOG_FILE.get(logFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)), toolErrorStream);
        }
    }
    
    private static FileLock acquireFileLock(final FileChannel fileChannel, final File logFile, final PrintStream toolErrorStream) {
        try {
            final FileLock fileLock = fileChannel.tryLock();
            if (fileLock != null) {
                return fileLock;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        int numAttempts = 1;
        final long stopWaitingTime = System.currentTimeMillis() + 1000L;
        while (System.currentTimeMillis() <= stopWaitingTime) {
            try {
                Thread.sleep(10L);
                final FileLock fileLock2 = fileChannel.tryLock();
                if (fileLock2 != null) {
                    return fileLock2;
                }
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
            ++numAttempts;
        }
        printError(ToolMessages.ERR_TOOL_LOGGER_UNABLE_TO_ACQUIRE_FILE_LOCK.get(logFile.getAbsolutePath(), numAttempts), toolErrorStream);
        return null;
    }
    
    private static void printError(final String message, final PrintStream toolErrorStream) {
        toolErrorStream.println();
        final int maxWidth = StaticUtils.TERMINAL_WIDTH_COLUMNS - 3;
        for (final String line : StaticUtils.wrapLine(message, maxWidth)) {
            toolErrorStream.println("# " + line);
        }
    }
    
    static {
        PROPERTY_TEST_INSTANCE_ROOT = ToolInvocationLogger.class.getName() + ".testInstanceRootPath";
    }
}
