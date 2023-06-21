package gaian.svsa.ct.controller.denuncia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.PieChartModel;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.Acao;
import gaian.svsa.ct.modelo.ListaAtendimento;
import gaian.svsa.ct.modelo.Pais;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.CorRaca;
import gaian.svsa.ct.modelo.enums.EnumUtil;
import gaian.svsa.ct.modelo.enums.FormaAcesso;
import gaian.svsa.ct.modelo.enums.Genero;
import gaian.svsa.ct.modelo.enums.Parentesco;
import gaian.svsa.ct.modelo.enums.ProgramaSocial;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.enums.Sexo;
import gaian.svsa.ct.modelo.enums.Status;
import gaian.svsa.ct.modelo.enums.TipoPcD;
import gaian.svsa.ct.modelo.enums.Uf;
import gaian.svsa.ct.modelo.to.EnderecoTO;
import gaian.svsa.ct.modelo.to.MunicipioTO;
import gaian.svsa.ct.modelo.to.PerfilEtarioTO;
import gaian.svsa.ct.service.MPComposicaoService;
import gaian.svsa.ct.service.PessoaService;
import gaian.svsa.ct.service.rest.BuscaCEPService;
import gaian.svsa.ct.service.rest.RestService;
import gaian.svsa.ct.util.CalculoUtil;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @author murakamiadmin
 *
 */
@Log4j
@Getter
@Setter
@Named(value="mPComposicaoFamiliarBean")
@ViewScoped
public class MPComposicaoFamiliarBean implements Serializable {

	private static final long serialVersionUID = 1769116747361287180L;

	private PessoaReferencia pessoaReferencia;
	private Pessoa novaPessoaReferencia;		
	private List<Pessoa> pessoas;
	private Pessoa pessoa;
	private Pessoa pessoaNova;
	private Long codigoPessoa;

	private Pessoa pessoaSelecionada;	
	
	private PerfilEtarioTO perfilEtarioTO;
	private PieChartModel graficoPerfil;	
	//private List<AtendimentoDTO> resumoAtendimentos = new ArrayList<>();
	private List<Acao> acoes = new ArrayList<>();
	private List<ListaAtendimento> listaFaltas = new ArrayList<>();
	private List<Pais> paises;
	private EnderecoTO enderecoTO;
	
	// Ufs e Municipios IBGE
	private List<Uf> ufs;
	private String nomeMunicipio; 
	private List<MunicipioTO> municipioList;
	private List<MunicipioTO> municipioListEnd;
	
	private Usuario usuarioLogado;
	private boolean administrativo;
	private boolean unidadeDoUsuario = false;
	private Long prontuarioDestino;	
	private String nomePessoaRef;
	private String nomePessoa;
	private Uf uf = null;
		
	// Enuns
	private List<Sexo> sexos;
	private List<Genero> generos;
	private List<Status> status;
	private List<TipoPcD> tiposPcD;
	private List<CorRaca> corRacas;
	private List<Parentesco> parentescos;
	private List<FormaAcesso> formasAcesso;
	private List<ProgramaSocial> programasSociais;
	
