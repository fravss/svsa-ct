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
import gaian.svsa.ct.util.DateUtils;
import gaian.svsa.ct.util.NegocioException;

public class DenunciaPDFService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(AtestadoPDFService.class);
	
	/*
	 * Emissão da Denúncia
	 * 
	 */
	
	// para stream e impressão de Emissão da Denúncia
	public ByteArrayOutputStream generateStream(Denuncia denuncia, String s3Key, String secretaria) throws NegocioException {
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			PdfWriter writer = new PdfWriter(baos);
	        // Creating a PdfDocument  
	        PdfDocument pdf = new PdfDocument(writer);	        
	        // Creating a Document   
	        Document document = new Document(pdf); 		        
	        
			document.setMargins(0, 50, 50, 50);

	        
	        // gera impressão da Denúncia
	        //generateContent(document, denuncia, s3Key, secretaria);
	        
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
		
		//generateContent(document, denuncia, s3Key, secretaria);
		
	}

		
	/*private void generateContent(Document document, Denuncia denuncia, String s3Key, String secretaria) throws Exception {
		
		PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
		TextAlignment align = TextAlignment.JUSTIFIED;
		
		//Header 	
		
		headerAtestado(document, denuncia.getTecnico().getUnidade().getNome(), denuncia, secretaria);
		//header(document, "Sistema Único de Assistência Social - SUAS");
		
		Paragraph line1 = new Paragraph("\nREGISTRO DE DENÚNCIA		DATA: " + DateUtils.parseDateToString(denuncia.getDataEmissao()) 
											+ "		N° " + denuncia.getCodigo() + "/" + denuncia.getAno());
		line1.setFontSize(16);
		line1.setFont(font);
		line1.setTextAlignment(align);
		document.add(line1);
		
		Paragraph line2 = new Paragraph("Nome da mãe: " + denuncia.getPessoa().getNomeMae() + "\n "
										+ "Endereço: " + denuncia.getPessoa().getEndereco() + "\n "
										+ "Telefone: " + denuncia.getPessoa().getTelefone());
		line2.setFontSize(14);
		line2.setFont(font);
		line2.setTextAlignment(align);
		document.add(line2);
		
		Paragraph line3 = new Paragraph("CRIANÇA / ADOLESCENTE");
		line3.setFontSize(16);
		line3.setFont(font);
		line3.setTextAlignment(align);
		document.add(line3);	
		
		Paragraph line4 = new Paragraph("Nome Criança: " + denuncia.getPessoa().getNomeCrianca() + "		Data Nasc. " + DateUtils.parseDateToString(denuncia.getPessoa().getDataNascimento()) + "\n "
										+ "Nome Pai: " + denuncia.getPessoa().getNomePai() + "		Telefone: " + denuncia.getPessoa().getTelefonePai() + "\n "
										+ "Endereço do pai: " + denuncia.getPessoa().getEnderecoPai() + "\n "
										+ "Escola: " + denuncia.getPessoa().getEscola() + "		Série: " + denuncia.getPessoa().getSerie() 
										/* + " Periodo: " + denuncia.getPessoa().getPeriodo() */     /* );
		line1.setFontSize(14);
		line1.setFont(font);
		line1.setTextAlignment(align);
		document.add(line4);
		
		if(denuncia.getPessoa().getNomeTerceiro() != null) {
			Paragraph line5 = new Paragraph("DENUNCIANTE: " + denuncia.getPessoa().getNomeTerceiro() + "		Telefone: " + denuncia.getPessoa().getTelefoneTerceiro());
			line3.setFontSize(16);
			line3.setFont(font);
			line3.setTextAlignment(align);
			document.add(line5);
		}
		else {
			Paragraph line5 = new Paragraph("DENUNCIANTE: Anônimo");
			line3.setFontSize(16);
			line3.setFont(font);
			line3.setTextAlignment(align);
			document.add(line5);
		}
		
		Paragraph line6 = new Paragraph("RESUMO: " + denuncia.getRelato());
		line3.setFontSize(14);
		line3.setFont(font);
		line3.setTextAlignment(align);
		document.add(line6);	
		
		document.close();		
	} */


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
