package gaian.svsa.ct.modelo.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import gaian.svsa.ct.dao.AgendamentoIndividualDAO;
import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.util.cdi.CDIServiceLocator;

/**
 * @author murakamiadmin
 *
 */
@FacesConverter(forClass=Atendimento.class)
public class ListaAtendimentoConverter implements Converter<Object> {

	private AgendamentoIndividualDAO listaDAO;
	
	public ListaAtendimentoConverter() {
		this.listaDAO = CDIServiceLocator.getBean(AgendamentoIndividualDAO.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Atendimento retorno = null;

		if (value != null && !value.isEmpty()) {
			retorno = this.listaDAO.buscarPeloCodigo(Long.valueOf(value));
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Long codigo = ((Atendimento) value).getCodigo();
			String retorno = (codigo == null ? null : codigo.toString());
			
			return retorno;
		}
		
		return "";
	}

}