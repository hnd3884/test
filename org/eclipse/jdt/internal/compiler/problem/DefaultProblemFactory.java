package org.eclipse.jdt.internal.compiler.problem;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import java.util.Locale;
import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;
import org.eclipse.jdt.internal.compiler.IProblemFactory;

public class DefaultProblemFactory implements IProblemFactory
{
    public HashtableOfInt messageTemplates;
    private Locale locale;
    private static HashtableOfInt DEFAULT_LOCALE_TEMPLATES;
    private static final char[] DOUBLE_QUOTES;
    private static final char[] SINGLE_QUOTE;
    private static final char[] FIRST_ARGUMENT;
    
    static {
        DOUBLE_QUOTES = "''".toCharArray();
        SINGLE_QUOTE = "'".toCharArray();
        FIRST_ARGUMENT = "{0}".toCharArray();
    }
    
    public DefaultProblemFactory() {
        this(Locale.getDefault());
    }
    
    public DefaultProblemFactory(final Locale loc) {
        this.setLocale(loc);
    }
    
    @Override
    public CategorizedProblem createProblem(final char[] originatingFileName, final int problemId, final String[] problemArguments, final String[] messageArguments, final int severity, final int startPosition, final int endPosition, final int lineNumber, final int columnNumber) {
        return new DefaultProblem(originatingFileName, this.getLocalizedMessage(problemId, messageArguments), problemId, problemArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
    }
    
    @Override
    public CategorizedProblem createProblem(final char[] originatingFileName, final int problemId, final String[] problemArguments, final int elaborationId, final String[] messageArguments, final int severity, final int startPosition, final int endPosition, final int lineNumber, final int columnNumber) {
        return new DefaultProblem(originatingFileName, this.getLocalizedMessage(problemId, elaborationId, messageArguments), problemId, problemArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
    }
    
    private static final int keyFromID(final int id) {
        return id + 1;
    }
    
    @Override
    public Locale getLocale() {
        return this.locale;
    }
    
    public void setLocale(final Locale locale) {
        if (locale == this.locale) {
            return;
        }
        this.locale = locale;
        if (Locale.getDefault().equals(locale)) {
            if (DefaultProblemFactory.DEFAULT_LOCALE_TEMPLATES == null) {
                DefaultProblemFactory.DEFAULT_LOCALE_TEMPLATES = loadMessageTemplates(locale);
            }
            this.messageTemplates = DefaultProblemFactory.DEFAULT_LOCALE_TEMPLATES;
        }
        else {
            this.messageTemplates = loadMessageTemplates(locale);
        }
    }
    
    @Override
    public final String getLocalizedMessage(final int id, final String[] problemArguments) {
        return this.getLocalizedMessage(id, 0, problemArguments);
    }
    
    @Override
    public final String getLocalizedMessage(final int id, final int elaborationId, final String[] problemArguments) {
        final String rawMessage = (String)this.messageTemplates.get(keyFromID(id & 0xFFFFFF));
        if (rawMessage == null) {
            return "Unable to retrieve the error message for problem id: " + (id & 0xFFFFFF) + ". Check compiler resources.";
        }
        char[] message = rawMessage.toCharArray();
        if (elaborationId != 0) {
            final String elaboration = (String)this.messageTemplates.get(keyFromID(elaborationId));
            if (elaboration == null) {
                return "Unable to retrieve the error message elaboration for elaboration id: " + elaborationId + ". Check compiler resources.";
            }
            message = CharOperation.replace(message, DefaultProblemFactory.FIRST_ARGUMENT, elaboration.toCharArray());
        }
        message = CharOperation.replace(message, DefaultProblemFactory.DOUBLE_QUOTES, DefaultProblemFactory.SINGLE_QUOTE);
        if (problemArguments == null) {
            return new String(message);
        }
        final int length = message.length;
        int start = 0;
        int end = length;
        StringBuffer output = null;
        if ((id & Integer.MIN_VALUE) != 0x0) {
            output = new StringBuffer(10 + length + problemArguments.length * 20);
            output.append((String)this.messageTemplates.get(keyFromID(514)));
        }
        while ((end = CharOperation.indexOf('{', message, start)) > -1) {
            if (output == null) {
                output = new StringBuffer(length + problemArguments.length * 20);
            }
            output.append(message, start, end - start);
            if ((start = CharOperation.indexOf('}', message, end + 1)) <= -1) {
                output.append(message, end, length);
                return new String(output.toString());
            }
            try {
                output.append(problemArguments[CharOperation.parseInt(message, end + 1, start - end - 1)]);
            }
            catch (final NumberFormatException ex) {
                output.append(message, end + 1, start - end);
            }
            catch (final ArrayIndexOutOfBoundsException ex2) {
                return "Cannot bind message for problem (id: " + (id & 0xFFFFFF) + ") \"" + new String(message) + "\" with arguments: {" + Util.toString(problemArguments) + "}";
            }
            ++start;
        }
        if (output == null) {
            return new String(message);
        }
        output.append(message, start, length - start);
        return new String(output.toString());
    }
    
    public final String localizedMessage(final CategorizedProblem problem) {
        return this.getLocalizedMessage(problem.getID(), problem.getArguments());
    }
    
    public static HashtableOfInt loadMessageTemplates(final Locale loc) {
        ResourceBundle bundle = null;
        final String bundleName = "org.eclipse.jdt.internal.compiler.problem.messages";
        try {
            bundle = ResourceBundle.getBundle(bundleName, loc);
        }
        catch (final MissingResourceException e) {
            System.out.println("Missing resource : " + bundleName.replace('.', '/') + ".properties for locale " + loc);
            throw e;
        }
        final HashtableOfInt templates = new HashtableOfInt(700);
        final Enumeration keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            try {
                final int messageID = Integer.parseInt(key);
                templates.put(keyFromID(messageID), bundle.getString(key));
            }
            catch (final NumberFormatException ex) {}
            catch (final MissingResourceException ex2) {}
        }
        return templates;
    }
}
