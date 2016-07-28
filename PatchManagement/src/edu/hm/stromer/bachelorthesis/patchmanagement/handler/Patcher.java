package edu.hm.stromer.bachelorthesis.patchmanagement.handler;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.hm.stromer.bachelorthesis.patchmanagement.Activator;
import edu.hm.stromer.bachelorthesis.patchmanagement.Connector;

/**
 * This handler generate mutants to the eclipse workspace choosing specific patch-files
 * 
 * @author Markus Stromer
 *
 */
public class Patcher implements IHandler {

	private Activator activator = Activator.getDefault();
	private Connector connector = activator.getConnector();
	
	/**
	 * @see org.eclipse.core.commands.IHandler
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
	 * Patches selected files to the eclipse workspace and create mutants
	 * 
	 * @see org.eclipse.core.commands.IHandler
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
		{
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IAdaptable)
			{
				IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);

				String path = project.getLocation().toOSString();
				File src = new File(path);

				DirectoryDialog directoryDialog = new DirectoryDialog(window.getShell(), SWT.OPEN);
				directoryDialog.setFilterPath(path);
				directoryDialog.setMessage("Select Patch Directory");

				String directoryName = directoryDialog.open();
				File patchDir = new File(directoryName);

				try {
					if(connector.getBackup() != null) {
						connector.applyPatches(patchDir, src);
						project.refreshLocal(IResource.DEPTH_INFINITE,null);
					} else {
						activator.writeErrorLog("no backup found");
					}
				} catch (IllegalArgumentException | IOException | CoreException e) {
					e.printStackTrace();
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

}
