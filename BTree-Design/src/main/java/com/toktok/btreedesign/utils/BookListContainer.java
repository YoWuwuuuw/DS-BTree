package com.toktok.btreedesign.utils;

import com.toktok.btreedesign.entity.po.Book;

import java.util.*;

public class BookListContainer {

    /**
     *  Book的List集合：用于存储所有Book对象的列表
     */
    private static List<Book> allBooks = new ArrayList<>();

    /**
     *  Book的HashSet集合，用于过滤已存在的Book，提高add等操作cpu
     */
    private static Set<Integer> addedIsbns = new HashSet<>();

    /**
     * 1. 将B树所有数据copy到List、HashSet中存储
     */
    public void convertToBookList(BTree btree) {
        // 将addedIsbns中不存在的book添加进list中
        doConvertToBookList(btree.root, allBooks);
        // 删除list中btree不存在的book
        removeBooksNotInBTree(allBooks, btree);
    }

    /**
     * 辅助方法：加载b树进入list
     * @param node 传入root
     * @param books list
     */
    private void doConvertToBookList(Node node, List<Book> books) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < node.keyNumber; i++) {
            doConvertToBookList(node.children[i], books);
            if (node.keyValues[i] != null) {
                // b树中查询出要新增的Book
                Book book = node.keyValues[i].getBook();
                Integer isbn = book.getBookName().hashCode();
                if (!addedIsbns.contains(isbn)) { // 检查是否已添加
                    books.add(book);
                    addedIsbns.add(isbn); // 添加到已添加集合中
                }
            }
        }

        doConvertToBookList(node.children[node.keyNumber], books); // 处理最后一个孩子
    }

    /**
     * 辅助方法：移除在B树中不存在的书籍
     */
    private void removeBooksNotInBTree(List<Book> books, BTree btree) {
        books.removeIf(book -> !btree.contains(book.getBookName().hashCode()));
    }

    /**
     * 2. 将list按热度排序，并返回
     * @return list
     */
    public List<Book> sortByHotAndReturn() {
        // 按hot降序排序：对于重复的调用，sort api内部交换值的次数很少，个别情况O(n) = n
        allBooks.sort(Comparator.comparingInt(Book::getHot).reversed());

        return allBooks;
    }

    /**
     * 3. 因为做了修改，删除记录，下次查询时懒加载更新
     * @param bookName 修改的文献名
     * @return 是否成功
     */
    public boolean removeByBookName(String bookName) {
        addedIsbns.remove(bookName.hashCode());
        allBooks.remove(new Book().setBookName(bookName));
        System.out.println("remove：：：");
        allBooks.stream().forEach(System.out::println);
        return true;
    }
}
