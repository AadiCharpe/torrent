import java.util.Set;

public class torrent {

    private static final Set<String> commands = Set.of(
        "host",
        "receive",
        "list"
    );


    public static void main(String[] args) {
        args = new String[] {
            "receive",
            "192.168.1.58",
            "8067",
            "teto_plush.png"
        };
        if (args.length < 1) {
            System.err.println("Usage: aaditorrent [command]");
            return;
        }
            String command = args[0];
            if (!commands.contains(command)) {
                // err
            }

            switch (command) {
                case "host":
                    if (args.length < 2) {
                        // err
                    }
                    Socketer.main(new String[] {args[1]});

                    break;
                case "receive":
                    if (args.length < 4) {
                        // err
                    }
                    Receiver.main(
                        new String[] {
                            args[1],
                            args[2],
                            args[3]
                        }
                    );
                    break;
        }
    }
}
