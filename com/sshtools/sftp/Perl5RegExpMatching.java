package com.sshtools.sftp;

import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternCompiler;
import com.maverick.sftp.SftpStatusException;
import com.maverick.sftp.SftpFile;
import org.apache.oro.text.regex.Pattern;
import java.util.Vector;
import org.apache.oro.text.regex.MalformedPatternException;
import com.maverick.ssh.SshException;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Compiler;
import java.io.File;

public class Perl5RegExpMatching implements RegularExpressionMatching
{
    public String[] matchFileNamesWithPattern(final File[] array, final String s) throws SshException {
        final Perl5Compiler perl5Compiler = new Perl5Compiler();
        final Perl5Matcher perl5Matcher = new Perl5Matcher();
        Pattern compile;
        try {
            compile = ((PatternCompiler)perl5Compiler).compile(s);
        }
        catch (final MalformedPatternException ex) {
            throw new SshException("Invalid regular expression:" + ((Throwable)ex).getMessage(), 4);
        }
        final Vector vector = new Vector();
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].getName().equals(".") && !array[i].getName().equals("..") && !array[i].isDirectory() && ((PatternMatcher)perl5Matcher).matches(array[i].getName(), compile)) {
                vector.addElement(array[i].getName());
            }
        }
        final String[] array2 = new String[vector.size()];
        vector.copyInto(array2);
        return array2;
    }
    
    public SftpFile[] matchFilesWithPattern(final SftpFile[] array, final String s) throws SftpStatusException, SshException {
        final Perl5Compiler perl5Compiler = new Perl5Compiler();
        final Perl5Matcher perl5Matcher = new Perl5Matcher();
        Pattern compile;
        try {
            compile = ((PatternCompiler)perl5Compiler).compile(s);
        }
        catch (final MalformedPatternException ex) {
            throw new SshException("Invalid regular expression:" + ((Throwable)ex).getMessage(), 4);
        }
        final Vector vector = new Vector();
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].getFilename().equals(".") && !array[i].getFilename().equals("..") && !array[i].isDirectory() && ((PatternMatcher)perl5Matcher).matches(array[i].getFilename(), compile)) {
                vector.addElement(array[i]);
            }
        }
        final SftpFile[] array2 = new SftpFile[vector.size()];
        vector.copyInto(array2);
        return array2;
    }
}
