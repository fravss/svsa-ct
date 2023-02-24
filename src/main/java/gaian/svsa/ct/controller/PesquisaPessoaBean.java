package gaian.svsa.ct.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.io.source.ByteArrayOutputStream;

import gaian.svsa.ct.dao.lazy.LazyPessoa;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.to.AtendimentoDTO;
import gaian.svsa.ct.service.PessoaService;
import gaian.svsa.ct.service.pdf.PessoaPDFService;
import gaian.svsa.ct.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;


/**
 * @author murakamiadmin
 *
 */
@Log4j
@Getter
@Setter
@Named
@ViewScoped
public class PesquisaPessoaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private LazyPessoa lazyPessoas;
	private Pessoa pessoa;
	private boolean administrativo;

	
	@Inject
	private PessoaService pessoaService;
	@Inject
	private PessoaPDFService pdfService;
	@Inject
	LoginBean loginBean;
	
	
	
	@PostConstruct
	public void inicializar() {	
		
		lazyPessoas = new LazyPessoa(pessoaService, loginBean.getTenantId());
		
		/*
		 * verifica se  eh administrativo
		 */		
		if(loginBean.getUsuario().getRole() == Role.ADMINISTRATIVO 
				|| loginBean.getUsuario().getRole() == Role.CADASTRADOR
				|| loginBean.getUsuario().getRole() == Role.AGENTE_SOCIAL)
			setAdministrativo(true);
		else
			setAdministrativo(false);
	}
	
	/* exibe a evolucao da pessoa */
	
	public void showPDF() {
		
		List<AtendimentoDTO> lista = pessoaService.consultarResumoAtendimentos(pessoa, loginBean.getTenantId());
		
		if(lista != null && !lista.isEmpty()) {	
		
			FacesContext context = FacesContext.getCurrentInstance(); 
		    HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();  
		    response.setContentType("application/pdf");    
		    response.setHeader("Content-disposition",  "inline=filename=file.pdf");
		    
		    try {	        
		    	// Creating a PdfWriter 
		        ByteArrayOutputStream baos = pdfService.generateStream(lista);	        
	
		        // setting some response headers
		        response.setHeader("Expires", "0");
		        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		        response.setHeader("Pragma", "public");
		        // setting the content type
		        response.setContentType("application/pdf");
		        // the contentlength
		        response.setContentLength(baos.size());
		        // write ByteArrayOutputStream to the ServletOutputStream
		        ServletOutputStream os = response.getOutputStream();
		        
		        baos.writeTo(os);
		        os.flush();
		        os.close();
		    }
		    catch (IOException e) {
		    	e.printStackTrace();
		    	MessageUtil.erro("Problema na escrita do PDF.");
		    }
		    catch (Exception ex) {
		    	ex.printStackTrace();
		    	MessageUtil.erro("Problema na geração do PDF.");
		    }
		    context.responseComplete();
		    log.info("PDF gerado!");
		}
		else {
			MessageUtil.erro("Não há registro de evolução para esta pessoa.");
		}
	}
	
	
	
}