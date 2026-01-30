import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

public class Logger implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient PrintWriter logWriter;
    private String currentLogFile;
    private boolean loggingEnabled = false;

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void startLogging(String filename) {
        try {
            if (logWriter != null) {
                logWriter.close();
                loggingEnabled = false;
            }

            if (filename.isEmpty()) {
                return;
            }

            logWriter = new PrintWriter(new FileWriter(filename));
            currentLogFile = filename;
            loggingEnabled = true;
        } catch (IOException e) {
            System.out.println("ERROR: Cannot open log file.");
        }
    }

    public void log(String message) {
        if (logWriter != null) {
            logWriter.println(message);
            logWriter.flush();
        }
    }
}
