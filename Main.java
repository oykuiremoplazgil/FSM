import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, HH:mm");
        String now = LocalDateTime.now().format(formatter);

        System.out.println("FSM DESIGNER 2.3 " + now);

        FSM fsm = new FSM();
        CommandProcessor processor = new CommandProcessor(fsm);

        if (args.length > 0) {
            try {
                String filename = args[0];
                if (filename.endsWith(".fs")) {
                    FSM loadedFSM = FileHandler.loadFromBinaryFile(filename);
                    if (loadedFSM != null) {
                        fsm = loadedFSM;
                        processor = new CommandProcessor(fsm);
                    }
                } else {
                    FileHandler.loadFromTextFile(filename);
                    System.out.println("Commands from " + filename + " executed.");
                }
            } catch (Exception e) {
                System.out.println("Error processing command line argument: " + e.getMessage());
            }
        }

        Scanner scanner = new Scanner(System.in);

        boolean keepRunning = true;
        while (keepRunning) {
            System.out.print("? ");
            StringBuilder commandBuilder = new StringBuilder();
            String line;

            do {
                line = scanner.nextLine();
                commandBuilder.append(line).append("\n");
            } while (!line.trim().endsWith(";") && !line.trim().equalsIgnoreCase("EXIT"));

            String command = commandBuilder.toString().trim();

            if (command.equalsIgnoreCase("EXIT")) {
                System.out.println("TERMINATED BY USER");
                break;
            }

            try {
                keepRunning = processor.processCommand(command);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

}
