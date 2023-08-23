package gaian.svsa.ct.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import gaian.svsa.ct.modelo.enums.AgenteViolador;
import gaian.svsa.ct.modelo.enums.DireitoViolado;
import gaian.svsa.ct.modelo.enums.OrigemDenuncia;
import gaian.svsa.ct.modelo.enums.Status;
import gaian.svsa.ct.modelo.enums.StatusRD;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author laurojr
 *
 */
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@NamedQueries({
	@NamedQuery(name="Denuncia.buscarTodos", query="select d from Denuncia d where d.tenant_id = :tenantId "
			+"and d.unidade = :unidade"),
	@NamedQuery(name="Denuncia.buscarTodosDia", query="select d from Denuncia d where d.tenant_id = :tenantId "
			+ "and d.unidade = :unidade "
			+ "and d.dataEmissao between :ini and :fim "),	
})
public class Denuncia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EqualsAndHashCode.Include
	@ToString.Include
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;
	
	@ToString.Include
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataEmissao;
	
	private Integer ano;
	
	private String rdFisico;
	
	@ToString.Include
	private String relato;
	
	private Long tenant_id;
	
	private String s3Key = null;
	
	@Enumerated(EnumType.STRING)
	@NotAudited
	private Status status;
	
	@Enumerated(EnumType.STRING)
	private OrigemDenuncia origemDenuncia;
	
	@Enumerated(EnumType.STRING)
	private StatusRD statusRD;
	
	@Enumerated(EnumType.STRING)
	private AgenteViolador agenteViolador;
	
	@ElementCollection(targetClass = DireitoViolado.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "DireitosViolados", joinColumns = @JoinColumn(name = "codigo_denuncia"))
	@Column(name = "direito", nullable = false)
	@Enumerated(EnumType.STRING)
	private List<DireitoViolado> direitosViolados;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="codigo_familia")
	@NotAudited
	private Familia familia;
	
	@ManyToOne
	@JoinColumn(name="codigo_unidade")
	@Audited(targetAuditMode = RelationTargetAuditMode.AUDITED)
	private Unidade unidade;
	
	@ManyToOne
	@JoinColumn(name="codigo_conselheiro")
	private Usuario conselheiro;
	
	@ManyToOne
	@JoinColumn(name="codigo_conselheiroRef")
	private Usuario conselheiroReferencia;
	
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
	}
}
