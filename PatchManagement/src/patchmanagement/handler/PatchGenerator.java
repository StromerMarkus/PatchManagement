package patchmanagement.handler;

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

import patchmanagement.Connector;

public class PatchGenerator implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(Connector.BACKUP == null) {
			System.out.println("no solution found");
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
						Connector.restore();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

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
				Connector.writePatch(projectFile, patchFile);
			}
		}
	}


}
