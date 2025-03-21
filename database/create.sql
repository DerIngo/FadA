/*
 * SQL erzeugen lassen:
spring:
  jpa:
    properties:
      javax:
        persistence:
          schema-generation:
            scripts:
              action: create
              create-target: database/create.sql
              create-source: metadata
 *
 * varchar(255) auf text geändert 
 */
create table items (length bigint, pub_date timestamp(6), author text, content text, description text, filename text, guid text not null, image text, keywords text, subtitle text, summary text, title text, type text, url text, primary key (guid));
