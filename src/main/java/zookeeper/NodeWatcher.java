package zookeeper;

import lombok.SneakyThrows;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

import static org.apache.zookeeper.KeeperException.Code.NONODE;
import static org.apache.zookeeper.Watcher.Event.EventType.*;
import static org.apache.zookeeper.Watcher.Event.KeeperState.Expired;

public class NodeWatcher implements Watcher {

    private static final int sessionTimeout = 5000;

    private final ZooKeeper zooKeeper;

    private final String node;
    private final NodeListener nodeListener;

    @SneakyThrows
    public NodeWatcher(final String connectString,
                       final String node,
                       final NodeListener nodeListener) {

        this.node = node;
        this.nodeListener = nodeListener;

        zooKeeper = new ZooKeeper(connectString, sessionTimeout, this);
        checkExistence();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == None && watchedEvent.getState() == Expired) {
            closeEvent();
        } else if (watchedEvent.getType() == NodeCreated || watchedEvent.getType() == NodeDeleted) {
            if (node.equals(watchedEvent.getPath())) {
                checkExistence();
            }
        } else if (watchedEvent.getType() == NodeChildrenChanged) {
            if (node.equals(watchedEvent.getPath())) {
                checkChildren();
            }
        }
    }

    public void printTree() {
        printTree(this.node);
    }

    private void printTree(String node) {
        System.out.println(node);
        try {
            Stat stat = zooKeeper.exists(node, false);

            if (stat != null) {
                List<String> children = zooKeeper.getChildren(node, false);

                children.stream()
                        .map(child -> node + "/" + child)
                        .forEach(this::printTree);
            }
        } catch (KeeperException e) {
            closeEvent();
            System.out.println(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    private void closeEvent() {
        nodeListener.closing();
    }

    private void checkExistence() {
        try {
            final Stat stat = zooKeeper.exists(node, true);

            boolean isStatNotNull = stat != null;
            nodeListener.changed(isStatNotNull);

            if (stat != null) {
                checkChildren();
            }

        } catch (KeeperException e) {
            closeEvent();
            System.out.println(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void checkChildren() {
        try {
            List<String> children = zooKeeper.getChildren(node, true);
            nodeListener.childrenChanged(children);
        } catch (KeeperException e) {
            if (e.code() != NONODE) {
                closeEvent();
                System.out.println(e.getLocalizedMessage());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

}
