package com.toktok.btreedesign.controller;

import com.toktok.btreedesign.common.CommonResult;
import com.toktok.btreedesign.entity.po.Book;
import com.toktok.btreedesign.entity.bo.RecordBo;
import com.toktok.btreedesign.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public CommonResult<Boolean> addBook(@RequestBody Book book){
        return CommonResult.autoResult(bookService.addBook(book));
    }

    @PutMapping("/deleteBookByBookName")
    public CommonResult<Boolean> deleteBookByBookName(String bookName){
        return CommonResult.autoResult(bookService.deleteBookByBookName(bookName));
    }

    @PostMapping("/borrowBookByBookName")
    public CommonResult<Boolean> borrowBookByBookName(@RequestBody RecordBo recordBo){
        return CommonResult.autoResult(bookService.borrowBookByBookName(recordBo));
    }

    @PutMapping("/returnBook")
    public CommonResult<Boolean> returnBook(String bookName, String userName){
        return CommonResult.autoResult(bookService.returnBook(bookName.hashCode(), userName));
    }

    @GetMapping("/getAllBook")
    public CommonResult<List<Book>> getAllBook(){
        return CommonResult.autoResult(true, bookService.getAllBook());
    }

}
