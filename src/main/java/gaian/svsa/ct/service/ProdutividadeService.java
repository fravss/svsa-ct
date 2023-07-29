package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import gaian.svsa.ct.dao.ProdutividadeDAO;
import gaian.svsa.ct.dao.UsuarioDAO;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.to.AtendimentoDTO;
import gaian.svsa.ct.modelo.to.AtendimentoTO;

/**
 * @author murakamiadmin
 *
 */
public class ProdutividadeService implements Serializable {

	private static final long serialVersionUID = 1L;	
	private Logger log = Logger.getLogger(ProdutividadeService.class);	
	
	private Integer totalAtendimentos = 0;
	private Float mediaUnidade = 0.0f;
	
	private Long totalGeral = 0L;
	private Float mediaGeral = 0.0f;
	
	@Inject
	private ProdutividadeDAO prodDAO;	
	@Inject
	private UsuarioDAO usuarioDAO;	
		
	
	public List<AtendimentoTO> buscarAtendCaUnicoDTO(Date ini, Date fim, Long tenantId) {	
		
		/* atendimentos cadunico por conselheiro*/
		List<AtendimentoDTO> dtos;		
		
		if(ini != null) {
			if(fim != null)
				dtos = prodDAO.buscarCadUnicoProdDTO(ini, fim, tenantId);
			else
				dtos = prodDAO.buscarCadUnicoProdDTO(ini, new Date(), tenantId);
		}
		else
			dtos = prodDAO.buscarCadUnicoProdDTO(tenantId);
					
		return carregarTO(dtos);
	}
		
	
	public List<AtendimentoTO> buscarAtendConselheiroDTO(Date ini, Date fim, Unidade unidade, Long tenantId) {
		
		/* atendimentos por conselheiro*/
		List<AtendimentoDTO> dtos;			
		
		if(ini != null) {
			if(fim != null) {
				dtos = prodDAO.buscarConselheiroProdDTO(ini, fim, unidade, tenantId);
				totalGeral = prodDAO.buscarTotalGeral(ini, fim, tenantId);
			}
			else {
				dtos = prodDAO.buscarConselheiroProdDTO(ini, new Date(), unidade, tenantId);
				totalGeral = prodDAO.buscarTotalGeral(ini, new Date(), tenantId);				
			}
			
			//mediaGeral = (float)totalGeral / usuarioDAO.buscarTotalConselheirosUnid(unidade);
		}
		else {
			dtos = prodDAO.buscarConselheiroProdDTO(unidade, tenantId);
			totalGeral = prodDAO.buscarTotalGeral(tenantId);
			//mediaGeral = (float)totalGeral / usuarioDAO.buscarTotalConselheiros();
		}
		
		mediaGeral = (float)totalGeral / usuarioDAO.buscarTotalConselheiros(tenantId);
			
		return carregarTO(dtos);
	}
	
	public List<AtendimentoTO> buscarAcoesConselheiroDTO(Date ini, Date fim, Unidade unidade, Long tenantId) {
			
			/* acoes por conselheiro*/
			List<AtendimentoDTO> dtos;			
			
			if(ini != null) {
				if(fim != null) {
					dtos = prodDAO.buscarConselheiroProdAcoesDTO(ini, fim, unidade, tenantId);
					totalGeral = prodDAO.buscarTotalGeralAcao(ini, fim, tenantId);
				}
				else {
					dtos = prodDAO.buscarConselheiroProdAcoesDTO(ini, new Date(), unidade, tenantId);
					totalGeral = prodDAO.buscarTotalGeralAcao(ini, new Date(), tenantId);				
				}
				
				//mediaGeral = (float)totalGeral / usuarioDAO.buscarTotalConselheirosUnid(unidade);
			}
			else {
				dtos = prodDAO.buscarConselheiroProdAcoesDTO(unidade, tenantId);
				totalGeral = prodDAO.buscarTotalGeralAcao(tenantId);
				//mediaGeral = (float)totalGeral / usuarioDAO.buscarTotalConselheiros();
			}
			
			mediaGeral = (float)totalGeral / usuarioDAO.buscarTotalConselheiros(tenantId);
					
			return carregarTO(dtos);
		}
	
	
	
	private List<AtendimentoTO>	carregarTO(List<AtendimentoDTO> dtos){
			
			log.info("Qde total atendimentos = " + dtos.size());			
			
			List<AtendimentoTO> tos = new ArrayList<>();		
			long qde = 0L;
			
			int tamanho = dtos.size();
			AtendimentoDTO atendDTO = dtos.get(0);
			
			if( tamanho > 0 ) {
				AtendimentoTO to = new AtendimentoTO(atendDTO.getNomeConselheiro(),atendDTO.getNomeUnidade(), atendDTO.getRole());
				
				for(AtendimentoDTO dto : dtos) {
					
					if(to.getNome().equals(dto.getNomeConselheiro())) {
						qde++;
					}
					else {
						to.setQdeAtendimentos(qde);
						to.setPercentual((float)qde/tamanho);			
						tos.add(to);
						to = new AtendimentoTO( dto.getNomeConselheiro(), dto.getNomeUnidade(), dto.getRole() );
						qde = 1L;
					}
				}
				to.setQdeAtendimentos(qde);	
				to.setPercentual((float)qde/tamanho);			
				tos.add(to);	
				
			}
			
			setTotalAtendimentos(dtos.size());
			setMediaUnidade((float)getTotalAtendimentos()/tos.size());
			return tos;
		}


	public Integer getTotalAtendimentos() {
		return totalAtendimentos;
	}


	public void setTotalAtendimentos(Integer totalAtendimentos) {
		this.totalAtendimentos = totalAtendimentos;
	}

	public Float getMediaUnidade() {
		return mediaUnidade;
	}


	public void setMediaUnidade(Float mediaUnidade) {
		this.mediaUnidade = mediaUnidade;
	}


	public Long getTotalGeral() {
		return totalGeral;
	}


	public void setTotalGeral(Long totalGeral) {
		this.totalGeral = totalGeral;
	}


	public Float getMediaGeral() {
		return mediaGeral;
	}


	public void setMediaGeral(Float mediaGeral) {
		this.mediaGeral = mediaGeral;
	}


	
}
