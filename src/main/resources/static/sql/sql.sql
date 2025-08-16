USE rentalsystem;
select * from users;
select * from users where username='admin';
SELECT id, username, email, password, role_id FROM users WHERE username = 'test6';
SELECT u.username, r.role_name, r.permissions 
FROM users u
JOIN roles r ON u.role_id = r.id
WHERE u.username = 'test6';
select * from roles;
select * from houses;
