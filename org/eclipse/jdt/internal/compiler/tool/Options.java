package org.eclipse.jdt.internal.compiler.tool;

import java.util.StringTokenizer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Options
{
    private static final Set<String> ZERO_ARGUMENT_OPTIONS;
    private static final Set<String> ONE_ARGUMENT_OPTIONS;
    private static final Set<String> FILE_MANAGER_OPTIONS;
    
    static {
        (ZERO_ARGUMENT_OPTIONS = new HashSet<String>()).add("-progress");
        Options.ZERO_ARGUMENT_OPTIONS.add("-proceedOnError");
        Options.ZERO_ARGUMENT_OPTIONS.add("-proceedOnError:Fatal");
        Options.ZERO_ARGUMENT_OPTIONS.add("-time");
        Options.ZERO_ARGUMENT_OPTIONS.add("-v");
        Options.ZERO_ARGUMENT_OPTIONS.add("-version");
        Options.ZERO_ARGUMENT_OPTIONS.add("-showversion");
        Options.ZERO_ARGUMENT_OPTIONS.add("-deprecation");
        Options.ZERO_ARGUMENT_OPTIONS.add("-help");
        Options.ZERO_ARGUMENT_OPTIONS.add("-?");
        Options.ZERO_ARGUMENT_OPTIONS.add("-help:warn");
        Options.ZERO_ARGUMENT_OPTIONS.add("-?:warn");
        Options.ZERO_ARGUMENT_OPTIONS.add("-noExit");
        Options.ZERO_ARGUMENT_OPTIONS.add("-verbose");
        Options.ZERO_ARGUMENT_OPTIONS.add("-referenceInfo");
        Options.ZERO_ARGUMENT_OPTIONS.add("-inlineJSR");
        Options.ZERO_ARGUMENT_OPTIONS.add("-g");
        Options.ZERO_ARGUMENT_OPTIONS.add("-g:none");
        Options.ZERO_ARGUMENT_OPTIONS.add("-warn:none");
        Options.ZERO_ARGUMENT_OPTIONS.add("-preserveAllLocals");
        Options.ZERO_ARGUMENT_OPTIONS.add("-enableJavadoc");
        Options.ZERO_ARGUMENT_OPTIONS.add("-Xemacs");
        Options.ZERO_ARGUMENT_OPTIONS.add("-X");
        Options.ZERO_ARGUMENT_OPTIONS.add("-O");
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.3");
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.4");
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.5");
        Options.ZERO_ARGUMENT_OPTIONS.add("-5");
        Options.ZERO_ARGUMENT_OPTIONS.add("-5.0");
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.6");
        Options.ZERO_ARGUMENT_OPTIONS.add("-6");
        Options.ZERO_ARGUMENT_OPTIONS.add("-6.0");
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.7");
        Options.ZERO_ARGUMENT_OPTIONS.add("-7");
        Options.ZERO_ARGUMENT_OPTIONS.add("-7.0");
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.8");
        Options.ZERO_ARGUMENT_OPTIONS.add("-8");
        Options.ZERO_ARGUMENT_OPTIONS.add("-8.0");
        Options.ZERO_ARGUMENT_OPTIONS.add("-proc:only");
        Options.ZERO_ARGUMENT_OPTIONS.add("-proc:none");
        Options.ZERO_ARGUMENT_OPTIONS.add("-XprintProcessorInfo");
        Options.ZERO_ARGUMENT_OPTIONS.add("-XprintRounds");
        Options.ZERO_ARGUMENT_OPTIONS.add("-parameters");
        Options.ZERO_ARGUMENT_OPTIONS.add("-genericsignature");
        (FILE_MANAGER_OPTIONS = new HashSet<String>()).add("-bootclasspath");
        Options.FILE_MANAGER_OPTIONS.add("-encoding");
        Options.FILE_MANAGER_OPTIONS.add("-d");
        Options.FILE_MANAGER_OPTIONS.add("-classpath");
        Options.FILE_MANAGER_OPTIONS.add("-cp");
        Options.FILE_MANAGER_OPTIONS.add("-sourcepath");
        Options.FILE_MANAGER_OPTIONS.add("-extdirs");
        Options.FILE_MANAGER_OPTIONS.add("-endorseddirs");
        Options.FILE_MANAGER_OPTIONS.add("-s");
        Options.FILE_MANAGER_OPTIONS.add("-processorpath");
        (ONE_ARGUMENT_OPTIONS = new HashSet<String>()).addAll(Options.FILE_MANAGER_OPTIONS);
        Options.ONE_ARGUMENT_OPTIONS.add("-log");
        Options.ONE_ARGUMENT_OPTIONS.add("-repeat");
        Options.ONE_ARGUMENT_OPTIONS.add("-maxProblems");
        Options.ONE_ARGUMENT_OPTIONS.add("-source");
        Options.ONE_ARGUMENT_OPTIONS.add("-target");
        Options.ONE_ARGUMENT_OPTIONS.add("-processor");
        Options.ONE_ARGUMENT_OPTIONS.add("-classNames");
        Options.ONE_ARGUMENT_OPTIONS.add("-properties");
    }
    
    public static int processOptionsFileManager(final String option) {
        if (option == null) {
            return -1;
        }
        if (Options.FILE_MANAGER_OPTIONS.contains(option)) {
            return 1;
        }
        return -1;
    }
    
    public static int processOptions(final String option) {
        if (option == null) {
            return -1;
        }
        if (Options.ZERO_ARGUMENT_OPTIONS.contains(option)) {
            return 0;
        }
        if (Options.ONE_ARGUMENT_OPTIONS.contains(option)) {
            return 1;
        }
        if (option.startsWith("-g")) {
            final int length = option.length();
            if (length > 3) {
                final StringTokenizer tokenizer = new StringTokenizer(option.substring(3, option.length()), ",");
                while (tokenizer.hasMoreTokens()) {
                    final String token = tokenizer.nextToken();
                    if (!"vars".equals(token) && !"lines".equals(token)) {
                        if ("source".equals(token)) {
                            continue;
                        }
                        return -1;
                    }
                }
                return 0;
            }
            return -1;
        }
        else if (option.startsWith("-warn")) {
            final int length = option.length();
            if (length <= 6) {
                return -1;
            }
            int warnTokenStart = 0;
            switch (option.charAt(6)) {
                case '+': {
                    warnTokenStart = 7;
                    break;
                }
                case '-': {
                    warnTokenStart = 7;
                    break;
                }
                default: {
                    warnTokenStart = 6;
                    break;
                }
            }
            final StringTokenizer tokenizer2 = new StringTokenizer(option.substring(warnTokenStart, option.length()), ",");
            int tokenCounter = 0;
            while (tokenizer2.hasMoreTokens()) {
                final String token2 = tokenizer2.nextToken();
                ++tokenCounter;
                if (!token2.equals("allDeadCode") && !token2.equals("allDeprecation") && !token2.equals("allJavadoc") && !token2.equals("allOver-ann") && !token2.equals("assertIdentifier") && !token2.equals("boxing") && !token2.equals("charConcat") && !token2.equals("compareIdentical") && !token2.equals("conditionAssign") && !token2.equals("constructorName") && !token2.equals("deadCode") && !token2.equals("dep-ann") && !token2.equals("deprecation") && !token2.equals("discouraged") && !token2.equals("emptyBlock") && !token2.equals("enumIdentifier") && !token2.equals("enumSwitch") && !token2.equals("fallthrough") && !token2.equals("fieldHiding") && !token2.equals("finalBound") && !token2.equals("finally") && !token2.equals("forbidden") && !token2.equals("hashCode") && !token2.equals("hiding") && !token2.equals("includeAssertNull") && !token2.equals("incomplete-switch") && !token2.equals("indirectStatic") && !token2.equals("interfaceNonInherited") && !token2.equals("intfAnnotation") && !token2.equals("intfNonInherited") && !token2.equals("intfRedundant") && !token2.equals("javadoc") && !token2.equals("localHiding") && !token2.equals("maskedCatchBlock") && !token2.equals("maskedCatchBlocks") && !token2.equals("nls") && !token2.equals("noEffectAssign") && !token2.equals("noImplicitStringConversion") && !token2.equals("null") && !token2.equals("nullDereference") && !token2.equals("over-ann") && !token2.equals("packageDefaultMethod") && !token2.equals("paramAssign") && !token2.equals("pkgDefaultMethod") && !token2.equals("raw") && !token2.equals("semicolon") && !token2.equals("serial") && !token2.equals("specialParamHiding") && !token2.equals("static-access") && !token2.equals("staticReceiver") && !token2.equals("super") && !token2.equals("suppress") && !token2.equals("syncOverride") && !token2.equals("synthetic-access") && !token2.equals("syntheticAccess") && !token2.equals("typeHiding") && !token2.equals("unchecked") && !token2.equals("unnecessaryElse") && !token2.equals("unnecessaryOperator") && !token2.equals("unqualified-field-access") && !token2.equals("unqualifiedField") && !token2.equals("unsafe") && !token2.equals("unused") && !token2.equals("unusedArgument") && !token2.equals("unusedArguments") && !token2.equals("unusedImport") && !token2.equals("unusedImports") && !token2.equals("unusedLabel") && !token2.equals("unusedLocal") && !token2.equals("unusedLocals") && !token2.equals("unusedPrivate") && !token2.equals("unusedThrown") && !token2.equals("unusedTypeArgs") && !token2.equals("uselessTypeCheck") && !token2.equals("varargsCast")) {
                    if (token2.equals("warningToken")) {
                        continue;
                    }
                    if (!token2.equals("tasks")) {
                        return -1;
                    }
                    String taskTags = "";
                    final int start = token2.indexOf(40);
                    final int end = token2.indexOf(41);
                    if (start >= 0 && end >= 0 && start < end) {
                        taskTags = token2.substring(start + 1, end).trim();
                        taskTags = taskTags.replace('|', ',');
                    }
                    if (taskTags.length() == 0) {
                        return -1;
                    }
                    continue;
                }
            }
            if (tokenCounter == 0) {
                return -1;
            }
            return 0;
        }
        else if (option.startsWith("-nowarn")) {
            switch (option.length()) {
                case 7: {
                    return 0;
                }
                case 8: {
                    return -1;
                }
                default: {
                    final int foldersStart = option.indexOf(91) + 1;
                    final int foldersEnd = option.lastIndexOf(93);
                    if (foldersStart <= 8 || foldersEnd == -1 || foldersStart > foldersEnd || foldersEnd < option.length() - 1) {
                        return -1;
                    }
                    final String folders = option.substring(foldersStart, foldersEnd);
                    if (folders.length() > 0) {
                        return 0;
                    }
                    return -1;
                }
            }
        }
        else {
            if (option.startsWith("-J") || option.startsWith("-X") || option.startsWith("-A")) {
                return 0;
            }
            return -1;
        }
    }
}
