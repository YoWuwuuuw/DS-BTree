package com.toktok.cn.BTree;

/**
 * B树
 */
public class BTree {
    Node root;

    int t; // 树中节点最小度数
    final int MIN_KEY_NUMBER; // 最小key数目
    final int MAX_KEY_NUMBER; // 最大key数目

    /**
     * 无参构造，默认最小度数为2
     */
    public BTree() {
        this(2);
    }

    public BTree(int t) {
        this.t = t;
        root = new Node(t);
        MAX_KEY_NUMBER = 2 * t - 1;
        MIN_KEY_NUMBER = t - 1;
    }

    // 1. 是否存在(衍生方法)
    public boolean contains(int key) {
        return root.get(key) != null;
    }

    // 2. 新增关键字
    public void put(int key) {
        doPut(root, key, null, 0);
    }

    /**
     * 新增关键字    doput()实现
     *
     * @param node   当前的节点
     * @param key    新增的关键字
     * @param parent 当前节点的父节点：用于递归新增非叶子节点时记录本节点
     * @param index  需要新增到孩子节点的关键字数组的下标，用于递归新增非叶子节点
     */
    private void doPut(Node node, int key, Node parent, int index) {
        // 首先是找到要插入的位置，用i记录index
        int i = 0;
        while (i < node.keyNumber) {
            // 找到重复key，则更新(没挂载数据，所以仅演示)
            if (node.keys[i] == key) {
                return;
            }
            // 找到了插入位置，即为此时的i(i代表待插入位置的关键字数组下标)
            if (node.keys[i] > key) {
                break;
            }
            i++;
        }

        // 如果是叶子节点：直接插入
        if (node.leaf) {
            node.insertKey(key, i);
        } else {
            // 非叶子节点：递归找当前节点的下标为i处关键字的左孩子(也可能下标i是孩子数组的最右孩子)，进行新增关键字
            doPut(node.children[i], key, node, i);
        }

        // 校验：当插入后，当前节点的关键字数超过最大值 -> 需要分裂
        if (node.keyNumber == MAX_KEY_NUMBER) {
            split(node, parent, index);
        }
    }

    /**
     * 分裂方法：最大的转移成新节点，中间的给父节点(分裂时必然是奇数)，重排当前节点和新节点
     * 因需要测试，所以不加private修饰了，不然不同包下不可见
     *
     * @param left   要分裂的节点(因为他要移动到左侧)
     * @param parent 分裂节点的父节点
     * @param index  分裂节点是第几个孩子
     */
    void split(Node left, Node parent, int index) {
        // ①若分裂的是根节点，额外步骤：创建新根(涉及root)、当前根的父节点设为root
        if (parent == null) {
            Node newRoot = new Node(t);
            newRoot.leaf = false;
            newRoot.insertChild(left, 0);
            this.root = newRoot;
            parent = newRoot;
        }

        // 1.创建right节点,把left中t之后的key和child移动过去
        Node right = new Node(t);
        right.leaf = left.leaf;
        System.arraycopy(left.keys, t, right.keys, 0, t - 1);

        // 分裂节点是非叶子的情况，多了一件事：把t之后的key和child也移动到新节点中
        if (!left.leaf) {
            System.arraycopy(left.children, t, right.children, 0, t);
            for (int i = t; i <= left.keyNumber; i++) {
                left.children[i] = null;
            }
        }
        // 修改分裂后的各节点的keynumber
        right.keyNumber = t - 1;
        left.keyNumber = t - 1;

        // 2.中间的key(t-1)插入到父节点的t-1处 <- 规律
        int mid = left.keys[t - 1];
        parent.insertKey(mid, index);

        // 3.right节点(新节点)作为父节点的孩子，插入到父节点的孩子数组的index+1处
        // (因为上升了一个键到父节点处，所以index多了1，实际上index+1就是原节点的位置)
        parent.insertChild(right, index + 1);
    }

