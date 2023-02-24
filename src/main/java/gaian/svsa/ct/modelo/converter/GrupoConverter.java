package gaian.svsa.ct.modelo.converter;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import gaian.svsa.ct.modelo.enums.Grupo;

/**
 * @author murakamiadmin
 *
 */
@FacesConverter(value="grupoConverter")
public class GrupoConverter extends EnumConverter {

    public GrupoConverter() {
        super(Grupo.class);
    }    

}
