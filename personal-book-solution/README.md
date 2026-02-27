

------------------------------------------- Implementation ------------------------------------------

# Personal Book List API with Google Books Integration

## Context
In this project provides a REST API to manage a personal list of books, fetching details from the Google Books API and storing them in an in-memory H2 database.

## Database Connection (H2 Console)
The application uses an H2 in-memory database. You can view and manage data using the H2 Console.

* Console URL: http://localhost:8080/h2-console
* JDBC URL: jdbc:h2:mem:booksdb
* Username: prodapt
* Password: prodapt

## Test the API (Using Swagger)
* Once the application is running, open your web browser and navigate to:
`http://localhost:8080/swagger-ui.html`

## Add a Book to Personal List (POST)
* Endpoint: POST /books/{googleId}
* Description: Fetches book details from Google Books using the googleId and saves it locally.
* Example googleId: `f779EAAAQBAJ`
* Swagger Action: Click "Try it out", enter the ID, and click "Execute".

## View All Saved Books (GET)
* Endpoint: GET /books
* Description: Returns a list of all books saved in the H2 database.

## Search Google Books (GET)
* Endpoint: GET /google?q={query}
* Description: Searches the real Google Books API for books based on a keyword (e.g., effective+java, Python, Javascript).

## How to Test the API (Using Swagger)
* Add a Book to Personal List (POST)
* Endpoint: POST /books/{googleId}
* Action: In Swagger, find POST /books/{googleId}, click "Try it out", enter a valid googleId `(e.g., f779EAAAQBAJ)`, and click     "Execute".

## View All Saved Books (GET)
* Endpoint: GET /books
* Action: In Swagger, find GET /books, click "Try it out", and click "Execute".
