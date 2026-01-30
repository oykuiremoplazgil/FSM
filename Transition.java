import java.io.Serializable;

public class Transition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String symbol;
    private State fromState;
    private State toState;


    public Transition(String symbol, State fromState, State toState) {
        this.symbol = symbol.toLowerCase();
        this.fromState = fromState;
        this.toState = toState;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol.toLowerCase();
    }

    public State getFromState() {
        return fromState;
    }

    public void setFromState(State fromState) {
        this.fromState = fromState;
    }

    public State getToState() {
        return toState;
    }

    public void setToState(State toState) {
        this.toState = toState;
    }

    @Override
    public String toString() {
        return fromState.getName() + " --" + symbol + "--> " + toState.getName();
    }
}
