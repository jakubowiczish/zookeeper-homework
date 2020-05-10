package zookeeper;

import lombok.SneakyThrows;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.Objects;

import static org.apache.zookeeper.KeeperException.Code.NONODE;
import static org.apache.zookeeper.Watcher.Event.EventType.*;
import static org.apache.zookeeper.Watcher.Event.KeeperState.Expired;

public class NodeWatcher implements Watcher {

    private static final int sessionTimeout = 5000;

    private final String node;
    private final NodeListener nodeListener;

    private final ZooKeeper zooKeeper;

    @SneakyThrows
    public NodeWatcher(final String connectString,
                       final String node,
                       final NodeListener nodeListener) {

        this.node = node;
        this.nodeListener = nodeListener;

        zooKeeper = new ZooKeeper(connectString, sessionTimeout, this);
        watchForExistingNode();
    }

    @Override
    public final void process(WatchedEvent watchedEvent) {
        if (isTypeNoneAndStateExpired(watchedEvent)) {
            closeEvent();
        } else if (isTypeNodeCreatedOrNodeDeleted(watchedEvent) && isNodeEqualToEventPath(watchedEvent)) {
            watchForExistingNode();
        } else if (isTypeNodeChildrenChanged(watchedEvent) && isNodeEqualToEventPath(watchedEvent)) {
            watchForChildren();
        }
    }

    public final void printTreeForNode() {
        printTreeForNode(this.node);
    }

    private boolean isTypeNoneAndStateExpired(WatchedEvent watchedEvent) {
        return watchedEvent.getType() == None && watchedEvent.getState() == Expired;
    }

    private boolean isTypeNodeCreatedOrNodeDeleted(WatchedEvent watchedEvent) {
        return watchedEvent.getType() == NodeCreated || watchedEvent.getType() == NodeDeleted;
    }

    private boolean isTypeNodeChildrenChanged(WatchedEvent watchedEvent) {
        return watchedEvent.getType() == NodeChildrenChanged;
    }

    private boolean isNodeEqualToEventPath(WatchedEvent watchedEvent) {
        return Objects.equals(node, watchedEvent.getPath());
    }

    private void printTreeForNode(String node) {
        System.out.println(node);

        try {
            final Stat stat = zooKeeper.exists(node, false);

            if (stat != null) {
                List<String> children = zooKeeper.getChildren(node, false);

                children.stream()
                        .map(child -> node + "/" + child)
                        .forEach(this::printTreeForNode);
            }
        } catch (KeeperException e) {
            closeEvent();
            System.out.println(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    private void closeEvent() {
        nodeListener.closed();
    }

    private void watchForExistingNode() {
        try {
            final Stat stat = zooKeeper.exists(node, true);

            boolean isStatNotNull = stat != null;
            nodeListener.changed(isStatNotNull);

            if (stat != null) {
                watchForChildren();
            }

        } catch (KeeperException e) {
            closeEvent();
            System.out.println(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void watchForChildren() {
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
