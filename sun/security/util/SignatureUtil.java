package sun.security.util;

import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import sun.misc.SharedSecrets;
import java.security.PublicKey;
import sun.security.rsa.RSAUtil;
import java.security.spec.ECParameterSpec;
import java.util.Locale;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.AlgorithmParameters;
import java.security.ProviderException;
import java.security.Signature;

public class SignatureUtil
{
    private static String checkName(final String s) throws ProviderException {
        if (s.indexOf(".") == -1) {
            return s;
        }
        try {
            return Signature.getInstance(s).getAlgorithm();
        }
        catch (final Exception ex) {
            throw new ProviderException("Error mapping algorithm name", ex);
        }
    }
    
    private static AlgorithmParameters createAlgorithmParameters(String checkName, final byte[] array) throws ProviderException {
        try {
            checkName = checkName(checkName);
            final AlgorithmParameters instance = AlgorithmParameters.getInstance(checkName);
            instance.init(array);
            return instance;
        }
        catch (final NoSuchAlgorithmException | IOException ex) {
            throw new ProviderException((Throwable)ex);
        }
    }
    
    public static AlgorithmParameterSpec getParamSpec(String upperCase, AlgorithmParameters algorithmParameters) throws ProviderException {
        upperCase = checkName(upperCase).toUpperCase(Locale.ENGLISH);
        AlgorithmParameterSpec algorithmParameterSpec = null;
        if (algorithmParameters != null) {
            if (algorithmParameters.getAlgorithm().indexOf(".") != -1) {
                try {
                    algorithmParameters = createAlgorithmParameters(upperCase, algorithmParameters.getEncoded());
                }
                catch (final IOException ex) {
                    throw new ProviderException(ex);
                }
            }
            if (upperCase.indexOf("RSA") == -1) {
                if (upperCase.indexOf("ECDSA") != -1) {
                    try {
                        algorithmParameterSpec = algorithmParameters.getParameterSpec(ECParameterSpec.class);
                        return algorithmParameterSpec;
                    }
                    catch (final Exception ex2) {
                        throw new ProviderException("Error handling EC parameters", ex2);
                    }
                }
                throw new ProviderException("Unrecognized algorithm for signature parameters " + upperCase);
            }
            algorithmParameterSpec = RSAUtil.getParamSpec(algorithmParameters);
        }
        return algorithmParameterSpec;
    }
    
    public static AlgorithmParameterSpec getParamSpec(String upperCase, final byte[] array) throws ProviderException {
        upperCase = checkName(upperCase).toUpperCase(Locale.ENGLISH);
        AlgorithmParameterSpec algorithmParameterSpec = null;
        if (array != null) {
            if (upperCase.indexOf("RSA") != -1) {
                algorithmParameterSpec = RSAUtil.getParamSpec(createAlgorithmParameters(upperCase, array));
            }
            else {
                if (upperCase.indexOf("ECDSA") == -1) {
                    throw new ProviderException("Unrecognized algorithm for signature parameters " + upperCase);
                }
                try {
                    algorithmParameterSpec = ECUtil.getECParameterSpec(Signature.getInstance(upperCase).getProvider(), array);
                }
                catch (final Exception ex) {
                    throw new ProviderException("Error handling EC parameters", ex);
                }
                if (algorithmParameterSpec == null) {
                    throw new ProviderException("Error handling EC parameters");
                }
            }
        }
        return algorithmParameterSpec;
    }
    
    public static void initVerifyWithParam(final Signature signature, final PublicKey publicKey, final AlgorithmParameterSpec algorithmParameterSpec) throws ProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        SharedSecrets.getJavaSecuritySignatureAccess().initVerify(signature, publicKey, algorithmParameterSpec);
    }
    
    public static void initVerifyWithParam(final Signature signature, final Certificate certificate, final AlgorithmParameterSpec algorithmParameterSpec) throws ProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        SharedSecrets.getJavaSecuritySignatureAccess().initVerify(signature, certificate, algorithmParameterSpec);
    }
    
    public static void initSignWithParam(final Signature signature, final PrivateKey privateKey, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws ProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        SharedSecrets.getJavaSecuritySignatureAccess().initSign(signature, privateKey, algorithmParameterSpec, secureRandom);
    }
}
