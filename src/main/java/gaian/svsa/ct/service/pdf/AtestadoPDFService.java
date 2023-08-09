package gaian.svsa.ct.service.pdf;

import java.io.Serializable;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.io.font.constants.StandardFonts;
//import com.itextpdf.io.image.ImageData;
//import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;


import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.enums.Grupo;
import gaian.svsa.ct.util.NegocioException;

import org.apache.log4j.Logger;

public class AtestadoPDFService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(AtestadoPDFService.class);
	
	/*
	 * Emissão de Atestado
	 * 
	 */
	
	// para stream e impressão de Emissão de Atestado
	public ByteArrayOutputStream generateStream(Pessoa pessoa, String s3Key, String secretaria) throws NegocioException {
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			PdfWriter writer = new PdfWriter(baos);
	        // Creating a PdfDocument  
	        PdfDocument pdf = new PdfDocument(writer);	        
	        // Creating a Document   
	        Document document = new Document(pdf); 		        
	        
			document.setMargins(0, 50, 50, 50);

	        
	        // gera impressão do Atestado
	        generateContent(document, pessoa, s3Key, secretaria);
	        
	        return baos;
	        
		}catch (Exception ex) {
	    	log.error("error: " + ex.getMessage());	    	
	    	throw new NegocioException("Erro na montagem do PDF: " + ex.getMessage());
	    }
	} 
	
	// para download de Emissão de Atestado
	 public void generatePDF(String dest, String nome, Pessoa pessoa, String s3Key, String secretaria) throws Exception {
		
		// Creating a PdfWriter			  
		PdfWriter writer = new 
		PdfWriter(dest);		  
		// Creating a PdfDocument       
		PdfDocument pdf = new PdfDocument(writer);
		// Adding an empty page 
        pdf.addNewPage();        
		// Creating a Document
		Document document = new Document(pdf);		
		
		document.setMargins(50, 50, 50, 50);		
		
		generateContent(document, pessoa, s3Key, secretaria);
		
	} 

		
	private void generateContent(Document document, Pessoa pessoa, String s3Key, String secretaria) throws Exception {
		
		PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
		TextAlignment align = TextAlignment.JUSTIFIED;
		
		//Header 	
		headerAtestado(document, pessoa.getFamilia().getDenuncia().getConselheiro().getNome(), pessoa, secretaria);
	
		Paragraph line = new Paragraph("\nAtestado");
		line.setFontSize(24);
		line.setFont(font);
		line.setTextAlignment(TextAlignment.CENTER);
		document.add(line);
		
		Paragraph line1 = new Paragraph("\nO Conselho Tutelar por seu Conselheiro abaixo assinado atesta para os devidos fins que:");
		line1.setFontSize(16);
		line1.setFont(font);
		line1.setTextAlignment(align);
		document.add(line1);
		
		Paragraph line2 = new Paragraph(pessoa.getNome() + "\n Portadora do RG:" + pessoa.getRg());
		
		line2.setFontSize(16);
		line2.setFont(font);
		line2.setTextAlignment(align);
		document.add(line2);
		
		Paragraph line3 = new Paragraph("Esteve presente neste órgão cumprindo assim a notificação recebida de acordo com o " +
		"Estatuto da Criança e do Adolescente ECA - Lei Federal Nº 8.069, nos termos do inciso VII, do Artigo 136 e sobe pena do art." +
		"236 da mesma lei mencionada acima.");
		line3.setFontSize(16);
		line3.setFont(font);
		line3.setTextAlignment(align);
		document.add(line3);	
		
	//	Paragraph line4 = new Paragraph("\nSalto: " + DateUtils.parseDateToString(pessoa.getDataEmissao()) );
	//	line4.setFontSize(16);
	//	line4.setFont(font);
	//	line4.setTextAlignment(align);
	//	document.add(line4);
		
		Paragraph line5 = new Paragraph("\n		Permaneceu Neste lugar: " + 
		"\nDia: " + "______" + " de" + "_________________" + "de 20" + "_____," + "das" + "_______" + "ás" + "______" + "Horas.");
		line5.setFontSize(16);
		line5.setFont(font);
		line5.setTextAlignment(align);
		document.add(line5);
		
		Paragraph line6 = null;
	
		if(pessoa.getFamilia().getDenuncia().getConselheiro().getGrupo() == Grupo.COORDENADORES) {
			if(pessoa.getFamilia().getDenuncia().getConselheiro().getRegistroProfissional() != null && !pessoa.getFamilia().getDenuncia().getConselheiro().getRegistroProfissional().isEmpty() ) {
				line6 = new Paragraph("\n\n_________________________________________\n" 
					+ pessoa.getFamilia().getDenuncia().getConselheiro().getNome() + "\n"
					+ pessoa.getFamilia().getDenuncia().getConselheiro().getRole() + " "
					+ pessoa.getFamilia().getDenuncia().getConselheiro().getUnidade().getNome() + "\n Nº de Registro Profissional:" 
					+ pessoa.getFamilia().getDenuncia().getConselheiro().getRegistroProfissional());
			}
			else {
				line6 = new Paragraph("\n\n_________________________________________\n" 
						+ pessoa.getFamilia().getDenuncia().getConselheiro().getNome() + "\n"
						+ pessoa.getFamilia().getDenuncia().getConselheiro().getRole() + " - Conselheiro(a) "
						+ pessoa.getFamilia().getDenuncia().getConselheiro().getUnidade().getNome());
						
			}
		}
		else {
			if(pessoa.getFamilia().getDenuncia().getConselheiro().getRegistroProfissional() != null && !pessoa.getFamilia().getDenuncia().getConselheiro().getRegistroProfissional().isEmpty() ) {
				line6 = new Paragraph("\n\n_________________________________________\n" 
					+ pessoa.getFamilia().getDenuncia().getConselheiro().getNome() + "\n" 
					+ pessoa.getFamilia().getDenuncia().getConselheiro().getRole() + "\n" 
					+ pessoa.getFamilia().getDenuncia().getConselheiro().getRegistroProfissional());
			}
			else {
				line6 = new Paragraph("\n\n_________________________________________\n" 
						+ pessoa.getFamilia().getDenuncia().getConselheiro().getNome() + "\n" 
						+ pessoa.getFamilia().getDenuncia().getConselheiro().getRole()); 
			}
		}	
		
		
		line6.setFontSize(12);
		line6.setFont(font);
		line6.setTextAlignment(TextAlignment.CENTER);
		document.add(line6);

		document.close();	
	} 

	
	 private void headerAtestado(Document document, String unidade, Pessoa pessoa, String secretaria) throws Exception {
		try {
			PdfFont fontTitulo = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
			log.info(unidade);
				Paragraph titulo = new Paragraph(
						"Conselho Tutelar de Salto");
			//	titulo.setBackgroundColor(ColorConstants.BLUE);
				titulo.setFontSize(24);
				titulo.setFont(fontTitulo);
				titulo.setTextAlignment(TextAlignment.CENTER);
				titulo.getMarginTop();
				document.add(titulo);
				
				Paragraph titulo2 = new Paragraph("Lei Federal nº 8.069, de 13 de julho de 1990\n" 
				+ pessoa.getFamilia().getDenuncia().getUnidade().getEndereco()
						+ "\nFone: " + pessoa.getFamilia().getDenuncia().getUnidade().getEndereco().getTelefoneContato()
						+ "\n");
			//	titulo2.setBackgroundColor(ColorConstants.BLUE);
				titulo2.setFontSize(12);
				titulo2.setFont(fontTitulo);
				titulo2.setTextAlignment(TextAlignment.CENTER);
				titulo2.getMarginTop();
				document.add(titulo2);
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Não foi possivel localizar a unidade.");
		}
	} 
}
