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
	
	// verifica disponibilidade do tecnico
	public void verificarDisponibilidade(Usuario tecnico, Date data, Long tenantId) throws NegocioException {
		
		log.debug("Verificando disponibilidades...feriados, folgas, data ocupada e tecnico na data");
		verificarDisponibilidade(tecnico.getUnidade(), data, tenantId);

		// verifica se técnico está de folga ou ferias
		calendarioDAO.verificaDataTecnico(data, tecnico, tenantId);
		
		// verifica se tecnico está ocupado
		calendarioDAO.verificaAgendaTecnico(data, tecnico, tenantId);
	}

	public void verificarDisponibilidade(Unidade unidade, Date data, Long tenantId) throws NegocioException {
		
		log.debug("Verificando disponibilidades...feriados, folgas, data ocupada e tecnico na data. unidade = " + unidade);		
	
		// verifica se é feriado ou dia ocupado
		calendarioDAO.verificaDataFeriados(data, unidade, tenantId);		
	}	
}