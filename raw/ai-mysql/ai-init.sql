-- From create_assignments.sql
create table assignments
(
    id varchar(240) not null,
    poolKey varchar(200) not null,
    poolType varchar(50) not null,
    subjectId varchar(200) not null,
    subjectType varchar(50) not null,
    subscriberContextId varchar(200) not null,
    features json null,
    subscriptionIdentifier varchar(200) null,
    assignmentType varchar(20) null,
    enabled tinyint(1) default 1 null,
    createdAt timestamp default CURRENT_TIMESTAMP not null,
    updatedAt timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    createdBy varchar(200) null,
    updatedBy varchar(200) null,
    constraint assignments_id_uindex
        unique (id),
    constraint assignments_subject_team_pool_uindex
        unique (subjectId, subscriberContextId, poolType, poolKey)
)
    charset=utf8;

alter table assignments
    add primary key (id);

-- From AUM-16526.sql
-- alter table assignmentassignintent.assignments drop column poolValue;
alter table assignmentassignintent.assignments modify poolKey varchar(200) not null;
alter table assignmentassignintent.assignments modify subjectId varchar(200) not null;
alter table assignmentassignintent.assignments modify subscriptionIdentifier varchar(200) null;
alter table assignmentassignintent.assignments modify createdBy varchar(200) null;
alter table assignmentassignintent.assignments modify updatedBy varchar(200) null;
-- alter table assignmentassignintent.assignments drop index
--     assignments_subjectId_subscriberContextId_poolKey_uindex
-- ;
-- alter table assignmentassignintent.assignments add constraint
--     assignments_subject_team_pool_uindex
--     unique (subjectId, subscriberContextId, poolType, poolKey)
-- ;

-- From AUM-17437.sql
--
-- THIS SCRIPT HAS BEEN COMMENTED OUT AS THE FOLLOWING ERROR OCCURS WHEN RUN
-- ai-db-1        | ERROR 1064 (42000) at line 49: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near '' at line 9
--
-- Index
-- create index assignments_poolKey_assignmentType_index
--     on assignments (poolKey, assignmentType);
--
-- -- create stored procedure
-- CREATE PROCEDURE revoke_feature_from_groups(
--     IN pool_key VARCHAR(200),
--     IN feature_id VARCHAR(50)
-- )
-- BEGIN
-- SELECT id, poolKey, poolType, subjectId, subjectType, subscriberContextId FROM assignments
-- WHERE poolKey = pool_key
--   AND subjectType = 'group'
--   AND JSON_SEARCH(features, 'one', feature_id) IS NULL;
--
-- UPDATE assignments
-- SET features =
--         JSON_ARRAY_APPEND(
--                 features,
--                 '$',
--                 CAST(
--                         CONCAT('{ "id": "', feature_id, '", "grant_type": "deny" }') as JSON
--                 )
--         )
-- WHERE poolKey = pool_key
--   AND subjectType = 'group'
--   AND JSON_SEARCH(features, 'one', feature_id) IS NULL;
-- END;

-- From AUM-17733.sql
-- THIS SCRIPT HAS BEEN COMMENTED OUT AS THE FOLLOWING ERROR OCCURS WHEN RUN
-- ai-db-1        | ERROR 1064 (42000) at line 81: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'type character varying(64)' at line 1
--
-- AUM-17733
-- Modifications to table offerings_assignments.assignments
--    Increase column length of tenant_id from 10 to 64
-- Modifications to table offerings_assignments.migration_audits
--    Increase column length of tenant_id from 10 to 64
-- alter table offerings_assignments.assignments alter column tenant_id type character varying(64);
-- alter table offerings_assignments.migration_audits alter column tenant_id type character varying(64);

-- FROM AUM-18425.sql
USE assignmentassignintent;

-- composite index sample
ALTER TABLE assignmentassignintent.assignments
    ADD INDEX assignments_subs_pool_assignments_index (subscriberContextId, poolKey, subscriptionIdentifier)
    USING HASH;


-- FROM UMA-1698.sql
create index assignments_poolKey_assignmentType_index
    on assignments (poolKey, assignmentType);

create index assignments_subjectType_subjectId_index
    on assignments (subjectType, subjectId);

create index assignments_subscriberContextId_index
    on assignments (subscriberContextId);


-- FROM UMA-7360.sql
alter table assignmentassignintent.assignments add column utsMigrationId varchar(255) null;
