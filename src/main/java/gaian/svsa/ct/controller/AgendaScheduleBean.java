package gaian.svsa.ct.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import gaian.svsa.ct.modelo.ListaAtendimento;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.service.AgendamentoIndividualService;
import gaian.svsa.ct.util.DateUtils;
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
public class AgendaScheduleBean implements Serializable {

	private static final long serialVersionUID = 6570142544541959632L;

	private ScheduleModel eventModel;
	private ScheduleEvent<?> event = new DefaultScheduleEvent<>();
	private List<ListaAtendimento> listaAtendimentos = new ArrayList<>();
	
		
	@Inject
	AgendamentoIndividualService listaAtendimentoService;	
	@Inject
	LoginBean loginBean;
    
 
    @PostConstruct
    public void init() {
    	
    	listaAtendimentos = listaAtendimentoService.buscarAtendimentosAgendados(loginBean.getUsuario().getUnidade(), loginBean.getTenantId());
    	//log.info("Qde agendamentos ind: " + listaAtendimentos.size());
    	
    	eventModel = new DefaultScheduleModel();
    	
    	carregarAgenda();
    }
    
    private void carregarAgenda() {
    	
    	log.debug("Qde agendamentos: " + listaAtendimentos.size());    	
    	
    	for(ListaAtendimento l : listaAtendimentos) { 
            
    		if( l.getRole() == Role.CADASTRADOR ) {    
    			log.debug("cadastrador");

    			DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder()
    					.title("[CAD] " + l.getPessoa().getNome())
    					.startDate(DateUtils.asLocalDateTime(l.getDataAgendamento()))
    					.endDate(DateUtils.asLocalDateTime(l.getDataAgendamento()))    					
    					.borderColor("blue")
    					.backgroundColor("blue")
    					.build();
    			eventModel.addEvent(event);  
    		}
    		else {
    			if(l.getTecnico() != null) {
    				log.debug("com tecnico");
    				event = DefaultScheduleEvent.builder()
        					.title("[IND] " + l.getPessoa().getNome())
        					.startDate(DateUtils.asLocalDateTime(l.getDataAgendamento()))
        					.endDate(DateUtils.asLocalDateTime(l.getDataAgendamento()))   
        					.description(""+l.getTecnico().getNome())
        					.borderColor("blue")
        					.backgroundColor("blue")
        					.build();    				
    				eventModel.addEvent(event);  
    				//eventModel.addEvent(new DefaultScheduleEvent("[IND] " + l.getPessoa().getNome() + " (" + l.getTecnico().getNome() + ")", l.getDataAgendamento(), l.getDataAgendamento()));
    			}
    			else {
    				log.debug("sem tecnico");
    				event = DefaultScheduleEvent.builder()
        					.title("[IND] " + l.getPessoa().getNome())
        					.startDate(DateUtils.asLocalDateTime(l.getDataAgendamento()))
        					.endDate(DateUtils.asLocalDateTime(l.getDataAgendamento()))   
        					.borderColor("blue")
        					.backgroundColor("blue")
        					.build();
    				eventModel.addEvent(event);     				
    			}
    		}    		 		
    	}    	
    }
    
   
	
	public void onEventSelect(SelectEvent<?> selectEvent) {
       setEvent((DefaultScheduleEvent<?>) selectEvent.getObject());
	}

}