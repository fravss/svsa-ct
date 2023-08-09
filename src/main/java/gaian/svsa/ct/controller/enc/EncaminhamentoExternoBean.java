package gaian.svsa.ct.controller.enc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.Encaminhamento;
import gaian.svsa.ct.modelo.Orgao;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.enums.CodigoEncaminhamento;
import gaian.svsa.ct.modelo.to.PessoaDTO;
import gaian.svsa.ct.service.EncaminhamentoService;
import gaian.svsa.ct.service.PessoaService;
import gaian.svsa.ct.service.pdf.EncamPDFService;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @author gabrielrodrigues
 *
 */
@Log4j
@Getter
@Setter
@Named
@ViewScoped
public class EncaminhamentoExternoBean implements Serializable {

	private static final long serialVersionUID = 1769116747361287180L;

	private Encaminhamento encaminhamento;
	private List<Encaminhamento> listaEncaminhamentos;
	private List<CodigoEncaminhamento> codigosEncaminhamento;
	private List<Orgao> orgaos;
	private Orgao orgao;
	private Pessoa pessoa;

	@Inject
	private EncamPDFService pdfService;
	@Inject
	private PessoaService pessoaService;
	@Inject
	private EncaminhamentoService encaminhamentoService;
	@Inject
	private LoginBean loginBean;

	@PostConstruct
	public void inicializar() {
		log.info("[LOG] " + loginBean.getUserName() + " -> " + this.getClass().getSimpleName());
		
		codigosEncaminhamento = Arrays.asList(CodigoEncaminhamento.values());

		this.limpar();
	}

	public void salvar() {
		
		try {

			this.encaminhamento.setPessoa(pessoa);
			this.encaminhamento.setConselheiro(loginBean.getUsuario());
			this.encaminhamento.setUnidade(loginBean.getUsuario().getUnidade());
			
			encaminhamento = this.encaminhamentoService.salvar(encaminhamento);
			carregarEncaminhamentos();

			MessageUtil.sucesso("Encaminhamento gravado com sucesso.");

			limpar();

		} 
		catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.alerta(e.getMessage());
		}
	}

	public void excluir() {
		try {

			this.encaminhamentoService.excluir(encaminhamento);

			this.listaEncaminhamentos.remove(encaminhamento);
			MessageUtil.sucesso("Encaminhamento " + encaminhamento.getCodigo() + " excluído com sucesso.");

		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}

	}

	public void limpar() {
		this.encaminhamento = new Encaminhamento();
		this.encaminhamento.setTenant_id(loginBean.getTenantId());
		
	}

	public void carregarEncaminhamentos() {
		listaEncaminhamentos = encaminhamentoService.buscarEncaminhamentos(pessoa, loginBean.getTenantId());
	}

	public void abrirDialogo() {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("width", 1000);
		options.put("height", 500);
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
		this.pessoa = p;

		listaEncaminhamentos = encaminhamentoService.buscarEncaminhamentos(pessoa, loginBean.getTenantId());
	}

	public void showPDF(Encaminhamento encaminhamento) {
		

		try {
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "inline=filename=file.pdf");
			
			// Emissão em nome de quem está imprimindo
			encaminhamento.setConselheiro(loginBean.getUsuario());
						
			// Creating a PdfWriter
			ByteArrayOutputStream baos = pdfService.generateStream(encaminhamento, loginBean.getUsuario().getTenant().getS3Key());

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
			log.info("PDF gerado!");
			
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
	}
	
	public void downloadPDF(Encaminhamento encaminhamento){
		try {
					
			String nomeArquivo = "Encaminhamento ".concat(encaminhamento.getCodigo().toString().concat(".pdf"));
			// no contexto da aplicacao
			String caminhoWebInf = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/pdfs/");

			
			/* cria o arquivo em pasta temporaria pra download*/
			pdfService.generatePDF(caminhoWebInf.concat(nomeArquivo), nomeArquivo, encaminhamento, loginBean.getUsuario().getTenant().getS3Key());	
			
			File file = new File(caminhoWebInf.concat(nomeArquivo));
			
			log.info(file.getAbsolutePath());
			
			FileInputStream fis = new FileInputStream(file);
			
			byte[] buf = new byte[1000000];
			int offset = 0;
			int numRead = 0;
			while ((offset < buf.length) && ((numRead = fis.read(buf, offset, buf.length - offset)) >= 0)) {
				offset += numRead;
			}
			
			fis.close();
			
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
	
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment;filename=" + nomeArquivo);
			response.getOutputStream().write(buf);
			response.getOutputStream().flush();
			response.getOutputStream().close();
			FacesContext.getCurrentInstance().responseComplete();
		}
		catch(FileNotFoundException fnf) {
			MessageUtil.erro("Não existe PDF!");
		}
		catch(Exception e) {
			MessageUtil.erro("Houve um problema na recuperação do PDF !");
		}
	}
	
	public void carregarOrgaos() {
		this.orgaos = encaminhamentoService.buscarCodigosEncaminhamento(encaminhamento.getCodigoEncaminhamento(), loginBean.getTenantId());
	}

	public void selecionarOrgao() {

		if (orgao != null) {
			encaminhamento.setOrgaoUnidadeDestino(orgao.getNome());
			encaminhamento.setEnderecoUnidadeDestino(orgao.getEndereco().toString());
		}else {
			encaminhamento.setOrgaoUnidadeDestino("");
			encaminhamento.setEnderecoUnidadeDestino("");
		}
		log.info("nome: " + encaminhamento.getOrgaoUnidadeDestino());
		log.info("endereco: " + encaminhamento.getEnderecoUnidadeDestino());
	}

	public boolean isPessoaSelecionada() {
		return pessoa != null && pessoa.getCodigo() != null;
	}

	public boolean isEncaminhamentoSelecionado() {
		return encaminhamento != null && encaminhamento.getCodigo() != null;
	}

}