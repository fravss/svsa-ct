package gaian.svsa.ct.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.EnumUtil;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.to.PessoaDTO;
import gaian.svsa.ct.service.AgendamentoIndividualService;
import gaian.svsa.ct.service.PessoaService;
import gaian.svsa.ct.service.UsuarioService;
import gaian.svsa.ct.util.DateUtils;
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
public class AgendamentoIndividualBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Atendimento> listaAtendimentos = new ArrayList<>();
	
	private Atendimento item;
	private List<Atendimento> listaFaltas = new ArrayList<>();
	private Date mesAno;
	
	private List<Role> roles;	
	private List<Usuario> tecnicos;
	
	// para preenchimento dos lists dinamicamente
	//private Role role;
	private Usuario roleTecnico;
	private String strRole;
	private String mse;
	
	private Unidade unidade;		
	
	@Inject
	private AgendamentoIndividualService atendimentoService;
	@Inject
	private PessoaService pessoaService;
	

	@Inject 
	private UsuarioService usuarioService;
	@Inject
	private LoginBean loginBean;

	@PostConstruct
	public void inicializar() {		
		
		unidade = loginBean.getUsuario().getUnidade();
		
		// para carregar todos os agendamentos do mes/ano corrente
		LocalDate ld = LocalDate.now();
		LocalDateTime ldt = LocalDateTime.of(ld.getYear(), ld.getMonthValue(), 1, 0, 0, 0);
		mesAno = DateUtils.asDate(ldt);
		log.debug(mesAno);
		
		buscarListaAtendimento(unidade);

		this.roles = EnumUtil.getRolesCt();
		limpar();	
	}	
	
	
	public void salvar() {
		try {
			log.info("Salvando agendamento...");
			item.setUnidade(unidade);
			item.setAgendador(loginBean.getUsuario());
			if(item.getTecnico() != null)
				item.setRole(item.getTecnico().getRole());
			
			if(item.getPessoa() != null) {
				item.setTenant_id(loginBean.getTenantId());
				this.atendimentoService.salvar(item, loginBean.getTenantId());
				MessageUtil.sucesso("Agendamento realizado com sucesso.");
				
				buscarListaAtendimento(unidade);	
				
				limpar();
			}
			else {
				MessageUtil.erro("Deve ser selecionada uma pessoa para o agendamento!");
			}
						
		} catch (NegocioException e) {
			log.error(e.getMessage());
			MessageUtil.erro(e.getMessage());
		}
	}	
	
	/*
	 * Verifica se é MSE e atualiza a lista recuperada
	 */
	private void buscarListaAtendimento(Unidade unidade) {
		
		log.debug(mesAno);
		listaAtendimentos = atendimentoService.buscarAtendimentosAgendados(unidade, mesAno, loginBean.getTenantId());
		
	}	
	// filtro para consulta dos atendimentos paginado por mes/ano
	public void buscarMesAtend() {

		buscarListaAtendimento(unidade);
		log.info(mesAno);
	}	

	public void consultaFaltas(Atendimento item) {
		
		log.info("pessoa consultada: " + item.getPessoa().getNome());
		setListaFaltas(atendimentoService.consultaFaltas(item.getPessoa(), loginBean.getTenantId()));
		
	}

	public void excluir() {
		try {			
			
			if(item.getResumoAtendimento() != null && item.getDataAtendimento() != null) {
				MessageUtil.sucesso("Agendamento não pode ser excluído porque já existe registro de atendimento! É necessário encerrar o atendimento.");
			}
			else {
				atendimentoService.excluir(item);
				//log.info("item selecionado: " + item.getPessoa().getNome());
				
				this.listaAtendimentos.remove(item);
				MessageUtil.sucesso("Agendamento " + item.getPessoa().getNome() + " excluída com sucesso.");
				
				limpar();
			}
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void excluirPorFalta() {
		try {
			
			item.setTecnico(loginBean.getUsuario());
			item.setResumoAtendimento("[Falta] " + item.getResumoAtendimento());
			atendimentoService.atualizar(item); 
			log.info("ausente: " + item.getPessoa().getNome());
			
			this.listaAtendimentos.remove(item);
			MessageUtil.sucesso("Agendamento de " + item.getPessoa().getNome() + " excluído com sucesso.");
			
			limpar();
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void carregarTecnicos() {

		this.tecnicos = usuarioService.buscarTecnicosRole(item.getRole(), unidade, loginBean.getTenantId());
		
		log.debug("Tecnicos carregados role = " + item.getRole().name());
	}
	
	public void verificarDispTecnico() {
		
		try {
			log.info("verificar disponibilidade do tecnico");			
			atendimentoService.verificarDisponibilidade(unidade, item, loginBean.getTenantId());			
		}
		catch (NegocioException e) {			
			MessageUtil.alerta(e.getMessage());
		}		
	}
	
	public void limpar() {

		item = new Atendimento();
		item.setUnidade(unidade);
		item.setTenant_id(loginBean.getTenantId());
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
        PrimeFaces.current().dialog().openDynamic("SelecionaPessoaReferencia", options, null);        	
    }	
	
	public boolean isItemSelecionado() {
        return item != null && item.getCodigo() != null;
    }	
	
	public void selecionarPessoaReferencia(SelectEvent<?> event) {		
		
		this.item = new Atendimento();
		item.setTenant_id(loginBean.getTenantId());
		
		PessoaDTO dto = (PessoaDTO) event.getObject();		
		Pessoa p = pessoaService.buscarPFPeloCodigo(dto.getCodigo());
		item.setPessoa(p);		
	
		log.info("Pessoa selecionada: " + this.item.getPessoa().getNome());
		
		MessageUtil.sucesso("Pessoa Selecionada: " + this.item.getPessoa().getNome());			
	}
	
	public List<Atendimento> getListaAtendimentos() {
		buscarListaAtendimento(unidade);	
		return listaAtendimentos;
	}
}