package gaian.svsa.ct.modelo.enums;
/*
 * @author laurojr
 *
 */

public enum DireitoViolado {
	
	VIDA("Vida"),
	SAUDE("Saúde"),
	ALIMENTACAO("Alimentação"),
	EDUCACAO("Educação"),
	ESPORTES("Esportes"),
	LAZER("Lazer"),
	PROFISSIONALIZACAO("Profissionalização"),
	CULTURA("Cultura"),
	LIBERDADE("Liberdade"),
	RESPEITO("Respeito"),
	DIGNIDADE("Dignidade"),
	CONVIVENCIA_FAMILIAR_COMUNITARIA("Convivência Familiar Comunitária"),
	NEGLIGENCIA_SAUDE_HIGIENE("Negligência Saúde Higiene"),
	NEGLIGENCIA_ALIMENTACAO("Negligência Alimentação"),
	MAUS_TRATOS_FISICOS("Maus Tratos Físicos"),
	MAUS_TRATOS_VERBAL_PSICOLOGICO("Maus Tratos Verbal Psicológico"),
	ABANDONO_DE_INCAPAZ("Abandono de Incapaz"),
	SUSPEITA_VVS("Suspeita VVS"),
	EXPLORACAO_SEXUAL_INFANTIL("Exploração Sexual Infantil"),
	TRABALHO_INFANTIL("Trabalho Infantil"),
	CONFLITO_FAMILIAR("Conflito Familiar"),
	CONVIVENCIA_FAMILIAR("Convivência Familiar"),
	SUSPEITA_USO_DROGAS_ADOLESCENTE("Suspeita de Uso de Drogas Adolescente"),
	INDISCIPLINA_ESCOLAR("Indisciplina Escolar"),
	EVASAO_ESCOLAR("Evasão Escolar"),
	FALTA_ESCOLAR("Falta Escolar"),
	AGRESSAO_FISICA_ESCOLA("Agressão Física Escola"),
	BULLYING("Bullying"),
	DIFICULDADE_DE_APRENDIZAGEM("Dificuldade de Aprendizagem");
	
	private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private DireitoViolado(String descricao) {
        this.descricao = descricao;
    }
}
