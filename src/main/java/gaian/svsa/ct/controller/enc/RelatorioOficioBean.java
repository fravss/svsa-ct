package gaian.svsa.ct.controller.enc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.Oficio;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.service.OficioService;
import gaian.svsa.ct.service.UnidadeService;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Talita
 *
 */
@Getter
@Setter
@Named
@ViewScoped
public class RelatorioOficioBean implements Serializable {

	private static final long serialVersionUID = 1769116747361287180L;

	private int qdeTotal = 0;
	private List<Oficio> listaOficios = new ArrayList<>();
	private List<Unidade> unidades = new ArrayList<>();	

	private Unidade unidade;	
	private Date dataInicio;
	private Date dataFim;	
	private Oficio oficio;	
	private Boolean sasc = false;
	
	@Inject
	private OficioService oService;
	@Inject
	private UnidadeService unidadeService;
	@Inject
	private LoginBean loginBean;
		
	
	@PostConstruct
	public void inicializar() {	
		
		unidades = unidadeService.buscarTodos(loginBean.getTenantId());
		this.unidade = loginBean.getUsuario().getUnidade();
	}	

	public void consultarOficios() {

		listaOficios = oService.buscarOficiosUnidade(unidade, dataInicio, dataFim, sasc, loginBean.getTenantId());
	
		qdeTotal = listaOficios.size();
		
	}

	public void redirectPdf() throws IOException {

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(oficio.getUrlAnexo());
    }
	
	public boolean isUnidadeSelecionada() {
		if(unidade != null)
			return true;
		return false;
	}

}
