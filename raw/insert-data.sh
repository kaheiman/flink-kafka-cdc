#!/bin/bash

# echo "Inserting test data into userlist database..."
# docker exec userlist-db mysql -uuserlist -puserlist userlist -e "
# INSERT INTO user (oxygen_id, first_name, last_name, email, created) 
# VALUES ('test-user-10', 'John', 'Doe', 'john@example.com', NOW());
# "

echo "Inserting test data into assignments database..."
docker exec ai-db mysql -uassignments -passignments assignmentassignintent



# echo "Inserting test data into usetlist.ace ..."
# docker exec userlist-db mysql -uuserlist -puserlist userlist -e "
# INSERT INTO userlist.ace (id, created, creator, updated, updater) 
# VALUES (1, NOW(), 'system', NOW(), 'system');
# "

# echo "Inserting test data into usetlist.tenant ..."
# docker exec userlist-db mysql -uuserlist -puserlist userlist -e "
# INSERT INTO userlist.tenant 
# (id, oxygen_id, created, creator, updated, updater, ace_id, alias, deleted, guest_default_assign) 
# VALUES (123, 'tenant-123', NOW(), 'system', NOW(), 'system', '1', '', 0, 0);
# "

# echo "Inserting test data into usetlist.group ..."
# docker exec userlist-db mysql -uuserlist -puserlist userlist -e "
# INSERT INTO \`group\` (oxygen_id, name, description, tenant_id, created, creator, updated, updater, group_type_id)
# VALUES ('test-group-19', 'test-group-19', 'test', 123, NOW(), 'test', NOW(), 'test', 10);"

echo "Data inserted"