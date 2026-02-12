-- This is to clean up all ACL related tables, used primarily for testing
ALTER TABLE acl_class DISABLE TRIGGER ALL;
ALTER TABLE acl_entry DISABLE TRIGGER ALL;
ALTER TABLE acl_sid DISABLE TRIGGER ALL;
ALTER TABLE acl_object_identity DISABLE TRIGGER ALL;
DELETE FROM acl_class;
DELETE FROM acl_entry;
DELETE FROM acl_sid;
DELETE FROM acl_object_identity;
ALTER TABLE acl_class ENABLE TRIGGER ALL;
ALTER TABLE acl_entry ENABLE TRIGGER ALL;
ALTER TABLE acl_sid ENABLE TRIGGER ALL;
ALTER TABLE acl_object_identity ENABLE TRIGGER ALL;
