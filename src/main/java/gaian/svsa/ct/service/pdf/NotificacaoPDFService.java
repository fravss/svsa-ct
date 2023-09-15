package gaian.svsa.ct.service.pdf;

import java.io.Serializable;

import org.apache.log4j.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import gaian.svsa.ct.modelo.to.NotificacaoTO;
import gaian.svsa.ct.util.DateUtils;
import gaian.svsa.ct.util.NegocioException;

public class NotificacaoPDFService implements Serializable {
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(NotificacaoPDFService.class);
	
	SimpleDateFormat formato1 = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");
	SimpleDateFormat formato2 = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
	
	Date dataAtual = DateUtils.getDia();
	
	/*
	 * Emissão de Notificação
	 * 
	 */
	
	// para stream e impressão de Emissão de Atestado
	
	public ByteArrayOutputStream generateStream(NotificacaoTO nto, String s3Key, String secretaria) throws NegocioException {
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			PdfWriter writer = new PdfWriter(baos);
	        // Creating a PdfDocument  
	        PdfDocument pdf = new PdfDocument(writer);	        
	        // Creating a Document   
	        Document document = new Document(pdf); 		        
	        
			document.setMargins(0, 50, 50, 50);

	        
	        // gera impressão do Atestado
	        generateContent(document, nto, s3Key, secretaria);
	        
	        return baos;
	        
		}catch (Exception ex) {
	    	log.error("error: " + ex.getMessage());	    	
	    	throw new NegocioException("Erro na montagem do PDF: " + ex.getMessage());
	    }
	}
	
	// para download de Emissão de Atestado
	public void generatePDF(String dest, String nome, NotificacaoTO nto, String s3Key, String secretaria) throws Exception {
		
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
		
		generateContent(document, nto, s3Key, secretaria);
		
	}
	
	private void generateContent(Document document, NotificacaoTO nto, String s3Key, String secretaria) throws Exception {
		
		PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
		TextAlignment align = TextAlignment.JUSTIFIED;
		
		
		
		// Header 		
		headerAtestado(document, nto.getTecnico().getNome(), nto, secretaria);
		
		Paragraph line = new Paragraph("\nNotificação");
		line.setFontSize(24);
		line.setFont(font);
		line.setTextAlignment(TextAlignment.CENTER);
		document.add(line);

		Paragraph line1 = new Paragraph("\nFico o (a) Sr.(a) " + nto.getNomeNotificacao() + "\n");
		line1.setFontSize(14);
		line1.setFont(font);
		line1.setTextAlignment(align);
		document.add(line1);

		Paragraph line2 = new Paragraph(
				"Residente a Rua: " + nto.getEndereco() + " nº " + nto.getNumero() + "\nBairro: " + nto.getBairro()
						+ " \nNOTIFICADO (A), nos termos do inciso VII, do Artigo 136, da lei Federal "
						+ "nº 8.069, de 13 de Julho de 1990, que instituiu o Estatuto da Criança e do Adolescente, a "
						+ "comparecer no dia: " + formato1.format(nto.getDataComparecimento()));
		line2.setFontSize(14);
		line2.setFont(font);
		line2.setTextAlignment(align);
		document.add(line2);
			
		Paragraph line3 = new Paragraph("na sede do CONSELHO TUTELAR, sito ao endereço: " + nto.getUnidade().getEndereco()
		+ " para: " + nto.getMotivo() 
		+ " sob as penas do Artigo 236 da mesma lei acima mencionada.");
		line3.setFontSize(14);
		line3.setFont(font);
		line3.setTextAlignment(align);
		document.add(line3);	

		Paragraph line5 = new Paragraph("\n Salto, " + formato2.format(dataAtual));
		line5.setFontSize(16);
		line5.setFont(font);
		line5.setTextAlignment(TextAlignment.RIGHT);
		document.add(line5);
		
		Paragraph line6 = new Paragraph("\nRecebi a Primeira Via\n" + "_______________________________________");
		line6.setFontSize(16);
		line6.setFont(font);
		line6.setTextAlignment(TextAlignment.LEFT);
		document.add(line6);
		
		Paragraph line7 = new Paragraph("\n\n\nCOMPARECIMENTO OBRIGATÓRIO");
		line7.setFontSize(20);
		line7.setBold();
		line7.setFont(font);
		line7.setTextAlignment(TextAlignment.CENTER);
		document.add(line7);
		
		/////////////////////////////////
		//PAGINA 2 - SEGUNDA VIA 
		/////////////////////////////////
		document.add(new AreaBreak());
		
		Paragraph line8 = new Paragraph("\n");
		line8.setFontSize(24);
		line8.setFont(font);
		line8.setTextAlignment(TextAlignment.CENTER);
		document.add(line8);
		
		headerAtestado(document, nto.getTecnico().getNome(), nto, secretaria);
		
		Paragraph line9 = new Paragraph("\nNotificação - SEGUNDA VIA");
		line9.setFontSize(24);
		line9.setFont(font);
		line9.setTextAlignment(TextAlignment.CENTER);
		document.add(line9);
		
		Paragraph line10 = new Paragraph("\nFico o (a) Sr.(a) " + nto.getNomeNotificacao() + "\n");
		line10.setFontSize(14);
		line10.setFont(font);
		line10.setTextAlignment(align);
		document.add(line10);
		
		
		Paragraph line11 = new Paragraph(
				"Residente a Rua: " + nto.getEndereco() + " nº " + nto.getNumero() + "\nBairro: " + nto.getBairro()
						+ " \nNOTIFICADO (A), nos termos do inciso VII, do Artigo 136, da lei Federal "
						+ "nº 8.069, de 13 de Julho de 1990, que instituiu o Estatuto da Criança e do Adolescente, a "
						+ "comparecer no dia: " + formato1.format(nto.getDataComparecimento()));
		line11.setFontSize(14);
		line11.setFont(font);
		line11.setTextAlignment(align);
		document.add(line11);
		
		Paragraph line12 = new Paragraph(" na sede do CONSELHO TUTELAR, sito ao endereço " + nto.getUnidade().getEndereco()
		+ " para: " + nto.getMotivo() 
		+ " sob as penas do Artigo 236 da mesma lei acima mencionada.");
		line12.setFontSize(14);
		line12.setFont(font);
		line12.setTextAlignment(align);
		document.add(line12);	
		
		Paragraph line13 = new Paragraph("\n Salto, " + formato2.format(dataAtual));
		line13.setFontSize(16);
		line13.setFont(font);
		line13.setTextAlignment(TextAlignment.RIGHT);
		document.add(line13);
		
		Paragraph line14 = new Paragraph("\nEmiti a Segunda Via\n\n" + "_______________________________________");
		line14.setFontSize(16);
		line14.setFont(font);
		line14.setTextAlignment(TextAlignment.LEFT);
		document.add(line14);
		

		document.close();		
	} 


	private void headerAtestado(Document document, String unidade, NotificacaoTO nto, String secretaria) throws Exception {
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
				+ nto.getUnidade().getEndereco()
						+ "\nFone: " + nto.getUnidade().getEndereco().getTelefoneContato()
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