	@Inject
	private MPComposicaoService composicaoService;
	@Inject
	private LoginBean loginBean;	
	@Inject
	private RestService restService;
	@Inject
	private PessoaService pessoaService;
	@Inject
	private BuscaCEPService buscaCEPService;
				
	
	@PostConstruct
	public void inicializar() {

		/*
		 * usuario logado
		 */
		usuarioLogado = loginBean.getUsuario();
		if(usuarioLogado.getRole() == Role.ADMINISTRATIVO 
				|| usuarioLogado.getRole() == Role.CADASTRADOR
				|| usuarioLogado.getRole() == Role.AGENTE_SOCIAL) {
			setAdministrativo(true);
		}		
		else {
			setAdministrativo(false);
		}
		
			
		
		this.ufs = Arrays.asList(Uf.values());		
		this.sexos = Arrays.asList(Sexo.values());
		this.tiposPcD = Arrays.asList(TipoPcD.values());
		this.generos = Arrays.asList(Genero.values());
		this.status = Arrays.asList(Status.values());
		this.setCorRacas(Arrays.asList(CorRaca.values()));
		this.parentescos = EnumUtil.getCodigosParentescoMembro();		
		this.formasAcesso = Arrays.asList(FormaAcesso.values());
		this.programasSociais = Arrays.asList(ProgramaSocial.values());
		this.paises = this.pessoaService.buscarTodosPaises();
				
		graficoPerfil = new PieChartModel();
		
		this.limpar();
	}
	
	
	public void pesquisarMembros() {
		if(this.pessoaReferencia != null) {
			pessoas = composicaoService.buscarTodosMembros(this.pessoaReferencia, loginBean.getTenantId());
		}
		else
			MessageUtil.erro("É necessária a pessoa de referência.");
	}
	
		
	/*
	public void consultarResumoAtendimentos() {
		
		this.resumoAtendimentos = composicaoService.consultarResumoAtendimentos(pessoa, loginBean.getTenantId());
		
	}
	*/
	
	/*
	public void trocarPessoaReferencia() {

		try {
			log.info("trocando pessoa referencia...1");
			this.pessoaReferencia = composicaoService.trocarPessoaReferencia(getPessoaReferencia(), getNovaPessoaReferencia());	
			pesquisarMembros(); 
			MessageUtil.sucesso("Pessoa de Referência trocada com sucesso!");
			//limpar();
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
		//return "/restricted/agenda/ManterProntuario.xhtml?faces-redirect=true";
		//return "/agenda/ManterProntuario.xhtml";
	} */
	
	
		
