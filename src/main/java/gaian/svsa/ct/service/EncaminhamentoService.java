package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import gaian.svsa.ct.dao.EncaminhamentoDAO;
import gaian.svsa.ct.dao.OrgaoDAO;
import gaian.svsa.ct.modelo.Encaminhamento;
import gaian.svsa.ct.modelo.Orgao;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.CodigoEncaminhamento;
import gaian.svsa.ct.util.NegocioException;


/**
 * @author gabrielrodrigues/Murakami
 *
 */
public class EncaminhamentoService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EncaminhamentoDAO encaminhamentoDAO;
	@Inject
	private OrgaoDAO orgaoDAO;
	

	
	public Encaminhamento salvar(Encaminhamento encaminhamento) throws NegocioException {			
		return this.encaminhamentoDAO.salvar(encaminhamento);
	}	
	
	public List<Encaminhamento> buscarEncaminhamentos(Pessoa pessoa, Long tenantId) {
		return encaminhamentoDAO.buscarEncaminhamentos(pessoa, tenantId);
	}
	
	public void excluir(Encaminhamento encaminhamento) throws NegocioException {
		encaminhamentoDAO.excluir(encaminhamento);		
	}
	
	
	public List<Orgao> buscarTodos(Long tenantId){
		return orgaoDAO.buscarTodos(tenantId);
	}
	
	public List<Orgao> buscarCodigosEncaminhamento(CodigoEncaminhamento codigosEncaminhamento, Long tenantId){
		return orgaoDAO.buscarCodigosEncaminhamento(codigosEncaminhamento,tenantId);
	}
	
	/*
	 * RelatorioEncExterno
	 */
	public List<Encaminhamento> buscarEncaminhamentos(Unidade unidade, Date dataInicio, Date dataFim, Long tenantId) {
		
		if(dataInicio != null) {
			if(dataFim != null) {
				return encaminhamentoDAO.buscarEncaminhamentos(unidade, dataInicio, dataFim, tenantId);
			}
			else {
				return encaminhamentoDAO.buscarEncaminhamentos(unidade, dataInicio,  new Date(), tenantId);
			}
		}
		return encaminhamentoDAO.buscarEncaminhamentos(unidade, tenantId);
	}
	/*
	 * RelatorioEncExterno
	 */
	public List<Encaminhamento> buscarEncaminhamentosGrafico(Unidade unidade, Date dataInicio, Date dataFim, Long tenantId) {
		
		if(dataInicio != null) {
			if(dataFim != null) {
				return encaminhamentoDAO.buscarEncaminhamentosGrafico(unidade, dataInicio, dataFim, tenantId);
			}
			else {
				return encaminhamentoDAO.buscarEncaminhamentosGrafico(unidade, dataInicio,  new Date(), tenantId);
			}
		}
		return encaminhamentoDAO.buscarEncaminhamentosGrafico(unidade, tenantId);
	}
}