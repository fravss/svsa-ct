package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.dao.AgendamentoIndividualDAO;
import gaian.svsa.ct.dao.EncaminhamentoDAO;
import gaian.svsa.ct.dao.UnidadeDAO;
import gaian.svsa.ct.modelo.Encaminhamento;
import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.modelo.Oficio;
import gaian.svsa.ct.modelo.OficioEmitido;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.enums.CodigoAuxiliarAtendimento;
import gaian.svsa.ct.modelo.enums.TipoUnidade;
import gaian.svsa.ct.modelo.to.AtendimentoDTO;
import gaian.svsa.ct.modelo.to.AtendimentoTO;

/**
 * Classe criada para desonerar a classe ListaAtendimentoService Especificamente
 * para relatórios. Essa classe só pode ser utilizada pelo
 * ListaAtendimentoService
 * 
 * @author murakamiadmin
 *
 */
class AgendamentoIndividualHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(AgendamentoIndividualHelper.class);

	@Inject
	private AgendamentoIndividualDAO listaDAO;
	@Inject
	private UnidadeDAO unidadeDAO;
	@Inject
	private EncaminhamentoDAO encDAO;
	@Inject
	AcaoService acaoService;
	@Inject
	OficioService oficioService;
	@Inject
	OficioEmitidoService oficioEmitidoService;
	@Inject
	private LoginBean loginBean;

	/*
	 * RelatorioAtendimentos (cadUnico)
	 */
	public List<Atendimento> buscarAtendCadUnicoPeriodo(Unidade unidade, Date ini, Date fim, Long tenantId) {
		if (ini != null)
			if (fim != null)
				return listaDAO.buscarAtendCadUnicoDataPeriodo(unidade, ini, fim, tenantId);
			else
				return listaDAO.buscarAtendCadUnicoDataPeriodo(unidade, ini, new Date(), tenantId);
		return listaDAO.buscarAtendidosCadUnico(unidade, tenantId);
	}

	public List<Atendimento> buscarAtendCadUnicoPeriodo2(Unidade unidade, Date ini, Date fim, Long tenantId) {
		if (ini != null)
			if (fim != null)
				return listaDAO.buscarAtendCadUnicoDataPeriodo2(unidade, ini, fim, tenantId);
			else
				return listaDAO.buscarAtendCadUnicoDataPeriodo2(unidade, ini, new Date(), tenantId);
		return listaDAO.buscarAtendidosCadUnico2(unidade, tenantId);
	}

	/*
	 * RelatorioAtendimentos (gestão)
	 */

	private Long buscar(CodigoAuxiliarAtendimento c, Unidade unidade, Date ini, Date fim, Long tenantId) {
		if (ini != null)
			if (fim != null)
				return listaDAO.buscarQdeAtendimentoCodAux(c, unidade, ini, fim, tenantId);
			else
				return listaDAO.buscarQdeAtendimentoCodAux(c, unidade, ini, new Date(), tenantId);
		return listaDAO.buscarQdeAtendimentoCodAux(c, unidade, tenantId);
	}
	private Long buscar(CodigoAuxiliarAtendimento c, Long tenantId) {			
			
		return listaDAO.buscarQdeAtendimentoCodAux(c, tenantId);
	}

	public List<AtendimentoTO> relatorioAtendimentosTOCodAux(Unidade unidade, Date ini, Date fim, Long tenantId) {

		/*
		 * Recuperar codigos auxiliares e para cada codigo buscar a quantidade de
		 * atendimentos da unidade
		 * 
		 */
		List<CodigoAuxiliarAtendimento> codigos = Arrays.asList(CodigoAuxiliarAtendimento.values());

		List<AtendimentoTO> listaTO = new ArrayList<>();

		for (CodigoAuxiliarAtendimento c : codigos) {
			AtendimentoTO to = new AtendimentoTO();
			to = new AtendimentoTO();
			to.setNome(c.name());
			to.setQdeAtendimentos(buscar(c, unidade, ini, fim, tenantId));
			if(to.getQdeAtendimentos() > 0) {
				listaTO.add(to);
			}			
		}

		return listaTO;
	}
	public List<AtendimentoTO> relatorioAtendimentosTOCodAux(Long tenantId) {

		/*
		 * Recuperar codigos auxiliares e para cada codigo buscar a quantidade de
		 * atendimentos da unidade
		 * 
		 */
		List<CodigoAuxiliarAtendimento> codigos = Arrays.asList(CodigoAuxiliarAtendimento.values());

		List<AtendimentoTO> listaTO = new ArrayList<>();

		for (CodigoAuxiliarAtendimento c : codigos) {
			AtendimentoTO to = new AtendimentoTO();
			to = new AtendimentoTO();
			to.setNome(c.name());
			to.setQdeAtendimentos(buscar(c, tenantId));
			if(to.getQdeAtendimentos() > 0) {
				listaTO.add(to);
			}
		}

		return listaTO;
	}

	public List<AtendimentoTO> relatorioAtendimentosTO(Long tenantId) {

		/*
		 * Recuperar unidades e para cada unidade buscar a quantidade de atendimentos
		 * 
		 */
		List<Unidade> unidades = unidadeDAO.buscarTodos(loginBean.getTenantId());

		List<AtendimentoTO> listaTO = new ArrayList<>();

		for (Unidade u : unidades) {

			if (u.getTipo() != TipoUnidade.SASC) {
				AtendimentoTO to = new AtendimentoTO();
				to = new AtendimentoTO();
				to.setNome(u.getNome());
				to.setQdeAtendimentos(listaDAO.buscarQdeAtendimentoUnidade(u, tenantId));
				listaTO.add(to);
			}
		}

		return listaTO;
	}
	
	
	

	/*
	 * ------- HISTÓRICO PESSOA ---------------------------
	 * Recuperação de toda evolução da Pessoa
	 * ---------------------------------------------------
	 */	
	
	
	
	public List<AtendimentoDTO> buscarResumoAtendimentosDTO(Pessoa pessoa, Long tenantId) {

		/*
		 * ================================================= 
		 * Buscar Atendimentos individualizados e Faltas
		 * =================================================
		 */
		List<AtendimentoDTO> atendimentos = listaDAO.buscarResumoAtendimentosDTO(pessoa, tenantId);
		log.debug("Qde atendimentos individualizados : (" + pessoa.getCodigo() + "-" + pessoa.getNome() + ") = "
				+ atendimentos.size());

		/*
		 * =================================== 
		 * Buscar Ações
		 * ===================================
		 */
		List<AtendimentoDTO> atendimentosAcoes = acaoService.buscarAcoes(pessoa, tenantId);
		log.debug("Qde ações : (" + pessoa.getCodigo() + "-" + pessoa.getNome() + ") = "
				+ atendimentosAcoes.size());
		atendimentos.addAll(atendimentosAcoes);

		
		/*
		 * ========================================== 
		 * Buscar Encaminhamentos Outros (externos)
		 * ==========================================
		 */
		List<Encaminhamento> encaminhamentos = encDAO.buscarEncaminhamentos(pessoa, tenantId);
		log.debug("Qde encaminhamentos da pessoa : (" + pessoa.getCodigo() + "-" + pessoa.getNome() + ") = "
				+ encaminhamentos.size());
		if (!encaminhamentos.isEmpty()) {
			for (Encaminhamento e : encaminhamentos) {
				AtendimentoDTO dto = new AtendimentoDTO();

				dto.setData(e.getData());

				dto.setResumoAtendimento("[Enc.Externo] PARA: " + e.getOrgaoUnidadeDestino()  + " - MOTIVO: " + e.getMotivo());
				if (e.getConselheiro() != null)
					dto.setNomeTecnico(e.getConselheiro().getNome());
				dto.setNomeUnidade(e.getConselheiro().getUnidade().getNome());
				dto.setNomePessoa(e.getPessoa().getNome());
				atendimentos.add(dto);
			}
		}

		/*
		 * ========================================== 
		 * Buscar ofícios Recebidos/respondidos 
		 * ==========================================
		 */
		List<Oficio> oficios = oficioService.buscarOficiosHist(pessoa, tenantId);
		log.debug("Qde oficios recebidos/emitidos da pessoa : (" + pessoa.getCodigo() + "-" + pessoa.getNome() + ") = "
				+ oficios.size());

		if (!oficios.isEmpty()) {

			for (Oficio o : oficios) {

				AtendimentoDTO dto, dto2 = null;

				if (o.getDataResposta() != null) {

					// oficio recebido
					dto = new AtendimentoDTO();
					dto.setData(o.getDataRecebimento());
					if (o.getConselheiro() != null) {
						dto.setNomeTecnico(o.getConselheiro().getNome()); // coordenador que recebeu
						dto.setNomeUnidade(o.getConselheiro().getUnidade().getNome()); // unidade do coordenador
					}
					dto.setNomePessoa(o.getPessoa().getNome());
					dto.setResumoAtendimento("[Ofício Recebido] " + o.getNrOficio() + " ( " + o.getAssunto() + " ) ");
					atendimentos.add(dto);

					// oficio respondido
					dto2 = new AtendimentoDTO();
					dto2.setData(o.getDataResposta());
					dto2.setNomeTecnico(o.getConselheiro().getNome());
					dto2.setNomeUnidade(o.getConselheiro().getUnidade().getNome());
					dto2.setNomePessoa(o.getPessoa().getNome());
					dto2.setResumoAtendimento(
							"[Ofício Resposta] " + o.getNrOficioResp() + " ( " + o.getAssunto() + " ) ");
					atendimentos.add(dto2);
				} else {

					// oficio recebido
					dto = new AtendimentoDTO();
					dto.setData(o.getDataRecebimento());
					if (o.getConselheiro() != null) {
						dto.setNomeTecnico(o.getConselheiro().getNome()); // coordenador que recebeu
						dto.setNomeUnidade(o.getConselheiro().getUnidade().getNome()); // unidade do coordenador
					}
					dto.setNomePessoa(o.getPessoa().getNome());
					dto.setResumoAtendimento("[Ofício Recebido] " + o.getNrOficio() + " ( " + o.getAssunto() + " ) ");
					atendimentos.add(dto);
				}
			}
		}

		
		
		/*
		 * ========================================== 
		 * Buscar ofícios Emitidos
		 * ==========================================
		 */
		List<OficioEmitido> oficiosEmitidos = oficioEmitidoService.buscarOficiosEmitidosHist(pessoa, tenantId);

		log.debug("Qde oficios emitidos da pessoa : (" + pessoa.getCodigo() + "-" + pessoa.getNome() + ") = "
				+ oficiosEmitidos.size());

		if (!oficiosEmitidos.isEmpty()) {

			for (OficioEmitido o : oficiosEmitidos) {

				AtendimentoDTO dto = new AtendimentoDTO();
				dto.setData(o.getDataEmissao());
				if (o.getConselheiro() != null)
					dto.setNomeTecnico(o.getConselheiro().getNome());
				// emitidos
				dto.setResumoAtendimento("[Ofício Emitido] " + o.getNrOficioEmitido() + " ( " + o.getAssunto() + " ) ");
				dto.setNomeUnidade(o.getConselheiro().getUnidade().getNome());
				dto.setNomePessoa(o.getPessoa().getNome());
				atendimentos.add(dto);
			}
		}
		
		
				
		
		/* ordem descendente por data 		*/
        
		Collections.sort(atendimentos, new Comparator<AtendimentoDTO>() {
			  public int compare(AtendimentoDTO o2, AtendimentoDTO o1) {
			      if (o1.getData() == null || o2.getData() == null)
			        return 0;
			      return o1.getData().compareTo(o2.getData());
			  }
			});

		
		/* ordem descendente por data com lambda */
		//atendimentos.sort((a, b) -> a.compareTo(b));	
		
		//atendimentos.sort(Comparator.comparing(AtendimentoDTO::getData).reversed());

		//atendimentos.forEach(System.out::println);
		
		//retorna evolução ordenada
		return atendimentos;
	}

	
	
	/*
	 * ------- FIM HISTÓRICO PESSOA ---------------------------	
	 */	
	
	

	public List<Atendimento> consultaFaltas(Pessoa pessoa, Long tenantId) {
		
		// faltas individualizada
		List<Atendimento> faltas = listaDAO.consultaFaltas(pessoa.getFamilia().getDenuncia().getUnidade(), pessoa, tenantId);
	
		return  faltas;
	}

}
