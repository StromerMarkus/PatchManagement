package patchmanagement.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class StringFileConverter {
	
	public static String convertFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		char[] buffer = new char[(int) file.length()];
		reader.read(buffer);
		reader.close();
		return new String(buffer);
	}
	
	public static void writeString(String content, File dest) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest)));
		writer.write(content);
		writer.flush();
		writer.close();
	}

}
