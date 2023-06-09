create table t_order
(
    id           bigint auto_increment
        primary key,
    uid          bigint                       not null,
    time         varchar(30)                  not null,
    price        decimal(10, 3) default 0.000 null,
    order_status int            default 0     not null,
    video_id     bigint                       not null,
    video        varchar(100)   default ''    not null,
    studio_id    int                          not null,
    studio       varchar(100)   default ''    not null,
    plan_id      bigint         default 0     not null
);