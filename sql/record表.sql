CREATE TABLE record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_key INT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    borrow_id VARCHAR(255), 
    borrow_at DATETIME,
		deleted tinyint NOT NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
    return_at DATETIME
);