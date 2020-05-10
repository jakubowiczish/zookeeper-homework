package zookeeper;

import static java.lang.System.out;

public class ZookeeperRunner {

    private static final String NODE = "/z";

    private static final String PROGRAM_USAGE_HELP_MESSAGE = "Program usage: java -jar zookeeper-homework.jar connect_string other_args...";

    public static void main(String[] args) {
        if (args.length == 0) {
            out.println(PROGRAM_USAGE_HELP_MESSAGE);
        } else {
            final String[] commands = new String[args.length - 1];
            System.arraycopy(args, 1, commands, 0, commands.length);

            final String connectString = args[0];

            final WatcherLauncher watcherLauncher = new WatcherLauncher(connectString, NODE, commands);
            watcherLauncher.start();
        }
    }
}
