package gaian.svsa.ct.controller;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import gaian.svsa.ct.modelo.AgendamentoFamiliar;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.CodigoAuxiliarAtendimento;
import gaian.svsa.ct.modelo.enums.EnumUtil;
import gaian.svsa.ct.service.AgendamentoFamiliarService;
import gaian.svsa.ct.service.UsuarioService;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @author Talita
 *
 */
@Log4j
@Getter
@Setter
@Named
@ViewScoped
public class RealizarAtendFamiliarBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<AgendamentoFamiliar> listaAtendimentos = new ArrayList<>();
	private List<AgendamentoFamiliar> resumoAtendimentos = new ArrayList<>();
	private AgendamentoFamiliar item;
	private List<CodigoAuxiliarAtendimento> codigosAuxiliares;	
	private DualListModel<Usuario> conselheiros;
	private Usuario usuarioLogado;
	
	private boolean statusPoll = true;
	
	@Inject
	AgendamentoFamiliarService agendamentoFamiliarService;
	@Inject 
	UsuarioService usuarioService;
	@Inject
	LoginBean loginBean;

	@PostConstruct
	public void inicializar() {	
		
		usuarioLogado = loginBean.getUsuario();		
		
		carregarCodAux();
		
		carregarConselheiros();
					
		buscarAtendimentosFamiliar();
		
		limpar();	
	}
	
	private void carregarCodAux() {
		
		this.codigosAuxiliares = EnumUtil.getTiposAtendimento();
	}
	
	private void carregarConselheiros() {		
		
		List<Usuario>consSource = new ArrayList<Usuario>();
		consSource = usuarioService.buscarConselheiros(usuarioLogado.getUnidade(), loginBean.getTenantId());
		consSource.remove(usuarioLogado);
		List<Usuario> consTarget = new ArrayList<Usuario>();
       
        conselheiros = new DualListModel<Usuario>(consSource, consTarget);
	}
	
	public void onTransfer(TransferEvent event) {

        for(Object conselheiro : event.getItems()) {
        	MessageUtil.sucesso("Conselheiro " + ((Usuario) conselheiro).getNome() + " selecionado.");
        }         
    }
	
	/*
	public void salvarAtendimento() {
		try {		
			
			item.setConselheiro(usuarioLogado);
			item.setConselheiros(new HashSet<Usuario>(conselheiros.getTarget()));
			item.setTenant_id(loginBean.getTenantId());
			
			this.agendamentoFamiliarService.salvarAtendFamiliar(item);
			this.listaAtendimentos.remove(item);
			limpar();
			MessageUtil.sucesso("Atendimento familiar gravado com sucesso!");
								
		} catch (NegocioException e) {
			FacesContext.getCurrentInstance().validationFailed();
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	*/
	
	public void salvarAtendimento() {
		try {		
			
			item.setConselheiro(usuarioLogado);
			item.setConselheiros(conselheiros.getTarget());
			item.setTenant_id(loginBean.getTenantId());
			
			this.agendamentoFamiliarService.salvarAtendFamiliar(item);
			this.listaAtendimentos.remove(item);
			limpar();
			MessageUtil.sucesso("Atendimento familiar gravado com sucesso!");
								
		} catch (NegocioException e) {
			FacesContext.getCurrentInstance().validationFailed();
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}	
	
	/*
	public void autoSave() {
		try {
			
			if(item.getCodigoAuxiliar() != null) {
				Instant time = Instant.now();
				log.info("auto save atendimento coletivo... " + time);
				
				item.setConselheiro(usuarioLogado);			
				item.setConselheiros(new HashSet<Usuario>(conselheiros.getTarget()));
				item.setTenant_id(loginBean.getTenantId());
				
				log.info("ANTES...auto save atendimento FAMILIAR codigo = " + item.getCodigo());
				this.agendamentoFamiliarService.autoSave(item);
				log.info("DEPOIS...auto save atendimento FAMILIAR codigo = " + item.getCodigo());
				MessageUtil.sucesso("Auto save executado.");
			}
								
		} catch (NegocioException e) {
			//FacesContext.getCurrentInstance().validationFailed();
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	*/
	
	public void autoSave() {
		try {
			
			if(item.getCodigoAuxiliar() != null) {
				Instant time = Instant.now();
				log.info("auto save atendimento coletivo... " + time);
				
				item.setConselheiro(usuarioLogado);			
				item.setConselheiros(conselheiros.getTarget());
				item.setTenant_id(loginBean.getTenantId());
				
				log.info("ANTES...auto save atendimento FAMILIAR codigo = " + item.getCodigo());
				this.agendamentoFamiliarService.autoSave(item);
				log.info("DEPOIS...auto save atendimento FAMILIAR codigo = " + item.getCodigo());
				MessageUtil.sucesso("Auto save executado.");
			}
								
		} catch (NegocioException e) {
			//FacesContext.getCurrentInstance().validationFailed();
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public String agendamento() {
		return "/restricted/agenda/AgendamentoFamiliar.xhtml";
	}
	
	public void buscarAtendimentosFamiliar() {
		
		this.listaAtendimentos =  agendamentoFamiliarService.buscarAtendimentosAgendados(usuarioLogado.getUnidade(), loginBean.getTenantId());		
	}	
	
	public void limpar() {
		item = new AgendamentoFamiliar();
		item.setTenant_id(loginBean.getTenantId());
	}
	
	public boolean isItemSelecionado() {
		
        return item != null && item.getCodigo() != null;
    }	
	
    /* 
	 * Indicadores fim
	 */
	
    public void stopPoll() {
		log.info("true");
		statusPoll = true;
	}
	public void startPoll() {
		log.info("false");
		statusPoll = false;
	}
	
	public void atualizarDataTable() {
		buscarAtendimentosFamiliar();
	}
	
}