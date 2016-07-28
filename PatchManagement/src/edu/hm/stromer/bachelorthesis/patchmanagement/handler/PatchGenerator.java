package edu.hm.stromer.bachelorthesis.patchmanagement.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.hm.stromer.bachelorthesis.patchmanagement.Activator;
import edu.hm.stromer.bachelorthesis.patchmanagement.Connector;

/**
 * This handler generates patch-files of changes between backup solution and eclipse workspace
 * 
 * @author Markus Stromer
 *
 */
public class PatchGenerator implements IHandler {

	private Activator activator = Activator.getDefault();
	private Connector connector = activator.getConnector();
	
	/**
	 *@see org.eclipse.core.commands.IHandler
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {


	}

	/**
	 * @see org.eclipse.core.commands.IHandler
	 */
	@Override
	public void dispose() {


	}

	/**
	 * Generates patch-files of changed solution.
	 * 
	 * After applied changes, differences found will be written into patch-files and stored to specific location
	 * 
	 * @see org.eclipse.core.commands.IHandler
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(connector.getBackup() == null) {
			
			activator.writeErrorLog("No solution found");
		} else {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null)
			{
				IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof IAdaptable)
				{
					IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);

					File projectFile = new File(project.getLocation().toOSString());

					DirectoryDialog directoryDialog = new DirectoryDialog(window.getShell(), SWT.OPEN);
					directoryDialog.setFilterPath(projectFile.getAbsolutePath());
					directoryDialog.setMessage("Select Patch Directory (outside project!)");

					String directoryName = directoryDialog.open();

					File patchDirectory = new File(directoryName);
					try {
						createPatches(projectFile, patchDirectory);
						connector.restore();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.core.commands.IHandler
	 */
	@Override
	public boolean isEnabled() {

		return true;
	}

	/**
	 * @see org.eclipse.core.commands.IHandler
	 */
	@Override
	public boolean isHandled() {

		return true;
	}

	/**
	 * @see org.eclipse.core.commands.IHandler
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {


	}

	/**
	 * Search all java source files and check for pending changes. If differences found generate patch-file
	 * 
	 * @param projectFile directory of project
	 * @param patchDir directory of stored patch-files
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void createPatches(File projectFile, File patchDir) throws FileNotFoundException, IOException {
		if(projectFile.isDirectory()) {
			File[] files = projectFile.listFiles();
			for(File file: files) {
				createPatches(file, patchDir);
			}
		} else {
			if(projectFile.getName().endsWith(".java")) {
				String patchName = projectFile.getName().replace(".java", ".diff");
				File patchFile = new File(patchDir, patchName);
				connector.writePatch(projectFile, patchFile);
			}
		}
	}


}
