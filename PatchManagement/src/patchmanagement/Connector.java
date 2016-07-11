package patchmanagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.diff_match_patch;
import lib.diff_match_patch.Patch;

public abstract class Connector {
	
	public static File backup = null;
	
	public static diff_match_patch patcher = new diff_match_patch();
	
	public static File getBackupSolution(File diff) throws FileNotFoundException {
		if(backup != null) {
			
			List<File> result = searchRecursive(diff, backup);
			if(result.size() == 1) {
				return result.get(0);
			} 
			return null;
			
		} else {
			throw new NullPointerException("no solution found");
		}
	}
	
	private static List<File> searchRecursive(File diff, File solution) {
		List<File> result = new ArrayList<>();
		
		File[] files = solution.listFiles();
		for(File file : files) {
			if(file.isDirectory() ) {
				result.addAll(searchRecursive(diff, file));
			} else {
				if(diff.getName().equals(file.getName())) {
					result.add(file);
				}
			}
		}
		
		return result;
	}
	


	public static String getContent(File file) throws IOException {
		
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

}
