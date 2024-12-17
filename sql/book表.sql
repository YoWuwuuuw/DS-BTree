CREATE TABLE `book` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `book_name` VARCHAR(255) NOT NULL COMMENT '文献名',
    `author` VARCHAR(255) NOT NULL COMMENT '著者',
    `stock` INT NOT NULL COMMENT '现存量',
    `total` INT NOT NULL COMMENT '总库存量',
    `hot` INT NOT NULL COMMENT '热度值',
		deleted tinyint NOT NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
    `put_in_at` DATETIME NOT NULL COMMENT '入库时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;