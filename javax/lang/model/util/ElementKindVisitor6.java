package javax.lang.model.util;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ElementKindVisitor6<R, P> extends SimpleElementVisitor6<R, P>
{
    protected ElementKindVisitor6() {
        super(null);
    }
    
    protected ElementKindVisitor6(final R r) {
        super(r);
    }
    
    @Override
    public R visitPackage(final PackageElement packageElement, final P p2) {
        assert packageElement.getKind() == ElementKind.PACKAGE : "Bad kind on PackageElement";
        return this.defaultAction(packageElement, p2);
    }
    
    @Override
    public R visitType(final TypeElement typeElement, final P p2) {
        final ElementKind kind = typeElement.getKind();
        switch (kind) {
            case ANNOTATION_TYPE: {
                return this.visitTypeAsAnnotationType(typeElement, p2);
            }
            case CLASS: {
                return this.visitTypeAsClass(typeElement, p2);
            }
            case ENUM: {
                return this.visitTypeAsEnum(typeElement, p2);
            }
            case INTERFACE: {
                return this.visitTypeAsInterface(typeElement, p2);
            }
            default: {
                throw new AssertionError((Object)("Bad kind " + kind + " for TypeElement" + typeElement));
            }
        }
    }
    
    public R visitTypeAsAnnotationType(final TypeElement typeElement, final P p2) {
        return this.defaultAction(typeElement, p2);
    }
    
    public R visitTypeAsClass(final TypeElement typeElement, final P p2) {
        return this.defaultAction(typeElement, p2);
    }
    
    public R visitTypeAsEnum(final TypeElement typeElement, final P p2) {
        return this.defaultAction(typeElement, p2);
    }
    
    public R visitTypeAsInterface(final TypeElement typeElement, final P p2) {
        return this.defaultAction(typeElement, p2);
    }
    
    @Override
    public R visitVariable(final VariableElement variableElement, final P p2) {
        final ElementKind kind = variableElement.getKind();
        switch (kind) {
            case ENUM_CONSTANT: {
                return this.visitVariableAsEnumConstant(variableElement, p2);
            }
            case EXCEPTION_PARAMETER: {
                return this.visitVariableAsExceptionParameter(variableElement, p2);
            }
            case FIELD: {
                return this.visitVariableAsField(variableElement, p2);
            }
            case LOCAL_VARIABLE: {
                return this.visitVariableAsLocalVariable(variableElement, p2);
            }
            case PARAMETER: {
                return this.visitVariableAsParameter(variableElement, p2);
            }
            case RESOURCE_VARIABLE: {
                return this.visitVariableAsResourceVariable(variableElement, p2);
            }
            default: {
                throw new AssertionError((Object)("Bad kind " + kind + " for VariableElement" + variableElement));
            }
        }
    }
    
    public R visitVariableAsEnumConstant(final VariableElement variableElement, final P p2) {
        return this.defaultAction(variableElement, p2);
    }
    
    public R visitVariableAsExceptionParameter(final VariableElement variableElement, final P p2) {
        return this.defaultAction(variableElement, p2);
    }
    
    public R visitVariableAsField(final VariableElement variableElement, final P p2) {
        return this.defaultAction(variableElement, p2);
    }
    
    public R visitVariableAsLocalVariable(final VariableElement variableElement, final P p2) {
        return this.defaultAction(variableElement, p2);
    }
    
    public R visitVariableAsParameter(final VariableElement variableElement, final P p2) {
        return this.defaultAction(variableElement, p2);
    }
    
    public R visitVariableAsResourceVariable(final VariableElement variableElement, final P p2) {
        return this.visitUnknown(variableElement, p2);
    }
    
    @Override
    public R visitExecutable(final ExecutableElement executableElement, final P p2) {
        final ElementKind kind = executableElement.getKind();
        switch (kind) {
            case CONSTRUCTOR: {
                return this.visitExecutableAsConstructor(executableElement, p2);
            }
            case INSTANCE_INIT: {
                return this.visitExecutableAsInstanceInit(executableElement, p2);
            }
            case METHOD: {
                return this.visitExecutableAsMethod(executableElement, p2);
            }
            case STATIC_INIT: {
                return this.visitExecutableAsStaticInit(executableElement, p2);
            }
            default: {
                throw new AssertionError((Object)("Bad kind " + kind + " for ExecutableElement" + executableElement));
            }
        }
    }
    
    public R visitExecutableAsConstructor(final ExecutableElement executableElement, final P p2) {
        return this.defaultAction(executableElement, p2);
    }
    
    public R visitExecutableAsInstanceInit(final ExecutableElement executableElement, final P p2) {
        return this.defaultAction(executableElement, p2);
    }
    
    public R visitExecutableAsMethod(final ExecutableElement executableElement, final P p2) {
        return this.defaultAction(executableElement, p2);
    }
    
    public R visitExecutableAsStaticInit(final ExecutableElement executableElement, final P p2) {
        return this.defaultAction(executableElement, p2);
    }
    
    @Override
    public R visitTypeParameter(final TypeParameterElement typeParameterElement, final P p2) {
        assert typeParameterElement.getKind() == ElementKind.TYPE_PARAMETER : "Bad kind on TypeParameterElement";
        return this.defaultAction(typeParameterElement, p2);
    }
}
