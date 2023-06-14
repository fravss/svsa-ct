package gaian.svsa.ct.controller.pront;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
//import javax.faces.bean.ViewScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.itextpdf.io.source.ByteArrayOutputStream;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.modelo.Endereco;
import gaian.svsa.ct.modelo.Familia;
import gaian.svsa.ct.modelo.Pais;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Prontuario;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.CorRaca;
import gaian.svsa.ct.modelo.enums.Genero;
import gaian.svsa.ct.modelo.enums.Sexo;
import gaian.svsa.ct.modelo.enums.Status;
import gaian.svsa.ct.modelo.enums.TipoPcD;
import gaian.svsa.ct.modelo.enums.TipoUnidade;
import gaian.svsa.ct.modelo.enums.Uf;
import gaian.svsa.ct.modelo.to.EnderecoTO;
import gaian.svsa.ct.modelo.to.MunicipioTO;
import gaian.svsa.ct.service.CapaProntuarioService;
import gaian.svsa.ct.service.MPComposicaoService;
import gaian.svsa.ct.service.PessoaService;
import gaian.svsa.ct.service.UnidadeService;
import gaian.svsa.ct.service.pdf.ProntuarioPDFService;
import gaian.svsa.ct.service.rest.BuscaCEPService;
import gaian.svsa.ct.service.rest.RestService;
import gaian.svsa.ct.service.s3.AmazonS3Service;
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
@Named(value="cadastroProntuarioBean")
@ViewScoped
public class CadastroCapaProntuarioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Prontuario prontuario;
	private PessoaReferencia pessoaReferencia;
	private String nrProntuario;
	//private List<Unidade> unidades;
	private List<Sexo> sexos;
	private List<Genero> generos;
	private List<TipoPcD> tiposPcD;
	private List<CorRaca> corRacas;
	private List<Uf> ufs;
	private List<MunicipioTO> municipioList;
	private List<Status> status;
	private EnderecoTO enderecoTO;
	private Unidade unidade;
	private List<Pais> paises;
	@SuppressWarnings("unused")
	private boolean edicao = false;
	private boolean composicao = false;
	private Part file;
	
	private Long prontuarioVinculado;
	private Prontuario prontuarioCras;
	private String nomePessoaRef;
	
	
	
	@Inject
	private CapaProntuarioService cadastroProntuarioService;	
	@Inject
	private BuscaCEPService buscaCEPService;	
	@Inject
	private UnidadeService unidadeService;	
	@Inject
	MPComposicaoService composicaoService;
	@Inject
	private PessoaService pessoaService;
	@Inject
	private ProntuarioPDFService pdfService;
	@Inject
	private RestService restService;
	@Inject
	private AmazonS3Service s3;
	
	@Inject
	private MPComposicaoFamiliarBean mpComposicaoBean;
	
	
	@Inject
	private LoginBean loginBean;
	
		
	@PostConstruct
	public void inicializar() {
		
		this.limpar();		
		//this.unidades = this.unidadeService.buscarTodos(loginBean.getTenantId());
		this.paises = this.pessoaService.buscarTodosPaises();
		this.sexos = Arrays.asList(Sexo.values());
		this.generos = Arrays.asList(Genero.values());
		this.tiposPcD = Arrays.asList(TipoPcD.values());
		this.corRacas = Arrays.asList(CorRaca.values());
		this.ufs = Arrays.asList(Uf.values());		
		this.status = Arrays.asList(Status.values());
		this.unidade = loginBean.getUsuario().getUnidade();
		
	}
	
	public List<String> completeText(String query) {
		
		List<String> results = new ArrayList<String>(); 
		
		try {	        
	        this.composicaoService.validarCadastro(query, loginBean.getTenantId());
	        //results = composicaoService.pesquisarNomesPR(query); 
	        return results;
		} catch (NegocioException e) {
			log.error(e.getMessage());
			MessageUtil.alerta(e.getMessage());
		}
        return results;
    }
	
	
	public void salvar() {
		try {
			//log.debug("Prontuario a ser gerado: " + getNrProntuario());
			
			prontuario.setUnidade(unidade);
			prontuario.getFamilia().setProntuario(prontuario);
			//prontuario.getFamilia().getPessoaReferencia().setParentescoPessoaReferencia(Parentesco.PESSOA_REFERENCIA);
			prontuario.getFamilia().getPessoaReferencia().setFamilia(prontuario.getFamilia());
			
			// registra o nome e email de quem criou o prontuário
			String  criador = loginBean.getUsuario().getNome().concat(":").concat(loginBean.getEmail());
			prontuario.setCriador(criador);			
					
			this.cadastroProntuarioService.salvar(prontuario);
			
			log.info("Prontuario criado por: " + criador);
			
			MessageUtil.sucesso("Prontuário salvo com sucesso!");
			this.limpar();
			
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}		
	}
	
	
	public void limpar() {
		this.prontuario = new Prontuario();	
		this.prontuario.setTenant_id(loginBean.getTenantId());
		this.prontuario.setStatus(Status.ATIVO);
		this.prontuario.setProntuario(getNrProntuario());
		this.prontuario.setFamilia(new Familia());
		this.prontuario.getFamilia().setTenant_id(loginBean.getTenantId());
		PessoaReferencia pr = new PessoaReferencia();
		pr.setTenant_id(loginBean.getTenantId());
		this.prontuario.getFamilia().setPessoaReferencia(pr);		
		this.prontuario.getFamilia().setEndereco(new Endereco());
		this.prontuario.getFamilia().getEndereco().setTenant_id(loginBean.getTenantId());
		setEdicao(false);
		setComposicao(false);
		setProntuarioVinculado(null);
		setNomePessoaRef(null);
	}
	
	public void listarMunicipiosEnd() throws Exception {
		
		try {
			setMunicipioList(restService.listarMunicipios(prontuario.getFamilia().getEndereco().getUf()));
		}
		catch(Exception e){
			MessageUtil.sucesso("Problema na recuperação dos municípios.");
		}
	}

	public void buscaEnderecoPorCEP() {
		
        try {
			enderecoTO  = buscaCEPService.buscaEnderecoPorCEP(prontuario.getFamilia().getEndereco().getCep());
			
			/*
	         * Preenche o Endereco do prontuario com os dados buscados
	         */	 
			
	        prontuario.getFamilia().getEndereco().setEndereco(enderecoTO.getTipoLogradouro().
	        		                concat(" ").concat(enderecoTO.getLogradouro()));
	        prontuario.getFamilia().getEndereco().setNumero(null);
	        prontuario.getFamilia().getEndereco().setBairro(enderecoTO.getBairro());
	        prontuario.getFamilia().getEndereco().setMunicipio(enderecoTO.getCidade());
	        prontuario.getFamilia().getEndereco().setUf(enderecoTO.getEstado());
	        
	        if (enderecoTO.getResultado() != 1) {
	        	MessageUtil.erro("Endereço não encontrado para o CEP fornecido.");
	        }
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());		            
		}       
	}
	
	public void buscarNomePessoa() {
			
		log.debug("Capa Prontuario creas = " + prontuario.getCodigo());
		
		if(prontuarioVinculado != null) {
			log.info("Capa Prontuario buscando prontuario digitado = " + prontuarioVinculado);
			
			try {
				setProntuarioCras(cadastroProntuarioService.buscarProntuarioCRAS(prontuarioVinculado, loginBean.getTenantId()));
				if(prontuarioCras != null) {
					log.info("Capa Prontuario cras digitado = " + prontuarioCras.getCodigo());
					if(prontuarioCras.getProntuarioVinculado() == null) {	
						setNomePessoaRef(prontuarioCras.getCodigo() + "-" + prontuarioCras.getFamilia().getPessoaReferencia().getNome() + " [" + prontuarioCras.getUnidade().getNome() + "]");
					}
					else {
						MessageUtil.alerta("Esse prontuário já está vinculado a outro prontuário CREAS");
					}						
				}
			}catch(Exception e) {
				MessageUtil.erro("Prontuário não existe ou não é de CRAS!");
			}						
		}
		else {
			MessageUtil.alerta("O Prontuário digitado é inválido ou não existe!");
		}	
	}
	
	/* Busca prontuario CRAS para vincular ao prontuario CREAS - Só para CREAS) */
	public void vincularProntuario() {		
		
		/* Para prontuario novo
		 * Verificar o vinculo é para prontuario novo, o que está sendo criado  
		 */
		if(prontuario.getCodigo() == null) {
			if(prontuarioCras != null) {
				if(prontuarioCras.getProntuarioVinculado() == null) {
					prontuarioCras.setProntuarioVinculado(prontuario);
					prontuario.setProntuarioVinculado(prontuarioCras);
				}				
			}
			else {
				MessageUtil.alerta("Selecione um prontuário a ser vinculado!");
			}
		}
		else {			
			
			// Verifica se o prontuário ainda não tem vínculo
			log.info("vinculando... " + prontuarioVinculado);
			if(prontuario.getProntuarioVinculado() == null) {				
				if(prontuarioVinculado != null) {
					log.info("ProntuarioVinculado  = " + prontuarioVinculado);
					
					try {
						//prontuarioCras = cadastroProntuarioService.buscarProntuarioCRAS(prontuarioVinculado);
						if(prontuarioCras != null) {
							log.info("Capa Prontuario cras digitado = " + prontuarioCras.getCodigo());
							if(prontuarioCras.getProntuarioVinculado() == null) {	
								setNomePessoaRef(prontuarioCras.getFamilia().getPessoaReferencia().getNome());
								// vinculo de volta CRAS->CREAS
								prontuarioCras.setProntuarioVinculado(prontuario);
								// vinculo de ida CREAS -> CRAS
								prontuario.setProntuarioVinculado(prontuarioCras);
								
								cadastroProntuarioService.salvar(prontuario);
							}
							else {
								MessageUtil.alerta("Esse prontuário já está vinculado a outro prontuário CREAS");
							}						
						}
					}catch(Exception e) {
						MessageUtil.erro("Prontuário não existe ou não é de CRAS!");
					}						
				}
				else {
					MessageUtil.alerta("O Prontuário digitado é inválido ou não existe!");
				}
			}
			else {
				log.info("Capa Prontuario substituindo o vinculo existente do prontuario creas = " + prontuario.getProntuarioVinculado().getCodigo());
				
				if(prontuarioVinculado != null) {
					log.info("Capa Prontuario buscando prontuario digitado = " + prontuarioVinculado);
					
					try {
						//prontuarioCras = cadastroProntuarioService.buscarProntuarioCRAS(prontuarioVinculado);
						if(prontuarioCras != null) {
							
							if(prontuarioCras.getProntuarioVinculado() == null) {
								log.info("Capa Prontuario prontuario cras = " + prontuarioCras.getProntuarioVinculado());
								setNomePessoaRef(prontuarioCras.getFamilia().getPessoaReferencia().getNome());
								
								Long codigoSubstituido = prontuario.getProntuarioVinculado().getCodigo();
								// substitui o vinculo
								prontuarioCras.setProntuarioVinculado(prontuario);	// cras aponta para creas						
								prontuario.setProntuarioVinculado(prontuarioCras);  // creas aponta para cras
								cadastroProntuarioService.salvar(prontuario);
								
								// limpa o vinculo do substituido							
								Prontuario prontuarioCrasSubstituido = cadastroProntuarioService.buscarProntuarioCRAS(codigoSubstituido, loginBean.getTenantId());
								log.info("codigo " + codigoSubstituido + "substituido por null" );
								prontuarioCrasSubstituido.setProntuarioVinculado(null);
								cadastroProntuarioService.salvar(prontuarioCrasSubstituido);
								
							}
							else {
								MessageUtil.alerta("Esse prontuário já está vinculado a outro prontuário CREAS");
							}						
						}
					}catch(Exception e) {
						MessageUtil.erro("Prontuário não existe ou não é de CRAS!");
					}						
				}
				else {
					MessageUtil.alerta("O Prontuário digitado é inválido!");
				}
			}
		}
	}
	
	
	/* 
	 * 
	 * Impressão da capa do prontuário 
	 * 
	 * 
	 * */
	
	public void showPDF() {
		
		FacesContext context = FacesContext.getCurrentInstance(); 
	    HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();  
	    response.setContentType("application/pdf");    
	    response.setHeader("Content-disposition",  "inline=filename=file.pdf");
	    
	    try {	        
	    	// Creating a PdfWriter 
	        ByteArrayOutputStream baos = pdfService.generateStream(prontuario, loginBean.getUsuario().getTenant().getS3Key());	        

	        // setting some response headers
	        response.setHeader("Expires", "0");
	        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	        response.setHeader("Pragma", "public");
	        // setting the content type
	        response.setContentType("application/pdf");
	        // the contentlength
	        response.setContentLength(baos.size());
	        // write ByteArrayOutputStream to the ServletOutputStream
	        ServletOutputStream os = response.getOutputStream();
	        
	        baos.writeTo(os);
	        os.flush();
	        os.close();
	    }
	    catch (IOException e) {
	    	log.error("error: "+e);
	    	MessageUtil.erro("Problema na escrita do PDF.");
	    }
	    catch (Exception ex) {
	    	log.error("error: " + ex.getMessage());
	    	MessageUtil.erro("Problema na geração do PDF.");
	    }
	    context.responseComplete();
	    log.info("PDF gerado!");
	}	
	
	
	
	/*
	 * upload file PDF
	 */
 
    public String gravarPdf() throws IOException{
    	
    	try {
	    	//Verifica se foi carregado algum arquivo
			if(getFile() != null) {
				prontuario = s3.gravaPdfCadUnico(prontuario, getFile());
				// grava a chave de acesso ao arquivo no s3
				prontuario = this.cadastroProntuarioService.salvarComPdf(prontuario);
			}
			MessageUtil.sucesso("Pdf CadÚnico gravado com sucesso.");
			
    	} catch (NegocioException e) {  
    		e.printStackTrace();
            MessageUtil.erro(e.getMessage());
	    } catch (Exception e) { 
	    	e.printStackTrace();
	        MessageUtil.erro(e.getMessage());
	    }
    	return "";
    }
    
    public void redirectPdf() throws IOException {

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(prontuario.getUrlAnexo());
    }

	
	
	
	
	
	
	
	public boolean isPessoaReferenciaSelecionada() {

    	if(getPessoaReferencia() != null && getPessoaReferencia().getCodigo() != null) {    		
    		return true;
    	}
        return false;
        		
    }	
	
	public void setPessoaReferencia() {

    	if(getProntuario() != null && getProntuario().getCodigo() != null) {
    		setPessoaReferencia(getProntuario().getFamilia().getPessoaReferencia());
    		mpComposicaoBean.setPessoaReferencia(getPessoaReferencia());
    		setComposicao(true);
    	}        		
    }

	public boolean isEdicao() {
		
		if(prontuario.getFamilia() != null && prontuario.getFamilia().getCodigo() != null) {
			return true;
		}
		return false;
	}
	
	public boolean isCreas() {
		if(loginBean.getUsuario().getUnidade().getTipo() == TipoUnidade.CREAS)
			return true;
		return false;
	}
	
}
