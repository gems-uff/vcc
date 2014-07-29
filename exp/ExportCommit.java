import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ExportCommit{

	public static void main(String args[]){
		// The name of the file to open.
        String input = "C:\\Desenvolvimento\\repositorios\\spring-security\\log.csv";
		String output = "C:\\Desenvolvimento\\repositorios\\spring-security\\output.csv";

        // This will reference one line at a time
        String line = null;

        try {
			// Assume default encoding.
            FileWriter fileWriter =
                new FileWriter(output);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                new BufferedWriter(fileWriter);
          
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(input);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            String previousLine = "";
            
            while((line = bufferedReader.readLine()) != null) {
				// Note that write() does not automatically
            // append a newline character.
				if(line.startsWith("Author")){
					bufferedWriter.write(previousLine);
					bufferedWriter.newLine();
				}
				if(!line.startsWith("Merge")){
					previousLine = line;
				}
            }	

            // Always close files.
            bufferedReader.close();	
            bufferedWriter.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                input + "'");				
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading/writing file '" 
                + input + "'/'"+ output + "'");					
            // Or we could just do this: 
            // ex.printStackTrace();
        }
	}
}