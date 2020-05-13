package zookeeper;

import java.util.Scanner;

import static zookeeper.Util.print;

public class ZookeeperRunner {

    private static final String PROGRAM_USAGE_HELP_MESSAGE = "Program usage: java -jar zookeeper-homework.jar connect_string process_name";

    public static void main(String[] args) {
        if (args.length == 0) {
            print(PROGRAM_USAGE_HELP_MESSAGE);
            return;
        }

        final String rootNode = "/z";
        final String connectString = args[0];
        final String processName = args[1];

        final NodeWatcher nodeWatcher = new NodeWatcher(connectString, rootNode, processName);
        handleUserInput(rootNode, nodeWatcher);
    }

    private static void handleUserInput(final String rootNode, final NodeWatcher nodeWatcher) {
        final Scanner scanner = new Scanner(System.in);

        while (true) {
            final String line = scanner.nextLine();

            if ("tree".equals(line)) {
                nodeWatcher.printTree(rootNode);
            } else if ("exit".equals(line)) {
                nodeWatcher.destroyNamedProcess();
                break;
            } else {
                print("Unknown command. List of available commands: tree, exit");
            }
        }
    }

}
