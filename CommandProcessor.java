import java.io.*;
import java.util.*;

public class CommandProcessor {
    private final FSM fsm;
    private final Logger logger;

    public CommandProcessor(FSM fsm, Logger logger) {
        this.fsm = fsm;
        this.logger = logger;
    }

    public void processCommand(String commandLine) {
        if (!commandLine.contains(";")) {
            logger.logAndPrint("Error: Commands must end with ';'");
            return;
        }

        String command = commandLine.split(";")[0].trim();

        if (command.toUpperCase().startsWith("SYMBOLS")) {
            handleSymbolsCommand(command.substring(7).trim());
        } else if (command.toUpperCase().startsWith("STATES")) {
            handleStatesCommand(command.substring(6).trim());
        } else if (command.toUpperCase().startsWith("INITIAL-STATE")) {
            handleInitialStateCommand(command.substring(13).trim());
        } else if (command.toUpperCase().startsWith("FINAL-STATES")) {
            handleFinalStatesCommand(command.substring(12).trim());
        } else if (command.toUpperCase().startsWith("TRANSITIONS")) {
            handleTransitionsCommand(command.substring(11).trim());
        } else if (command.toUpperCase().startsWith("EXECUTE")) {
            handleExecuteCommand(command.substring(7).trim());
        } else if (command.equalsIgnoreCase("CLEAR")) {
            handleClearCommand();
        } else if (command.equalsIgnoreCase("PRINT")) {
            handlePrintCommand();
        } else if (command.toUpperCase().startsWith("LOG")) {
            handleLogCommand(command.substring(3).trim());
        } else if (command.toUpperCase().startsWith("COMPILE")) {
            FileHandler.compileFSM(fsm, command.substring(7).trim(), logger);
        } else if (command.toUpperCase().startsWith("LOAD")) {
            FileHandler.loadFSM(fsm, command.substring(4).trim(), logger, this);
        } else {
            logger.logAndPrint("Unsupported or Invalid Command: " + command);
        }
    }

    private void handleSymbolsCommand(String symbolsPart) {
        if (symbolsPart.isEmpty()) {
            logger.logAndPrint("Current symbols: " + fsm.getSymbols());
            return;
        }

        String[] newSymbols = symbolsPart.split("\\s+");
        for (String sym : newSymbols) {
            if (!isValidSymbolLength(sym)) {
                logger.logAndPrint("Warning: '" + sym + "' is not allowed as a symbol, length must be 1");
            } else if (isValidSymbol(sym)) {
                if (!fsm.getSymbols().add(sym.toUpperCase())) {
                    logger.logAndPrint("Warning: Symbol '" + sym + "' was already declared.");
                }
            } else {
                logger.logAndPrint("Warning: Invalid symbol '" + sym + "'. Must be alphanumeric and single character.");
            }
        }
    }

    private void handleStatesCommand(String statesPart) {
        if (statesPart.isEmpty()) {
            logger.logAndPrint("Current states: " + fsm.getStates());
            logger.logAndPrint("Initial State: " + (fsm.getInitialState() != null ? fsm.getInitialState() : "Not Set"));
            logger.logAndPrint("Final States: " + fsm.getFinalStates());
            return;
        }

        String[] newStates = statesPart.split("\\s+");
        for (String st : newStates) {
            st = st.toUpperCase();
            if (isValidState(st)) {
                if (!fsm.getStates().add(st)) {
                    logger.logAndPrint("Warning: State '" + st + "' was already declared.");
                } else if (fsm.getInitialState() == null) {
                    fsm.setInitialState(st);
                    logger.logAndPrint("Info: Initial state set to '" + st + "'.");
                }
            } else {
                logger.logAndPrint("Warning: Invalid state '" + st + "'. Must be alphanumeric.");
            }
        }
    }

    private void handleInitialStateCommand(String statePart) {
        if (statePart.isEmpty()) {
            logger.logAndPrint("Error: No initial state provided.");
            return;
        }

        String st = statePart.toUpperCase();
        if (!isValidState(st)) {
            logger.logAndPrint("Warning: Invalid state '" + st + "'. Must be alphanumeric.");
            return;
        }

        if (!fsm.getStates().contains(st)) {
            fsm.addState(st);
            logger.logAndPrint("Warning: State '" + st + "' was not previously declared, added automatically.");
        }

        fsm.setInitialState(st);
        logger.logAndPrint("Initial state set to '" + st + "'.");
    }

    private void handleFinalStatesCommand(String statesPart) {
        if (statesPart.isEmpty()) {
            logger.logAndPrint("Error: No final states provided.");
            return;
        }

        String[] finals = statesPart.split("\\s+");
        for (String st : finals) {
            st = st.toUpperCase();
            if (!isValidState(st)) {
                logger.logAndPrint("Warning: Invalid state '" + st + "'. Must be alphanumeric.");
                continue;
            }

            if (!fsm.getStates().contains(st)) {
                fsm.addState(st);
                logger.logAndPrint("Warning: State '" + st + "' was not previously declared, added automatically.");
            }

            if (!fsm.getFinalStates().add(st)) {
                logger.logAndPrint("Warning: State '" + st + "' was already declared as a final state.");
            }
        }
    }

