import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalCommandRunner {
    public static void main(String[] args) {
        try {
            // Command to execute (replace this with your desired command)
            String[] command = {"ls", "streaming data/rawdata"};

            // Create ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exited with error code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
