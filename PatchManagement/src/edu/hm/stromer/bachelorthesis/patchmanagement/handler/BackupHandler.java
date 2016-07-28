package edu.hm.stromer.bachelorthesis.patchmanagement.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.hm.stromer.bachelorthesis.patchmanagement.Activator;
import edu.hm.stromer.bachelorthesis.patchmanagement.Connector;

/**
 * This handler is used to backup solution in eclipse workspace to filesystem
 * 
 * @author Markus Stromer
 *
 */
public class BackupHandler implements IHandler {
	
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
	 * Execution command of this handler
	 * 
	 * Creates a new temp directory and copy marked project to that location
	 * 
	 * @see org.eclipse.core.commands.IHandler
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	
		
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IViewSite site = (IViewSite) window.getActivePage().getActivePart().getSite();
	    if (window != null)
	    {
	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IAdaptable)
	        {
	            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);

	            String path = project.getLocation().toOSString();
	            File src = new File(path);

	            try {
					Path backup = Files.createTempDirectory(project.getName());
					connector.setBackup(backup.toFile());
					connector.copyDirectory(src, connector.getBackup());
					site.getActionBars().getStatusLineManager().setMessage("Backup successful: " + connector.getBackup().getAbsolutePath());

				} catch (IOException e) {
					
					activator.writeErrorLog(e.getMessage(), e);
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
