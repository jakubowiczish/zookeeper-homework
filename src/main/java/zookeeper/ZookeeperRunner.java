package zookeeper;

import util.ConsoleColor;

import static util.ColouredPrinter.printlnColoured;

public class ZookeeperRunner {

    public static void main(String[] args) {
        if (args.length == 0) {
            printlnColoured(
                    "Program usage: java -jar zookeeper-homework.jar connect_string other_args...",
                    ConsoleColor.RED_BOLD);
        } else {
            final String[] commands = new String[args.length - 1];
            System.arraycopy(args, 1, commands, 0, commands.length);

            final String connectString = args[0];

            final WatcherLauncher watcherLauncher = new WatcherLauncher(connectString, "/z", commands);
            watcherLauncher.start();
        }
    }
}
