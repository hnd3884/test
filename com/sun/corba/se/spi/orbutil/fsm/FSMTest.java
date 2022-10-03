package com.sun.corba.se.spi.orbutil.fsm;

public class FSMTest
{
    public static final State STATE1;
    public static final State STATE2;
    public static final State STATE3;
    public static final State STATE4;
    public static final Input INPUT1;
    public static final Input INPUT2;
    public static final Input INPUT3;
    public static final Input INPUT4;
    private Guard counterGuard;
    
    public FSMTest() {
        this.counterGuard = new Guard() {
            @Override
            public Result evaluate(final FSM fsm, final Input input) {
                return Result.convert(((MyFSM)fsm).counter < 3);
            }
        };
    }
    
    private static void add1(final StateEngine stateEngine, final State state, final Input input, final State state2) {
        stateEngine.add(state, input, new TestAction1(state, input, state2), state2);
    }
    
    private static void add2(final StateEngine stateEngine, final State state, final State state2) {
        stateEngine.setDefault(state, new TestAction2(state, state2), state2);
    }
    
    public static void main(final String[] array) {
        final TestAction3 testAction3 = new TestAction3(FSMTest.STATE3, FSMTest.INPUT1);
        final StateEngine create = StateEngineFactory.create();
        add1(create, FSMTest.STATE1, FSMTest.INPUT1, FSMTest.STATE1);
        add2(create, FSMTest.STATE1, FSMTest.STATE2);
        add1(create, FSMTest.STATE2, FSMTest.INPUT1, FSMTest.STATE2);
        add1(create, FSMTest.STATE2, FSMTest.INPUT2, FSMTest.STATE2);
        add1(create, FSMTest.STATE2, FSMTest.INPUT3, FSMTest.STATE1);
        add1(create, FSMTest.STATE2, FSMTest.INPUT4, FSMTest.STATE3);
        create.add(FSMTest.STATE3, FSMTest.INPUT1, testAction3, FSMTest.STATE3);
        create.add(FSMTest.STATE3, FSMTest.INPUT1, testAction3, FSMTest.STATE4);
        add1(create, FSMTest.STATE3, FSMTest.INPUT2, FSMTest.STATE1);
        add1(create, FSMTest.STATE3, FSMTest.INPUT3, FSMTest.STATE2);
        add1(create, FSMTest.STATE3, FSMTest.INPUT4, FSMTest.STATE2);
        final MyFSM myFSM = new MyFSM(create);
        final TestInput testInput = new TestInput(FSMTest.INPUT1, "1.1");
        final TestInput testInput2 = new TestInput(FSMTest.INPUT1, "1.2");
        final TestInput testInput3 = new TestInput(FSMTest.INPUT2, "2.1");
        final TestInput testInput4 = new TestInput(FSMTest.INPUT2, "2.2");
        final TestInput testInput5 = new TestInput(FSMTest.INPUT3, "3.1");
        final TestInput testInput6 = new TestInput(FSMTest.INPUT3, "3.2");
        final TestInput testInput7 = new TestInput(FSMTest.INPUT3, "3.3");
        final TestInput testInput8 = new TestInput(FSMTest.INPUT4, "4.1");
        myFSM.doIt(testInput.getInput());
        myFSM.doIt(testInput2.getInput());
        myFSM.doIt(testInput8.getInput());
        myFSM.doIt(testInput.getInput());
        myFSM.doIt(testInput4.getInput());
        myFSM.doIt(testInput5.getInput());
        myFSM.doIt(testInput7.getInput());
        myFSM.doIt(testInput8.getInput());
        myFSM.doIt(testInput8.getInput());
        myFSM.doIt(testInput8.getInput());
        myFSM.doIt(testInput4.getInput());
        myFSM.doIt(testInput6.getInput());
        myFSM.doIt(testInput8.getInput());
        myFSM.doIt(testInput.getInput());
        myFSM.doIt(testInput2.getInput());
        myFSM.doIt(testInput.getInput());
        myFSM.doIt(testInput.getInput());
        myFSM.doIt(testInput.getInput());
        myFSM.doIt(testInput.getInput());
        myFSM.doIt(testInput.getInput());
    }
    
    static {
        STATE1 = new StateImpl("1");
        STATE2 = new StateImpl("2");
        STATE3 = new StateImpl("3");
        STATE4 = new StateImpl("4");
        INPUT1 = new InputImpl("1");
        INPUT2 = new InputImpl("2");
        INPUT3 = new InputImpl("3");
        INPUT4 = new InputImpl("4");
    }
}
