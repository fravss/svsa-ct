package gaian.svsa.ct.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.primefaces.model.ScheduleEvent;

import gaian.svsa.ct.dao.CalendarioDAO;
import gaian.svsa.ct.modelo.Calendario;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.util.NegocioException;

/**
 * @author murakamiadmin
 *
 */
public class CalendarioService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(CalendarioService.class);
	
	@Inject
	private CalendarioDAO calendarioDAO;


	public Calendario salvar(ScheduleEvent<?> event, Unidade unidade, Long tenantId) throws NegocioException {
		
		Calendario calendario;
		
		if(((Calendario)event.getData()).getConselheiro() != null) {
			calendario = new Calendario(event.getTitle(),
					event.getStartDate(),
					event.getEndDate(), 
					((Calendario)event.getData()).getConselheiro(),  unidade);
			log.info("event.getData() COM conselheiro = " + ((Calendario)event.getData()).getConselheiro().getNome()); 
		}
		else {
			calendario = new Calendario(event.getTitle(),
					event.getStartDate(),
					event.getEndDate(), 
					null,  unidade);
			log.info("event.getData() SEM conselheiro = ");
		}
		calendario.setTenant_id(tenantId);

		return this.calendarioDAO.merge(calendario);
	}
	
	public void atualizar(ScheduleEvent<?> event) throws NegocioException {
		
		//log.info("evento atz service " + ((Calendario)event.getData()).getCodigo());
		
		Calendario calendario = calendarioDAO.buscarPeloCodigo( ((Calendario)event.getData()).getCodigo() );
		
		if(((Calendario)event.getData()).getConselheiro() != null) {
					
			
			calendario.setConselheiro( ((Calendario)event.getData()).getConselheiro() );
			
			log.info("event.getData() conselheiro = " + ((Calendario)event.getData()).getConselheiro().getNome()); 
		}
		else {
			if(calendario.getConselheiro() != null) {
				throw new NegocioException("Não é permitido converter para feriado!");
			}
			calendario.setTitle(event.getTitle());
			calendario.setConselheiro( null );			
		}		
		
		calendario.setStartDate(event.getStartDate());
		calendario.setEndDate(event.getEndDate());
		
		//log.info("calendario recuperado " + calendario.getCodigo() + " end " + calendario.getEndDate()); 
		this.calendarioDAO.merge(calendario);
	}
	
	public void excluir(ScheduleEvent<?> event) throws NegocioException {
		Calendario calendario = calendarioDAO.buscarPeloCodigo( ((Calendario)event.getData()).getCodigo());
		calendarioDAO.excluir(calendario);		
	}

	public Calendario buscarPeloCodigo(Long codigo) {
		return calendarioDAO.buscarPeloCodigo(codigo);
	}	

	public List<Calendario> buscarTodos(Unidade unidade, Long tenantId) throws ParseException {
		return calendarioDAO.buscarTodos(unidade, tenantId);
	}
		

	public CalendarioDAO getCalendarioDAO() {
		return calendarioDAO;
	}
}