package zookeeper;

public class ZookeeperRunner {

    private static final String node = "/z";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Program usage: java -jar zookeeper-homework.jar connect_string other_args...");
        } else {
            final String[] commands = new String[args.length - 1];
            System.arraycopy(args, 1, commands, 0, commands.length);

            final String connectString = args[0];

            final WatcherLauncher watcherLauncher = new WatcherLauncher(connectString, node, commands);
            watcherLauncher.start();
        }
    }
}
