create extension if not exists "uuid-ossp";

drop table if exists jobs;

create table jobs(
    id  uuid primary key,
    username varchar(255) not null,
    query text not null,
    created_at timestamp not null,
    started_at timestamp,
    completed_at timestamp,
    target_match_id varchar(255),
    result text,
    constraint fk_username
     foreign key (username)
         references users(username)
         on delete cascade,
     constraint fk_target_id
     foreign key (target_match_id)
         references targets(id)
         on delete cascade
);
