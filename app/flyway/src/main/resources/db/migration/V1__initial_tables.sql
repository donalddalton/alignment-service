drop table if exists users;

create table users (
    username varchar(255) primary key,
    password varchar(255) not null
);
