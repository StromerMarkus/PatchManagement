package main;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import tester.DeleteDir;
import tester.JUnitTest;
import converter.Extractor;

public class Main {

	private static final String configFile = "config.ini";
	
	public static void main(String[] args)   {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Properties properties = new Properties();
			properties.load(new FileInputStream(new File(configFile)));
			Extractor extractor = new Extractor(properties.getProperty("outputDir"));

			extractor.extractArchive(new File(properties.getProperty("inputFile")));
			
			File testDir = new File(properties.getProperty("testClassDir"));
			extractor.addTestFiles(testDir);

			File output = new File(properties.getProperty("outputDir"));
			for(File student : output.listFiles()) {
				JUnitTest junit = new JUnitTest(student, getClassNames(testDir));
				junit.junitTest();
			}
			DeleteDir.deleteDir(output);
			JOptionPane.showMessageDialog(null, "completed");

		} catch(Exception e) {
			String error = e.toString() + " " + e.getMessage() + "\n";
			for(StackTraceElement element : e.getStackTrace()) {
				error += element.toString();
				error += "\n";
			}
			JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	private static List<String> getClassNames(File dir) {
		List<String> names = new ArrayList<>();
		
		for(File file : dir.listFiles()) {
			names.add(file.getParentFile().getName() + "." + file.getName().replace(".java", ""));
		}
		
		return names;
	}

}
