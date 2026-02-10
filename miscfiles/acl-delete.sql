-- This is to clean up all ACL related tables, used primarily for testing
DELETE FROM acl_class;
DELETE FROM acl_entry;
DELETE FROM acl_sid;
DELETE FROM acl_object_identity;
