package gaian.svsa.ct.controller.denuncia;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import gaian.svsa.ct.modelo.enums.Uf;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.Endereco;
import gaian.svsa.ct.modelo.Familia;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.AgenteViolador;
import gaian.svsa.ct.modelo.enums.DireitoViolado;
import gaian.svsa.ct.modelo.enums.OrigemDenuncia;
import gaian.svsa.ct.modelo.enums.Parentesco;
import gaian.svsa.ct.modelo.enums.Sexo;
import gaian.svsa.ct.modelo.enums.Status;
import gaian.svsa.ct.modelo.enums.StatusRD;
import gaian.svsa.ct.modelo.to.EnderecoTO;
import gaian.svsa.ct.modelo.to.MunicipioTO;
import gaian.svsa.ct.service.DenunciaService;
import gaian.svsa.ct.service.pdf.AtestadoPDFService;
import gaian.svsa.ct.service.pdf.DenunciaPDFService;
import gaian.svsa.ct.service.pdf.NotificacaoPDFService;
import gaian.svsa.ct.service.rest.BuscaCEPService;
import gaian.svsa.ct.service.rest.RestService;
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
	private PessoaReferencia pessoaReferencia;
	private Unidade unidade;
	private EnderecoTO enderecoTO;
	
	private List<Denuncia> denuncias = new ArrayList<>();
	private List<AgenteViolador> agentes;
	private List<DireitoViolado> direitos;
	private List<StatusRD> statusRD;
	private List<Uf> ufs;
	private List<OrigemDenuncia> origens;
	private List<Sexo> sexos;
	private List<MunicipioTO> municipioList;
	private boolean composicao = false;
	
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
	private RDComposicaoFamiliarBean rdComposicaoBean;
	@Inject
	private RestService restService;
	@Inject
	private BuscaCEPService buscaCEPService;	
	
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
				this.unidade = loginBean.getUsuario().getUnidade();
				this.ufs = Arrays.asList(Uf.values());
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
			
			denuncia.setUnidade(unidade);
			denuncia.getFamilia().setDenuncia(denuncia);
			denuncia.setConselheiro(loginBean.getUsuario());
			denuncia.getFamilia().getPessoaReferencia().setParentesco(Parentesco.GENITORA);
			denuncia.getFamilia().getPessoaReferencia().setFamilia(denuncia.getFamilia());
			
			this.denunciaService.salvar(denuncia);			
			
			MessageUtil.sucesso("Denuncia salva com sucesso!");	
			this.limpar();
		
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
		this.denuncia.setStatus(Status.ATIVO);
		
		PessoaReferencia pr = new PessoaReferencia();
		pr.setTenant_id(loginBean.getTenantId());
		
		this.denuncia.setFamilia(new Familia());
		this.denuncia.getFamilia().setTenant_id(loginBean.getTenantId());
		this.denuncia.getFamilia().setPessoaReferencia(pr);		
		this.denuncia.getFamilia().getPessoaReferencia().setEndereco(new Endereco());
		this.denuncia.getFamilia().getPessoaReferencia().getEndereco().setTenant_id(loginBean.getTenantId());
	}
	
	public void listarMunicipiosEnd() throws Exception {
		
		try {
			setMunicipioList(restService.listarMunicipios(denuncia.getFamilia().getPessoaReferencia().getEndereco().getUf()));
		}
		catch(Exception e){
			MessageUtil.sucesso("Problema na recuperação dos municípios.");
		}
	}
	
	public void buscaEnderecoPorCEP() {
		
        try {
			enderecoTO  = buscaCEPService.buscaEnderecoPorCEP(denuncia.getFamilia().getPessoaReferencia().getEndereco().getCep());
			
			/*
	         * Preenche o Endereco do prontuario com os dados buscados
	         */	 
			
	        denuncia.getFamilia().getPessoaReferencia().getEndereco().setEndereco(enderecoTO.getTipoLogradouro().
	        		                concat(" ").concat(enderecoTO.getLogradouro()));
	        denuncia.getFamilia().getPessoaReferencia().getEndereco().setNumero(null);
	        denuncia.getFamilia().getPessoaReferencia().getEndereco().setBairro(enderecoTO.getBairro());
	        denuncia.getFamilia().getPessoaReferencia().getEndereco().setMunicipio(enderecoTO.getCidade());
	        denuncia.getFamilia().getPessoaReferencia().getEndereco().setUf(enderecoTO.getEstado());
	        
	        if (enderecoTO.getResultado() != 1) {
	        	MessageUtil.erro("Endereço não encontrado para o CEP fornecido.");
	        }
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());		            
		}       
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
	
	public boolean isEdicao() {
		
		if(denuncia.getFamilia().getPessoaReferencia() != null && denuncia.getFamilia().getPessoaReferencia().getCodigo() != null) {
			return true;
		}
		return false;
	}
	
	public boolean isPessoaReferenciaSelecionada() {

    	if(getPessoaReferencia() != null && getPessoaReferencia().getCodigo() != null) {    		
    		return true;
    	}
        return false;
        		
    }	
	
	public void setPessoaReferencia() {

    	if(getDenuncia() != null && getDenuncia().getCodigo() != null) {
    		setPessoaReferencia(getDenuncia().getFamilia().getPessoaReferencia());
    		rdComposicaoBean.setPessoaReferencia(getPessoaReferencia());
    		setComposicao(true);
    	}        		
    }
}

