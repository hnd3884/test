package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.exception.MathInternalError;

public enum FilterType
{
    TRIGGER_ONLY_DECREASING_EVENTS {
        @Override
        protected boolean getTriggeredIncreasing() {
            return false;
        }
        
        @Override
        protected Transformer selectTransformer(final Transformer previous, final double g, final boolean forward) {
            if (forward) {
                switch (previous) {
                    case UNINITIALIZED: {
                        if (g > 0.0) {
                            return Transformer.MAX;
                        }
                        if (g < 0.0) {
                            return Transformer.PLUS;
                        }
                        return Transformer.UNINITIALIZED;
                    }
                    case PLUS: {
                        if (g >= 0.0) {
                            return Transformer.MIN;
                        }
                        return previous;
                    }
                    case MINUS: {
                        if (g >= 0.0) {
                            return Transformer.MAX;
                        }
                        return previous;
                    }
                    case MIN: {
                        if (g <= 0.0) {
                            return Transformer.MINUS;
                        }
                        return previous;
                    }
                    case MAX: {
                        if (g <= 0.0) {
                            return Transformer.PLUS;
                        }
                        return previous;
                    }
                    default: {
                        throw new MathInternalError();
                    }
                }
            }
            else {
                switch (previous) {
                    case UNINITIALIZED: {
                        if (g > 0.0) {
                            return Transformer.MINUS;
                        }
                        if (g < 0.0) {
                            return Transformer.MIN;
                        }
                        return Transformer.UNINITIALIZED;
                    }
                    case PLUS: {
                        if (g <= 0.0) {
                            return Transformer.MAX;
                        }
                        return previous;
                    }
                    case MINUS: {
                        if (g <= 0.0) {
                            return Transformer.MIN;
                        }
                        return previous;
                    }
                    case MIN: {
                        if (g >= 0.0) {
                            return Transformer.PLUS;
                        }
                        return previous;
                    }
                    case MAX: {
                        if (g >= 0.0) {
                            return Transformer.MINUS;
                        }
                        return previous;
                    }
                    default: {
                        throw new MathInternalError();
                    }
                }
            }
        }
    }, 
    TRIGGER_ONLY_INCREASING_EVENTS {
        @Override
        protected boolean getTriggeredIncreasing() {
            return true;
        }
        
        @Override
        protected Transformer selectTransformer(final Transformer previous, final double g, final boolean forward) {
            if (forward) {
                switch (previous) {
                    case UNINITIALIZED: {
                        if (g > 0.0) {
                            return Transformer.PLUS;
                        }
                        if (g < 0.0) {
                            return Transformer.MIN;
                        }
                        return Transformer.UNINITIALIZED;
                    }
                    case PLUS: {
                        if (g <= 0.0) {
                            return Transformer.MAX;
                        }
                        return previous;
                    }
                    case MINUS: {
                        if (g <= 0.0) {
                            return Transformer.MIN;
                        }
                        return previous;
                    }
                    case MIN: {
                        if (g >= 0.0) {
                            return Transformer.PLUS;
                        }
                        return previous;
                    }
                    case MAX: {
                        if (g >= 0.0) {
                            return Transformer.MINUS;
                        }
                        return previous;
                    }
                    default: {
                        throw new MathInternalError();
                    }
                }
            }
            else {
                switch (previous) {
                    case UNINITIALIZED: {
                        if (g > 0.0) {
                            return Transformer.MAX;
                        }
                        if (g < 0.0) {
                            return Transformer.MINUS;
                        }
                        return Transformer.UNINITIALIZED;
                    }
                    case PLUS: {
                        if (g >= 0.0) {
                            return Transformer.MIN;
                        }
                        return previous;
                    }
                    case MINUS: {
                        if (g >= 0.0) {
                            return Transformer.MAX;
                        }
                        return previous;
                    }
                    case MIN: {
                        if (g <= 0.0) {
                            return Transformer.MINUS;
                        }
                        return previous;
                    }
                    case MAX: {
                        if (g <= 0.0) {
                            return Transformer.PLUS;
                        }
                        return previous;
                    }
                    default: {
                        throw new MathInternalError();
                    }
                }
            }
        }
    };
    
    protected abstract boolean getTriggeredIncreasing();
    
    protected abstract Transformer selectTransformer(final Transformer p0, final double p1, final boolean p2);
}
