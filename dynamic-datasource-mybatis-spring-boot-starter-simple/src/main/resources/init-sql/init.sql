

create table if not exists `dy_user` (
    `id` bigint not null comment '主键ID',
    `name` varchar(255) default null comment '姓名',
    `age` int(11) default 0 comment '年龄',
    primary key(id)
);