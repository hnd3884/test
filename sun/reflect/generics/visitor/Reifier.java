package sun.reflect.generics.visitor;

import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.FloatSignature;
import sun.reflect.generics.tree.LongSignature;
import sun.reflect.generics.tree.IntSignature;
import sun.reflect.generics.tree.CharSignature;
import sun.reflect.generics.tree.ShortSignature;
import sun.reflect.generics.tree.BooleanSignature;
import sun.reflect.generics.tree.ByteSignature;
import sun.reflect.generics.tree.BottomSignature;
import sun.reflect.generics.tree.Wildcard;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.ArrayTypeSignature;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.TypeArgument;
import sun.reflect.generics.factory.GenericsFactory;
import java.lang.reflect.Type;

public class Reifier implements TypeTreeVisitor<Type>
{
    private Type resultType;
    private GenericsFactory factory;
    
    private Reifier(final GenericsFactory factory) {
        this.factory = factory;
    }
    
    private GenericsFactory getFactory() {
        return this.factory;
    }
    
    public static Reifier make(final GenericsFactory genericsFactory) {
        return new Reifier(genericsFactory);
    }
    
    private Type[] reifyTypeArguments(final TypeArgument[] array) {
        final Type[] array2 = new Type[array.length];
        for (int i = 0; i < array.length; ++i) {
            array[i].accept(this);
            array2[i] = this.resultType;
        }
        return array2;
    }
    
    @Override
    public Type getResult() {
        assert this.resultType != null;
        return this.resultType;
    }
    
    @Override
    public void visitFormalTypeParameter(final FormalTypeParameter formalTypeParameter) {
        this.resultType = this.getFactory().makeTypeVariable(formalTypeParameter.getName(), formalTypeParameter.getBounds());
    }
    
    @Override
    public void visitClassTypeSignature(final ClassTypeSignature classTypeSignature) {
        final List<SimpleClassTypeSignature> path = classTypeSignature.getPath();
        assert !path.isEmpty();
        final Iterator iterator = path.iterator();
        SimpleClassTypeSignature simpleClassTypeSignature = (SimpleClassTypeSignature)iterator.next();
        final StringBuilder sb = new StringBuilder(simpleClassTypeSignature.getName());
        simpleClassTypeSignature.getDollar();
        while (iterator.hasNext() && simpleClassTypeSignature.getTypeArguments().length == 0) {
            simpleClassTypeSignature = (SimpleClassTypeSignature)iterator.next();
            sb.append(simpleClassTypeSignature.getDollar() ? "$" : ".").append(simpleClassTypeSignature.getName());
        }
        assert simpleClassTypeSignature.getTypeArguments().length > 0;
        final Type namedType = this.getFactory().makeNamedType(sb.toString());
        if (simpleClassTypeSignature.getTypeArguments().length == 0) {
            assert !iterator.hasNext();
            this.resultType = namedType;
        }
        else {
            assert simpleClassTypeSignature.getTypeArguments().length > 0;
            ParameterizedType resultType = this.getFactory().makeParameterizedType(namedType, this.reifyTypeArguments(simpleClassTypeSignature.getTypeArguments()), null);
            while (iterator.hasNext()) {
                final SimpleClassTypeSignature simpleClassTypeSignature2 = (SimpleClassTypeSignature)iterator.next();
                sb.append(simpleClassTypeSignature2.getDollar() ? "$" : ".").append(simpleClassTypeSignature2.getName());
                resultType = this.getFactory().makeParameterizedType(this.getFactory().makeNamedType(sb.toString()), this.reifyTypeArguments(simpleClassTypeSignature2.getTypeArguments()), resultType);
            }
            this.resultType = resultType;
        }
    }
    
    @Override
    public void visitArrayTypeSignature(final ArrayTypeSignature arrayTypeSignature) {
        arrayTypeSignature.getComponentType().accept(this);
        this.resultType = this.getFactory().makeArrayType(this.resultType);
    }
    
    @Override
    public void visitTypeVariableSignature(final TypeVariableSignature typeVariableSignature) {
        this.resultType = this.getFactory().findTypeVariable(typeVariableSignature.getIdentifier());
    }
    
    @Override
    public void visitWildcard(final Wildcard wildcard) {
        this.resultType = this.getFactory().makeWildcard(wildcard.getUpperBounds(), wildcard.getLowerBounds());
    }
    
    @Override
    public void visitSimpleClassTypeSignature(final SimpleClassTypeSignature simpleClassTypeSignature) {
        this.resultType = this.getFactory().makeNamedType(simpleClassTypeSignature.getName());
    }
    
    @Override
    public void visitBottomSignature(final BottomSignature bottomSignature) {
    }
    
    @Override
    public void visitByteSignature(final ByteSignature byteSignature) {
        this.resultType = this.getFactory().makeByte();
    }
    
    @Override
    public void visitBooleanSignature(final BooleanSignature booleanSignature) {
        this.resultType = this.getFactory().makeBool();
    }
    
    @Override
    public void visitShortSignature(final ShortSignature shortSignature) {
        this.resultType = this.getFactory().makeShort();
    }
    
    @Override
    public void visitCharSignature(final CharSignature charSignature) {
        this.resultType = this.getFactory().makeChar();
    }
    
    @Override
    public void visitIntSignature(final IntSignature intSignature) {
        this.resultType = this.getFactory().makeInt();
    }
    
    @Override
    public void visitLongSignature(final LongSignature longSignature) {
        this.resultType = this.getFactory().makeLong();
    }
    
    @Override
    public void visitFloatSignature(final FloatSignature floatSignature) {
        this.resultType = this.getFactory().makeFloat();
    }
    
    @Override
    public void visitDoubleSignature(final DoubleSignature doubleSignature) {
        this.resultType = this.getFactory().makeDouble();
    }
    
    @Override
    public void visitVoidDescriptor(final VoidDescriptor voidDescriptor) {
        this.resultType = this.getFactory().makeVoid();
    }
}
