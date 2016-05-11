package tester;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFCreator {
	
	
	private String path;
	private String student;
	
	
	public PDFCreator(String path, String student) {
		this.path = path;
		this.student = student;
	}
	
	
	public void createPDF(String message) throws DocumentException, IOException {
	       Document document = new Document(); 
	        PdfWriter.getInstance(document, new FileOutputStream(path)); 
	        document.open(); 
	        document.add(new Paragraph(student));
	        document.add(new Paragraph(message));
	        document.close(); 
	       
	}
	
	

	public static void main(String[] args) throws DocumentException, IOException {
		PDFCreator pdf = new PDFCreator("student1.pdf", "Student 1");
		pdf.createPDF("JUnitTest erfolgreich");

	}

}
