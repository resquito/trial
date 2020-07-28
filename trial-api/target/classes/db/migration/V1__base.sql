create table if not exists user (
  user_id bigint auto_increment primary key,
  username varchar(100) not null,
  password varchar(200) not null,
  firstname varchar(100),
  lastname varchar(100)
);

insert into user (username, password, firstname, lastname)
values('admin',
'$2a$10$ySHpN8uafpgtCUxrnORLxeZgP9aaZnhQUVDPQbZO1CMJu6eWmpnMa',
'admin',
'admin');

alter table user add constraint unique(username);

create table if not exists role(
    role_id bigint auto_increment primary key,
    role varchar(100)
);

insert into role(role) values ('ADMIN');
insert into role(role) values ('USER');

create table if not exists user_roles (
    user_id bigint,
    role_id bigint,
    primary key(user_id, role_id)
);

insert into user_roles values (1,1);
insert into user_roles values (1,2)
