package gaian.svsa.ct.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import gaian.svsa.ct.modelo.AgendamentoFamiliar;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.EnumUtil;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.to.PessoaDTO;
import gaian.svsa.ct.service.AgendamentoFamiliarService;
import gaian.svsa.ct.service.PessoaService;
import gaian.svsa.ct.service.UsuarioService;
import gaian.svsa.ct.util.DateUtils;
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
public class AgendamentoFamiliarBean implements Serializable {

private static final long serialVersionUID = 1L;
	
	private List<AgendamentoFamiliar> listaAtendimentos = new ArrayList<>();
	private AgendamentoFamiliar item;
	
	private PessoaReferencia pessoaReferencia;
	
	private Date mesAno;
		
	private List<Role> roles;	
	private List<Usuario> conselheiros;
	
	// para preenchimento dos lists dinamicamente
	private Role role;
	private Usuario roleConselheiro;
	private String strRole;
	private Unidade unidade;		
	
	@Inject
	private PessoaService pessoaService;
	@Inject
	private AgendamentoFamiliarService agendamentoFamiliarService;
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

		buscarAtendimentoFamiliar(unidade);
		log.debug("ATENDIMENTOS AGENDADOS = " + getListaAtendimentos().size());
		
		this.roles = EnumUtil.getRolesCt();
		limpar();
	}
	
	public void salvarAgendamento() {
		try {										
			
			item.setUnidade(unidade);
			item.setTenant_id(loginBean.getTenantId());
			item.setAgendador(loginBean.getUsuario());
			if(item.getConselheiro() != null)
				item.setRole(item.getConselheiro().getRole());
			
			if(item.getPessoas().size() > 0) {
		
				this.agendamentoFamiliarService.salvarAgendamento(item, loginBean.getTenantId());
				MessageUtil.sucesso("Agendamento realizado com sucesso.");
				
				buscarAtendimentoFamiliar(unidade);	
				
				limpar();
			}
			else {
				MessageUtil.erro("O agendamento deve ter pelo menos uma pessoa.");
			}
						
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}	
	
	private void buscarAtendimentoFamiliar(Unidade unidade) {
		log.debug(mesAno);
		listaAtendimentos = agendamentoFamiliarService.buscarAtendimentosAgendados(unidade, mesAno, loginBean.getTenantId());
		
	}
	
	public void buscarMesAtend() {

		buscarAtendimentoFamiliar(unidade);
		log.info(mesAno);
	}	

	
	public void excluir() {
		try {			
			
			agendamentoFamiliarService.excluir(item);
			
			this.listaAtendimentos.remove(item);
			MessageUtil.sucesso("Agendamento excluído com sucesso.");
			
			limpar();
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	
	public void carregarConselheiros() {

		this.conselheiros = usuarioService.buscarConselheirosRole(item.getRole(), unidade, loginBean.getTenantId());		
	}
	
	public void verificaDispConselheiro() throws NegocioException {
		try {
			log.info("verificar disponibilidade do conselheiro");
			agendamentoFamiliarService.verificarDisponibilidade(unidade, item, loginBean.getTenantId());			
		}
		catch (NegocioException e) {
			MessageUtil.alerta(e.getMessage());
		}		
	}
	
	public void limpar() {

		item = new AgendamentoFamiliar();
		item.setTenant_id(loginBean.getTenantId());
		item.setUnidade(unidade);
		item.setPessoas(new ArrayList<Pessoa>());
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

		PessoaDTO dto = (PessoaDTO) event.getObject();		
		this.pessoaReferencia = pessoaService.buscarPFPeloCodigo(dto.getCodigo());
		
		
		List<Pessoa> pessoasList = new ArrayList<>(pessoaReferencia.getFamilia().getMembros());

	    item.setPessoas(pessoasList);
		item.setPessoasFaltosas(new HashSet<Pessoa>());
		
		MessageUtil.sucesso("Pessoa Referencia Selecionada: " + this.pessoaReferencia.getNome());			
	}
	
	public void excluirPessoa(Pessoa p) {	
		
		if(p instanceof PessoaReferencia) {
			MessageUtil.alerta("A Pessoa de referência não pode ser removida!");	
		}
		else {
			// implementar a exclusão de pessoas exceto a pessoa de referencia		
			this.item.getPessoas().remove(p);
			
			MessageUtil.sucesso("A Pessoa " + p.getNome() + " foi removida!" );
		}
	}
	
	public void marcarFalta(Pessoa p) {
		
		if(this.item.getPessoas().size() > 1) {
			this.item.getPessoasFaltosas().add(p);
			log.info("Lista: " + item.getPessoasFaltosas());
			this.item.getPessoas().remove(p);
			
			MessageUtil.sucesso("Falta marcada: " + p.getNome());
		}
		else {	
			MessageUtil.erro("O agendamento deve ter pelo menos uma pessoa.");
		}
		
	}
	public void reverterFalta(Pessoa p) {
		
		this.item.getPessoas().add(p);
		this.item.getPessoasFaltosas().remove(p);		
		
		MessageUtil.sucesso("Falta revertida: " + p.getNome());
		
	}

}