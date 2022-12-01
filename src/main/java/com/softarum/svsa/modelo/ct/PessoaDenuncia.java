package com.softarum.svsa.modelo.ct;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.softarum.svsa.modelo.Unidade;
import com.softarum.svsa.modelo.enums.ct.Sexo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

/**
 * @author laurojr
 *
 */
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Log4j
@NamedQueries({
	@NamedQuery(name="PessoaDenuncia.buscarTodos", query="select pd from PessoaDenuncia pd where pd.tenant_id = :tenantId")
})

public class PessoaDenuncia implements Cloneable, Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EqualsAndHashCode.Include
	@ToString.Include
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;
	
	private String nomeCrianca;
	
	private String nomeMae;
	
	private String nomePai;
	
	private String nomeTerceiro;
	
	private Long tenant_id;
	
	private String rg;
	
	@ToString.Include
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataNascimento;
	
	private String endereco;
	
	private String telefone;
	
	private String escola;
	
	private String serie;
	
	@Enumerated(EnumType.STRING)
	private Sexo sexo;
	
	@ManyToOne
	@JoinColumn(name="codigo_unidade")
	private Unidade unidade;
	
	/*
	 * Datas de Criação e Modificação
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCriacao;	
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModificacao;

	@PrePersist
	@PreUpdate
	public void configuraDatasCriacaoAlteracao() {
		this.setDataModificacao( new Date() );
				
		if (this.getDataCriacao() == null) {
			this.setDataCriacao( new Date() );
		}
		log.debug("Datas de criação e modificação criados");
	}

}
