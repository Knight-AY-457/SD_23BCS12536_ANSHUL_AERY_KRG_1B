import { useEffect, useState } from "react";
import { API_BASE_URL } from "./apiConfig";

function App() {
  const [apiKey, setApiKey] = useState("FREE_USER_KEY");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState("");
  const [cooldownSeconds, setCooldownSeconds] = useState(0);
  const [movies, setMovies] = useState([]);
  const [selectedMovieId, setSelectedMovieId] = useState("");
  const [userName, setUserName] = useState("Anshul");
  const [seats, setSeats] = useState(1);

  useEffect(() => {
    if (cooldownSeconds <= 0) {
      return undefined;
    }

    const timer = setInterval(() => {
      setCooldownSeconds((previous) => (previous > 0 ? previous - 1 : 0));
    }, 1000);

    return () => clearInterval(timer);
  }, [cooldownSeconds]);

  const callApi = async (path, options = {}, onSuccess) => {
    setLoading(true);
    setResult("");
    try {
      const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers: {
          "Content-Type": "application/json",
          "X-API-KEY": apiKey,
          ...(options.headers || {})
        }
      });

      if (response.status === 429) {
        const retryAfter = response.headers.get("Retry-After");
        const retryAfterSeconds = Number.parseInt(retryAfter || "60", 10);
        const safeCooldown = Number.isNaN(retryAfterSeconds) ? 60 : Math.max(1, retryAfterSeconds);
        setCooldownSeconds(safeCooldown);
        setResult(`Rate limit exceeded. Try again later. Retry after ${safeCooldown}s.`);
        return;
      }

      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        setResult(JSON.stringify({ status: response.status, error: data.message || "Request failed" }, null, 2));
        return;
      }

      if (onSuccess) {
        onSuccess(data);
      }
      setResult(JSON.stringify({ status: response.status, data }, null, 2));
    } catch (error) {
      setResult(`Request failed: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const loadMovies = () => {
    callApi("/movies", {}, (data) => {
      setMovies(data);
      if (data.length > 0 && !selectedMovieId) {
        setSelectedMovieId(String(data[0].id));
      }
    });
  };

  const bookTicket = () => {
    callApi(
      "/book",
      {
        method: "POST",
        body: JSON.stringify({
          movieId: Number(selectedMovieId),
          userName,
          seats: Number(seats)
        })
      },
      () => loadMovies()
    );
  };

  const fetchBookings = () => {
    callApi("/bookings");
  };

  return (
    <div className="page">
      <div className="card">
        <h1>Movie Booking Demo</h1>
        <p>Test a realistic in-memory service with API key-based rate limits.</p>

        <label htmlFor="apiKey">API Key</label>
        <input
          id="apiKey"
          type="text"
          value={apiKey}
          onChange={(event) => setApiKey(event.target.value)}
          placeholder="Enter X-API-KEY"
        />

        <label htmlFor="movieSelect">Movie</label>
        <select
          id="movieSelect"
          value={selectedMovieId}
          onChange={(event) => setSelectedMovieId(event.target.value)}
        >
          <option value="">Select a movie</option>
          {movies.map((movie) => (
            <option key={movie.id} value={movie.id}>
              {movie.title} ({movie.availableSeats} seats left)
            </option>
          ))}
        </select>

        <label htmlFor="userName">Customer Name</label>
        <input
          id="userName"
          type="text"
          value={userName}
          onChange={(event) => setUserName(event.target.value)}
          placeholder="Enter customer name"
        />

        <label htmlFor="seats">Seats</label>
        <input
          id="seats"
          type="number"
          min="1"
          value={seats}
          onChange={(event) => setSeats(event.target.value)}
        />

        <div className="buttonRow">
          <button disabled={loading || cooldownSeconds > 0} onClick={loadMovies}>
            {loading ? "Loading..." : "Get Movies"}
          </button>
          <button disabled={loading || cooldownSeconds > 0 || !selectedMovieId} onClick={bookTicket}>
            {loading ? "Loading..." : "Book Ticket"}
          </button>
          <button disabled={loading || cooldownSeconds > 0} onClick={fetchBookings}>
            {loading ? "Loading..." : "View Bookings"}
          </button>
        </div>

        {cooldownSeconds > 0 && (
          <div className="cooldownMessage">
            Rate limit active. You can send the next request in <strong>{cooldownSeconds}s</strong>.
          </div>
        )}

        {movies.length > 0 && (
          <div className="movieList">
            {movies.map((movie) => (
              <div key={movie.id} className="movieCard">
                <strong>{movie.title}</strong>
                <span>{movie.genre} | {movie.language} | {movie.durationMinutes} min</span>
                <span>${movie.ticketPrice.toFixed(2)} | Seats left: {movie.availableSeats}</span>
              </div>
            ))}
          </div>
        )}

        <pre className="response">{result || "Response will appear here..."}</pre>
      </div>
    </div>
  );
}

export default App;
