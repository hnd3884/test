package com.unboundid.ldap.sdk.examples;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.CommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class IndentLDAPFilter extends CommandLineTool
{
    private static final int WRAP_COLUMN;
    private static final String ARG_INDENT_SPACES = "indent-spaces";
    private static final String ARG_DO_NOT_SIMPLIFY = "do-not-simplify";
    private ArgumentParser parser;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final OutputStream out, final OutputStream err, final String... args) {
        final IndentLDAPFilter indentLDAPFilter = new IndentLDAPFilter(out, err);
        return indentLDAPFilter.runTool(args);
    }
    
    public IndentLDAPFilter(final OutputStream out, final OutputStream err) {
        super(out, err);
        this.parser = null;
    }
    
    @Override
    public String getToolName() {
        return "indent-ldap-filter";
    }
    
    @Override
    public String getToolDescription() {
        return "Parses a provided LDAP filter string and displays it a multi-line form that makes it easier to understand its hierarchy and embedded components.  If possible, it may also be able to simplify the provided filter in certain ways (for example, by removing unnecessary levels of hierarchy, like an AND embedded in an AND).";
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public int getMinTrailingArguments() {
        return 1;
    }
    
    @Override
    public int getMaxTrailingArguments() {
        return 1;
    }
    
    @Override
    public String getTrailingArgumentsPlaceholder() {
        return "{filter}";
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
        return true;
    }
    
    @Override
    public void addToolArguments(final ArgumentParser parser) throws ArgumentException {
        this.parser = parser;
        final IntegerArgument indentColumnsArg = new IntegerArgument(null, "indent-spaces", false, 1, "{numSpaces}", "Specifies the number of spaces that should be used to indent each additional level of filter hierarchy.  A value of zero indicates that the hierarchy should be displayed without any additional indenting.  If this argument is not provided, a default indent of two spaces will be used.", 0, Integer.MAX_VALUE, 2);
        indentColumnsArg.addLongIdentifier("indentSpaces", true);
        indentColumnsArg.addLongIdentifier("indent-columns", true);
        indentColumnsArg.addLongIdentifier("indentColumns", true);
        indentColumnsArg.addLongIdentifier("indent", true);
        parser.addArgument(indentColumnsArg);
        final BooleanArgument doNotSimplifyArg = new BooleanArgument(null, "do-not-simplify", 1, "Indicates that the tool should not make any attempt to simplify the provided filter.  If this argument is not provided, then the tool will try to simplify the provided filter (for example, by removing unnecessary levels of hierarchy, like an AND embedded in an AND).");
        doNotSimplifyArg.addLongIdentifier("doNotSimplify", true);
        doNotSimplifyArg.addLongIdentifier("do-not-simplify-filter", true);
        doNotSimplifyArg.addLongIdentifier("doNotSimplifyFilter", true);
        doNotSimplifyArg.addLongIdentifier("dont-simplify", true);
        doNotSimplifyArg.addLongIdentifier("dontSimplify", true);
        doNotSimplifyArg.addLongIdentifier("dont-simplify-filter", true);
        doNotSimplifyArg.addLongIdentifier("dontSimplifyFilter", true);
        parser.addArgument(doNotSimplifyArg);
    }
    
    @Override
    public ResultCode doToolProcessing() {
        Filter filter;
        try {
            filter = Filter.create(this.parser.getTrailingArguments().get(0));
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            this.wrapErr(0, IndentLDAPFilter.WRAP_COLUMN, "ERROR:  Unable to parse the provided filter string:  " + StaticUtils.getExceptionMessage(e));
            return e.getResultCode();
        }
        final int indentSpaces = this.parser.getIntegerArgument("indent-spaces").getValue();
        final char[] indentChars = new char[indentSpaces];
        Arrays.fill(indentChars, ' ');
        final String indentString = new String(indentChars);
        final List<String> indentedFilterLines = new ArrayList<String>(10);
        indentLDAPFilter(filter, "", indentString, indentedFilterLines);
        for (final String line : indentedFilterLines) {
            this.out(line);
        }
        if (!this.parser.getBooleanArgument("do-not-simplify").isPresent()) {
            this.out(new Object[0]);
            final Filter simplifiedFilter = Filter.simplifyFilter(filter, false);
            if (simplifiedFilter.equals(filter)) {
                this.wrapOut(0, IndentLDAPFilter.WRAP_COLUMN, "The provided filter cannot be simplified.");
            }
            else {
                this.wrapOut(0, IndentLDAPFilter.WRAP_COLUMN, "The provided filter can be simplified to:");
                this.out(new Object[0]);
                this.out("     ", simplifiedFilter.toString());
                this.out(new Object[0]);
                this.wrapOut(0, IndentLDAPFilter.WRAP_COLUMN, "An indented representation of the simplified filter:");
                this.out(new Object[0]);
                indentedFilterLines.clear();
                indentLDAPFilter(simplifiedFilter, "", indentString, indentedFilterLines);
                for (final String line2 : indentedFilterLines) {
                    this.out(line2);
                }
            }
        }
        return ResultCode.SUCCESS;
    }
    
    public static void indentLDAPFilter(final Filter filter, final String currentIndentString, final String indentSpaces, final List<String> indentedFilterLines) {
        switch (filter.getFilterType()) {
            case -96: {
                final Filter[] andComponents = filter.getComponents();
                if (andComponents.length == 0) {
                    indentedFilterLines.add(currentIndentString + "(&)");
                    break;
                }
                indentedFilterLines.add(currentIndentString + "(&");
                final String andComponentIndent = currentIndentString + " &" + indentSpaces;
                for (final Filter andComponent : andComponents) {
                    indentLDAPFilter(andComponent, andComponentIndent, indentSpaces, indentedFilterLines);
                }
                indentedFilterLines.add(currentIndentString + " &)");
                break;
            }
            case -95: {
                final Filter[] orComponents = filter.getComponents();
                if (orComponents.length == 0) {
                    indentedFilterLines.add(currentIndentString + "(|)");
                    break;
                }
                indentedFilterLines.add(currentIndentString + "(|");
                final String orComponentIndent = currentIndentString + " |" + indentSpaces;
                for (final Filter orComponent : orComponents) {
                    indentLDAPFilter(orComponent, orComponentIndent, indentSpaces, indentedFilterLines);
                }
                indentedFilterLines.add(currentIndentString + " |)");
                break;
            }
            case -94: {
                indentedFilterLines.add(currentIndentString + "(!");
                indentLDAPFilter(filter.getNOTComponent(), currentIndentString + " !" + indentSpaces, indentSpaces, indentedFilterLines);
                indentedFilterLines.add(currentIndentString + " !)");
                break;
            }
            default: {
                indentedFilterLines.add(currentIndentString + filter.toString());
                break;
            }
        }
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        examples.put(new String[] { "(|(givenName=jdoe)(|(sn=jdoe)(|(cn=jdoe)(|(uid=jdoe)(mail=jdoe)))))" }, "Displays an indented representation of the provided filter, as well as a simplified version of that filter.");
        return examples;
    }
    
    static {
        WRAP_COLUMN = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
    }
}
