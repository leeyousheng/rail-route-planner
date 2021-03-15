package railrouteplanner.domain.journey;

import org.junit.Test;
import railrouteplanner.domain.date.DateUtil;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class TimeTypeTest {
    @Test
    public void testGet() throws ParseException {
        System.out.println(DateUtil.parse("2021-01-09T18:00", "yyyy-MM-dd'T'HH:mm"));
        assertEquals("should return peak if Monday and 6am", TimeType.peak, TimeType.get(DateUtil.parse("2021-01-04T06:00", "yyyy-MM-dd'T'HH:mm")));
        assertEquals("should return peak if Monday and 8:59am", TimeType.peak, TimeType.get(DateUtil.parse("2021-01-04T08:59", "yyyy-MM-dd'T'HH:mm")));
        assertEquals("should return peak if Friday and 6pm", TimeType.peak, TimeType.get(DateUtil.parse("2021-01-08T18:00", "yyyy-MM-dd'T'HH:mm")));
        assertEquals("should return peak if Friday and 8:59pm,", TimeType.peak, TimeType.get(DateUtil.parse("2021-01-08T20:59", "yyyy-MM-dd'T'HH:mm")));

        assertEquals("should return nonpeak if Saturday and 6am", TimeType.nonpeak, TimeType.get(DateUtil.parse("2021-01-02T06:00", "yyyy-MM-dd'T'HH:mm")));
        assertEquals("should return nonpeak if Sunday and 8:59pm", TimeType.nonpeak, TimeType.get(DateUtil.parse("2021-01-03T20:59", "yyyy-MM-dd'T'HH:mm")));
        assertEquals("should return nonpeak if Monday and 9am", TimeType.nonpeak, TimeType.get(DateUtil.parse("2021-01-04T09:00", "yyyy-MM-dd'T'HH:mm")));
        assertEquals("should return nonpeak if Friday and 5:59pm", TimeType.nonpeak, TimeType.get(DateUtil.parse("2021-01-08T17:59", "yyyy-MM-dd'T'HH:mm")));
        assertEquals("should return nonpeak if Wednesday and 9pm", TimeType.nonpeak, TimeType.get(DateUtil.parse("2021-01-06T21:00", "yyyy-MM-dd'T'HH:mm")));
        assertEquals("should return nonpeak if Thursday and 9:59pm", TimeType.nonpeak, TimeType.get(DateUtil.parse("2021-01-07T21:59", "yyyy-MM-dd'T'HH:mm")));

        assertEquals("should return night if Monday and 10pm", TimeType.night, TimeType.get(DateUtil.parse("2021-01-04T22:00", "yyyy-MM-dd'T'HH:mm")));
        assertEquals("should return night if Sunday and 5:59am", TimeType.night, TimeType.get(DateUtil.parse("2021-01-03T05:59", "yyyy-MM-dd'T'HH:mm")));
    }
}
