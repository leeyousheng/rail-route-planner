package railrouteplanner.application;

import java.text.ParseException;
import java.util.Scanner;

/**
 * ApplicationService is a singleton service to perform the actual application loop.ss
 */
@SuppressWarnings("FieldCanBeLocal")
public final class ApplicationService {
    private static ApplicationService instance;
    private final CommandService commandService;
    private final Scanner sc;

    private final String WELCOME_MSG = "Rail Route Planner\nType 'quit' anytime to exit.";
    private final String EXIT_MSG = "Wishing you a pleasant journey!";
    private final String INIT_MSG = "Insert station source filepath. Type 'd' for default input source.";
    private final String PROMPT_MSG = "Please state where you would like to travel to in format:\n'travel from \"<initial station name>\" to \"<destination station name>\" at <YYYY-MM-DDThh:mm>'";

    private ApplicationService(CommandService commandService, Scanner sc) {
        this.commandService = commandService;
        this.sc = sc;
    }

    public static ApplicationService getInstance() {
        return getInstance(CommandService.getInstance(), new Scanner(System.in));
    }

    public static ApplicationService getInstance(CommandService commandService, Scanner sc) {
        if (instance == null)
            instance = new ApplicationService(commandService, sc);
        return instance;
    }

    /**
     * reset allows retrieval of new instance.
     * CAUTION: might cause error if used inappropriately.
     */
    public static void reset() {
        instance = null;
    }


    public void run() {
        System.out.println(WELCOME_MSG);
        System.out.println();

        if (initialiseData()) {
            planRouteLoop();
        }

        System.out.println(EXIT_MSG);
    }

    private boolean initialiseData() {
        String filepath;
        while (true) {
            System.out.println(INIT_MSG);

            filepath = sc.nextLine();
            if (filepath.equalsIgnoreCase("quit")) return false;

            if (commandService.initialiseData(filepath)) return true;
        }
    }

    private void planRouteLoop() {
        String cmd;
        while (true) {
            System.out.println(PROMPT_MSG);

            cmd = sc.nextLine();
            if (cmd.equalsIgnoreCase("quit")) return;

            try {
                System.out.println();
                System.out.println(commandService.handleCommand(cmd));
                System.out.println();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
