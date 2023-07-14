package gaian.svsa.ct.modelo.to;

import java.io.Serializable;
import java.util.Date;

import gaian.svsa.ct.modelo.enums.Status;
import lombok.Getter;
import lombok.Setter;

/**
 * @author gabriel
 *
 */
@Getter
@Setter
public class PessoaDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long codigo = 0L;
	private Long codigoDenuncia = 0L;
	private String nome = "";
	private Date dataNascimento = null;
	private String unidade = "";
	private Status statusDenuncia = null;
	
	public PessoaDTO() {}
	
	public PessoaDTO(Long codigo, Long codigoDenuncia, String nome, Date dataNascimento, 
			String unidade, Status statusDenuncia) {
		
		this.codigo = codigo;
		this.codigoDenuncia = codigoDenuncia;
		this.nome = nome;
		this.dataNascimento = dataNascimento;
		this.unidade = unidade;
		this.statusDenuncia = statusDenuncia;
	}
}
