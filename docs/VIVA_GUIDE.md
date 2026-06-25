# Viva Guide

## What changed from the Java CLI project?

The old project stored data in memory and accepted terminal input. The full-stack version stores data permanently in PostgreSQL, exposes REST APIs through Spring Boot, and provides a React user interface.

## Why keep DSA classes?

The DSA layer remains independent. Spring services convert database records into the input needed by BST, AVL, B+ Tree, graph, sorting, greedy, and DP algorithms.

## Why Spring Boot?

It provides REST controllers, dependency injection, validation, transaction management, and database integration.

## Why PostgreSQL?

It stores books, members, publications, borrowing transactions, and knowledge graph edges permanently with relational constraints.

## Why React?

It creates a responsive interface and communicates with the backend using JSON REST calls.

## Important demonstration

1. Add a book.
2. Borrow and return it.
3. Show PostgreSQL tables in pgAdmin.
4. Generate recommendations.
5. Run BST vs AVL.
6. Run BFS and Dijkstra.
7. Show the dashboard updating from real data.
