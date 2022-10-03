package org.eclipse.jdt.internal.compiler;

import java.util.Locale;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

public interface IProblemFactory
{
    CategorizedProblem createProblem(final char[] p0, final int p1, final String[] p2, final String[] p3, final int p4, final int p5, final int p6, final int p7, final int p8);
    
    CategorizedProblem createProblem(final char[] p0, final int p1, final String[] p2, final int p3, final String[] p4, final int p5, final int p6, final int p7, final int p8, final int p9);
    
    Locale getLocale();
    
    String getLocalizedMessage(final int p0, final String[] p1);
    
    String getLocalizedMessage(final int p0, final int p1, final String[] p2);
}
