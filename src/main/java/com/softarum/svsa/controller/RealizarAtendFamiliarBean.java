package com.softarum.svsa.controller;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import com.softarum.svsa.modelo.AgendamentoFamiliar;
import com.softarum.svsa.modelo.Usuario;
import com.softarum.svsa.modelo.enums.CodigoAuxiliarAtendimento;
import com.softarum.svsa.modelo.enums.EnumUtil;
import com.softarum.svsa.service.AgendamentoFamiliarService;
import com.softarum.svsa.service.UsuarioService;
import com.softarum.svsa.util.MessageUtil;
import com.softarum.svsa.util.NegocioException;

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
	private DualListModel<Usuario> tecnicos;
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
		
		carregarTecnicos();
					
		buscarAtendimentosFamiliar();
		
		limpar();	
	}
	
	private void carregarCodAux() {
		
		this.codigosAuxiliares = EnumUtil.getCodigosAtendIndividualizado();
	}
	
	private void carregarTecnicos() {		
		
		List<Usuario>tecsSource = new ArrayList<Usuario>();
		tecsSource = usuarioService.buscarTecnicos(usuarioLogado.getUnidade(), loginBean.getTenantId());
		tecsSource.remove(usuarioLogado);
		List<Usuario> tecsTarget = new ArrayList<Usuario>();
       
        tecnicos = new DualListModel<Usuario>(tecsSource, tecsTarget);
	}
	
	public void onTransfer(TransferEvent event) {

        for(Object tecnico : event.getItems()) {
        	MessageUtil.sucesso("Técnico " + ((Usuario) tecnico).getNome() + " selecionado.");
        }         
    }
	
	
	public void salvarAtendimento() {
		try {		
			
			item.setTecnico(usuarioLogado);
			item.setTecnicos(tecnicos.getTarget());
			item.setTenant_id(loginBean.getTenantId());
			
			this.agendamentoFamiliarService.salvarAtendFamiliar(item);
			this.listaAtendimentos.remove(item);
			limpar();
			MessageUtil.sucesso("Atendimento familiar gravado com sucesso!");
								
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void autoSave() {
		try {
			
			Instant time = Instant.now();
			log.info("auto save atendimento coletivo... " + time);
			
			item.setTecnico(usuarioLogado);			
			item.setTecnicos(tecnicos.getTarget());
			item.setTenant_id(loginBean.getTenantId());
			
			log.info("ANTES...auto save atendimento FAMILIAR codigo = " + item.getCodigo());
			this.agendamentoFamiliarService.autoSave(item);
			log.info("DEPOIS...auto save atendimento FAMILIAR codigo = " + item.getCodigo());
			MessageUtil.sucesso("Auto save executado.");
								
		} catch (NegocioException e) {
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