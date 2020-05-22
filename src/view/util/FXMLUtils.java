package view.util;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class FXMLUtils {

    public static Node getChildWithId(Pane root, String id) {
        ObservableList<Node> children = root.getChildren();
        for (Node child: children) {
            if (child.getId() != null && child.getId().equals(id)) {
                return child;
            } else if (child instanceof Pane) {
                Node recursiveResult = getChildWithId((Pane) child, id);
                if (recursiveResult != null) {
                    return recursiveResult;
                }
            }
        }
        return null;
    }

    public static List<Node> getChildrenWithId(Node root, String id) {
        // TODO
        throw new NotImplementedException();
    }

    public static List<Node> applyStyleClassToAll(List<Node> nodes, String styleClass) {
        // TODO
        throw new NotImplementedException();
    }
}
