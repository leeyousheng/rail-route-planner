package railrouteplanner.domain.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMMM yyyy");

    public static Date parse(String dateStr) throws ParseException {
        return parse(dateStr, "dd MMMMM yyyy");
    }

    public static Date parse(String dateStr, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(dateStr);
    }
}
