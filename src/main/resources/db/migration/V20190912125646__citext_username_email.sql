create extension if not exists citext with schema public;

alter table usr alter column username type citext;
alter table usr alter column email type citext;