package br.com.ieptbto.cra.component.label;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Utilitário para datas.
 */
@SuppressWarnings("serial")
public class DataUtil implements Serializable {

	/** {@link DateTimeZone} padrão. */
	public static final DateTimeZone ZONE = DateTimeZone.forID("America/Sao_Paulo");

	/** {@link Locale} padrão. */
	public static final Locale LOCALE = new Locale("Pt", "BR");

	/** Pattern padrão para formatação de data/hora. */
	public static final String PADRAO_FORMATACAO_DATAHORASEG = "dd/MM/yyyy HH:mm:ss";

	/** Pattern padrão para formatação de data/hora. */
	public static final String PADRAO_FORMATACAO_DATAHORA = "dd/MM/yyyy HH:mm";

	/** Pattern padrão para formatação de data. */
	public static final String PADRAO_FORMATACAO_DATA = "dd/MM/yyyy";

	/** Pattern padrão para formatação de data. */
	public static final String PADRAO_FORMATACAO_DATA_YYYYMMDD = "yyyyMMdd";

	/** Formato padrão para data */
	public static final String FORMATO_DATA_PADRAO_REGISTRO = PADRAO_FORMATACAO_DATA_YYYYMMDD;

	/**
	 * Cria um {@link LocalDate} a partir de uma {@link String} no formato
	 * {@link #PADRAO_FORMATACAO_DATAHORA}.
	 * 
	 * @param dataHoraString
	 *            data/hora no formato esperado
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime stringToLocalDateTime(String dataHoraString) {
		return getDateTimeFormatter(PADRAO_FORMATACAO_DATAHORA).parseDateTime(dataHoraString).toLocalDateTime();
	}

	/**
	 * Cria um {@link LocalDateTime} a partir de uma {@link String} no formato
	 * informado.
	 * 
	 * @param formato
	 *            formato da string
	 * @param dataString
	 *            data/hora no formato esperado
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime stringToLocalDateTime(String formato, String dataString) {
		return getDateTimeFormatter(formato).parseDateTime(dataString).toLocalDateTime();
	}

	/**
	 * Cria um {@link LocalDate} a partir de uma {@link String} no formato
	 * informado.
	 * 
	 * @param formato
	 *            formato da string
	 * @param dataString
	 *            data no formato esperado
	 * @return {@link LocalDate}
	 */
	public static LocalDate stringToLocalDate(String formato, String dataString) {
		return getDateFormatter(formato).parseDateTime(dataString).toLocalDate();
	}

	/**
	 * Cria um {@link LocalDate} a partir de uma {@link String} no formato
	 * {@link #PADRAO_FORMATACAO_DATA}.
	 * 
	 * @param dataString
	 *            data no formato esperado
	 * @return {@link LocalDate}
	 */
	public static LocalDate stringToLocalDate(String dataString) {
		if (dataString == null) {
			return null;
		}
		return getDateFormatter(PADRAO_FORMATACAO_DATA).parseDateTime(dataString).toLocalDate();
	}

	/**
	 * Converte {@link LocalDate} para {@link String} no formato
	 * {@link #PADRAO_FORMATACAO_DATA}.
	 * 
	 * @param localDate
	 *            data a ser convertida
	 * @return data formatada
	 */
	public static String localDateToString(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}
		return localDate.toString(getDateTimeFormatter(PADRAO_FORMATACAO_DATA));
	}

	/**
	 * Converte {@link LocalDate} para {@link Date} no formato
	 * {@link #PADRAO_FORMATACAO_DATA}.
	 * 
	 * @param localDate
	 *            data a ser convertida
	 * @return data formatada
	 */
	public static Date localDateToDate(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}
		return localDate.toDateTimeAtCurrentTime().toDate();
	}

	/**
	 * @param dataHoraString
	 * @return
	 */
	public static DateTime stringToDateTime(String dataHoraString) {
		if (dataHoraString == null) {
			return null;
		}
		return getDateTimeFormatter(PADRAO_FORMATACAO_DATAHORA).parseDateTime(dataHoraString).toDateTime();
	}

	/**
	 * Cria um {@link String} a partir de uma {@link LocalDate} no formato
	 * {@link #PADRAO_FORMATACAO_DATA_YYYYMMDD}.
	 * 
	 * @param data
	 *            data a ser transformada
	 * @return {@link String} no formato yyyyMMdd
	 */

	public static String localDateToStringyyyyMMdd(LocalDate data) {
		return data.toString(getDateTimeFormatter(PADRAO_FORMATACAO_DATA_YYYYMMDD));
	}

	/**
	 * Converte {@link LocalDateTime} para {@link String} no formato
	 * {@link #PADRAO_FORMATACAO_DATAHORA}.
	 * 
	 * @param localDateTime
	 *            data/hora a ser convertida
	 * @return data formatada
	 */
	public static String localDateTimeToString(LocalDateTime localDateTime) {
		return localDateTime.toString(getDateTimeFormatter(PADRAO_FORMATACAO_DATAHORA));
	}

	/**
	 * 
	 * Converte {@link Date} para {@link LocalDate}.
	 * 
	 * @param data
	 * @return
	 */
	public static LocalDate dateToLocalDate(Date data) {
		return new LocalDate(data.getTime());
	}

	/**
	 * Verifica se a data passada como parâmetro está no período determinado
	 * pela dataInicio e dataFim.
	 * 
	 * @param data
	 *            data a ser verificada
	 * @param dataInicio
	 *            data de início do período
	 * @param dataFim
	 *            data fim do período
	 * @return
	 */
	public static boolean dataEstaNoPeriodo(LocalDate data, LocalDate dataInicio, LocalDate dataFim) {
		Interval periodo = new Interval(dataInicio.toDateTimeAtStartOfDay(), dataFim.plusDays(1).toDateTimeAtStartOfDay());
		return periodo.contains(data.toDateTimeAtStartOfDay());
	}

	private static DateTimeFormatter getDateTimeFormatter(String formato) {
		return DateTimeFormat.forPattern(formato).withZone(ZONE).withLocale(LOCALE);
	}

	private static DateTimeFormatter getDateFormatter(String formato) {
		// Necessário timezone UTC para não dar problema com horário de verão
		return DateTimeFormat.forPattern(formato).withZone(DateTimeZone.UTC);
	}

}
