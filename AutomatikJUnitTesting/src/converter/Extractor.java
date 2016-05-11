package converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Extractor {
	
	private String output;


	
	public Extractor(String output) {
		this.output = output;

	}
	
    public void extractArchive(File archive) throws  IOException {
    	File destDir = new File(output);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
 
        ZipFile zipFile = new ZipFile(archive);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
  

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
 
            String entryFileName = entry.getName();
 
            File dir = buildDirectoryHierarchyFor(entryFileName, destDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
 
            if (entry.getName().endsWith(".java")) {

            	
            	BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));

            	FileWriter writer = new FileWriter(new File(destDir, entryFileName));
            	char[] buffer = new char[(int) entry.getSize()];
            	reader.read(buffer);
            	writer.write(buffer);
            	writer.flush();
            	writer.close();
            	reader.close();
            	
            }
        }
                zipFile.close();
    }

    public void addTestFiles(File testDir) throws FileNotFoundException, IOException {
    	File root = new File(output);
    	for(File student : root.listFiles()) {
    		File testFolder = new File(student, testDir.getName());
    		testFolder.mkdir();
    		for(File testClass : testDir.listFiles()) {

    			List<String> importStatements = getAllJavaFiles(student);
    			try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testClass)));
    				FileWriter writer = new FileWriter(new File(testFolder, testClass.getName()))	) {
    				writer.write("package " + testDir.getName() + ";\n");
    				for(String importStatement : importStatements) {
    					if(importStatement.endsWith(".java"))
    						writer.write("import " + importStatement.replace(".java", "") + ";\n");
    				}
    				char[] buffer = new char[(int) testClass.length()];
    				reader.read(buffer);
    				writer.write(buffer);
    				writer.flush();
    			}
    		}
    		
    	}
    }
    
    private List<String> getAllJavaFiles(File student) {
    	List<String> names = new ArrayList<>();
    	for(File file : student.listFiles()) {
    		names.addAll(recursiveFiles(file,""));
    	}
    	return names;
    }
    
    private List<String> recursiveFiles(File file, String prefix) {
    	List<String> names = new ArrayList<>();
    	String fileName = prefix + file.getName();
    	if(file.isDirectory()) {
    		
    		
    		for(File sub : file.listFiles()) {
    			names.addAll(recursiveFiles(sub, fileName + "."));
    		}
    	} else {
    		names.add(fileName);
    	}
    	return names;
    }

 
    private File buildDirectoryHierarchyFor(String entryName, File destDir) {
        int lastIndex = entryName.lastIndexOf('/');
        String internalPathToEntry = entryName.substring(0, lastIndex + 1);
        return new File(destDir, internalPathToEntry);
    }
}
