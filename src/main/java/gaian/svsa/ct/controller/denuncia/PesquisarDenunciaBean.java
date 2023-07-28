package gaian.svsa.ct.controller.denuncia;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.io.source.ByteArrayOutputStream;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.to.EnderecoTO;
import gaian.svsa.ct.service.DenunciaService;
import gaian.svsa.ct.service.pdf.DenunciaPDFService;
import gaian.svsa.ct.service.rest.BuscaCEPService;
import gaian.svsa.ct.service.rest.RestService;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
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
@Named(value="pesquisarDenunciaBean")
@ViewScoped   
public class PesquisarDenunciaBean implements Serializable {

private static final long serialVersionUID = 1L;

	private List<Denuncia> denuncias = new ArrayList<>();

	private Denuncia denunciaSelecionada;
	private PessoaReferencia pessoaReferencia;
	private Unidade unidade;
	private EnderecoTO enderecoTO;
	
	private Integer ano;	
	
	@Inject
	private DenunciaService denunciaService;
	@Inject
	private DenunciaPDFService denunciapdfService;
	@Inject
	private RDComposicaoFamiliarBean mpComposicaoBean;
	@Inject
	private RestService restService;
	@Inject
	private BuscaCEPService buscaCEPService;	
	@Inject
	private RegistrarDenunciaBean registrarDenuncia;
	@Inject
	private LoginBean loginBean;
	
	//@Inject
	//private AmazonS3Service s3;

	@PostConstruct
	public void inicializar() {
	
	try {
		LocalDate data = LocalDate.now();
		setAno(data.getYear());	
		denuncias = denunciaService.buscarTodos(loginBean.getTenantId());
		this.unidade = loginBean.getUsuario().getUnidade();
	}
	catch(Exception e){
		log.error("Erro inicializar busca de Denuncias CT: " + e.getMessage());
		e.printStackTrace();
	}
}
	public void excluir() {
		try {
			this.denunciaService.excluir(denunciaSelecionada);
			denuncias = denunciaService.buscarTodos(loginBean.getTenantId());
			MessageUtil.sucesso("Denuncia" + denunciaSelecionada.getCodigo() + " excluída com sucesso.");
			
		//	limpar();
				
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void ativar() {
		try {
			denunciaService.ativar(denunciaSelecionada);
			log.info("denunciaSelecionada para ativar: " + denunciaSelecionada.getCodigo());
			MessageUtil.sucesso("Denúncia " + denunciaSelecionada.getCodigo() + " ativado com sucesso.");
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());		
		} catch (PersistenceException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	public void inativar() {
		try {
			denunciaService.inativar(denunciaSelecionada);
			log.info("denunciaSelecionada para inativar: " + denunciaSelecionada.getCodigo());
			MessageUtil.sucesso("Denúncia " + denunciaSelecionada.getCodigo() + " inativado com sucesso.");
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());		
		} catch (PersistenceException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	//Relatório de Denuncia
	public void showPDFDenuncia() {

		try {
			
			log.info("Parametros Para a geração de PDF:");
			log.info(denunciaSelecionada.getFamilia().getMembros());
			log.info(getDenunciaSelecionada());
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "inline=filename=file.pdf");

			// Creating a PdfWriter
			
			log.info(loginBean.getUsuario().getTenant().getS3Key());
			log.info(loginBean.getUsuario().getTenant().getSecretaria());
			ByteArrayOutputStream baos = denunciapdfService.generateStream(denunciaSelecionada,
					loginBean.getUsuario().getTenant().getS3Key(),
					loginBean.getUsuario().getTenant().getSecretaria());
					

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
			context.responseComplete();
		} catch (NegocioException ne) {
			ne.printStackTrace();
			MessageUtil.erro(ne.getMessage());
		}catch (IOException e) {
			e.printStackTrace();
			MessageUtil.erro("Problema na escrita do PDF.");
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageUtil.erro("Problema na geração do PDF.");
		}
		
		log.info("PDF gerado!");
	}
	
	
	
	/*public void consultaTransferencias(Prontuario p) {		
		log.info("prontuario hist transf: " + p.getCodigo());
		setListaTransferencias(transferenciaService.buscarTodos(p, loginBean.getTenantId()));
	}
	*/
	
	/*	public boolean isCoordenador() {
		if(loginBean.getUsuario().getGrupo() == Grupo.COORDENADORES)
			return true;
		
		return false;
	}
*/
	
	public void setDenunciaSelecionada(Denuncia denunciaSelecionada) {
		this.denunciaSelecionada = denunciaSelecionada;
		//setUltimoAtendimento(prontuarioService.ultimoAtendimento(prontuarioSelecionado, loginBean.getTenantId()));		
	}
	
	/*
	 * upload file PDF
	 */
 
	/*  public String gravarPdf() throws IOException{
    	
    	try {
	    	//Verifica se foi carregado algum arquivo
			if(getFile() != null) {
				prontuarioSelecionado = s3.gravaPdfCadUnico(prontuarioSelecionado, getFile());
				// grava a chave de acesso ao arquivo no s3
				prontuarioSelecionado = this.prontuarioService.salvarComPdf(prontuarioSelecionado);
			}
			MessageUtil.sucesso("Pdf CadÚnico gravado com sucesso.");
			
    	} catch (NegocioException e) {  
    		e.printStackTrace();
            MessageUtil.erro(e.getMessage());
	    } catch (Exception e) { 
	    	e.printStackTrace();
	        MessageUtil.erro(e.getMessage());
	    }
    	return "";
    }
    
    public void redirectPdf(Prontuario prontuario) throws IOException {

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(prontuario.getUrlAnexo());
    }
	 */
}