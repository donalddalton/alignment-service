drop table if exists users;

create table users (
    id       integer primary key generated always as identity,
    username varchar(255) not null unique,
    password varchar(255) not null
);