    // 3. 删除
    public void remove(int key) {
        doRemove(null, root, 0, key);
    }

    /**
     * 删除关键字
     *
     * @param parent 用于递归时记录原节点
     * @param node   本节点
     * @param index  用于递归时记录删除位置
     * @param key    待删除的关键字
     */
    private void doRemove(Node parent, Node node, int index, int key) {
        int i = 0;
        while (i < node.keyNumber) {
            if (node.keys[i] >= key) {
                break;
            }
            i++;
        }
        // i 找到：代表待删除 key 的索引
        // i 没找到： 代表到第i个孩子继续查找

        // 当前是叶子节点
        if (node.leaf) {
            if (!found(node, key, i)) { // case1：没有找到
                return;
            } else { // case2：找到
                node.removeKey(i);
            }
        } else {
            // 当前是非叶子节点(可以理解为大顶堆的删除，先交换堆顶和堆尾，再删除...)
            if (!found(node, key, i)) { // case3：没有找到，递归找
                doRemove(node, node.children[i], i, key);
            } else { // case4：找到
                // 1. 找到后继 key
                Node s = node.children[i + 1];
                while (!s.leaf) {
                    s = s.children[0];
                }
                int skey = s.keys[0];
                // 2. 替换待删除 key
                node.keys[i] = skey;
                // 3. 删除后继 key
                doRemove(node, node.children[i + 1], i + 1, skey);
            }
        }

        // 调整平衡：删除后key数目 < MIN_NUMBER
        if (node.keyNumber < MIN_KEY_NUMBER) {
            // case 6：考虑根节点
            balance(parent, node, index);
        }
    }

    private void balance(Node parent, Node x, int i) {
        // case 6 根节点
        if (x == root) {
            if (root.keyNumber == 0 && root.children[0] != null) {
                root = root.children[0];
            }
            return;
        }
        Node left = parent.childLeftSibling(i);
        Node right = parent.childRightSibling(i);

        // case 5-1 左边富裕(超了)，右旋
        if (left != null && left.keyNumber > MIN_KEY_NUMBER) {
            // a) 父节点中前驱key旋转下来
            x.insertKey(parent.keys[i - 1], 0);
            if (!left.leaf) {
                // b) left中最大的孩子换爹
                x.insertChild(left.removeRightmostChild(), 0);
            }
            // c) left中最大的key旋转上去
            parent.keys[i - 1] = left.removeRightmostKey();
            return;
        }

        // case 5-2 右边富裕，左旋
        if (right != null && right.keyNumber > MIN_KEY_NUMBER) {
            // a) 父节点中后继key旋转下来
            x.insertKey(parent.keys[i], x.keyNumber);
            // b) right中最小的孩子换爹
            if (!right.leaf) {
                x.insertChild(right.removeLeftmostChild(), x.keyNumber); // @TODO 学员指出多加了1
            }
            // c) right中最小的key旋转上去
            parent.keys[i] = right.removeLeftmostKey();
            return;
        }

        // case 5-3 两边都不够借，向左合并
        if (left != null) {
            // 向左兄弟合并
            parent.removeChild(i);
            left.insertKey(parent.removeKey(i - 1), left.keyNumber);
            x.moveToTarget(left);
        } else {
            // 向自己合并
            parent.removeChild(i + 1);
            x.insertKey(parent.removeKey(i), x.keyNumber);
            right.moveToTarget(x);
        }
    }

    private boolean found(Node node, int key, int i) {
        return i < node.keyNumber && node.keys[i] == key;
    }

    /**
     * 遍历打印B树：前序遍历
     */
    public void travel() {
        doTravel(root);
    }

    private void doTravel(Node node) {
        if (node == null) {
            return;
        }
        int i = 0;
        for (; i < node.keyNumber; i++) {
            doTravel(node.children[i]);
            System.out.println(node.keys[i]);
        }
        doTravel(node.children[i]);
    }
}
