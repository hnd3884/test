package org.eclipse.jdt.core;

import java.util.Arrays;
import java.util.Comparator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.io.File;
import org.apache.tools.ant.util.JavaEnvUtils;
import java.lang.reflect.InvocationTargetException;
import org.apache.tools.ant.types.Path;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.apache.tools.ant.taskdefs.Javac;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.BuildException;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.eclipse.jdt.internal.antadapter.AntAdapterMessages;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;

public class JDTCompilerAdapter extends DefaultCompilerAdapter
{
    private static final char[] SEPARATOR_CHARS;
    private static final char[] ADAPTER_PREFIX;
    private static final char[] ADAPTER_ENCODING;
    private static final char[] ADAPTER_ACCESS;
    private static String compilerClass;
    String logFileName;
    Map customDefaultOptions;
    private Map fileEncodings;
    private Map dirEncodings;
    private List accessRules;
    
    static {
        SEPARATOR_CHARS = new char[] { '/', '\\' };
        ADAPTER_PREFIX = "#ADAPTER#".toCharArray();
        ADAPTER_ENCODING = "ENCODING#".toCharArray();
        ADAPTER_ACCESS = "ACCESS#".toCharArray();
        JDTCompilerAdapter.compilerClass = "org.eclipse.jdt.internal.compiler.batch.Main";
    }
    
    public JDTCompilerAdapter() {
        this.fileEncodings = null;
        this.dirEncodings = null;
        this.accessRules = null;
    }
    
    public boolean execute() throws BuildException {
        this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.usingJDTCompiler"), 3);
        final Commandline cmd = this.setupJavacCommand();
        try {
            final Class c = Class.forName(JDTCompilerAdapter.compilerClass);
            final Constructor batchCompilerConstructor = c.getConstructor(PrintWriter.class, PrintWriter.class, Boolean.TYPE, Map.class);
            final Object batchCompilerInstance = batchCompilerConstructor.newInstance(new PrintWriter(System.out), new PrintWriter(System.err), Boolean.TRUE, this.customDefaultOptions);
            final Method compile = c.getMethod("compile", String[].class);
            final Object result = compile.invoke(batchCompilerInstance, cmd.getArguments());
            final boolean resultValue = (boolean)result;
            if (!resultValue && this.logFileName != null) {
                this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.error.compilationFailed", this.logFileName));
            }
            return resultValue;
        }
        catch (final ClassNotFoundException ex2) {
            throw new BuildException(AntAdapterMessages.getString("ant.jdtadapter.error.cannotFindJDTCompiler"));
        }
        catch (final Exception ex) {
            throw new BuildException((Throwable)ex);
        }
    }
    
