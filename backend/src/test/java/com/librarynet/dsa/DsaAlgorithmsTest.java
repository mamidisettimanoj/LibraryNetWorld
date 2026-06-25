package com.librarynet.dsa;

import com.librarynet.dsa.model.Book;
import com.librarynet.dsa.sorting.MergeSort;
import com.librarynet.dsa.trees.AVLTree;
import com.librarynet.dsa.trees.BSTLibrary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DsaAlgorithmsTest {
    @Test
    void treesRemainValid() {
        BSTLibrary bst = new BSTLibrary();
        AVLTree avl = new AVLTree();
        for (int id : new int[]{50, 30, 70, 20, 40, 60, 80}) {
            Book book = new Book(id, "Book " + id, "Author", "Category");
            bst.addResource(book);
            avl.addResource(book);
        }
        assertTrue(bst.isValidBST());
        assertTrue(avl.isBalanced());
        assertEquals(7, avl.inorder().size());
    }

    @Test
    void mergeSortOrdersValues() {
        int[] values = {8, 2, 5, 1, 9, 3};
        MergeSort.sort(values);
        assertArrayEquals(new int[]{1, 2, 3, 5, 8, 9}, values);
    }
}
