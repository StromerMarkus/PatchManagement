package edu.hm.stromer.bachelorthesis.patchmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;

import edu.hm.stromer.bachelorthesis.lib.diff_match_patch;
import edu.hm.stromer.bachelorthesis.lib.diff_match_patch.Patch;


/**
 * Connector connect the handler and apply methods for patching and matching
 * 
 * @author Markus Stromer
 *
 */
public class Connector {

	private File backup = null;

	private diff_match_patch patcher = new diff_match_patch();

	/**
	 * Get-method for saved sample solution
	 * 
	 * @return File location of backup
	 */
	public File getBackup() {
		return backup;
	}
	
	/**
	 * Set-method for saved sample solution
	 * 
	 * @param backup File location of backup
	 */
	public void setBackup(File backup) {
		this.backup = backup;
	}
	
	/**
	 * Search recursively the backup solution for specified file
	 * 
	 * @param diff referenced file that must be searched
	 * @return File object of the found source or null if no file found
	 * @throws FileNotFoundException no backup solution available
	 */
	public  File getBackupSolution(File diff) throws FileNotFoundException {
		if(backup != null) {

			return searchRecursive(diff.getName(), backup);


		} else {
			throw new FileNotFoundException("no solution found");
		}
	}

	/**
	 * Recursive search of directory
	 * 
	 * @param name filename that is beeing searched
	 * @param solution directory to search
	 * @return File object found in directory or null if no or more than one files found
	 */
	private  File searchRecursive(String name, File solution) {

		List<File> result = new ArrayList<>();

		File[] files = solution.listFiles();
		for(File file : files) {
			if(file.isDirectory() ) {
				File recursiveFile = searchRecursive(name, file);
				if(recursiveFile != null) {
					result.add(recursiveFile);
				}
			} else {
				if(name.equals(file.getName())) {
					result.add(file);
				}
			}
		}

		if(result.size() == 1) {
			return result.get(0);
		}
		return null;
	}

	/**
	 * Generate mutants with given patch-files on source
	 * 
	 * @param patchDir directory of patch-files
	 * @param solutionDir directory of solution to be changed
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public  void applyPatches(File patchDir, File solutionDir) throws IllegalArgumentException, IOException {
		if(patchDir.isDirectory() && solutionDir.isDirectory()) {
			File[] patchFiles = patchDir.listFiles();

			for(File patchFile : patchFiles) {
				if (patchFile.getName().endsWith(".diff")) {
					File solutionFile = searchRecursive(patchFile.getName().replace(".diff", ".java"), solutionDir);
					applySinglePatch(patchFile, solutionFile);
				}
			}

		} 
	}

	/**
	 * Generate single mutant with given patch-file on source
	 * 
	 * @param patchFile 
	 * @param solutionFile
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	private  void applySinglePatch(File patchFile, File solutionFile) throws IllegalArgumentException, IOException {

		LinkedList<Patch> patches = (LinkedList<Patch>) patcher.patch_fromText(getContent(patchFile));
		String content = getContent(solutionFile);
		Object[] patchResult = patcher.patch_apply(patches, content);
		String textResult = (String) patchResult[0];
		boolean[] boolResult = (boolean[]) patchResult[1];
		FileWriter writer = new FileWriter(solutionFile);
		writer.write(textResult);
		writer.flush();
		writer.close();
		MessageDialog.openInformation(null, "File: " + patchFile.getName(), getReportMessage(boolResult));

	}

	/**
	 * String representation for given results
	 * 
	 * @param results boolean array of results
	 * @return Reportmessage that can be displayed on console or dialog
	 */
	private  String getReportMessage(boolean[] results) {

		String message = "";
		for(int i = 0; i < results.length; i++) {
			message += "Diff " + (i + 1) + ":";
			if(results[i]) {
				message += " success";
			} else {
				message += " failed";
			}
			message += "\n";
		}

		return message;

	}





	/**
	 * Read the given file and convert read bytes into string
	 * 
	 * @param file to be read
	 * @return content of the file
	 * @throws IOException
	 */
	private  String getContent(File file) throws IOException {

		FileReader reader = new FileReader(file);
		char[] buffer = new char[(int) file.length()];
		reader.read(buffer);
		reader.close();
		return new String(buffer);	
	}

	/**
	 * Genertate single patch-file compared of backup-file and given diffed file
	 * 
	 * @param diffed File that has been changed
	 * @param output Output file to be written
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public  void writePatch(File diffed, File output) throws FileNotFoundException, IOException {

		String origin = getContent((getBackupSolution(diffed)));
		String failure = getContent(diffed);

		List<Patch> patches = patcher.patch_make(origin, failure);
		if(patches.size() > 0) {
			FileWriter writer = new FileWriter(output);

			for(Patch patch: patches) {
				writer.write(patch.toString());
			}
			writer.flush();
			writer.close();
		} else {
			output.delete();
			System.out.println(diffed.getName() + ": no difference");
		}

	}
	
	/**
	 * Restore the eclipse workspace to saved backup solution
	 */
	public  void restore() {

		if(backup != null) {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			String projectName = backup.getName().split("[0-9]")[0];
			for(IProject project : projects) {
				if(projectName.equals(project.getName())) {
					restore(project);
					return;
				}
			}
		}
	}

	private  void restore(IProject project) {
		String projectPath = project.getLocation().toOSString();
		File projectFile = new File(projectPath);
		try {
			copyDirectory(backup, projectFile);
		} catch (IOException e) {
			Activator.getDefault().writeErrorLog(e.getMessage(), e);
		}
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE,null);
		} catch (CoreException e) {
			Activator.getDefault().writeErrorLog(e.getMessage(), e);
		}
	}

	/**
	 * Copy recursively the complete directory to specified location
	 * 
	 * @param source directory that have to be copied
	 * @param destination location where directory have to be copied
	 * @throws IOException
	 */
	public  void copyDirectory(File source, File destination) throws IOException {

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
