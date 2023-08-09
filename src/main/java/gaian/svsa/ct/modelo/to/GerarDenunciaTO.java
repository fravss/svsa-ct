package gaian.svsa.ct.modelo.to;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GerarDenunciaTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String NomeRef;
	private String relato;
	private String conselheiro;
	private Date dataEmissao;
	private Long codigoDen;
	private List<String> membros;
	
}
