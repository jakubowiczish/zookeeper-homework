package zookeeper;

import lombok.SneakyThrows;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

import static zookeeper.Util.print;
import static zookeeper.Util.printErr;

public class NodeWatcher implements Watcher {

    private static final int SESSION_TIMEOUT = 5000;

    private final String rootNode;
    private final String processName;

    private final ZooKeeper zooKeeper;

    private Process process;
    private boolean isProcessRunning = false;

    @SneakyThrows
    public NodeWatcher(final String connectString, final String rootNode, final String processName) {
        this.rootNode = rootNode;
        this.processName = processName;

        zooKeeper = new ZooKeeper(connectString, SESSION_TIMEOUT, this);
        print("Program has started");
        watchFor(rootNode);
    }

    private void watchFor(String node) {
        try {
            if (exists(node)) {
                List<String> children = zooKeeper.getChildren(node, true);
                children.stream()
                        .map(child -> node + "/" + child)
                        .forEach(this::watchFor);
            }
        } catch (InterruptedException e) {
            printErr(e);
        } catch (KeeperException e) {
            print("Server is now unreachable");
        }
    }

    @Override
    public void process(WatchedEvent event) {
        watchFor(rootNode);

        switch (event.getType()) {
            case NodeCreated: {
                if (rootNode.equals(event.getPath()))
                    startNamedProcess();
                break;
            }

            case NodeDeleted: {
                if (rootNode.equals(event.getPath()))
                    destroyNamedProcess();
                break;
            }

            case NodeChildrenChanged: {
                int count = countChildren(rootNode);
                print("The node: " + rootNode + " has now " + count + " descendants.");
                break;
            }
        }
    }

    public int countChildren(String node) {
        int childrenCounter = 0;

        try {
            if (exists(node)) {
                List<String> children = zooKeeper.getChildren(node, true);
                childrenCounter += children.size();
                for (String child : children) {
                    childrenCounter += countChildren(node + "/" + child);
                }
            }
        } catch (KeeperException | InterruptedException e) {
            print("Problem with counting number of children");
            printErr(e);
        }

        return childrenCounter;
    }

    public void printTree(String node) {
        print(node);

        try {
            if (exists(node)) {
                List<String> children = zooKeeper.getChildren(node, true);
                children.stream()
                        .map(child -> node + "/" + child)
                        .forEach(this::printTree);
            }
        } catch (KeeperException | InterruptedException e) {
            print("Problem with printing the tree of nodes");
            printErr(e);
        }
    }

    public void startNamedProcess() {
        if (isProcessRunning) return;

        print("Starting the process: " + processName);
        ProcessBuilder pb = new ProcessBuilder(processName);
        try {
            process = pb.start();
            isProcessRunning = true;
        } catch (IOException e) {
            print("Problem with starting the process: " + processName);
            printErr(e);
        }
    }

    public void destroyNamedProcess() {
        if (!isProcessRunning) return;

        print("Killing the process: " + processName);
        process.destroy();
        isProcessRunning = false;
    }

    @SneakyThrows
    private boolean exists(String node) {
        return zooKeeper.exists(node, true) != null;
    }

}