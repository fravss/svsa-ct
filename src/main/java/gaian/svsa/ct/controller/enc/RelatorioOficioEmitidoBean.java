package gaian.svsa.ct.controller.enc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.OficioEmitido;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.service.OficioEmitidoService;
import gaian.svsa.ct.service.UnidadeService;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Lauro
 *
 */
@Getter
@Setter
@Named
@ViewScoped
public class RelatorioOficioEmitidoBean implements Serializable {

	private static final long serialVersionUID = 1L;	

	private int qdeTotal = 0;
	private List<OficioEmitido> listaOficiosEmitidos = new ArrayList<>();
	private List<Unidade> unidades = new ArrayList<>();	

	private Unidade unidade;	
	private Date dataInicio;
	private Date dataFim;	
	private OficioEmitido oficioEmitido;	
	
	@Inject
	private OficioEmitidoService oService;
	@Inject
	private UnidadeService unidadeService;
	@Inject
	private LoginBean loginBean;
		
	
	@PostConstruct
	public void inicializar() {	
		
		unidades = unidadeService.buscarTodos(loginBean.getTenantId());
		this.unidade = loginBean.getUsuario().getUnidade();
	}	

	public void consultarOficiosEmitidos() {

		listaOficiosEmitidos = oService.buscarOficiosEmitidosUnidade(unidade, dataInicio, dataFim, loginBean.getTenantId());
	
		qdeTotal = listaOficiosEmitidos.size();
		
	}
	
	public boolean isUnidadeSelecionada() {
		if(unidade != null)
			return true;
		return false;
	}
	
}
