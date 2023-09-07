package gaian.svsa.ct.controller.rel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.PieChartModel;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.AgendamentoFamiliar;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.Ano;
import gaian.svsa.ct.modelo.enums.CodigoAuxiliarAtendimento;
import gaian.svsa.ct.modelo.enums.EnumUtil;
import gaian.svsa.ct.modelo.enums.Grupo;
import gaian.svsa.ct.modelo.enums.Mes;
import gaian.svsa.ct.modelo.to.DatasIniFimTO;
import gaian.svsa.ct.service.AgendamentoFamiliarService;
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
public class RelatorioAtendimentoFamiliarBean implements Serializable {
	
	private static final long serialVersionUID = 1769116747361287180L;

	private Long qdeTotal = 0L;
	private List<AgendamentoFamiliar> listaAtendimentoFamiliar = new ArrayList<>();
	private List<AgendamentoFamiliar> listaAtendimentoFamiliarGrafico = new ArrayList<>();
	private Unidade unidade;
	private AgendamentoFamiliar itemAlterar = new AgendamentoFamiliar();
	private AgendamentoFamiliar itemExcluir;
	private boolean consultado = false;
	
	private List<CodigoAuxiliarAtendimento> codigosAux;
	private CodigoAuxiliarAtendimento codigoAux;
	
	private List<String> anos = new ArrayList<>(Arrays.asList(Ano.getAnos()));
	private Integer ano;
	private List<Mes> meses;	
	private Mes mes;
	private DatasIniFimTO datasTO;
	private Date dataInicio;
	private Date dataFim;
	private AgendamentoFamiliar agendamentoFamiliar;
	
	private PieChartModel graficoAtendimentoFamiliar;
	private PieChartModel graficoAtendimentoFamiliarCol;	
	
	
	@Inject
	AgendamentoFamiliarService agendamentoFamiliarService;
	@Inject
	private LoginBean loginBean;
		
	
	@PostConstruct
	public void inicializar() {	
		LocalDate data = LocalDate.now();
		setAno(data.getYear());
		setMes(Mes.porCodigo(data.getMonthValue()));
		
		anos = new ArrayList<>(Arrays.asList(Ano.getAnos()));		
		meses = Arrays.asList(Mes.values());
		codigosAux = EnumUtil.getTiposAtendimento();
		
		unidade = loginBean.getUsuario().getUnidade();
		graficoAtendimentoFamiliar = new PieChartModel();
		graficoAtendimentoFamiliarCol = new PieChartModel();		
	}
	
	public void consultarAtendimentos() {
		datasTO = DateUtils.getDatasIniFim(getAno(), getMes());
		listaAtendimentoFamiliar = agendamentoFamiliarService.buscarAtendimentosCodAux(unidade, datasTO.getIni(), datasTO.getFim(), loginBean.getTenantId());
		qdeTotal = (long) listaAtendimentoFamiliar.size();
		consultado = true;
	}
	
	public void initGraficoAtendimentoFamiliar() {		
		listaAtendimentoFamiliarGrafico =  listaAtendimentoFamiliar; 
		qdeTotal = Long.valueOf(listaAtendimentoFamiliarGrafico.size());
		createPieModel();
	}


	private void createPieModel() {
		
		log.info("Criando grafico  ... ");	
		graficoAtendimentoFamiliar = new PieChartModel(); 
        
        
        if(listaAtendimentoFamiliarGrafico != null && listaAtendimentoFamiliarGrafico.size() > 0) {

        	
        	log.info("Tamanho da lista: " + listaAtendimentoFamiliarGrafico.size());        	
        	
        	String codigo = listaAtendimentoFamiliarGrafico.get(0).getCodigoAuxiliar().toString();        	
        	int qde = 0; 
	        for(AgendamentoFamiliar l: listaAtendimentoFamiliarGrafico) {   
        		
        		
	        	if(!l.getCodigoAuxiliar().toString().equals(codigo)) {
	        		graficoAtendimentoFamiliar.set(codigo, qde);
	            	
	            	codigo = l.getCodigoAuxiliar().toString();
	            	qde = 1;	            	
	        	}
	        	else {
	        		qde++;
	        	}
	        }
	        log.info("criado o grafico");       
	        graficoAtendimentoFamiliar.set(codigo, qde);	        
	        graficoAtendimentoFamiliar.setLegendPosition("e");	        
	        
        }
        else
        	MessageUtil.alerta("Não existe atendimentos realizados.");
        
    }
	
	public void alterar() {
		
		try {
			agendamentoFamiliarService.salvarAlterar(itemAlterar, loginBean.getUsuario() );
			log.info("Atendimento Alterado por " + loginBean.getUserName());
			
			MessageUtil.sucesso("Atendimento familiar número (" + itemAlterar.getCodigo() + ") alterado com sucesso.");
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void excluir() {
		try {
			agendamentoFamiliarService.excluir(itemExcluir);
			log.info("Atendimento DELETED por " + loginBean.getUserName());
			this.listaAtendimentoFamiliar.remove(itemExcluir);
			MessageUtil.sucesso("Atendimento familiar número (" + itemExcluir.getCodigo() + ") excluído com sucesso.");
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}

	public boolean isCoordenador() {
		if(loginBean.getUsuario().getGrupo() == Grupo.COORDENADORES)
			return true;
		
		return false;
	}

}