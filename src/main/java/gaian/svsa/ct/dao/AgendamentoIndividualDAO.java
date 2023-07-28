package gaian.svsa.ct.dao;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import gaian.svsa.ct.modelo.Acao;
import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.CodigoAuxiliarAtendimento;
import gaian.svsa.ct.modelo.enums.StatusAtendimento;
import gaian.svsa.ct.modelo.to.AtendimentoDTO;
import gaian.svsa.ct.modelo.to.DatasIniFimTO;
import gaian.svsa.ct.util.DateUtils;
import gaian.svsa.ct.util.NegocioException;
import gaian.svsa.ct.util.jpa.Transactional;

/**
 * @author murakamiadmin
 *
 */
public class AgendamentoIndividualDAO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger(AgendamentoIndividualDAO.class);	
	
	
	@Inject
	private EntityManager manager;	
	
	@Transactional
	public void salvar(Atendimento lista) throws NegocioException {				
		
		try {
			// todo agendamento é uma ação.
			if(lista.getCodigo() == null) {
				manager.merge(gerarAcaoAgendamento(lista, false, null));
			}
			else {
				// verifica se é reagendamento
				Atendimento atend = buscarPeloCodigo(lista.getCodigo());
				
				// gera uma ação de reagendamento
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		        String dataAntiga = dateFormat.format(atend.getDataAgendamento());
		        String dataNova = dateFormat.format(lista.getDataAgendamento());
		        
		        if(!dataNova.equals(dataAntiga)) {				
					manager.merge(gerarAcaoAgendamento(lista, true, atend.getDataAgendamento()));
				}			
			}
			manager.merge(lista);
			
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
	public void salvarRecepcao(Atendimento lista) throws NegocioException {
		try {
			manager.merge(lista);
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
	public void salvarAlterar(Atendimento lista) throws NegocioException {	
		try {
			manager.merge(lista);
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
	public void salvarEncerramento(Atendimento lista) throws NegocioException {
		try {
			manager.merge(lista);		
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
	public Atendimento autoSaveVisita(Atendimento lista) throws NegocioException {
		try {
			return manager.merge(lista);
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
	
	private Acao gerarAcaoAgendamento(Atendimento lista, boolean reagendamento, Date novaData) {		
		
		SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
		Acao a = new Acao();
		a.setData(new Date());
		if(reagendamento) {
			a.setDescricao("Reagendado atendimento individualizado de: " + out.format(novaData) + " para: "+ out.format(lista.getDataAgendamento()) );
		}
		else {
			a.setDescricao("Agendado atendimento individualizado para "+ out.format(lista.getDataAgendamento()) );
		}		
		a.setPessoa(lista.getPessoa());
		a.setAgendador(lista.getAgendador());
		if(lista.getTecnico() != null)
			a.setTecnico(lista.getTecnico());
		a.setUnidade(lista.getUnidade());
		a.setTenant_id(lista.getTenant_id());
		a.setStatusAtendimento(StatusAtendimento.ATENDIDO);
		
		return a;		
	}	
		
	@Transactional
	public void excluir(Atendimento lista) throws NegocioException {
		lista = buscarPeloCodigo(lista.getCodigo());
		try {
			manager.remove(lista);
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
	
	
	
	/*
	 * Buscas
	 */
	
	public Atendimento buscarPeloCodigo(Long codigo) {
		return manager.find(Atendimento.class, codigo);
	}
	
	@SuppressWarnings("unchecked")
	public List<Atendimento> buscarTodos(Long tenantId) {
		return manager.createNamedQuery("Atendimento.buscarTodos")
				.setParameter("tenantId", tenantId)
				.getResultList();
	}
	
	public Atendimento ultimoAtendimento(Pessoa pessoa, Long tenantId) {
		
		String jpql = "SELECT a FROM Atendimento a where a.statusAtendimento = :status "
				+ "and a.pessoa.codigo = :pessoa "
				+ "and a.tenant_id = :tenantId "
				+ "ORDER BY a.dataAtendimento DESC ";
		
		TypedQuery<Atendimento> query = manager.createQuery(jpql, Atendimento.class);		
		query.setParameter("pessoa", pessoa.getCodigo());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", StatusAtendimento.ATENDIDO);
		query.setMaxResults(1);
		
		try {			
			return query.getSingleResult();
		}catch(Exception e) {
			return null;
		}		
	}
	
	// para relatorioAcompPAIF
	public Atendimento ultimoAtendimento(Long pessoa, Long tenantId) {
		
		String jpql = "SELECT a FROM Atendimento a where a.statusAtendimento = :status "
				+ "and a.pessoa.codigo = :pessoa "
				+ "and a.tenant_id = :tenantId "
				+ "ORDER BY a.dataAtendimento DESC ";
		
		TypedQuery<Atendimento> query = manager.createQuery(jpql, Atendimento.class);		
		query.setParameter("pessoa", pessoa);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", StatusAtendimento.ATENDIDO);
		query.setMaxResults(1);
		
		try {			
			return query.getSingleResult();
		}catch(Exception e) {
			return null;
		}		
	}

	
	/*
	 * ATENDIMENTOS AGENDADOS
	 */
		
	public List<Atendimento> buscarAtendimentosRole(Usuario usuarioLogado, Long tenantId) {		
		return manager.createNamedQuery("Atendimento.buscarAtendimentosRole", Atendimento.class)
							.setParameter("unidade", usuarioLogado.getUnidade())
							.setParameter("tenantId", tenantId)
							.setParameter("role", usuarioLogado.getRole())
							.setParameter("status", StatusAtendimento.AGENDADO)
							.getResultList();	
	}
	
	public List<Atendimento> buscarAtendimentosTecnicos(Unidade unidade, Long tenantId) {
		return manager.createNamedQuery("Atendimento.buscarAtendimentosTecnicos", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.AGENDADO)
				.getResultList();	
	}
	
	public List<Atendimento> buscarAtendimentosAgendados(Unidade unidade, Long tenantId) {			
		return manager.createNamedQuery("Atendimento.buscarAtendimentosAgendados", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.AGENDADO)
				.getResultList();	
	}
	public List<Atendimento> buscarAtendimentosAgendados(Unidade unidade, Date ini, Long tenantId) {			
		return manager.createNamedQuery("Atendimento.buscarAtendAgendados", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("ini", ini, TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDays(ini, 31), TemporalType.TIMESTAMP)
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.AGENDADO)
				.getResultList();	
	}
	
	public List<Atendimento> buscarAgendaUsuario(Usuario usuario, Long tenantId) {			
		return manager.createNamedQuery("Atendimento.buscarAgendaUsuario", Atendimento.class)
				.setParameter("tecnico", usuario)
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.AGENDADO)
				.getResultList();	
	}
	
	public Long encontrarQuantidadeAgendados(Unidade unidade, Long tenantId) {
		return manager.createQuery("select count(a) from Atendimento a "
				+ "where a.statusAtendimento = :status "
				+ "and a.tenant_id = :tenantId "
				+ "and a.unidade = :unidade", Long.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.AGENDADO)
				.getSingleResult();
	}

	public Long buscarPorPessoa(PessoaReferencia pessoaReferencia, Long tenantId) {
		return manager.createQuery("select count(a) from Atendimento a "
				+ "where a.statusAtendimento = :status "
				+ "and a.tenant_id = :tenantId "
				+ "and a.pessoa.codigo = :codigo", Long.class)
				.setParameter("codigo", pessoaReferencia.getCodigo())
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.AGENDADO)
				.getSingleResult();
		
	}
	
	public List<Atendimento> buscarAtendimentosPendentes(Unidade unidade, Long tenantId) {
		return manager.createNamedQuery("Atendimento.buscarAtendimentosPendentes", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.EM_ATENDIMENTO)
				.getResultList();
	}
	
	
	
	
	/*
	 * ATENDIMENTOS ATENDIDOS
	 */
	
	
	public List<Atendimento> buscarAtendimentosRecepcao(Usuario usuario, Date ini, Date fim, Long tenantId) {		
		return manager.createNamedQuery("Atendimento.buscarAtendimentosRecepcao", Atendimento.class)
				.setParameter("unidade", usuario.getUnidade())
				.setParameter("tenantId", tenantId)
				.setParameter("ini", ini, TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(fim), TemporalType.TIMESTAMP)
				.setParameter("codigoAux", CodigoAuxiliarAtendimento.ATENDIMENTO_RECEPCAO)
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.getResultList();	
	}
	
	
	/* ########################################
	 * Consulta atendimentos individualizados usando DTO Projection JPQL (Helper)
	 * #########################################
	 */
	public List<AtendimentoDTO> buscarResumoAtendimentosDTO(Pessoa pessoa, Long tenantId) {		
		/*
		 SELECT a.dataAtendimento, 
			a.resumoAtendimento, 
			c.nome AS nomeTecnico, 
			d.nome AS nomeUnidade,
			b.nome AS nomePessoa,
			a.codigoAuxiliar
		FROM svsa.listaatendimento a
			INNER JOIN svsa.pessoa b ON b.codigo = a.codigo_pessoa
			INNER JOIN svsa.usuario c ON c.codigo = a.codigo_tecnico
			INNER JOIN svsa.unidade d ON d.codigo = a.codigo_unidade
		WHERE a.codigo_pessoa = 31722 
			and a.tenant_id = 1
            and (a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO') or a.codigoAuxiliar is null)
			and (a.statusAtendimento = "ATENDIDO" or a.statusAtendimento = "FALTOU")
		 */
		List<AtendimentoDTO> lista = manager.createQuery("SELECT new gaian.svsa.ct.modelo.to.AtendimentoDTO( "
				+ "a.dataAtendimento, "
				+ "a.resumoAtendimento, "
				+ "c.nome, "
				+ "d.nome, "
				+ "b.nome, "
				+ "a.codigoAuxiliar) "
			+ "FROM Atendimento a "
				+ "INNER JOIN Pessoa b ON b.codigo = a.pessoa.codigo "
				+ "INNER JOIN Usuario c ON c.codigo = a.tecnico.codigo "
				+ "INNER JOIN Unidade d ON d.codigo = a.unidade.codigo "
			+ "WHERE a.pessoa.codigo = :codigo_pessoa "
			 	+ "and a.tenant_id = :tenantId "
				+ "and (a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO') or a.codigoAuxiliar is null)"
				+ "and (a.statusAtendimento = :status or a.statusAtendimento = :falta)", AtendimentoDTO.class)
		.setParameter("codigo_pessoa", pessoa.getCodigo())
		.setParameter("tenantId", tenantId)
		.setParameter("status", StatusAtendimento.ATENDIDO)
		.setParameter("falta", StatusAtendimento.FALTOU)
		.getResultList();

		return lista;
	}
	
	
	
	
	/*
	 * RelatorioAtendimentoFamilia
	 */
	
	public List<Atendimento> buscarAtendimentoFamilia(Unidade unidade, Denuncia denuncia, Long tenantId) {
		return manager.createNamedQuery("Atendimento.buscarAtendimentoFamilia", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("denuncia", denuncia)
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.getResultList();	
	}

	
	
	
	
	
	
	
	
	/*
	 * RelatorioAtendimentos
	 */
	
	
	
	/*
	 * Filtros para atender relatorio de atendimentos lazy
	 * 
	 * 
	 */	
	// filtros
	public List<Atendimento> buscarComPaginacao(int first, int pageSize, Unidade unidade, DatasIniFimTO datasTO, String filtro, int opcao, Long tenantId) {
		
		List<Atendimento> lista = new ArrayList<Atendimento>();
		
		if(opcao == 1) {  // codigo pessoa
			lista = manager.createQuery("select a from Atendimento a "
					+ " INNER JOIN Pessoa pes ON a.pessoa = pes "
					+ " INNER JOIN Familia fam ON pes.familia = fam "
					+ " INNER JOIN Prontuario pro ON fam.prontuario = pro "
					+ " INNER JOIN Unidade uni ON pro.unidade = uni "
					+ "where a.statusAtendimento = :status "
					+ " and uni = :unidade "
					+ " and a.tenant_id = :tenantId "
					+ " and pes.codigo = :filtro "
					+ " and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO')"
					+ " and a.dataAtendimento between :ini and :fim "
					+ " order by a.dataAtendimento", Atendimento.class)
				.setParameter("ini", datasTO.getIni(), TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(datasTO.getFim()), TemporalType.TIMESTAMP)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("filtro", Long.valueOf(filtro))
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.setFirstResult(first)
				.setMaxResults(pageSize)
				.getResultList();				
		} else if(opcao == 2) {  // nome pessoa
			lista = manager.createQuery("select a from Atendimento a "
					+ " INNER JOIN Pessoa pes ON a.pessoa = pes "
					+ " INNER JOIN Familia fam ON pes.familia = fam "
					+ " INNER JOIN Prontuario pro ON fam.prontuario = pro "
					+ " INNER JOIN Unidade uni ON pro.unidade = uni "
					+ "where a.statusAtendimento = :status "
					+ " and uni = :unidade "
					+ " and a.tenant_id = :tenantId "
					+ " and pes.nome LIKE :filtro "
					+ " and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO')"
					+ " and a.dataAtendimento between :ini and :fim "
					+ " order by a.dataAtendimento", Atendimento.class)
				.setParameter("ini", datasTO.getIni(), TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(datasTO.getFim()), TemporalType.TIMESTAMP)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("filtro", filtro.toUpperCase() + "%")
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.setFirstResult(first)
				.setMaxResults(pageSize)
				.getResultList();				
		} else if(opcao == 3) {  // nome tecnico
			lista = manager.createQuery("select a from Atendimento a "
					+ " INNER JOIN Pessoa pes ON a.pessoa = pes "
					+ " INNER JOIN Familia fam ON pes.familia = fam "
					+ " INNER JOIN Prontuario pro ON fam.prontuario = pro "
					+ " INNER JOIN Unidade uni ON pro.unidade = uni "
					+ "where a.statusAtendimento = :status "
					+ " and uni = :unidade "
					+ " and a.tenant_id = :tenantId "
					+ " and a.tecnico.nome LIKE :filtro "
					+ " and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO')"
					+ " and a.dataAtendimento between :ini and :fim "
					+ " order by a.dataAtendimento", Atendimento.class)
				.setParameter("ini", datasTO.getIni(), TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(datasTO.getFim()), TemporalType.TIMESTAMP)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("filtro", filtro.toUpperCase() + "%")
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.setFirstResult(first)
				.setMaxResults(pageSize)
				.getResultList();				
		}		
		return lista;		
	}
	// quantidade total
	public Long encontrarQde(Unidade unidade, DatasIniFimTO datasTO, String filtro, int opcao, Long tenantId) {	
		
		Long qde = 0L;
		
		if(opcao == 1) {  // codigo pessoa			
			qde = manager.createQuery("select count(a) from Atendimento a "
				+ " INNER JOIN Pessoa pes ON a.pessoa = pes "
				+ " INNER JOIN Familia fam ON pes.familia = fam "
				+ " INNER JOIN Prontuario pro ON fam.prontuario = pro "
				+ " INNER JOIN Unidade uni ON pro.unidade = uni "
				+ "where a.statusAtendimento = :status "
				+ " and uni = :unidade "
				+ " and a.tenant_id = :tenantId "
				+ " and pes.codigo = :filtro "
				+ " and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO')"
				+ " and a.dataAtendimento between :ini and :fim "
				+ " order by a.dataAtendimento", Long.class)
			.setParameter("ini", datasTO.getIni(), TemporalType.TIMESTAMP)
			.setParameter("fim", DateUtils.plusDay(datasTO.getFim()), TemporalType.TIMESTAMP)
			.setParameter("unidade", unidade)
			.setParameter("tenantId", tenantId)
			.setParameter("filtro", Long.valueOf(filtro))
			.setParameter("status", StatusAtendimento.ATENDIDO)
			.getSingleResult();
		} else if(opcao == 2) {  // nome pessoa			
			qde = manager.createQuery("select count(a) from Atendimento a "
					+ " INNER JOIN Pessoa pes ON a.pessoa = pes "
					+ " INNER JOIN Familia fam ON pes.familia = fam "
					+ " INNER JOIN Prontuario pro ON fam.prontuario = pro "
					+ " INNER JOIN Unidade uni ON pro.unidade = uni "
					+ "where a.statusAtendimento = :status "
					+ " and uni = :unidade "
					+ " and a.tenant_id = :tenantId "
					+ " and pes.nome LIKE :filtro "
					+ " and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO')"
					+ " and a.dataAtendimento between :ini and :fim "
					+ " order by a.dataAtendimento", Long.class)
				.setParameter("ini", datasTO.getIni(), TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(datasTO.getFim()), TemporalType.TIMESTAMP)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("filtro", filtro.toUpperCase() + "%")
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.getSingleResult();
		} else if(opcao == 3) {  // nome tecnico			
			qde = manager.createQuery("select count(a) from Atendimento a "
					+ " INNER JOIN Pessoa pes ON a.pessoa = pes "
					+ " INNER JOIN Familia fam ON pes.familia = fam "
					+ " INNER JOIN Prontuario pro ON fam.prontuario = pro "
					+ " INNER JOIN Unidade uni ON pro.unidade = uni "
					+ "where a.statusAtendimento = :status "
					+ " and uni = :unidade "
					+ " and a.tenant_id = :tenantId "
					+ " and a.tecnico.nome LIKE :filtro "
					+ " and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO')"
					+ " and a.dataAtendimento between :ini and :fim "
					+ " order by a.dataAtendimento", Long.class)
				.setParameter("ini", datasTO.getIni(), TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(datasTO.getFim()), TemporalType.TIMESTAMP)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("filtro", filtro.toUpperCase() + "%")
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.getSingleResult();
		}
		
		return qde;	
	}	
	
	// sem filtro 
	//SELECT * FROM svsa_salto.ListaAtendimento where codigo_unidade=1 and statusAtendimento = 'ATENDIDO' order by dataAtendimento;	
	public List<Atendimento> buscarComPaginacao(int first, int pageSize, Unidade unidade, DatasIniFimTO datasTO, Long tenantId) {
		List<Atendimento> lista = manager.createQuery("select a from Atendimento a "				
				+ "where a.statusAtendimento = :status "
				+ " and a.unidade = :unidade "
				+ " and a.tenant_id = :tenantId "
				+ " and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO')"
				+ " and a.dataAtendimento between :ini and :fim "
				+ " order by a.dataAtendimento", Atendimento.class)
			.setParameter("ini", datasTO.getIni(), TemporalType.TIMESTAMP)
			.setParameter("fim", DateUtils.plusDay(datasTO.getFim()), TemporalType.TIMESTAMP)
			.setParameter("unidade", unidade)
			.setParameter("tenantId", tenantId)
			.setParameter("status", StatusAtendimento.ATENDIDO)
			.setFirstResult(first)
			.setMaxResults(pageSize)
			.getResultList();			
		return lista;
	}
	public Long encontrarQde(Unidade unidade, DatasIniFimTO datasTO, Long tenantId) {		
		Long qde = manager.createQuery("select COUNT(a) from Atendimento a "				
				+ "where a.statusAtendimento = :status "
				+ " and a.unidade = :unidade "
				+ " and a.tenant_id = :tenantId "
				+ " and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO')"
				+ " and a.dataAtendimento between :ini and :fim "
				+ " order by a.dataAtendimento", Long.class)
			.setParameter("ini", datasTO.getIni(), TemporalType.TIMESTAMP)
			.setParameter("fim", DateUtils.plusDay(datasTO.getFim()), TemporalType.TIMESTAMP)
			.setParameter("unidade", unidade)
			.setParameter("tenantId", tenantId)
			.setParameter("status", StatusAtendimento.ATENDIDO)
			.getSingleResult();			
		return qde;
	}
	
	// grafico do relatorio atendimentos
	public List<Atendimento> buscarAtendimentosCodAuxGrafico(Unidade unidade, Long tenantId) {			
		return manager.createNamedQuery("Atendimento.buscarAtendimentosCodAux", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.getResultList();	
	}	
	public List<Atendimento> buscarAtendimentosCodAuxGrafico(Unidade unidade, Date ini, Date fim, Long tenantId) {
		return manager.createNamedQuery("Atendimento.buscarAtendimentosCodAuxGrafico", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("ini", ini, TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(fim), TemporalType.TIMESTAMP)
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.getResultList();
	}
	/*
	 * 
	 * 
	 * 
	 * Fim filtros para atender relatorio de atendimentos lazy
	 */
	
	
	
	
	
	
	// Helper
	public Long buscarQdeAtendimentoCodAux(CodigoAuxiliarAtendimento c, Unidade unidade, Date ini, Date fim, Long tenantId){
				
		log.debug("antes: " + fim + "...depois: " + DateUtils.plusDay(fim));
		
		Query q = manager.createQuery("Select count(a.codigo) from Atendimento a  "
				+ "where a.statusAtendimento = :status "
				+ "and a.unidade = :unidade "
				+ "and a.tenant_id = :tenantId "
				+ "and a.dataAtendimento between :ini and :fim "
				+ "and a.codigoAuxiliar = :codAux");
		q.setParameter("unidade", unidade);
		q.setParameter("tenantId", tenantId);
		q.setParameter("ini", ini, TemporalType.TIMESTAMP);
		q.setParameter("fim", DateUtils.plusDay(fim), TemporalType.TIMESTAMP);
		q.setParameter("codAux", c);
		q.setParameter("status", StatusAtendimento.ATENDIDO);
		Long qde = (Long) q.getSingleResult();
		
		return qde;
	}
	
	public Long buscarQdeAtendimentoCodAux(CodigoAuxiliarAtendimento c, Unidade unidade, Long tenantId) {
		Query q = manager.createQuery("Select count(a.codigo) from Atendimento a  "
				+ "where a.statusAtendimento = :status "
				+ "and a.unidade = :unidade "
				+ "and a.tenant_id = :tenantId "
				+ "and a.codigoAuxiliar = :codAux");
		q.setParameter("unidade", unidade);
		q.setParameter("tenantId", tenantId);
		q.setParameter("codAux", c);
		q.setParameter("status", StatusAtendimento.ATENDIDO);
		Long qde = (Long) q.getSingleResult();
		
		return qde;
	}
	public Long buscarQdeAtendimentoCodAux(CodigoAuxiliarAtendimento c, Long tenantId) {
		Query q = manager.createQuery("Select count(a.codigo) from Atendimento a  "
				+ "where a.statusAtendimento = :status "
				+ "and a.tenant_id = :tenantId "
				+ "and a.codigoAuxiliar = :codAux");
		q.setParameter("codAux", c);
		q.setParameter("tenantId", tenantId);
		q.setParameter("status", StatusAtendimento.ATENDIDO);
		Long qde = (Long) q.getSingleResult();
		
		return qde;
	}
	
	public Long buscarQdeAtendimentoUnidade(Unidade unidade, Long tenantId) {
		
		Query q = manager.createQuery("Select count(a.codigo) from Atendimento a  "
				+ "where a.statusAtendimento = :status "
				+ "and a.tenant_id = :tenantId "
				+ "and unidade = :unidade");		
		q.setParameter("unidade", unidade);
		q.setParameter("tenantId", tenantId);
		q.setParameter("status", StatusAtendimento.ATENDIDO);
		Long qde = (Long) q.getSingleResult();
		
		return qde;
	}	
	
	
	
	/*
	 * Atendimentos CadUnico
	 */
	
	public List<Atendimento> buscarAtendCadUnicoDataPeriodo(Unidade unidade, Date ini, Date fim, Long tenantId) {
		
		return manager.createNamedQuery("Atendimento.buscarAtendCadUnicoDataPeriodo", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("ini", ini, TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(fim), TemporalType.TIMESTAMP)
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.getResultList();
	}
	public List<Atendimento> buscarAtendCadUnicoDataPeriodo2(Unidade unidade, Date ini, Date fim, Long tenantId) {
		
		return manager.createNamedQuery("Atendimento.buscarAtendCadUnicoDataPeriodo2", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("ini", ini, TemporalType.TIMESTAMP)
				.setParameter("fim", DateUtils.plusDay(fim), TemporalType.TIMESTAMP)
				.setParameter("status", StatusAtendimento.ATENDIDO)
				.getResultList();
	}
	public List<Atendimento> buscarAtendidosCadUnico(Unidade unidade, Long tenantId) {			
		return manager.createNamedQuery("Atendimento.buscarAtendidosCadUnico", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.ATENDIDO)				
				.getResultList();	
	}
	public List<Atendimento> buscarAtendidosCadUnico2(Unidade unidade, Long tenantId) {			
		return manager.createNamedQuery("Atendimento.buscarAtendidosCadUnico2", Atendimento.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", StatusAtendimento.ATENDIDO)				
				.getResultList();	
	}
	
	/*
	 * Faltas individualizadas
	 */
	public List<Atendimento> consultaFaltas(Unidade unidade, Pessoa pessoa, Long tenantId) {
		return manager.createNamedQuery("Atendimento.consultaFaltas", Atendimento.class)
			.setParameter("unidade", unidade)
			.setParameter("pessoa", pessoa)
			.setParameter("tenantId", tenantId)
			.setParameter("status", StatusAtendimento.FALTOU)
			.getResultList();	
	}
		
	
	// criado para realização de testes unitários com JIntegrity
	public void setEntityManager(EntityManager manager) {
		this.manager = manager;
	}
	
	public void setManager(EntityManager manager2) {
		this.manager = manager2;
		
	}	
}