-- Useful PostgreSQL queries for review and viva.
SELECT * FROM books ORDER BY catalog_id;
SELECT * FROM members ORDER BY name;
SELECT * FROM borrow_transactions ORDER BY borrowed_at DESC;
SELECT category, COUNT(*) AS total_books FROM books GROUP BY category ORDER BY total_books DESC;
SELECT b.title, b.borrow_count FROM books b ORDER BY b.borrow_count DESC, b.title LIMIT 5;
SELECT m.name, COUNT(t.id) AS active_loans
FROM members m
LEFT JOIN borrow_transactions t ON t.member_id = m.id AND t.status = 'BORROWED'
GROUP BY m.id, m.name
ORDER BY active_loans DESC;
