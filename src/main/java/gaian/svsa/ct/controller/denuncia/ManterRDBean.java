package gaian.svsa.ct.controller.denuncia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.to.PessoaDTO;
import gaian.svsa.ct.service.RDComposicaoService;
import gaian.svsa.ct.service.PessoaService;
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
@Named(value="manterRDBean")
@ViewScoped
public class ManterRDBean implements Serializable {

	private static final long serialVersionUID = 1769116747361287180L;
	//private LogUtil logUtil = new LogUtil(ManterProntuarioBean.class);
	
	private PessoaReferencia pessoaReferencia;
	private Pessoa pessoa;
	private List<PessoaReferencia> listaPessoasReferencia = new ArrayList<>();
	private boolean administrativo;
	private Usuario usuarioLogado;
	
	
	@Inject
	private LoginBean loginBean;
	@Inject
	private PessoaService pessoaService;
	@Inject
	private RDComposicaoService composicaoService;	
	@Inject
	private RDComposicaoFamiliarBean rdComposicaoBean;
	
	
	
	@PostConstruct
	public void inicializar() {		
		
		usuarioLogado = loginBean.getUsuario();

		if(usuarioLogado.getRole() == Role.ADMINISTRATIVO 
				|| usuarioLogado.getRole() == Role.CADASTRADOR
				|| usuarioLogado.getRole() == Role.AGENTE_SOCIAL)
			setAdministrativo(true);
		else
			setAdministrativo(false);
	
	}
	
	public void todasPessoasReferencia() {
        listaPessoasReferencia = composicaoService.todasPessoasReferencia(loginBean.getTenantId());
    }	
		
	public void selecionarPessoaReferencia(SelectEvent<?> event) {
		
		PessoaDTO dto = (PessoaDTO) event.getObject();		
		this.pessoaReferencia = pessoaService.buscarPFPeloCodigo(dto.getCodigo());
		
		/* dados familiares */
		rdComposicaoBean.setPessoaReferencia(this.pessoaReferencia);
	//	mpAcompanhamentoBean.setPessoaReferencia(this.pessoaReferencia);
	//	beneficioBean.setPessoaReferencia(this.pessoaReferencia);
	//	habitacionalBean.setPessoaReferencia(this.pessoaReferencia);
	//	convivenciaBean.setPessoaReferencia(this.pessoaReferencia);
		
		/* dados individuais */
		//	mpIndividualBean.setMembros(mpComposicaoBean.getPessoas());
		//	violenciaBean.setMembros(mpComposicaoBean.getPessoas());
		//	saudeBean.setMembros(mpComposicaoBean.getPessoas());
		//	trabalhoBean.setMembros(mpComposicaoBean.getPessoas());
		//	educacionalBean.setMembros(mpComposicaoBean.getPessoas());		
		
		MessageUtil.sucesso("Pessoa Referencia Selecionada: " + this.pessoaReferencia.getNome());			
	}	
	
	public boolean isPessoaReferenciaSelecionada() {
        return pessoaReferencia != null && pessoaReferencia.getCodigo() != null;
    }
	
	public void setPessoa(Pessoa pessoa) {
		log.info("manterProntuarioBean.setPessoa() = " + pessoa.getNome());
		this.pessoa = pessoa;
		this.rdComposicaoBean.setPessoa(pessoa);
		//	violenciaBean.setPessoa(pessoa);
		//	saudeBean.setPessoa(pessoa);
		//	trabalhoBean.setPessoa(pessoa);
		//	educacionalBean.setPessoa(pessoa);
	}
	
	public List<Pessoa> getMembros(){
		return rdComposicaoBean.getPessoas();
	}
		
}