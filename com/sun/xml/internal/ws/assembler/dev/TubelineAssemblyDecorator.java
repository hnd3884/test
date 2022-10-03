package com.sun.xml.internal.ws.assembler.dev;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import com.sun.xml.internal.ws.api.pipe.Tube;

public class TubelineAssemblyDecorator
{
    public static TubelineAssemblyDecorator composite(final Iterable<TubelineAssemblyDecorator> decorators) {
        return new CompositeTubelineAssemblyDecorator(decorators);
    }
    
    public Tube decorateClient(final Tube tube, final ClientTubelineAssemblyContext context) {
        return tube;
    }
    
    public Tube decorateClientHead(final Tube tube, final ClientTubelineAssemblyContext context) {
        return tube;
    }
    
    public Tube decorateClientTail(final Tube tube, final ClientTubelineAssemblyContext context) {
        return tube;
    }
    
    public Tube decorateServer(final Tube tube, final ServerTubelineAssemblyContext context) {
        return tube;
    }
    
    public Tube decorateServerTail(final Tube tube, final ServerTubelineAssemblyContext context) {
        return tube;
    }
    
    public Tube decorateServerHead(final Tube tube, final ServerTubelineAssemblyContext context) {
        return tube;
    }
    
    private static class CompositeTubelineAssemblyDecorator extends TubelineAssemblyDecorator
    {
        private Collection<TubelineAssemblyDecorator> decorators;
        
        public CompositeTubelineAssemblyDecorator(final Iterable<TubelineAssemblyDecorator> decorators) {
            this.decorators = new ArrayList<TubelineAssemblyDecorator>();
            for (final TubelineAssemblyDecorator decorator : decorators) {
                this.decorators.add(decorator);
            }
        }
        
        @Override
        public Tube decorateClient(Tube tube, final ClientTubelineAssemblyContext context) {
            for (final TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateClient(tube, context);
            }
            return tube;
        }
        
        @Override
        public Tube decorateClientHead(Tube tube, final ClientTubelineAssemblyContext context) {
            for (final TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateClientHead(tube, context);
            }
            return tube;
        }
        
        @Override
        public Tube decorateClientTail(Tube tube, final ClientTubelineAssemblyContext context) {
            for (final TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateClientTail(tube, context);
            }
            return tube;
        }
        
        @Override
        public Tube decorateServer(Tube tube, final ServerTubelineAssemblyContext context) {
            for (final TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateServer(tube, context);
            }
            return tube;
        }
        
        @Override
        public Tube decorateServerTail(Tube tube, final ServerTubelineAssemblyContext context) {
            for (final TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateServerTail(tube, context);
            }
            return tube;
        }
        
        @Override
        public Tube decorateServerHead(Tube tube, final ServerTubelineAssemblyContext context) {
            for (final TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateServerHead(tube, context);
            }
            return tube;
        }
    }
}
