package com.softarum.svsa.service.ct;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.softarum.svsa.dao.ct.DenunciaDAO;
import com.softarum.svsa.modelo.Unidade;
import com.softarum.svsa.modelo.ct.Denuncia;
import com.softarum.svsa.modelo.ct.PessoaDenuncia;
import com.softarum.svsa.util.DateUtils;
import com.softarum.svsa.util.NegocioException;

import lombok.extern.log4j.Log4j;


/**
 * @author laurojr
 *
 */
@Log4j
public class DenunciaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DenunciaDAO denunciaDAO;
	
	public Denuncia salvar(Denuncia denuncia) throws NegocioException {
		log.debug("Service : tenant = " + denuncia.getTenant_id());
		
		return this.denunciaDAO.salvar(denuncia);
	}
	
	public void salvarAlterar(Denuncia denuncia, Long codigoUsuarioLogado) throws NegocioException {
		
		log.info("para verificar o problema de alteração de denuncia...criada por: " + denuncia.getTecnico().getCodigo() + " **** Tentativa de alteração por : " + codigoUsuarioLogado);
			
		if(codigoUsuarioLogado.longValue() == denuncia.getTecnico().getCodigo().longValue()) {
			if(new Date().after(DateUtils.plusDays(denuncia.getDataEmissao(), 7)) ){
				throw new NegocioException("Prazo para alteração (7 dias) foi ultrapassado!");
					
			}
			else {
				denunciaDAO.salvar(denuncia);
			}
		}
		else {
			throw new NegocioException("Somente o técnico que registrou pode alterar a denuncia! E isso só pode ser feito antes de 7 dias do registro.");
		}	
	}
	
	public void excluir(Denuncia denuncia) throws NegocioException {
		denunciaDAO.excluir(denuncia);
		
	}
	
/* Buscas */
	
	
	public List<String> buscarNomes(String query, Unidade unidade, Long tenantId) {		
		return denunciaDAO.buscarNomes(query, unidade, tenantId);
	}

	public Denuncia buscarPeloCodigo(long codigo) {
		return denunciaDAO.buscarPeloCodigo(codigo);
	}
	
	// buscar pessoa pelo nome
	public PessoaDenuncia buscarPeloNome(String nome) {
		return denunciaDAO.buscarPeloNome(nome);
	}
	

	public List<Denuncia> buscarTodos(Long tenantId) {
		return denunciaDAO.buscarTodos(tenantId);
	}
	
	public List<Denuncia> buscarTodosDia(Long tenantId, Unidade unidade) {
		return denunciaDAO.buscarTodosDia(tenantId, unidade);
	}
}
