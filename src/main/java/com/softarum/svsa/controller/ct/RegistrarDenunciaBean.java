package com.softarum.svsa.controller.ct;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.softarum.svsa.controller.LoginBean;
import com.softarum.svsa.modelo.PessoaReferencia;
import com.softarum.svsa.modelo.Usuario;
import com.softarum.svsa.modelo.ct.Denuncia;
import com.softarum.svsa.modelo.ct.PessoaDenuncia;
import com.softarum.svsa.modelo.enums.ct.AgenteViolador;
import com.softarum.svsa.modelo.enums.ct.DireitoViolado;
import com.softarum.svsa.modelo.enums.ct.OrigemDenuncia;
import com.softarum.svsa.modelo.enums.ct.Sexo;
import com.softarum.svsa.modelo.enums.ct.Status;
import com.softarum.svsa.service.ct.DenunciaService;
import com.softarum.svsa.util.MessageUtil;
import com.softarum.svsa.util.NegocioException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @author laurojr
 *
 */
@Getter
@Setter
@Named
@ViewScoped
@Log4j
public class RegistrarDenunciaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Denuncia denuncia;
	private List<Denuncia> denuncias = new ArrayList<>();
	private List<AgenteViolador> agentes;
	private List<DireitoViolado> direitos;
	private List<Status> status;
	private List<OrigemDenuncia> origens;
	private List<Sexo> sexos;
	
	private Integer ano;
	private String nome;
	
	@Inject
	private DenunciaService denunciaService;
	
	@Inject
	private LoginBean loginBean;
	
	@PostConstruct
	public void inicializar()  {	
		
		try {
				LocalDate data = LocalDate.now();
				setAno(data.getYear());		
				this.agentes = Arrays.asList(AgenteViolador.values());
				this.direitos = Arrays.asList(DireitoViolado.values());
				this.status = Arrays.asList(Status.values());
				this.origens = Arrays.asList(OrigemDenuncia.values());
				this.sexos = Arrays.asList(Sexo.values());
				
				denuncias = denunciaService.buscarTodos(loginBean.getTenantId());
				limpar();
			}
		
		catch(Exception e){
			log.error("Erro inicializar() Denuncia CT: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void salvar() {
		try {
			
			denuncia = this.denunciaService.salvar(denuncia);			
			
			MessageUtil.sucesso("Denuncia salva com sucesso!");			
		
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
		
		denuncias = denunciaService.buscarTodos(loginBean.getTenantId());
		this.limpar();
	}
	
	public void excluir() {
		try {
			this.denunciaService.excluir(denuncia);
			denuncias = denunciaService.buscarTodos(loginBean.getTenantId());
			MessageUtil.sucesso("Denuncia" + denuncia.getCodigo() + " excluída com sucesso.");
			
			limpar();
				
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void limpar() {		

		this.denuncia = new Denuncia();
		this.denuncia.setAno(getAno());
		this.denuncia.setPessoa(new PessoaDenuncia());
		this.denuncia.setTecnico(loginBean.getUsuario());
		this.denuncia.setStatus(Status.EM_AVERIGUACAO);
		this.denuncia.setUnidade(loginBean.getUsuario().getUnidade());
		this.denuncia.setTenant_id(loginBean.getTenantId());
	}
	
	public List<String> buscarNomes(String query) {
        List<String> results = new ArrayList<>();
        
        try {
			results = denunciaService.buscarNomes(query, loginBean.getTenantId());		
		
			
		} catch(Exception e) {
			MessageUtil.alerta("Não existe PESSOA com esse nome!");
		}        
       
        return results;
    }
	
	public void buscarPessoa() {
	       
        try {
        	PessoaReferencia p = denunciaService.buscarPeloNome(getNome());
        	denuncia.setProntuario(p.getFamilia().getProntuario());
			
		} catch(Exception e) {
			MessageUtil.alerta("Não existe PESSOA com esse nome!");
		}        
	}
}