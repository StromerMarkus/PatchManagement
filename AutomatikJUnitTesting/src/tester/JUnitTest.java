package tester;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.itextpdf.text.DocumentException;


public class JUnitTest {
	
	private File dir;
	
	private List<String> testClasses;
	
	public JUnitTest(File studentDir, List<String> testClasses) {
		this.dir = studentDir;
		this.testClasses = testClasses;
	}

	public void junitTest() throws IOException, ClassNotFoundException, DocumentException {
		
		List<File> files = getAllJava(dir);
		PDFCreator pdf = new PDFCreator(dir.getAbsolutePath() + File.separator +"result.pdf", dir.getName());
		String pdfMessage = "";

		
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		StandardJavaFileManager fileManager = compiler.getStandardFileManager( null, null, null );

		Iterable<? extends JavaFileObject> units;

		units = fileManager.getJavaFileObjects(files.toArray(new File[files.size()]));


		CompilationTask task = compiler.getTask( null, fileManager, diagnostics, null, null, units );
		boolean compiled = task.call();

		fileManager.close();
		
		if(compiled) {

			pdfMessage += "compiled successfully\n";
			URL url = dir.toURI().toURL();
			
		    URLClassLoader cl = new URLClassLoader( new URL[]{ url } );
		    for(String testClass : testClasses) {
		    	Result result = JUnitCore.runClasses(cl.loadClass(testClass));
		    	if(result.wasSuccessful()) {
		    		pdfMessage += testClass + " was successful\n";
		    	} else {
		    		pdfMessage += testClass + " failed (" + result.getFailureCount() + " failures)\n";
		    		for(Failure failure : result.getFailures()) {
		    			pdfMessage += failure.getTestHeader() + "\n";
		    			pdfMessage += failure.getMessage() + "\n";
		    		}
		    	}
		    }
		    cl.close();

		} else {
			pdfMessage += "compilation impossible\n";
			for(Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
	
				pdfMessage += diagnostic + "\n\n";
			}
		}
		pdf.createPDF(pdfMessage);
	    
	}
	
	private static List<File> getAllJava(File dir) {
		List<File> files = new ArrayList<>();
		for(File file : dir.listFiles()) {
			if(file.isDirectory()) {
				files.addAll(getAllJava(file));
			} else if(file.getName().endsWith(".java")){
				files.add(file);
			}
		}
		return files;
	}

}
