import chess.*;
import ui.DrawBoard;
import ui.Menu;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Main {
    public static void main(String[] args) throws Exception {
        var server  = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 0 ? Integer.parseInt(args[1]) : 8080;
        Menu menu = new Menu(server, port);
        System.out.println("♕ CS240 Chess Client");
        System.out.println("Type 'Help' to get started");
        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equalsIgnoreCase("quit")) {
            menu.printInputPrompt();
            input = scanner.nextLine();

            if (input.isEmpty()) {
                continue;
            }

            menu.handleInput(input);
        }
    }
}