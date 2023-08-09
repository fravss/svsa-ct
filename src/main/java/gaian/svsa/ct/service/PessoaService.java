package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import gaian.svsa.ct.dao.PessoaDAO;
import gaian.svsa.ct.modelo.Pais;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.ProgramaSocial;
import gaian.svsa.ct.modelo.to.AtendimentoDTO;
import gaian.svsa.ct.modelo.to.PessoaDTO;


/**
 * @author murakamiadmin
 *
 */
public class PessoaService implements Serializable {

	private static final long serialVersionUID = 1L;
	//private LogUtil logUtil = new LogUtil(PessoaService.class);
	
	@Inject
	private PessoaDAO pessoaDAO;
	@Inject
	private AgendamentoIndividualHelper helper;
	
	public Pessoa buscarPeloCodigo(Long codigo) {
		return pessoaDAO.buscarPeloCodigo(codigo);
	}
	public PessoaReferencia buscarPFPeloCodigo(Long codigo) {
		return pessoaDAO.buscarPFPeloCodigo(codigo);
	}
	
	public Pessoa buscarPessoa(Long codigo, Unidade unidade, Long tenantId) {
		return pessoaDAO.buscarPessoa(codigo, unidade, tenantId);
	}
	
	
	/* SelecionaPessoa por unidade */
	
	
	public List<PessoaDTO> pesquisarPessoaDTO(String termoPesquisa, Unidade unidade, Long tenantId) {
		return pessoaDAO.pesquisarPessoaDTO(termoPesquisa, unidade, tenantId);
	}	
	public List<PessoaDTO> pesquisarPessoaPorEnderecoDTO(String termoPesquisa, Unidade unidade, Long tenantId) {
		return pessoaDAO.pesquisarPessoaPorEnderecoDTO(termoPesquisa, unidade, tenantId);
	}	
	public List<PessoaDTO> pesquisarPorDenunciaDTO(String termoPesquisa, Unidade unidade, Long tenantId) {
		return pessoaDAO.pesquisarPessoaPorDenunciaDTO(termoPesquisa, unidade, tenantId);
	}	
	
	/* SelecionaPessoa Geral */
	
	public List<PessoaDTO> pesquisarPessoaDTO(String termoPesquisa, Long tenantId) {
		return pessoaDAO.pesquisarPessoaDTO(termoPesquisa, tenantId);
	}	
	public List<PessoaDTO> pesquisarPessoaPorEnderecoDTO(String termoPesquisa, Long tenantId) {
		return pessoaDAO.pesquisarPessoaPorEnderecoDTO(termoPesquisa, tenantId);
	}	
	public List<PessoaDTO> pesquisarPorDenunciaDTO(String termoPesquisa, Long tenantId) {
		return pessoaDAO.pesquisarPessoaPorDenunciaDTO(termoPesquisa, tenantId);
	}

	/*
	 * SelecionaPessoaReferencia
	 */
	
	public List<PessoaDTO> pesquisarPorNome(String termoPesquisa, Unidade unidade, Long tenantId) {
		return pessoaDAO.pesquisarPorNome(termoPesquisa, unidade, tenantId);
	}
	public List<PessoaDTO> pesquisarPorEndereco(String termoPesquisa, Unidade unidade, Long tenantId) {
		return pessoaDAO.pesquisarPorEndereco(termoPesquisa, unidade, tenantId);
	}		
	public List<PessoaDTO> pesquisarPorDenuncia(String termoPesquisa, Unidade unidade, Long tenantId) {
		return pessoaDAO.pesquisarPorDenuncia(termoPesquisa, unidade, tenantId);
	}
	
	/*
	 * SelecionaPessoaReferenciaGeral
	 */
	public List<PessoaDTO> pesquisarPorNome(String termoPesquisa, Long tenantId) {
		return pessoaDAO.pesquisarPorNome(termoPesquisa, tenantId);
	}
	public List<PessoaDTO> pesquisarPorEndereco(String termoPesquisa, Long tenantId) {
		return pessoaDAO.pesquisarPorEndereco(termoPesquisa, tenantId);
	}
	public List<PessoaDTO> pesquisarPorDenuncia(Long termoPesquisa, Long tenantId) {
		return pessoaDAO.pesquisarPorDenuncia(termoPesquisa, tenantId);
	}
	
	
	/* Programa Social */
	
	public List<Pessoa> pesquisarPessoaPorProgSocial(ProgramaSocial programa, Unidade unidade, Long tenantId) {
		return pessoaDAO.pesquisarPessoaPorProgSocial(programa, unidade, tenantId);
	}
	
	public List<Pessoa> pesquisarPessoaPorProgSocial(ProgramaSocial programa, Long tenantId) {
		return pessoaDAO.pesquisarPessoaPorProgSocial(programa, tenantId);
	}
	
	
	/* Busca dos países */
	public List<Pais> buscarTodosPaises() {
		return pessoaDAO.buscarTodosPaises();
	}
	public Pais buscarPais(Long codigo) {
		return pessoaDAO.buscarPais(codigo);
	}
	public List<Pessoa> buscarPessoasPais(Pais pais, Unidade unidade, Long tenantId) {
		return pessoaDAO.pesquisarPessoasPais(pais, unidade, tenantId);
	}
	public List<Pessoa> buscarPessoasPais(Pais pais, Long tenantId) {
		return pessoaDAO.pesquisarPessoasPais(pais, tenantId);
	}
	

	public List<AtendimentoDTO> consultarResumoAtendimentos(Pessoa pessoa, Long tenantId) {

		return helper.buscarResumoAtendimentosDTO(pessoa, tenantId);
	}
	
	
	/* 
	 * Migração de dados da pessoa - usado pelo MPComposicaoFamiliar na migração de dados pessoais
	 */
	public void migrarDadosPessoais(Pessoa pessoa, Pessoa pessoaNova) {
		pessoaDAO.migrarDadosPessoais(pessoa, pessoaNova);
	}
	public List<String> buscarNomes(String query, Unidade unidade, Long tenantId) {		
		return pessoaDAO.buscarNomes(query, unidade, tenantId);
	}
	public Pessoa buscarPeloNome(String nome) {		
		return pessoaDAO.buscarPeloNome(nome);
	}
	
	public PessoaDAO getPessoaDAO() {
		return pessoaDAO;
	}
	
	

}