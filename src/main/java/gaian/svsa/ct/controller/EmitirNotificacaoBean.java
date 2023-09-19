package gaian.svsa.ct.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import com.itextpdf.io.source.ByteArrayOutputStream;
import gaian.svsa.ct.service.pdf.NotificacaoPDFService;

import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.to.PessoaDTO;
import gaian.svsa.ct.service.PessoaService;
import gaian.svsa.ct.modelo.to.NotificacaoTO;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @author Michel
 *
 */
@Log4j
@Getter
@Setter
@Named
@ViewScoped
public class EmitirNotificacaoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String nomePessoaCadastrada;
	private String nomePessoaSemCadastro;
	private String motivo;
	private String endereco;
	private String bairro;
	private String numero;
	
	private Date dataComparecimento;
	
	private Unidade unidade;
	
	private NotificacaoTO nto = new NotificacaoTO();
	
	@Inject
	private PessoaService pessoaService;
	
	@Inject
	private NotificacaoPDFService pdfService;
	
	@Inject
	private LoginBean loginBean;
	
	@PostConstruct
	public void inicializar() {		
		
		this.unidade = loginBean.getUsuario().getUnidade();
		
		nomePessoaCadastrada = "";
		nomePessoaSemCadastro = "";
		dataComparecimento = null;
		motivo = "";
		endereco = "";
		bairro = "";
		numero = "";
	}
	
	public void emitir() {
		
		try {
			
			if( !nomePessoaCadastrada.equals("") && !nomePessoaCadastrada.isEmpty()) {
					log.info("Emitindo Declaração de Comparecimento...");					
					imprimirPdf();
			} else {
				if( !nomePessoaSemCadastro.equals("") && !nomePessoaSemCadastro.isEmpty()) {
					log.info("Emitindo Declaração de Comparecimento...");					
					imprimirPdf();
				} else {
					MessageUtil.erro("O nome da pessoa é obrigatório");
				}
			}
						
		} catch (Exception e) {
			log.error(e.getMessage());
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void limpar() {
		
		nto = new NotificacaoTO();
		
		nomePessoaCadastrada = "";
		nomePessoaSemCadastro = "";
		dataComparecimento = null;
		motivo = "";
		endereco = "";
		bairro = "";
		numero = "";
	}
	
	public void abrirDialogo() {
		Map<String,Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("width", 1000);
        options.put("height", 600);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        options.put("draggable", true);
        options.put("responsive", true);
        options.put("closeOnEscape", true);
        PrimeFaces.current().dialog().openDynamic("/restricted/agenda/SelecionaPessoa", options, null);  
    }
	
	public void selecionarPessoa(SelectEvent<?> event) {		
		
		PessoaDTO dto = (PessoaDTO) event.getObject();		
		Pessoa p = pessoaService.buscarPeloCodigo(dto.getCodigo());
		this.nomePessoaCadastrada = p.getNome();
		this.nomePessoaSemCadastro = "";
	
		log.info("Pessoa selecionada: " + this.nomePessoaCadastrada);
		
		MessageUtil.sucesso("Pessoa Selecionada: " + this.nomePessoaCadastrada);			
	}
	
	public boolean isPessoaSelecionada() {
		
		boolean cond = this.nomePessoaCadastrada != null && !this.nomePessoaCadastrada.isEmpty();
			
        return cond;
    }
	
	public void imprimirPdf() {
		try {
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "inline=filename=file.pdf");
			
			// Creating a PdfWriter
			nto.setNomeNotificacao(isPessoaSelecionada() ? nomePessoaCadastrada : nomePessoaSemCadastro);
			nto.setDataComparecimento(dataComparecimento);
			nto.setMotivo(motivo);
			nto.setEndereco(endereco);
			nto.setBairro(bairro);
			nto.setNumero(numero);
			
			nto.setUnidade(this.unidade);
			nto.setTecnico(loginBean.getUsuario());
	
			log.debug(nto.getNomeNotificacao());
			log.debug(nto.getDataComparecimento());
			log.debug(nto.getEndereco());
			log.debug(nto.getBairro());
			log.debug(nto.getNumero());
			log.debug(nto.getMotivo());
			log.debug(nto.getUnidade());		
			log.debug(nto.getTecnico());
			
			ByteArrayOutputStream baos = pdfService.generateStream(nto,
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
	}

