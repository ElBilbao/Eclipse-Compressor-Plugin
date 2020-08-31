package dasoft.files.processor.compressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import dasoft.files.processor.extensionsource.IProcessor;

public class Controller implements IProcessor {
	
	/**
	 * Method in which the process starts
	 */
	@Override
	public void process(List<File> files) {
		for(File file : files) {
			if(file.length() > 300000) {
				compressFile(file);
			}
		}
	}
	
	/**
	 * Generate a output compressed file based on the file given
	 * @param file is a File to be compressed
	 */
	void compressFile(File file) {
		try {
			String sourceFile = file.toString();
			String compressedFile = sourceFile.substring(0, sourceFile.length() - 4) + ".zip";
			
			FileOutputStream foStream = new FileOutputStream(compressedFile);
			ZipOutputStream zipOut = new ZipOutputStream(foStream);
	        
			File fileToZip = new File(sourceFile);
	        FileInputStream fiStream = new FileInputStream(fileToZip);
	        
	        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
	        zipOut.putNextEntry(zipEntry);
	        
	        byte[] aux = new byte[1024];
	        int length;
	        while((length = fiStream.read(aux)) >= 0) {
	            zipOut.write(aux, 0, length);
	        }
	        
	        zipOut.close();
	        fiStream.close();
	        foStream.close();
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
