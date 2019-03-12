import java.util.*;

public class ScapeGoatTree<T extends Comparable<T>> extends AbstractCollection<T> implements Set<T> {

    private SGTNode<T> root = null;
    private int size = getSize(root);
    private double alpha = 0.5;
    private double maxDepth = Math.log(size) / Math.log(1 / alpha);

    class SGTNode<T extends Comparable<T>> {
        int depth = -1;
        SGTNode<T> right;
        SGTNode<T> left;
        SGTNode<T> parent;
        T value;

        SGTNode(T value) {
            this.value = value;
        }
    }

    private SGTNode<T> find(T value) {
        if (root == null) return null;
        else return find(root, value);
    }

    private SGTNode<T> find(SGTNode<T> node, T value) {
        int compare = value.compareTo(node.value);
        if (compare == 0) {
            return node;
        } else if (compare < 0) {
            if (node.left == null) return node;
            return find(node.left, value);
        } else {
            if (node.right == null) return node;
            return find(node.right, value);
        }
    }


    @Override
    public int size() {
        return size;
    }

    private int getSize(SGTNode<T> node) {
        if (node == null) return 0;
        return 1 + getSize(node.left) + getSize(node.right);
    }


    private List<SGTNode<T>> flattenTree(SGTNode<T> node) {
        List<SGTNode<T>> sgtNodes = new ArrayList<>();
        if (node == null) return sgtNodes;
        flattenTree(node.left);
        sgtNodes.add(node);
        flattenTree(node.right);
        return sgtNodes;
    }

    private SGTNode<T> buildHeightBalancedTree(List<SGTNode<T>> list, int depth) {
        if (list.size() == 0) return null;
        if (list.size() == 1) return list.get(0);
        int mid;
        if (list.size() % 2 != 0) mid = list.size() / 2;
        else mid = list.size() / 2 - 1;
        SGTNode<T> newRoot = list.get(mid);
        newRoot.depth = depth;
        SGTNode<T> recursionLeft = buildHeightBalancedTree(list.subList(0, mid), newRoot.depth + 1);
        if (recursionLeft != null) {
            recursionLeft.parent = newRoot;
            recursionLeft.depth = newRoot.depth + 1;
        }
        SGTNode<T> recursionRight = buildHeightBalancedTree(list.subList(mid + 1, list.size()), newRoot.depth + 1);
        if (recursionRight != null) {
            recursionRight.parent = newRoot;
            recursionRight.depth = newRoot.depth + 1;
        }
        newRoot.left = recursionLeft;
        newRoot.right = recursionRight;
        return newRoot;
    }

    private void rebuildTree(SGTNode<T> scapegoat) {
        SGTNode<T> newRoot = buildHeightBalancedTree(flattenTree(scapegoat), scapegoat.depth);
        if (scapegoat == root) root = newRoot;
        else {
            if (newRoot != null) newRoot.parent = scapegoat.parent;
            if (scapegoat.parent.right != null) {
                if (scapegoat.parent.right.value == scapegoat.value) scapegoat.parent.right = newRoot;
                else scapegoat.parent.left = newRoot;
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new IteratorSGT();
    }


    public class IteratorSGT implements Iterator<T> {

        Stack<SGTNode<T>> stack;

        IteratorSGT() {
          toStack(root);
        }

        private void toStack(SGTNode<T> node) {
            stack = new Stack<>();
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public T next() {
            SGTNode<T> node = stack.pop();
            T result = node.value;
            if (node.right != null) {
                node = node.right;
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }
            return result;
        }
    }

    private int findMaxDepth() {
        Iterator<T> iterator = this.iterator();
        int max = -1;
        while (iterator.hasNext()) {
            int current = find(iterator.next()).depth;
            if (current > max) max = current;
        }
        return max;
    }

    private SGTNode<T> getScapegoat(SGTNode<T> node) {
        SGTNode<T> scapegoat = node;
        SGTNode<T> temp = node;
        while (temp.parent != null) {
            temp = temp.parent;
            if (getSize(temp.left) > alpha * getSize(temp.right) ||
                    getSize(temp.right) > alpha * getSize(temp.left)) {
                scapegoat = temp;
            }
        }
        return scapegoat;
    }

    @Override
    public boolean add(T value) {
        SGTNode<T> node = find(value);
        int comparison = node == null ? -1 : value.compareTo(node.value);
        if (comparison == 0) {
            return false;
        }
        SGTNode<T> newNode = new SGTNode<>(value);
        if (node == null) {
            newNode.depth = 0;
            root = newNode;
        } else if (comparison < 0) {
            assert (node.left == null);
            newNode.parent = node;
            newNode.depth = node.depth + 1;
            node.left = newNode;
        } else {
            assert (node.right == null);
            newNode.parent = node;
            newNode.depth = node.depth + 1;
            node.right = newNode;
        }
        size++;
        this.maxDepth = Math.log(size) / Math.log(1 / alpha);
        if (newNode.depth > maxDepth) {
            rebuildTree(getScapegoat(newNode));
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c.isEmpty()) return false;
        else {
            for (T element : c) {
                add(element);
            }
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        T value = (T) o;
        if (root == null) return false;
        SGTNode<T> del = delete(root, value);
        if (del != null) {
            root = del;
        } else return false;
        size--;
        maxDepth = Math.log(size) / Math.log(1 / alpha);
        if (findMaxDepth() > maxDepth) {
            rebuildTree(root);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) return false;
        for (Object element : c) {
            if (contains(element)) {
                remove(element);
            } else return false;
        }
        return true;
    }

    private SGTNode<T> delete(SGTNode<T> root, T value) {
        int comparison = value.compareTo(root.value);
        if (comparison > 0) {
            root.right = delete(root.right, value);
        } else if (comparison < 0) {
            root.left = delete(root.left, value);
        } else if (root.left != null && root.right != null) {
            SGTNode<T> temp = root.right;
            while (temp.left != null) temp = temp.left;
            root.value = temp.value;
            root.right = delete(root.right, temp.value);
        } else if (root.left != null) {
            root.left.parent = root.parent;
            root.left.depth = root.depth;
            return root.left;
        } else {
            if (root.right!=null) {
                root.right.parent = root.parent;
                root.right.depth = root.depth;
            }
            return root.right;
        }
        return root;
    }

    @Override
    public boolean contains(Object o) {
        T value = (T) o;
        SGTNode<T> node = find(value);
        return node != null && value.compareTo(node.value) == 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c.isEmpty()) return false;
        for (Object element : c) {
            if (!this.contains(element)) return false;
        }
        return true;
    }

    void setAlpha(double a) {
        alpha = Math.max(a, 0.5);
        alpha = Math.min(1, alpha);
        if (root != null && findMaxDepth() > maxDepth) {
            rebuildTree(root);
        }
    }

    double getAlpha(){return alpha;}

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    SGTNode<T> getRoot() {
        return root;
    }
}
