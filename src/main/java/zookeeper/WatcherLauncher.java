package zookeeper;

import util.ConsoleColor;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static util.ColouredPrinter.printColoured;
import static util.ColouredPrinter.printlnColoured;

public class WatcherLauncher implements Launcher {

    private final String connectString;
    private final String node;
    private final String[] commands;

    private Process process;

    public WatcherLauncher(final String connectString,
                           final String node,
                           final String[] commands) {
        this.connectString = connectString;
        this.node = node;
        this.commands = commands;
    }

    @Override
    public void start() {
        final NodeListener nodeListener = createNodeListener();
        final NodeWatcher nodeWatcher = new NodeWatcher(connectString, node, nodeListener);

        handleUserInput(nodeWatcher);
    }

    public final void startProcess() {
        if (process != null) return;

        final ProcessBuilder processBuilder = new ProcessBuilder(commands)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            printlnColoured("Starting program", ConsoleColor.CYAN_BOLD);
            process = processBuilder.start();
        } catch (IOException e) {
            printlnColoured(e.getLocalizedMessage(), ConsoleColor.RED_BOLD);
        }

    }

    public final void stopProcess() {
        if (process == null) return;

        printlnColoured("Stopping program...", ConsoleColor.CYAN_BOLD);
        process.destroy();
        process = null;
    }

    private NodeListener createNodeListener() {
        return new NodeListener() {

            @Override
            public void changed(boolean exists) {
                if (exists) {
                    printlnColoured("Node still exists!", ConsoleColor.GREEN_BOLD);
                    startProcess();
                } else {
                    printlnColoured("Node does not exist anymore!", ConsoleColor.BLUE_BOLD);
                    stopProcess();
                }
            }

            @Override
            public void childrenChanged(List<String> children) {
                printColoured(children.size() + " is the number of children for node: " + node, ConsoleColor.MAGENTA_BOLD);
            }

            @Override
            public void closing() {
                printColoured("Lost connection, stopping...", ConsoleColor.RED_BOLD);
                System.exit(-1);
            }
        };
    }

    private void handleUserInput(NodeWatcher nodeWatcher) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine();

            if ("tree".equals(line)) {
                System.out.println("Printing tree:");
                nodeWatcher.printTree();
            } else if ("exit".equals(line)) {
                stopProcess();
                System.exit(0);
            }
        }
    }
}
