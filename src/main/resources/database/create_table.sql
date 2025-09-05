create database if not exists module_insert;

create table module_insert.cluster(
                                      id bigint primary key,
                                      name varchar(255) not null,
                                      status varchar(50),
                                      base_folder text,
                                      description text,
                                      module_number int
);

create table module_insert.module(
                                     id bigint primary key,
                                     name varchar(255) not null,
                                     description text
);

create table module_insert.cluster_module (
                                              id bigint primary key,
                                              cpu_list varchar(100),
                                              config_path varchar(255),
                                              run_path varchar(255),
                                              module_id bigint,
                                              cluster_id bigint,
                                              log_path text
);

create table module_insert.config (
                                      id bigint primary key,
                                      name varchar(255)
);

create table module_insert.config_cluster_module(
                                                    id bigint primary key,
                                                    config_id bigint,
                                                    cluster_module_id bigint,
                                                    module_name varchar(255),
                                                    cluster_name varchar(255),
                                                    PID int,
                                                    status varchar(50),
                                                    start_time datetime,
                                                    stop_time datetime,
                                                    process_log varchar(255)
);