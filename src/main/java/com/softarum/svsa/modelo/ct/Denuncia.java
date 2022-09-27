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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.softarum.svsa.modelo.Unidade;
import com.softarum.svsa.modelo.Usuario;
import com.softarum.svsa.modelo.enums.ct.AgenteViolador;
import com.softarum.svsa.modelo.enums.ct.DireitoViolado;
import com.softarum.svsa.modelo.enums.ct.OrigemDenuncia;
import com.softarum.svsa.modelo.enums.ct.Status;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author laurojr
 *
 */
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@NamedQueries({
	@NamedQuery(name="Denuncia.buscarTodos", query="select d from Denuncia d where d.tenant_id = :tenantId"),
	@NamedQuery(name="Denuncia.buscarTodosDia", query="select d from Denuncia d where d.tenant_id = :tenantId "
			+ "and d.unidade = :unidade "
			+ "and d.dataEmissao between :ini and :fim ")
	
})
public class Denuncia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;
	
	private Long tenant_id;
	
	private Integer ano;
	
	private String relato;
	
	@ToString.Include
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataEmissao;
	
	@Enumerated(EnumType.STRING)
	private AgenteViolador agenteViolador;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@Enumerated(EnumType.STRING)
	private DireitoViolado direitoViolado;

	@Enumerated(EnumType.STRING)
	private OrigemDenuncia origemDenuncia;
	
	@ManyToOne
	@JoinColumn(name="codigo_tecnico")
	private Usuario tecnico;
	
	@ManyToOne
	@JoinColumn(name="codigo_pessoa")
	private PessoaDenuncia pessoa;
	
	@ManyToOne
	@JoinColumn(name="codigo_unidade")
	private Unidade unidade;
	
}
