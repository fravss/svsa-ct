package gaian.svsa.ct.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
//import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.service.AgendamentoIndividualService;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author murakamiadmin
 *
 */
@Getter
@Setter
@Named
@ViewScoped
public class PesquisaAtendimentoRecepcaoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Atendimento> itensAtendimento = new ArrayList<>();
	
	private Atendimento itemExcluir;
	
	@Inject
	AgendamentoIndividualService atendimentoService;	
	@Inject
	LoginBean loginBean;
	
	@PostConstruct
	public void inicializar() {
		
		itensAtendimento = atendimentoService.buscarAtendimentosRecepcao(loginBean.getUsuario(), loginBean.getTenantId());
	}
	
	public void excluir() {
		try {
			atendimentoService.excluir(itemExcluir);
			
			this.itensAtendimento.remove(itemExcluir);
			MessageUtil.sucesso("Atendimento recepção número (" + itemExcluir.getCodigo() + ") excluído com sucesso.");
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
}