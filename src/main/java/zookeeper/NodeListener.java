package zookeeper;

import java.util.List;

public interface NodeListener {

    void changed(boolean exists);

    void childrenChanged(List<String> children);

    void closing();
}
