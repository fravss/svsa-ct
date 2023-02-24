package gaian.svsa.ct.modelo;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


/**
 * @author murakamiadmin
 *
 */
@Entity
@DiscriminatorValue("PESSOA_REFERENCIA")
@NamedQueries({
	@NamedQuery(name="PessoaReferencia.buscarTodos", query="select p from PessoaReferencia p where p.excluida = :exc "
			+ "and p.tenant_id = :tenantId "),
	@NamedQuery(name="PessoaReferencia.buscarPeloNome", query="select p from PessoaReferencia p where p.nome = :nome "),
	@NamedQuery(name="PessoaReferencia.buscarNomes", query="select p.nome from PessoaReferencia p where p.tenant_id = :tenantId "
			+ "and p.nome LIKE :nome")	
})
public class PessoaReferencia extends Pessoa implements Cloneable, Serializable{

	private static final long serialVersionUID = -502805488399990718L;	
	
	
	@Override
	public PessoaReferencia clone() throws CloneNotSupportedException {
		return (PessoaReferencia) super.clone();
	}
	
}