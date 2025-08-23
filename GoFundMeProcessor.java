import java.io.*;

public class GoFundMeProcessor {


    public static void main(String[] args) {
        System.out.println("Processing GoFundMe campaigns...");
        BufferedReader bufferedReader;
        // Case 1: File provided as argument
        if (args.length > 0) {
            System.out.println("Reading from file: " + args[0]);
            try {
                bufferedReader = new BufferedReader(new FileReader(args[0]));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        // Case 2: Read from stdin (for piped input)
        else {
            System.out.println("Reading from standard input...");
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }

        String line;
        try{

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.equalsIgnoreCase("END")) break;

                System.out.println("Processing line: " + line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
