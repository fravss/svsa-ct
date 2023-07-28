package gaian.svsa.ct.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.Familia;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.util.NegocioException;
import gaian.svsa.ct.util.jpa.Transactional;


/**
 * @author murakamiadmin
 *
 */
public class MPComposicaoDAO implements Serializable {

	private static final long serialVersionUID = 1L;	
	private Logger log = Logger.getLogger(PessoaDAO.class);
	
	@Inject
	private EntityManager manager;
	
	
	@Transactional
	public Pessoa salvar(Pessoa pessoa) throws NegocioException {
		try {
			return manager.merge(pessoa);
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
	
	

	public Pessoa buscarPeloCodigo(Long codigo) {
		return manager.find(Pessoa.class, codigo);
	}


	
	/*
	 *  PessoaReferencia
	 */
	
	public PessoaReferencia buscarPFPeloCodigo(Long codigo) {
		return manager.find(PessoaReferencia.class, codigo);
	}
	
	public List<String> pesquisarNomesPR(String termo, Long tenantId) {	
		
		String jpql = "Select p.nome from PessoaReferencia p where p.nome LIKE :termo "
				+ "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc";
		
		TypedQuery<String> query = manager.createQuery(jpql, String.class);	
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", "%" + termo + "%");	
		query.setParameter("exc", false);
		
		return query.getResultList();			
	}
	
	public List<Denuncia> pesquisarExistente(String termo, Long tenantId) {
		TypedQuery<Denuncia> q = manager.createQuery("Select p FROM Denuncia d  "
				+ "INNER JOIN Familia f ON d.familia = f.codigo "
				+ "INNER JOIN PessoaReferencia g ON d.familia.pessoaReferencia = g.codigo "				
				+ "WHERE g.nome = :termo "
				+ "and d.tenant_id = :tenantId "
				+ "and d.excluido = :exc" , Denuncia.class);
		q.setParameter("tenantId", tenantId);
		q.setParameter("termo", termo);
		q.setParameter("exc", false);
		
		return q.getResultList();		
	}
	
	// usado pelo ReceberEncaminhamento
	public List<PessoaReferencia> pesquisar(String termo, Unidade unidade, Long tenantId) {
		log.info("TermoPesquisa por pessoaReferencia/unidade na DAO = " + termo);
		
		String jpql = "from PessoaReferencia p where p.nome LIKE :termo "
				+ "and p.familia.prontuario.unidade = :unidade "
				+ "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc";
		
		TypedQuery<PessoaReferencia> query = manager.createQuery(jpql, PessoaReferencia.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("unidade", unidade);
		query.setParameter("exc", false);
		
		return query.getResultList();		
	}
	
	
	// trocaPessoa de Referencia
	@Transactional
	public void trocarPR(Familia familia, PessoaReferencia prAntiga, Pessoa prNova) throws NegocioException {
		try {
			
			log.info("trocando pessoa referencia...3 para " + prNova.getCodigo());
			
			// altera para PessoaReferencia
			final Query query = manager.createNativeQuery( "UPDATE Pessoa SET TIPO_PESSOA = 'PESSOA_REFERENCIA', "
					+ "parentescoPessoaReferencia = 'RESPONSAVEL_FAMILIAR' WHERE codigo = :id" );
			query.setParameter( "id", prNova.getCodigo() );	 
            query.executeUpdate();
            
            // altera para Pessoa
            final Query query2 = manager.createNativeQuery( "UPDATE Pessoa SET TIPO_PESSOA = 'Pessoa', "
            		+ "parentescoPessoaReferencia = 'NAO_PARENTE' WHERE codigo = :id" );
            query2.setParameter( "id", prAntiga.getCodigo() );
            query2.executeUpdate();
            
            //Altera PessoaReferencia da Familia
            final Query query3 = manager.createNativeQuery( "UPDATE Familia SET codigo_pessoa_referencia = :pr WHERE codigo = :id" );
            query3.setParameter( "pr", prNova.getCodigo() );	 
            query3.setParameter( "id", familia.getCodigo() );
            query3.executeUpdate();
			
            log.info("trocado.");
		
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
	
	
	
	
	
	
	
	
	
	
	
	public List<PessoaReferencia> todasPessoasReferencia(Long tenantId) {		
        return manager.createQuery("from PessoaReferencia p where p.excluida = :exc "
        		+ "and p.tenant_id = :tenantId ", PessoaReferencia.class)
        		.setParameter("tenantId", tenantId)
        		.setParameter("exc", false)
        		.getResultList();
    }
	
	@SuppressWarnings("unchecked")
	public List<PessoaReferencia> buscarTodasPRef(Long tenantId) {
		return manager.createNamedQuery("PessoaReferencia.buscarTodos")
			.setParameter("tenantId", tenantId)
			.setParameter("exc", false)
			.getResultList();
	}

	public List<Pessoa> buscarTodosMembros(PessoaReferencia pessoaReferencia, Long tenantId) {
		String jpql = "from Pessoa p where p.familia = :familiaReferencia "
				+ "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc";
		
		TypedQuery<Pessoa> query = manager.createQuery(jpql, Pessoa.class);	
		query.setParameter("tenantId", tenantId);
		query.setParameter("familiaReferencia", pessoaReferencia.getFamilia());
		query.setParameter("exc", false);
		
		return query.getResultList();
	}
	
	public List<Pessoa> buscarTodosMembros(Denuncia denuncia, Long tenantId) {
		String jpql = "from Pessoa p where p.familia.denuncia = :denuncia "
				+ "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc";
		
		TypedQuery<Pessoa> query = manager.createQuery(jpql, Pessoa.class);		
		query.setParameter("tenantId", tenantId);
		query.setParameter("denuncia", denuncia);
		query.setParameter("exc", false);
		
		return query.getResultList();
	}
		
	
	// para fins de testes unitários
	public void setManager(EntityManager manager) {
		this.manager = manager;
	}


	
	
}
