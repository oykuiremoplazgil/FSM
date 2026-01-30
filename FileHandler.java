import java.io.*;

public class FileHandler {
    public static void saveToTextFile(FSM fsm, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print("SYMBOLS");
            for (String symbol : fsm.getSymbols()) {
                writer.print(" " + symbol);
            }
            writer.println(";");

            writer.print("STATES");
            for (String state : fsm.getStates().keySet()) {
                writer.print(" " + state);
            }
            writer.println(";");

            if (fsm.getInitialState() != null) {
                writer.println("INITIAL-STATE " + fsm.getInitialState().getName() + ";");
            }

            if (!fsm.getFinalStates().isEmpty()) {
                writer.print("FINAL-STATES");
                for (State state : fsm.getFinalStates()) {
                    writer.print(" " + state.getName());
                }
                writer.println(";");
            }

            if (!fsm.getTransitions().isEmpty()) {
                writer.print("TRANSITIONS");
                boolean first = true;
                for (Transition t : fsm.getTransitions()) {
                    if (!first) {
                        writer.print(",");
                    } else {
                        first = false;
                    }
                    writer.print(" " + t.getSymbol() + " " +
                            t.getFromState().getName() + " " +
                            t.getToState().getName());
                }
                writer.println(";");
            }

            System.out.println("FSM saved to text file: " + filename);
        } catch (IOException e) {
            System.out.println("Error: Could not save to file " + filename);
            System.out.println(e.getMessage());
        }
    }
    public static void saveToBinaryFile(FSM fsm, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(fsm);
            System.out.println("FSM saved to binary file: " + filename);
        } catch (IOException e) {
            System.out.println("Error: Could not save to binary file " + filename);
            System.out.println(e.getMessage());
        }
    }
    public static FSM loadFromBinaryFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            FSM fsm = (FSM) ois.readObject();
            System.out.println("FSM loaded from binary file: " + filename);
            return fsm;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: Could not load from binary file " + filename);
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static FSM loadFromTextFile(String filename) {
        FSM fsm = new FSM();
        CommandProcessor processor = new CommandProcessor(fsm);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder commandBuilder = new StringBuilder();
            String line;
            int lineNumber = 1;
            int commandStartLine = 1;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    lineNumber++;
                    continue;
                }

                if (line.trim().startsWith(";")) {
                    lineNumber++;
                    continue;
                }

                if (commandBuilder.length() == 0) {
                    commandStartLine = lineNumber;
                }

                commandBuilder.append(line).append("\n");

                if (line.contains(";")) {
                    String command = commandBuilder.toString();
                    try {
                        processor.processCommand(command, commandStartLine);
                    } catch (Exception e) {
                        System.out.println("Error at line " + commandStartLine + "-" + lineNumber + ": " + e.getMessage());
                    }
                    commandBuilder = new StringBuilder();
                }

                lineNumber++;
            }

            if (commandBuilder.length() > 0) {
                System.out.println("Warning at line " + commandStartLine + ": Incomplete command at end of file, missing semicolon");
            }

            System.out.println("FSM loaded from text file: " + filename);
        } catch (IOException e) {
            System.out.println("Error: Could not load from file " + filename);
            System.out.println(e.getMessage());
        }

        return fsm;
    }
}
