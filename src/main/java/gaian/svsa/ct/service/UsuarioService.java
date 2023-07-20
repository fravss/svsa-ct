package gaian.svsa.ct.service;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import gaian.svsa.ct.dao.AgendamentoIndividualDAO;
import gaian.svsa.ct.dao.UsuarioDAO;
import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.enums.Status;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;

/**
 * @author murakamiadmin
 *
 */
public class UsuarioService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger(UsuarioService.class);
	
	@Inject
	private UsuarioDAO usuarioDAO;
	@Inject
	private AgendamentoIndividualDAO listaAtendimentoDAO;
	@Inject
	private UnidadeService unidadeService;
	
	public void salvar(Usuario usuario, Long tenantId) throws NegocioException, SQLIntegrityConstraintViolationException {
				
		/*
		 * Verifica se existe agendamento em aberto	
		 */		
		verificaAgendamentos(usuario, tenantId);
		
		
		
		if (usuario.getRole() == null) 
			throw new NegocioException("O papel (role) é obrigatório.");
		if (usuario.getGrupo() == null) 
			throw new NegocioException("O grupo é obrigatório.");
		if (usuario.getStatus() == null) 
			throw new NegocioException("O status é obrigatório.");
		
		
		if(usuario.getSenha() == null || usuario.getSenha().equals(""))
			usuario.setSenha(BCrypt.hashpw("123456", BCrypt.gensalt()));		
		
					
		this.usuarioDAO.salvar(usuario);
	}
			


	public void excluir(Usuario usuario) throws NegocioException {
		usuario.setStatus(Status.INATIVO);
		usuarioDAO.excluir(usuario);
		
	}
	
	public void reset(Usuario usuario) throws NegocioException, SQLIntegrityConstraintViolationException {			
		
		usuario.setSenha(BCrypt.hashpw("123456", BCrypt.gensalt()));		
		this.usuarioDAO.salvar(usuario);
	}
	
	private void verificaAgendamentos(Usuario usuario, Long tenantId) throws NegocioException {
		
		if(usuario.getCodigo() != null) {
			Usuario u = usuarioDAO.buscarPeloCodigo(usuario.getCodigo());		
		
			if(u != null) {			
				
				List<Atendimento> result = listaAtendimentoDAO.buscarAgendaUsuario(u, tenantId);
				log.info(" qde agendamentos " + result.size() + " para usuario " + u.getNome() );
				if( result != null && result.size() > 0) {
					MessageUtil.sucesso("O usuário possui agendamentos pendentes!");
				}	
				else {
					if(usuario.getUnidade().getCodigo() != u.getUnidade().getCodigo())
						MessageUtil.sucesso("Usuario transferido para " + usuario.getUnidade().getNome());	
				}
			}
		}	
	
	}


	public UsuarioDAO getUsuarioDAO() {
		return usuarioDAO;
	}


	public Usuario buscarPeloEmail(String email) throws NoResultException {
		return usuarioDAO.buscarPeloEmail(email);
	}

	public void trocarSenha(Usuario usuario, String senhaAntiga) throws NegocioException, PersistenceException {
					
		Usuario u = buscarPeloEmail(usuario.getEmail());
		
		if(u != null) {
			if(!BCrypt.checkpw(senhaAntiga, u.getSenha()))
				throw new NegocioException("Senha não confere! Digite a senha antiga corretamente.");
			else {
				usuario.setSenha(BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt()));
				this.usuarioDAO.salvar(usuario);
			}				
		}
			
	}
	
	public void alterarPerfil(Usuario usuario) throws NegocioException, PersistenceException {		
		this.usuarioDAO.salvar(usuario);
	}



	public List<Usuario> buscarTecnicos(Unidade unidade, Long tenantId) {		
		return usuarioDAO.buscarTecnicos(unidade, tenantId);
	}
	public List<Usuario> buscarTecnicosRole(Role role, Unidade unidade, Long tenantId) {
		return usuarioDAO.buscarTecnicosRole(role, unidade, tenantId);
	}
	public List<Usuario> buscarUsuarios(Unidade unidade, Long tenantId) {		
		return usuarioDAO.buscarUsuarios(unidade, tenantId);
	}



	public List<Unidade> buscarUnidades(Long tenantId) {	
		log.info("Primeiro acesso a banco... buscar unidades");
		
		return unidadeService.buscarTodos(tenantId);
	}
	
	/*
	private void verificarEmail(Usuario usuario) throws NegocioException {
		
		Usuario u = buscarPeloEmail(usuario.getEmail());
		
		if(u != null) {
			throw new NegocioException("E-mail já cadastrado! Tente outro. O sistema não permite e-mails repetidos.");
		}
	}
	*/
	
}
