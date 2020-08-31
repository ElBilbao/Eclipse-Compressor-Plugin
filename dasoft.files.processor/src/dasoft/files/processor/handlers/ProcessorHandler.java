package dasoft.files.processor.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import dasoft.files.processor.dialog.ProcessorDialog;
import dasoft.files.processor.extensionsource.IProcessor;

public class ProcessorHandler extends AbstractHandler {
	
	// Variables //
	List<String> processesNames = new ArrayList<String>();
	IConfigurationElement[] extensiones;
	List<String> filesToProcess = new ArrayList<String>();
	List<File> filesAddresses = new ArrayList<File>();
	
	/**
	 * Constructor
	 * Used to obtain extension points and their attributes
	 */
	public ProcessorHandler() {
		IExtensionRegistry registro = Platform.getExtensionRegistry();
		extensiones = registro.getConfigurationElementsFor("dasoft.files.processor.processorsource");

		for (IConfigurationElement e : extensiones) {
			
			if (e.getName().equals("ProcessorSource")) {
				processesNames.add(e.getAttribute("name").toString());
			}
		}
	}
	
	/**
	 * Execute method
	 * File Processor Wizard to display, analyze and deploy extension points processes
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ProcessorDialog dialog = new ProcessorDialog(window, processesNames);
		IConfigurationElement selected = null;
		String[] fileExtension = {""};
		Boolean onlyWorkspace = true;
		Boolean hasResults = false;
		
		if(dialog.getResult() != 0) return null; // Process canceled
		
		// Find selected process
		for (IConfigurationElement e : extensiones) {
			if (e.getName().equals("ProcessorSource") && e.getAttribute("name") == dialog.getAnswer()) {
				selected = e;
				fileExtension = e.getAttribute("extensions").split(" ");
				onlyWorkspace = Boolean.parseBoolean(e.getAttribute("onlyWorkspace"));
				hasResults = Boolean.parseBoolean(e.getAttribute("hasResults"));
			}
		}
				
		File root = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		int counter = 0;
		
		// Find number of files depending on onlyWorkspace clause of the extension point selected
		if(!onlyWorkspace) {
			counter = countFiles(root, fileExtension, 0);
		} else {
			counter = countFilesRoot(root, fileExtension);
		}
		
		// Notify no files to process found
		if(counter < 1) {
			MessageDialog.openError(window.getShell(), "Error", "No files found to process.");
			return null;
		}
		
		// Confirm files to process
		String message = "The following " + counter + " files are to be processed.\n";
		for(String s : filesToProcess) message += s + "\n";
		MessageDialog confirmDialog = new MessageDialog(window.getShell(), "File Processor Wizard", null,
			    message, MessageDialog.CONFIRM, new String[] { "Process",
			    "Cancel"}, 0);
		
		int result = confirmDialog.open();
		
		// Launch process from extension point
		if(result == 0) {
			try {
				IProcessor extProcessor = (IProcessor) selected.createExecutableExtension("class");
				extProcessor.process(filesAddresses);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		// Inform user about reports depending on result generation clause
		if (hasResults) {
			MessageDialog.openInformation(window.getShell(), "File Processor Wizard", "SUCCESS: results logged in 'resultados.txt'.");
		}
		
		// Clear lists in case of re-running a process
		filesToProcess.clear();
		filesAddresses.clear();
		return null;
	}
	
	/**
	 * Method to find the number of files to process from selected process type
	 * @param dir Root to File directory
	 * @param ext File extensions to look for
	 * @param counter number of files found with the extensions
	 * @return number of files found
	 */
	int countFiles(File dir, String[] ext, int counter) {
		List<File> dirList = Arrays.asList(dir.listFiles());
		int i = 0;
		
		while (i < dirList.size()) {
			
			if(dirList.get(i).isDirectory()) {
				//System.out.println("Found Dir. " + dirList.get(i).toString());
				counter = countFiles(dirList.get(i), ext, counter);
			}
			
			else if (dirList.get(i).isFile()) {
				for(String e : ext) {
					if (dirList.get(i).toString().contains(e)) {
						filesToProcess.add(dirList.get(i).toString());
						filesAddresses.add(dirList.get(i));
						counter++;
						//System.out.println("Found " + e + " at " + dirList.get(i).toString());
					}
				}
			}
			i++;
		}
		
		return counter;
	}
	
	int countFilesRoot(File dir, String[] ext) {
		// Variables
		List<File> dirList = Arrays.asList(dir.listFiles());
		int i = 0;
		int counter = 0;
		
		// Loop through files/directories and add elements that comply with extensions clause
		while (i < dirList.size()) {
			if (dirList.get(i).isFile()) {
				for(String e : ext) {
					if (dirList.get(i).toString().contains(e)) {
						filesToProcess.add(dirList.get(i).toString());
						filesAddresses.add(dirList.get(i));
						counter++;
						//System.out.println("Found " + e + " at " + dirList.get(i).toString());
					}
				}
			}
			i++;
		}
		
		return counter;
	}
}
