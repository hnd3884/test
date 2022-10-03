package sun.security.util;

import java.io.IOException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParameters;
import java.security.spec.ECParameterSpec;
import java.security.AlgorithmParametersSpi;

public final class ECParameters extends AlgorithmParametersSpi
{
    private NamedCurve namedCurve;
    
    public static AlgorithmParameters getAlgorithmParameters(final ECParameterSpec ecParameterSpec) throws InvalidKeyException {
        try {
            final AlgorithmParameters instance = AlgorithmParameters.getInstance("EC", "SunEC");
            instance.init(ecParameterSpec);
            return instance;
        }
        catch (final GeneralSecurityException ex) {
            throw new InvalidKeyException("EC parameters error", ex);
        }
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (algorithmParameterSpec == null) {
            throw new InvalidParameterSpecException("paramSpec must not be null");
        }
        if (algorithmParameterSpec instanceof NamedCurve) {
            this.namedCurve = (NamedCurve)algorithmParameterSpec;
            return;
        }
        if (algorithmParameterSpec instanceof ECParameterSpec) {
            this.namedCurve = CurveDB.lookup((ECParameterSpec)algorithmParameterSpec);
        }
        else if (algorithmParameterSpec instanceof ECGenParameterSpec) {
            this.namedCurve = CurveDB.lookup(((ECGenParameterSpec)algorithmParameterSpec).getName());
        }
        else {
            if (!(algorithmParameterSpec instanceof ECKeySizeParameterSpec)) {
                throw new InvalidParameterSpecException("Only ECParameterSpec and ECGenParameterSpec supported");
            }
            this.namedCurve = CurveDB.lookup(((ECKeySizeParameterSpec)algorithmParameterSpec).getKeySize());
        }
        if (this.namedCurve == null) {
            throw new InvalidParameterSpecException("Not a supported curve: " + algorithmParameterSpec);
        }
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        final DerValue derValue = new DerValue(array);
        if (derValue.tag != 6) {
            throw new IOException("Only named ECParameters supported");
        }
        final ObjectIdentifier oid = derValue.getOID();
        final NamedCurve lookup = CurveDB.lookup(oid.toString());
        if (lookup == null) {
            throw new IOException("Unknown named curve: " + oid);
        }
        this.namedCurve = lookup;
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        this.engineInit(array);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        if (clazz.isAssignableFrom(ECParameterSpec.class)) {
            return clazz.cast(this.namedCurve);
        }
        if (clazz.isAssignableFrom(ECGenParameterSpec.class)) {
            return clazz.cast(new ECGenParameterSpec(this.namedCurve.getObjectId()));
        }
        if (clazz.isAssignableFrom(ECKeySizeParameterSpec.class)) {
            return clazz.cast(new ECKeySizeParameterSpec(this.namedCurve.getCurve().getField().getFieldSize()));
        }
        throw new InvalidParameterSpecException("Only ECParameterSpec and ECGenParameterSpec supported");
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        return this.namedCurve.getEncoded();
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) throws IOException {
        return this.engineGetEncoded();
    }
    
    @Override
    protected String engineToString() {
        if (this.namedCurve == null) {
            return "Not initialized";
        }
        return this.namedCurve.toString();
    }
}
