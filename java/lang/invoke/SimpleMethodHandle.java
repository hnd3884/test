package java.lang.invoke;

final class SimpleMethodHandle extends BoundMethodHandle
{
    static final SpeciesData SPECIES_DATA;
    
    private SimpleMethodHandle(final MethodType methodType, final LambdaForm lambdaForm) {
        super(methodType, lambdaForm);
    }
    
    static BoundMethodHandle make(final MethodType methodType, final LambdaForm lambdaForm) {
        return new SimpleMethodHandle(methodType, lambdaForm);
    }
    
    public SpeciesData speciesData() {
        return SimpleMethodHandle.SPECIES_DATA;
    }
    
    @Override
    BoundMethodHandle copyWith(final MethodType methodType, final LambdaForm lambdaForm) {
        return make(methodType, lambdaForm);
    }
    
    @Override
    String internalProperties() {
        return "\n& Class=" + this.getClass().getSimpleName();
    }
    
    public int fieldCount() {
        return 0;
    }
    
    @Override
    final BoundMethodHandle copyWithExtendL(final MethodType methodType, final LambdaForm lambdaForm, final Object o) {
        return BoundMethodHandle.bindSingle(methodType, lambdaForm, o);
    }
    
    @Override
    final BoundMethodHandle copyWithExtendI(final MethodType methodType, final LambdaForm lambdaForm, final int n) {
        try {
            return SimpleMethodHandle.SPECIES_DATA.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(methodType, lambdaForm, n);
        }
        catch (final Throwable t) {
            throw MethodHandleStatics.uncaughtException(t);
        }
    }
    
    @Override
    final BoundMethodHandle copyWithExtendJ(final MethodType methodType, final LambdaForm lambdaForm, final long n) {
        try {
            return SimpleMethodHandle.SPECIES_DATA.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(methodType, lambdaForm, n);
        }
        catch (final Throwable t) {
            throw MethodHandleStatics.uncaughtException(t);
        }
    }
    
    @Override
    final BoundMethodHandle copyWithExtendF(final MethodType methodType, final LambdaForm lambdaForm, final float n) {
        try {
            return SimpleMethodHandle.SPECIES_DATA.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(methodType, lambdaForm, n);
        }
        catch (final Throwable t) {
            throw MethodHandleStatics.uncaughtException(t);
        }
    }
    
    @Override
    final BoundMethodHandle copyWithExtendD(final MethodType methodType, final LambdaForm lambdaForm, final double n) {
        try {
            return SimpleMethodHandle.SPECIES_DATA.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(methodType, lambdaForm, n);
        }
        catch (final Throwable t) {
            throw MethodHandleStatics.uncaughtException(t);
        }
    }
    
    static {
        SPECIES_DATA = SpeciesData.EMPTY;
    }
}
