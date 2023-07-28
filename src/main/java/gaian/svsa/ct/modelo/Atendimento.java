package gaian.svsa.ct.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import gaian.svsa.ct.modelo.enums.CodigoAuxiliarAtendimento;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.enums.StatusAtendimento;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author murakamiadmin
 *
 */
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Audited
@NamedQueries({
	@NamedQuery(name="Atendimento.buscarTodos", query="select a from Atendimento a where a.tenant_id = :tenantId"),
	@NamedQuery(name="Atendimento.buscarAtendimentosAgendados", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "order by a.dataAgendamento"),
	// usado para em atendimento individual sem agendamento
	@NamedQuery(name="Atendimento.buscarAtendimentosPendentes", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "order by a.dataAgendamento"),
	@NamedQuery(name="Atendimento.buscarAtendAgendados", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.dataAgendamento between :ini and :fim "
			+ "and a.tenant_id = :tenantId "
			+ "order by a.dataAgendamento"),
	@NamedQuery(name="Atendimento.buscarAgendaUsuario", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.tecnico = :tecnico "
			+ "and a.tenant_id = :tenantId "),
	@NamedQuery(name="Atendimento.buscarAtendimentosRole", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "and a.role = :role "
			+ "order by a.dataAgendamento"),
	@NamedQuery(name="Atendimento.buscarAtendimentosTecnicos", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "order by a.dataAgendamento"),
	@NamedQuery(name="Atendimento.buscarAtendimentosRecepcao", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "and a.dataAtendimento between :ini and :fim "
			+ "and a.codigoAuxiliar = :codigoAux "),	
	@NamedQuery(name="Atendimento.buscarResumoAtendimentos", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.pessoa = :pessoa "
			+ "and a.tenant_id = :tenantId "),	
	
	@NamedQuery(name="Atendimento.consultaFaltas", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.pessoa = :pessoa "
			+ "and a.tenant_id = :tenantId "),
	
	
	/*
	 * relatorios atendimentos	
	 */
	@NamedQuery(name="Atendimento.buscarAtendimentosCodAux", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO') "
			+ "order by a.codigoAuxiliar"),	
	@NamedQuery(name="Atendimento.buscarAtendimentosCodAuxGrafico", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "and a.dataAtendimento between :ini and :fim "
			+ "and a.codigoAuxiliar not in ('ATENDIMENTO_RECEPCAO') "
			+ "order by a.codigoAuxiliar"),
	
	
	/*
	 * RelatorioAtendimentoFamilia
	 */
	@NamedQuery(name="Atendimento.buscarAtendimentoFamilia", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "and a.pessoa.familia.denuncia = :denuncia "
			+ "order by a.dataAtendimento"),
	
	/*
	 * RelatorioAtendimentos CAdUnico
	 */
	@NamedQuery(name="Atendimento.buscarAtendCadUnicoDataPeriodo", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "and a.dataAtendimento between :ini and :fim "
			+ "and a.codigoAuxiliar in ('CADASTRAMENTO_CADUNICO', 'CADASTRAMENTO_CADUNICO_BPC', 'ATUALIZACAO_CADUNICO', 'OUTROS_CADUNICO') "), /* cuidado */
	@NamedQuery(name="Atendimento.buscarAtendidosCadUnico", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "and a.codigoAuxiliar in ('CADASTRAMENTO_CADUNICO', 'CADASTRAMENTO_CADUNICO_BPC', 'ATUALIZACAO_CADUNICO', 'OUTROS_CADUNICO') "), /* cuidado */
	@NamedQuery(name="Atendimento.buscarAtendCadUnicoDataPeriodo2", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "and a.dataAtendimento between :ini and :fim "
			+ "and a.codigoAuxiliar in ('CADASTRAMENTO_CADUNICO', 'CADASTRAMENTO_CADUNICO_BPC', 'ATUALIZACAO_CADUNICO', 'OUTROS_CADUNICO') " /* cuidado */
			+ "order by a.codigoAuxiliar"),
	@NamedQuery(name="Atendimento.buscarAtendidosCadUnico2", query="select a from Atendimento a "
			+ "where a.statusAtendimento = :status "
			+ "and a.unidade = :unidade "
			+ "and a.tenant_id = :tenantId "
			+ "and a.codigoAuxiliar in ('CADASTRAMENTO_CADUNICO', 'CADASTRAMENTO_CADUNICO_BPC', 'ATUALIZACAO_CADUNICO', 'OUTROS_CADUNICO') " /* cuidado */
			+ "order by a.codigoAuxiliar"),
})
public class Atendimento implements Serializable, Comparable<Atendimento>{

	private static final long serialVersionUID = 145526938705551405L;
	
	@EqualsAndHashCode.Include
	@ToString.Include
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;

	private Long tenant_id;
	
	@ToString.Include
	@Temporal(TemporalType.TIMESTAMP)
	@NotAudited
	private Date dataAgendamento;
	
	@ToString.Include
	@Temporal(TemporalType.TIMESTAMP)
	//@NotAudited
	private Date dataAtendimento; 						//@Index(name="idx_dataAtendimento") para buscas lazyAtendimento
	
	@Column(length = 512000,columnDefinition="Text")
	@Basic(fetch=FetchType.LAZY)
	private String resumoAtendimento;
	
	@Column(length = 512000,columnDefinition="Text")
	@Basic(fetch=FetchType.LAZY)
	private String motivo;
	
	@ManyToOne
	@JoinColumn(name="codigo_unidade")
	@NotFound( action = NotFoundAction.IGNORE )
	@NotAudited
	private Unidade unidade;
	
	@Enumerated(EnumType.STRING)
	@NotAudited
	private Role role;
	
	@Enumerated(EnumType.STRING)
	//@NotAudited
	private CodigoAuxiliarAtendimento codigoAuxiliar;	//@Index(name="idx_codigoAuxiliar") para buscas lazyAtendimento
	
	@ManyToOne
	@JoinColumn(name="codigo_tecnico")
	@NotFound( action = NotFoundAction.IGNORE )
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Usuario tecnico;
	
	@ManyToOne
	@JoinColumn(name="codigo_agendador")
	@NotFound( action = NotFoundAction.IGNORE )
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Usuario agendador;
	
	@ManyToMany(fetch = FetchType.EAGER, 
			cascade = {CascadeType.PERSIST,	CascadeType.MERGE}	)
		@JoinTable(	name="TecnicoAtendimento", 
					joinColumns={@JoinColumn(name="codigo_atendimento")}, 
					inverseJoinColumns={@JoinColumn(name="codigo_tecnico")}	)
		@NotFound( action = NotFoundAction.IGNORE )
		//@NotAudited
	private Set<Usuario> tecnicos = new HashSet<>();
	
	@ManyToOne
	@JoinColumn(name="codigo_pessoa")
	@NotFound( action = NotFoundAction.IGNORE )
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Pessoa pessoa;
	
	@Enumerated(EnumType.STRING)
	@NotAudited
	private StatusAtendimento statusAtendimento;		//@Index(name="idx_cstatusAtendimento") para buscas lazyAtendimento
	
	//private Date createdOn;  // Para auditoria
	
	@Override
	public int compareTo(Atendimento atendimento) {
		//ListaAtendimento a = (ListaAtendimento) atendimento;
        //return this.pessoa.getNome().compareToIgnoreCase(a.pessoa.getNome()); // string
		
		if (this.pessoa.getCodigo() > atendimento.getPessoa().getCodigo()) {
			return 1;
		}
		if (this.pessoa.getCodigo() < atendimento.getPessoa().getCodigo()) {
			return -1;
		}
		return 0; 
	}
	
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
	}
}
