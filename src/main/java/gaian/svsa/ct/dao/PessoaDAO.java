package gaian.svsa.ct.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import gaian.svsa.ct.modelo.Pais;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.ProgramaSocial;
import gaian.svsa.ct.modelo.to.PessoaDTO;
import gaian.svsa.ct.util.NegocioException;
import gaian.svsa.ct.util.jpa.Transactional;
import lombok.extern.log4j.Log4j;

/**
 * @author murakamiadmin
 *
 */
@Log4j
public class PessoaDAO implements Serializable {

	private static final long serialVersionUID = 2L;
	
	@Inject
	private EntityManager manager;

	@Transactional
	public void excluir(Pessoa pessoa) throws NegocioException {
		try {
			manager.remove(pessoa);
			manager.flush();
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

	// buscas

	public Pessoa buscarPeloCodigo(Long codigo) {
		return manager.find(Pessoa.class, codigo);
	}
	public PessoaReferencia buscarPFPeloCodigo(Long codigo) {
		return manager.find(PessoaReferencia.class, codigo);
	}
	
	public Pessoa buscarPeloNome(String nome) {		
		return manager.createNamedQuery("Pessoa.buscarPeloNome", Pessoa.class)
				.setParameter("nome", nome)
				.setParameter("exc", false)
				.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> buscarNomes(String query, Unidade unidade, Long tenantId) {
		return manager.createNamedQuery("Pessoa.buscarNomes")
				.setParameter("tenantId", tenantId)
				.setParameter("unidade", unidade)
				.setParameter("exc", false)
				.setParameter("nome", "%" + query + "%")
				.getResultList();
	}


	public Pessoa buscarPessoa(Long codigo, Unidade unidade, Long tenantId) {

		String jpql = "from Pessoa p where p.codigo = :codigo " + "and p.familia.denuncia.unidade = :unidade "
				+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc";

		TypedQuery<Pessoa> query = manager.createQuery(jpql, Pessoa.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("codigo", codigo);
		query.setParameter("unidade", unidade);
		query.setParameter("exc", false);

		return query.getSingleResult();

	}

	/* SelecionaPessoa */

	
	public List<PessoaDTO> pesquisarPessoaDTO(String termo, Unidade unidade, Long tenantId) {
		log.info("TermoPesquisa por nome na DAO = " + termo);

		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, "
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from Pessoa p "
			+ "where p.nome LIKE :termo " 
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc "
				+ "and p.familia.denuncia.unidade = :unidade ";

		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);
		query.setParameter("unidade", unidade);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("tenantId", tenantId);
		query.setParameter("exc", false);

		return query.getResultList();
	}

	public List<PessoaDTO> pesquisarPessoaPorEnderecoDTO(String termo, Unidade unidade, Long tenantId) {
		log.info("TermoPesquisa por endereco na DAO = " + termo);

		/*
		String jpql = "from Pessoa p 
		where p.familia.endereco.endereco LIKE :termo "
				+ "and p.familia.prontuario.unidade = :unidade " + "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc";

		TypedQuery<Pessoa> query = manager.createQuery(jpql, Pessoa.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("unidade", unidade);
		query.setParameter("exc", false);

		return query.getResultList();
	}
	*/
		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome,"
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, "
				+ "p.familia.denuncia.status) "
			+ "from Pessoa p "
			+ "where p.endereco.endereco LIKE :termo "
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc "
				+ "and p.familia.denuncia.unidade = :unidade ";
		
		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);
		query.setParameter("unidade", unidade);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("tenantId", tenantId);
		query.setParameter("exc", false);

		return query.getResultList();
	}

	public List<PessoaDTO> pesquisarPessoaPorDenunciaDTO(String termo, Unidade unidade, Long tenantId) {
		log.info("TermoPesquisa por denuncia na DAO = " + termo);

		/*
		String jpql = "from Pessoa p where p.familia.prontuario.codigo = :termo "
				+ "and p.familia.prontuario.unidade = :unidade " + "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc";

		TypedQuery<Pessoa> query = manager.createQuery(jpql, Pessoa.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", Long.valueOf(termo));
		query.setParameter("unidade", unidade);
		query.setParameter("exc", false);

		return query.getResultList();
		*/
		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, "
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from Pessoa p "
			+ "where p.familia.denuncia.codigo = :termo "
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc "
				+ "and p.familia.denuncia.unidade = :unidade ";
		
		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);
		query.setParameter("unidade", unidade);
		query.setParameter("termo", Long.valueOf(termo));
		query.setParameter("tenantId", tenantId);
		query.setParameter("exc", false);
		
		return query.getResultList();
	}

	/* SelecionaPessoaGeral */

	public List<PessoaDTO> pesquisarPessoaDTO(String termo, Long tenantId) {

		log.info("TermoPesquisa pessoa na DAO = " + termo);
		/*
		String jpql = "from Pessoa p where p.nome LIKE :termo " + "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc";
		*/
		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, "
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from Pessoa p "
			+ "where p.nome LIKE :termo " 
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc ";

		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("exc", false);

		return query.getResultList();

	}

	public List<PessoaDTO> pesquisarPessoaPorEnderecoDTO(String termo, Long tenantId) {
		log.info("TermoPesquisa pessoa por endereco na DAO = " + termo);

		/*
		String jpql = "from Pessoa p where p.familia.endereco.endereco LIKE :termo " + "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc";
		*/
		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, "
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from Pessoa p "
			+ "where p.endereco.endereco LIKE :termo " 
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc ";
		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("exc", false);

		return query.getResultList();
	}

	public List<PessoaDTO> pesquisarPessoaPorDenunciaDTO(String termo, Long tenantId) {
		log.info("TermoPesquisa pessoa por denuncia na DAO = " + termo);
		/*
		String jpql = "from Pessoa p where p.familia.prontuario.codigo = :termo " + "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc";
		*/
		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, "
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from Pessoa p "
			+ "where p.familia.denuncia.codigo = :termo " 
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc ";

		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", Long.valueOf(termo));
		query.setParameter("exc", false);

		return query.getResultList();
	}

	/*
	 * SelecionaPessoaReferencia
	 */
	
	
	public List<PessoaDTO> pesquisarPorNome(String termo, Unidade unidade, Long tenantId) {
		log.info("TermoPesquisa PR por nome na DAO = " + termo);		
		
		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, " 
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from PessoaReferencia p "
			+ "where p.nome LIKE :termo " 
				+ "and p.familia.denuncia.unidade = :unidade "
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc ";

		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("unidade", unidade);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("exc", false);
		
		return query.getResultList();		
	}	
	
	public List<PessoaDTO> pesquisarPorNome(String termo, Long tenantId) {
		log.info("TermoPesquisa por pessoaReferencia/geral na DAO = " + termo);

		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, " 
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from PessoaReferencia p "
			+ "where p.nome LIKE :termo " 
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc ";
		
		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("exc", false);
		
		return query.getResultList();
	}	
	
	public List<PessoaDTO> pesquisarPorEndereco(String termo, Unidade unidade, Long tenantId) {
		log.info("TermoPesquisa por endereco/unidade na DAO = " + termo);
		
		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, " 
				+ "p.nome, " 
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from PessoaReferencia p "
			+ "where p.endereco.endereco LIKE :termo " 
				+ "and p.familia.denuncia.unidade = :unidade "
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc ";

		
		TypedQuery<PessoaDTO> query = manager
				.createQuery(jpql, PessoaDTO.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("unidade", unidade);
		query.setParameter("exc", false);
		
		return query.getResultList();
	}	
	public List<PessoaDTO> pesquisarPorEndereco(String termo, Long tenantId) {
		log.info("TermoPesquisa por endereco/geral na DAO = " + termo);
	
		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, " 
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from PessoaReferencia p "
			+ "where p.endereco.endereco LIKE :termo " 
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc ";
		
		TypedQuery<PessoaDTO> query = manager
				.createQuery(jpql, PessoaDTO.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", "%" + termo + "%");
		query.setParameter("exc", false);
		
		return query.getResultList();
	}	
	
	public List<PessoaDTO> pesquisarPorDenuncia(String termo, Unidade unidade, Long tenantId) {
		log.info("TermoPesquisa por denuncia/unidade na DAO = " + termo + " unidade = " + unidade.getCodigo());
				
		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, " 
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from PessoaReferencia p "
			+ "where p.familia.denuncia.codigo = :termo " 
				+ "and p.familia.denuncia.unidade = :unidade "
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc ";
		
		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", Long.valueOf(termo));
		query.setParameter("unidade", unidade);
		query.setParameter("exc", false);
		
		return query.getResultList();
	}
	
	public List<PessoaDTO> pesquisarPorDenuncia(Long termo, Long tenantId) {
		log.info("TermoPesquisa por denuncia/geral na DAO = " + termo);	

		String jpql = "SELECT new gaian.svsa.ct.modelo.to.PessoaDTO( "
				+ "p.codigo, "
				+ "p.familia.denuncia.codigo, "
				+ "p.nome, "  
				+ "p.dataNascimento, "
				+ "p.familia.denuncia.unidade.nome, " 
				+ "p.familia.denuncia.status) "
			+ "from PessoaReferencia p "
			+ "where p.familia.denuncia.codigo = :termo " 
				+ "and p.tenant_id = :tenantId " 
				+ "and p.excluida = :exc ";
		
		TypedQuery<PessoaDTO> query = manager.createQuery(jpql, PessoaDTO.class);	
		query.setParameter("tenantId", tenantId);
		query.setParameter("termo", termo);
		query.setParameter("exc", false);
		
		return query.getResultList();
	}

	/* Filtros PesquisaPessoa paginação */

	@SuppressWarnings("unchecked")
	public List<Pessoa> buscarComPaginacao(int first, int pageSize, String termo, int codigo, Long tenantId) {

		if (codigo == 1) { // nome
			log.debug("filtro = " + codigo);
			return manager
					.createQuery("Select p From Pessoa p where p.nome LIKE :termo " + "and p.tenant_id = :tenantId "
							+ "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).setFirstResult(first).setMaxResults(pageSize).getResultList();
		} /*else if (codigo == 2) { // filtroNomeSocial
			log.debug("filtro = " + codigo);
			return manager
					.createQuery("Select p From Pessoa p where p.nomeSocial LIKE :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).setFirstResult(first).setMaxResults(pageSize).getResultList();
		}*/ /* else if (codigo == 3) { // filtroMae
			log.debug("filtro = " + codigo);
			return manager
					.createQuery("Select p From Pessoa p where p.nomeMae LIKE :termo " + "and p.tenant_id = :tenantId "
							+ "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).setFirstResult(first).setMaxResults(pageSize).getResultList();
		} */else if (codigo == 4) { // filtroEndereco
			log.debug("filtro = " + codigo);
			return manager
					.createQuery("Select p From Pessoa p where p.endereco.endereco LIKE :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).setFirstResult(first).setMaxResults(pageSize).getResultList();
		} else if (codigo == 5) { // filtroCodigoDenuncia
			log.debug("filtro = " + codigo);
			return manager
					.createQuery("Select p From Pessoa p where p.familia.denuncia.codigo = :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", Long.valueOf(termo)).setParameter("tenantId", tenantId)
					.setParameter("exc", false).setFirstResult(first).setMaxResults(pageSize).getResultList();
		} /*else if (codigo == 6) { // filtroFisico
			log.debug("filtro = " + codigo);
			return manager
					.createQuery("Select p From Pessoa p where p.familia.denuncia.denunciaFisica LIKE :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).setFirstResult(first).setMaxResults(pageSize).getResultList();
		} */ else if (codigo == 7) { // filtroCodigoPessoa
			log.debug("filtro = " + codigo);
			return manager
					.createQuery("Select p From Pessoa p where p.codigo = :termo " + "and p.tenant_id = :tenantId "
							+ "and p.excluida = :exc")
					.setParameter("termo", Long.valueOf(termo)).setParameter("tenantId", tenantId)
					.setParameter("exc", false).setFirstResult(first).setMaxResults(pageSize).getResultList();
		} else {
			return manager.createNamedQuery("Pessoa.buscarTodos").setParameter("exc", false)
					.setParameter("tenantId", tenantId).setFirstResult(first).setMaxResults(pageSize).getResultList();
		}	
	}
	
	/* Filtros RelatorioPaisPessoa paginação*/
	
	@SuppressWarnings("unchecked")
	public List<Pessoa> buscarComPaginacao(int first, int pageSize, String termo, int codigo, Unidade unidade, Pais pais, Long tenantId) {	
		
		if(codigo == 1) {  // nome
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.nome LIKE :termo "
					+ "and p.familia.denuncia.unidade = :unidade "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", "%" + termo.toUpperCase() + "%")
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		}
		/* else if(codigo == 3) {  // filtroMae
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.nomeMae LIKE :termo "
					+ "and p.familia.denuncia.unidade = :unidade "
					+ "and p.paisOrigem = :pais "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", "%" + termo.toUpperCase() + "%")
				.setParameter("unidade", unidade)
				.setParameter("pais", pais)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		} */
		else if(codigo == 5) {  // filtroCodigoDenuncia
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.familia.denuncia.codigo = :termo "
					+ "and p.familia.denuncia.unidade = :unidade "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", Long.valueOf(termo))
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		}
		/*else if(codigo == 6) {  // filtroFisico
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.familia.denuncia.denunciaFisica LIKE :termo "
					+ "and p.familia.denuncia.unidade = :unidade "
					+ "and p.paisOrigem = :pais "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", "%" + termo.toUpperCase() + "%")
				.setParameter("unidade", unidade)
				.setParameter("pais", pais)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		} */
		else if(codigo == 7) {  // filtroCodigoPessoa
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.codigo = :termo "
					+ "and p.familia.denuncia.unidade = :unidade "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", Long.valueOf(termo))
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		}
		else {
			return manager.createQuery("Select p from Pessoa p where p.excluida = :exc "
				+ "and p.familia.denuncia.unidade = :unidade "
				+ "and p.tenant_id = :tenantId")
				.setParameter("exc", false)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<Pessoa> buscarComPaginacao(int first, int pageSize, String termo, int codigo, Pais pais, Long tenantId) {	
		
		if(codigo == 1) {  // nome
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.nome LIKE :termo "
					+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", "%" + termo.toUpperCase() + "%")
				.setParameter("pais", pais)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		}
		/* else if(codigo == 2) {  // filtroNomeSocial
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.nomeSocial LIKE :termo "
					+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
					+ "and p.paisOrigem = :pais "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")
				.setParameter("termo", "%" + termo.toUpperCase() + "%")
				.setParameter("pais", pais)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		} */
		/* else if(codigo == 3) {  // filtroMae
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.nomeMae LIKE :termo "
					+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
					+ "and p.paisOrigem = :pais "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", "%" + termo.toUpperCase() + "%")
				.setParameter("pais", pais)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		} */
		else if(codigo == 4) {  // filtroEndereco
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.endereco.endereco LIKE :termo "
					+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", "%" + termo.toUpperCase() + "%")
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		}
		else if(codigo == 5) {  // filtroCodigoDenuncia
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.familia.denuncia.codigo = :termo "
					+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", Long.valueOf(termo))
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		}
		/* else if(codigo == 6) {  // filtroFisico
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.familia.denuncia.denunciaFisica LIKE :termo "
					+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
					+ "and p.paisOrigem = :pais "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", "%" + termo.toUpperCase() + "%")
				.setParameter("pais", pais)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		} */
		else if(codigo == 7) {  // filtroCodigoPessoa
			log.debug("filtro = " + codigo);
			return manager.createQuery("Select p From Pessoa p where p.codigo = :termo "
					+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
					+ "and p.tenant_id = :tenantId "
					+ "and p.excluida = :exc")			
				.setParameter("termo", Long.valueOf(termo))
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.setFirstResult(first).setMaxResults(pageSize).getResultList();
		}
		else {
			return manager.createQuery("Select p from Pessoa p where p.excluida = :exc "
					+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
					+ "and p.tenant_id = :tenantId")
					.setParameter("exc", false)
					.setParameter("tenantId", tenantId)
					.setFirstResult(first).setMaxResults(pageSize).getResultList();
		}		
	}
	
	//Quantidade de pessoas
	
	public Long encontrarQdePessoas(String termo, int codigo, Long tenantId) {

		if (codigo == 1) { // nome
			log.debug("filtro = " + codigo);
			return (Long) manager
					.createQuery("Select count(p) From Pessoa p where p.nome LIKE :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).getSingleResult();
		} /* else if (codigo == 2) { // filtroNomeSocial
			log.debug("filtro = " + codigo);
			return (Long) manager
					.createQuery("Select count(p) From Pessoa p where p.nomeSocial LIKE :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).getSingleResult();
		} */ /* else if (codigo == 3) { // filtroMae
			log.debug("filtro = " + codigo);
			return (Long) manager
					.createQuery("Select count(p) From Pessoa p where p.nomeMae LIKE :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).getSingleResult();
		} */ else if (codigo == 4) { // filtroEndereco
			log.debug("filtro = " + codigo);
			return (Long) manager
					.createQuery("Select count(p) From Pessoa p where p.endereco.endereco LIKE :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).getSingleResult();
		} else if (codigo == 5) { // filtroCodigoDenuncia
			log.debug("filtro = " + codigo);
			return (Long) manager
					.createQuery("Select count(p) From Pessoa p where p.familia.denuncia.codigo = :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", Long.valueOf(termo)).setParameter("tenantId", tenantId)
					.setParameter("exc", false).getSingleResult();
		} /*else if (codigo == 6) { // filtroFisico
			log.debug("filtro = " + codigo);
			return (Long) manager
					.createQuery("Select count(p) From Pessoa p where p.familia.denuncia.denunciaFisica LIKE :termo "
							+ "and p.tenant_id = :tenantId " + "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%").setParameter("tenantId", tenantId)
					.setParameter("exc", false).getSingleResult();
		} */ else {
			return (Long) manager
					.createQuery(
							"Select count(p) From Pessoa p where p.excluida = :exc " + "and p.tenant_id = :tenantId ")
					.setParameter("exc", false).setParameter("tenantId", tenantId).getSingleResult();
		}
	}
	
	//Quantidade de pessoas RelatorioPessoaPais
	
		public Long encontrarQdePessoas(String termo, int codigo, Unidade unidade, Pais pais, Long tenantId) {	
			
			if(codigo == 1) {  // nome
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.nome LIKE :termo "
						+ "and p.familia.denuncia.unidade = :unidade "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("unidade", unidade)
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			}
			/* else if(codigo == 2) {  // filtroNomeSocial
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.nomeSocial LIKE :termo "
						+ "and p.familia.denuncia.unidade = :unidade "
						+ "and p.paisOrigem = :pais "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("unidade", unidade)
					.setParameter("pais", pais)
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			} */
			/* else if(codigo == 3) {  // filtroMae
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.nomeMae LIKE :termo "
						+ "and p.familia.denuncia.unidade = :unidade "
						+ "and p.paisOrigem = :pais "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("unidade", unidade)
					.setParameter("pais", pais)
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			} */
			else if(codigo == 4) {  // filtroEndereco
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.endereco.endereco LIKE :termo "
						+ "and p.familia.denuncia.unidade = :unidade "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("unidade", unidade)
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			}
			else if(codigo == 5) {  // filtroCodigoDenuncia
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.familia.denuncia.codigo = :termo "
						+ "and p.familia.denuncia.unidade = :unidade "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", Long.valueOf(termo))
					.setParameter("unidade", unidade)
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			}
			/* else if(codigo == 6) {  // filtroFisico
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.familia.denuncia.denunciaFisica LIKE :termo "
						+ "and p.familia.denuncia.unidade = :unidade "
						+ "and p.paisOrigem = :pais "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("unidade", unidade)
					.setParameter("pais", pais)
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			} */
			else {
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.excluida = :exc "
						+ "and p.familia.denuncia.unidade = :unidade "
						+ "and p.tenant_id = :tenantId ")					
					.setParameter("exc", false)
					.setParameter("unidade", unidade)
					.setParameter("tenantId", tenantId)
					.getSingleResult();
			}		
		}
	
		public Long encontrarQdePessoas(String termo, int codigo, Pais pais, Long tenantId) {	
			
			if(codigo == 1) {  // nome
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.nome LIKE :termo "
						+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			}
			/* else if(codigo == 2) {  // filtroNomeSocial
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.nomeSocial LIKE :termo "
						+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
						+ "and p.paisOrigem = :pais "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("pais", pais)
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			} */
			/* else if(codigo == 3) {  // filtroMae
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.nomeMae LIKE :termo "
						+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
						+ "and p.paisOrigem = :pais "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("pais", pais)
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			} */
			else if(codigo == 4) {  // filtroEndereco
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.endereco.endereco LIKE :termo "
						+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			}
			else if(codigo == 5) {  // filtroCodigoDenuncia
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.familia.denuncia.codigo = :termo "
						+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", Long.valueOf(termo))
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			}
			/* else if(codigo == 6) {  // filtroFisico
				log.debug("filtro = " + codigo);
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.familia.denuncia.denunciaFisica LIKE :termo "
						+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
						+ "and p.paisOrigem = :pais "
						+ "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc")			
					.setParameter("termo", "%" + termo.toUpperCase() + "%")
					.setParameter("pais", pais)
					.setParameter("tenantId", tenantId)
					.setParameter("exc", false)
					.getSingleResult();
			} */
			else {
				return (Long) manager.createQuery("Select count(p) From Pessoa p where p.excluida = :exc "
						+ "and p.familia.denuncia.unidade.tipo not in ('SASC') "
						+ "and p.tenant_id = :tenantId ")					
					.setParameter("exc", false)
					.setParameter("tenantId", tenantId)
					.getSingleResult();
			}		
		}

	/* Filtros PesquisaPessoa paginação */

	/*
	 * RelatorioProgSocial
	 */
	public List<Pessoa> pesquisarPessoaPorProgSocial(ProgramaSocial programa, Unidade unidade, Long tenantId) {
		return manager
				.createQuery("Select p From Pessoa p " + "where p.formaIngresso.programaSocial = :programa "
						+ "and p.familia.denuncia.unidade = :unidade " + "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc", Pessoa.class)
				.setParameter("unidade", unidade).setParameter("tenantId", tenantId).setParameter("programa", programa)
				.setParameter("exc", false).getResultList();
	}

	public List<Pessoa> pesquisarPessoaPorProgSocial(ProgramaSocial programa, Long tenantId) {
		return manager
				.createQuery(
						"Select p From Pessoa p " + "where p.formaIngresso.programaSocial = :programa "
								+ "and p.tenant_id = :tenantId "
								+ "and p.familia.denuncia.unidade.tipo not in ('SASC') " + "and p.excluida = :exc",
						Pessoa.class)
				.setParameter("programa", programa).setParameter("tenantId", tenantId).setParameter("exc", false)
				.getResultList();
	}

	/*
	 * RelatorioPessoasPais
	 */
	public List<Pessoa> pesquisarPessoasPais(Pais pais, Unidade unidade, Long tenantId) {
		return manager.createQuery("Select p From Pessoa p "
				+ "where p.paisOrigem = :pais "
				+ "and p.familia.denuncia.unidade = :unidade "
				+ "and p.tenant_id = :tenantId "
				+ "and p.excluida = :exc", Pessoa.class)		
				.setParameter("pais", pais)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("exc", false)
				.getResultList();
	}

	public List<Pessoa> pesquisarPessoasPais(Pais pais, Long tenantId) {
		return manager
				.createQuery("Select p From Pessoa p " + "where p.paisOrigem = :pais "
						+ "and p.familia.denuncia.unidade.tipo not in ('SASC') " + "and p.tenant_id = :tenantId "
						+ "and p.excluida = :exc", Pessoa.class)
				.setParameter("pais", pais).setParameter("tenantId", tenantId).setParameter("exc", false)
				.getResultList();
	}

	/*
	 * Buscar todos os Países
	 * 
	 */
	public Pais buscarPais(Long codigo) {
		return manager.find(Pais.class, codigo);
	}

	@SuppressWarnings("unchecked")
	public List<Pais> buscarTodosPaises() {
		return manager.createNamedQuery("Pais.buscarTodosPaises").getResultList();
	}
	
	
	
	
	
	@Transactional
	public void migrarDadosPessoais(Pessoa pessoa, Pessoa pessoaNova) {
		
		log.info("Migração de Dados Pessoais de " + pessoa.getNome() + " para " + pessoaNova.getNome());
		
		/*  tabelas que afetam dados pessoais das pessoas
		 
			#  ManyToMany (pessoas)
			SELECT * FROM svsa.pessoaAcao where codigo_pessoa_acao = 15715;			
					
			#  ManyToOne (pessoa)
			SELECT * FROM svsa.Acao where codigo_pessoa = 15715; 
			SELECT * FROM svsa.ListaAtendimento where codigo_pessoa = 15715     ;
			
			SELECT * FROM svsa.oficio where codigo_pessoa = 15715   ;
			SELECT * FROM svsa.oficioemitido where codigo_pessoa = 15715   ;
			
		 */

		Query query;
		
		/* Ação */
		query = manager.createNativeQuery( 
				"UPDATE Acao "
				+ "SET codigo_pessoa = :pessoaNova "
				+ "WHERE codigo_pessoa = :pessoa" );
		query.setParameter( "pessoa", pessoa.getCodigo() );
		query.setParameter( "pessoaNova", pessoaNova.getCodigo() );
        query.executeUpdate();
        
		query = manager.createNativeQuery( 
				"UPDATE PessoaAcao "
				+ "SET codigo_pessoa_acao = :pessoaNova "
				+ "WHERE codigo_pessoa_acao = :pessoa" );
		query.setParameter( "pessoa", pessoa.getCodigo() );
		query.setParameter( "pessoaNova", pessoaNova.getCodigo() );
        query.executeUpdate();
        
       
        /* ListaAtendimento */
		query = manager.createNativeQuery( 
				"UPDATE Atendimento "
				+ "SET codigo_pessoa = :pessoaNova "
				+ "WHERE codigo_pessoa = :pessoa" );
		query.setParameter( "pessoa", pessoa.getCodigo() );
		query.setParameter( "pessoaNova", pessoaNova.getCodigo() );
        query.executeUpdate();
        
        /* Encaminhamento (externo) */
		query = manager.createNativeQuery( 
				"UPDATE Encaminhamento "
				+ "SET codigo_pessoa = :pessoaNova "
				+ "WHERE codigo_pessoa = :pessoa" );
		query.setParameter( "pessoa", pessoa.getCodigo() );
		query.setParameter( "pessoaNova", pessoaNova.getCodigo() );
        query.executeUpdate();
        
        /* Oficio */
		query = manager.createNativeQuery( 
				"UPDATE Oficio "
				+ "SET codigo_pessoa = :pessoaNova "
				+ "WHERE codigo_pessoa = :pessoa" );
		query.setParameter( "pessoa", pessoa.getCodigo() );
		query.setParameter( "pessoaNova", pessoaNova.getCodigo() );
        query.executeUpdate();
        
        /* OficioEmitido */
		query = manager.createNativeQuery( 
				"UPDATE OficioEmitido "
				+ "SET codigo_pessoa = :pessoaNova "
				+ "WHERE codigo_pessoa = :pessoa" );
		query.setParameter( "pessoa", pessoa.getCodigo() );
		query.setParameter( "pessoaNova", pessoaNova.getCodigo() );
        query.executeUpdate();
        
        log.info("Migração concluida com sucesso.");
	}


	// para fins de testes unitários
	public void setManager(EntityManager manager) {
		this.manager = manager;
	}

	
	
}
