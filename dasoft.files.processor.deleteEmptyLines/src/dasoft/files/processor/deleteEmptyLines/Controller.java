package dasoft.files.processor.deleteEmptyLines;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.resources.ResourcesPlugin;

import dasoft.files.processor.extensionsource.IProcessor;

public class Controller implements IProcessor {
	
	/**
	 * Method in which the process starts
	 */
	@Override
	public void process(List<File> files) {
		// Generate results file
		try {
			File source = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
			File result = new File(source.toString() + "/resultado.txt");
			PrintWriter writer = new PrintWriter(result);
			
			// Delete empty lines of n number of files
			for(File file : files) {
				int numDeletedLines = deleteEmptyLines(file);
				writer.write(file.toString() + " - " + numDeletedLines + " deleted lines.");
                writer.write("\n");
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that deletes empty lines from a file
	 * @param file is a File to have its empty lines deleted
	 * @return a int with the number of empty lines deleted from the File
	 */
	int deleteEmptyLines(File file) {
		int numDeletedLines = 0;
		Scanner scanner;
        PrintWriter writer;

        try {
        	File newFile = new File(file.toString() + ".temp");
        	
            scanner = new Scanner(file);
            writer = new PrintWriter(newFile);
            
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (!line.isEmpty()) {
                    writer.write(line);
                    writer.write("\n");
                } else {
                	numDeletedLines++;
                }
            }
            
            scanner.close();
            writer.close();
            
            file.delete();
            newFile.renameTo(file);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return numDeletedLines;
	}
	
}
