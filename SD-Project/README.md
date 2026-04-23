# Rate Limiting Demo (Spring Boot + React)

This project demonstrates API rate limiting with **Bucket4j** using:
- `X-API-KEY` header-based user identification
- `HandlerInterceptor` for centralized request checks
- In-memory `ConcurrentHashMap` bucket storage

## API Keys and Limits
- `FREE_USER_KEY` -> 10 requests/minute
- `PREMIUM_USER_KEY` -> 100 requests/minute

## Backend Endpoints
- `GET /movies`
- `GET /movies/{id}`
- `POST /book`
- `GET /bookings`

`POST /book` body example:
```json
{
  "movieId": 1,
  "userName": "Anshul",
  "seats": 2
}
```

If a request exceeds the limit, backend returns:
- HTTP `429 Too Many Requests`
- `Retry-After` header

## Run Backend
```bash
cd backend
mvn spring-boot:run
```

## Run Frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173` and calls backend at `http://localhost:8080`.
Change base URL in `frontend/src/apiConfig.js` if needed.
