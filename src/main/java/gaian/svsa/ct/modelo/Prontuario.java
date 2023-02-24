package gaian.svsa.ct.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import gaian.svsa.ct.modelo.enums.Status;
import gaian.svsa.ct.service.s3.AmazonS3Keys;
import lombok.Getter;
import lombok.Setter;;

/**
 * @author murakamiadmin
 *
 */
@Getter
@Setter
@Entity
@Audited
@NamedQueries({
	@NamedQuery(name="Prontuario.buscarTodos", query="select p from Prontuario p where p.tenant_id = :tenantId  "), // UTILIZADO APENAS PARA TESTES DE MIGRAÇÃO
	@NamedQuery(name="Prontuario.obterNrProntuario", query="select max(p.codigo) from Prontuario p"),
	@NamedQuery(name="Prontuario.buscarTodosUnidade", 
		query="select p from Prontuario p where p.unidade = :unidade "
				+ "and p.tenant_id = :tenantId "
				+ "and p.excluido = :exc"),
	
	/* LazyCapaProntuarios */
	@NamedQuery(name="Prontuario.buscarTodosUnid", 
		query="select p from Prontuario p where p.unidade = :unidade "
				+ "and p.tenant_id = :tenantId "
				+ "and p.excluido = :exc"),
	@NamedQuery(name="Prontuario.qdeTodosUnid", 
		query="select count(p) from Prontuario p where p.unidade = :unidade "
				+ "and p.tenant_id = :tenantId "
				+ "and p.excluido = :exc"),
	
	@NamedQuery(name="Prontuario.buscarTodosPR", 
		query="select p from Prontuario p where p.unidade = :unidade "
				+ "and p.tenant_id = :tenantId "
				+ "and p.familia.pessoaReferencia.nome LIKE :nome and p.excluido = :exc"),
	@NamedQuery(name="Prontuario.qdeTodosPR", 
		query="select count(p) from Prontuario p where p.unidade = :unidade "
				+ "and p.tenant_id = :tenantId "
				+ "and p.familia.pessoaReferencia.nome LIKE :nome and p.excluido = :exc"),
	
	@NamedQuery(name="Prontuario.buscarTodosProntuario", 
		query="select p from Prontuario p where p.unidade = :unidade "
				+ "and p.tenant_id = :tenantId "
				+ "and p.codigo = :codigo and p.excluido = :exc"),
	@NamedQuery(name="Prontuario.qdeTodosProntuario", 
		query="select count(p) from Prontuario p where p.unidade = :unidade "
				+ "and p.tenant_id = :tenantId "
				+ "and p.codigo = :codigo and p.excluido = :exc"),
	/* LazyCapaProntuarios */
	
	
	/* Rel. Pront Novos */
	@NamedQuery(name="Prontuario.buscarTodosPorData", query="select p from Prontuario p where "
			+ "p.dataEntrada between :ini and :fim "
			+ "and p.tenant_id = :tenantId "
			+ "and p.unidade = :unidade and p.excluido = :exc"),
	
	/* Rel. Pront Atualizados */
	@NamedQuery(name="Prontuario.buscarTodosPorDataModificacao", query="select p from Prontuario p where "
			+ "p.dataModificacao between :ini and :fim "
			+ "and p.tenant_id = :tenantId "
			+ "and p.unidade = :unidade and p.excluido = :exc")
	
})
public class Prontuario implements Cloneable, Serializable {
	
	private static final long serialVersionUID = -3495721768377035814L;	
	
	/*
	 * RELACIONAMENTOS
	 * 
	 * Por default no Hibernate todos os relacionamentos 
	 *   ToOne  são fetch = FetchType.EAGER (ansioso)
	 *   ToMany são fetch = FetchType.LAZY  (preguicoso)
	 *  
	 */
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;
	
	private Long tenant_id;
	
	private String prontuario;
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotAudited
	private Date dataEntrada;
	
	private Boolean excluido = false;
	private String s3Key = null;
	private String criador = null;
	
	@Enumerated(EnumType.STRING)
	@NotAudited
	private Status status;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="codigo_familia")
	@NotAudited
	private Familia familia;
	
	@ManyToOne
	@JoinColumn(name="codigo_unidade")
	@Audited(targetAuditMode = RelationTargetAuditMode.AUDITED)
	private Unidade unidade;
	
	@OneToMany (cascade=CascadeType.ALL, mappedBy="prontuario")
	@NotAudited
	private List<ObsComposicaoFamiliar> obsComposicaoFamiliar;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="prontuario_vinculado" , unique=true)
	@NotAudited
	private Prontuario prontuarioVinculado; // Se for um prontuario CREAS aponta para o respectivo prontuario CRAS e vice-versa	
	
	@Transient
	public String getUrlAnexo() {
		return AmazonS3Keys.URL_S3_BUCKET + getS3Key();
	}	
	
	@Override
	public Prontuario clone() throws CloneNotSupportedException {
		return (Prontuario) super.clone();
	}
	
	
	
	/*
	 * Registro de criacao e modificacao de dados
	 */
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCriacao;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModificacao;
	
	@PrePersist
	@PreUpdate
	public void configuraDatasCriacaoAlteracao() {
		this.dataModificacao = new Date();
		
		if (this.dataCriacao == null) {
			this.dataCriacao = new Date();
		}
	}	
	
}
