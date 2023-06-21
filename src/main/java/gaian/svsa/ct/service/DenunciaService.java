package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import gaian.svsa.ct.dao.DenunciaDAO;
import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.util.NegocioException;
import lombok.extern.log4j.Log4j;


/**
 * @author laurojr
 *
 */
@Log4j
public class DenunciaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DenunciaDAO denunciaDAO;
	
	public void salvar(Denuncia denuncia) throws NegocioException {
		log.debug("Service : tenant = " + denuncia.getTenant_id());
		
		this.denunciaDAO.salvar(denuncia);
	}
	
	/*public void salvarAlterar(Denuncia denuncia, Long codigoUsuarioLogado) throws NegocioException {
		
		log.info("para verificar o problema de alteração de denuncia...criada por: " + denuncia.getTecnico().getCodigo() + " **** Tentativa de alteração por : " + codigoUsuarioLogado);
			
		if(codigoUsuarioLogado.longValue() == denuncia.getTecnico().getCodigo().longValue()) {
			if(new Date().after(DateUtils.plusDays(denuncia.getDataEmissao(), 7)) ){
				throw new NegocioException("Prazo para alteração (7 dias) foi ultrapassado!");
					
			}
			else {
				denunciaDAO.salvar(denuncia);
			}
		}
		else {
			throw new NegocioException("Somente o conselheiro que registrou pode alterar a denuncia! E isso só pode ser feito antes de 7 dias do registro.");
		}	
	} */
	
	public void excluir(Denuncia denuncia) throws NegocioException {
		denunciaDAO.excluir(denuncia);
		
	}
	
/* Buscas */
	
	
	public List<String> buscarNomes(String query, Long tenantId) {		
		return denunciaDAO.buscarNomes(query, tenantId);
	}

	public Denuncia buscarPeloCodigo(long codigo) {
		return denunciaDAO.buscarPeloCodigo(codigo);
	}
	
	// buscar pessoa pelo nome
	public PessoaReferencia buscarPeloNome(String nome) {
		return denunciaDAO.buscarPeloNome(nome);
	}

	public List<Denuncia> buscarTodos(Long tenantId) {
		return denunciaDAO.buscarTodos(tenantId);
	}
	
	public List<Denuncia> buscarTodosDia(Long tenantId, Unidade unidade) {
		return denunciaDAO.buscarTodosDia(tenantId, unidade);
	}
	
	public Denuncia buscarDenuncia(Long codigo, Unidade unidade, Long tenantId) {
		
		return denunciaDAO.buscarDenuncia(codigo, unidade, tenantId);
	}
}
