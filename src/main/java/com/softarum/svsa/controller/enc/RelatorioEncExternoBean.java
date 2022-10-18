package com.softarum.svsa.controller.enc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.softarum.svsa.controller.LoginBean;
import com.softarum.svsa.modelo.Encaminhamento;
import com.softarum.svsa.modelo.Pessoa;
import com.softarum.svsa.modelo.Unidade;
import com.softarum.svsa.service.EncaminhamentoService;
import com.softarum.svsa.service.PessoaService;
import com.softarum.svsa.service.UnidadeService;
import com.softarum.svsa.util.MessageUtil;

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
@Named
@ViewScoped
public class RelatorioEncExternoBean implements Serializable {

	private static final long serialVersionUID = 1769116747361287180L;

	private int qdeTotal = 0;
	private List<Encaminhamento> listaEncaminhamentos = new ArrayList<>();
	private List<Unidade> unidades = new ArrayList<>();	
	private Encaminhamento encaminhamento;
	private Encaminhamento itemMigrar;
	private Long codigoPessoa;
	private String nomePessoa;
	private Pessoa pessoa = null;

	private Unidade unidade;	
	private Date dataInicio;
	private Date dataFim;
	
	
	@Inject
	private EncaminhamentoService encaminhamentoService;
	@Inject
	private UnidadeService unidadeService;
	@Inject
	private LoginBean loginBean;
	@Inject
	PessoaService pessoaService;
		
	
	@PostConstruct
	public void inicializar() {	
		unidades = unidadeService.buscarTodos(loginBean.getTenantId());
		this.unidade = loginBean.getUsuario().getUnidade();
	}	

	public void consultarEncaminhamentos() {

		listaEncaminhamentos = encaminhamentoService.buscarEncaminhamentos(unidade, dataInicio, dataFim, loginBean.getTenantId());
	
		qdeTotal = listaEncaminhamentos.size();	
		
	}
	
	/* Migração de encaminhamento */
	
	public void buscarNomePessoa() {
		try {
			pessoa = pessoaService.buscarPessoa(codigoPessoa, unidade, loginBean.getTenantId());		
			
			if(pessoa != null) {
				setNomePessoa(pessoa.getNome());
				log.info("codigo pessoa: " + pessoa.getCodigo());
				log.info("nome pessoa: " + getNomePessoa());
			}
			else{
				limparPessoa();
			}
		} catch(Exception e) {
			limparPessoa();
			MessageUtil.erro("Não existe PESSOA com esse código!");
		}
	}
	
	private void limparPessoa() {
		codigoPessoa = null;
		nomePessoa = null;
	}
	
	public void migrarEncaminhamento(){
		try {
			
			if (pessoa != null) {
				
					itemMigrar.setPessoa(pessoa);
					itemMigrar = encaminhamentoService.salvar(itemMigrar);
					log.info("Atendimento MIGRADO para a pessoa " + itemMigrar.getPessoa().getNome());

					limparPessoa();

					MessageUtil.sucesso("Atendimento MIGRADO para a pessoa " + itemMigrar.getPessoa().getNome());
				}
			else {
				MessageUtil.erro("Essa PESSOA não existe ou é de outra unidade!");
			}

		}
		catch (Exception e) {
			MessageUtil.erro("Essa PESSOA não existe ou é de outra unidade!");
		}
	}
	
	//Teste para saber se a unidade selecionada é a mesma do técnico para permitir ou não a migração
	public boolean isMesmaUnidade() {
		if(unidade.equals(loginBean.getUsuario().getUnidade()))
			return true;
			
		return false;
	}
	
	public boolean isUnidadeSelecionada() {
		if(unidade != null)
			return true;
		return false;
	}

}