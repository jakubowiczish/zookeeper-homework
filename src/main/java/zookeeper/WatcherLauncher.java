package zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;


public class WatcherLauncher implements Launcher {

    private static final String WATCHER_PREFIX = "[WATCHER_INFO] ";

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

        out.println("App started running!");

        handleUserInput(nodeWatcher);
    }

    public final void startProcess() {
        if (process != null) return;

        final ProcessBuilder processBuilder = new ProcessBuilder(commands)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            out.println("Starting program");
            process = processBuilder.start();
        } catch (IOException e) {
            out.println(e.getLocalizedMessage());
        }

    }

    public final void stopProcess() {
        if (process == null) return;

        out.println("Stopping recently opened program...");
        process.destroy();
        process = null;
    }

    private NodeListener createNodeListener() {
        return new NodeListener() {

            @Override
            public void changed(boolean exists) {
                if (exists) {
                    out.println(WATCHER_PREFIX + "Node still exists!");
                    startProcess();
                } else {
                    out.println(WATCHER_PREFIX + "Node does not exist anymore!");
                    stopProcess();
                }
            }

            @Override
            public void childrenChanged(List<String> children) {
                out.println(WATCHER_PREFIX + children.size() + " is the number of children for node: " + node);
            }

            @Override
            public void closed() {
                out.println(WATCHER_PREFIX + "Lost connection, stopping...");
                System.exit(-1);
            }
        };
    }

    private void handleUserInput(NodeWatcher nodeWatcher) {
        final Scanner scanner = new Scanner(System.in);

        while (true) {
            final String inputLine = scanner.nextLine();

            if ("tree".equals(inputLine)) {
                out.println("Printing the tree of nodes below:");
                nodeWatcher.printTreeForNode();
            } else if ("exit".equals(inputLine)) {
                stopProcess();
                System.exit(0);
            }
        }
    }
}
