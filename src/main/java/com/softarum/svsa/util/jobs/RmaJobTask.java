package com.softarum.svsa.util.jobs;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.softarum.svsa.modelo.Unidade;
import com.softarum.svsa.modelo.enums.Mes;
import com.softarum.svsa.modelo.to.rma.RmaCreasTO;
import com.softarum.svsa.modelo.to.rma.RmaPopTO;
import com.softarum.svsa.modelo.to.rma.RmaTO;
import com.softarum.svsa.service.RMAService;
import com.softarum.svsa.service.RelatorioRMACrasService;
import com.softarum.svsa.service.RelatorioRMACreasService;
import com.softarum.svsa.service.RelatorioRMAPopService;
import com.softarum.svsa.service.UnidadeService;

import lombok.extern.log4j.Log4j;

@Log4j
public class RmaJobTask implements Job {
	
	private Integer ano;
	private Mes mes;
	
	private EntityManager manager;
	private EntityTransaction transaction;
	
	private UnidadeService unidadeService;
	private RelatorioRMACrasService rmaCrasService;
	private RelatorioRMACreasService rmaCreasService;
	private RelatorioRMAPopService rmaPopService;
	private RMAService rmaService;
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		// fechamento dos RMAs
		
		definirDatas();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy – hh:mm:ss");
		log.info("Fechando RMAs..." + dateFormat.format( new Date() ));
		
		inicializar();		
		
		fecharRmaCras();
		fecharRmaCreas();
		fecharRmaPop();
		
		finalizar();
	}
	
	private void inicializar() {	
		
		log.info("Inicializando... ");
		
		manager = Persistence.createEntityManagerFactory("svsaPU").createEntityManager();
		transaction = manager.getTransaction();
		
		log.info("EntityManager: " + manager.toString());
		
		unidadeService = new UnidadeService();
		
		rmaCrasService = new RelatorioRMACrasService();
		rmaCreasService = new RelatorioRMACreasService();
		rmaPopService = new RelatorioRMAPopService();
		rmaService = new RMAService();
		
		unidadeService.setManager(manager);
		rmaCrasService.setManager(manager);
		rmaCreasService.setManager(manager);
		rmaPopService.setManager(manager);
		rmaService.setManager(manager);		
	}
	
	private void finalizar() {

		log.info("Finalizando... ");
		
		ano = null;
		mes = null;
		
		unidadeService = null;		
		rmaCrasService = null;
		rmaCreasService = null;
		rmaPopService = null;
		rmaService = null;
		
		transaction = null;
		manager = null;
	}
	
	private void fecharRmaCras() {		
		
		try {
			
			List<Unidade> unidades = unidadeService.buscarCRAS();
			
			for(Unidade uni : unidades) {
				
				log.info("fechando RMA CRAS do mes = " + mes + "  unidade = " + uni.getNome());
				RmaTO rma = rmaCrasService.gerarRelatorioRMA(uni, ano, mes, uni.getTenant_id());
				
				try {
					
					transaction.begin();
					rmaService.salvarCras(rma, uni.getTenant_id());
					transaction.commit();	
					
				} catch (Exception e) {
					e.printStackTrace();
					if (transaction != null) {
						transaction.rollback();
					}
					throw e;
				} finally {
					if (transaction != null && transaction.isActive()) {
						transaction.commit();
					}
				}
				
				log.info("RMA CRAS do mês " + mes + " fechado com sucesso! "  + "  unidade = " + uni.getNome()) ;
			}	
		} catch (Exception e) {
			log.info("Problemas no fechamento dos Rmas CRAS.");
			e.printStackTrace();
		}
	}
	
	private void fecharRmaCreas() {	
		
		try {
			List<Unidade> unidades = unidadeService.buscarCREAS();
			
			for(Unidade uni : unidades) {		
				
				log.info("fechando RMA CREAS do mes = " + mes + "  unidade = " + uni.getNome());
				RmaCreasTO rma = rmaCreasService.gerarRelatorioRMA(uni, ano, mes, uni.getTenant_id());	
				
				try {
					
					transaction.begin();
					rmaService.salvarCreas(rma, uni.getTenant_id());	
					transaction.commit();	
					
				} catch (Exception e) {
					e.printStackTrace();
					if (transaction != null) {
						transaction.rollback();
					}
					throw e;
				} finally {
					if (transaction != null && transaction.isActive()) {
						transaction.commit();
					}
				}		
				
				log.info("RMA CREAS do mês " + mes + " fechado com sucesso! "  + "  unidade = " + uni.getNome()) ;
			}
				
		} catch (Exception e) {
			log.info("Problemas no fechamento dos Rmas CREAS.");
			e.printStackTrace();
		}
	}
	
	private void fecharRmaPop() {	
		
		try {
			List<Unidade> unidades = unidadeService.buscarCREAS();
			
			for(Unidade uni : unidades) {		
				
				log.info("fechando RMA POP do mes = " + mes + "  unidade = " + uni.getNome());
				RmaPopTO rma = rmaPopService.gerarRelatorioRMA(uni, ano, mes, uni.getTenant_id());	
				
				try {
					
					transaction.begin();
					rmaService.salvarPop(rma, uni.getTenant_id());	
					transaction.commit();	
					
				} catch (Exception e) {
					e.printStackTrace();
					if (transaction != null) {
						transaction.rollback();
					}
					throw e;
				} finally {
					if (transaction != null && transaction.isActive()) {
						transaction.commit();
					}
				}		
				
				log.info("RMA POP do mês " + mes + " fechado com sucesso! "  + "  unidade = " + uni.getNome()) ;
			}
				
		} catch (Exception e) {
			log.info("Problemas no fechamento dos Rmas POP.");
			e.printStackTrace();
		}
	}
	
	private void definirDatas() {
		/*
		 * A geração de RMA ocorre todo dia 10 de cada mes subsequente do qual deve ser gerado o RMA
		 */
		LocalDate data = LocalDate.now();
		
		/* se mes for janeiro usa dezembro do ano anterior */
		if(data.getMonthValue() == 1) {
			ano = data.getYear() - 1;
			mes = Mes.porCodigo(12);
		}
		else {
			ano = data.getYear();
			mes = Mes.porCodigo(data.getMonthValue() - 1);			
		}
		
		log.info("Mes/ano fechamento = " + mes.name() + "/" + ano);
	}
}