	/*
	 * Cadastro de Pessoas membros
	 */
	public void salvarMembro() {
		try {	
			if(!isUnidadeDoUsuario())
				throw new NegocioException("Operação inválida! O prontuário não é da sua unidade.");			
			
			// novo membro - se é alteração não seta a familia novamente
			if(pessoa.getCodigo() == null) {
				pessoa.setFamilia(pessoaReferencia.getFamilia());				
			}
			
			pessoa.getFamilia().getEndereco().setMunicipio(pessoa.getFamilia().getEndereco().getMunicipio());
			
			this.composicaoService.salvar(pessoa);
			
			MessageUtil.sucesso("Membro " + pessoa.getNome() + " incluído/alterado com sucesso.");
			pesquisarMembros();
			limpar();			
						
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	/*
	 * Manipulação de membros
	 */

	
	/* Exclui membro da familia	*/
	public void excluirMembro() {
		try {
			
			if(!isUnidadeDoUsuario())
				throw new NegocioException("Operação inválida! O prontuário não é da sua unidade.");
		
			composicaoService.excluirMembro(pessoa);
			pesquisarMembros();
			
			MessageUtil.sucesso("Membro excluído com sucesso!");
		}
		catch (NegocioException e) {
			MessageUtil.erro(e.getMessage());
		}
	}
	
	/* Inativa o membro */
	public void inativarMembro() {
		try {
			
			if(!isUnidadeDoUsuario())
				throw new NegocioException("Operação inválida! O prontuário não é da sua unidade.");
			
			composicaoService.inativarMembro(pessoa);
			
			log.info("pessoa INATIVADA: " + pessoa.getNome());
			pesquisarMembros();
			
			pessoa = null;
			
			MessageUtil.sucesso("Membro INTIVADO com sucesso!");

		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	/* Transfere membro para outra família
	public void transferirMembro() {
		try {
			
			composicaoService.transferirMembro(pessoa, prontuarioDestino);
			
			log.info("pessoa transferida: " + pessoa.getNome());
			pesquisarMembros();
			
			pessoa = null;
			
			MessageUtil.sucesso("Membro transferido com sucesso!");

		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	} */
	
	/* Migração de dados de pessoais */
	public void migrarDadosPessoais() {
		try {
									
			pessoaService.migrarDadosPessoais(pessoa, pessoaNova);
			
			log.info("Dados pessoais migrados para a pessoa: " + pessoaNova.getNome());
			pesquisarMembros();
			
			MessageUtil.sucesso("Dados de " + pessoa.getNome() + " migrados com sucesso para " + pessoaNova.getNome());
			
			pessoa = null;			

		} catch (Exception e) {
			e.printStackTrace();
			MessageUtil.erro("Problemas na migração de dados.");
		}
	}
	public List<String> buscarNomes(String query) {
        List<String> results = new ArrayList<>();
        
        try {
			results = pessoaService.buscarNomes(query, usuarioLogado.getUnidade(), loginBean.getTenantId());	
					
		} catch(Exception e) {
			MessageUtil.alerta("Não existem PESSOAS com esse nome!");
		}        
       
        return results;
    }
	public void buscarPessoa() {
		try {
			
			pessoaNova = pessoaService.buscarPeloNome(nomePessoa);
		
		} catch(Exception e) {
			MessageUtil.sucesso("Não existe esta PESSOA!");
		}
	}
	
	/* por prontuario
	public void buscarNomePessoa() {
		
		log.debug("buscar nome ");
		
		if(prontuarioDestino != null) {
			log.info("buscando prontuario digitado = " + prontuarioDestino);
			
			try {
				Prontuario prontuario = composicaoService.buscarProntuario(prontuarioDestino, usuarioLogado.getUnidade(), loginBean.getTenantId());
				
				if(prontuario != null) {
					setNomePessoaRef(prontuario.getCodigo() + " - " + prontuario.getFamilia().getPessoaReferencia().getNome());
				}				
					
			}catch(Exception e) {
				MessageUtil.erro("Prontuário não existe ou não é da sua unidade!");
			}						
		}
		else {
			MessageUtil.alerta("O Prontuário digitado é inválido ou não existe!");
		}	
	} */

	/*
	 * Fim manipulação de membros
	 */
	
	/*
	 * Cadastro de Observacoes
	 */
	public void salvarObservacao() {
		try {	
			
			if(!isUnidadeDoUsuario())
				throw new NegocioException("Operação inválida! O prontuário não é da sua unidade.");
			
			
			MessageUtil.sucesso("Observação incluída com sucesso.");
			pesquisarMembros();
			
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	
	public void limpar() {	
			
			this.pessoa = new Pessoa();		
			
			this.pessoa.setTenant_id(loginBean.getTenantId());
			uf = null;
	}
	
	public void limparObservacao() {
		try {
			if(!isUnidadeDoUsuario())
				throw new NegocioException("Operação inválida! O prontuário não é da sua unidade.");
			
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void consultaFaltas() {		
		
		setListaFaltas(composicaoService.consultaFaltas(pessoa, loginBean.getTenantId()));		
	}
		
	public boolean isPessoaSelecionada() {			
		//log.info("isPessoaSelecionada()");
        return pessoa != null && pessoa.getCodigo() != null;
    }
	
	public boolean isPessoaReferenciaSelecionada() {		
		//log.info("isPessoaReferenciaSelecionada()");
        return pessoaReferencia != null && pessoaReferencia.getCodigo() != null;
    }	
	
	public void setPessoaReferencia(PessoaReferencia pr) {		
		
		long codigo = pr.getFamilia().getDenuncia().getUnidade().getCodigo();
		long codigo2 = usuarioLogado.getUnidade().getCodigo();
		
		if( codigo == codigo2 ) {
			log.info("mesma unidade ");
			setUnidadeDoUsuario(true);
		}
		else {
			setUnidadeDoUsuario(false);
		}			
		
		//log.info("Unidade do usuario = unidade do prontuario?  " + isUnidadeDoUsuario());
				
		this.pessoaReferencia = pr;
		pesquisarMembros();
		//inicializar();
		limpar();
	} 

	/*
	 * Grafico perfil etario
	 */	
	public void initGraficoPerfil() {
		
		log.info("initGraficoPerfil chamado...");
		
		calcularIdades();
		createPieModel();
	}
	
	private void calcularIdades() {		
		
		perfilEtarioTO = new PerfilEtarioTO();
		
		List<Pessoa> pessoasIdades;		
		
		if(this.pessoaReferencia != null) {
			if(this.pessoas != null) {
				pessoasIdades = composicaoService.buscarTodosMembros(this.pessoaReferencia, loginBean.getTenantId());
				log.info("Calculando idades de " + String.valueOf(pessoasIdades.size()));
			}
			else {
				pessoasIdades = this.pessoas;				
			}
		
			int idade = 0;
			for (Pessoa p : pessoasIdades) {
				idade = CalculoUtil.calcularIdade(p.getDataNascimento());
				
				if(idade < 7) {
					perfilEtarioTO.setQde0a6anos(perfilEtarioTO.getQde0a6anos() + 1);
				} else if(idade < 15) {
					perfilEtarioTO.setQde7a14anos(perfilEtarioTO.getQde7a14anos() + 1);
				} else if(idade < 18) {
					perfilEtarioTO.setQde15a17anos(perfilEtarioTO.getQde15a17anos() + 1);
				} else if(idade < 30) {
					perfilEtarioTO.setQde18a29anos(perfilEtarioTO.getQde18a29anos() + 1);
				} else if(idade < 60) {
					perfilEtarioTO.setQde30a59anos(perfilEtarioTO.getQde30a59anos() + 1);
				} else if(idade < 65) {
					perfilEtarioTO.setQde60a64anos(perfilEtarioTO.getQde60a64anos() + 1);	
				} else if(idade < 70) {
					perfilEtarioTO.setQde65a69anos(perfilEtarioTO.getQde65a69anos() + 1);
				} else if(idade < 29) {
					perfilEtarioTO.setQde18a29anos(perfilEtarioTO.getQde18a29anos() + 1);	
				} else {
					perfilEtarioTO.setMais70anos(perfilEtarioTO.getMais70anos() + 1);
				}
			}			
		}		
		else
			MessageUtil.erro("É necessária a pessoa de Referência.");
	}
	
	private void createPieModel() {
		
		//log.info("Criando grafico");
		
        graficoPerfil = new PieChartModel();     
        
		
        graficoPerfil.set("0 a 6 ("+perfilEtarioTO.getQde0a6anos()+")", perfilEtarioTO.getQde0a6anos());
        graficoPerfil.set("7 a 14 ("+perfilEtarioTO.getQde7a14anos()+")", perfilEtarioTO.getQde7a14anos());
        graficoPerfil.set("15 a 17 ("+perfilEtarioTO.getQde15a17anos()+")", perfilEtarioTO.getQde15a17anos());
        graficoPerfil.set("18 a 29 ("+perfilEtarioTO.getQde18a29anos()+")", perfilEtarioTO.getQde18a29anos());
        graficoPerfil.set("30 a 59 ("+perfilEtarioTO.getQde30a59anos()+")", perfilEtarioTO.getQde30a59anos());
        graficoPerfil.set("60 a 64 ("+perfilEtarioTO.getQde60a64anos()+")", perfilEtarioTO.getQde60a64anos());
        graficoPerfil.set("65 a 69 ("+perfilEtarioTO.getQde65a69anos()+")", perfilEtarioTO.getQde65a69anos());
        graficoPerfil.set("+ 70 ("+perfilEtarioTO.getMais70anos()+")", perfilEtarioTO.getMais70anos());
        
        graficoPerfil.setLegendPosition("e");
        
    }	

	
	/*
	public void listarMunicipiosNasc() throws Exception {
		
		try {
			log.info(pessoa.getUfNascimento());
			log.info(uf);
			municipioList = restService.listarMunicipios(uf.name());
		}
		catch(Exception e){
			MessageUtil.sucesso("Problema na recuperação dos municípios.");
		}
	}
	
	
		
						
					<p:outputLabel value="UF de Nascimento" for="ufNasc" />
					<h:panelGroup>
						<p:selectOneMenu id="ufNasc" 
							value="#{manterProntuarioBean.mpComposicaoBean.pessoa.ufNascimento}">
							<f:selectItem itemLabel="Selecione a Uf" />
							<f:selectItems value="#{manterProntuarioBean.mpComposicaoBean.ufs}" 
								var="ufNa" 
								itemLabel="#{ufNa}" itemValue="#{ufNa}"/>							
							<p:ajax listener="#{manterProntuarioBean.mpComposicaoBean.listarMunicipiosNasc}" 
								update="municipioNasc" event="change" process="ufNasc municipioNasc"/>
						</p:selectOneMenu>
						<p:spacer width="10px" />						
					
						<p:outputLabel for="municipioNasc" value="Município de Nascimento" />
						<p:selectOneMenu id="municipioNasc" value="#{manterProntuarioBean.mpComposicaoBean.pessoa.municipioNascimento}">
							<f:selectItem itemLabel="Selecione o Município" />
							<f:selectItems value="#{manterProntuarioBean.mpComposicaoBean.municipioList}" var="municipioNa" 
								itemLabel="#{municipioNa.nome}" itemValue="#{municipioNa.nome}"/>
						</p:selectOneMenu>
					</h:panelGroup>
					
					
					
	public void listarMunicipiosEnd() throws Exception {
		
		try {
			log.info(pessoa.getFamilia().getEndereco().getUf());
			log.info(uf);
			municipioListEnd = restService.listarMunicipios(uf.name());
		}
		catch(Exception e){
			MessageUtil.sucesso("Problema na recuperação dos municípios.");
		}
	}
	
	
	
	<p:outputLabel value="Novo Município" for="uf" />
						<h:panelGroup>
							<p:selectOneMenu id="uf" 
								value="manterProntuarioBean.mpComposicaoBean.pessoa.familia.endereco.uf"
								disabled="#{manterProntuarioBean.mpComposicaoBean.pessoa.codigo ne 
											manterProntuarioBean.mpComposicaoBean.pessoaReferencia.codigo}">
								<f:selectItem itemLabel="Selecione a Uf" />
								<f:selectItems value="#{manterProntuarioBean.mpComposicaoBean.ufs}" 
									var="uf" 
									itemLabel="#{uf}" itemValue="#{uf}"/>							
								<p:ajax listener="#{manterProntuarioBean.mpComposicaoBean.listarMunicipiosEnd}" 
									update="municipio" event="change" process="uf municipio"/>
							</p:selectOneMenu>
							<p:spacer width="10px" />
						
							<p:outputLabel for="municipio" value="Município" />								
							<p:selectOneMenu id="municipio" value="#{manterProntuarioBean.mpComposicaoBean.pessoa.familia.endereco.municipio}"
								disabled="#{manterProntuarioBean.mpComposicaoBean.pessoa.codigo ne 
											manterProntuarioBean.mpComposicaoBean.pessoaReferencia.codigo}">
								<f:selectItem itemLabel="Selecione o Município" />
								<f:selectItems value="#{manterProntuarioBean.mpComposicaoBean.municipioList}" var="municipio" 
									itemLabel="#{municipio.nome}" itemValue="#{municipio.nome}"/>
							</p:selectOneMenu>
						</h:panelGroup>			
	
	*/
	
	public void buscaEnderecoPorCEP() {
			
	        try {
				enderecoTO  = buscaCEPService.buscaEnderecoPorCEP(pessoa.getFamilia().getEndereco().getCep());
				
				/*
		         * Preenche o Endereco do prontuario com os dados buscados
		         */	 
				
				pessoa.getFamilia().getEndereco().setEndereco(enderecoTO.getTipoLogradouro().
		        		                concat(" ").concat(enderecoTO.getLogradouro()));
				pessoa.getFamilia().getEndereco().setNumero(null);
				pessoa.getFamilia().getEndereco().setBairro(enderecoTO.getBairro());
				pessoa.getFamilia().getEndereco().setMunicipio(enderecoTO.getCidade());
				pessoa.getFamilia().getEndereco().setUf(enderecoTO.getEstado());
		        
		        if (enderecoTO.getResultado() != 1) {
		        	MessageUtil.erro("Endereço não encontrado para o CEP fornecido.");
		        }
			} catch (NegocioException e) {
				e.printStackTrace();
				MessageUtil.erro(e.getMessage());		            
			}       
		}

}