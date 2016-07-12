package patchmanagement;

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

import javax.swing.JOptionPane;

import lib.diff_match_patch;
import lib.diff_match_patch.Patch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public abstract class Connector {
	
	public static File BACKUP = null;
	
	private static diff_match_patch patcher = new diff_match_patch();
	
	public static File getBackupSolution(File diff) throws FileNotFoundException {
		if(BACKUP != null) {
			
			return searchRecursive(diff.getName(), BACKUP);
	
			
		} else {
			throw new NullPointerException("no solution found");
		}
	}
	
	private static File searchRecursive(String name, File solution) {

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
	
	public static boolean[] applyPatches(File patchDir, File solutionDir) throws IllegalArgumentException, IOException {
		if(patchDir.isDirectory() && solutionDir.isDirectory()) {
			File[] patchFiles = patchDir.listFiles();
			
			for(File patchFile : patchFiles) {
				File solutionFile = searchRecursive(patchFile.getName().replace(".diff", ".java"), solutionDir);
				applySinglePatch(patchFile, solutionFile);
			}
			return null;
		} else {
			return null;
		}
	}
	
	private static void applySinglePatch(File patchFile, File solutionFile) throws IllegalArgumentException, IOException {
		
		LinkedList<Patch> patches = (LinkedList<Patch>) patcher.patch_fromText(getContent(patchFile));
		String content = getContent(solutionFile);
		Object[] patchResult = patcher.patch_apply(patches, content);
		String textResult = (String) patchResult[0];
		boolean[] boolResult = (boolean[]) patchResult[1];
		FileWriter writer = new FileWriter(solutionFile);
		writer.write(textResult);
		writer.flush();
		writer.close();
		JOptionPane.showMessageDialog(null, getReportMessage(boolResult));
		
	}
	
	private static String getReportMessage(boolean[] results) {
		
		String message = "";
		for(boolean result : results) {
			message += result;
			message += "\n";
		}
		
		return message;
		
	}
	

	



	private static String getContent(File file) throws IOException {
		
		FileReader reader = new FileReader(file);
		char[] buffer = new char[(int) file.length()];
		reader.read(buffer);
		reader.close();
		return new String(buffer);	
	}
	
	public static void writePatch(File diffed, File output) throws FileNotFoundException, IOException {
		
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
			System.out.println("no difference");
		}
		
	}
	
	public static void restore() {
		
		if(BACKUP != null) {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			String projectName = BACKUP.getName().split("[0-9]")[0];
			for(IProject project : projects) {
				if(projectName.equals(project.getName())) {
					restore(project);
					return;
				}
			}
		}
	}
	
	private static void restore(IProject project) {
		String projectPath = project.getLocation().toOSString();
		File projectFile = new File(projectPath);
		try {
			copyDirectory(BACKUP, projectFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void copyDirectory(File source, File destination) throws IOException {
		
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
