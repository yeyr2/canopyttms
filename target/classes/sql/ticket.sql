create table ticket
(
    id             bigint auto_increment
        primary key,
    plan_id        int    default 0 not null,
    order_id       bigint default 0 not null,
    status         int    default 3 null,
    ticket_rows    int    default 1 not null,
    ticket_columns int    default 1 not null,
    last_time      bigint default 0 not null
);

