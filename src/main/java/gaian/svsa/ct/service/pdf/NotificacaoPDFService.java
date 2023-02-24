package gaian.svsa.ct.service.pdf;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.util.NegocioException;

public class NotificacaoPDFService implements Serializable {
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(NotificacaoPDFService.class);
	
	/*
	 * Emissão de Notificação
	 * 
	 */
	
	// para stream e impressão de Emissão de Atestado
	
	public ByteArrayOutputStream generateStream(Denuncia denuncia, String s3Key, String secretaria) throws NegocioException {
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			PdfWriter writer = new PdfWriter(baos);
	        // Creating a PdfDocument  
	        PdfDocument pdf = new PdfDocument(writer);	        
	        // Creating a Document   
	        Document document = new Document(pdf); 		        
	        
			document.setMargins(0, 50, 50, 50);

	        
	        // gera impressão do Atestado
	        generateContent(document, denuncia, s3Key, secretaria);
	        
	        return baos;
	        
		}catch (Exception ex) {
	    	log.error("error: " + ex.getMessage());	    	
	    	throw new NegocioException("Erro na montagem do PDF: " + ex.getMessage());
	    }
	}
	
	// para download de Emissão de Atestado
	public void generatePDF(String dest, String nome, Denuncia denuncia, String s3Key, String secretaria) throws Exception {
		
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
		
		generateContent(document, denuncia, s3Key, secretaria);
		
	}
private void generateContent(Document document, Denuncia denuncia, String s3Key, String secretaria) throws Exception {
		
		PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
		TextAlignment align = TextAlignment.JUSTIFIED;
		
		/* 
	     * Header 
	     */		
		headerAtestado(document, denuncia.getTecnico().getUnidade().getNome(), denuncia, secretaria);
		//header(document, "Sistema Único de Assistência Social - SUAS");
		
		Paragraph line = new Paragraph("\nNotificação");
		line.setFontSize(24);
		line.setFont(font);
		line.setTextAlignment(TextAlignment.CENTER);
		document.add(line);
		
		if (denuncia.getPessoa().getNomeMae().isEmpty() && denuncia.getPessoa().getNomePai().isEmpty() == false) {
			Paragraph line1 = new Paragraph("\nFico o (a) Sr.(a) " + denuncia.getPessoa().getNomePai());
			line1.setFontSize(16);
			line1.setFont(font);
			line1.setTextAlignment(align);
			document.add(line1);

			Paragraph line2 = new Paragraph("\nResidente ao endereço: " + denuncia.getPessoa().getEnderecoPai()
					+ " \nNOTIFICADO (A), nos termos do inciso VII, do Artigo 136, da lei Federal "
					+ "nº 8.069, de 13 de Julho de 1990, que instituiu o Estatuto da Criança e do Adolescente, a "
					+ "Comparecer no dia ________________________ de ___________________________ de 20___ às _________________hs.,\n");
			line2.setFontSize(16);
			line2.setFont(font);
			line2.setTextAlignment(align);
			document.add(line2);
		} else {
			Paragraph line1 = new Paragraph("\nFico o (a) Sr.(a) " + denuncia.getPessoa().getNomeMae());
			line1.setFontSize(16);
			line1.setFont(font);
			line1.setTextAlignment(align);
			document.add(line1);

			Paragraph line2 = new Paragraph("\nResidente ao endereço: " + denuncia.getPessoa().getEndereco()
					+ " \nNOTIFICADO (A), nos termos do inciso VII, do Artigo 136, da lei Federal "
					+ "nº 8.069, de 13 de Julho de 1990, que instituiu o Estatuto da Criança e do Adolescente, a "
					+ "Comparecer no dia ________________________ de ___________________________ de 20___ às _________________hs.,\n");
			line2.setFontSize(16);
			line2.setFont(font);
			line2.setTextAlignment(align);
			document.add(line2);
		}
			
		Paragraph line3 = new Paragraph("na sede do CONSELHO TUTELAR, sito ao endereço \n"+ denuncia.getUnidade().getEndereco() 
		+ " para _________________________________________________________________________________" + " sob as penas do Artigo 236 da mesma lei acima mencionada.");
		line3.setFontSize(16);
		line3.setFont(font);
		line3.setTextAlignment(align);
		document.add(line3);	
		
	//	Paragraph line4 = new Paragraph("\nSalto: " + DateUtils.parseDateToString(denuncia.getDataEmissao()) );
	//	line4.setFontSize(16);
	//	line4.setFont(font);
	//	line4.setTextAlignment(align);
	//	document.add(line4);
		
		Paragraph line5 = new Paragraph("\n Salto, "  
		+ "______" + " de" + "_________________" + "de 20" + "_____," + "\n _______________________________________");
		line5.setFontSize(16);
		line5.setFont(font);
		line5.setTextAlignment(TextAlignment.RIGHT);
		document.add(line5);
		
		Paragraph line6 = new Paragraph("\nRecebi a Primeira Via\n" + "_______________________________________");
		line6.setFontSize(16);
		line6.setFont(font);
		line6.setTextAlignment(TextAlignment.LEFT);
		document.add(line6);
		
		Paragraph line7 = new Paragraph("\nCOMPARECIMENTO OBRIGATÓRIO");
		line7.setFontSize(20);
		line7.setBold();
		line7.setFont(font);
		line7.setTextAlignment(TextAlignment.CENTER);
		document.add(line7);
		

		document.close();		
	}


	private void headerAtestado(Document document, String unidade, Denuncia denuncia, String secretaria) throws Exception {
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
				+ denuncia.getUnidade().getEndereco()
						+ "\nFone: " + denuncia.getUnidade().getEndereco().getTelefoneContato() 
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
