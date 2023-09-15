package gaian.svsa.ct.modelo.to;

import java.io.Serializable;
import java.util.Date;

import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificacaoTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String nomeNotificacao;
	private String nomeTerceiro;
	private String endereco;
	private String bairro;
	private String numero;
	private String motivo;
	
	private Date dataComparecimento;
	
	private Usuario tecnico;
	private Unidade unidade;
}
