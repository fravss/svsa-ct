package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import gaian.svsa.ct.dao.OrgaoDAO;
import gaian.svsa.ct.modelo.Orgao;
import gaian.svsa.ct.util.NegocioException;

/**
 * @author Talita
 *
 */
public class OrgaoService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private OrgaoDAO orgaoDAO;


	public void salvar(Orgao orgao) throws NegocioException {
			
		this.orgaoDAO.salvar(orgao);
	}

	public Orgao buscarPeloCodigo(long codigo) {
		return orgaoDAO.buscarPeloCodigo(codigo);
	}
	

	public List<Orgao> buscarTodos(Long tenantId) {
		return orgaoDAO.buscarTodos(tenantId);
	}

	
	public void excluir(Orgao orgao) throws NegocioException {
		orgaoDAO.excluir(orgao);
		
	}


	public OrgaoDAO getOrgaoDAO() {
		return orgaoDAO;
	}
	
}