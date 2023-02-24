package gaian.svsa.ct.modelo.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gaian.svsa.ct.dao.PessoaDAO;
import gaian.svsa.ct.modelo.Pais;
import gaian.svsa.ct.util.cdi.CDIServiceLocator;

/**
 * @author murakamiadmin
 *
 */
@FacesConverter(forClass=Pais.class )
public class PaisConverter implements Converter<Object> {

	private PessoaDAO pessoaDAO;
	public PaisConverter() {
		this.pessoaDAO = CDIServiceLocator.getBean(PessoaDAO.class);
	}

	@Override    //converte tipo String para objeto - necessário mapear do modelo relacional para obj
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Pais retorno = null;

		if (value != null && !value.isEmpty()) {
			retorno = this.pessoaDAO.buscarPais(Long.valueOf(value));
		}

		return retorno;
	}

	@Override  //converte de objeto para codigo - necessário mapear do modelo obj para relacional
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Long codigo = ((Pais) value).getCodigo();
			String retorno = (codigo == null ? null : codigo.toString());
			
			return retorno;
		}
		
		return "";
	}
	
	
}
