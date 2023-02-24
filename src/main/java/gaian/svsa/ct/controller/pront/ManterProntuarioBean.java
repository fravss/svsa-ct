package gaian.svsa.ct.controller.pront;

import java.io.Serializable;
import java.util.ArrayList;
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
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.to.PessoaDTO;
import gaian.svsa.ct.service.MPComposicaoService;
import gaian.svsa.ct.service.PessoaService;
import gaian.svsa.ct.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author murakamiadmin
 *
 */
@Getter
@Setter
@Named(value="manterProntuarioBean")
@ViewScoped
public class ManterProntuarioBean implements Serializable {

	private static final long serialVersionUID = 1769116747361287180L;
	//private LogUtil logUtil = new LogUtil(ManterProntuarioBean.class);
	
	private PessoaReferencia pessoaReferencia;
	private List<PessoaReferencia> listaPessoasReferencia = new ArrayList<>();
	private boolean administrativo;
	private Usuario usuarioLogado;
	
	
	@Inject
	private LoginBean loginBean;
	@Inject
	private PessoaService pessoaService;
	@Inject
	private MPComposicaoService composicaoService;	
	@Inject
	private MPComposicaoFamiliarBean mpComposicaoBean;
	@Inject
	
	
	
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
        PrimeFaces.current().dialog().openDynamic("/restricted/agenda/SelecionaPessoaReferencia", options, null);
        	
    }
	
	public void abrirDialogoGeral() {
		Map<String,Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("width", 1000);
        options.put("height", 500);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        options.put("draggable", true);
        options.put("responsive", true);
        options.put("closeOnEscape", true);
        PrimeFaces.current().dialog().openDynamic("/restricted/agenda/SelecionaPReferenciaGeral", options, null);
        	
    }	
	
	public void selecionarPessoaReferencia(SelectEvent<?> event) {
		
		PessoaDTO dto = (PessoaDTO) event.getObject();		
		this.pessoaReferencia = pessoaService.buscarPFPeloCodigo(dto.getCodigo());
		
		mpComposicaoBean.setPessoaReferencia(this.pessoaReferencia);		
		
		MessageUtil.sucesso("Pessoa Referencia Selecionada: " + this.pessoaReferencia.getNome());			
	}	
	
	public boolean isPessoaReferenciaSelecionada() {
        return pessoaReferencia != null && pessoaReferencia.getCodigo() != null;
    }
	
}