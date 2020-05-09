import java.io.*;

public class Shell {
    public static void main(String[] args) {
        int errorCode, firstInit;
        Manager manager;
        PrintWriter out;
        String userInput;
        BufferedReader reader;
        BufferedWriter writer;

        try {
            firstInit = 0;
            manager = new Manager();
            reader = new BufferedReader(new FileReader("./Examples/input.txt"));
            writer = new BufferedWriter(new FileWriter("./Examples/output.txt"));
            out = new PrintWriter(writer);

            userInput = reader.readLine();
            while(userInput != null) {
                errorCode = 0;
                userInput = userInput.trim();

                if (userInput.isEmpty()) {
                    userInput = reader.readLine();
                    continue;
                }

                if (userInput.contains("cr")) {
                    int priority = Character.getNumericValue(userInput.charAt(3));
                    errorCode = manager.create(priority);
                }
                else if (userInput.contains("de")) {
                    int processIndex = Character.getNumericValue(userInput.charAt(3));
                    errorCode = manager.destroy(processIndex);
                }
                else if (userInput.contains("rq")) {
                    int resourceIndex = Character.getNumericValue(userInput.charAt(3));
                    int numUnitsRequested = Character.getNumericValue(userInput.charAt(5));
                    errorCode = manager.request(resourceIndex, numUnitsRequested);
                }
                else if (userInput.contains("rl")) {
                    int resourceIndex = Character.getNumericValue(userInput.charAt(3));
                    int numUnitsReleased = Character.getNumericValue(userInput.charAt(5));
                    errorCode = manager.release(resourceIndex, numUnitsReleased);
                }
                else if (userInput.equalsIgnoreCase("to"))
                    manager.timeout();
                else if (userInput.equalsIgnoreCase("in"))
                    manager.init();

                if (errorCode == 0) {
                    if (userInput.equalsIgnoreCase("in") && firstInit == 0)
                        firstInit = 1;
                    else if (userInput.equalsIgnoreCase("in") && firstInit == 1)
                        out.println();

                    out.print(manager.getCurrProcessIndex() + " ");
                }
                else
                    out.print(errorCode);

                userInput = reader.readLine();
            }

            reader.close();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
