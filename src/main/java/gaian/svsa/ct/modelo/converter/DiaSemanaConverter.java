package gaian.svsa.ct.modelo.converter;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import gaian.svsa.ct.modelo.enums.DiaSemana;

/**
 * @author murakamiadmin
 *
 */
@FacesConverter(value="diaSemanaConverter")
public class DiaSemanaConverter extends EnumConverter {

    public DiaSemanaConverter() {
        super(DiaSemana.class);
    }

}
