package gaian.svsa.ct.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;

import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.Status;
import gaian.svsa.ct.util.DateUtils;
import gaian.svsa.ct.util.NegocioException;
import gaian.svsa.ct.util.jpa.Transactional;

/**
 * @author laurojr
 *
 */
public class DenunciaDAO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(DenunciaDAO.class);

	@Inject
	private EntityManager manager;
	
	@Transactional
	public void salvar(Denuncia denuncia) throws NegocioException {
		
		try {
			
			manager.merge(denuncia);
			
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Error e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		}		
	}
	
	@Transactional
	public void excluir(Denuncia denuncia) throws NegocioException {
		denuncia = buscarPeloCodigo(denuncia.getCodigo());
		try {
			manager.remove(denuncia);
			manager.flush();
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw e;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Error e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		}
	}
	
	/*
	 * Buscas
	 */
	
	public Denuncia buscarPeloCodigo(Long codigo) {
		return manager.find(Denuncia.class, codigo);
	}
	
	public PessoaReferencia buscarPeloNome(String nome) {
		return manager.createNamedQuery("PessoaReferencia.buscarPeloNome", PessoaReferencia.class)
				.setParameter("nome", nome)
				.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Denuncia> buscarTodos(Long tenantId) {
		return manager.createNamedQuery("Denuncia.buscarTodos")
				.setParameter("tenantId", tenantId)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Denuncia> buscarTodosDia(Long tenantId, Unidade unidade) {
		
		Date data = new Date();
		return manager.createNamedQuery("Denuncia.buscarTodosDia")
				.setParameter("tenantId", tenantId)
				.setParameter("unidade", unidade)
				.setParameter("ini", DateUtils.minusDay(data), TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(data), TemporalType.TIMESTAMP)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> buscarNomes(String query, Long tenantId) {
		return manager.createNamedQuery("PessoaReferencia.buscarNomes")
				.setParameter("tenantId", tenantId)
				.setParameter("nome", query + "%")
				.getResultList();
	}
	
	public Denuncia buscarDenuncia(Long codigo, Unidade unidade, Long tenantId) {
		return manager.createQuery("select p from Denuncia p where p.codigo = :codigo "
				+ "and p.excluido = :exc "
				+ "and p.tenant_id = :tenantId "
				+ "and p.unidade = :unidade", Denuncia.class)
				.setParameter("codigo", codigo)
				.setParameter("tenantId", tenantId)
				.setParameter("unidade", unidade)
				.setParameter("exc", false)
				.getSingleResult();
	}
	
	@Transactional
	public void ativarDenuncia(Denuncia denuncia) throws NegocioException {
		
		try {
			
			Denuncia d = buscarPeloCodigo(denuncia.getCodigo());
			
			d.setStatus(Status.ATIVO);
			manager.merge(d);		
			
			log.info("Denúncia ativada: " + d.getCodigo());
			
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Error e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		}
	}
	
	@Transactional
	public void inativarDenuncia(Denuncia denuncia) throws NegocioException {
		try {
			
			Denuncia d = buscarPeloCodigo(denuncia.getCodigo());				
			
			d.setStatus(Status.INATIVO);
			manager.merge(d);
			
			log.info("Denúncia inativado: " + d.getCodigo());
			
			
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Error e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		}	
	}
}
