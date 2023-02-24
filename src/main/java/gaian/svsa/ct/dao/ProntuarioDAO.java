package gaian.svsa.ct.dao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import gaian.svsa.ct.modelo.Acao;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Prontuario;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.StatusAtendimento;
import gaian.svsa.ct.util.NegocioException;
import gaian.svsa.ct.util.jpa.Transactional;


/**
 * @author murakamiadmin
 *
 */
public class ProntuarioDAO implements Serializable {

	private static final long serialVersionUID = 2L;
	
	private Logger log = Logger.getLogger(ProntuarioDAO.class);
	
	@Inject
	private EntityManager manager;
	
		
	@Transactional
	public Prontuario salvarDesmembramento(Prontuario clone, Pessoa pessoa, Usuario tecnico, Long tenantId) throws NegocioException {
		try {
	
			// altera para PessoaReferencia
			final Query query = manager.createNativeQuery( "UPDATE Pessoa SET TIPO_PESSOA = 'PESSOA_REFERENCIA' WHERE codigo = :id "
					+ "and tenant_id = :tenantId " );
			query.setParameter( "id", pessoa.getCodigo() );
			query.setParameter("tenantId", tenantId);
            query.executeUpdate();
						
			PessoaReferencia pr = buscarPessoaReferencia(pessoa.getCodigo());
			
			clone.getFamilia().setPessoaReferencia(pr);
			//clone.getFamilia().getMembros().add(pr);
			pr.setFamilia(clone.getFamilia());
			
			
			// criar ação de desmembramento .
			if(clone.getCodigo() == null) {
				log.info("Criando ações para desmembramento coletivo...");
				gerarAcaoDesmembramento(clone, tecnico);
			}
		
			Prontuario prontNovo = manager.merge(clone);
			
			log.info("sucesso na criação do prontuario novo.");
			
			return prontNovo;
			
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
	
	/* cria uma ação para o desmembramento. */
	private void gerarAcaoDesmembramento(Prontuario prontuario, Usuario tecnico) {
		
		SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		Acao a = new Acao();
		a.setData(new Date());
		a.setDescricao("Desmembrado da familia anterior e criado novo prontuário. " + out.format(a.getData()) );
		a.setPessoa(prontuario.getFamilia().getPessoaReferencia());
		a.setTecnico(tecnico);
		a.setUnidade(prontuario.getUnidade());
		a.setTenant_id(prontuario.getTenant_id());
		a.setStatusAtendimento(StatusAtendimento.ATENDIDO);
		
		manager.merge(a);
	}
			
	/*
	 * Buscas	
	 */
	
	public Prontuario buscarProntuario(Long codigo, Unidade unidade, Long tenantId) {
		return manager.createQuery("select p from Prontuario p where p.codigo = :codigo "
				+ "and p.excluido = :exc "
				+ "and p.tenant_id = :tenantId "
				+ "and p.unidade = :unidade", Prontuario.class)
				.setParameter("codigo", codigo)
				.setParameter("tenantId", tenantId)
				.setParameter("unidade", unidade)
				.setParameter("exc", false)
				.getSingleResult();
	}
	
	public String obterNrProntuario() {
		
		Long np = (Long) manager.createNamedQuery("Prontuario.obterNrProntuario").getSingleResult();
		if(np == null)
			return "1";
		return np.toString();
	}
	
	public Prontuario buscarPeloCodigo(Long codigo) {
		return manager.find(Prontuario.class, codigo);
	}
	
	public PessoaReferencia buscarPessoaReferencia(Long codigo) {
		return manager.find(PessoaReferencia.class, codigo);
	}

	
	
	// para fins de testes unitários
	public void setManager(EntityManager manager) {
		this.manager = manager;
	}
	
}
