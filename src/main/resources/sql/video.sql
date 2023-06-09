create table video
(
    id           bigint auto_increment
        primary key,
    name         varchar(50)    default 'user1'  not null,
    imageUrl     varchar(250)   default 'ss'     not null,
    type         varchar(100)   default '1'      not null,
    description  varchar(200)   default ' '      not null,
    release_time bigint         default 0        not null,
    score        decimal(10, 3) default 0.000    null,
    duration     varchar(20)    default '10分钟' not null,
    source       varchar(20)    default 'China'  not null,
    hot          tinyint(1)     default 0        null,
    coming_soon  tinyint(1)     default 0        not null,
    actors       varchar(300)   default ''       not null,
    is_deleted   int            default 0        not null,
    constraint id
        unique (id)
);