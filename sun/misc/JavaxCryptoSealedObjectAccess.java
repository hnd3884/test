package sun.misc;

import java.io.IOException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.io.ObjectInputStream;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;

public interface JavaxCryptoSealedObjectAccess
{
    ObjectInputStream getExtObjectInputStream(final SealedObject p0, final Cipher p1) throws BadPaddingException, IllegalBlockSizeException, IOException;
}
