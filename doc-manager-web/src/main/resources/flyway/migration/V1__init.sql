-- ============================================================
-- 文档管理工具 - 数据库初始化脚本
-- 版本: V1 20260902
-- 说明: 创建核心表结构，支持文档片段化存储、分类、标签及训练数据
-- 数据库: SQLite (通过 Flyway 执行)
-- ============================================================

-- ------------------------------------------------------------
-- 表: category 分类表
-- 说明: 存储文档片段的分类信息，支持树形层级结构
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS category (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,                        -- 分类唯一ID，自增主键
    name            TEXT    NOT NULL,                                         -- 分类名称（如“技术/编程”）
    description     TEXT,                                                     -- 分类描述，可为空
    parent_id       INTEGER,                                                  -- 父分类ID，用于构建树形结构，NULL表示顶级分类
    rule_keywords   TEXT,                                                     -- 规则匹配关键词（JSON数组），用于快速分类
    create_time    TEXT    NOT NULL DEFAULT (datetime('now','localtime')),   -- 创建时间（本地时间字符串）
    update_time    TEXT    NOT NULL DEFAULT (datetime('now','localtime')),   -- 最后更新时间
    FOREIGN KEY (parent_id) REFERENCES category(id) ON DELETE SET NULL        -- 父分类删除时置空
);

-- ------------------------------------------------------------
-- 表: document 文档表
-- 说明: 存储导入的原始文档元数据，不包含具体内容（内容拆分为片段）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS document (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,                      -- 文档唯一ID，自增主键
    title           TEXT    NOT NULL,                                       -- 文档标题（文件名或首个标题）
    file_type       TEXT    NOT NULL CHECK (file_type IN ('md','txt')),     -- 源文件类型
    create_time    TEXT    NOT NULL DEFAULT (datetime('now','localtime')),  -- 导入时间
    update_time    TEXT    NOT NULL DEFAULT (datetime('now','localtime'))   -- 最后修改时间
);
    
-- ------------------------------------------------------------
-- 表: document_section 文档片段表
-- 说明: 存储从文档中拆分的每个独立片段，是核心业务实体
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS document_section (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT, -- 片段唯一ID，自增主键
    document_id         INTEGER NOT NULL,                  -- 所属文档ID
    title               TEXT,                              -- 片段标题（解析自动生成或用户编辑，可为空）
    original_content    TEXT NOT NULL,                     -- 片段正文初始内容（保留markdown格式）
    content             TEXT NOT NULL,                     -- 片段正文内容（纯文本）
    order_index         INTEGER NOT NULL,                  -- 片段在文档中的顺序，从1开始递增
    category_id         INTEGER,                           -- 片段独立分类ID（可空，表示未分类）
    create_time        TEXT NOT NULL DEFAULT (datetime('now','localtime')), -- 创建时间
    update_time        TEXT NOT NULL DEFAULT (datetime('now','localtime')), -- 最后更新时间
    FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE, -- 文档删除时级联删除片段
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL -- 分类删除时置空
);

-- ------------------------------------------------------------
-- 表: tag 标签表
-- 说明: 存储标签信息，标签为扁平结构
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tag (
    id   INTEGER PRIMARY KEY AUTOINCREMENT, -- 标签唯一ID，自增主键
    name TEXT NOT NULL UNIQUE               -- 标签名称，唯一约束防止重复
);

-- ------------------------------------------------------------
-- 表: section_tag 片段-标签关联表
-- 说明: 多对多关系，一个片段可有多个标签，一个标签可属于多个片段
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS section_tag (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,   --唯一id，自增主键
    section_id  INTEGER NOT NULL,                    -- 片段ID（联合主键之一）
    tag_id      INTEGER NOT NULL,                    -- 标签ID（联合主键之一）
    UNIQUE (section_id, tag_id),                     -- 保持唯一性约束
    FOREIGN KEY (section_id)    REFERENCES document_section(id) ON DELETE CASCADE, -- 片段删除时删除关联
    FOREIGN KEY (tag_id)        REFERENCES tag(id)              ON DELETE CASCADE  -- 标签删除时删除关联
);

-- ------------------------------------------------------------
-- 表: category_training 分类训练数据表
-- 说明: 存储用户确认的片段分类样本，用于训练智能分类模型
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS category_training (
    id              INTEGER PRIMARY KEY AUTOINCREMENT, -- 训练样本唯一ID
    content_text    TEXT NOT NULL,                     -- 片段内容（截取前500字符）
    category_id     INTEGER NOT NULL,                  -- 用户最终确认的分类ID
    create_time    TEXT NOT NULL DEFAULT (datetime('now','localtime')), -- 样本创建时间
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE  -- 分类删除时删除训练样本
);

-- ------------------------------------------------------------
-- 索引定义
-- 说明: 为常用查询字段建立索引，提高搜索和过滤性能
-- ------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_document_title       ON document(title);
CREATE INDEX IF NOT EXISTS idx_section_document     ON document_section(document_id);
CREATE INDEX IF NOT EXISTS idx_section_title        ON document_section(title);
CREATE INDEX IF NOT EXISTS idx_section_content      ON document_section(content);
CREATE INDEX IF NOT EXISTS idx_section_category     ON document_section(category_id);