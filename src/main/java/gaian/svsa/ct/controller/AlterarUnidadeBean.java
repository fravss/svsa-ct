package gaian.svsa.ct.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.Uf;
import gaian.svsa.ct.modelo.to.EnderecoTO;
import gaian.svsa.ct.service.UnidadeService;
import gaian.svsa.ct.service.rest.BuscaCEPService;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author murakamiadmin
 *
 */
@Getter
@Setter
@Named
@ViewScoped
public class AlterarUnidadeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Unidade unidade;
	private List<Uf> ufs;
	private EnderecoTO enderecoTO;	
	
	@Inject
	private UnidadeService unidadeService;
	@Inject
	private BuscaCEPService buscaCEPService;	
	@Inject
	private LoginBean usuarioLogado;
	
	@PostConstruct
	public void inicializar() {
		unidade = usuarioLogado.getUsuario().getUnidade();
		this.ufs = Arrays.asList(Uf.values());
	}
	
	/* AlterarUnidade.xhtml */
	public void salvarAlteracao() {
		try {
			unidade.getEndereco().setMunicipio(unidade.getEndereco().getMunicipio());
			this.unidadeService.salvar(unidade);
			MessageUtil.sucesso("Unidade alterada com sucesso!");
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}	
	}
		
	public void buscaEnderecoPorCEP() {
		
        try {
			enderecoTO  = buscaCEPService.buscaEnderecoPorCEP(unidade.getEndereco().getCep());
			
			/*
	         * Preenche o Endereco com os dados buscados
	         */	 
			//unidade.getEndereco().setCep(cep);
			unidade.getEndereco().setEndereco(enderecoTO.getTipoLogradouro().
	        		                concat(" ").concat(enderecoTO.getLogradouro()));
			unidade.getEndereco().setBairro(enderecoTO.getBairro());
			unidade.getEndereco().setMunicipio(enderecoTO.getCidade());
			unidade.getEndereco().setUf(enderecoTO.getEstado());
	        
	        if (enderecoTO.getResultado() != 1) {
	        	MessageUtil.erro("Endereço não encontrado para o CEP fornecido.");		            
	        }
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());		            
		}       
	}
	
}
