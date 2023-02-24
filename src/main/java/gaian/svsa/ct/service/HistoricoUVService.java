package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import gaian.svsa.ct.dao.HistoricoUVDAO;
import gaian.svsa.ct.modelo.HistPessoaUV;
import gaian.svsa.ct.modelo.HistProntuarioUV;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.util.NegocioException;
import lombok.extern.log4j.Log4j;

/**
 * @author murakamiadmin
 *
 */
@Log4j
public class HistoricoUVService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private HistoricoUVDAO historicoDAO;
	
	
	/*
	 * Historico de Prontuario UV
	 */
	
	public void salvar(HistProntuarioUV historico) throws NegocioException {		
		this.historicoDAO.salvar(historico);
	}
	
	public List<HistProntuarioUV> buscarTodos(Long tenantId) {
		return historicoDAO.buscarTodos(tenantId);
	}

	public List<HistProntuarioUV> buscarTodos(Unidade unidade, Date dataInicio, Date dataFim, Long tenantId) {
		
		if(dataInicio != null)
			if(dataFim != null) {
				log.info("ações periodo -- Ini: " + dataInicio + " -- fim: " + dataFim);
				return historicoDAO.buscarTodos(unidade, dataInicio, dataFim, tenantId);
			}
			else {
				log.info("ações periodo -- Ini: " + dataInicio + " -- fim: " + dataFim);
				return historicoDAO.buscarTodos(unidade, dataInicio, new Date(), tenantId);
			}		
		
		return historicoDAO.buscarTodos(unidade, tenantId);
	}
	
	
	
	
	
	/*
	 * Historico de Pessoa UV
	 */

	public void salvar(HistPessoaUV historico) throws NegocioException {		
		this.historicoDAO.salvar(historico);
	}
	
	public List<HistPessoaUV> buscarTodosPUV(Long tenantId) {
		return historicoDAO.buscarTodosPUV(tenantId);
	}

	public List<HistPessoaUV> buscarTodosPUV(Unidade unidade, Date dataInicio, Date dataFim, Long tenantId) {
		
		if(dataInicio != null)
			if(dataFim != null) {
				log.info("ações periodo -- Ini: " + dataInicio + " -- fim: " + dataFim);
				return historicoDAO.buscarTodosPUV(unidade, dataInicio, dataFim, tenantId);
			}
			else {
				log.info("ações periodo -- Ini: " + dataInicio + " -- fim: " + dataFim);
				return historicoDAO.buscarTodosPUV(unidade, dataInicio, new Date(), tenantId);
			}		
		
		return historicoDAO.buscarTodosPUV(unidade, tenantId);
	}
}
