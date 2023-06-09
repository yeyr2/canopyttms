create table studio
(
    id            bigint auto_increment
        primary key,
    name          varchar(30)  default 'dcy' not null,
    StudioRows    int                        null,
    StudioColumns int                        null,
    description   varchar(100) default 'aaa' null,
    is_deleted    int          default 0     not null
);

