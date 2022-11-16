package com.softarum.svsa.dao.ct;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;

import com.softarum.svsa.modelo.PessoaReferencia;
import com.softarum.svsa.modelo.Unidade;
import com.softarum.svsa.modelo.ct.Denuncia;
import com.softarum.svsa.modelo.ct.PessoaDenuncia;
import com.softarum.svsa.util.DateUtils;
import com.softarum.svsa.util.NegocioException;
import com.softarum.svsa.util.jpa.Transactional;

/**
 * @author laurojr
 *
 */
public class DenunciaDAO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	
	@Transactional
	public Denuncia salvar(Denuncia denuncia) throws NegocioException {
		try {
			// se pessoa nova
			if(denuncia.getPessoa().getCodigo() == null) {
				denuncia.getPessoa().setTenant_id(denuncia.getTenant_id());
				denuncia.getPessoa().setUnidade(denuncia.getUnidade());
				PessoaDenuncia p = manager.merge(denuncia.getPessoa());
				denuncia.setPessoa(p);
			}
			else {
				manager.merge(denuncia.getPessoa());
			}
			return manager.merge(denuncia);	
			
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
}
