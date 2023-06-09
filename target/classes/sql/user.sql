create table user
(
    id               bigint auto_increment
        primary key,
    username         varchar(100)                         null,
    password         varchar(65)                          null,
    sex              varchar(2)     default '男'          not null,
    age              int            default 0             not null,
    balance          decimal(12, 2) default 0.00          not null,
    is_admin         tinyint(1)     default 0             not null,
    is_deleted       int            default 0             not null,
    phone            bigint         default 1145141919810 not null,
    birthday         varchar(20)    default '2024-2-10'   not null,
    hobbies          varchar(50)    default ' '           not null,
    description      varchar(150)   default '啥都没'      not null,
    permission_level int            default 0             not null
);
