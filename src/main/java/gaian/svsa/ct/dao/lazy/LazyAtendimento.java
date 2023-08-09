package gaian.svsa.ct.dao.lazy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ComparatorUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import gaian.svsa.ct.dao.AgendamentoIndividualDAO;
import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.CodigoAuxiliarAtendimento;
import gaian.svsa.ct.modelo.to.DatasIniFimTO;
import gaian.svsa.ct.service.AgendamentoIndividualService;


/**
 * @author murakamiadmin
 *
 */
public class LazyAtendimento extends LazyDataModel<Atendimento> implements Serializable {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(LazyAtendimento.class);
	
	private List<Atendimento> atendimentos = new ArrayList<Atendimento>();
	private AgendamentoIndividualDAO atendDAO;
	private Unidade unidade;
	private DatasIniFimTO datasTO;
	private CodigoAuxiliarAtendimento codigoAux;
	private Long tenantId;
	
	public LazyAtendimento(AgendamentoIndividualService service, Unidade unidade, DatasIniFimTO datasTO, CodigoAuxiliarAtendimento codigoAux, Long tenantId) {
		
		this.atendDAO = service.getListaDAO();
		this.unidade = unidade;
		this.datasTO = datasTO;
		this.codigoAux = codigoAux;
		this.tenantId = tenantId;
	}	
 
	
	@Override
    public Atendimento getRowData(String rowKey) {        
		return atendDAO.buscarPeloCodigo(Long.parseLong(rowKey));
    }
	
	@Override
	public String getRowKey(Atendimento lista) {
        return String.valueOf(lista.getCodigo());
    }
	
	@Override
	public int count(Map<String, FilterMeta> filterBy) {
		
		if(this.codigoAux == null)
			return this.atendDAO.encontrarQde(unidade, datasTO, tenantId).intValue();
		else
			return this.atendDAO.encontrarQde(unidade, datasTO, codigoAux, tenantId).intValue();
		
		/*
		 return (int) atendimentos.stream()
		
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();
         */
	}	
	/* Para ser usado com filtros automaticamente.
	 * TO DO no futuro implementar sem filtro HARDCODED (que está no load). Seria retirado e usado esse metodo.
	 
	private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();

            try {
                Object columnValue = String.valueOf(o.getClass().getField(filter.getField()).get(o));
                matching = constraint.isMatching(context, columnValue, filterValue, LocaleUtils.getCurrentLocale());
            }
            catch (ReflectiveOperationException e) {
                matching = false;
            }

            if (!matching) {
                break;
            }
        }

        return matching;
    }
    */
	
	@Override
	public List<Atendimento> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filters) {	
		
		String filtro1 = "";
		String filtro2 = "";
		String filtro3 = "";
		
		for (Map.Entry<String, FilterMeta> entrada : filters.entrySet()) { 
            try {
            	FilterMeta meta = entrada.getValue();
            	         
                    if(meta.getField().equals("pessoa.codigo")) {
                    	filtro1 = (String)meta.getFilterValue();
                    } else if(meta.getField().equals("pessoa.nome")) {
                    	filtro2 = (String)meta.getFilterValue();
                    } else if(meta.getField().equals("conselheiro.nome")) {
                    	filtro3 = (String)meta.getFilterValue();
                    }                    	
	
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

		
		atendimentos = new ArrayList<Atendimento>();
		int dataSize = 0;
		
		
		if(filtro1 != null && !filtro1.equals("")) {			
			log.debug("filtro por codigo pessoa = :" + filtro1);
			
			if(this.codigoAux == null) {
				
				atendimentos = this.atendDAO.buscarComPaginacao(first, pageSize, unidade, datasTO, filtro1, 1, tenantId);	
				dataSize = this.atendDAO.encontrarQde(unidade, datasTO, filtro1, 1, tenantId).intValue();
			}
			else {
				
				atendimentos = this.atendDAO.buscarComPaginacao(first, pageSize, unidade, datasTO, codigoAux, filtro1, 1, tenantId);	
				dataSize = this.atendDAO.encontrarQde(unidade, datasTO, codigoAux, filtro1, 1, tenantId).intValue();
			}
				
			this.setRowCount(dataSize); 
		} 
		else if(filtro2 != null && !filtro2.equals(""))  {
			log.debug("filtro por nome pessoa = :" + filtro2); 
			log.debug("buscarComPaginacao (parametros): " + first + ", " + pageSize + ", " + unidade.getCodigo() + ", " + datasTO.getIni() + ", " + datasTO.getFim());
			
			if(this.codigoAux == null) {
				
				atendimentos = this.atendDAO.buscarComPaginacao(first, pageSize, unidade, datasTO, filtro2, 2, tenantId);	
				dataSize = this.atendDAO.encontrarQde(unidade, datasTO, filtro2, 2, tenantId).intValue();
			}
			else {
				
				atendimentos = this.atendDAO.buscarComPaginacao(first, pageSize, unidade, datasTO, codigoAux, filtro2, 2, tenantId);	
				dataSize = this.atendDAO.encontrarQde(unidade, datasTO, codigoAux, filtro2, 2, tenantId).intValue();
			}
			
			this.setRowCount(dataSize);
		} 
		else if(filtro3 != null && !filtro3.equals("")) {
			log.debug("filtro por nome tecnico = :" + filtro3);  
			
			if(this.codigoAux == null) {
				
				atendimentos = this.atendDAO.buscarComPaginacao(first, pageSize, unidade, datasTO, filtro3, 3, tenantId);
				dataSize = this.atendDAO.encontrarQde(unidade, datasTO, filtro3, 3, tenantId).intValue();
			}
			else {
				
				atendimentos = this.atendDAO.buscarComPaginacao(first, pageSize, unidade, datasTO, codigoAux, filtro3, 3, tenantId);
				dataSize = this.atendDAO.encontrarQde(unidade, datasTO, codigoAux, filtro3, 3, tenantId).intValue();
			}
			
			this.setRowCount(dataSize);
		} 
		else {	
			log.debug("sem filtro da unidade " + unidade.getNome()); 
			log.debug("buscarComPaginacao (parametros): " + first + ", " + pageSize + ", " + unidade.getCodigo() + ", " + datasTO.getIni() + ", " + datasTO.getFim());
			
			if(this.codigoAux == null) {
				
				atendimentos = this.atendDAO.buscarComPaginacao(first, pageSize, unidade, datasTO, tenantId);
				dataSize = this.atendDAO.encontrarQde(unidade, datasTO, tenantId).intValue();
			}
			else {
				
				atendimentos = this.atendDAO.buscarComPaginacao(first, pageSize, unidade, datasTO, codigoAux, tenantId);
				dataSize = this.atendDAO.encontrarQde(unidade, datasTO, codigoAux, tenantId).intValue();
			}
			
			this.setRowCount(dataSize);
			log.debug("qde = : " + atendimentos.size() + " dataSize: " + dataSize);
		}

		log.debug("tamanho = :" + dataSize);
		
		// sort
        if (!sortBy.isEmpty()) {
            List<Comparator<Atendimento>> comparators = sortBy.values().stream()
                    .map(o -> new LazyAtendimentoSorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<Atendimento> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            atendimentos.sort(cp);
        }
        
        return atendimentos;
	}
	
}