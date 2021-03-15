package railrouteplanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import railrouteplanner.application.ApplicationService;
import railrouteplanner.application.CommandService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class FunctionalTest {
    private final Scanner sc = mock(Scanner.class);

    @Before
    public void before() {
        ApplicationService.reset();
        CommandService.reset();
        reset(sc);
    }

    @After
    public void after() {
        System.setOut(System.out);
    }

    @Test
    public void testFunctionalFlow_givenReachableDestination() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        when(sc.nextLine()).thenReturn("d", "travel from \"Jurong East\" to \"Changi Airport\" at 2020-01-01T05:30", "quit");

        ApplicationService.getInstance(CommandService.getInstance(), sc).run();

        assertEquals("normal application result", "Rail Route Planner\n" +
                "Type 'quit' anytime to exit.\n" +
                "\n" +
                "Insert station source filepath. Type 'd' for default input source.\n" +
                "data initialisation success\n" +
                "Please state where you would like to travel to in format:\n" +
                "'travel from \"<initial station name>\" to \"<destination station name>\" at <YYYY-MM-DDThh:mm>'\n" +
                "\n" +
                "Journey to Changi Airport (Estimated arrival - Wed Jan 01 09:25:00 SGT 2020):\n" +
                "Wed Jan 01 05:30:00 SGT 2020 - (night) Take EW from Jurong East to Clementi.\n" +
                "Wed Jan 01 05:40:00 SGT 2020 - (night) Take EW from Clementi to Dover.\n" +
                "Wed Jan 01 05:50:00 SGT 2020 - (night) Take EW from Dover to Buona Vista.\n" +
                "Wed Jan 01 06:00:00 SGT 2020 - (peak) Take EW from Buona Vista to Commonwealth.\n" +
                "Wed Jan 01 06:10:00 SGT 2020 - (peak) Take EW from Commonwealth to Queenstown.\n" +
                "Wed Jan 01 06:20:00 SGT 2020 - (peak) Take EW from Queenstown to Redhill.\n" +
                "Wed Jan 01 06:30:00 SGT 2020 - (peak) Take EW from Redhill to Tiong Bahru.\n" +
                "Wed Jan 01 06:40:00 SGT 2020 - (peak) Take EW from Tiong Bahru to Outram Park.\n" +
                "Wed Jan 01 06:50:00 SGT 2020 - (peak) Take EW from Outram Park to Tanjong Pagar.\n" +
                "Wed Jan 01 07:00:00 SGT 2020 - (peak) Take EW from Tanjong Pagar to Raffles Place.\n" +
                "Wed Jan 01 07:10:00 SGT 2020 - (peak) Take EW from Raffles Place to City Hall.\n" +
                "Wed Jan 01 07:20:00 SGT 2020 - (peak) Take EW from City Hall to Bugis.\n" +
                "Wed Jan 01 07:30:00 SGT 2020 - (peak) Take EW from Bugis to Lavender.\n" +
                "Wed Jan 01 07:40:00 SGT 2020 - (peak) Take EW from Lavender to Kallang.\n" +
                "Wed Jan 01 07:50:00 SGT 2020 - (peak) Take EW from Kallang to Aljunied.\n" +
                "Wed Jan 01 08:00:00 SGT 2020 - (peak) Take EW from Aljunied to Paya Lebar.\n" +
                "Wed Jan 01 08:10:00 SGT 2020 - (peak) Take EW from Paya Lebar to Eunos.\n" +
                "Wed Jan 01 08:20:00 SGT 2020 - (peak) Take EW from Eunos to Kembangan.\n" +
                "Wed Jan 01 08:30:00 SGT 2020 - (peak) Take EW from Kembangan to Bedok.\n" +
                "Wed Jan 01 08:40:00 SGT 2020 - (peak) Take EW from Bedok to Tanah Merah.\n" +
                "Wed Jan 01 08:50:00 SGT 2020 - (peak) Change line from EW to CG at Tanah Merah.\n" +
                "Wed Jan 01 09:05:00 SGT 2020 - (nonpeak) Take CG from Tanah Merah to Expo.\n" +
                "Wed Jan 01 09:15:00 SGT 2020 - (nonpeak) Take CG from Expo to Changi Airport.\n" +
                "Wed Jan 01 09:25:00 SGT 2020 - Arrived at Changi Airport.\n" +
                "\n" +
                "Please state where you would like to travel to in format:\n" +
                "'travel from \"<initial station name>\" to \"<destination station name>\" at <YYYY-MM-DDThh:mm>'\n" +
                "Wishing you a pleasant journey!\n", outContent.toString());
    }

    @Test
    public void testFunctionalFlow_givenUnreachableDestination() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        when(sc.nextLine()).thenReturn("d", "travel from \"Jurong East\" to \"Canberra\" at 2018-01-01T05:30", "quit");

        ApplicationService.getInstance(CommandService.getInstance(), sc).run();

        assertEquals("Rail Route Planner\n" +
                "Type 'quit' anytime to exit.\n" +
                "\n" +
                "Insert station source filepath. Type 'd' for default input source.\n" +
                "data initialisation success\n" +
                "Please state where you would like to travel to in format:\n" +
                "'travel from \"<initial station name>\" to \"<destination station name>\" at <YYYY-MM-DDThh:mm>'\n" +
                "\n" +
                "Destination could not be reached.\n" +
                "\n" +
                "Please state where you would like to travel to in format:\n" +
                "'travel from \"<initial station name>\" to \"<destination station name>\" at <YYYY-MM-DDThh:mm>'\n" +
                "Wishing you a pleasant journey!\n", outContent.toString());
    }

    @Test
    public void testFunctionalFlow_givenSameStation() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        when(sc.nextLine()).thenReturn("d", "travel from \"Jurong East\" to \"Jurong East\" at 2020-01-01T05:30", "quit");

        ApplicationService.getInstance(CommandService.getInstance(), sc).run();

        assertEquals("Rail Route Planner\n" +
                "Type 'quit' anytime to exit.\n" +
                "\n" +
                "Insert station source filepath. Type 'd' for default input source.\n" +
                "data initialisation success\n" +
                "Please state where you would like to travel to in format:\n" +
                "'travel from \"<initial station name>\" to \"<destination station name>\" at <YYYY-MM-DDThh:mm>'\n" +
                "\n" +
                "Already at destination.\n" +
                "\n" +
                "Please state where you would like to travel to in format:\n" +
                "'travel from \"<initial station name>\" to \"<destination station name>\" at <YYYY-MM-DDThh:mm>'\n" +
                "Wishing you a pleasant journey!\n", outContent.toString());
    }
}
