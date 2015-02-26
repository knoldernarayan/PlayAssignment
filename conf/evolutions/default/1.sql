# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "knolxusertab" ("id" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"address" VARCHAR(254) NOT NULL,"company" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL,"password" VARCHAR(254) NOT NULL,"phone" VARCHAR(254) NOT NULL,"user_type" INTEGER NOT NULL,"created" DATE NOT NULL,"updated" DATE NOT NULL);

# --- !Downs

drop table "knolxusertab";

