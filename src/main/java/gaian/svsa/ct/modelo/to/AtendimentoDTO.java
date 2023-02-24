package gaian.svsa.ct.modelo.to;

import java.io.Serializable;
import java.util.Date;

import gaian.svsa.ct.modelo.enums.CodigoAuxiliarAtendimento;
import gaian.svsa.ct.modelo.enums.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe usada para projetar dados de atendimentos tanto individualizado quanto coletivo
 * Criada para otimizar as buscas dos atendimentos
 * 
 * @author murakamiadmin
 *
 */
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class AtendimentoDTO implements Serializable, Comparable<AtendimentoDTO>{
	
	private static final long serialVersionUID = 1L;
	
	@ToString.Include
	private Date data;
	private String resumoAtendimento = "";
	private String nomeTecnico = "";
	private String nomeUnidade = "";
	private String nomePessoa = "";
	private CodigoAuxiliarAtendimento codAux;
	private Role role;
	private String nomeAgendador = "";
	
	public AtendimentoDTO() {}
	
	public AtendimentoDTO(Date data, String resumoAtendimento, String nomeTecnico, String nomeUnidade, String nomePessoa, CodigoAuxiliarAtendimento codAux) {
		this.data = data;
		this.resumoAtendimento = resumoAtendimento;
		this.nomeTecnico = nomeTecnico;
		this.nomeUnidade = nomeUnidade;
		this.nomePessoa = nomePessoa;
		this.codAux = codAux;
	}
	
	public AtendimentoDTO(Date data, String resumoAtendimento, String nomeTecnico, String nomeUnidade, String nomePessoa) {
		this.data = data;
		this.resumoAtendimento = resumoAtendimento;
		this.nomeTecnico = nomeTecnico;
		this.nomeUnidade = nomeUnidade;
		this.nomePessoa = nomePessoa;
	}
	
	// usado em produtividadeDAO
	public AtendimentoDTO(String nomeTecnico, String nomeUnidade) {		
		this.nomeTecnico = nomeTecnico;		
		this.nomeUnidade = nomeUnidade;
	}
	public AtendimentoDTO(String nomeTecnico, String nomeUnidade, Role role) {		
		this.nomeTecnico = nomeTecnico;		
		this.nomeUnidade = nomeUnidade;
		this.role = role;
	}

	@Override
	public int compareTo(AtendimentoDTO a) {
		
		if(this.getData() != null && a.getData() != null){	
					
			if (this.getData().after(a.getData())) {
	            return -1;
	        }
			if (this.getData().before(a.getData())) {
	            return 1;
	        }
		}
        return 0;
	}

}