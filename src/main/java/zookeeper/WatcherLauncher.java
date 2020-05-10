package zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

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

        System.out.println("App started running");

        handleUserInput(nodeWatcher);
    }

    public final void startProcess() {
        if (process != null) return;

        final ProcessBuilder processBuilder = new ProcessBuilder(commands)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            System.out.println("Starting program");
            process = processBuilder.start();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

    public final void stopProcess() {
        if (process == null) return;

        System.out.println("Stopping program...");
        process.destroy();
        process = null;
    }

    private NodeListener createNodeListener() {
        return new NodeListener() {

            @Override
            public void changed(boolean exists) {
                if (exists) {
                    System.out.println("Node still exists!");
                    startProcess();
                } else {
                    System.out.println("Node does not exist anymore!");
                    stopProcess();
                }
            }

            @Override
            public void childrenChanged(List<String> children) {
                System.out.println(children.size() + " is the number of children for node: " + node);
            }

            @Override
            public void closing() {
                System.out.println("Lost connection, stopping...");
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
