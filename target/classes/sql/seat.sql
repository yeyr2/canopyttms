create table seat
(
    id          bigint auto_increment
        primary key,
    studio_id   bigint default 0 not null,
    Seat_row    int    default 0 not null,
    Seat_column int    default 0 not null,
    status      int    default 3 not null
);

