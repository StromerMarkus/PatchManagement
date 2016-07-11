package patchmanagement.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import patchmanagement.Connector;

public class BackupHandler implements IHandler {
	

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
	            System.out.println(path);
	            File src = new File(path);

	            try {
					Path backup = Files.createTempDirectory(project.getName());
					Connector.backup = backup.toFile();
					System.out.println(backup);
					copyDirectory(src, Connector.backup);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	

	private void copyDirectory(File source, File destination) throws FileNotFoundException, IOException {
		
		if(source.isDirectory()) {
			if(!destination.isDirectory()) {
				destination.mkdir();
			}
			File[] files = source.listFiles();
			for(File file: files)  {
				File dest = new File(destination, file.getName());
				copyDirectory(file,dest);
			}
		} else {
			try(FileInputStream fin = new FileInputStream(source); 
				FileOutputStream fout = new FileOutputStream(destination)) {
				
				byte[] buffer = new byte[(int) source.length()];
				fin.read(buffer);
				fout.write(buffer);
				fout.flush();
				
			}
		}
		
	}

}
