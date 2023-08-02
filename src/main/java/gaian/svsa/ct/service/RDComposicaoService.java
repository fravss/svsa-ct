package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import gaian.svsa.ct.dao.RDComposicaoDAO;
import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.to.AtendimentoDTO;
import gaian.svsa.ct.util.NegocioException;


/**
 * @author murakamiadmin
 *
 */
public class RDComposicaoService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(RDComposicaoService.class);
	
	@Inject
	private RDComposicaoDAO composicaoDAO;
	@Inject
	private AgendamentoIndividualService listaService;
	@Inject
	private DenunciaService denunciaService;

	
	public Pessoa salvar(Pessoa pessoa) throws NegocioException {		
		
		//pessoa.setDataRegistroComposicaoFamiliar(Calendar.getInstance());
		
		/* grava municipio em maiusculo*/
		pessoa.getFamilia().getPessoaReferencia().getEndereco().setMunicipio(pessoa.getFamilia().getPessoaReferencia().getEndereco().getMunicipio());
		
		return this.composicaoDAO.salvar(pessoa);		
		
	}

	public void inativarMembro(Pessoa pessoa) throws NegocioException {
		
		if(pessoa instanceof PessoaReferencia)
			throw new NegocioException("Pessoa de Referência não pode ser INATIVADA!");	
		
		this.composicaoDAO.salvar(pessoa);		
	}
	
	
	public void transferirMembro(Pessoa pessoa, Long codigoDenunciaDestino) throws NegocioException {
		
		if(pessoa instanceof PessoaReferencia)
			throw new NegocioException("Pessoa de Referência não pode ser transferida!");	
				
		denunciaService.transferirMembro(pessoa, codigoDenunciaDestino);
	} 
	
	public void excluirMembro(Pessoa pessoa) throws NegocioException {
		
		if(pessoa instanceof PessoaReferencia)
			throw new NegocioException("Pessoa de Referência não pode ser excluída da família!");
		
		pessoa.setExcluida(true);
		this.composicaoDAO.salvar(pessoa);
	}

	public Denuncia buscarDenuncia(Long denunciaDestino, Unidade unidade, Long tenantId) {
		
		//log.info(denunciaDestino);
		//log.info(unidade);
		//log.info(tenantId);
		return denunciaService.buscarDenuncia(denunciaDestino, unidade, tenantId);
	}
	
	public void verificarExistenciaProntuario(Pessoa pessoa, Unidade unidade, Long tenantId) throws NegocioException {
		
		List<PessoaReferencia> pessoasReferencia = composicaoDAO.pesquisar(pessoa.getNome(), unidade, tenantId);
				
		if(pessoasReferencia.size() > 0) {
			
			for(PessoaReferencia p : pessoasReferencia) {
				if(p.getCodigo() == pessoa.getCodigo()) {
					throw new NegocioException("Essa pessoa já tem prontuário. ");
				}
			}
			throw new NegocioException("Existem prontuários com nomes semelhantes ao desta pessoa. ");
		}		
	}

	
	
	
	/*
	 * FIM da Criação de prontuario a partir da composição familiar 
	 */
	
	public List<PessoaReferencia> todasPessoasReferencia(Long tenantId) {
		return composicaoDAO.todasPessoasReferencia(tenantId);
	}

	public List<Pessoa> buscarTodosMembros(PessoaReferencia pessoaReferencia, Long tenantId) {
		return composicaoDAO.buscarTodosMembros(pessoaReferencia, tenantId);
	}
	
	public List<Pessoa> buscarTodosMembros(Denuncia denuncia, Long tenantId) {
		return composicaoDAO.buscarTodosMembros(denuncia, tenantId);
	}
	public RDComposicaoDAO getComposicaoDAO() {
		return composicaoDAO;
	}

	
	public List<PessoaReferencia> pesquisar(String termoPesquisa, Unidade unidade, Long tenantId) {
		return composicaoDAO.pesquisar(termoPesquisa, unidade, tenantId);
	}
	
	
	public List<String> pesquisarNomesPR(String query, Long tenantId) {
		return composicaoDAO.pesquisarNomesPR(query, tenantId);
	}
	public void validarCadastro(String query, Long tenantId) throws NegocioException {
		
		List<Denuncia> denuncias = composicaoDAO.pesquisarExistente(query, tenantId);
		StringJoiner message = new StringJoiner(", ").add("CUIDADO! Já existe denuncia com esse nome");
		for(Denuncia p : denuncias) {			
			
			String s = p.getCodigo() + " - " + p.getFamilia().getPessoaReferencia().getNome() + " - " + p.getUnidade().getNome();
			log.info(s);
			message.add(s);			
		}
		message.add("PORÉM, isso não impede o cadastro duplicado.");
		
		if(denuncias.size() > 0) {
			log.info(message.toString());
			throw new NegocioException(message.toString());
		}
	}
	
	
	
	
	
	
	public List<AtendimentoDTO> consultarResumoAtendimentos(Pessoa pessoa, Long tenantId) {		

		/* atendimentos individualizados, coletivos, ações, etc.) */
		List<AtendimentoDTO> atendIndiv = listaService.buscarResumoAtendimentosDTO(pessoa, tenantId);
		
		return atendIndiv;
	}	
	
	public List<Atendimento> consultaFaltas(Pessoa pessoa, Long tenantId) {
		
		return listaService.consultaFaltas(pessoa, tenantId);
		
	}
	
	
	public PessoaReferencia trocarPessoaReferencia(PessoaReferencia pessoaReferencia, Pessoa novaPessoaReferencia) throws NegocioException {
		
		
		Pessoa pessoa = composicaoDAO.buscarPeloCodigo(pessoaReferencia.getCodigo());
		
		// trocando PessoaReferencia na mesma transacao
		log.info("trocando pessoa referencia...2");
		this.composicaoDAO.trocarPR(pessoa.getFamilia(), pessoa.getFamilia().getPessoaReferencia(), novaPessoaReferencia);			
		
		return this.composicaoDAO.buscarPFPeloCodigo(novaPessoaReferencia.getCodigo());
		
	} 
	
} 