package patchmanagement.handler;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import patchmanagement.Connector;

public class Patcher implements IHandler {

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

	            JFileChooser chooser = new JFileChooser();
	            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            
	            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	            	File patchDir = chooser.getSelectedFile();
	            	try {
						Connector.applyPatches(patchDir, src);
					} catch (IllegalArgumentException | IOException e) {
						// TODO Auto-generated catch block
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

}
