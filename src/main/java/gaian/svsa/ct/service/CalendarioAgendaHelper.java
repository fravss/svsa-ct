package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import gaian.svsa.ct.dao.CalendarioDAO;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.util.NegocioException;
import lombok.extern.log4j.Log4j;

/**
 * @author murakamiadmin
 *
 */
@Log4j
public class CalendarioAgendaHelper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private CalendarioDAO calendarioDAO;
	
	// verifica disponibilidade do conselheiro
	public void verificarDisponibilidade(Usuario conselheiro, Date data, Long tenantId) throws NegocioException {
		
		log.debug("Verificando disponibilidades...feriados, folgas, data ocupada e conselheiro na data");
		verificarDisponibilidade(conselheiro.getUnidade(), data, tenantId);

		// verifica se técnico está de folga ou ferias
		calendarioDAO.verificaDataConselheiro(data, conselheiro, tenantId);
		
		// verifica se conselheiro está ocupado
		calendarioDAO.verificaAgendaConselheiro(data, conselheiro, tenantId);
	}

	public void verificarDisponibilidade(Unidade unidade, Date data, Long tenantId) throws NegocioException {
		
		log.debug("Verificando disponibilidades...feriados, folgas, data ocupada e conselheiro na data. unidade = " + unidade);		
	
		// verifica se é feriado ou dia ocupado
		calendarioDAO.verificaDataFeriados(data, unidade, tenantId);		
	}	
}