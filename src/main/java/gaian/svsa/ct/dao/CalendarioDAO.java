package gaian.svsa.ct.dao;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;

import gaian.svsa.ct.modelo.Calendario;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.StatusAtendimento;
import gaian.svsa.ct.util.DateUtils;
import gaian.svsa.ct.util.NegocioException;
import gaian.svsa.ct.util.jpa.Transactional;

/**
 * @author murakamiadmin
 *
 */
public class CalendarioDAO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(CalendarioDAO.class);
	
	@Inject
	private EntityManager manager;
	

	@Transactional
	public Calendario merge(Calendario calendario) throws NegocioException {
		try {
			return manager.merge(calendario);
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
	public void excluir(Calendario calendario) throws NegocioException {
		calendario = buscarPeloCodigo(calendario.getCodigo());
		try {
			manager.remove(calendario);
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
	
	
	public Calendario buscarPeloCodigo(Long codigo) {
		return manager.find(Calendario.class, codigo);
	}
	
	@SuppressWarnings("unchecked")
	public List<Calendario> buscarTodos(Unidade unidade, Long tenantId) throws ParseException {
		
		LocalDateTime data = LocalDateTime.now();
		data = data.minusYears(1);
		log.info("Data inicio da busca: " + data);
				
		return manager.createNamedQuery("Calendario.buscarTodos")
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("data", data)
				.getResultList();
	}
	
	/*
	 * Verificar se data está ocupada
	 */
	public void verificaDataFeriados(Date data, Unidade unidade, Long tenantId) throws NegocioException {
		/*
		 * SELECT *
				FROM svsa_salto.calendario
				where '2020-11-18 07:00:00' >= startDate 
					AND '2020-11-18 07:00:00' <= endDate;
		 */
		
		//log.info("Data conferir : " + data);
		LocalDateTime dat = DateUtils.asLocalDateTime(data);		
		//log.info("Data conferir : " + dat);
		
		Long qde = manager.createQuery("SELECT count(c) from Calendario c "
				+ "where :data >= c.startDate and :data <= c.endDate "
				+ "and c.unidade = :unidade "
				+ "and c.tenant_id = :tenantId "
				+ "and c.conselheiro is null", Long.class)
		.setParameter("data", dat)
		.setParameter("unidade", unidade)
		.setParameter("tenantId", tenantId)
		.getSingleResult();
		
	
		if(qde > 0) {
			throw new NegocioException("Verifique o calendário de feriados e datas comemorativas, existe restrição para esta data.");
		}		
	}
	/*
	 * Verificar se conselheiro está ocupado (folga ou férias)
	 */
	public void verificaDataConselheiro(Date data, Usuario conselheiro, Long tenantId) throws NegocioException {
		/*
		 * SELECT *
				FROM svsa_salto.calendario
				where '2020-11-18 07:00:00' >= startDate 
					AND '2020-11-18 07:00:00' <= endDate
						AND codigo_conselheiro = 74;
		 */
		
		//log.info("Data conferir : " + data);
		LocalDateTime dat = DateUtils.asLocalDateTime(data);
		//log.info("Data conferir : " + dat);
		
		Long qde = manager.createQuery("SELECT count(c) from Calendario c "
				+ "where :data >= c.startDate and :data <= c.endDate "
				+ "and c.conselheiro = :conselheiro "
				+ "and c.tenant_id = :tenantId ", Long.class)
		.setParameter("data", dat)
		.setParameter("conselheiro", conselheiro)
		.setParameter("tenantId", tenantId)
		.getSingleResult();
		
	
		if(qde > 0) {
			throw new NegocioException("O(a)" + conselheiro.getNome() + " está de folga ou em férias. Verifique no calendário de feriados.");
		}		
	}
	
	/*
	 * Verificar se conselheiro está agendado no mesmo dia e horário
	 */
	public void verificaAgendaConselheiro(Date data, Usuario conselheiro, Long tenantId) throws NegocioException {	
		
		Long qde, qdeFam = 0L;
		
		/* individual */	
		qde = manager.createQuery("SELECT count(a) FROM Atendimento a "				
				+ "WHERE a.dataAgendamento = :data "
					+ "and a.conselheiro = :conselheiro "
					+ "and a.tenant_id = :tenantId "
					+ "and a.statusAtendimento = 'AGENDADO' ", Long.class)
		.setParameter("data", data, TemporalType.TIMESTAMP)
		.setParameter("conselheiro", conselheiro)
		.setParameter("tenantId", tenantId)
		.getSingleResult();
		
		log.info("verificando agend individual ......################### qde --> " + qde);
		
		/* familiar */
		qdeFam = manager.createQuery("SELECT count(l) FROM AgendamentoFamiliar l "
				+ "INNER JOIN l.pessoas r "				
				+ "WHERE l.dataAgendamento = :data "
				+ "and l.conselheiro = :conselheiro "
				+ "and l.tenant_id = :tenantId "
				+ "and l.statusAtendimento = :status ", Long.class)
				.setParameter("data", data, TemporalType.TIMESTAMP)
				.setParameter("status", StatusAtendimento.AGENDADO)
				.setParameter("conselheiro", conselheiro)
				.setParameter("tenantId", tenantId)
				.getSingleResult();
		log.info("verificando agend familiar ......################### qde --> " + qdeFam);
		
		if(qde + qdeFam > 0) {
			throw new NegocioException("Já existe um agendamento para " + conselheiro.getNome() + " neste horário.");

		}		
	}
	
	// criado para realização de testes unitários com JIntegrity
	public void setEntityManager(EntityManager manager) {
		this.manager = manager;
	}
		
}