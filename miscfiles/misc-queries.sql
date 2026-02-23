SELECT * FROM acl_sid;
--SELECT * FROM acl_entry;
--SELECT * FROM acl_object_identity;
--SELECT * FROM Meetup;
--SELECT * FROM users;
--SELECT * FROM acl_class;

SELECT sid.sid AS owner_sid, ident.object_id_identity, ident.owner_sid, acl_class.class FROM acl_class
INNER JOIN acl_object_identity ident ON acl_class.id = ident.object_id_class
INNER JOIN acl_sid sid ON sid.id = CAST (ident.owner_sid AS INTEGER);

SELECT * FROM acl_entry;
