use learning;
create table answer
(
    id             bigint auto_increment comment '答案ID'
        primary key,
    content        varchar(1000) not null comment '内容',
    user_name      varchar(20)   not null comment '用户名',
    question_id    bigint        not null comment '问题ID',
    points_awarded TINYINT       NOT NULL DEFAULT 0 COMMENT '是否已奖励积分 (1:是, 0:否)',
    create_time    datetime      not null comment '创建时间',
    update_time    datetime      not null comment '修改时间'
)
    comment '答案';

create table category
(
    id          int auto_increment comment '分类ID'
        primary key,
    name        varchar(20) not null comment '名称',
    parent_id   int         not null comment '父级ID',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '修改时间'
)
    comment '分类';

create table category_course
(
    id          bigint auto_increment comment '分类-课程关系ID'
        primary key,
    category_id int    not null comment '分类ID',
    course_id   bigint not null comment '课程ID'
);

create table chapter
(
    id           bigint auto_increment comment '章节ID'
        primary key,
    title        varchar(100) not null comment '标题',
    type         varchar(10)  not null comment '内容类型',
    video_url    varchar(255) null comment '视频链接',
    video_time   varchar(20)  null comment '视频时长',
    text_content text         null comment '文字内容',
    course_id    bigint       not null comment '课程ID',
    create_time  datetime     not null comment '创建时间',
    update_time  datetime     not null comment '修改时间'
)
    comment '章节';

create table course
(
    id              bigint auto_increment comment '课程ID'
        primary key,
    name            varchar(100)   not null comment '名称',
    price           decimal(10, 2) not null comment '价格',
    points_price    int            not null comment '积分价格',
    description     varchar(200)   not null comment '简介',
    user_name       varchar(20)    not null comment '用户名',
    average_score   tinyint        not null comment '平均评分',
    cover_picture   varchar(100)   not null comment '封面图片地址',
    approved        tinyint        not null comment '审核通过',
    category_id     int            not null comment '分类ID',
    chapter_count   int            not null default 0 comment '章节数',
    points_reward   INT            NOT NULL DEFAULT 100 COMMENT '完成课程奖励积分数',
    certificate_url VARCHAR(255)   NULL COMMENT '教师上传的证书图片URL',
    is_premium      tinyint        not null default 0 comment '是否为精品课程(1:是, 0:否, 完成后可颁发证书',
    create_time     datetime       not null comment '创建时间',
    update_time     datetime       not null comment '修改时间'
)
    comment '课程';

create table evaluation
(
    id          bigint auto_increment comment '评价ID'
        primary key,
    score       tinyint      not null comment '评分',
    comment     varchar(255) not null comment '评论',
    user_name   varchar(20)  not null comment '用户名',
    course_id   bigint       not null comment '课程ID',
    create_time datetime     not null comment '创建时间',
    update_time datetime     not null comment '修改时间'
)
    comment '评价';

create table note
(
    id          bigint auto_increment comment '笔记ID'
        primary key,
    content     text        not null comment '内容',
    user_name   varchar(20) not null comment '用户名',
    course_id   bigint      not null comment '课程ID',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '修改时间'
)
    comment '笔记';

create table `order`
(
    id                 bigint auto_increment
        primary key,
    trade_no           varchar(40)    not null comment '交易编号',
    price              decimal(10, 2) not null comment '价格',
    points_price       int            not null comment '积分价格',
    user_name          varchar(255)   not null comment '买家',
    product_id         bigint         not null comment '商品ID',
    product_name       varchar(255)   not null comment '商品名称',
    close_milliseconds bigint         not null comment '关闭时间',
    create_time        datetime       not null comment '创建时间',
    update_time        datetime       not null comment '更新时间',
    status             tinyint        not null comment '状态：0->未支付 1->已支付 2->已关闭'
)
    comment '订单';

create table question
(
    id           bigint auto_increment comment '问题ID'
        primary key,
    title        varchar(255)  not null comment '标题',
    content      varchar(1000) not null comment '内容',
    user_name    varchar(20)   not null comment '用户名',
    course_id    bigint        not null comment '课程ID',
    answer_count int           not null comment '答案数量',
    create_time  datetime      not null comment '创建时间',
    update_time  datetime      not null comment '更新时间'
)
    comment '问题';

create table role
(
    id          int auto_increment comment '主键ID'
        primary key,
    name        varchar(20) not null comment '角色名',
    create_time datetime    not null comment '创建时间',
    update_time datetime    null comment '更新时间'
)
    comment '角色';

INSERT INTO `role`
VALUES (1, '学生', now(), now());
INSERT INTO `role`
VALUES (2, '教师', now(), now());
INSERT INTO `role`
VALUES (3, '管理员', now(), now());

create table user
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    username        varchar(20)  not null comment '用户名',
    password        varchar(100) not null comment '密码',
    full_name       varchar(20)  null comment '姓名',
    gender          char(2)      null comment '性别',
    role_id         int          not null comment '角色ID',
    email_address   varchar(100) not null comment '邮箱地址',
    phone_number    varchar(100) null comment '手机号码',
    profile_picture varchar(100) null comment '头像链接',
    points          INT          NOT NULL DEFAULT 0 COMMENT '用户积分',
    create_time     datetime     not null comment '创建时间',
    update_time     datetime     not null comment '修改时间'
)
    comment '用户';

INSERT INTO `user`
VALUES (1, 'admin', '$2a$10$YmZg6KFPsMVUyWzsxiNgOun7SYoKW1FiAqFvaqnehx2fj0xh5geSa', '张三', '男', 3, 'admin@qq.com',
        '15648768888', 'http://localhost:8080/dfs/profile-pictures/default.jpg', now(), now());


create table user_course
(
    id              bigint auto_increment comment '用户-课程关系ID'
        primary key,
    user_name       varchar(20) not null comment '用户名',
    course_id       bigint      not null comment '课程ID',
    is_completed    TINYINT     NOT NULL DEFAULT 0 COMMENT '用户是否已完成该课程 (1:是, 0:否)',
    completion_time DATETIME    NULL COMMENT '课程完成时间',
    current_chapter BIGINT NULL COMMENT '用户当前观看的章节ID',
    create_time     datetime    not null comment '创建时间',
    update_time     datetime    not null comment '修改时间'
);

create table roadmap
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    name        varchar(20) not null comment '名称',
    image_url   varchar(100) not null comment '图片链接',
    category_id int         not null comment '所属分类ID',
    tags        varchar(100) null comment '标签',
    description varchar(255) null comment '描述',-- 用逗号分隔的标签字符串，例如 "前端,React,Node.js"
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '修改时间'
)
