package com.sun.xml.internal.fastinfoset.tools;

import java.io.IOException;
import org.xml.sax.SAXException;
import java.net.URISyntaxException;
import java.io.File;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import java.io.FileOutputStream;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.net.URI;

public abstract class TransformInputOutput
{
    private static URI currentJavaWorkingDirectory;
    
    public void parse(final String[] args) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        if (args.length == 0) {
            in = new BufferedInputStream(System.in);
            out = new BufferedOutputStream(System.out);
        }
        else if (args.length == 1) {
            in = new BufferedInputStream(new FileInputStream(args[0]));
            out = new BufferedOutputStream(System.out);
        }
        else {
            if (args.length != 2) {
                throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.optinalFileNotSpecified"));
            }
            in = new BufferedInputStream(new FileInputStream(args[0]));
            out = new BufferedOutputStream(new FileOutputStream(args[1]));
        }
        this.parse(in, out);
    }
    
    public abstract void parse(final InputStream p0, final OutputStream p1) throws Exception;
    
    public void parse(final InputStream in, final OutputStream out, final String workingDirectory) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    protected static EntityResolver createRelativePathResolver(final String workingDirectory) {
        return new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
                if (systemId != null && systemId.startsWith("file:/")) {
                    final URI workingDirectoryURI = new File(workingDirectory).toURI();
                    try {
                        final URI workingFile = convertToNewWorkingDirectory(TransformInputOutput.currentJavaWorkingDirectory, workingDirectoryURI, new File(new URI(systemId)).toURI());
                        return new InputSource(workingFile.toString());
                    }
                    catch (final URISyntaxException ex) {}
                }
                return null;
            }
        };
    }
    
    private static URI convertToNewWorkingDirectory(final URI oldwd, final URI newwd, final URI file) throws IOException, URISyntaxException {
        final String oldwdStr = oldwd.toString();
        final String newwdStr = newwd.toString();
        final String fileStr = file.toString();
        String cmpStr = null;
        if (fileStr.startsWith(oldwdStr) && (cmpStr = fileStr.substring(oldwdStr.length())).indexOf(47) == -1) {
            return new URI(newwdStr + '/' + cmpStr);
        }
        final String[] oldwdSplit = oldwdStr.split("/");
        final String[] newwdSplit = newwdStr.split("/");
        String[] fileSplit;
        int diff;
        for (fileSplit = fileStr.split("/"), diff = 0; diff < oldwdSplit.length && diff < fileSplit.length && oldwdSplit[diff].equals(fileSplit[diff]); ++diff) {}
        int diffNew;
        for (diffNew = 0; diffNew < newwdSplit.length && diffNew < fileSplit.length && newwdSplit[diffNew].equals(fileSplit[diffNew]); ++diffNew) {}
        if (diffNew > diff) {
            return file;
        }
        final int elemsToSub = oldwdSplit.length - diff;
        final StringBuffer resultStr = new StringBuffer(100);
        for (int i = 0; i < newwdSplit.length - elemsToSub; ++i) {
            resultStr.append(newwdSplit[i]);
            resultStr.append('/');
        }
        for (int i = diff; i < fileSplit.length; ++i) {
            resultStr.append(fileSplit[i]);
            if (i < fileSplit.length - 1) {
                resultStr.append('/');
            }
        }
        return new URI(resultStr.toString());
    }
    
    static {
        TransformInputOutput.currentJavaWorkingDirectory = new File(System.getProperty("user.dir")).toURI();
    }
}
