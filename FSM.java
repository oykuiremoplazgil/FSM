import java.io.Serializable;
import java.util.*;

public class FSM implements Serializable {
    private static final long serialVersionUID = 1L;
    private Logger logger;
    private Set<String> symbols;
    private Map<String, State> states = new LinkedHashMap<>();
    private State initialState;
    private Set<State> finalStates;
    private List<Transition> transitions;
    private Map<String, Transition> transitionMap;

    public FSM() {
        symbols = new HashSet<>();
        states = new LinkedHashMap<>();
        finalStates = new HashSet<>();
        transitions = new ArrayList<>();
        transitionMap = new HashMap<>();
        logger = new Logger();
    }
    public Set<String> getSymbols() {
        return symbols;
    }

    public Map<String, State> getStates() {
        return states;
    }

    public State getInitialState() {
        return initialState;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }
    public void startLogging(String filename) {
        logger.startLogging(filename);
    }
    public void log(String message) {
        logger.log(message);
    }
    public void addSymbol(String symbol) {
        symbol = symbol.toLowerCase();

        if (symbol.length() != 1 || !symbol.matches("[a-z0-9]")) {
            return;
        }
        if (!symbols.add(symbol)) {
            System.out.println("Warning: " + symbol + " was already declared as a symbol");
        }
    }

    public void addState(String name, boolean isFinal) {
        name = name.toLowerCase();
        if (!name.matches("[a-z0-9]+")) {
            System.out.println("ERROR: Invalid state '" + name + "'");
            return;
        }

        if (states.containsKey(name)) {
            System.out.println("WARNING: State '" + name + "' already exists.");
            if (isFinal && !finalStates.contains(states.get(name))) {
                State state = states.get(name);
                state.setFinal(true);
                finalStates.add(state);
            }
            return;
        }
        State state = new State(name, isFinal);
        states.put(name, state);
        if (isFinal) {
            finalStates.add(state);
        }

        if (initialState == null) {
            initialState = state;
        }
    }
    public void setInitialState(String name) {
        name = name.toLowerCase();
        State state = states.get(name);
        if (state == null) {
            System.out.println("WARNING: State '" + name + "' was not previously declared.");
            addState(name, false);
            state = states.get(name);
        }
        initialState = state;
    }
    public void addFinalState(String name) {
        name = name.toLowerCase();
        State state = states.get(name);
        if (state == null) {
            System.out.println("WARNING: State '" + name + "' was not previously declared.");
            addState(name, true);
            return;
        }

        if (finalStates.contains(state)) {
            System.out.println("WARNING: State '" + name + "' was already declared as a final state.");
            return;
        }

        state.setFinal(true);
        finalStates.add(state);
    }

    public void addTransition(String symbol, String from, String to) {
        symbol = symbol.toLowerCase();
        from = from.toLowerCase();
        to = to.toLowerCase();

        if (!symbols.contains(symbol)) {
            System.out.println("Error: Invalid symbol '" + symbol + "'");
            return;
        }

        State fromState = states.get(from);
        if (fromState == null) {
            System.out.println("Error: Invalid state '" + from + "' in transition");
            return;
        }

        State toState = states.get(to);
        if (toState == null) {
            System.out.println("Error: Invalid state '" + to + "' in transition");
            return;
        }

        String key = symbol + "_" + from;
        Transition existingTransition = transitionMap.get(key);
        if (existingTransition != null) {
            String existingTo = existingTransition.getToState().getName();
            if (!existingTo.equalsIgnoreCase(to)) {
                System.out.println("Warning: Overriding existing transition for <" + symbol + "," + from + "> from "
                        + existingTo.toUpperCase() + " to " + to.toUpperCase());
                transitions.remove(existingTransition);
            } else {
                System.out.println("Warning: Duplicate transition <" + symbol + "," + from + "," + to.toUpperCase() + "> already exists");
                return;
            }
        }

        Transition newTransition = new Transition(symbol, fromState, toState);
        transitions.add(newTransition);
        transitionMap.put(key, newTransition);
    }
    public String execute(String input) {
        if (initialState == null) {
            return "ERROR: Initial state not set";
        }

        State currentState = initialState;
        List<String> path = new ArrayList<>();
        path.add(currentState.getName().toUpperCase());

        for (char ch : input.toCharArray()) {
            String symbol = String.valueOf(ch).toLowerCase();

            if (!symbols.contains(symbol)) {
                return "ERROR: Invalid symbol '" + symbol + "'";
            }

            String key = symbol + "_" + currentState.getName();
            Transition transition = transitionMap.get(key);

            if (transition == null) {
                System.out.println(String.join(" ", path));
                return "NO";
            }

            currentState = transition.getToState();
            path.add(currentState.getName().toUpperCase());
        }
        System.out.println(String.join(" ", path));

        String result = finalStates.contains(currentState) ? "YES" : "NO";

        return result;
    }
    public boolean isLoggingEnabled() {
        return logger.isLoggingEnabled();
    }
    public void printFSM() {
        System.out.print("SYMBOLS {");
        boolean first = true;
        for (String symbol : symbols) {
            if (!first) System.out.print(",");
            else first = false;
            System.out.print(symbol);
        }
        System.out.println("}");

        System.out.print("STATES {");
        first = true;
        for (String state : states.keySet()) {
            if (!first) System.out.print(", ");
            else first = false;
            System.out.print(state.toUpperCase());
        }
        System.out.println("}");

        System.out.println("INITIAL STATE " + (initialState != null ? initialState.getName().toUpperCase() : "None"));

        System.out.print("FINAL STATES {");
        first = true;
        for (State state : finalStates) {
            if (!first) System.out.print(", ");
            else first = false;
            System.out.print(state.getName().toUpperCase());
        }
        System.out.println("}");

        System.out.print("TRANSITIONS ");
        first = true;
        for (Transition t : transitions) {
            if (!first) System.out.print(", ");
            else first = false;
            System.out.print(t.getSymbol() + " " +
                    t.getFromState().getName().toUpperCase() + " " +
                    t.getToState().getName().toUpperCase());
        }
        System.out.println();
    }
    public void clearFSM() {
        symbols.clear();
        states.clear();
        transitions.clear();
        finalStates.clear();
        initialState = null;
    }
}
