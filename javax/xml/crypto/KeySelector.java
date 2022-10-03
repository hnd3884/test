package javax.xml.crypto;

import java.security.Key;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

public abstract class KeySelector
{
    protected KeySelector() {
    }
    
    public abstract KeySelectorResult select(final KeyInfo p0, final Purpose p1, final AlgorithmMethod p2, final XMLCryptoContext p3) throws KeySelectorException;
    
    public static KeySelector singletonKeySelector(final Key key) {
        return new SingletonKeySelector(key);
    }
    
    public static class Purpose
    {
        private final String name;
        public static final Purpose SIGN;
        public static final Purpose VERIFY;
        public static final Purpose ENCRYPT;
        public static final Purpose DECRYPT;
        
        private Purpose(final String name) {
            this.name = name;
        }
        
        public String toString() {
            return this.name;
        }
        
        static {
            SIGN = new Purpose("sign");
            VERIFY = new Purpose("verify");
            ENCRYPT = new Purpose("encrypt");
            DECRYPT = new Purpose("decrypt");
        }
    }
    
    private static class SingletonKeySelector extends KeySelector
    {
        private final Key key;
        
        SingletonKeySelector(final Key key) {
            if (key == null) {
                throw new NullPointerException();
            }
            this.key = key;
        }
        
        public KeySelectorResult select(final KeyInfo keyInfo, final Purpose purpose, final AlgorithmMethod algorithmMethod, final XMLCryptoContext xmlCryptoContext) throws KeySelectorException {
            return new KeySelectorResult() {
                public Key getKey() {
                    return SingletonKeySelector.this.key;
                }
            };
        }
    }
}