    private void handleTransitionsCommand(String transitionsPart) {
        if (transitionsPart.isEmpty()) {
            logger.logAndPrint("Error: No transitions provided.");
            return;
        }

        String[] transitionDefs = transitionsPart.split(",");
        for (String def : transitionDefs) {
            def = def.trim();
            if (def.isEmpty()) {
                continue;
            }

            String[] parts = def.trim().split("\\s+");
            if (parts.length < 3) {
                logger.logAndPrint("Error: Transition must have 3 parts. Found: " + Arrays.toString(parts));
                continue;
            }

            String symbol = parts[0].toUpperCase();
            String currentState = parts[1].toUpperCase();
            String nextState = String.join("", Arrays.copyOfRange(parts, 2, parts.length)).toUpperCase();

            if (!fsm.getSymbols().contains(symbol)) {
                logger.logAndPrint("Error: Symbol '" + symbol + "' not declared.");
                continue;
            }
            if (!fsm.getStates().contains(currentState)) {
                logger.logAndPrint("Error: Current state '" + currentState + "' not declared.");
                continue;
            }
            if (!fsm.getStates().contains(nextState)) {
                logger.logAndPrint("Error: Next state '" + nextState + "' not declared.");
                continue;
            }

            boolean contains = fsm.addTransition(symbol, currentState, nextState);

            if (contains) {
                logger.logAndPrint("Warning: Transition already exists for (" + symbol + ", " + currentState + "). Overriding.");
            }
        }
    }

    private void handleExecuteCommand(String inputString) {
        if (fsm.getInitialState() == null) {
            logger.logAndPrint("Error: No initial state defined.");
            return;
        }

        if (inputString.isEmpty()) {
            logger.logAndPrint("Error: No input string provided.");
            return;
        }

        inputString = inputString.toUpperCase();
        String currentState = fsm.getInitialState();
        System.out.print(currentState + " ");

        for (char ch : inputString.toCharArray()) {
            String symbol = String.valueOf(ch);

            if (!fsm.getSymbols().contains(symbol)) {
                logger.logAndPrint("\nError: Symbol '" + symbol + "' not declared.");
                return;
            }

            Map<String, String> stateTransitions = fsm.getTransitions().get(currentState);
            if (stateTransitions == null || !stateTransitions.containsKey(symbol)) {
                logger.logAndPrint("\nNO (No valid transition from state '" + currentState + "' with symbol '" + symbol + "')");
                return;
            }

            currentState = stateTransitions.get(symbol);
            System.out.print(currentState + " ");
        }

        if (fsm.getFinalStates().contains(currentState)) {
            logger.logAndPrint("\nYES");
        } else {
            logger.logAndPrint("\nNO");
        }
    }

    private void handleClearCommand() {
        fsm.getSymbols().clear();
        fsm.getStates().clear();
        fsm.setInitialState(null);
        fsm.getFinalStates().clear();
        fsm.getTransitions().clear();
        logger.logAndPrint("FSM cleared.");
        logger.log("FSM cleared.");
    }

    private void handlePrintCommand() {
        logger.logAndPrint("---- FSM CURRENT STATE ----");
        logger.logAndPrint("Symbols: " + fsm.getSymbols());
        logger.logAndPrint("States: " + fsm.getStates());
        logger.logAndPrint("Initial State: " + fsm.getInitialState());
        logger.logAndPrint("Final States: " + fsm.getFinalStates());
        logger.logAndPrint("Transitions:");
        for (String fromState : fsm.getTransitions().keySet()) {
            for (String symbol : fsm.getTransitions().get(fromState).keySet()) {
                logger.logAndPrint("  On '" + symbol + "' from " + fromState + " to " + fsm.getTransitions().get(fromState).get(symbol));
            }
        }
        logger.logAndPrint("----------------------------");
    }

    private void handleLogCommand(String logPart) {
        try {
            if (logPart.isEmpty()) {
                if (logger.isLoggingEnabled()) {
                    logger.close();
                    logger.logAndPrint("STOPPED LOGGING");
                } else {
                    logger.logAndPrint("LOGGING was not enabled");
                }
            } else {
                if (logger.isLoggingEnabled()) logger.close();
                logger.startLogging(logPart);
            }
        } catch (IOException e) {
            logger.logAndPrint("Error: Cannot open log file: " + logPart);
        }
    }

    private boolean isValidSymbol(String sym) {
        return Character.isLetterOrDigit(sym.charAt(0));
    }

    private boolean isValidSymbolLength(String sym) {
        return sym.length() == 1;
    }

    private boolean isValidState(String st) {
        return st.matches("[a-zA-Z0-9]+");
    }
}
