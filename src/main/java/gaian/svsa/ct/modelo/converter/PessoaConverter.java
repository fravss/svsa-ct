package gaian.svsa.ct.modelo.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gaian.svsa.ct.dao.RDComposicaoDAO;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.util.cdi.CDIServiceLocator;

/**
 * @author murakamiadmin
 *
 */
@FacesConverter(forClass=Pessoa.class )
public class PessoaConverter implements Converter<Object> {

	private RDComposicaoDAO composicaoDAO;
	public PessoaConverter() {
		this.composicaoDAO = CDIServiceLocator.getBean(RDComposicaoDAO.class);
	}

	@Override    //converte tipo String para objeto - necessário mapear do modelo relacional para obj
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Pessoa retorno = null;

		if (value != null && !value.isEmpty()) {
			retorno = this.composicaoDAO.buscarPeloCodigo(Long.valueOf(value));
		}

		return retorno;
	}

	@Override  //converte de objeto para codigo - necessário mapear do modelo obj para relacional
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Long codigo = ((Pessoa) value).getCodigo();
			String retorno = (codigo == null ? null : codigo.toString());
			
			return retorno;
		}
		
		return "";
	}
	
	
}
