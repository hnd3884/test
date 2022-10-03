package com.sun.corba.se.spi.orbutil.fsm;

public interface Guard
{
    Result evaluate(final FSM p0, final Input p1);
    
    public static final class Complement extends GuardBase
    {
        private Guard guard;
        
        public Complement(final GuardBase guard) {
            super("not(" + guard.getName() + ")");
            this.guard = guard;
        }
        
        @Override
        public Result evaluate(final FSM fsm, final Input input) {
            return this.guard.evaluate(fsm, input).complement();
        }
    }
    
    public static final class Result
    {
        private String name;
        public static final Result ENABLED;
        public static final Result DISABLED;
        public static final Result DEFERED;
        
        private Result(final String name) {
            this.name = name;
        }
        
        public static Result convert(final boolean b) {
            return b ? Result.ENABLED : Result.DISABLED;
        }
        
        public Result complement() {
            if (this == Result.ENABLED) {
                return Result.DISABLED;
            }
            if (this == Result.DISABLED) {
                return Result.ENABLED;
            }
            return Result.DEFERED;
        }
        
        @Override
        public String toString() {
            return "Guard.Result[" + this.name + "]";
        }
        
        static {
            ENABLED = new Result("ENABLED");
            DISABLED = new Result("DISABLED");
            DEFERED = new Result("DEFERED");
        }
    }
}
