import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ScapeGoatTreeTest {

    @Test
    void add() {
        List<Integer> list = Arrays.asList(3, 6, 11, 4, 5);
        ScapeGoatTree<Integer> tree = new ScapeGoatTree<>();
        tree.addAll(list);
        assertTrue(tree.containsAll(list));
        assertTrue(tree.size() == 5);
    }

    @Test
    void iterator() {
        ScapeGoatTree<Integer> tree = new ScapeGoatTree<>();
        List<Integer> list = Arrays.asList(3, 6, 11, 4, 5);
        tree.addAll(list);
        Iterator<Integer> iterator = tree.iterator();
        List<Integer> list2 = new ArrayList<>();
        while (iterator.hasNext()) {
           list2.add(iterator.next());
        }
        Collections.sort(list);
        assertEquals(list,list2);
    }

    @Test
    void remove() {
        List<Integer> list = Arrays.asList(3, 6, 11, 4, 5);
        ScapeGoatTree<Integer> tree = new ScapeGoatTree<>();
        tree.addAll(list);
        tree.remove(6);
        assertTrue(!tree.contains(6));
    }

    @Test
    void setAlpha() {
        ScapeGoatTree<Integer> tree = new ScapeGoatTree<>();
        tree.setAlpha(0.7);
        assertTrue(tree.getAlpha() == 0.7);
        tree.setAlpha(100);
        assertTrue(tree.getAlpha() == 1.0);
    }

    @Test
    void isEmpty() {
        ScapeGoatTree<Integer> tree = new ScapeGoatTree<>();
        assertTrue(tree.isEmpty());
    }

    @Test
    void clear() {
        ScapeGoatTree<Integer> tree = new ScapeGoatTree<>();
        tree.add(3);
        tree.clear();
        assertTrue(tree.isEmpty());
    }

    @Test
    void getRoot() {
        ScapeGoatTree<Integer> tree = new ScapeGoatTree<>();
        tree.add(3);
        assertTrue(tree.getRoot().value == 3);
    }
}