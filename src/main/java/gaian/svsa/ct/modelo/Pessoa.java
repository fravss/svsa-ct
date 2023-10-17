package gaian.svsa.ct.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import gaian.svsa.ct.modelo.enums.Parentesco;
import gaian.svsa.ct.modelo.enums.Sexo;
import gaian.svsa.ct.util.CalculoUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

/**
 * @author murakamiadmin
 *
 */
@Log4j
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TIPO_PESSOA", discriminatorType=DiscriminatorType.STRING)
@NamedQueries({
	@NamedQuery(name="Pessoa.buscarTodos", query="select p from Pessoa p where p.excluida = :exc and p.tenant_id = :tenantId"),
	@NamedQuery(name="Pessoa.buscarPeloNome", query="select p from Pessoa p where p.nome = :nome and p.excluida = :exc"),
	@NamedQuery(name="Pessoa.buscarNomes", query="select p.nome from Pessoa p where p.tenant_id = :tenantId "
			+ "and p.familia.denuncia.unidade = :unidade "
			+ "and p.excluida = :exc "
			+ "and p.nome LIKE :nome")
})
public class Pessoa implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 9109362274647458688L;
	
	@EqualsAndHashCode.Include
	@ToString.Include
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;
	
	@ToString.Include
	private String nome;				//@Index(name="idx_nome") - para buscas com like% lazyPessoa
	
	private String rg;
	
	@ToString.Include
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataNascimento;
	
	private String telefone;
	
	private String email;
	
	@ToString.Include
	private String escola;
	
	@ToString.Include
	private String periodo;

	@ToString.Include
	private String serie;
	
	private Boolean excluida = false;
	
	private Long tenant_id;
	
	@Enumerated(EnumType.STRING)
	private Parentesco parentesco;
	
	@Enumerated(EnumType.STRING)
	private Sexo sexo;
	
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="codigo_familia")
	private Familia familia;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="codigo_endereco")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ToString.Include
	private Endereco endereco;
	
	@ManyToOne
	@JoinColumn(name="codigo_conselheiroResp")
	private Usuario conselheiroResponsavel;
	
	@ManyToOne
	@JoinColumn (name="codigo_responsavel")
	private Pessoa responsavel;
	
	@Transient
	public int getIdade() {
		int idade = 0;
		if(this.getDataNascimento() != null)
			idade = CalculoUtil.calcularIdade(this.getDataNascimento());
		
		return idade;			
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
		else {
			atzObjAssocidados();
		}		
	}
	// Atualiza as datas de modificação dos objetos associados também
	public void atzObjAssocidados() {
		if(this.getFamilia() != null) {	
			log.info("datas Pessoa, Familia e Prontuario atualizados.");
			this.getFamilia().setDataModificacao(this.getDataModificacao());
			if(this.getFamilia().getDenuncia() != null) {
				this.getFamilia().getDenuncia().setDataModificacao(this.getDataModificacao());	
			}
		}
	}	
}