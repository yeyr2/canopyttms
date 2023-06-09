create table plan
(
    id         bigint auto_increment
        primary key,
    time       bigint         default 0         not null,
    video_id   bigint         default 0         null,
    video      varchar(200)   default ''        not null,
    language   varchar(20)    default 'Chinese' not null,
    studio     varchar(20)                      not null,
    studio_id  bigint                           not null,
    price      decimal(10, 3) default 0.000     not null,
    is_deleted int            default 0         not null
);
