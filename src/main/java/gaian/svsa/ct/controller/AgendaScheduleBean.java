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

import gaian.svsa.ct.modelo.AgendamentoFamiliar;
import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.service.AgendamentoFamiliarService;
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
	private List<Atendimento> listaAtendimentos = new ArrayList<>();
	private List<AgendamentoFamiliar> familiares = new ArrayList<>();
	
		
	@Inject
	AgendamentoIndividualService atendimentoService;
	@Inject
	private AgendamentoFamiliarService familiarService;
	@Inject
	LoginBean loginBean;
    
 
    @PostConstruct
    public void init() {
    	
    	listaAtendimentos = atendimentoService.buscarAtendimentosAgendados(loginBean.getUsuario().getUnidade(), loginBean.getTenantId());
    	//log.info("Qde agendamentos ind: " + listaAtendimentos.size());
    	familiares = familiarService.buscarAtendimentosAgendados(loginBean.getUsuario().getUnidade(), loginBean.getTenantId());

    	eventModel = new DefaultScheduleModel();
    	
    	carregarAgenda();
    	carregarAgendaFamiliar();
    }
    
    private void carregarAgenda() {
    	
    	log.debug("Qde agendamentos: " + listaAtendimentos.size());    	
    	
    	for(Atendimento l : listaAtendimentos) { 
            
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
    			if(l.getConselheiro() != null) {
    				log.debug("com conselheiro");
    				event = DefaultScheduleEvent.builder()
        					.title("[IND] " + l.getPessoa().getNome())
        					.startDate(DateUtils.asLocalDateTime(l.getDataAgendamento()))
        					.endDate(DateUtils.asLocalDateTime(l.getDataAgendamento()))   
        					.description(""+l.getConselheiro().getNome())
        					.borderColor("blue")
        					.backgroundColor("blue")
        					.build();    				
    				eventModel.addEvent(event);  
    				//eventModel.addEvent(new DefaultScheduleEvent("[IND] " + l.getPessoa().getNome() + " (" + l.getConselheiro().getNome() + ")", l.getDataAgendamento(), l.getDataAgendamento()));
    			}
    			else {
    				log.debug("sem conselheiro");
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
    
private void carregarAgendaFamiliar() {
    	
    	log.debug("Qde agendamentos familiar: " + familiares.size());
    	
    	for(AgendamentoFamiliar a : familiares) { 
    		
    		log.debug("hora fam db " + a.getDataAgendamento());
    		
    		if(a.getConselheiro() != null) {
				log.debug("com tecnico");
				event = DefaultScheduleEvent.builder()
    					.title("[FAM] " + a.getPessoas().get(0).getNome())
    					.startDate(DateUtils.asLocalDateTime(a.getDataAgendamento()))
    					.endDate(DateUtils.asLocalDateTime(a.getDataAgendamento()))   
    					.description("" + a.getConselheiro().getNome())
    					.borderColor("green")
    					.backgroundColor("green")
    					.build(); 
				eventModel.addEvent(event);  				
			}
			else {
				log.debug("sem tecnico");    			
				event = DefaultScheduleEvent.builder()
    					.title("[FAM] " + a.getPessoas().get(0).getNome())
    					.startDate(DateUtils.asLocalDateTime(a.getDataAgendamento()))
    					.endDate(DateUtils.asLocalDateTime(a.getDataAgendamento()))   
    					.borderColor("green")
    					.backgroundColor("green")
    					.build(); 
				eventModel.addEvent(event);  
			}
    	}    	
    }

    
   
	
	public void onEventSelect(SelectEvent<?> selectEvent) {
       setEvent((DefaultScheduleEvent<?>) selectEvent.getObject());
	}

}