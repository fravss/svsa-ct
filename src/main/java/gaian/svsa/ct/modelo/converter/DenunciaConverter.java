package gaian.svsa.ct.modelo.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gaian.svsa.ct.dao.DenunciaDAO;
import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.util.cdi.CDIServiceLocator;


/**
 * @author murakamiadmin
 *
 */
@FacesConverter(forClass=Denuncia.class)
public class DenunciaConverter implements Converter<Object> {

	private DenunciaDAO denunciaDAO;
	
	public DenunciaConverter() {
		this.denunciaDAO = CDIServiceLocator.getBean(DenunciaDAO.class);
	}
	
	@Override    //converte tipo String para objeto - necessário mapear do modelo relacional para obj
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Denuncia retorno = null;

		if (value != null && !value.isEmpty()) {
			retorno = this.denunciaDAO.buscarPeloCodigo(Long.valueOf(value));
		}

		return retorno;
	}

	@Override  //converte de objeto para codigo - necessário mapear do modelo obj para relacional
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Long codigo = ((Denuncia) value).getCodigo();
			String retorno = (codigo == null ? null : codigo.toString());
			
			return retorno;
		}
		
		return "";
	}

}