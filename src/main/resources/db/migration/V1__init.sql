create sequence hibernate_sequence start with 1 increment by 1;

create table usr(
    id              bigint          primary key,
    email           varchar(255)    not null unique,
    username        varchar(16)     not null unique,
    password_hash   varchar(255)    not null,
    bio             varchar(512),
    image           varchar(2048)
);

create table article(
    id              bigint          primary key,
    slug            varchar(255)    not null unique,
    title           varchar(255)    not null,
    description     varchar(255)    not null,
    body            text            not null,
    created_at      timestamp       not null,
    updated_at      timestamp,
    author_id       bigint          not null references usr(id)
);

create table comment(
    id              bigint          primary key,
    body            varchar(1024)   not null,
    created_at      timestamp       not null,
    updated_at      timestamp,
    author_id       bigint          not null references usr(id),
    article_id      bigint          not null references article(id)
);

create table tag(
    id              bigint          primary key,
    name            varchar(255)    not null unique
);

create table usr_follow(
    follower_id     bigint          not null references usr(id),
    followed_id     bigint          not null references usr(id),
    unique (follower_id, followed_id)
);

create table usr_favorite(
    usr_id          bigint          not null references usr(id),
    article_id      bigint          not null references article(id),
    unique (usr_id, article_id)
);

create table article_tag(
    article_id      bigint          not null references article(id),
    tag_id          bigint          not null references tag(id),
    unique (article_id, tag_id)
);