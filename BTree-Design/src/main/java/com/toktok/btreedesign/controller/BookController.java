package com.toktok.btreedesign.controller;

import com.toktok.btreedesign.common.CommonResult;
import com.toktok.btreedesign.entity.po.Book;
import com.toktok.btreedesign.entity.bo.RecordBo;
import com.toktok.btreedesign.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 新增文献
     * @param book 实体类
     * @return 是否成功
     */
    @PostMapping("/addBook")
    public CommonResult<Boolean> addBook(@RequestBody @NotEmpty Book book){
        return CommonResult.autoResult(bookService.addBook(book));
    }

    /**
     * 删除文献
     * @param bookName 要删除的文献名
     * @return 是否成功
     */
    @PutMapping("/deleteBookByBookName")
    public CommonResult<Boolean> deleteBookByBookName(@NotEmpty(message = "文献不能为空") String bookName){
        return CommonResult.autoResult(bookService.deleteBookByBookName(bookName));
    }

    /**
     * 借阅文献
     * @param recordBo 借阅记录：传入book.bookName,userName,borrowId
     * @return 是否成功
     */
    @PostMapping("/borrowBookByBookName")
    public CommonResult<Boolean> borrowBookByBookName(@RequestBody @NotEmpty(message = "文献不能为空") RecordBo recordBo){
        return CommonResult.autoResult(bookService.borrowBookByBookName(recordBo));
    }

    /**
     * 归还文献
     * @param bookName 借阅的文献名
     * @param userName 借阅的用户名
     * @return 是否成功
     */
    @PutMapping("/returnBook")
    public CommonResult<Boolean> returnBook(@NotNull String bookName, String userName){
        return CommonResult.autoResult(bookService.returnBook(bookName.hashCode(), userName));
    }

    /**
     * 获取所有文献
     * @return 所有文献集合
     */
    @GetMapping("/getAllBook")
    public CommonResult<List<Book>> getAllBook(){
        return CommonResult.autoResult(true, bookService.getAllBook());
    }

}
