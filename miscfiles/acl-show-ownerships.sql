-- Default permissions bit masking: read (bit 0), write (bit 1), create (bit 2), delete (bit 3), and administer (bit 4)
SELECT 
sid.sid AS owner_username, 
ident.object_id_identity AS object_id,
acl_class.class,
acl_object_identity AS acl_object_id,
mask AS permissions_bit_masking 
FROM acl_class
INNER JOIN acl_object_identity ident ON acl_class.id = ident.object_id_class
INNER JOIN acl_sid sid ON sid.id = CAST (ident.owner_sid AS INTEGER)
LEFT JOIN acl_entry entry ON entry.acl_object_identity = ident.id;