package chess;

import java.util.ArrayList;
import java.util.List;

public abstract class Tree<T> {

    private T data;
    private List<Tree<T>> children = new ArrayList<>();
    private Tree parent = null;

    Tree(T data){
        this.data = data;
    }

    Tree<T> addChild (Tree<T> child){
        child.setParent(this);
        this.children.add(child);
        return child;
    }

    void addChildren (List<Tree<T>> children){
        for(Tree<T> child : children){
            child.setParent(this);
        }
        this.children.addAll(children);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<Tree<T>> getChildren() {
        return children;
    }

    public void setChildren(List<Tree<T>> children) {
        this.children = children;
    }

    public Tree getParent() {
        return parent;
    }

    public void setParent(Tree parent) {
        this.parent = parent;
    }
}
