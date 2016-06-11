package br.com.ieptbto.cra.component.label;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.joda.time.LocalDate;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class LocalDateTextField extends TextField<LocalDate> {

	public LocalDateTextField(String id) {
		super(id);
	}

	public LocalDateTextField(String id, IModel<LocalDate> model) {
		super(id, model);
	}

	@Override
	protected LocalDate convertValue(String[] value) throws ConversionException {
		return DataUtil.stringToLocalDate(value.toString());
	}

	@Override
	public <C> IConverter<C> getConverter(Class<C> type) {
		return new LocalDateConverter("dd/MM/yyyy");
	}
}
