package gaian.svsa.ct.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.service.UnidadeService;
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
public class PesquisaUnidadeBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Unidade> unidades = new ArrayList<>();
	private Unidade unidadeSelecionada;
	
	@Inject
	UnidadeService unidadeService;
	@Inject
	private LoginBean loginBean;

		
	@PostConstruct
	public void inicializar() {
		unidades = unidadeService.buscarTodos(loginBean.getTenantId());
	}
	
	public void excluir() {
		try {
			unidadeService.excluir(unidadeSelecionada);			
			this.unidades.remove(unidadeSelecionada);
			MessageUtil.sucesso("Unidade " + unidadeSelecionada.getNome() + " excluída com sucesso.");
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
}