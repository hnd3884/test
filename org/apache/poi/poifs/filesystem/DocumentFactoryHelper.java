package org.apache.poi.poifs.filesystem;

import org.apache.poi.util.Removal;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import java.io.IOException;
import java.io.FilterInputStream;
import java.io.InputStream;
import org.apache.poi.util.Internal;

@Internal
public final class DocumentFactoryHelper
{
    private DocumentFactoryHelper() {
    }
    
    public static InputStream getDecryptedStream(final POIFSFileSystem fs, final String password) throws IOException {
        return new FilterInputStream(getDecryptedStream(fs.getRoot(), password)) {
            @Override
            public void close() throws IOException {
                fs.close();
                super.close();
            }
        };
    }
    
    public static InputStream getDecryptedStream(final DirectoryNode root, final String password) throws IOException {
        final EncryptionInfo info = new EncryptionInfo(root);
        final Decryptor d = Decryptor.getInstance(info);
        try {
            boolean passwordCorrect = false;
            if (password != null && d.verifyPassword(password)) {
                passwordCorrect = true;
            }
            if (!passwordCorrect && d.verifyPassword("VelvetSweatshop")) {
                passwordCorrect = true;
            }
            if (passwordCorrect) {
                return d.getDataStream(root);
            }
            if (password != null) {
                throw new EncryptedDocumentException("Password incorrect");
            }
            throw new EncryptedDocumentException("The supplied spreadsheet is protected, but no password was supplied");
        }
        catch (final GeneralSecurityException e) {
            throw new IOException(e);
        }
    }
    
    @Deprecated
    @Removal(version = "4.0")
    public static boolean hasOOXMLHeader(final InputStream inp) throws IOException {
        return FileMagic.valueOf(inp) == FileMagic.OOXML;
    }
}
