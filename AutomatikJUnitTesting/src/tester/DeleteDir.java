package tester;

import java.io.File;

public class DeleteDir {
	 
	   public static void deleteDir(File path) {
	      for (File file : path.listFiles()) {
	         if (file.isDirectory()) {
	            deleteDir(file);
	         } else if(file.getName().endsWith(".class")) {
	        	 file.delete();
	         }
	        
	      }

	   }
	 

	}