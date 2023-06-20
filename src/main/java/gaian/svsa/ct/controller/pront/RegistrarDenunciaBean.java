package gaian.svsa.ct.controller.pront;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.io.source.ByteArrayOutputStream;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.enums.AgenteViolador;
import gaian.svsa.ct.modelo.enums.DireitoViolado;
import gaian.svsa.ct.modelo.enums.OrigemDenuncia;
import gaian.svsa.ct.modelo.enums.Sexo;
import gaian.svsa.ct.modelo.enums.StatusRD;
import gaian.svsa.ct.service.DenunciaService;
import gaian.svsa.ct.service.pdf.AtestadoPDFService;
import gaian.svsa.ct.service.pdf.DenunciaPDFService;
import gaian.svsa.ct.service.pdf.NotificacaoPDFService;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @author laurojr
 *
 */
@Getter
@Setter
@Named
@ViewScoped
@Log4j
public class RegistrarDenunciaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Denuncia denuncia;
	private List<Denuncia> denuncias = new ArrayList<>();
	private List<AgenteViolador> agentes;
	private List<DireitoViolado> direitos;
	private List<StatusRD> statusRD;
	private List<OrigemDenuncia> origens;
	private List<Sexo> sexos;
	
	private Integer ano;
	private String nome;
	
	@Inject
	private DenunciaService denunciaService;
	
	@Inject
	private AtestadoPDFService atestadopdfService;
	@Inject
	private DenunciaPDFService denunciapdfService;
	@Inject
	private NotificacaoPDFService notificacaopdfService;
	
	@Inject
	private LoginBean loginBean;
	
	@PostConstruct
	public void inicializar()  {	
		
		try {
				LocalDate data = LocalDate.now();
				setAno(data.getYear());		
				this.agentes = Arrays.asList(AgenteViolador.values());
				this.direitos = Arrays.asList(DireitoViolado.values());
				this.statusRD = Arrays.asList(StatusRD.values());
				this.origens = Arrays.asList(OrigemDenuncia.values());
				this.sexos = Arrays.asList(Sexo.values());
				
				denuncias = denunciaService.buscarTodos(loginBean.getTenantId());
				limpar();
			}
		
		catch(Exception e){
			log.error("Erro inicializar() Denuncia CT: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void salvar() {
		try {
			
			denuncia = this.denunciaService.salvar(denuncia);			
			
			MessageUtil.sucesso("Denuncia salva com sucesso!");			
		
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
		
		denuncias = denunciaService.buscarTodos(loginBean.getTenantId());
		this.limpar();
	}
	
	public void excluir() {
		try {
			this.denunciaService.excluir(denuncia);
			denuncias = denunciaService.buscarTodos(loginBean.getTenantId());
			MessageUtil.sucesso("Denuncia" + denuncia.getCodigo() + " excluída com sucesso.");
			
			limpar();
				
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void limpar() {		

		this.denuncia = new Denuncia();
		this.denuncia.setAno(getAno());
		this.denuncia.setStatusRD(StatusRD.EM_AVERIGUACAO);
		this.denuncia.setUnidade(loginBean.getUsuario().getUnidade());
		this.denuncia.setTenant_id(loginBean.getTenantId());
	}	

	//Atestado
	public void showPDF() {

		try {
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "inline=filename=file.pdf");

			// Creating a PdfWriter
			log.info(denuncia);
			log.info(loginBean.getUsuario().getTenant().getS3Key());
			log.info(loginBean.getUsuario().getTenant().getSecretaria());
			ByteArrayOutputStream baos = atestadopdfService.generateStream(denuncia,
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
	
	//Relatório de Denuncia
	public void showPDFDenuncia() {

		try {
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "inline=filename=file.pdf");

			// Creating a PdfWriter
			log.info(denuncia);
			log.info(loginBean.getUsuario().getTenant().getS3Key());
			log.info(loginBean.getUsuario().getTenant().getSecretaria());
			ByteArrayOutputStream baos = denunciapdfService.generateStream(denuncia,
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
	
	//Notificação
	public void showPDFNotificacao() {

		try {
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "inline=filename=file.pdf");

			// Creating a PdfWriter
			log.info(denuncia);
			log.info(loginBean.getUsuario().getTenant().getS3Key());
			log.info(loginBean.getUsuario().getTenant().getSecretaria());
			ByteArrayOutputStream baos = notificacaopdfService.generateStream(denuncia,
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
	
	public boolean isAtestadoSelecionado() {
        return denuncia != null && denuncia.getCodigo() != null;
    }

	public List<Denuncia> getListaAtestados() {
		return denuncias;
	}


	public List<String> buscarNomes(String query) {
        List<String> results = new ArrayList<>();
        
        try {
			results = denunciaService.buscarNomes(query, loginBean.getTenantId());		
		
			
		} catch(Exception e) {
			MessageUtil.alerta("Não existe PESSOA com esse nome!");
		}        
       
        return results;
    }
	
	public void buscarPessoa() {
	       
        try {
        	//PessoaReferencia p = denunciaService.buscarPeloNome(getNome());
        	//denuncia.setProntuario(p.getFamilia().getProntuario());
			
		} catch(Exception e) {
			MessageUtil.alerta("Não existe PESSOA com esse nome!");
		}        
	}
}

