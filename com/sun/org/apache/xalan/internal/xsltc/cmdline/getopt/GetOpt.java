package com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class GetOpt
{
    private Option theCurrentOption;
    private ListIterator theOptionsIterator;
    private List theOptions;
    private List theCmdArgs;
    private OptionMatcher theOptionMatcher;
    
    public GetOpt(final String[] args, final String optString) {
        this.theCurrentOption = null;
        this.theOptions = null;
        this.theCmdArgs = null;
        this.theOptionMatcher = null;
        this.theOptions = new ArrayList();
        int currOptIndex = 0;
        this.theCmdArgs = new ArrayList();
        this.theOptionMatcher = new OptionMatcher(optString);
        for (int i = 0; i < args.length; ++i) {
            final String token = args[i];
            final int tokenLength = token.length();
            if (token.equals("--")) {
                currOptIndex = i + 1;
                break;
            }
            if (token.startsWith("-") && tokenLength == 2) {
                this.theOptions.add(new Option(token.charAt(1)));
            }
            else if (token.startsWith("-") && tokenLength > 2) {
                for (int j = 1; j < tokenLength; ++j) {
                    this.theOptions.add(new Option(token.charAt(j)));
                }
            }
            else if (!token.startsWith("-")) {
                if (this.theOptions.size() == 0) {
                    currOptIndex = i;
                    break;
                }
                int indexoflast = 0;
                indexoflast = this.theOptions.size() - 1;
                final Option op = this.theOptions.get(indexoflast);
                final char opLetter = op.getArgLetter();
                if (op.hasArg() || !this.theOptionMatcher.hasArg(opLetter)) {
                    currOptIndex = i;
                    break;
                }
                op.setArg(token);
            }
        }
        this.theOptionsIterator = this.theOptions.listIterator();
        for (int i = currOptIndex; i < args.length; ++i) {
            final String token = args[i];
            this.theCmdArgs.add(token);
        }
    }
    
    public void printOptions() {
        final ListIterator it = this.theOptions.listIterator();
        while (it.hasNext()) {
            final Option opt = it.next();
            System.out.print("OPT =" + opt.getArgLetter());
            final String arg = opt.getArgument();
            if (arg != null) {
                System.out.print(" " + arg);
            }
            System.out.println();
        }
    }
    
    public int getNextOption() throws IllegalArgumentException, MissingOptArgException {
        int retval = -1;
        if (this.theOptionsIterator.hasNext()) {
            this.theCurrentOption = this.theOptionsIterator.next();
            final char c = this.theCurrentOption.getArgLetter();
            final boolean shouldHaveArg = this.theOptionMatcher.hasArg(c);
            final String arg = this.theCurrentOption.getArgument();
            if (!this.theOptionMatcher.match(c)) {
                final ErrorMsg msg = new ErrorMsg("ILLEGAL_CMDLINE_OPTION_ERR", new Character(c));
                throw new IllegalArgumentException(msg.toString());
            }
            if (shouldHaveArg && arg == null) {
                final ErrorMsg msg = new ErrorMsg("CMDLINE_OPT_MISSING_ARG_ERR", new Character(c));
                throw new MissingOptArgException(msg.toString());
            }
            retval = c;
        }
        return retval;
    }
    
    public String getOptionArg() {
        String retval = null;
        final String tmp = this.theCurrentOption.getArgument();
        final char c = this.theCurrentOption.getArgLetter();
        if (this.theOptionMatcher.hasArg(c)) {
            retval = tmp;
        }
        return retval;
    }
    
    public String[] getCmdArgs() {
        final String[] retval = new String[this.theCmdArgs.size()];
        int i = 0;
        final ListIterator it = this.theCmdArgs.listIterator();
        while (it.hasNext()) {
            retval[i++] = it.next();
        }
        return retval;
    }
    
    class Option
    {
        private char theArgLetter;
        private String theArgument;
        
        public Option(final char argLetter) {
            this.theArgument = null;
            this.theArgLetter = argLetter;
        }
        
        public void setArg(final String arg) {
            this.theArgument = arg;
        }
        
        public boolean hasArg() {
            return this.theArgument != null;
        }
        
        public char getArgLetter() {
            return this.theArgLetter;
        }
        
        public String getArgument() {
            return this.theArgument;
        }
    }
    
    class OptionMatcher
    {
        private String theOptString;
        
        public OptionMatcher(final String optString) {
            this.theOptString = null;
            this.theOptString = optString;
        }
        
        public boolean match(final char c) {
            boolean retval = false;
            if (this.theOptString.indexOf(c) != -1) {
                retval = true;
            }
            return retval;
        }
        
        public boolean hasArg(final char c) {
            boolean retval = false;
            final int index = this.theOptString.indexOf(c) + 1;
            if (index == this.theOptString.length()) {
                retval = false;
            }
            else if (this.theOptString.charAt(index) == ':') {
                retval = true;
            }
            return retval;
        }
    }
}
