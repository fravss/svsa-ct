package gaian.svsa.ct.controller.enc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.Oficio;
import gaian.svsa.ct.modelo.OficioEmitido;
import gaian.svsa.ct.modelo.Orgao;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.CodigoEncaminhamento;
import gaian.svsa.ct.modelo.enums.TipoUnidade;
import gaian.svsa.ct.modelo.to.PessoaDTO;
import gaian.svsa.ct.service.OficioService;
import gaian.svsa.ct.service.PessoaService;
import gaian.svsa.ct.service.UnidadeService;
import gaian.svsa.ct.service.UsuarioService;
import gaian.svsa.ct.service.s3.AmazonS3Service;
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
@Named
@ViewScoped
public class ReceberOficioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Oficio> listaOficios = new ArrayList<>();
	private List<Unidade> unidades = new ArrayList<>();
	private Oficio oficio;
	private OficioEmitido oficioEmitido;
	private List<Orgao> orgaos;
	private Orgao orgao;
	private Boolean todos = false;
	
	private List<Usuario> tecnicos;
	private List<CodigoEncaminhamento> codigoEncaminhamento;
	
	private Usuario usuarioLogado;
	private Unidade unidade;
	private String unidadeEncaminhada;
	
	@Inject
	private PessoaService pessoaService;
	@Inject
	private UnidadeService unidadeService;
	@Inject
	private OficioService oficioService;
	@Inject 
	private UsuarioService usuarioService;
	@Inject
	private AmazonS3Service s3;
	@Inject
	private LoginBean loginBean;
	
	
	@PostConstruct
	public void inicializar() {
		
		usuarioLogado = loginBean.getUsuario();
		unidade = usuarioLogado.getUnidade();
		unidades = unidadeService.buscarTodos(loginBean.getTenantId());
		this.codigoEncaminhamento = Arrays.asList(CodigoEncaminhamento.values());
		
		carregarOficios();
		
		tecnicos = usuarioService.buscarTecnicos(unidade, loginBean.getTenantId());
		
		limpar();
	}
	
	
	public void carregarOficios() {
		
		if(usuarioLogado.getUnidade().getTipo() == TipoUnidade.SASC) {
			listaOficios = oficioService.buscarTodosOficiosSasc(loginBean.getTenantId());
		}
		else {
			listaOficios = oficioService.buscarTodosOficiosRecebidos(unidade, todos, loginBean.getTenantId());
		}
	}
	
	public void salvar() throws IOException {
	
		try {										
			log.info("Salvando recebimento de oficio...");
			
			oficio.setCoordenador(usuarioLogado);
			oficio.setUnidade(usuarioLogado.getUnidade());
									
			//Verifica se existe ao menos um orgão
			if(oficio.getNomeOrgao() != "" && oficio.getNomeOrgao() != null) {
				
				if(oficio.getUnidade() == null) {
					throw new NegocioException("A unidade é obrigatória!");
				}
				else {					
					oficio.setTenant_id(loginBean.getTenantId());
					oficio = this.oficioService.salvar(oficio);
				}
				
				
				MessageUtil.sucesso("ofício recebido com sucesso.");
			}
			else {			
				throw new NegocioException("Selecione ou insira um orgão!");
			}
			carregarOficios();
			
			limpar();
						
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}	
	
	public void salvarResposta(){
		
		try {										
			log.info("Salvando resposta de oficio recebido...");	
			
			completarOficios();
			OficioEmitido emitido = oficioService.salvarResposta(oficio, oficioEmitido);

			MessageUtil.sucesso("Operação realizada com sucesso! Número do ofício emitido: " + emitido.getNrOficioEmitido());
			
			carregarOficios();
			
			limpar();
						
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	private void completarOficios() {
		log.info("Criando resposta de oficio (oficioEmitido)...");
		oficioEmitido.setCodigoEncaminhamento(oficio.getCodigoEncaminhamento());
		oficioEmitido.setEndereco(oficio.getEndereco());
		if(oficio.getPessoa() != null) {
			oficioEmitido.setPessoa(oficio.getPessoa());
		}		
		oficioEmitido.setNomeOrgao(oficio.getNomeOrgao());		
		oficioEmitido.setConselheiro(usuarioLogado);
		oficioEmitido.setTenant_id(loginBean.getTenantId());
		oficioEmitido.setUnidade(unidade);
		
		oficio.setDataResposta(new Date());
		oficio.setConselheiro(usuarioLogado);
		oficio.setAssunto(oficioEmitido.getAssunto());
	}
	
	public void excluir() {
		try {			
			
				oficioService.excluir(oficio);
				//log.info("oficio selecionado: " + item.getPessoa().getNome());
				
				this.listaOficios.remove(oficio);
				MessageUtil.sucesso("Oficio excluído com sucesso.");
				
				
				limpar();

		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void limpar() {
		
		oficio = new Oficio();
		oficio.setTenant_id(loginBean.getTenantId());
		oficioEmitido = new OficioEmitido();
		oficioEmitido.setTenant_id(loginBean.getTenantId());
	}	

	public void abrirDialogo() {
		Map<String,Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("width", 1000);
        options.put("height", 500);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        options.put("draggable", true);
        options.put("responsive", true);
        options.put("closeOnEscape", true);
        
        /* se usuario for de SASC chama selecionaPessoaGeral */
        if(usuarioLogado.getUnidade().getTipo() == TipoUnidade.SASC) {
        	PrimeFaces.current().dialog().openDynamic("/restricted/agenda/SelecionaPessoaGeral", options, null);
        }
        else {
        	PrimeFaces.current().dialog().openDynamic("/restricted/agenda/SelecionaPessoa", options, null);
        }      	
    }	
	public void selecionarPessoa(SelectEvent<?> event) {		
		
		oficio = new Oficio();
		
		PessoaDTO dto = (PessoaDTO) event.getObject();		
		Pessoa p = pessoaService.buscarPeloCodigo(dto.getCodigo());
		oficio.setPessoa(p);	
				
		oficio.setUnidade(oficio.getPessoa().getFamilia().getDenuncia().getUnidade());
		unidadeEncaminhada = oficio.getPessoa().getFamilia().getDenuncia().getUnidade().getNome();
		log.debug("Pessoa selecionada: " + oficio.getPessoa().getNome() + " - " + unidadeEncaminhada);
		MessageUtil.sucesso("Pessoa Selecionada: " + oficio.getPessoa().getNome());			
	}
	
	public void carregarOrgaos() {
		this.orgaos = oficioService.buscarCodigosEncaminhamento(oficio.getCodigoEncaminhamento(), loginBean.getTenantId());
	}
	
	public void selecionarOrgao() {

		if (orgao != null) {
			oficio.setNomeOrgao(orgao.getNome());
			oficio.setEndereco(orgao.getEndereco().toString());
		}else {
			oficio.setNomeOrgao("");
			oficio.setEndereco("");
		}
		log.debug("nome: " + oficio.getNomeOrgao());
		log.debug("endereco: " + oficio.getEndereco());
	}
	
	public boolean isOficioSelecionado() {
        return oficio != null && oficio.getCodigo() != null;
    }	
	
}