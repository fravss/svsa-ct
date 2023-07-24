package gaian.svsa.ct.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import gaian.svsa.ct.modelo.Endereco;
import gaian.svsa.ct.modelo.Orgao;
import gaian.svsa.ct.modelo.enums.CodigoEncaminhamento;
import gaian.svsa.ct.modelo.enums.Uf;
import gaian.svsa.ct.modelo.to.EnderecoTO;
import gaian.svsa.ct.service.OrgaoService;
import gaian.svsa.ct.service.rest.BuscaCEPService;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Talita
 *
 */
@Getter
@Setter
@Named
@ViewScoped
public class CadastroOrgaoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Orgao orgao;
	private List<Uf> ufs;
	private EnderecoTO enderecoTO;
	private List<CodigoEncaminhamento> codigos;
	
	@Inject
	private OrgaoService orgaoService;
	@Inject
	private BuscaCEPService buscaCEPService;
	@Inject
	private LoginBean loginBean;
	
	@PostConstruct
	public void inicializar() {
		this.limpar();
		this.ufs = Arrays.asList(Uf.values());
		this.codigos = Arrays.asList(CodigoEncaminhamento.values());
	}
	
	public void salvar() {
		try {
			this.orgaoService.salvar(orgao);
			MessageUtil.sucesso("Orgão salvo com sucesso!");
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
		
		this.limpar();
	}
	
	public void limpar() {
		this.orgao = new Orgao();
		this.orgao.setEndereco(new Endereco());
		this.orgao.setTenant_id(loginBean.getTenantId());
	}
	
	public void buscaEnderecoPorCEP() {
		
        try {
			enderecoTO  = buscaCEPService.buscaEnderecoPorCEP(orgao.getEndereco().getCep());
			
			/*
	         * Preenche o Endereco com os dados buscados
	         */	 
			//orgao.getEndereco().setCep(cep);
			orgao.getEndereco().setEndereco(enderecoTO.getTipoLogradouro().
	        		                concat(" ").concat(enderecoTO.getLogradouro()));
			orgao.getEndereco().setBairro(enderecoTO.getBairro());
			orgao.getEndereco().setMunicipio(enderecoTO.getCidade());
			orgao.getEndereco().setUf(enderecoTO.getEstado());
	        
	        if (enderecoTO.getResultado() != 1) {
	        	MessageUtil.erro("Endereço não encontrado para o CEP fornecido.");		            
	        }
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());		            
		}       
	}	 
}
