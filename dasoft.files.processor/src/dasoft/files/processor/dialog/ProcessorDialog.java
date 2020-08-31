package dasoft.files.processor.dialog;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class ProcessorDialog {
	
	String answer;
	int result;
	
	/**
	 * Method to deploy UI to select a process
	 * @param window is the window in which the event was generated
	 * @param processesNames is the name of the available processes
	 */
	public ProcessorDialog(IWorkbenchWindow window, List<String> processesNames) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(window.getShell(), new LabelProvider());
		dialog.setTitle("File Processor Wizard");
		dialog.setMultipleSelection(false);
		dialog.setEmptyListMessage("No processes found.");
		dialog.setEmptySelectionMessage("No process found.");
		dialog.setElements(processesNames.toArray());
		dialog.setAllowDuplicates(false);
		dialog.setHelpAvailable(false);
		dialog.setMessage("Select a process.");
		
		result = dialog.open();
		answer = (String) dialog.getFirstResult();
		
		dialog.close();
	}

	public String getAnswer() {
		return answer;
	}
	
	public int getResult() {
		return result;
	}
	
}
