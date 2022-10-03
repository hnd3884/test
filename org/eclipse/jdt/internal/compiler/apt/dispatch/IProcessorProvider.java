package org.eclipse.jdt.internal.compiler.apt.dispatch;

import javax.annotation.processing.Processor;
import java.util.List;

public interface IProcessorProvider
{
    ProcessorInfo discoverNextProcessor();
    
    List<ProcessorInfo> getDiscoveredProcessors();
    
    void reportProcessorException(final Processor p0, final Exception p1);
}
