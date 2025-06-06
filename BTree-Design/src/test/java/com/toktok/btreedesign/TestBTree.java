package com.toktok.btreedesign;

import com.toktok.btreedesign.entity.po.Book;
import com.toktok.btreedesign.utils.KeyValue;
import com.toktok.btreedesign.utils.BTree;
import com.toktok.btreedesign.utils.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestBTree {

//    @Test
//    @DisplayName("split(t=2)，分裂叶子节点")
//    void testSplit1() {
//        /*
//                5               2|5
//              /   \     ==>    / | \
//           1|2|3   6          1  3  6
//         */
//        BTree tree = new BTree();
//        Node root = tree.root;
//        root.leaf = false;
//
//        KeyValue keyValue = new KeyValue();
//        keyValue.setKey(5);
//        keyValue.setBook(new Book());
//        root.keyValues[0] = keyValue;
//        root.keyNumber = 1;
//
//        KeyValue[] keyValues = new KeyValue[3];
//        KeyValue keyValue1 = new KeyValue();
//        keyValue1.setKey(1);
//        keyValue1.setBook(new Book());
//        keyValues[0] = keyValue1;
//
//        KeyValue keyValue2 = new KeyValue();
//        keyValue2.setKey(2);
//        keyValue2.setBook(new Book());
//        keyValues[1] = keyValue2;
//
//        KeyValue keyValue3 = new KeyValue();
//        keyValue3.setKey(3);
//        keyValue3.setBook(new Book());
//        keyValues[2] = keyValue3;
//
//        root.children[0] = new Node(keyValues);
//        root.children[0].keyNumber = 3;
//
//        KeyValue[] keyValues2 = new KeyValue[1];
//        KeyValue keyValue4 = new KeyValue();
//        keyValue4.setKey(6);
//        keyValue4.setBook(new Book());
//        keyValues2[0] = keyValue4;
//        root.children[1] = new Node(keyValues2);
//        root.children[1].keyNumber = 1;
//
//        tree.split(root.children[0], root, 0);
//        System.out.println(root.toString());
//        System.out.println(root.children[0].toString());
//        System.out.println(root.children[1].toString());
//        System.out.println(root.children[2].toString());

//        Assertions.assertEquals("[2, 5]", root.toString());
//        Assertions.assertEquals("[1]", root.children[0].toString());
//        Assertions.assertEquals("[3]", root.children[1].toString());
//        Assertions.assertEquals("[6]", root.children[2].toString());
//    }

//    @Test
//    @DisplayName("split(t=3)，分裂叶子节点")
//    void testSplit2() {
//        /*
//                  6                 3|6
//               /     \     ==>    /  |  \
//           1|2|3|4|5  7         1|2 4|5  7
//
//         */
//        BTree tree = new BTree(3);
//        Node root = tree.root;
//        root.leaf = false;
//        root.keys[0] = 6;
//        root.keyNumber = 1;
//        root.children[0] = new Node(new int[]{1, 2, 3, 4, 5});
//        root.children[0].keyNumber = 5;
//
//        root.children[1] = new Node(new int[]{7});
//        root.children[1].keyNumber = 1;
//
//        tree.split(root.children[0], root, 0);
//        assertEquals("[3, 6]", root.toString());
//        assertEquals("[1, 2]", root.children[0].toString());
//        assertEquals("[4, 5]", root.children[1].toString());
//        assertEquals("[7]", root.children[2].toString());
//    }
//
//    @Test
//    @DisplayName("put(t=2)，添加1-11节点")
//    void testPut() {
//        /*
//                    // 1根节点分裂 //
//                                  2
//                                 / \
//              1|2|3       =>    1   3
//
//
//
//                    // 2叶子节点分裂 //
//
//                2                2|4
//               / \        =>    / | \
//              1 3|4|5          1  3  5
//
//
//
//                  // 3叶子节点分裂，后根节点分裂 //
//                                                    4
//                2|4             2|4|6              / \
//               / | \      =>   / / \ \     =>     2   6
//              1  3 5|6|7      1  3  5 7          / \ / \
//                                                1  3 5  7
//
//
//
//                      // 4 == 2 //
//
//                4                 4
//               / \              /   \
//              2   6      =>    2    6|8
//             /\  / \          /\   / | \
//            1  3 5 7|8|9     1  3 5  7  9
//
//
//
//                // 5叶子节点分裂，后非叶子节点分裂//
//
//                 4                   4                   4|8
//               /   \               /   \                / | \
//              2    6|8      =>    2    6|8|10      =>  2  6  10
//             /\   / | \          /\   / / \ \         /\  /\  /\
//            1  3 5  7 9|10|11   1  3 5 7  9  11      1 3 5 7 9 11
//        */
//
//        // 验证最后结果
//        BTree tree = new BTree();
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//        tree.put(5);
//        tree.put(6);
//        tree.put(7);
//        tree.put(8);
//        tree.put(9);
//        tree.put(10);
//        tree.put(11);
//        assertEquals("[4, 8]", tree.root.toString());
//        assertEquals("[2]", tree.root.children[0].toString());
//        assertEquals("[6]", tree.root.children[1].toString());
//        assertEquals("[10]", tree.root.children[2].toString());
//        assertEquals("[1]", tree.root.children[0].children[0].toString());
//        assertEquals("[3]", tree.root.children[0].children[1].toString());
//        assertEquals("[5]", tree.root.children[1].children[0].toString());
//        assertEquals("[7]", tree.root.children[1].children[1].toString());
//        assertEquals("[9]", tree.root.children[2].children[0].toString());
//        assertEquals("[11]", tree.root.children[2].children[1].toString());
//
//        tree.travel();
//    }


    @Test
    void testSearch() {
        BTree tree = new BTree();

        // 插入 KeyValue 对象
        tree.put(new KeyValue(1, new Book()));
        tree.put(new KeyValue(2, new Book()));
        tree.put(new KeyValue(3, new Book()));
        tree.put(new KeyValue(4, new Book()));
        tree.put(new KeyValue(5, new Book()));
        tree.put(new KeyValue(6, new Book()));
        tree.put(new KeyValue(7, new Book()));
        tree.put(new KeyValue(8, new Book()));
        tree.put(new KeyValue(9, new Book()));
        tree.put(new KeyValue(10, new Book()));

        tree.travel();
    }


//    @Test
//    void testPut3() {
//        BTree tree = new BTree();
//        tree.put(6);
//        tree.put(3);
//        tree.put(8);
//        tree.put(1);
//        tree.put(2);
//        tree.put(5);
//        tree.put(4);
//        assertEquals("[4]", tree.root.toString());
//        assertEquals("[2]", tree.root.children[0].toString());
//        assertEquals("[6]", tree.root.children[1].toString());
//        assertEquals("[1]", tree.root.children[0].children[0].toString());
//        assertEquals("[3]", tree.root.children[0].children[1].toString());
//        assertEquals("[5]", tree.root.children[1].children[0].toString());
//        assertEquals("[8]", tree.root.children[1].children[1].toString());
//    }
//
//    @Test
//    @DisplayName("case1: leaf && not found")
//    public void testRemove0() {
//        /*
//             1|2|3|4
//         */
//        BTree tree = new BTree(3);
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//
//        tree.remove(0);
//        tree.remove(8);
//        assertEquals("[1, 2, 3, 4]", tree.root.toString());
//    }


    @Test
    @DisplayName("case3: non-leaf && not found")
    public void testRemove1() {
        /*
                 3
               /   \
             1|2  4|5|6|7
         */
        BTree tree = new BTree(3);
        tree.put(new KeyValue(1, new Book()));
        tree.put(new KeyValue(2, new Book()));
        tree.put(new KeyValue(3, new Book()));
        tree.put(new KeyValue(4, new Book()));
        tree.put(new KeyValue(5, new Book()));
        tree.put(new KeyValue(6, new Book()));
        tree.put(new KeyValue(7, new Book()));

        tree.remove(new KeyValue(0, new Book()));
        tree.remove(new KeyValue(8, new Book()));
        System.out.println(tree.root.toString());
        System.out.println(tree.root.children[0].toString());
        System.out.println(tree.root.children[1].toString());
    }


//    @Test
//    @DisplayName("case2: remove directly")
//    public void testRemove2() {
//        /*
//                 3
//               /   \
//             1|2  4|5|6|7
//         */
//        BTree tree = new BTree(3);
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//
//        tree.remove(3);
//        assertEquals("[1, 2, 4]", tree.root.toString());
//        tree.remove(1);
//        assertEquals("[2, 4]", tree.root.toString());
//    }


//    @Test
//    @DisplayName("case4: replace with successor")
//    public void testRemove3() {
//        /*
//                 3
//               /   \
//             1|2  4|5|6|7
//         */
//        BTree tree = new BTree(3);
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//        tree.put(5);
//        tree.put(6);
//        tree.put(7);
//
//        tree.remove(3);
//        assertEquals("[4]", tree.root.toString());
//        assertEquals("[1, 2]", tree.root.children[0].toString());
//        assertEquals("[5, 6, 7]", tree.root.children[1].toString());
//    }

//    @Test
//    @DisplayName("case5: balance right rotate")
//    public void testRemove4() { // 右旋
//        /*
//                  4
//                /   \
//             1|2|3  5|6
//         */
//        BTree tree = new BTree(3);
//        tree.put(1);
//        tree.put(2);
//        tree.put(4);
//        tree.put(5);
//        tree.put(6);
//        tree.put(3);
//
//        tree.remove(5);
//        assertEquals("[3]", tree.root.toString());
//        assertEquals("[1, 2]", tree.root.children[0].toString());
//        assertEquals("[4, 6]", tree.root.children[1].toString());
//    }

//    @Test
//    @DisplayName("case5: balance left rotate")
//    public void testRemove5() {
//        /*
//                  3
//                /   \
//             1|2   4|5|6
//
//                  4
//                /   \
//              1|3   5|6
//         */
//        BTree tree = new BTree(3);
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//        tree.put(5);
//        tree.put(6);
//
//        tree.remove(2);
//        assertEquals("[4]", tree.root.toString());
//        assertEquals("[1, 3]", tree.root.children[0].toString());
//        assertEquals("[5, 6]", tree.root.children[1].toString());
//    }

//    @Test
//    @DisplayName("case5: balance merge a")
//    public void testRemove6() { // 合并
//        /*
//                  3
//                /   \
//             1|2    4|5
//         */
//        BTree tree = new BTree(3);
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//        tree.put(5);
//
//        tree.remove(4);
//        assertEquals("[1, 2, 3, 5]", tree.root.toString());
//    }

//    @Test
//    @DisplayName("case5: balance merge b")
//    public void testRemove7() { // 合并
//        /*
//                  3
//                /   \
//             1|2    4|5
//         */
//        BTree tree = new BTree(3);
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//        tree.put(5);
//
//        tree.remove(2);
//        assertEquals("[1, 3, 4, 5]", tree.root.toString());
//    }
//
//    @Test
//    @DisplayName("case6: from right to left")
//    void testRemove8() {
//        /*
//
//                4|8                    4
//               / | \                  / \
//              2  6  10        =>     2  6|8
//             /\  /\  /\             /\ / | \
//            1 3 5 7 9 11           1 3 5 7 9|10
//
//
//                4                       4
//               / \                     / \
//              2  6|8        =>        2   6
//             /\ / | \                /\  / \
//            1 3 5 7  9              1 3 5  7|8
//
//
//                4                       4
//               / \                     / \
//              2   6         =>        2   _      =>     2|4
//             /\   /\                 /\  /             / | \
//            1  3 5  7               1 3 5|6           1 3 5|6
//
//              2|4                       2
//             / | \          =>         / \
//            1 3  5                    1  3|4
//
//        */
//        BTree tree = new BTree();
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//        tree.put(5);
//        tree.put(6);
//        tree.put(7);
//        tree.put(8);
//        tree.put(9);
//        tree.put(10);
//        tree.put(11);
//        tree.remove(11);
//        assertEquals("[4]", tree.root.toString());
//        assertEquals("[2]", tree.root.children[0].toString());
//        assertEquals("[6, 8]", tree.root.children[1].toString());
//        assertEquals("[1]", tree.root.children[0].children[0].toString());
//        assertEquals("[3]", tree.root.children[0].children[1].toString());
//        assertEquals("[5]", tree.root.children[1].children[0].toString());
//        assertEquals("[7]", tree.root.children[1].children[1].toString());
//        assertEquals("[9, 10]", tree.root.children[1].children[2].toString());
//        tree.remove(10);
//        assertEquals("[9]", tree.root.children[1].children[2].toString());
//        tree.remove(9);
//        assertEquals("[6]", tree.root.children[1].toString());
//        assertEquals("[5]", tree.root.children[1].children[0].toString());
//        assertEquals("[7, 8]", tree.root.children[1].children[1].toString());
//        tree.remove(8);
//        assertEquals("[7]", tree.root.children[1].children[1].toString());
//        tree.remove(7);
//        assertEquals("[2, 4]", tree.root.toString());
//        assertEquals("[1]", tree.root.children[0].toString());
//        assertEquals("[3]", tree.root.children[1].toString());
//        assertEquals("[5, 6]", tree.root.children[2].toString());
//        tree.remove(6);
//        assertEquals("[5]", tree.root.children[2].toString());
//        tree.remove(5);
//        assertEquals("[2]", tree.root.toString());
//        assertEquals("[1]", tree.root.children[0].toString());
//        assertEquals("[3, 4]", tree.root.children[1].toString());
//        tree.remove(4);
//        assertEquals("[3]", tree.root.children[1].toString());
//        tree.remove(3);
//        assertEquals("[1, 2]", tree.root.toString());
//        tree.remove(2);
//        assertEquals("[1]", tree.root.toString());
//        tree.remove(1);
//        assertEquals("[]", tree.root.toString());
//    }

//    @Test
//    @DisplayName("case6: from left to right")
//    void testRemove9() {
//        /*
//
//                4|8                    4|8                  8
//               / | \                  / | \                / \
//              2  6  10        =>     _  6  10       =>   4|6  10
//             /\  /\  /\             /   /\  /\          / | \  /\
//            1 3 5 7 9 11           2|3 5 7 9 11       2|3 5 7 9 11
//
//
//            remove(2,3) =>  8
//                           / \
//                          6   10
//                         / \  /\
//                        4|5 7 9 11
//
//            remove(4,5) =>  8         =>        8|10
//                           / \                 / | \
//                          _   10             6|7 9 11
//                         / \  /\
//                        6|7   9 11
//
//            remove(6,7)   =>   10
//                               / \
//                             8|9 11
//        */
//        BTree tree = new BTree();
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//        tree.put(5);
//        tree.put(6);
//        tree.put(7);
//        tree.put(8);
//        tree.put(9);
//        tree.put(10);
//        tree.put(11);
//        tree.remove(1);
//        assertEquals("[8]", tree.root.toString());
//        assertEquals("[4, 6]", tree.root.children[0].toString());
//        assertEquals("[10]", tree.root.children[1].toString());
//        assertEquals("[2, 3]", tree.root.children[0].children[0].toString());
//        assertEquals("[5]", tree.root.children[0].children[1].toString());
//        assertEquals("[7]", tree.root.children[0].children[2].toString());
//        assertEquals("[9]", tree.root.children[1].children[0].toString());
//        assertEquals("[11]", tree.root.children[1].children[1].toString());
//        tree.remove(2);
//        tree.remove(3);
//        assertEquals("[8]", tree.root.toString());
//        assertEquals("[6]", tree.root.children[0].toString());
//        assertEquals("[10]", tree.root.children[1].toString());
//        assertEquals("[4, 5]", tree.root.children[0].children[0].toString());
//        assertEquals("[7]", tree.root.children[0].children[1].toString());
//        assertEquals("[9]", tree.root.children[1].children[0].toString());
//        assertEquals("[11]", tree.root.children[1].children[1].toString());
//        tree.remove(4);
//        tree.remove(5);
//        assertEquals("[8, 10]", tree.root.toString());
//        assertEquals("[6, 7]", tree.root.children[0].toString());
//        assertEquals("[9]", tree.root.children[1].toString());
//        assertEquals("[11]", tree.root.children[2].toString());
//        tree.remove(6);
//        tree.remove(7);
//        assertEquals("[10]", tree.root.toString());
//        assertEquals("[8, 9]", tree.root.children[0].toString());
//        assertEquals("[11]", tree.root.children[1].toString());
//        tree.remove(8);
//        tree.remove(9);
//        assertEquals("[10, 11]", tree.root.toString());
//        assertTrue(tree.root.leaf);
//        tree.remove(10);
//        tree.remove(11);
//        assertEquals("[]", tree.root.toString());
//    }


//    @Test
//    @DisplayName("case6: delete middle")
//    void testRemove11() {
//        /*
//
//                4|8                5|8               5|8                8
//               / | \              / | \             / | \              / \
//              2  6  10      =>   2  6  10      =>  2  _  10       => 2|5  10
//             /\  /\  /\         /\  /\  /\        /\  |  /\         / | \  /\
//            1 3 5 7 9 11       1 3 _ 7 9 11      1 3 6|7 9 11      1 3 6|7 9 11
//
//
//                 8                    9                 9                5
//                / \                /    \            /    \           /    \
//              2|5  10     =>     2|5    10     =>  2|5     _    =>   2      9
//             / | \  /\          / | \   /\        / | \    |        /\    /  \
//            1 3 6|7 9 11       1 3 6|7 _ 11      1 3 6|7 10|11     1 3  6|7 10|11
//
//
//                  5
//               /    \
//              2      9     =>
//             /\    /  \
//            1 3  6|7 10|11
//
//                  6                    7                7
//               /    \               /    \           /    \
//              2      9     =>      2      9     =>  2     10
//             /\    /  \           /\    /  \       /\    /  \
//            1 3   7  10|11       1 3   _  10|11   1 3   9   11
//
//                 7                   9                9
//               /  \                /  \              / \
//              2   10       =>     2   10     =>     2   _      =>   2|9
//             /\   / \            /\   / \          /\   |          / | \
//            1 3  9  11          1 3  _  11        1 3 10|11       1  3 10|11
//
//               2|9                 3|9                3|10
//              / | \         =>    / | \       =>     / | \
//             1  3 10|11          1  _ 10|11         1  9  11
//
//               3|10                9|10               10
//              / | \         =>    / | \       =>     / \
//             1  9  11            1  _  11          1|9  11
//
//               10                   11               9
//              / \           =>     / \        =>    / \
//            1|9  11              1|9  _            1  11
//
//               9                    11
//              / \         =>       / \        =>    1|11
//             1  11                1   _
//        */
//        BTree tree = new BTree();
//        tree.put(1);
//        tree.put(2);
//        tree.put(3);
//        tree.put(4);
//        tree.put(5);
//        tree.put(6);
//        tree.put(7);
//        tree.put(8);
//        tree.put(9);
//        tree.put(10);
//        tree.put(11);
//        tree.remove(4);
//        assertEquals("[8]", tree.root.toString());
//        assertEquals("[2, 5]", tree.root.children[0].toString());
//        assertEquals("[10]", tree.root.children[1].toString());
//        assertEquals("[1]", tree.root.children[0].children[0].toString());
//        assertEquals("[3]", tree.root.children[0].children[1].toString());
//        assertEquals("[6, 7]", tree.root.children[0].children[2].toString());
//        assertEquals("[9]", tree.root.children[1].children[0].toString());
//        assertEquals("[11]", tree.root.children[1].children[1].toString());
//        tree.remove(8);
//        assertEquals("[5]", tree.root.toString());
//        assertEquals("[2]", tree.root.children[0].toString());
//        assertEquals("[9]", tree.root.children[1].toString());
//        assertEquals("[1]", tree.root.children[0].children[0].toString());
//        assertEquals("[3]", tree.root.children[0].children[1].toString());
//        assertEquals("[6, 7]", tree.root.children[1].children[0].toString());
//        assertEquals("[10, 11]", tree.root.children[1].children[1].toString());
//        tree.remove(5);
//        assertEquals("[6]", tree.root.toString());
//        assertEquals("[2]", tree.root.children[0].toString());
//        assertEquals("[9]", tree.root.children[1].toString());
//        assertEquals("[1]", tree.root.children[0].children[0].toString());
//        assertEquals("[3]", tree.root.children[0].children[1].toString());
//        assertEquals("[7]", tree.root.children[1].children[0].toString());
//        assertEquals("[10, 11]", tree.root.children[1].children[1].toString());
//        tree.remove(6);
//        assertEquals("[7]", tree.root.toString());
//        assertEquals("[2]", tree.root.children[0].toString());
//        assertEquals("[10]", tree.root.children[1].toString());
//        assertEquals("[1]", tree.root.children[0].children[0].toString());
//        assertEquals("[3]", tree.root.children[0].children[1].toString());
//        assertEquals("[9]", tree.root.children[1].children[0].toString());
//        assertEquals("[11]", tree.root.children[1].children[1].toString());
//        tree.remove(7);
//        assertEquals("[2, 9]", tree.root.toString());
//        assertEquals("[1]", tree.root.children[0].toString());
//        assertEquals("[3]", tree.root.children[1].toString());
//        assertEquals("[10, 11]", tree.root.children[2].toString());
//        tree.remove(2);
//        assertEquals("[3, 10]", tree.root.toString());
//        assertEquals("[1]", tree.root.children[0].toString());
//        assertEquals("[9]", tree.root.children[1].toString());
//        assertEquals("[11]", tree.root.children[2].toString());
//        tree.remove(3);
//        assertEquals("[10]", tree.root.toString());
//        assertEquals("[1, 9]", tree.root.children[0].toString());
//        assertEquals("[11]", tree.root.children[1].toString());
//        tree.remove(10);
//        assertEquals("[9]", tree.root.toString());
//        assertEquals("[1]", tree.root.children[0].toString());
//        assertEquals("[11]", tree.root.children[1].toString());
//        tree.remove(9);
//        assertEquals("[1, 11]", tree.root.toString());
//    }


//    @Test
//    @DisplayName("case5: balance left rotate")
//    public void testRemove5_2() {
//    /*
//        4|5|6  ==>  5
//                   / \
//                  4   6
//       5
//      / \        ==>      5|7    ==>    5|7
//     4   6|7|8           / | \         / | \
//                        4  6  8      2|3|4  6 8
//
//     3|5|7                     5                             5
//    / | | \  ==>             /   \                        /     \
//   2  4 6  8                3     7              ==>     3      7|9
//                           / \   / \                    / \    / | \
//                          2   4 6   8|9|10             2   4  6  8  10
//
//     5                           7
//    / \                        /   \
//   _   7|9      ==>           5     9
//   |   /|\                   / \   / \
//  2|4 6 8 10                2|4 6 8  10
//    */
//        BTree tree = new BTree(2);
//        tree.put(4);
//        tree.put(5);
//        tree.put(6);
//        tree.put(7);
//        tree.put(8);
//        tree.put(3);
//        tree.put(2);
//        tree.put(9);
//        tree.put(10);
//
//        tree.remove(3);
//        assertEquals("[7]", tree.root.toString());
//        assertEquals("[5]", tree.root.children[0].toString());
//        assertEquals("[9]", tree.root.children[1].toString());
//        assertEquals("[2, 4]", tree.root.children[0].children[0].toString());
//        assertEquals("[6]", tree.root.children[0].children[1].toString());
//        assertEquals("[8]", tree.root.children[1].children[0].toString());
//        assertEquals("[10]", tree.root.children[1].children[1].toString());
//    }

}