package com.toktok.cn.BTree;

import java.util.Arrays;

/**
 * B树节点类
 */
public class Node {
    // 关键字/键数组
    int[] keys;

    // 孩子数组
    Node[] children;

    // 有效关键字数目(存储键的数组中有效的键数，因为数组长度是最大键数)
    int keyNumber;

    // 是否是叶子节点
    boolean leaf = true;

    // 最小度数 (最小孩子数，即至少几个)
    int t;

    /**
     * 构造方法
     *
     * @param t 最小度数
     */
    public Node(int t) { // 至少t>=2，不能只有一个孩子
        this.t = t;

        // 由最小度数确定最大孩子数、最大关键字数
        this.children = new Node[2 * t];
        this.keys = new int[2 * t - 1];
    }

    /**
     * 弄一个构造方法方便测试
     *
     * @param keys 关键词数组
     */
    public Node(int[] keys) {
        this.keys = keys;
    }

    /**
     * 将当前节点的关键字转换为String，便于打印
     *
     * @return String
     */
    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOfRange(keys, 0, keyNumber));
    }

    /**
     * 多路查找关键字
     *
     * @param key 带查找关键字
     * @return 关键字所在节点
     */
    Node get(int key) {
        int i = 0;

        // 从左到右比较
        while (i < keyNumber) {
            // 如果关键字在本节点，返回自己
            if (keys[i] == key) {
                return this;
            }
            // 如果关键字在当前关键字的左孩子，退出循环
            if (keys[i] > key) {
                break;
            }
            i++;
        }

        // 本节点是叶子节点，则无左孩子，返回null
        if (leaf) {
            return null;
        }

        // 非叶子情况，递归查找左孩子(可能返回左孩子，或找不到返回null)
        return children[i].get(key);
    }

    /**
     * 1. 向 keys 指定索引处插入 key
     *
     * @param key   待插入关键字
     * @param index 待插入的位置
     */
    void insertKey(int key, int index) {
        System.arraycopy(keys, index, keys, index + 1, keyNumber - index);
        keys[index] = key;
        keyNumber++;
    }

    /**
     * 2. 向 children 指定索引处插入 child
     *
     * @param child 待插入孩子节点
     * @param index 待插入的位置
     */
    void insertChild(Node child, int index) {
        System.arraycopy(children, index, children, index + 1, keyNumber - index);
        children[index] = child;
    }

    /**
     * 3. 移除指定 index 处的 key
     */
    int removeKey(int index) {
        int t = keys[index];
        System.arraycopy(keys, index + 1, keys, index, --keyNumber - index);
        return t;
    }

    /**
     * 4. 移除最左边的 key
     */
    int removeLeftmostKey() {
        return removeKey(0);
    }

    /**
     * 5. 移除最右边的 key
     */
    int removeRightmostKey() {
        return removeKey(keyNumber - 1);
    }

    /**
     * 6. 移除指定 index 处的 child
     */
    Node removeChild(int index) {
        Node t = children[index];
        System.arraycopy(children, index + 1, children, index, keyNumber - index);
        children[keyNumber] = null; // help GC
        return t;
    }

    /**
     * 7. 移除最左边的 child
     */
    Node removeLeftmostChild() {
        return removeChild(0);
    }

    /**
     * 8. 移除最右边的 child
     */
    Node removeRightmostChild() {
        return removeChild(keyNumber);
    }

    /**
     * 9. index 孩子处左边的兄弟
     */
    Node childLeftSibling(int index) {
        return index > 0 ? children[index - 1] : null;
    }

    /**
     * 10. index 孩子处右边的兄弟
     */
    Node childRightSibling(int index) {
        return index == keyNumber ? null : children[index + 1];
    }

    /**
     * 11. 复制当前节点的所有 key 和 child 到 target
     */
    void moveToTarget(Node target) {
        int start = target.keyNumber;
        if (!leaf) {
            for (int i = 0; i <= keyNumber; i++) {
                target.children[start + i] = children[i];
            }
        }
        for (int i = 0; i < keyNumber; i++) {
            target.keys[target.keyNumber++] = keys[i];
        }
    }
}