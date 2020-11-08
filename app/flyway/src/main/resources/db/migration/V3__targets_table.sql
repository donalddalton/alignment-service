drop table if exists targets;

create table targets (
    id   varchar(255) primary key,
    sequence TEXT not null
);