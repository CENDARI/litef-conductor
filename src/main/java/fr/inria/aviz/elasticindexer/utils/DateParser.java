package fr.inria.aviz.elasticindexer.utils;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

/**
 * Class DateParser
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class DateParser {
    private static final Logger logger = Logger.getLogger(DateParser.class);
    
    private static Parser dateParser = new Parser();
    /** Date printer for elasticsearch */
    @JsonIgnore
    public final static DateTimeFormatter DATE_PRINTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
    
    private static final Pattern YEAR = Pattern.compile("[1-9][0-9]{1,3}");
    
    /**
     * Formats the specified date according to elasticsearch's format
     * @param date a Java Date
     * @return a string representation of the date
     */
    public static String format(Date date) {
        return format(new DateTime(date));
    }
    
    /**
     * Formats the specified date according to elasticsearch's format
     * @param date a Joda Date
     * @return a string representation of the date
     */
    public static String format(DateTime date) {
        return DATE_PRINTER.print(date);
    }
    

    /**
     * Check the date format and fix it or return null
     * @param date a date format as string
     * @return a correct date format or null
     */
    public static String checkDate(String date) {
        if (date == null || date.length() == 0)
            return null;
        if (YEAR.matcher(date).matches())
            return date+"-01-01";
        try {
            DATE_PRINTER.parseDateTime(date);
            return date;
        }
        catch(IllegalArgumentException e) {
            logger.debug("Illegal date format for elasticsearch: "+date);
        }
        List<DateGroup> groups = dateParser.parse(date);
        for (DateGroup group : groups) {

            List<Date> dates = group.getDates();
            if (dates.size() > 1) {
                logger.info("Ambiguous date: "+date+" with "+
                            dates.size()+" interpretations");
            }
            String match = group.getText();
            if (YEAR.matcher(match).matches())
                return match+"-01-01";
            return DATE_PRINTER.print(new DateTime(dates.get(0)));
        }
        return null;
    }
    
    /**
     * @return the dateParser
     */
    public static Parser getDateParser() {
        return dateParser;
    }
}