    protected Commandline setupJavacCommand() throws BuildException {
        final Commandline cmd = new Commandline();
        this.customDefaultOptions = new CompilerOptions().getMap();
        final Class javacClass = Javac.class;
        final String[] compilerArgs = this.processCompilerArguments(javacClass);
        cmd.createArgument().setValue("-noExit");
        if (this.bootclasspath != null) {
            cmd.createArgument().setValue("-bootclasspath");
            if (this.bootclasspath.size() != 0) {
                cmd.createArgument().setPath(this.bootclasspath);
            }
            else {
                cmd.createArgument().setValue(Util.EMPTY_STRING);
            }
        }
        if (this.extdirs != null) {
            cmd.createArgument().setValue("-extdirs");
            cmd.createArgument().setPath(this.extdirs);
        }
        final Path classpath = new Path(this.project);
        classpath.append(this.getCompileClasspath());
        cmd.createArgument().setValue("-classpath");
        this.createClasspathArgument(cmd, classpath);
        Path sourcepath = null;
        Method getSourcepathMethod = null;
        try {
            getSourcepathMethod = javacClass.getMethod("getSourcepath", (Class[])null);
        }
        catch (final NoSuchMethodException ex) {}
        Path compileSourcePath = null;
        if (getSourcepathMethod != null) {
            try {
                compileSourcePath = (Path)getSourcepathMethod.invoke(this.attributes, (Object[])null);
            }
            catch (final IllegalAccessException ex2) {}
            catch (final InvocationTargetException ex3) {}
        }
        if (compileSourcePath != null) {
            sourcepath = compileSourcePath;
        }
        else {
            sourcepath = this.src;
        }
        cmd.createArgument().setValue("-sourcepath");
        this.createClasspathArgument(cmd, sourcepath);
        final String javaVersion = JavaEnvUtils.getJavaVersion();
        final String memoryParameterPrefix = javaVersion.equals("1.1") ? "-J-" : "-J-X";
        if (this.memoryInitialSize != null) {
            if (!this.attributes.isForkedJavac()) {
                this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.ignoringMemoryInitialSize"), 1);
            }
            else {
                cmd.createArgument().setValue(String.valueOf(memoryParameterPrefix) + "ms" + this.memoryInitialSize);
            }
        }
        if (this.memoryMaximumSize != null) {
            if (!this.attributes.isForkedJavac()) {
                this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.ignoringMemoryMaximumSize"), 1);
            }
            else {
                cmd.createArgument().setValue(String.valueOf(memoryParameterPrefix) + "mx" + this.memoryMaximumSize);
            }
        }
        if (this.debug) {
            Method getDebugLevelMethod = null;
            try {
                getDebugLevelMethod = javacClass.getMethod("getDebugLevel", (Class[])null);
            }
            catch (final NoSuchMethodException ex4) {}
            String debugLevel = null;
            if (getDebugLevelMethod != null) {
                try {
                    debugLevel = (String)getDebugLevelMethod.invoke(this.attributes, (Object[])null);
                }
                catch (final IllegalAccessException ex5) {}
                catch (final InvocationTargetException ex6) {}
            }
            if (debugLevel != null) {
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "do not generate");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "do not generate");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "do not generate");
                if (debugLevel.length() != 0) {
                    if (debugLevel.indexOf("vars") != -1) {
                        this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
                    }
                    if (debugLevel.indexOf("lines") != -1) {
                        this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "generate");
                    }
                    if (debugLevel.indexOf("source") != -1) {
                        this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "generate");
                    }
                }
            }
            else {
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "generate");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "generate");
            }
        }
        else {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "do not generate");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "do not generate");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "do not generate");
        }
        if (this.attributes.getNowarn()) {
            final Object[] entries = this.customDefaultOptions.entrySet().toArray();
            for (int i = 0, max = entries.length; i < max; ++i) {
                final Map.Entry entry = (Map.Entry)entries[i];
                if (entry.getKey() instanceof String) {
                    if (entry.getValue() instanceof String) {
                        if (entry.getValue().equals("warning")) {
                            this.customDefaultOptions.put(entry.getKey(), "ignore");
                        }
                    }
                }
            }
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.taskTags", Util.EMPTY_STRING);
            if (this.deprecation) {
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "enabled");
                this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "enabled");
            }
        }
        else if (this.deprecation) {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "enabled");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "enabled");
        }
        else {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecation", "ignore");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "disabled");
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "disabled");
        }
        if (this.destDir != null) {
            cmd.createArgument().setValue("-d");
            cmd.createArgument().setFile(this.destDir.getAbsoluteFile());
        }
        if (this.verbose) {
            cmd.createArgument().setValue("-verbose");
        }
        if (!this.attributes.getFailonerror()) {
            cmd.createArgument().setValue("-proceedOnError");
        }
        if (this.target != null) {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", this.target);
        }
        final String source = this.attributes.getSource();
        if (source != null) {
            this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.source", source);
        }
        if (compilerArgs != null) {
            final int length = compilerArgs.length;
            if (length != 0) {
                for (int j = 0, max2 = length; j < max2; ++j) {
                    final String arg = compilerArgs[j];
                    if (this.logFileName == null && "-log".equals(arg) && j + 1 < max2) {
                        this.logFileName = compilerArgs[j + 1];
                    }
                    cmd.createArgument().setValue(arg);
                }
            }
        }
        if (this.encoding != null) {
            cmd.createArgument().setValue("-encoding");
            cmd.createArgument().setValue(this.encoding);
        }
        this.logAndAddFilesToCompile(cmd);
        return cmd;
    }
    
    private String[] processCompilerArguments(final Class javacClass) {
        Method getCurrentCompilerArgsMethod = null;
        try {
            getCurrentCompilerArgsMethod = javacClass.getMethod("getCurrentCompilerArgs", (Class[])null);
        }
        catch (final NoSuchMethodException ex) {}
        String[] compilerArgs = null;
        if (getCurrentCompilerArgsMethod != null) {
            try {
                compilerArgs = (String[])getCurrentCompilerArgsMethod.invoke(this.attributes, (Object[])null);
            }
            catch (final IllegalAccessException ex2) {}
            catch (final InvocationTargetException ex3) {}
        }
        if (compilerArgs != null) {
            this.checkCompilerArgs(compilerArgs);
        }
        return compilerArgs;
    }
    
    private void checkCompilerArgs(final String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].charAt(0) == '@') {
                try {
                    final char[] content = Util.getFileCharContent(new File(args[i].substring(1)), null);
                    int offset = 0;
                    final int prefixLength = JDTCompilerAdapter.ADAPTER_PREFIX.length;
                    while ((offset = CharOperation.indexOf(JDTCompilerAdapter.ADAPTER_PREFIX, content, true, offset)) > -1) {
                        int start = offset + prefixLength;
                        int end = CharOperation.indexOf('\n', content, start);
                        if (end == -1) {
                            end = content.length;
                        }
                        while (CharOperation.isWhitespace(content[end])) {
                            --end;
                        }
                        if (CharOperation.equals(JDTCompilerAdapter.ADAPTER_ENCODING, content, start, start + JDTCompilerAdapter.ADAPTER_ENCODING.length)) {
                            CharOperation.replace(content, JDTCompilerAdapter.SEPARATOR_CHARS, File.separatorChar, start, end + 1);
                            start += JDTCompilerAdapter.ADAPTER_ENCODING.length;
                            final int encodeStart = CharOperation.lastIndexOf('[', content, start, end);
                            if (start < encodeStart && encodeStart < end) {
                                final boolean isFile = CharOperation.equals(SuffixConstants.SUFFIX_java, content, encodeStart - 5, encodeStart, false);
                                final String str = String.valueOf(content, start, encodeStart - start);
                                final String enc = String.valueOf(content, encodeStart, end - encodeStart + 1);
                                if (isFile) {
                                    if (this.fileEncodings == null) {
                                        this.fileEncodings = new HashMap();
                                    }
                                    this.fileEncodings.put(str, enc);
                                }
                                else {
                                    if (this.dirEncodings == null) {
                                        this.dirEncodings = new HashMap();
                                    }
                                    this.dirEncodings.put(str, enc);
                                }
                            }
                        }
                        else if (CharOperation.equals(JDTCompilerAdapter.ADAPTER_ACCESS, content, start, start + JDTCompilerAdapter.ADAPTER_ACCESS.length)) {
                            start += JDTCompilerAdapter.ADAPTER_ACCESS.length;
                            final int accessStart = CharOperation.indexOf('[', content, start, end);
                            CharOperation.replace(content, JDTCompilerAdapter.SEPARATOR_CHARS, File.separatorChar, start, accessStart);
                            if (start < accessStart && accessStart < end) {
                                final String path = String.valueOf(content, start, accessStart - start);
                                final String access = String.valueOf(content, accessStart, end - accessStart + 1);
                                if (this.accessRules == null) {
                                    this.accessRules = new ArrayList();
                                }
                                this.accessRules.add(path);
                                this.accessRules.add(access);
                            }
                        }
                        offset = end;
                    }
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    private void createClasspathArgument(final Commandline cmd, final Path classpath) {
        final Commandline.Argument arg = cmd.createArgument();
        final String[] pathElements = classpath.list();
        if (pathElements.length == 0) {
            arg.setValue(Util.EMPTY_STRING);
            return;
        }
        if (this.accessRules == null) {
            arg.setPath(classpath);
            return;
        }
        final int rulesLength = this.accessRules.size();
        final String[] rules = this.accessRules.toArray(new String[rulesLength]);
        int nextRule = 0;
        final StringBuffer result = new StringBuffer();
        for (int i = 0, max = pathElements.length; i < max; ++i) {
            if (i > 0) {
                result.append(File.pathSeparatorChar);
            }
            final String pathElement = pathElements[i];
            result.append(pathElement);
            for (int j = nextRule; j < rulesLength; j += 2) {
                final String rule = rules[j];
                if (pathElement.endsWith(rule)) {
                    result.append(rules[j + 1]);
                    nextRule = j + 2;
                    break;
                }
                if (rule.endsWith(File.separator)) {
                    final int ruleLength = rule.length();
                    if (pathElement.regionMatches(false, pathElement.length() - ruleLength + 1, rule, 0, ruleLength - 1)) {
                        result.append(rules[j + 1]);
                        nextRule = j + 2;
                        break;
                    }
                }
                else if (pathElement.endsWith(File.separator)) {
                    final int ruleLength = rule.length();
                    if (pathElement.regionMatches(false, pathElement.length() - ruleLength - 1, rule, 0, ruleLength)) {
                        result.append(rules[j + 1]);
                        nextRule = j + 2;
                        break;
                    }
                }
            }
        }
        arg.setValue(result.toString());
    }
    
    protected void logAndAddFilesToCompile(final Commandline cmd) {
        this.attributes.log("Compilation " + cmd.describeArguments(), 3);
        final StringBuffer niceSourceList = new StringBuffer("File");
        if (this.compileList.length != 1) {
            niceSourceList.append("s");
        }
        niceSourceList.append(" to be compiled:");
        niceSourceList.append(JDTCompilerAdapter.lSep);
        String[] encodedFiles = null;
        String[] encodedDirs = null;
        int encodedFilesLength = 0;
        int encodedDirsLength = 0;
        if (this.fileEncodings != null) {
            encodedFilesLength = this.fileEncodings.size();
            encodedFiles = new String[encodedFilesLength];
            this.fileEncodings.keySet().toArray(encodedFiles);
        }
        if (this.dirEncodings != null) {
            encodedDirsLength = this.dirEncodings.size();
            encodedDirs = new String[encodedDirsLength];
            this.dirEncodings.keySet().toArray(encodedDirs);
            final Comparator comparator = new Comparator() {
                @Override
                public int compare(final Object o1, final Object o2) {
                    return ((String)o2).length() - ((String)o1).length();
                }
            };
            Arrays.sort(encodedDirs, comparator);
        }
        for (int i = 0; i < this.compileList.length; ++i) {
            String arg = this.compileList[i].getAbsolutePath();
            boolean encoded = false;
            if (encodedFiles != null) {
                for (int j = 0; j < encodedFilesLength; ++j) {
                    if (arg.endsWith(encodedFiles[j])) {
                        arg = String.valueOf(arg) + this.fileEncodings.get(encodedFiles[j]);
                        if (j < encodedFilesLength - 1) {
                            System.arraycopy(encodedFiles, j + 1, encodedFiles, j, encodedFilesLength - j - 1);
                        }
                        encodedFiles[--encodedFilesLength] = null;
                        encoded = true;
                        break;
                    }
                }
            }
            if (!encoded && encodedDirs != null) {
                for (int j = 0; j < encodedDirsLength; ++j) {
                    if (arg.lastIndexOf(encodedDirs[j]) != -1) {
                        arg = String.valueOf(arg) + this.dirEncodings.get(encodedDirs[j]);
                        break;
                    }
                }
            }
            cmd.createArgument().setValue(arg);
            niceSourceList.append("    " + arg + JDTCompilerAdapter.lSep);
        }
        this.attributes.log(niceSourceList.toString(), 3);
    }
}
